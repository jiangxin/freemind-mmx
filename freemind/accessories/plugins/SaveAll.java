/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 12.03.2004
 *
 */
package accessories.plugins;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import freemind.controller.Controller;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.view.MapModule;

/**
 * @author foltin
 * 
 */
public class SaveAll extends ModeControllerHookAdapter {

	/**
     * 
     */
	public SaveAll() {
		super();
	}

	public void startupMapHook() {
		super.startupMapHook();
		// store initial mapModule:
		Controller mainController = getController().getController();
		MapModule initialMapModule = mainController.getMapModule();
		Map modules = getMapModules();
		// to prevent concurrent modification:
		Vector v = new Vector();
		v.addAll(modules.values());
		for (Iterator iter = v.iterator(); iter.hasNext();) {
			MapModule module = (MapModule) iter.next();
			// change to module to display map properly.
			mainController.getMapModuleManager().changeToMapModule(
					module.toString());
			if (!module.getModeController().save()) {
				// if not successfully, break the action.
				JOptionPane
						.showMessageDialog(
								getController().getFrame().getContentPane(),
								"FreeMind",
								getResourceString("accessories/plugins/SaveAll.properties_save_all_cancelled"),
								JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		mainController.getMapModuleManager().changeToMapModule(
				initialMapModule.toString());
	}

	/**
     */
	private Map getMapModules() {
		return getController().getController().getMapModuleManager()
				.getMapModules();
	}

}
