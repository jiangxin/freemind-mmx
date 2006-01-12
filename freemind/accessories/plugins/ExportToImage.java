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
	

}
