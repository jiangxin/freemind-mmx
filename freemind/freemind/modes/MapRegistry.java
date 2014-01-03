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
 * Created on 26.05.2005
 *
 */
package freemind.modes;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import freemind.controller.filter.util.SortedMapListModel;

/**
 * @author dimitri 26.05.2005
 */
public class MapRegistry {
	private SortedMapListModel mapIcons;
	private MindMap map;

	public MapRegistry(MindMap map) {
		super();
		this.map = map;
		mapIcons = new SortedMapListModel();
	}

	public void addIcon(MindIcon icon) {
		mapIcons.add(icon);
	}

	/**
     */
	public SortedMapListModel getIcons() {
		return mapIcons;
	}

	public void registrySubtree(MindMapNode root, boolean registerMyself) {
		if (registerMyself) {
			registerNodeIcons(root);
		}
		ListIterator iterator = root.childrenUnfolded();
		while (iterator.hasNext()) {
			MindMapNode node = (MindMapNode) iterator.next();
			registrySubtree(node, true);
		}
	}

	public void registerNodeIcons(MindMapNode node) {
		List icons = node.getIcons();
		Iterator i = icons.iterator();
		while (i.hasNext()) {
			MindIcon icon = (MindIcon) i.next();
			addIcon(icon);
		}
	}

	public MindMap getMap() {
		return map;
	}

	/**
	 * @throws IOException
	 */
	public void save(Writer fileout) throws IOException {
	}
}
