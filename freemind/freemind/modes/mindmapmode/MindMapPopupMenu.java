/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2001 Joerg Mueller <joergmueller@bigfoot.com> See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/*
 * $Id: MindMapPopupMenu.java,v 1.12.18.2 2005/01/04 10:39:41 christianfoltin
 * Exp $
 */

package freemind.modes.mindmapmode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;
import javax.swing.event.MenuListener;

import freemind.controller.StructuredMenuHolder;

public class MindMapPopupMenu extends JPopupMenu implements
		StructuredMenuHolder.MenuEventSupplier {

	private HashSet listeners = new HashSet();
	
	private static final String MINDMAPMODE_POPUP = "mindmapmode_popup/";

	private static Logger logger = null;

	private MindMapController c;

	public MindMapPopupMenu(MindMapController c) {
		this.c = c;
		if (logger == null) {
			logger = c.getFrame().getLogger(this.getClass().getName());
		}
	}

	/**
	 * @param holder
	 */
	public void update(StructuredMenuHolder holder) {
		this.removeAll();
		c.createPatternSubMenu(holder, MINDMAPMODE_POPUP);
		c.addIconsToMenu(holder, MINDMAPMODE_POPUP+"icons/");
		holder.updateMenus(this, MINDMAPMODE_POPUP);
		
	}

	protected void firePopupMenuWillBecomeVisible() {
		super.firePopupMenuWillBecomeVisible();
		logger.info("Popup firePopupMenuWillBecomeVisible called.");
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
		logger.info("Popup firePopupMenuCanceled called.");
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuCanceled(null);
		}
	}
	protected void firePopupMenuWillBecomeInvisible() {
		super.firePopupMenuWillBecomeInvisible();
		logger.info("Popup firePopupMenuWillBecomeInvisible called.");
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			MenuListener listener = (MenuListener) i.next();
			listener.menuDeselected(null);
		}
	}
}