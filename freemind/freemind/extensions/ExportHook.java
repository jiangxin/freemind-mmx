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
/*$Id: ExportHook.java,v 1.1.2.3 2004-10-18 05:46:01 christianfoltin Exp $*/

package freemind.extensions;

import java.awt.Container;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import freemind.main.Tools;

/**
 * @author foltin
 *
 */
public class ExportHook extends ModeControllerHookAdapter {
	/**
     * @param type
     * @param description
     * @return
     */
    protected File chooseFile(String type, String description) {
        Container component = getController().getFrame().getContentPane();
		JFileChooser chooser = null;
		chooser = new JFileChooser();
		File file = getController().getMap().getFile();
		String mapFile ="";
        if (file != null) {
            mapFile = file.getAbsolutePath();
			if(mapFile.indexOf('.') >= 0) {
			    mapFile = mapFile.substring(0, mapFile.lastIndexOf('.'));
			}
        } else {
            mapFile = getController().getRootNode().getText();
        }
		String proposedName = mapFile+"."+ type;
		chooser.setSelectedFile(new File(proposedName));

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

	private class ImageFilter extends FileFilter {
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

}
