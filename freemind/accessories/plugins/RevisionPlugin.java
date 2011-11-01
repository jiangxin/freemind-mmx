/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 5.06.2004
 *
 */
package accessories.plugins;

import java.awt.Color;

import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionHandler;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * 
 */
public class RevisionPlugin extends PermanentMindMapNodeHookAdapter implements
		ActionHandler {

	static boolean alreadyUsed = false;

	private Color color;

	/**
	 * 
	 */
	public RevisionPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		if (alreadyUsed == false) {
			color = Color.YELLOW;
			// new register:
			getMindMapController().getActionFactory().registerHandler(this);
			alreadyUsed = true;
		}
	}

	public void shutdownMapHook() {
		getMindMapController().getActionFactory().deregisterHandler(this);
		super.shutdownMapHook();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActionHandler#executeAction(freemind.controller
	 * .actions.ActionPair)
	 */
	public void executeAction(XmlAction action) {
		if (action instanceof EditNodeAction) {
			// there is an edit action.
			EditNodeAction editAction = (EditNodeAction) action;
			NodeAdapter node = getMindMapController().getNodeFromID(
					editAction.getNode());
			node.setBackgroundColor(color);
			nodeChanged(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActionHandler#startTransaction(java.lang.
	 * String)
	 */
	public void startTransaction(String name) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActionHandler#endTransaction(java.lang.String
	 * )
	 */
	public void endTransaction(String name) {
	}

}
