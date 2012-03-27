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
 */
package onekin.WSL;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class WSL_Util {
	private final String messagebox_warning = "wsl_resources/images/messagebox_warning.png";
	private final String stop_sign = "wsl_resources/images/stop-sign.png";
	private final String WSL_check_model = "wsl_resources/WSL_check_model.xsl";

	public WSL_Util() {
		}
	
	public Object[] checkModel(File currentMap){
		Vector<Object> list = new Vector<Object>();
		ImageIcon warning = new ImageIcon(getClass().getClassLoader().getResource(messagebox_warning));
		ImageIcon error = new ImageIcon(getClass().getClassLoader().getResource(stop_sign));
	
	    InputStream xsltFile = getClass().getClassLoader().getResourceAsStream(WSL_check_model);
	    String xml = transform(new StreamSource(currentMap), xsltFile);
	    String[] token;
	    if (xml != null && xml.length() != 0 ) {
	    	
	    	token = xml.split("/");
	    	
	    	for(int i =0; i < token.length ; i++){
	    		if(token[i].contains("ERROR")){
	    			list.add(error);
	    		}
	    		else if (token[i].contains("WARNING")){
	    			list.add(warning);
	    		}
	    		list.add(token[i]);
	    		}
	    	}
	    return list.toArray();
	}

	public String transform(Source xmlSource, InputStream xsltStream) {
	    Source xsltSource = new StreamSource(xsltStream);
	    StringWriter writer = new StringWriter();
	    Result result = new StreamResult(writer);
	
	    // create an instance of TransformerFactory
	    try {
	        TransformerFactory transFact = TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(xsltSource);
	        trans.transform(xmlSource, result);
	    } catch (Exception e) {
	        freemind.main.Resources.getInstance().logException(e);
	        return null;
	    }
	    return writer.toString();
	}	
	/**
	 * This method copies a source folder to a 
	 * target folder
	 * @param srcFolder
	 * @param destFolder
	 * @throws IOException
	 */
	public void copyFolder(File srcFolder, File destFolder) throws IOException {
		if (srcFolder.isDirectory()){
			if (! destFolder.exists()){
	            	destFolder.mkdir();
	            }
	         String[] oChildren = srcFolder.list();
	         	for (int i=0; i < oChildren.length; i++) {
	         		copyFolder(new File(srcFolder, oChildren[i]), new File(destFolder, oChildren[i]));
	            }
	        } 
		else {
			if(destFolder.isDirectory()){
				copyFile(srcFolder, new File(destFolder, srcFolder.getName()));
				}
	        else {
	        	copyFile(srcFolder, destFolder);
	        	}
			}
		}
	
	/**
	 * This method copies a source file to a target file
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public void copyFile(File srcFile, File destFile) throws IOException {
		InputStream oInStream = new FileInputStream(srcFile);
	    OutputStream oOutStream = new FileOutputStream(destFile); //here
	    // Transfer bytes from in to out
	    byte[] oBytes = new byte[1024];
	    int nLength;
	    BufferedInputStream oBuffInputStream = new BufferedInputStream( oInStream );
	    while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
	    	oOutStream.write(oBytes, 0, nLength);
	    	}
	        oInStream.close();
	        oOutStream.close();
		}
}