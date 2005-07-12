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
/*$Id: CutAction.java,v 1.1.4.6.6.1 2005-07-12 15:41:16 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBException;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.TransferableContentType;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.actions.PasteAction.NodeCoordinate;

public class CutAction extends AbstractAction implements ActorXml {
    private String text;
    private final ControllerAdapter c;
    public CutAction(ControllerAdapter c) {
        super(
            c.getText("cut"),
            new ImageIcon(c.getResource("images/editcut.png")));
        this.c = c;
        this.text = c.getText("cut");
        setEnabled(false);
		this.c.getActionFactory().registerActor(this, getDoActionClass());
    }
    public void actionPerformed(ActionEvent e) {
		Transferable copy = c.cut();
		// and set it.
		c.getClipboard().setContents(copy, null);
		c.getController().obtainFocusForSelected();
    }

	public CutNodeAction getCutNodeAction(Transferable t, MindMapNode node) throws JAXBException {
		return getCutNodeAction(t, new NodeCoordinate(node, node.isLeft().getValue()));
	}


    /** 
    */
    public CutNodeAction getCutNodeAction(Transferable t, NodeCoordinate coord)
        throws JAXBException {
        CutNodeAction cutAction =
            c.getActionXmlFactory().createCutNodeAction();
        cutAction.setTransferableContent(getTransferableContent(t));
        cutAction.setNode(c.getNodeID(coord.target));
		cutAction.setAsSibling(coord.asSibling);
		cutAction.setIsLeft(coord.isLeft);
       
        return cutAction;
    }

    public Transferable cut(List nodeList) {
	c.sortNodesByDepth(nodeList);
    	Transferable totalCopy = c.getModel().copy(nodeList, null);
		try {
			// Do-action
			CompoundAction doAction = c.getActionXmlFactory().createCompoundAction();
			// Undo-action
			CompoundAction undo= c.getActionXmlFactory().createCompoundAction();
			// sort selectedNodes list by depth, in order to guarantee that sons are deleted first:
			for (Iterator i = nodeList.iterator(); i.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();
				if(node.getParentNode() == null) continue;
				Transferable copy = c.getModel().copy(node);
				NodeCoordinate coord = new NodeCoordinate(node, node.isLeft().getValue());
                CutNodeAction cutNodeAction = getCutNodeAction( copy, coord);
				doAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(cutNodeAction);
				
				PasteNodeAction pasteNodeAction=null;
                pasteNodeAction = c.paste.getPasteNodeAction(copy, coord);
                // The paste actions are reversed because of the strange coordinates.
				undo.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(0,pasteNodeAction);
                
            }
			if (doAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().size() > 0){
			    c.getActionFactory().startTransaction(text);
			    c.getActionFactory().executeAction(new ActionPair(doAction, undo));
			    c.getActionFactory().endTransaction(text);
			}
			return totalCopy;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return totalCopy;
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
        CutNodeAction cutAction = (CutNodeAction) action;
        // clear all recently cutted links from the registry:
        c.getModel().getLinkRegistry().clearCuttedNodeBuffer();
		NodeCoordinate coord = new NodeCoordinate(c.getNodeFromID(cutAction.getNode()), cutAction.isAsSibling(), cutAction.isIsLeft());
        MindMapNode selectedNode = coord.getNode();
		c.getModel().getLinkRegistry().cutNode(selectedNode);
		c.deleteChild.deleteWithoutUndo(selectedNode);
    }

	public TransferableContentType getTransferableContent(
		Transferable t) throws JAXBException {

		try {
			TransferableContentType trans =
					c.getActionXmlFactory().createTransferableContentType();
			if (t.isDataFlavorSupported(MindMapNodesSelection.mindMapNodesFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(MindMapNodesSelection.mindMapNodesFlavor);
				trans.setTransferable(textFromClipboard);
			}
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(DataFlavor.stringFlavor);
				trans.setTransferableAsPlainText(textFromClipboard);
			}
			if (t.isDataFlavorSupported(MindMapNodesSelection.rtfFlavor)) {
//				byte[] textFromClipboard = (byte[]) t.getTransferData(MindMapNodesSelection.rtfFlavor);
//				trans.setTransferableAsRTF(textFromClipboard.toString());
			}
			if(t.isDataFlavorSupported(MindMapNodesSelection.htmlFlavor)) {
				String textFromClipboard;
				textFromClipboard =
					(String) t.getTransferData(MindMapNodesSelection.htmlFlavor);
				trans.setTransferableAsHtml(textFromClipboard);
			}
			if(t.isDataFlavorSupported(MindMapNodesSelection.fileListFlavor)) {
				/* Since the JAXB-generated interface TransferableContentType doesn't supply
				  a setTranserableAsFileList method, we have to get the fileList, clear it,
				  and then set it to the new value.
				*/ 
	            List fileList = (List)t.getTransferData(MindMapNodesSelection.fileListFlavor);
				List listCopy = trans.getTransferableAsFileList();
				listCopy.clear();
				listCopy.addAll(fileList);
			}
			return trans;
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null; 
	}

    public Transferable getTransferable(TransferableContentType trans) {
        // create Transferable:
        //URGENT: Add file list to this selection.
        Transferable copy =
            new MindMapNodesSelection(
                trans.getTransferable(),
        		trans.getTransferableAsPlainText(),
                trans.getTransferableAsRTF(),
                trans.getTransferableAsDrop(), 
                trans.getTransferableAsHtml(), 
            	trans.getTransferableAsFileList());
        return copy;
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return CutNodeAction.class;
    }

}