/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 11.03.2005
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import freemind.modes.MindMap;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Reverts the map to the saved version. In Xml, the old map is stored as xml
 * and as an undo action, the new map is stored, too.
 * 
 * Moreover, the filename of the doAction is set to the appropriate map file's
 * name. The undo action has no file name associated.
 * 
 * The action goes like this: close the actual map and open the given Xml/File.
 * If only a Xml string is given, a temporary file name is created, the xml
 * stored into and this map is opened instead of the actual.
 * 
 * @author foltin
 * 
 */
public class RevertAction extends MindmapAction  {

	private final MindMapController mindMapController;

	/**
	 */
	public RevertAction(MindMapController modeController) {
		super("RevertAction", (String) null, modeController);
		mindMapController = modeController;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		try {
			MindMap map = mindMapController.getMap();
			File file = map.getFile();
			if (file == null) {
				JOptionPane.showMessageDialog(mindMapController.getView(),
						mindMapController.getText("map_not_saved"), "FreeMind",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// FIXME: Make action from MindMapActions out of it.
			mindMapController.getActorFactory().getRevertActor().revertMap(map, file);
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}

	}


}
