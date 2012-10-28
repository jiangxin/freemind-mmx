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

import freemind.controller.actions.generated.instance.AddArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.RemoveArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class RemoveArrowLinkAction extends FreemindAction implements ActorXml {

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
		if (arrowLink == null) {
			addActor(this);
		}
	}

	public void actionPerformed(ActionEvent e) {
		removeReference(mArrowLink);
	}

	public void removeReference(MindMapLink arrowLink) {
		controller.doTransaction((String) getValue(NAME), getActionPair(arrowLink));
	}

	/**
     */
	private ActionPair getActionPair(MindMapLink arrowLink) {
		return new ActionPair(
				createRemoveArrowLinkXmlAction(arrowLink.getUniqueId()),
				createAddArrowLinkXmlAction(arrowLink));
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

	public void act(XmlAction action) {
		if (action instanceof RemoveArrowLinkXmlAction) {
			RemoveArrowLinkXmlAction removeAction = (RemoveArrowLinkXmlAction) action;
			MindMapLink arrowLink = getLinkRegistry().getLinkForId(
					removeAction.getId());
			if (arrowLink == null) {
				// strange: link not found:
				throw new IllegalArgumentException("Unknown link to id "
						+ removeAction.getId() + " should be deleted.");
			}
			getLinkRegistry().deregisterLink(arrowLink);
			controller.nodeChanged(arrowLink.getSource());
			controller.nodeChanged(arrowLink.getTarget());
		}
	}

	public Class getDoActionClass() {
		return RemoveArrowLinkXmlAction.class;
	}

	/**
     */
	private MindMapLinkRegistry getLinkRegistry() {
		return controller.getMap().getLinkRegistry();
	}

	public RemoveArrowLinkXmlAction createRemoveArrowLinkXmlAction(String id) {
		RemoveArrowLinkXmlAction action = new RemoveArrowLinkXmlAction();
		action.setId(id);
		return action;
	}

	public AddArrowLinkXmlAction createAddArrowLinkXmlAction(MindMapLink link) {
		AddArrowLinkXmlAction action = new AddArrowLinkXmlAction();
		action.setNode(link.getSource().getObjectId(controller));
		action.setDestination(link.getTarget().getObjectId(controller));
		action.setNewId(link.getUniqueId());
		action.setColor(Tools.colorToXml(link.getColor()));
		if (link instanceof MindMapArrowLink) {
			MindMapArrowLink arrowLink = (MindMapArrowLink) link;
			action.setEndArrow(arrowLink.getEndArrow());
			action.setEndInclination(Tools.PointToXml(arrowLink
					.getEndInclination()));
			action.setStartArrow(arrowLink.getStartArrow());
			action.setStartInclination(Tools.PointToXml(arrowLink
					.getStartInclination()));
		}
		return action;
	}

}
