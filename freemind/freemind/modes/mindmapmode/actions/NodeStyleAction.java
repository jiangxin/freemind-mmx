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
/* $Id: NodeStyleAction.java,v 1.1.2.2.2.4 2007-05-06 12:09:41 dpolivaev Exp $ */

package freemind.modes.mindmapmode.actions;

import freemind.controller.actions.generated.instance.NodeStyleFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class NodeStyleAction extends NodeGeneralAction implements NodeActorXml {
    private final String mStyle;


    public NodeStyleAction(MindMapController controller, String style) {
        super(controller, style, null);
        this.mStyle = style;
        addActor(this);
    }

    public ActionPair apply(MindMap model, MindMapNode selected)
            {
        return getActionPair(selected, mStyle);
    }

    public Class getDoActionClass() {
        return NodeStyleFormatAction.class;
    }

    public void setStyle(MindMapNode node, String style) {
        modeController.getActionFactory().startTransaction(
                (String) getValue(NAME));
        modeController.getActionFactory().executeAction(
                getActionPair(node, style));
        modeController.getActionFactory().endTransaction(
                (String) getValue(NAME));
        
    }

    private ActionPair getActionPair(MindMapNode targetNode, String style)
             {
        NodeStyleFormatAction styleAction = createNodeStyleFormatAction(targetNode, style);
        NodeStyleFormatAction undoStyleAction = createNodeStyleFormatAction(targetNode, targetNode.getStyle());
        return new ActionPair(styleAction, undoStyleAction);
    }

    private NodeStyleFormatAction createNodeStyleFormatAction(MindMapNode selected, String style)
            {
        NodeStyleFormatAction nodeStyleAction = new NodeStyleFormatAction();
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
    //                logger.info("Setting style of " + node + " to "+ style);
                    node.setStyle(style);
                    modeController.nodeStructureChanged(node.isRoot() ? node : node.getParentNode());
                }
            }
        }
}