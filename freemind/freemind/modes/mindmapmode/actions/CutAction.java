/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 09.05.2004
 */
/* $Id: CutAction.java,v 1.1.2.2.2.10 2007-08-12 08:06:43 dpolivaev Exp $ */

package freemind.modes.mindmapmode.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.TransferableContent;
import freemind.controller.actions.generated.instance.TransferableFile;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class CutAction extends AbstractAction implements ActorXml {
    private String text;
    private final MindMapController controller;
    public CutAction(MindMapController c) {
        super(
            c.getText("cut"),
            new ImageIcon(c.getResource("images/editcut.png")));
        this.controller = c;
        this.text = c.getText("cut");
        setEnabled(false);
		this.controller.getActionFactory().registerActor(this, getDoActionClass());
    }
    public void actionPerformed(ActionEvent e) {
    	if (controller.getView().getRoot().isSelected()){
    		controller.getController().errorMessage(
    				controller.getFrame().getResourceString("cannot_delete_root"));
    		return;
    	}
		Transferable copy = controller.cut();
		// and set it.
		controller.getClipboard().setContents(copy, null);
		controller.getController().obtainFocusForSelected();
    }

	public CutNodeAction getCutNodeAction(MindMapNode node){
        CutNodeAction cutAction =            new CutNodeAction();
        cutAction.setNode(controller.getNodeID(node));
        return cutAction;
	}


    public Transferable cut(List nodeList) {
	controller.sortNodesByDepth(nodeList);
    	Transferable totalCopy = controller.copy(nodeList, true);
		// Do-action
        CompoundAction doAction = new CompoundAction();
        // Undo-action
        CompoundAction undo= new CompoundAction();
        // sort selectedNodes list by depth, in order to guarantee that sons are deleted first:
        for (Iterator i = nodeList.iterator(); i.hasNext();) {
        	MindMapNode node = (MindMapNode) i.next();
        	if(node.getParentNode() == null) continue;
            CutNodeAction cutNodeAction = getCutNodeAction(node);
        	doAction.addChoice(cutNodeAction);

        	NodeCoordinate coord = new NodeCoordinate(node, node.isLeft());
        	Transferable copy = controller.copy(node, true);
        	XmlAction pasteNodeAction = controller.paste.getPasteNodeAction(copy, coord);
            // The paste actions are reversed because of the strange coordinates.
        	undo.addAtChoice(0,pasteNodeAction);

        }
        if (doAction.sizeChoiceList() > 0){
            controller.getActionFactory().startTransaction(text);
            controller.getActionFactory().executeAction(new ActionPair(doAction, undo));
            controller.getActionFactory().endTransaction(text);
        }
        return totalCopy;
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
        CutNodeAction cutAction = (CutNodeAction) action;
        // clear all recently cutted links from the registry:
        controller.getModel().getLinkRegistry().clearCuttedNodeBuffer();
        MindMapNode selectedNode = controller.getNodeFromID(cutAction.getNode());
		controller.getModel().getLinkRegistry().cutNode(selectedNode);
		controller.deleteChild.deleteWithoutUndo(selectedNode);
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return CutNodeAction.class;
    }

}