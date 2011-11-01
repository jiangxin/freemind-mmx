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
 * Created on 15.07.2004
 */
/*$Id: StructuredMenuItemHolder.java,v 1.1.4.1.16.1 2008/01/13 20:55:34 christianfoltin Exp $*/
package freemind.controller;

import javax.swing.Action;
import javax.swing.JMenuItem;

public class StructuredMenuItemHolder {
	private JMenuItem menuItem;
	private Action action;
	private MenuItemEnabledListener enabledListener;
	private MenuItemSelectedListener selectionListener;

	public StructuredMenuItemHolder() {
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		if (action instanceof MenuItemEnabledListener) {
			MenuItemEnabledListener listener = (MenuItemEnabledListener) action;
			setEnabledListener(listener);
		}
		if (action instanceof MenuItemSelectedListener) {
			MenuItemSelectedListener listener = (MenuItemSelectedListener) action;
			setSelectedListener(listener);
		}
	}

	public MenuItemEnabledListener getEnabledListener() {
		return enabledListener;
	}

	public void setEnabledListener(MenuItemEnabledListener enabledListener) {
		this.enabledListener = enabledListener;
	}

	public JMenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(JMenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public MenuItemSelectedListener getSelectionListener() {
		return selectionListener;
	}

	public void setSelectedListener(MenuItemSelectedListener selectionListener) {
		this.selectionListener = selectionListener;
	}
}