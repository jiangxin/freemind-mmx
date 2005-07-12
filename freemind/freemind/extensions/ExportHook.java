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
/*$Id: ExportHook.java,v 1.1.4.4.8.1 2005-07-12 15:41:13 dpolivaev Exp $*/

package freemind.extensions;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import freemind.main.Tools;
import freemind.modes.NodeAdapter;
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
     * @return
     */
    protected File chooseFile(String type, String description) {
        Container component = getController().getFrame().getContentPane();
		JFileChooser chooser = null;
		chooser = new JFileChooser();
		File mmFile = getController().getMap().getFile();
        if (mmFile!=null) {
            String proposedName = mmFile.getAbsolutePath().replaceFirst(
                    "\\.[^.]*?$", "")
                    + "." + type;
            chooser.setSelectedFile(new File(proposedName));
        }

		chooser.addChoosableFileFilter(new ImageFilter(type, description));
		//    	chooser.setDialogTitle(label);
		int returnVal = chooser.showSaveDialog(component);
		if (returnVal != JFileChooser.APPROVE_OPTION) { // not ok pressed
			return null;
		}

		// |= Pressed O.K.
		File chosenFile = chooser.getSelectedFile();
		String ext = Tools.getExtension(chosenFile.getName());
		if (!Tools.safeEqualsIgnoreCase(ext, type)) {
			chosenFile = new File(chosenFile.getParent(), chosenFile.getName() + "." + type);
		}

		if (chosenFile.exists()) { // If file exists, ask before overwriting.
			String overwriteText= MessageFormat.format(getController().getText("file_already_exists"),
			        new Object[] {chosenFile.toString()});
            int overwriteMap = JOptionPane.showConfirmDialog(component, overwriteText, overwriteText, JOptionPane.YES_NO_OPTION);
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
			return description==null?type:description;
		}
	}
	
	
	public BufferedImage createBufferedImage() {
		view = getController().getView();
		if (view == null)
			return null;

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


}
