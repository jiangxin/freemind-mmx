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
 * Created on 07.05.2005
 *
 */
package freemind.controller.filter;

import freemind.controller.Controller;
import freemind.modes.MindMapNode;

/**
 * @author dimitri 07.05.2005
 */
public interface Filter {
	public static final int FILTER_INITIAL_VALUE = 1;
	static final int FILTER_SHOW_MATCHED = 2;
	static final int FILTER_SHOW_ANCESTOR = 4;
	static final int FILTER_SHOW_DESCENDANT = 8;
	static final int FILTER_SHOW_ECLIPSED = 16;
	static final int FILTER_SHOW_HIDDEN = 32;

	void applyFilter(Controller c);

	boolean isVisible(MindMapNode node);

	boolean areMatchedShown();

	boolean areHiddenShown();

	boolean areAncestorsShown();

	boolean areDescendantsShown();

	boolean areEclipsedShown();

	Object getCondition();
}
