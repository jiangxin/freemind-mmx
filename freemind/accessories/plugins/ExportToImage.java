/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.main.Tools;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * @author kakeda
 * @author rreppel
 *
 */
public class ExportToImage extends ModeControllerHookAdapter {

	private MapView view;

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
		view = getController().getView();
		if (view == null)
			return;
		BufferedImage image = createBufferedImage();
		if (image != null)
			exportToImage(image, getResourceString("image_type"));

	}

	public BufferedImage createBufferedImage() {
		//Determine which part of the view contains the nodes of the map:
			//(Needed to eliminate areas of whitespace around the actual rendering of the map)

		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		Rectangle innerBounds = view.getInnerBounds(root.getViewer());

		 //Create an image containing the map:
		 BufferedImage myImage = (BufferedImage) view.createImage(view.getWidth(), view.getHeight() );

		 //Render the mind map nodes on the image:
		 Graphics g = myImage.getGraphics();
		 g.clipRect(innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);
		 view.print(g);
		 myImage = myImage.getSubimage(innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);
		 return myImage;
//		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
//		Rectangle rect = view.getInnerBounds(root.getViewer());
//
//		BufferedImage image =
//			new BufferedImage(
//				rect.width,
//				rect.height,
//				BufferedImage.TYPE_INT_RGB);
//		Graphics2D g = (Graphics2D) image.createGraphics();
//		g.translate(-rect.getMinX(), -rect.getMinY());
//		view.update(g);
//		return image;
	}
	/**
	 * Export image.
	 * @return
	 */
	public boolean exportToImage(BufferedImage image, String type) {
		JFileChooser chooser = null;
		chooser = new JFileChooser();
		String imageName = view.getName() + type;
		chooser.setSelectedFile(new File(imageName));

		chooser.addChoosableFileFilter(new ImageFilter(type));
		//    	String label = MessageFormat.format(
		//                        getText("export_to_image"),
		//                        new Object[] {type.getUpperTypeString()});
		//
		//    	chooser.setDialogTitle(label);
		int returnVal = chooser.showSaveDialog(view);
		if (returnVal != JFileChooser.APPROVE_OPTION) { // not ok pressed
			return false;
		}

		// |= Pressed O.K.
		File f = chooser.getSelectedFile();
		//Force the extension to be .mm
		String ext = Tools.getExtension(f.getName());
		if (!ext.equals(type)) {
			f = new File(f.getParent(), f.getName() + "." + type);
		}

		if (f.exists()) { // If file exists, ask before overwriting.
			int overwriteMap = JOptionPane.showConfirmDialog(view, /*getText*/
			 ("map_already_exists"), "FreeMind", JOptionPane.YES_NO_OPTION);
			if (overwriteMap != JOptionPane.YES_OPTION) {
				return false;
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(f);
			ImageIO.write(image, type, out);
//			OutputStream out = new FileOutputStream(f);
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//			encoder.encode(image);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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

	private class ImageFilter extends FileFilter {
		private String type;
		public ImageFilter(String type) {
			this.type = type;
		}

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = Tools.getExtension(f.getName());
			if (extension != null) {
				if (extension.equals(type)) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

		public String getDescription() {
			return type;
			//    	   MessageFormat.format(
			//               getText("image_desc"),
			//               new Object[] {type.getUpperTypeString()});
		}
	}

}
