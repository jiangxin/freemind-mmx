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
 * Created on 05.10.2004
 */
/* $Id: NodeStyleAction.java,v 1.1.4.1 2004-10-17 23:00:10 dpolivaev Exp $ */

package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.NodeStyleFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class NodeStyleAction extends NodeGeneralAction implements NodeActorXml {
    private final String mStyle;


    public NodeStyleAction(ModeController controller, String style) {
        super(controller, style, null);
        this.mStyle = style;
        addActor(this);
    }

    public ActionPair apply(MapAdapter model, MindMapNode selected)
            throws JAXBException {
        return getActionPair(selected, mStyle);
    }

    public Class getDoActionClass() {
        return NodeStyleFormatAction.class;
    }

    public void setStyle(MindMapNode node, String style) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, style));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        
    }

    private ActionPair getActionPair(MindMapNode selected, String style)
            throws JAXBException {
        NodeStyleFormatAction styleAction = createNodeStyleFormatAction(selected, style);
        NodeStyleFormatAction undoStyleAction = createNodeStyleFormatAction(selected, selected.getStyle());
        return new ActionPair(styleAction, undoStyleAction);
    }

    private NodeStyleFormatAction createNodeStyleFormatAction(MindMapNode selected, String style)
            throws JAXBException {
        NodeStyleFormatAction nodeStyleAction = getActionXmlFactory()
                .createNodeStyleFormatAction();
        nodeStyleAction.setNode(getNodeID(selected));
        nodeStyleAction.setStyle(style);
        return nodeStyleAction;
    }

    public void act(XmlAction action) {
        if (action instanceof NodeStyleFormatAction) {
            NodeStyleFormatAction nodeStyleAction = (NodeStyleFormatAction) action;
            MindMapNode node = getNodeFromID(nodeStyleAction.getNode());
            String style = nodeStyleAction.getStyle();
            if(!Tools.safeEquals(node.getStyle(), style)) {
                node.setStyle(style);
                modeController.nodeStructureChanged(node);
            }
        }
    }
}