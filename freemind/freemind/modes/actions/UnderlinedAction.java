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
 * Created on 25.08.2004
 */
/* $Id: UnderlinedAction.java,v 1.1.2.2 2004-09-29 21:49:04 christianfoltin Exp $ */
package freemind.modes.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.xml.bind.JAXBException;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.UnderlinedNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;


public class UnderlinedAction extends NodeGeneralAction implements NodeActorXml, MenuItemEnabledListener{
	private final ControllerAdapter modeController;
	/**
	 * @param textID
	 * @param iconPath
	 * @param actor
	 */
	public UnderlinedAction(ControllerAdapter modeController) {
		super(modeController, "underlined", "images/Underline24.gif");
		this.modeController = modeController;
		addActor(this);			
	}

	public void act(XmlAction action) {
		UnderlinedNodeAction underlinedact = (UnderlinedNodeAction) action;
		NodeAdapter node = getNodeFromID(underlinedact.getNode());
		if(node.isUnderlined() != underlinedact.isUnderlined()) {
			node.setUnderlined(underlinedact.isUnderlined());
			this.modeController.nodeChanged(node);
		}
	}


	public Class getDoActionClass() {
		return UnderlinedNodeAction.class;
	}

	public ActionPair apply(MapAdapter model, MindMapNode selected) throws JAXBException {
		// every node is set to the inverse of the focussed node.
		boolean underlined = modeController.getSelected().isUnderlined();
		return getActionPair(selected, underlined);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean underlined)
		throws JAXBException {
		UnderlinedNodeAction underlinedAction = toggleUnderlined(selected, !underlined);
		UnderlinedNodeAction undoUnderlinedAction = toggleUnderlined(selected, underlined);
		return new ActionPair(underlinedAction, undoUnderlinedAction);
	}

	private UnderlinedNodeAction toggleUnderlined(MindMapNode selected, boolean underlined)
		throws JAXBException {
		UnderlinedNodeAction underlinedAction = getActionXmlFactory().createUnderlinedNodeAction();
		underlinedAction.setNode(getNodeID(selected));
		underlinedAction.setUnderlined(underlined);
		return underlinedAction;
	}

	public void setUnderlined(MindMapNode node, boolean  underlined) {
		try {
			execute(getActionPair(node, underlined));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

    public boolean isEnabled(JMenuItem item, Action action) {
		boolean underlined = modeController.getSelected().isUnderlined();
		setSelected(item, underlined);
        return true;
    }


}