/*
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        return chooseFile(getResourceString("file_type"), getResourceString("file_description"));
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
            // get AREA:
            // create HTML image?
            MindMapNode root = (MindMapNode) getController().getMap().getRoot();
            ClickableImageCreator creator=null;
            boolean create_image = Tools.safeEquals(getResourceString("create_html_linked_image"), "true");
            if(create_image) {
                creator = new ClickableImageCreator(root, getController(), 
                        getResourceString("link_replacement_regexp"));
            }
            // get output:
            StringWriter writer = new StringWriter();
            // get XML
            getController().getMap().getXml(writer);
            StringReader reader = new StringReader(writer.getBuffer().toString());
            // search for xslt file:
            String xsltFileName = getResourceString("xslt_file");
            URL xsltUrl = getResource(xsltFileName);
            if(xsltUrl == null) {
                logger.severe("Can't find " + xsltFileName + " as resource.");
                throw new IllegalArgumentException("Can't find " + xsltFileName + " as resource.");
            }
            InputStream xsltFile = xsltUrl.openStream();
            transForm(new StreamSource(reader), xsltFile, saveFile, creator);
            // copy files from the resources to the file system:
            if(Tools.safeEquals(getResourceString("create_dir"), "true")) {
                String directoryName = saveFile.getAbsolutePath()+"_files";
                boolean success = true;
                File dir = new File(directoryName);
                // create directory, if not exists:
                if (!dir.exists()) {
                    success = dir.mkdir();
                }
                if(success) {
                    String files = getResourceString("files_to_copy");
                    String filePrefix = getResourceString("file_prefix");
                    StringTokenizer tokenizer = new StringTokenizer(files, ",");
                    while(tokenizer.hasMoreTokens()) {
                        String next = tokenizer.nextToken();
                        copyFromResource(filePrefix, next, directoryName); 
                    }
                    // copy icons?
                    if(Tools.safeEquals(getResourceString("copy_icons"),"true")) {
                        String directoryName2 = directoryName + File.separatorChar + "icons";
                        File dir2 = new File(directoryName2);
                        // create directory, if not exists:
                        if (!dir2.exists()) {
                            success = dir2.mkdir();
                        }
                        if(success) {
	                        Vector iconNames = MindIcon.getAllIconNames();
	                        for ( int i = 0 ; i < iconNames.size(); ++i ) {
	                            String iconName = ((String) iconNames.get(i));
	                            MindIcon myIcon     = MindIcon.factory(iconName);
	                            copyFromResource(MindIcon.getIconsPath(), myIcon.getIconBaseFileName(), directoryName2); 
	                        }
                        }
                    }
                }
                if(success && create_image) {
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
                if(!success){
                    JOptionPane.showMessageDialog(null, getResourceString("error_creating_directory"), "Freemind", JOptionPane.ERROR_MESSAGE);
                }
            }
            if(Tools.safeEquals(getResourceString("load_file"), "true")) {
                getController().getFrame().openDocument(saveFile.toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param next
     * @param directoryName
     * @param directoryName2
     */
    private void copyFromResource(String prefix, String fileName, String destinationDirectory) {
        // adapted from http://javaalmanac.com/egs/java.io/CopyFile.html
        // Copies src file to dst file.
        // If the dst file does not exist, it is created
            try {
                logger.finest("searching for " + prefix + fileName);
                URL resource = getResource(prefix + fileName);
                if(resource==null){
                		logger.severe("Cannot find resource: "+ prefix+fileName);
                		return;
                }
                InputStream in = resource.openStream();
                OutputStream out = new FileOutputStream(destinationDirectory
                        + "/" + fileName);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                logger.severe("File not found or could not be copied. " +
                		"Was earching for " + prefix + fileName + " and should go to "+destinationDirectory);
                e.printStackTrace();
            }
 
        
    }

    private void export(File file) {
        ExportDialog exp = new ExportDialog(file);
        exp.setVisible(true);
	}

    public void transForm(Source xmlSource, InputStream xsltStream, File resultFile, ClickableImageCreator creator){
        String areaCode="";
        if(creator!=null) {
            areaCode = creator.generateHtml();
        }
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
       };
      return ;
      }
      
	
	
}
