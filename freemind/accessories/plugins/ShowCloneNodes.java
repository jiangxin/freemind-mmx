/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import java.awt.EventQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 15.05.2012
 */
public class ShowCloneNodes extends MindMapNodeHookAdapter{
	
	
	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		final Vector newSelecteds = new Vector();
		final MindMapController mindMapController = getMindMapController();
		List selecteds = mindMapController.getSelecteds();
		for (Iterator it = selecteds.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			addClonesToList(newSelecteds, node);
			newSelecteds.remove(node);
		}
		if (!newSelecteds.isEmpty()) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					mindMapController.select(
							(MindMapNode) newSelecteds.get(0), newSelecteds);
				}
			});
		}
	}

	protected void addClonesToList(Vector newSelecteds, MindMapNode node) {
		ClonePlugin hook = ClonePlugin.getHook(node);
		if(hook != null) {
			// original found. 
			HashSet clones = hook.getCloneNodes();
			newSelecteds.addAll(clones);
		}
	}
}
