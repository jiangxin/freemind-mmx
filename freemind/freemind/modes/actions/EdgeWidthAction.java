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
/* $Id: EdgeWidthAction.java,v 1.1.4.1.10.1 2005-07-12 15:41:16 dpolivaev Exp $ */

package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.EdgeWidthFormatAction;
import freemind.controller.actions.generated.instance.EdgeWidthFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class EdgeWidthAction extends NodeGeneralAction implements NodeActorXml {
    private int mWidth;

    public EdgeWidthAction(ModeController controller, int width) {
        super(controller, null, null);
        this.mWidth = width;
        setName(getWidthTitle(controller, width));
        addActor(this);
    }

    public ActionPair apply(MapAdapter model, MindMapNode selected)
            throws JAXBException {
        return getActionPair(selected, mWidth);
    }

    public Class getDoActionClass() {
        return EdgeWidthFormatAction.class;
    }

    public void setEdgeWidth(MindMapNode node, int width) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, width));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private ActionPair getActionPair(MindMapNode selected, int width)
            throws JAXBException {
        EdgeWidthFormatAction styleAction = createEdgeWidthFormatAction(
                selected, width);
        EdgeWidthFormatAction undoStyleAction = createEdgeWidthFormatAction(
                selected, ((EdgeAdapter) selected.getEdge()).getRealWidth());
        return new ActionPair(styleAction, undoStyleAction);
    }

    private EdgeWidthFormatAction createEdgeWidthFormatAction(
            MindMapNode selected, int  width) throws JAXBException {
        EdgeWidthFormatAction edgeWidthAction = getActionXmlFactory()
                .createEdgeWidthFormatAction();
        edgeWidthAction.setNode(getNodeID(selected));
        edgeWidthAction.setWidth(width);
        return edgeWidthAction;
    }

    public void act(XmlAction action) {
        if (action instanceof EdgeWidthFormatAction) {
            EdgeWidthFormatAction edgeWithAction = (EdgeWidthFormatAction) action;
            MindMapNode node = getNodeFromID(edgeWithAction.getNode());
            int width = edgeWithAction.getWidth();
            if (((EdgeAdapter) node.getEdge()).getRealWidth() != width) {
                ((EdgeAdapter) node.getEdge()).setWidth(width);
                modeController.nodeChanged(node);
            }
        }
    }

    private static String getWidthTitle(ModeController controller, int width) {
        String returnValue;
        if (width == EdgeAdapter.WIDTH_PARENT) {
            returnValue = controller.getText("edge_width_parent");
        } else if (width == EdgeAdapter.WIDTH_THIN) {
            returnValue = controller.getText("edge_width_thin");
        } else {
            returnValue = Integer.toString(width);
        }
        return /*controller.getText("edge_width") +*/ returnValue;
    }

}