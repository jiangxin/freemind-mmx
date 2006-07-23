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
/* $Id: CutAction.java,v 1.1.2.2.2.3 2006-07-23 20:34:09 christianfoltin Exp $ */

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
		Transferable copy = controller.cut();
		// and set it.
		controller.getClipboard().setContents(copy, null);
		controller.getController().obtainFocusForSelected();
    }

	public CutNodeAction getCutNodeAction(Transferable t, MindMapNode node){
		return getCutNodeAction(t, new NodeCoordinate(node, node.isLeft().getValue()));
	}


    /**
    */
    public CutNodeAction getCutNodeAction(Transferable t, NodeCoordinate coord)
         {
        CutNodeAction cutAction =
            new CutNodeAction();
        cutAction.setTransferableContent(getTransferableContent(t));
        cutAction.setNode(controller.getNodeID(coord.target));
		cutAction.setAsSibling(coord.asSibling);
		cutAction.setIsLeft(coord.isLeft);

        return cutAction;
    }

    public Transferable cut(List nodeList) {
	controller.sortNodesByDepth(nodeList);
    	Transferable totalCopy = controller.getModel().copy(nodeList, null);
		// Do-action
        CompoundAction doAction = new CompoundAction();
        // Undo-action
        CompoundAction undo= new CompoundAction();
        // sort selectedNodes list by depth, in order to guarantee that sons are deleted first:
        for (Iterator i = nodeList.iterator(); i.hasNext();) {
        	MindMapNode node = (MindMapNode) i.next();
        	if(node.getParentNode() == null) continue;
        	Transferable copy = controller.getModel().copy(node);
        	NodeCoordinate coord = new NodeCoordinate(node, node.isLeft().getValue());
            CutNodeAction cutNodeAction = getCutNodeAction( copy, coord);
        	doAction.addChoice(cutNodeAction);

        	PasteNodeAction pasteNodeAction=null;
            pasteNodeAction = controller.paste.getPasteNodeAction(copy, coord);
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
		NodeCoordinate coord = new NodeCoordinate(controller.getNodeFromID(cutAction.getNode()), cutAction.getAsSibling(), cutAction.getIsLeft());
        MindMapNode selectedNode = coord.getNode();
		controller.getModel().getLinkRegistry().cutNode(selectedNode);
		controller.deleteChild.deleteWithoutUndo(selectedNode);
    }

	public TransferableContent getTransferableContent(
		Transferable t)  {

		try {
			TransferableContent trans =
					new TransferableContent();
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
				/* Since the JAXB-generated interface TransferableContent doesn't supply
				  a setTranserableAsFileList method, we have to get the fileList, clear it,
				  and then set it to the new value.
				*/
	            List fileList = (List)t.getTransferData(MindMapNodesSelection.fileListFlavor);
                for (Iterator iter = fileList.iterator(); iter.hasNext();) {
                    File fileName = (File) iter.next();
                    TransferableFile transferableFile = new TransferableFile();
                    transferableFile.setFileName(fileName.getAbsolutePath());
                    trans.addTransferableFile(transferableFile);
                }
			}
			return trans;
		} catch (UnsupportedFlavorException e) {
freemind.main.Resources.getInstance().logExecption(			e);
		} catch (IOException e) {
freemind.main.Resources.getInstance().logExecption(			e);
		}
		return null;
	}

    public Transferable getTransferable(TransferableContent trans) {
        // create Transferable:
        //Add file list to this selection.
        Vector fileList = new Vector();
        for (Iterator iter = trans.getListTransferableFileList().iterator(); iter.hasNext();)
        {
            TransferableFile tFile = (TransferableFile) iter.next();
            fileList.add(new File(tFile.getFileName()));
        }
        Transferable copy =
            new MindMapNodesSelection(
                trans.getTransferable(),
        		trans.getTransferableAsPlainText(),
                trans.getTransferableAsRTF(),
                trans.getTransferableAsHtml(),
                trans.getTransferableAsDrop(),
                fileList);
        return copy;
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return CutNodeAction.class;
    }

}