/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;

/**
 * @author foltin
 * 
 */
public class ExportToOoWriter extends ExportHook {

	/**
	 * 
	 */
	public ExportToOoWriter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		File chosenFile = chooseFile("sxw", null);
		if (chosenFile == null) {
			return;
		}
		getController().getFrame().setWaitingCursor(true);
		try {
			exportToOoWriter(chosenFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getController().getFrame().setWaitingCursor(false);
	}
	
	private void applyXsltFile(String xsltFileName, 
			                   StringWriter  writer, 
			                   Result result) throws IOException
	{
		URL xsltUrl = getResource(xsltFileName);
		if (xsltUrl == null) {
			logger.severe("Can't find " + xsltFileName + " as resource.");
			throw new IllegalArgumentException("Can't find " + xsltFileName
					+ " as resource.");
		}
		InputStream xsltStream = xsltUrl.openStream();
		// System.out.println("set xsl");
		Source xsltSource = new StreamSource(xsltStream);

		// create an instance of TransformerFactory
		try {
			StringReader reader = new StringReader(writer.getBuffer().toString());

			// System.out.println("make transform instance");
			TransformerFactory transFact = TransformerFactory.newInstance();

			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(new StreamSource(reader), result);
		} catch (Exception e) {
			// System.err.println("error applying the xslt file "+e);
			e.printStackTrace();
		}
	}
	private void exportToOoWriter(File file) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(file));
		
		// get output:
		StringWriter writer = new StringWriter();
		// get XML
		getController().getMap().getFilteredXml(writer);
					
		// System.out.println("set result");
		Result result = new StreamResult(zipout);

		ZipEntry entry = new ZipEntry("content.xml");
		zipout.putNextEntry(entry);
		applyXsltFile("accessories/mm2oowriter.xsl", writer, result);
		zipout.closeEntry();

		entry = new ZipEntry("META-INF/manifest.xml");
		zipout.putNextEntry(entry);
		applyXsltFile("accessories/mm2oowriter.manifest.xsl", writer, result);
		zipout.closeEntry();		
		
		entry = new ZipEntry("styles.xml");
		zipout.putNextEntry(entry);
        copyFromResource("accessories/mm2oowriterStyles.xml", zipout);
		zipout.closeEntry();		
		
		zipout.close();
	}
    
    /**
     * @param next
     * @param directoryName
     * @param directoryName2
     */
    private void copyFromResource(String fileName, OutputStream out) {
        // adapted from http://javaalmanac.com/egs/java.io/CopyFile.html
        // Copies src file to dst file.
        // If the dst file does not exist, it is created
            try {
                logger.finest("searching for "  + fileName);
                URL resource = getResource( fileName);
                if(resource==null){
                        logger.severe("Cannot find resource: "+ fileName);
                        return;
                }
                InputStream in = resource.openStream();
                

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
            } catch (Exception e) {
                logger.severe("File not found or could not be copied. " +
                        "Was earching for " +  fileName + " and should go to "+out);
                e.printStackTrace();
            }
 
        
    }

    public void transForm(Source xmlSource, InputStream xsltStream, File resultFile, String areaCode)
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
       };
      return ;
      }

}

