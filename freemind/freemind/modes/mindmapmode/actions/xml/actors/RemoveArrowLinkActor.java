/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

package freemind.modes.mindmapmode.actions.xml.actors;

import freemind.controller.actions.generated.instance.AddArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.RemoveArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 27.03.2014
 */
public class RemoveArrowLinkActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public RemoveArrowLinkActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void removeReference(MindMapLink arrowLink) {
		execute(getActionPair(arrowLink));
	}

	/**
     */
	private ActionPair getActionPair(MindMapLink arrowLink) {
		return new ActionPair(
				createRemoveArrowLinkXmlAction(arrowLink.getUniqueId()),
				createAddArrowLinkXmlAction(arrowLink));
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
			getExMapFeedback().nodeChanged(arrowLink.getSource());
			getExMapFeedback().nodeChanged(arrowLink.getTarget());
		}
	}

	public Class getDoActionClass() {
		return RemoveArrowLinkXmlAction.class;
	}

	public RemoveArrowLinkXmlAction createRemoveArrowLinkXmlAction(String id) {
		RemoveArrowLinkXmlAction action = new RemoveArrowLinkXmlAction();
		action.setId(id);
		return action;
	}

	public AddArrowLinkXmlAction createAddArrowLinkXmlAction(MindMapLink link) {
		AddArrowLinkXmlAction action = new AddArrowLinkXmlAction();
		action.setNode(getNodeID(link.getSource()));
		action.setDestination(getNodeID(link.getTarget()));
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
