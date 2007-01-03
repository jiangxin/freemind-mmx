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
 * Created on 19.04.2004
 *
 */
package accessories.plugins;

import java.awt.datatransfer.Transferable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 */
public class SortNodes extends MindMapNodeHookAdapter {

	/**
	 * 
	 */
	public SortNodes() {
		super();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode, java.util.List)
	 */
	public void invoke(MindMapNode node) {
		// we want to sort the children of the node:
		TreeMap sortSet = new TreeMap(new Comparator(){

			public int compare(Object pArg0, Object pArg1) {
				if (pArg0 instanceof MindMapNode) {
					MindMapNode node1 = (MindMapNode) pArg0;
					if (pArg1 instanceof MindMapNode) {
						MindMapNode node2 = (MindMapNode) pArg1;
						String nodeText1 = node1.getPlainTextContent();
						String nodeText2 = node2.getPlainTextContent();
						return nodeText1.compareToIgnoreCase(nodeText2);
					}
				}
				return 0;
			}});
		// put in all children of the node
		for (Iterator iter = node.childrenUnfolded(); iter.hasNext();) {
			MindMapNode child = (MindMapNode) iter.next();
			sortSet.put(child , null);
		}
		// now, it is already sorted. we cut the children
		for (Iterator iter = sortSet.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			MindMapNode child = (MindMapNode) entry.getKey();
			Vector childList = new Vector();
			childList.add(child);
			entry.setValue(getMindMapController().cut(childList));
		}
		// lets paste them again:
		for (Iterator iter = sortSet.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			MindMapNode child = (MindMapNode) entry.getKey();
			Transferable transf = (Transferable) entry.getValue();
			getMindMapController().paste(transf, node);
		}
		
	}

}
