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

import java.util.logging.Logger;

import freemind.controller.FreeMindPopupMenu;
import freemind.controller.StructuredMenuHolder;

public class MindMapPopupMenu extends FreeMindPopupMenu {

	private static final String MINDMAPMODE_POPUP = "mindmapmode_popup/";

	private static Logger logger = null;

	private MindMapController c;

	public MindMapPopupMenu(MindMapController c) {
		super();
		this.c = c;
		if (logger == null) {
			logger = c.getFrame().getLogger(this.getClass().getName());
		}
	}

	/**
	 */
	public void update(StructuredMenuHolder holder) {
		this.removeAll();
		c.createPatternSubMenu(holder, MINDMAPMODE_POPUP);
		c.addIconsToMenu(holder, MINDMAPMODE_POPUP + "icons/");
		holder.updateMenus(this, MINDMAPMODE_POPUP);

	}

}
