/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import freemind.extensions.ExportHook;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

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
			
            if (!imageType.equals("clipboard")) {
                exportToImage(image, imageType,
                        getResourceString("image_description"));
            } else {
                setClipboard(image);
            }
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



//	Hi all !
//
//	I just found out about this project last week. I just missed the possibility of saving the mapview as tiff, jpeg (no compression) or bmp (svg later ?) so I imported JAI package and wrote what was needed while trying to respect the whole pattern. May be this could be an interesting feature ? It is just about specifying  xxx.bmp, xxx.tiff or xxx.jpeg (as well as xxx.mm) in File->SaveAs menu.  For bmp it is awfully slow (jai...). In any case -Xmx128m is required at startup. Oh, as it prints what's on the mapview panel, this printing feature inherited a "feature" which prevents large diagrams from being fully expanded i.e. they are truncated.
//
//	Keith 
//	-    public boolean saveJPEG(File file,BufferedImage bi){ //KR
//	-        boolean result = false;
//	-        System.out.println("Saving JPEG");
//	-        try{
//	-            OutputStream os = new FileOutputStream(file);
//	-            JPEGEncodeParam param = new JPEGEncodeParam();
//	-            param.setQuality(1.0f);
//	-            ImageEncoder encoder = ImageCodec.createImageEncoder("JPEG",os,param);    
//	-            encoder.encode(bi);
//	-            os.flush();
//	-            os.close();
//	-            result = true;
//	-        }
//	-        catch (IOException e){
//	-            e.printStackTrace();
//	-            result = false;
//	-        }
//	-        return result;
//	-    } //KR
	
	// adapted from http://javaalmanac.com/egs/java.awt.datatransfer/ToClipImg.html:
	
//	 This method writes a image to the system clipboard.
	// fc, 28.10.2004: this new method does not work.
    // otherwise it returns null.
    public void setClipboard(BufferedImage image) {
        ImageSelection imgSel = new ImageSelection(image);
        getController().getClipboard().setContents(imgSel, null);
    }
    
    // This class is used to hold an image while on the clipboard.
    public static class ImageSelection implements Transferable {
        private BufferedImage image;
        private DataFlavor imageFlavor;
    
        public ImageSelection(BufferedImage image) {
            this.image = image;
            try {
                imageFlavor = new DataFlavor("image/jpeg");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    
        // Returns supported flavors
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{imageFlavor};
        }
    
        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return imageFlavor.equals(flavor);
        }
    
        // Returns image
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!imageFlavor.equals(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "JPEG", out);
            return out;
        }
    }

}
