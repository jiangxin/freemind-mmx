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
 * Created on 12.10.2004
 */
/*
 * $Id: SetLinkByTextFieldAction.java,v 1.16.10.1 12.10.2004 22:18:45
 * christianfoltin Exp $
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.AddLinkXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class SetLinkByTextFieldAction extends FreemindAction implements
		ActorXml {
	private final MindMapController controller;

	public SetLinkByTextFieldAction(MindMapController controller) {
		super("set_link_by_textfield", (String) null, controller);
		this.controller = controller;
		addActor(this);
	}

	public void actionPerformed(ActionEvent e) {
		String inputValue = JOptionPane.showInputDialog(controller.getView()
				.getSelected(), controller.getText("edit_link_manually"),
				controller.getSelected().getLink());
		if (inputValue != null) {
			if (inputValue.equals("")) {
				inputValue = null; // In case of no entry unset link
			}
			setLink(controller.getSelected(), inputValue);
		}
	}

	public void setLink(MindMapNode node, String link) {
		controller.doTransaction((String) getValue(NAME),
				getActionPair(node, link));
	}

	public void act(XmlAction action) {
		if (action instanceof AddLinkXmlAction) {
			AddLinkXmlAction linkAction = (AddLinkXmlAction) action;
			NodeAdapter node = controller.getNodeFromID(linkAction.getNode());
			node.setLink(linkAction.getDestination());
			controller.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddLinkXmlAction.class;
	}

	private ActionPair getActionPair(MindMapNode node, String link) {
		return new ActionPair(createAddLinkXmlAction(node, link),
				createAddLinkXmlAction(node, node.getLink()));
	}

	private AddLinkXmlAction createAddLinkXmlAction(MindMapNode node,
			String link) {
		AddLinkXmlAction action = new AddLinkXmlAction();
		action.setNode(node.getObjectId(controller));
		action.setDestination(link);
		return action;
	}
}