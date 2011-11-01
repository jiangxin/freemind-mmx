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
 * Created on 14.05.2005
 *
 */
package freemind.controller.filter.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedMapListModel extends AbstractListModel implements
		SortedListModel {
	SortedSet model;

	public SortedMapListModel() {
		model = new TreeSet();
	}

	public int getSize() {
		return model.size();
	}

	public Object getElementAt(int index) {
		return model.toArray()[index];
	}

	public void add(Object element) {
		if (model.add(element)) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void addAll(Object elements[]) {
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}

	public void clear() {
		int oldSize = getSize();
		if (oldSize > 0) {
			model.clear();
			fireIntervalRemoved(this, 0, oldSize - 1);
		}
	}

	public boolean contains(Object element) {
		return model.contains(element);
	}

	public Object firstElement() {
		return model.first();
	}

	public Iterator iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		return model.last();
	}

	/**
 */
	public int getIndexOf(Object o) {
		Iterator i = iterator();
		int count = -1;
		while (i.hasNext()) {
			count++;
			if (i.next().equals(o))
				return count;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.filter.util.SortedListModel#replace(java.lang.Object,
	 * java.lang.Object)
	 */
	public void replace(Object oldO, Object newO) {
		if (oldO.equals(newO))
			return;
		boolean removed = model.remove(oldO);
		boolean added = model.add(newO);
		if (removed || added) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.filter.util.SortedListModel#delete(java.lang.Object)
	 */
	public void remove(Object element) {
		if (model.remove(element)) {
			fireContentsChanged(this, 0, getSize());
		}

	}
}
