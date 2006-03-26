/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;

/**
 * @author foltin
 * @author kakeda
 * @author rreppel
 *
 */
public class ExportToImage extends ExportHook {


	/**
	 * 
	 */
	public ExportToImage() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		BufferedImage image = createBufferedImage();
		if (image != null) {
			String imageType = getResourceString("image_type");
			
            exportToImage(image, imageType,
                    getResourceString("image_description"));
		}

	}

	/**
	 * Export image.
	 * @return
	 */
	public boolean exportToImage(BufferedImage image, String type, String description) {
	    File chosenFile = chooseFile(type, description);
	    if(chosenFile==null) {
	        return false;
	    }
		try {
		    getController().getFrame().setWaitingCursor(true);
			FileOutputStream out = new FileOutputStream(chosenFile);
			ImageIO.write(image, type, out);
//			OutputStream out = new FileOutputStream(f);
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//			encoder.encode(image);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    getController().getFrame().setWaitingCursor(false);
		return true;
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
