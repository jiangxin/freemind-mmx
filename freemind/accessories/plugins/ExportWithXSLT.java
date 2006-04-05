/*
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import accessories.plugins.util.html.ClickableImageCreator;
import accessories.plugins.util.xslt.ExportDialog;
import freemind.extensions.ExportHook;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExportWithXSLT extends ExportHook {
    protected File chooseFile() {
        return chooseFile(getResourceString("file_type"), getTranslatableResourceString("file_description"));
    }

	private String getTranslatableResourceString(String resourceName) {
        String returnValue = getResourceString(resourceName);
        if(returnValue != null && returnValue.startsWith("%")) {
            return getController().getText(returnValue.substring(1));
        }
        return returnValue;
    }

    /**
	 * 
	 */
	public ExportWithXSLT() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		ModeController mc = getController();
		MindMap model = getController().getMap();
		 if (Tools.safeEquals(getResourceString("file_type"), "user")) {
			 if(model == null) 
				 return; // there may be no map open
			 if((model.getFile() == null) || model.isReadOnly()) {
				if(mc.save()) {
					export(model.getFile());
					return;
				}
				else
					return;
			 }
			 else
				 export(model.getFile());
		 } else {
		     transform();
		 }
	}
	/**
     * 
     */
    private void transform() {
        try {
            File saveFile = chooseFile();
            if(saveFile==null) {
                // no file.
                return;
            }
            // get AREA:
            // create HTML image?
            boolean create_image = Tools.safeEquals(getResourceString("create_html_linked_image"), "true");
            String areaCode = getAreaCode(create_image);
            // XSLT Transformation
            String xsltFileName = getResourceString("xslt_file");
            boolean success = transformMapWithXslt(xsltFileName, saveFile, areaCode);
            // create directory?
            if(success && Tools.safeEquals(getResourceString("create_dir"), "true")) {
                String directoryName = saveFile.getAbsolutePath()+"_files";
                success = createDirectory(directoryName);
                
                // copy files from the resources to the file system:
                if(success) {
                    String files = getResourceString("files_to_copy");
                    String filePrefix = getResourceString("file_prefix");
                    copyFilesFromResourcesToDirectory(directoryName, files, filePrefix);
                }
                // copy icons?
                if(success && Tools.safeEquals(getResourceString("copy_icons"),"true")) {
                    success = copyIcons(directoryName);
                }
                if(success && Tools.safeEquals(getResourceString("copy_map"),"true")) {
                    success = copyMap(directoryName);
                }
                if(success && create_image) {
                    createImageFromMap(directoryName);
                }
            }
            if(!success){
                JOptionPane.showMessageDialog(null, getResourceString("error_creating_directory"), "Freemind", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(Tools.safeEquals(getResourceString("load_file"), "true")) {
                getController().getFrame().openDocument(saveFile.toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean copyMap(String pDirectoryName) throws IOException
    {
        boolean success = true;
//      Generating output Stream            
        BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(pDirectoryName+File.separator + "map.mm") ) );
        getController().getMap().getFilteredXml(fileout);
        return success;
    }

    /**
     * @param directoryName
     * @return
     */
    private boolean copyIcons(String directoryName)
    {
        boolean success;
        String iconDirectoryName = directoryName + File.separatorChar + "icons";
        
        success = createDirectory(iconDirectoryName);
        if(success) {
            copyIconsToDirectory(iconDirectoryName);
        }
        return success;
    }

    /**
     * @param directoryName
     */
    private void createImageFromMap(String directoryName)
    {
        // create image:
        BufferedImage image = createBufferedImage();
        try {
        	FileOutputStream out = new FileOutputStream(directoryName+File.separator+"image.png");
        	ImageIO.write(image, "png", out);
        	out.close();
        } catch (IOException e1) {
        	e1.printStackTrace();
        }
    }

    /**
     * @param directoryName2
     */
    private void copyIconsToDirectory(String directoryName2)
    {
        Vector iconNames = MindIcon.getAllIconNames();
        for ( int i = 0 ; i < iconNames.size(); ++i ) {
            String iconName = ((String) iconNames.get(i));
            MindIcon myIcon     = MindIcon.factory(iconName);
            copyFromResource(MindIcon.getIconsPath(), myIcon.getIconBaseFileName(), directoryName2); 
        }
    }

    /**
     * @param directoryName
     * @param files
     * @param filePrefix
     */
    private void copyFilesFromResourcesToDirectory(String directoryName, String files, String filePrefix)
    {
        StringTokenizer tokenizer = new StringTokenizer(files, ",");
        while(tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            copyFromResource(filePrefix, next, directoryName); 
        }
    }

    /**
     * @param directoryName
     * @param success
     * @return
     */
    private boolean createDirectory(String directoryName)
    {
        File dir = new File(directoryName);
        // create directory, if not exists:
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return true;
    }

    /**
     * @param xsltFileName
     * @param saveFile
     * @param areaCode
     * @throws IOException
     */
    private boolean transformMapWithXslt(String xsltFileName, File saveFile, String areaCode) throws IOException
    {
        StringWriter writer = getMapXml();
        StringReader reader = new StringReader(writer.getBuffer().toString());
        // search for xslt file:
        URL xsltUrl = getResource(xsltFileName);
        if(xsltUrl == null) {
            logger.severe("Can't find " + xsltFileName + " as resource.");
            throw new IllegalArgumentException("Can't find " + xsltFileName + " as resource.");
        }
        InputStream xsltFile = xsltUrl.openStream();
        return transForm(new StreamSource(reader), xsltFile, saveFile, areaCode);
    }

    /**
     * @return
     * @throws IOException
     */
    private StringWriter getMapXml() throws IOException
    {
        // get output:
        StringWriter writer = new StringWriter();
        // get XML
        getController().getMap().getFilteredXml(writer);
        return writer;
    }

    /**
     * @param create_image
     * @return
     */
    private String getAreaCode(boolean create_image)
    {
        String areaCode="";
        if(create_image) {
            MindMapNode root = (MindMapNode) getController().getMap().getRoot();
            ClickableImageCreator creator = new ClickableImageCreator(root, getController(), 
                    getResourceString("link_replacement_regexp"));
            areaCode = creator.generateHtml();
        }
        return areaCode;
    }

    private void export(File file) {
        ExportDialog exp = new ExportDialog(file);
        exp.setVisible(true);
	}

    public boolean transForm(Source xmlSource, InputStream xsltStream, File resultFile, String areaCode)
    {
        //System.out.println("set xsl");
       Source xsltSource =  new StreamSource(xsltStream);
        //System.out.println("set result");
       Result result = new StreamResult(resultFile);
    
       // create an instance of TransformerFactory
       try{
           //System.out.println("make transform instance");
       TransformerFactory transFact = TransformerFactory.newInstance(  );
    
       Transformer trans = transFact.newTransformer(xsltSource);
       // set parameter:
       // relative directory <filename>_files
       trans.setParameter("destination_dir", resultFile.getName()+"_files/");
       trans.setParameter("area_code", areaCode);
       trans.setParameter("folding_type", getController().getFrame().getProperty("html_export_folding"));
       trans.transform(xmlSource, result);
       }
       catch(Exception e){
       //System.err.println("error applying the xslt file "+e);
       e.printStackTrace();
       return false;
       };
      return true;
      }
      
	
	
}
