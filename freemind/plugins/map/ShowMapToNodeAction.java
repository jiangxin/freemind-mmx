/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package plugins.map;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 12.12.2011
 */
public class ShowMapToNodeAction extends MindMapNodeHookAdapter {

	static final String NODE_CONTEXT_PLUGIN_NAME = "plugins/map/MapDialog_ShowMapToNode.properties";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		// is the map open? Ask base class.
		Registration registration = getRegistration();
		if (registration != null) {
			// is the map open?
			MapDialog mapDialog = registration.getMapDialog();
			if (mapDialog == null) {
				// if not, open it!
				getMindMapController().createModeControllerHook(MapDialog.MAP_HOOK_NAME);
			}
			mapDialog = registration.getMapDialog();
			if (mapDialog != null) {
				mapDialog.getFreeMindMapController().showSelectedNodes();
				mapDialog.getMapDialog().requestFocus();
			} else {
				logger.warning("Can't find dialog to connect to!");
			}
		} else {
			logger.warning("Can't find registration base class!");
			
		}
	}

	public Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

}
