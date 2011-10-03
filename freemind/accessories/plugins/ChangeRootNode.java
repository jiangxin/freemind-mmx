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

package accessories.plugins;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeMotionListenerView;
import freemind.view.mindmapview.NodeView;

/**
 * Changes the root node to another one
 * TODO: nodes must be set to specific sides of the new root node.
 * TODO: Undo is missing
 * TODO: Plugins for root must be moved.
 * 
 * @author foltin
 * @date 01.10.2011
 */
public class ChangeRootNode extends MindMapNodeHookAdapter {
	public void invoke(MindMapNode node) {
		// we dont need node.
		MindMapNode focussed = getMindMapController().getSelected();
		
		if (focussed.isRoot()) {
			getMindMapController().getController().errorMessage(
					getResourceString("cannot_add_parent_to_root"));
			return;
		}
		MindMapNode oldRoot = getMap().getRootNode();
		getMap().changeRoot(focussed);
    	// remove all viewers:
    	Vector nodes = new Vector();
    	nodes.add(focussed);
    	while(!nodes.isEmpty()) {
    		MindMapNode firstElement = (MindMapNode) nodes.firstElement();
    		logger.fine("Removing viewers for " + firstElement);
    		nodes.remove(0);
    		nodes.addAll(firstElement.getChildren());
    		Collection viewers = new Vector(firstElement.getViewers());
    		for (Iterator it = viewers.iterator(); it.hasNext();) {
    			NodeView viewer = (NodeView) it.next();
				firstElement.removeViewer(viewer);
			}
    	}

		MapView mapView = getMindMapController().getView();
		for (int i = mapView.getComponentCount()-1; i >= 0; i--) {
			Component comp = mapView.getComponent(i);
			if (comp instanceof NodeView) {
				NodeView nodeView = (NodeView) comp;
				mapView.remove(nodeView);
			}
			if (comp instanceof NodeMotionListenerView) {
				NodeMotionListenerView nmlView = (NodeMotionListenerView) comp;
				mapView.remove(nmlView);
			}
			
		}
		mapView.initRoot();
		mapView.add(mapView.getRoot());
		mapView.doLayout();
		getMindMapController().nodeChanged(focussed);
		logger.fine("layout done.");
		getMindMapController().select(focussed, Tools.getVectorWithSingleElement(focussed));
		getMindMapController().centerNode(focussed);
	};

}
