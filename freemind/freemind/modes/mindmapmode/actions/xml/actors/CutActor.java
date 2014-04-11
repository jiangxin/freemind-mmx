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

import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.List;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.actors.PasteActor.NodeCoordinate;

/**
 * @author foltin
 * @date 11.04.2014
 */
public class CutActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public CutActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public CutNodeAction getCutNodeAction(MindMapNode node) {
		CutNodeAction cutAction = new CutNodeAction();
		cutAction.setNode(getNodeID(node));
		return cutAction;
	}

	public Transferable cut(List<MindMapNode> nodeList) {
		getExMapFeedback().sortNodesByDepth(nodeList);
		Transferable totalCopy = getExMapFeedback().copy(nodeList, true);
		// Do-action
		CompoundAction doAction = new CompoundAction();
		// Undo-action
		CompoundAction undo = new CompoundAction();
		// sort selectedNodes list by depth, in order to guarantee that sons are
		// deleted first:
		for (Iterator<MindMapNode> i = nodeList.iterator(); i.hasNext();) {
			MindMapNode node = i.next();
			if (node.getParentNode() == null)
				continue;
			CutNodeAction cutNodeAction = getCutNodeAction(node);
			doAction.addChoice(cutNodeAction);

			NodeCoordinate coord = new NodeCoordinate(node, node.isLeft());
			Transferable copy = getExMapFeedback().copy(node, true);
			XmlAction pasteNodeAction = getXmlActorFactory().getPasteActor()
					.getPasteNodeAction(copy, coord, (UndoPasteNodeAction) null);
			// The paste actions are reversed because of the strange
			// coordinates.
			undo.addAtChoice(0, pasteNodeAction);

		}
		if (doAction.sizeChoiceList() > 0) {
			execute(new ActionPair(doAction, undo));
		}
		return totalCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		CutNodeAction cutAction = (CutNodeAction) action;
		MindMapNode selectedNode = getNodeFromID(cutAction
				.getNode());
		getXmlActorFactory().getDeleteChildActor().deleteWithoutUndo(selectedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return CutNodeAction.class;
	}

}
