/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 16.10.2004
 */
/*$Id: ExportHook.java,v 1.1.4.7.2.12 2010/02/27 09:27:50 christianfoltin Exp $*/

package freemind.extensions;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import freemind.main.Tools;
import freemind.modes.FreeMindFileDialog;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class ExportHook extends ModeControllerHookAdapter {
	private MapView view;

	/**
	 * @param type
	 * @param description
	 * @param nameExtension
	 * @return
	 */
	protected File chooseFile(String type, String description,
			String nameExtension) {
		ModeController controller = getController();
		return ExportHook.chooseImageFile(type, description, nameExtension, controller);
	}
	
	public static File chooseImageFile(String type, String description,
			String nameExtension, ModeController controller) {
		Container component = controller.getFrame().getContentPane();
		final ImageFilter filter = new ImageFilter(type, description);
		FreeMindFileDialog chooser = null;
		chooser = controller.getFileChooser(filter);
		File mmFile = controller.getMap().getFile();
		if (mmFile != null) {
			String proposedName = mmFile.getAbsolutePath().replaceFirst(
					"\\.[^.]*?$", "")
					+ ((nameExtension != null) ? nameExtension : "")
					+ "."
					+ type;
			chooser.setSelectedFile(new File(proposedName));
		}
		int returnVal = chooser.showSaveDialog(component);
		if (returnVal != JFileChooser.APPROVE_OPTION) { // not ok pressed
			return null;
		}

		// |= Pressed O.K.
		File chosenFile = chooser.getSelectedFile();
		String ext = Tools.getExtension(chosenFile.getName());
		if (!Tools.safeEqualsIgnoreCase(ext, type)) {
			chosenFile = new File(chosenFile.getParent(), chosenFile.getName()
					+ "." + type);
		}

		if (chosenFile.exists()) { // If file exists, ask before overwriting.
			String overwriteText = MessageFormat.format(controller
					.getText("file_already_exists"), new Object[] { chosenFile
					.toString() });
			int overwriteMap = JOptionPane.showConfirmDialog(component,
					overwriteText, overwriteText, JOptionPane.YES_NO_OPTION);
			if (overwriteMap != JOptionPane.YES_OPTION) {
				return null;
			}
		}
		return chosenFile;
	}

	public static class ImageFilter extends FileFilter {
		private String type;
		private final String description;

		public ImageFilter(String type, String description) {
			this.type = type;
			this.description = description;
		}

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = Tools.getExtension(f.getName());
			return Tools.safeEqualsIgnoreCase(extension, type);
		}

		public String getDescription() {
			return description == null ? type : description;
		}
	}

	protected String getTranslatableResourceString(String resourceName) {
		String returnValue = getResourceString(resourceName);
		if (returnValue != null && returnValue.startsWith("%")) {
			return getController().getText(returnValue.substring(1));
		}
		return returnValue;
	}

	public BufferedImage createBufferedImage() {
		view = getController().getView();
		if (view == null)
			return null;

		// Determine which part of the view contains the nodes of the map:
		// (Needed to eliminate areas of whitespace around the actual rendering
		// of the map)

		// NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();

		// call prepare printing to lay out for printing before getting the
		// inner bounds
		view.preparePrinting();
		Rectangle innerBounds = view.getInnerBounds();

		// Create an image containing the map:
		BufferedImage myImage = (BufferedImage) view.createImage(
				view.getWidth(), view.getHeight());

		// Render the mind map nodes on the image:
		Graphics g = myImage.getGraphics();
		g.clipRect(innerBounds.x, innerBounds.y, innerBounds.width,
				innerBounds.height);
		view.print(g);
		myImage = myImage.getSubimage(innerBounds.x, innerBounds.y,
				innerBounds.width, innerBounds.height);
		view.endPrinting();
		return myImage;
		// NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		// Rectangle rect = view.getInnerBounds(root.getViewer());
		//
		// BufferedImage image =
		// new BufferedImage(
		// rect.width,
		// rect.height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D g = (Graphics2D) image.createGraphics();
		// g.translate(-rect.getMinX(), -rect.getMinY());
		// view.update(g);
		// return image;
	}

	/**
     */
	protected void copyFromResource(String prefix, String fileName,
			String destinationDirectory) {
		// adapted from http://javaalmanac.com/egs/java.io/CopyFile.html
		// Copies src file to dst file.
		// If the dst file does not exist, it is created
		try {
			logger.finest("searching for " + prefix + fileName);
			URL resource = getResource(prefix + fileName);
			if (resource == null) {
				logger.severe("Cannot find resource: " + prefix + fileName);
				return;
			}
			InputStream in = resource.openStream();
			OutputStream out = new FileOutputStream(destinationDirectory + "/"
					+ fileName);

			// Transfer bytes from in to out
			Tools.copyStream(in, out, true);
		} catch (Exception e) {
			logger.severe("File not found or could not be copied. "
					+ "Was earching for " + prefix + fileName
					+ " and should go to " + destinationDirectory);
			freemind.main.Resources.getInstance().logException(e);
		}

	}

	/**
     */
	protected void copyFromFile(String dir, String fileName,
			String destinationDirectory) {
		// adapted from http://javaalmanac.com/egs/java.io/CopyFile.html
		// Copies src file to dst file.
		// If the dst file does not exist, it is created
		try {
			logger.finest("searching for " + dir + fileName);
			File resource = new File(dir, fileName);
			if (resource == null) {
				logger.severe("Cannot find resource: " + dir + fileName);
				return;
			}
			InputStream in = new FileInputStream(resource);
			OutputStream out = new FileOutputStream(destinationDirectory + "/"
					+ fileName);

			// Transfer bytes from in to out
			Tools.copyStream(in, out, true);
		} catch (Exception e) {
			logger.severe("File not found or could not be copied. "
					+ "Was earching for " + dir + fileName
					+ " and should go to " + destinationDirectory);
			freemind.main.Resources.getInstance().logException(e);
		}

	}

}
