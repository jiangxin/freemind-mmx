/*
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.StringTokenizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import accessories.plugins.util.xslt.ExportDialog;
import freemind.extensions.ExportHook;
import freemind.main.Tools;
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
		// TODO Auto-generated constructor stub
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
            MindMapNode root = (MindMapNode) getController().getMap().getRoot();
            StringWriter writer = new StringWriter();
            root.save(writer, getController().getMap().getLinkRegistry());
            StringReader reader = new StringReader(writer.getBuffer().toString());
            transForm(new StreamSource(reader), new File(
                    getResourceString("xslt_file")), saveFile);
            // copy files from the resources to the file system:
            if(Tools.safeEquals(getResourceString("create_dir"), "true")) {
                String directoryName = saveFile.getAbsolutePath()+"_files";
                boolean success = (new File(directoryName)).mkdir();
                if(success) {
                    String files = getResourceString("files_to_copy");
                    String filePrefix = getResourceString("file_prefix");
                    StringTokenizer tokenizer = new StringTokenizer(files, ",");
                    while(tokenizer.hasMoreTokens()) {
                        String next = tokenizer.nextToken();
                        copyFromResource(filePrefix, next, directoryName); 
                    }
                }
            }
        } catch (IOException e) {
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
                InputStream in = getResource(
                        prefix + fileName).openStream();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
 
        
    }

    private void export(File file) {
        ExportDialog exp = new ExportDialog(file);
        exp.setVisible(true);
	}

    public void transForm(Source xmlSource, File xsltFile, File resultFile){
        //System.out.println("set xsl");
       Source xsltSource =  new StreamSource(xsltFile);
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
       trans.transform(xmlSource, result);
       }
       catch(Exception e){
       //System.err.println("error applying the xslt file "+e);
       e.printStackTrace();
       };
      return ;
      }
      
	
	
}
