/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008 Christian Foltin, Dimitri Polivaev and others.
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
 * Created on 03.01.2008
 *
 */
package freemind.controller;

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JPopupMenu;
import javax.swing.event.MenuListener;

/**
 * @author foltin
 * 
 */
public class FreeMindPopupMenu extends JPopupMenu implements
		StructuredMenuHolder.MenuEventSupplier {
	private HashSet listeners = new HashSet();

	protected static java.util.logging.Logger logger = null;
	
	public FreeMindPopupMenu() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}
	
	protected void firePopupMenuWillBecomeVisible() {
		super.firePopupMenuWillBecomeVisible();
		logger.fine("Popup firePopupMenuWillBecomeVisible called.");
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuSelected(null);
		}
	}

	public void addMenuListener(MenuListener listener) {
		listeners.add(listener);
	}

	public void removeMenuListener(MenuListener listener) {
		listeners.remove(listener);
	}

	protected void firePopupMenuCanceled() {
		super.firePopupMenuCanceled();
		// logger.info("Popup firePopupMenuCanceled called.");
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuCanceled(null);
		}
	}

	protected void firePopupMenuWillBecomeInvisible() {
		super.firePopupMenuWillBecomeInvisible();
		// logger.info("Popup firePopupMenuWillBecomeInvisible called.");
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuDeselected(null);
		}
	}

}
