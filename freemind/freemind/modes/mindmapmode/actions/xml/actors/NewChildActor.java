/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import java.util.Iterator;

import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 16.03.2014
 */
public class NewChildActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public NewChildActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		NewNodeAction addNodeAction = (NewNodeAction) action;
		NodeAdapter parent = getNodeFromID(addNodeAction.getNode());
		int index = addNodeAction.getIndex();
		MindMapNode newNode = getExMapFeedback().newNode("", parent.getMap());
		newNode.setLeft(addNodeAction.getPosition().equals("left"));
		String newId = addNodeAction.getNewId();
		String givenId = getLinkRegistry()
				.registerLinkTarget(newNode, newId);
		if (!givenId.equals(newId)) {
			throw new IllegalArgumentException("Designated id '" + newId
					+ "' was not given to the node. It received '" + givenId
					+ "'.");
		}
		getExMapFeedback().insertNodeInto(newNode, parent, index);
		// call hooks:
		for (Iterator i = parent.getActivatedHooks().iterator(); i.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) i.next();
			hook.onNewChild(newNode);
		}
		// done.
	}

	protected MindMapLinkRegistry getLinkRegistry() {
		return getExMapFeedback().getMap().getLinkRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return NewNodeAction.class;
	}



	public MindMapNode addNewNode(MindMapNode parent, int index,
			boolean newNodeIsLeft) {
		if (index == -1) {
			index = parent.getChildCount();
		}
		// bug fix from Dimitri.
		getLinkRegistry().registerLinkTarget(parent);
		String newId = getLinkRegistry().generateUniqueID(null);
		NewNodeAction newNodeAction = getAddNodeAction(parent, index, newId,
				newNodeIsLeft);
		// Undo-action
		DeleteNodeAction deleteAction = getExMapFeedback().getActorFactory().getDeleteChildActor()
				.getDeleteNodeAction(newId);
		getExMapFeedback().doTransaction(getExMapFeedback().getResourceString("new_child"),
				new ActionPair(newNodeAction, deleteAction));
		return (MindMapNode) parent.getChildAt(index);
	}

	public NewNodeAction getAddNodeAction(MindMapNode parent, int index,
			String newId, boolean newNodeIsLeft) {
		String pos = newNodeIsLeft ? "left" : "right";
		NewNodeAction newNodeAction = new NewNodeAction();
		newNodeAction.setNode(getNodeID(parent));
		newNodeAction.setPosition(pos);
		newNodeAction.setIndex(index);
		newNodeAction.setNewId(newId);
		return newNodeAction;
	}

}
