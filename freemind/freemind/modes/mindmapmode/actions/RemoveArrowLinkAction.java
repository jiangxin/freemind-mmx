/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
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
 * 
 * Created on 08.10.2004
 */
/*
 * $Id: RemoveArrowLinkAction.java,v 1.16.10.1 08.10.2004 07:51:02
 * christianfoltin Exp $
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapController;

public class RemoveArrowLinkAction extends MindmapAction {

	private MindMapArrowLinkModel mArrowLink;

	private final MindMapController controller;

	/**
	 * can be null can be null.
	 */
	public RemoveArrowLinkAction(MindMapController controller,
			MindMapArrowLinkModel arrowLink) {
		super("remove_arrow_link", "images/edittrash.png", controller);
		this.controller = controller;
		setArrowLink(arrowLink);
	}

	/**
	 * @return Returns the arrowLink.
	 */
	public MindMapArrowLinkModel getArrowLink() {
		return mArrowLink;
	}
	
	/**
	 * The arrowLink to set.
	 */
	public void setArrowLink(MindMapArrowLinkModel arrowLink) {
		this.mArrowLink = arrowLink;
	}
	
	public void actionPerformed(ActionEvent e) {
		controller.removeReference(mArrowLink);
	}


}
