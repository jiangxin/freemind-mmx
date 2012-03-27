/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

/**
 * @author Gorka Puente García
 * @version 1
 * Nov, 11th 2010
 */
package onekin.WSL;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.xml.transform.stream.StreamSource;

import onekin.WSL.config.WSL_Configuration;
import freemind.extensions.ExportHook;
import freemind.main.XMLParseException;
import freemind.modes.MindMap;
import freemind.modes.ModeController;

public class WSL_Main extends  ExportHook {
	
	private static String extensions = "";
	private HashMap<String, String> databaseVars = new HashMap<String, String>();
	private final String curDir = System.getProperty("user.dir");
	private final String configFile= curDir 
		+ File.separator + "plugins/WSL/resources/localSettings.conf";
	private final String WSL_mm2php = "wsl_resources/WSL_mm2php.xsl";
	private final String WSL_mm2sql = "wsl_resources/WSL_mm2sql.xsl";
	private final String createWikiMarkup = "wsl_resources/word2wiki/createWikiMarkup.xsl";
	private final String createTemplateWikiMarkup = "wsl_resources/word2wiki/createTemplateWikiMarkup.xsl";
	private final String mySQLScript = curDir 
		+ File.separator + "plugins/WSL/resources/mySQLScript.sql";
	private final String extensionsDir = curDir + File.separator + "plugins/WSL/resources/extensions";
	private final String skinsDir = curDir + File.separator +"plugins/WSL/resources/skins";
	private File localSettings;
	private static String extensionsToInstall = "";
	private WSL_Util wsl_util;
	
    public WSL_Main() {
		super();
		System.out.println("Main constructor");
		}

	public void startupMapHook() {
		super.startupMapHook();
		wsl_util = new WSL_Util();
		System.out.println("startupMapHook Main");
	    if(modelChecking()){
	    	System.out.println("modelChecking true");
	    	File localSettingsConf = new File(configFile);
	    	if (!localSettingsConf.exists()){
	    		// Launch configuration panel
	    		WSL_Configuration conf = new WSL_Configuration();
	    		conf.setController(getController());
	    		conf.launch();
	    		localSettingsConf = new File(configFile);
	    	}
		    try {
			    String mySQLScript ="";
			    String strLine="";
		    	DataInputStream in;
				in = new DataInputStream(new FileInputStream(localSettingsConf));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				//Read configuration file line by line (should be only one)
				while ((strLine = br.readLine()) != null){
			    	if(strLine.contains("LocalSettings.php")){
			    		localSettings = new File (strLine);
						if(parseLocalSettings()){
						    mySQLScript = createMySQLScript();
						    executeMySQLScript(mySQLScript);
							installMediaWikiExtensions();
							installMediaWikiSkins();
							createPhpModification();
						}
			    	}
				}
				finish();
			} catch (IOException ioe) {
		        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), ioe.getLocalizedMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
		        freemind.main.Resources.getInstance().logException(ioe);
		    } catch (XMLParseException xmle) {
		        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), xmle.getLocalizedMessage(), "XMLParseException", JOptionPane.ERROR_MESSAGE);
		    	freemind.main.Resources.getInstance().logException(xmle);
		    } catch (Exception e) {
		        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
		        freemind.main.Resources.getInstance().logException(e);
		    }
	    }
	    getController().getFrame().setWaitingCursor(false);
	 }
	
