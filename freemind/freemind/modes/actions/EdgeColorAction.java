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
/* $Id: EdgeColorAction.java,v 1.1.2.3 2004-10-05 22:23:58 christianfoltin Exp $ */
package freemind.modes.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.xml.bind.JAXBException;

import freemind.controller.Controller;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.EdgeColorFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapEdgeModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class EdgeColorAction extends AbstractAction implements ActorXml {
	private final ModeController controller;

	public EdgeColorAction(ModeController controller) {
		super(controller.getText("edge_color"));
		this.controller = controller;
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		Color color = Controller.showCommonJColorChooserDialog(controller
				.getView().getSelected(), controller.getText("choose_edge_color"), controller
				.getSelected().getEdge().getColor());
		if (color == null)
			return;
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			setEdgeColor(selected, color);
		}
	}

	public void setEdgeColor(MindMapNode node, Color color) {
		try {
			EdgeColorFormatAction doAction = createEdgeColorFormatAction(node, color);
			EdgeColorFormatAction undoAction = createEdgeColorFormatAction(node, ((EdgeAdapter) node.getEdge()).getRealColor());
			ActionPair pair = new ActionPair(doAction, undoAction);
			controller.getActionFactory().startTransaction(this.getClass().getName());
			controller.getActionFactory().executeAction(pair);
			controller.getActionFactory().endTransaction(this.getClass().getName());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof EdgeColorFormatAction) {
			EdgeColorFormatAction edgeAction = (EdgeColorFormatAction) action;
			Color color = Tools.xmlToColor(edgeAction.getColor());
			MindMapNode node = controller.getNodeFromID(edgeAction.getNode());
			Color oldColor = ((EdgeAdapter) node.getEdge()).getRealColor() ;
			if (!Tools.safeEquals(color, oldColor)) {
				((MindMapEdgeModel) node.getEdge()).setColor(color);
                controller.nodeChanged(node);
            }
		}
	}

	public EdgeColorFormatAction createEdgeColorFormatAction(MindMapNode node, Color color) throws JAXBException {
		EdgeColorFormatAction edgeAction = controller.getActionXmlFactory().createEdgeColorFormatAction();
		edgeAction.setNode(node.getObjectId(controller));
		if (color != null) {
            edgeAction.setColor(Tools.colorToXml(color));
        }
		return edgeAction;
	}
	
	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EdgeColorFormatAction.class;
	}

}