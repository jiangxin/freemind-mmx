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
/* $Id: MoveNodeAction.java,v 1.1.4.2 2005-04-27 21:45:30 christianfoltin Exp $ */
package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.MoveNodeXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;

public class MoveNodeAction extends NodeGeneralAction implements NodeActorXml {
    private final ControllerAdapter modeController;

    /**
     * @param textID
     * @param iconPath
     * @param actor
     */
    public MoveNodeAction(ControllerAdapter modeController) {
        super(modeController, "reset_node_position", (String) null);
        this.modeController = modeController;
        addActor(this);
    }

    public void act(XmlAction action) {
        MoveNodeXmlAction moveAction = (MoveNodeXmlAction) action;
        NodeAdapter node = getNodeFromID(moveAction.getNode());
        if (node.getHGap() != moveAction.getHGap()
                || node.getVGap() != moveAction.getVGap()
                || node.getShiftY() != moveAction.getShiftY()) {
            node.setHGap(moveAction.getHGap());
            node.setVGap(moveAction.getVGap());
            node.setShiftY(moveAction.getShiftY());
            this.modeController.nodeChanged(node);
        }
    }

    public Class getDoActionClass() {
        return MoveNodeXmlAction.class;
    }

    public ActionPair apply(MapAdapter model, MindMapNode selected)
            throws JAXBException {
        // reset position
        return getActionPair(selected, MindMapNode.AUTO, 0, 0);
    }

    private ActionPair getActionPair(MindMapNode selected, int vGap, int hGap,
            int shiftY) throws JAXBException {
        MoveNodeXmlAction moveAction = moveNode(selected, vGap, hGap, shiftY);
        MoveNodeXmlAction undoItalicAction = moveNode(selected, selected
                .getVGap(), selected.getHGap(), selected.getShiftY());
        return new ActionPair(moveAction, undoItalicAction);
    }

    private MoveNodeXmlAction moveNode(MindMapNode selected, int vGap,
            int hGap, int shiftY) throws JAXBException {
        MoveNodeXmlAction moveNodeAction = getActionXmlFactory()
                .createMoveNodeXmlAction();
        moveNodeAction.setNode(getNodeID(selected));
        moveNodeAction.setHGap(hGap);
        moveNodeAction.setVGap(vGap);
        moveNodeAction.setShiftY(shiftY);
        return moveNodeAction;
    }

    public void moveNodeTo(MindMapNode node, int vGap, int hGap, int shiftY) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, vGap, hGap, shiftY));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}