//	public ModeController getThisController() {
//		return getController();
//	}

	// Create php code to modify LocalSettings.php
	private void createPhpModification() throws Exception{
//		try {
			boolean emailPage=false;
			File currentMMFile = getCurrentFile();
			// XSLT transformation to create the php code from the FreeMind model
		    InputStream xsltFile = getClass().getClassLoader().getResourceAsStream(WSL_mm2php);
		    String phpToAdd = wsl_util.transform(new StreamSource(currentMMFile), xsltFile);
//			// Change the default skin depending on the user preferences
//		    //$wgDefaultSkin = 'monobook';
//		    phpToAdd.concat("$wgDefaultSkin = '"++"';")
//		    int fromIndex = phpToAdd.indexOf("$wgDefaultSkin = '");
//		    if (fromIndex != -1){
//		    	String newSkin = phpToAdd.substring(fromIndex, phpToAdd.indexOf("';", fromIndex)+2);
//		    	phpToAdd = phpToAdd.replace("$wgDefaultSkin = 'monobook';", newSkin);
//		    }
		    // If emailPage extension has been chosen
		    if(phpToAdd.contains("__@kmail@__")){
		    	phpToAdd = phpToAdd.replace("__@kmail@__", "");
		    	emailPage=true;
		    }

		    //Upload logo to server in skins/common/images/ and point to it in localsettings.php
    		String logo = phpToAdd.substring(phpToAdd.indexOf("__@logo@__")+ "__@logo@__".length(), phpToAdd.indexOf(";", phpToAdd.indexOf("__@logo@__"))-1).replace("%20", " ");
    		String logoName = logo.substring(logo.lastIndexOf("/"), logo.length());
    		// If file is in other volume (e.g., d: or e:) then get the file directly
    		if(logo.contains(":")){
    			wsl_util.copyFile(new File(logo.substring(logo.indexOf(":") -1)), new File(localSettings.getParent().replace("\\", "/")+"/skins/common/images"+logoName.replace(" ", "_")));
    		} else{
    			wsl_util.copyFile(new File(getCurrentFile().getParent().replace("\\", "/")+logo), new File(localSettings.getParent().replace("\\", "/")+"/skins/common/images"+logoName.replace(" ", "_")));
    		}
    		
    		phpToAdd = phpToAdd.replace("__@logo@__"+logo.replace(" ", "%20"), "$wgScriptPath/skins/common/images"+logoName.replace(" ", "_"));
    		
		    String extensionsRequire="";
		    String phpToInsExtensions="\nif (!$wgCommandLineMode) {\n";  
		    String[] splitExtensions = extensionsToInstall.split("\\s");
		    for(int i =0; i < splitExtensions.length ; i++){
		    	if(!splitExtensions[i].equals("")){
		    		extensionsRequire= extensionsRequire + "require_once( \"$IP/extensions/" + splitExtensions[i] + "/"+ splitExtensions[i]  +".php\");\n";	
		    	}
		    }
		    
		    // Comment (with "#") emailPage if it isn't chosen
		    if(!emailPage){
		    	extensionsRequire = new StringBuffer(extensionsRequire).insert(extensionsRequire.indexOf("require_once( \"$IP/extensions/EmailPage"), "#").toString();
		    }
		    phpToInsExtensions = phpToInsExtensions + extensionsRequire +"}\n";

		    FileWriter fw = new FileWriter(localSettings, true);
		    fw.write(phpToAdd);
		    if (phpToInsExtensions != null) {
		    	fw.write(phpToInsExtensions);
		    }
		    fw.close();
		    getController().getFrame().setWaitingCursor(false);
//		} finally{
//			getController().getFrame().setWaitingCursor(false);
//		}
//	    } catch (IOException ioe) {
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), ioe.getLocalizedMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
//	        freemind.main.Resources.getInstance().logException(ioe);
//	        System.exit(1);
//	    } catch (XMLParseException xmle) {
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), xmle.getLocalizedMessage(), "XMLParseException", JOptionPane.ERROR_MESSAGE);
//	    	freemind.main.Resources.getInstance().logException(xmle);
//	    } catch (Exception e) {
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
//	        freemind.main.Resources.getInstance().logException(e);
//	    }
	}
	
	private void finish() {
	    JOptionPane.showMessageDialog (null, "WSL finished the wiki", "WSL done", JOptionPane.INFORMATION_MESSAGE);
	    getController().getFrame().setWaitingCursor(false);
	}

	public boolean modelChecking() {
		try {
			Object[] list = wsl_util.checkModel(getCurrentFile());
	    	JDialog.setDefaultLookAndFeelDecorated(false);
		    return confirmDialog(list);
		     } catch (XMLParseException xmle) {
		    	freemind.main.Resources.getInstance().logException(xmle);
		    } catch (Exception e) {
		        freemind.main.Resources.getInstance().logException(e);
		        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		    }
	    getController().getFrame().setWaitingCursor(false);
		return true;
	}
	
	private boolean confirmDialog(Object[] list) {
        int response = 0;
		if (list.length == 0){
			response = JOptionPane.showConfirmDialog(null, "The model is correct, Do you want to continue?", "WSL Checking Report.",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		}else{
			response = JOptionPane.showConfirmDialog(null, list, "WSL Checking Report. Do you want to continue?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		}	            
	        if (response == JOptionPane.NO_OPTION) {
	        	return false;
	        } else if (response == JOptionPane.YES_OPTION) {
	        	return true;
	        } else if (response == JOptionPane.CLOSED_OPTION) {
	        	return false;
	        }
	        return true;
	}

	/**
	 * This method parses the LocalSettings file to 
	 * extract the configuration values
	 * @return
	 * @throws Exception
	 */
	private boolean parseLocalSettings() throws Exception{
		databaseVars.put("$wgDBserver", "localhost");
		databaseVars.put("$wgDBport", "3306");
		// Open LocalSettings and get the object of DataInputStream
		DataInputStream in = new DataInputStream(new FileInputStream(localSettings));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		//Read file line by line
		while ((strLine = br.readLine()) != null)   {
			if(strLine.toLowerCase().contains("require_once") || strLine.toLowerCase().contains("include_once")){
				extensions += strLine+" ";
		    } else if(strLine.toLowerCase().contains("$wgdbserver")){
		    	databaseVars.put("$wgDBserver", getValue(strLine));
		    } else if(strLine.toLowerCase().contains("$wgdbname")){
		    	databaseVars.put("$wgDBname", getValue(strLine));
		    } else if(strLine.toLowerCase().contains("$wgdbuser")){
		    	databaseVars.put("$wgDBuser", getValue(strLine));
		    } else if(strLine.toLowerCase().contains("$wgdbpassword")){
		    	databaseVars.put("$wgDBpassword", getValue(strLine));
		    } else if (strLine.toLowerCase().contains("$wgdbport")){
		    	databaseVars.put("$wgDBport", getValue(strLine));
		    }
		 }
		 //Close the input stream
		 in.close();
		 //If any parameter is missing ToDo
//		    if (xml != null && xml.length() != 0 ) {
//		    	return confirmDialog("Model elements:\n"+xml);
//		    }
	    return true;
	}
	
	/**
	 * This method extracts the value of a line between " and "
	 * @param strLine
	 * @return
	 */
	private String getValue(String strLine) {
		return strLine.substring(strLine.indexOf("\""), strLine.lastIndexOf("\"")+1);
	}
	
	/**
	 * This method creates the MySQL script executing
	 * a XSL transformation
	 * @return
	 * @throws Exception
	 */
	private String createMySQLScript() throws Exception{
//		try {
			File mySQLFile = new File (mySQLScript);
			File currentMMFile = getCurrentFile();
	        // search xslt file and apply the transformation
		    InputStream xsltFile = getClass().getClassLoader().getResourceAsStream(WSL_mm2sql);
			// Transform the source file to create the sql script    
		    String xml = wsl_util.transform(new StreamSource(currentMMFile), xsltFile);

		    // Look for the pattern that indicates xmlWord content to wiki syntax
		    Pattern pattWord2Wiki = Pattern.compile("__@word2wiki@__(.*?)__@word2wiki@__");
		    Matcher m1 = pattWord2Wiki.matcher(xml);
		    while (m1.find()) {
			    // Content text from WordProcessingML (XML format generated by Microsoft Word 2003 and 2007) http://sourceforge.net/projects/word2wiki/
			    InputStream xsltFileWikiMarkup = getClass().getClassLoader().getResourceAsStream(createWikiMarkup);
		    	//Entire pattern found
		    	String entirePattern = m1.group(0);
		    	//Source xml file to transform
		        String sourceFile = m1.group(1);
		        String word2wiki = wsl_util.transform(new StreamSource(currentMMFile.getParent()+"/" + sourceFile), xsltFileWikiMarkup).replaceAll("'", "&#39;");
		        xml = xml.replace(entirePattern, word2wiki);
		    }
		    // Look for the pattern that indicates xmlWord content to transform to wiki template syntax
		    Pattern pattWord2WikiTemplate = Pattern.compile("__@word2wikiTemplate@__(.*?)__@word2wikiTemplate@__");
		    Matcher m2 = pattWord2WikiTemplate.matcher(xml);
		    while (m2.find()) {
			    // Content text from WordProcessingML (XML format generated by Microsoft Word 2003 and 2007) http://sourceforge.net/projects/word2wiki/
			    InputStream xsltFileWikiMarkup = getClass().getClassLoader().getResourceAsStream(createTemplateWikiMarkup);
		    	//Entire pattern found
		    	String entirePattern = m2.group(0);
		    	//Source xml file to transform
		        String sourceFile = m2.group(1);
		        String word2wiki = wsl_util.transform(new StreamSource(currentMMFile.getParent()+ File.separator + sourceFile), xsltFileWikiMarkup).replaceAll("'", "&#39;");
		        xml = xml.replace(entirePattern, word2wiki);
		    }
		    
		    // Write MySQL to output file
		    if (xml != null) {
		    	FileWriter fw = new FileWriter(mySQLFile);
		    	fw.write(xml);
		    	fw.close();
		    	return mySQLFile.getAbsolutePath();
		     }
		    getController().getFrame().setWaitingCursor(false);
			return null;
		}
//	    } catch (IOException ioe) {
//	        freemind.main.Resources.getInstance().logException(ioe);
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), ioe.toString()+" "+ioe.getLocalizedMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
//	        System.exit(1);
//	    } catch (XMLParseException xmle) {
//	    	freemind.main.Resources.getInstance().logException(xmle);
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), xmle.toString()+" "+xmle.getLocalizedMessage(), "XMLParseException", JOptionPane.ERROR_MESSAGE);
//	    }catch (Exception e) {
//	        freemind.main.Resources.getInstance().logException(e);
//	        JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.toString()+" "+e.getLocalizedMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
//	    }
//	}
	
    public File getCurrentFile() {
//    	MapView view = getController().getView();
//        
//    	if (view == null)
//    		return null;
//
    	ModeController mc = getController();
    	mc.getFrame().setWaitingCursor(true);
		MindMap model = getController().getMap();
		
		if(model == null){
			return null; // there may be no map open	
		}
			
		if((model.getFile() == null) || model.isReadOnly()) {
			if(mc.save()) {
				return null;
			}
			else
				return null;
		 }
		return model.getFile();
	}

	private void executeMySQLScript(String mySQLScript) throws Exception{
		//Extract the variables from databaseVars
		boolean verbose=true;
			String cmd = "mysql "+
				databaseVars.get("$wgDBname")+
				" --user=" + databaseVars.get("$wgDBuser")+
				" --password=" + databaseVars.get("$wgDBpassword")+
				" --port=" + databaseVars.get("$wgDBport")+
				" --host=" + databaseVars.get("$wgDBserver")+
				" -e"+
				" \" source " + mySQLScript + "\"";

	    	Process proc = Runtime.getRuntime().exec(cmd);
	    	
			// raise error in pop up if it fails ToDo
			if (verbose) {
	    		System.err.println(cmd);
				InputStream inputstream = proc.getInputStream();
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
				// read the output
				String line;
				while ((line = bufferedreader.readLine()) != null) {
				}
				// check for failure
				try {
					if (proc.waitFor() != 0) {
						System.err.println("exit value = " + proc.exitValue());
					}
				} catch (InterruptedException e) {
					System.err.println(e);
					}
			}
		}
	
	/**
	 * This method copies the extension files to
	 * plugins/WSL/resources/extensions 
	 */
	private void installMediaWikiExtensions() throws IOException {
		File extensionsRoot = new File (extensionsDir);
	    File[] sourceExtensions = extensionsRoot.listFiles();

	    for (int i = 0; i < sourceExtensions.length; i++) {
	    	if (sourceExtensions[i].isDirectory() && !extensions.toLowerCase().contains(sourceExtensions[i].getName().toLowerCase())) {
	    		File dir = new File(localSettings.getParent() + File.separator + "extensions" 
	    				+ File.separator + sourceExtensions[i].getName());
	    		extensionsToInstall = extensionsToInstall + " " + sourceExtensions[i].getName();
	    		wsl_util.copyFolder(sourceExtensions[i], dir);
	    	} 
	     }
	}
	
	/**
	 * This method copies the skins files to 
	 * plugins/WSL/resources/skins
	 */
	private void installMediaWikiSkins() throws IOException {
		File skinsRoot = new File (skinsDir);
	    File[] sourceExtensions = skinsRoot.listFiles();
	    for (int i = 0; i < sourceExtensions.length; i++) {
	    	if (sourceExtensions[i].isDirectory()) {
	    		wsl_util.copyFolder(sourceExtensions[i], new File(localSettings.getParent()+ 
	    				File.separator + "skins" + File.separator + sourceExtensions[i].getName()));
	    	}
	    	else if (sourceExtensions[i].isFile()){
	    		wsl_util.copyFile(sourceExtensions[i], new File(localSettings.getParent()+ 
	    				File.separator + "skins" + File.separator + sourceExtensions[i].getName()));
	    	}
	     }
	}
	
}
