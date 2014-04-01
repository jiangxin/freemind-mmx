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
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 27.03.2014
 */
public class AddArrowLinkActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public AddArrowLinkActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof AddArrowLinkXmlAction) {
			AddArrowLinkXmlAction arrowAction = (AddArrowLinkXmlAction) action;
			MindMapNode source = getNodeFromID(arrowAction.getNode());
			MindMapNode target = getNodeFromID(arrowAction.getDestination());
			String proposedId = arrowAction.getNewId();

			if (getLinkRegistry().getLabel(target) == null) {
				// call registry to give new label
				getLinkRegistry().registerLinkTarget(target);
			}
			MindMapArrowLinkModel linkModel = new MindMapArrowLinkModel(source,
					target, getExMapFeedback());
			linkModel.setDestinationLabel(getLinkRegistry().getLabel(target));
			// give label:
			linkModel.setUniqueId(getLinkRegistry().generateUniqueLinkId(
					proposedId));
			// check for other attributes:
			if (arrowAction.getColor() != null) {
				linkModel.setColor(Tools.xmlToColor(arrowAction.getColor()));
			}
			if (arrowAction.getEndArrow() != null) {
				linkModel.setEndArrow(arrowAction.getEndArrow());
			}
			if (arrowAction.getEndInclination() != null) {
				linkModel.setEndInclination(Tools.xmlToPoint(arrowAction
						.getEndInclination()));
			}
			if (arrowAction.getStartArrow() != null) {
				linkModel.setStartArrow(arrowAction.getStartArrow());
			}
			if (arrowAction.getStartInclination() != null) {
				linkModel.setStartInclination(Tools.xmlToPoint(arrowAction
						.getStartInclination()));
			}
			// register link.
			getLinkRegistry().registerLink(linkModel);
			getExMapFeedback().nodeChanged(target);
			getExMapFeedback().nodeChanged(source);

		}
	}

	public Class getDoActionClass() {
		return AddArrowLinkXmlAction.class;
	}

	private ActionPair getActionPair(MindMapNode source, MindMapNode target) {
		AddArrowLinkXmlAction doAction = createAddArrowLinkXmlAction(source,
				target, getLinkRegistry().generateUniqueLinkId(null));
		// now, the id is clear:
		RemoveArrowLinkXmlAction undoAction = getExMapFeedback()
				.getActorFactory().getRemoveArrowLinkActor()
				.createRemoveArrowLinkXmlAction(doAction.getNewId());
		return new ActionPair(doAction, undoAction);
	}

	public AddArrowLinkXmlAction createAddArrowLinkXmlAction(
			MindMapNode source, MindMapNode target, String proposedID) {
		AddArrowLinkXmlAction action = new AddArrowLinkXmlAction();
		action.setNode(getNodeID(source));
		action.setDestination(getNodeID(target));
		action.setNewId(proposedID);
		return action;
	}

	/**
	 * Source holds the MindMapArrowLinkModel and points to the id placed in
	 * target.
	 */
	public void addLink(MindMapNode source, MindMapNode target) {
		execute(getActionPair(source, target));
	}

}
