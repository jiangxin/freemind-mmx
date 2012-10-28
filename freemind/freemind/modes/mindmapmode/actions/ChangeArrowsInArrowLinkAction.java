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
 * $Id: ChangeArrowsInArrowLinkAction.java,v 1.16.10.1 08.10.2004 23:12:57
 * christianfoltin Exp $
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import freemind.controller.actions.generated.instance.ArrowLinkArrowXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class ChangeArrowsInArrowLinkAction extends FreemindAction implements
		ActorXml {
	MindMapArrowLinkModel arrowLink;

	boolean hasStartArrow;

	boolean hasEndArrow;

	private final MindMapController controller;

	public ChangeArrowsInArrowLinkAction(MindMapController controller,
			String text, String iconPath, MindMapArrowLinkModel arrowLink,
			boolean hasStartArrow, boolean hasEndArrow) {
		super("change_arrows_in_arrow_link", iconPath, controller);
		this.controller = controller;
		this.arrowLink = arrowLink;
		this.hasStartArrow = hasStartArrow;
		this.hasEndArrow = hasEndArrow;
		addActor(this);
	}

	public void actionPerformed(ActionEvent e) {
		changeArrowsOfArrowLink(arrowLink, hasStartArrow, hasEndArrow);
	}

	public void changeArrowsOfArrowLink(MindMapArrowLinkModel arrowLink,
			boolean hasStartArrow, boolean hasEndArrow) {
		controller.doTransaction((String) getValue(NAME),
				getActionPair(arrowLink, hasStartArrow, hasEndArrow));
	}

	/**
     */
	private ActionPair getActionPair(MindMapArrowLinkModel arrowLink2,
			boolean hasStartArrow2, boolean hasEndArrow2) {
		return new ActionPair(createArrowLinkArrowXmlAction(arrowLink2,
				hasStartArrow2, hasEndArrow2), createArrowLinkArrowXmlAction(
				arrowLink2, arrowLink2.getStartArrow(),
				arrowLink2.getEndArrow()));
	}

	public void act(XmlAction action) {
		if (action instanceof ArrowLinkArrowXmlAction) {
			ArrowLinkArrowXmlAction arrowAction = (ArrowLinkArrowXmlAction) action;
			MindMapLink link = getLinkRegistry().getLinkForId(
					arrowAction.getId());
			((ArrowLinkAdapter) link)
					.setStartArrow(arrowAction.getStartArrow());
			((ArrowLinkAdapter) link).setEndArrow(arrowAction.getEndArrow());
			controller.nodeChanged(link.getSource());
			controller.nodeChanged(link.getTarget());
		}
	}

	public Class getDoActionClass() {
		return ArrowLinkArrowXmlAction.class;
	}

	private ArrowLinkArrowXmlAction createArrowLinkArrowXmlAction(
			MindMapArrowLinkModel arrowLink, boolean hasStartArrow,
			boolean hasEndArrow) {
		return createArrowLinkArrowXmlAction(arrowLink,
				(hasStartArrow) ? "Default" : "None", (hasEndArrow) ? "Default"
						: "None");
	}

	private ArrowLinkArrowXmlAction createArrowLinkArrowXmlAction(
			MindMapArrowLinkModel arrowLink, String hasStartArrow,
			String hasEndArrow) {
		ArrowLinkArrowXmlAction action = new ArrowLinkArrowXmlAction();
		action.setStartArrow(hasStartArrow);
		action.setEndArrow(hasEndArrow);
		action.setId(arrowLink.getUniqueId());
		return action;
	}

	/**
     */
	private MindMapLinkRegistry getLinkRegistry() {
		return controller.getMap().getLinkRegistry();
	}

}
