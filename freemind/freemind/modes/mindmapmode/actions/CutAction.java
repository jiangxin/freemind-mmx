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


package freemind.modes.mindmapmode.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class CutAction extends AbstractAction implements ActorXml {
	private String text;
	private final MindMapController mMindMapController;
	private static java.util.logging.Logger logger = null;

	public CutAction(MindMapController c) {
		super(c.getText("cut"), new ImageIcon(
				c.getResource("images/editcut.png")));
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.mMindMapController = c;
		this.text = c.getText("cut");
		setEnabled(false);
		this.mMindMapController.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		if (mMindMapController.getView().getRoot().isSelected()) {
			mMindMapController.getController().errorMessage(
					mMindMapController.getFrame().getResourceString(
							"cannot_delete_root"));
			return;
		}
		int showResult = new OptionalDontShowMeAgainDialog(mMindMapController
				.getFrame().getJFrame(), mMindMapController.getSelectedView(),
				"really_cut_node", "confirmation", mMindMapController,
				new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
						mMindMapController.getController(),
						FreeMind.RESOURCES_CUT_NODES_WITHOUT_QUESTION),
				OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
				.show().getResult();
		if (showResult != JOptionPane.OK_OPTION) {
			return;
		}
		Transferable copy = mMindMapController.cut();
		// and set it.
		mMindMapController.setClipboardContents(copy);
		mMindMapController.getController().obtainFocusForSelected();
	}

	public CutNodeAction getCutNodeAction(MindMapNode node) {
		CutNodeAction cutAction = new CutNodeAction();
		cutAction.setNode(mMindMapController.getNodeID(node));
		return cutAction;
	}

	public Transferable cut(List nodeList) {
		mMindMapController.sortNodesByDepth(nodeList);
		Transferable totalCopy = mMindMapController.copy(nodeList, true);
		// Do-action
		CompoundAction doAction = new CompoundAction();
		// Undo-action
		CompoundAction undo = new CompoundAction();
		// sort selectedNodes list by depth, in order to guarantee that sons are
		// deleted first:
		for (Iterator i = nodeList.iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			if (node.getParentNode() == null)
				continue;
			CutNodeAction cutNodeAction = getCutNodeAction(node);
			doAction.addChoice(cutNodeAction);

			NodeCoordinate coord = new NodeCoordinate(node, node.isLeft());
			Transferable copy = mMindMapController.copy(node, true);
			XmlAction pasteNodeAction = mMindMapController.paste
					.getPasteNodeAction(copy, coord, (UndoPasteNodeAction) null);
			logger.fine("Undo for cut: "
					+ mMindMapController.marshall(pasteNodeAction));
			// The paste actions are reversed because of the strange
			// coordinates.
			undo.addAtChoice(0, pasteNodeAction);

		}
		if (doAction.sizeChoiceList() > 0) {
			mMindMapController.doTransaction(text,
					new ActionPair(doAction, undo));
		}
		return totalCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		CutNodeAction cutAction = (CutNodeAction) action;
		MindMapNode selectedNode = mMindMapController.getNodeFromID(cutAction
				.getNode());
		mMindMapController.deleteChild.deleteWithoutUndo(selectedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return CutNodeAction.class;
	}

}