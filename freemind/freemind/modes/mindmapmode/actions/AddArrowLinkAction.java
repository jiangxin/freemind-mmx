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
 * Created on 07.10.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import freemind.controller.actions.generated.instance.AddArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.RemoveArrowLinkXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * @author foltin
 * 
 */
public class AddArrowLinkAction extends FreemindAction implements ActorXml {

	private final MindMapController modeController;

	private RemoveArrowLinkAction removeAction;

	/**
	 * @param removeAction
	 *            The removeAction to set.
	 */
	public void setRemoveAction(RemoveArrowLinkAction removeAction) {
		this.removeAction = removeAction;
	}

	/**
     */
	public AddArrowLinkAction(MindMapController modeController) {
		super("add_link", "images/designer.png", modeController);
		this.modeController = modeController;
		addActor(this);
	}

	public void actionPerformed(ActionEvent e) {
		// assert that at least two nodes are selected. draw an arrow link in
		// between.
		List selecteds = modeController.getSelecteds();
		if (selecteds.size() < 2) {
			modeController.getController().errorMessage(
					modeController.getText("less_than_two_selected_nodes"));
			return;
		}
		for (int i = 1; i < selecteds.size(); i++) {
			addLink((MindMapNode) selecteds.get(i),
					(MindMapNode) selecteds.get(0));
		}
	}

	public void act(XmlAction action) {
		if (action instanceof AddArrowLinkXmlAction) {
			AddArrowLinkXmlAction arrowAction = (AddArrowLinkXmlAction) action;
			MindMapNode source = modeController.getNodeFromID(arrowAction
					.getNode());
			MindMapNode target = modeController.getNodeFromID(arrowAction
					.getDestination());
			String proposedId = arrowAction.getNewId();

			if (getLinkRegistry().getLabel(target) == null) {
				// call registry to give new label
				getLinkRegistry().registerLinkTarget(target);
			}
			MindMapArrowLinkModel linkModel = new MindMapArrowLinkModel(source,
					target, modeController.getFrame());
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
			modeController.nodeChanged(target);
			modeController.nodeChanged(source);

		}
	}

	public Class getDoActionClass() {
		return AddArrowLinkXmlAction.class;
	}

	private ActionPair getActionPair(MindMapNode source, MindMapNode target) {
		AddArrowLinkXmlAction doAction = createAddArrowLinkXmlAction(source,
				target, getLinkRegistry().generateUniqueLinkId(null));
		// now, the id is clear:
		RemoveArrowLinkXmlAction undoAction = removeAction
				.createRemoveArrowLinkXmlAction(doAction.getNewId());
		return new ActionPair(doAction, undoAction);
	}

	public AddArrowLinkXmlAction createAddArrowLinkXmlAction(
			MindMapNode source, MindMapNode target, String proposedID) {
		AddArrowLinkXmlAction action = new AddArrowLinkXmlAction();
		action.setNode(source.getObjectId(modeController));
		action.setDestination(target.getObjectId(modeController));
		action.setNewId(proposedID);
		return action;
	}

	/**
	 * Source holds the MindMapArrowLinkModel and points to the id placed in
	 * target.
	 */
	public void addLink(MindMapNode source, MindMapNode target) {
		modeController.doTransaction(
				(String) getValue(NAME), getActionPair(source, target));
	}

	/**
     */
	private MindMapLinkRegistry getLinkRegistry() {
		return modeController.getMap().getLinkRegistry();
	}

}
