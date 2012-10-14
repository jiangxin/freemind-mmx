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
 * Created on 12.08.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FoldAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.common.CommonToggleFoldedAction;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class ToggleFoldedAction extends AbstractAction implements ActorXml {
	private final MindMapController modeController;

	private Logger logger;

	public ToggleFoldedAction(MindMapController controller) {
		super(controller.getText("toggle_folded"));
		this.modeController = controller;
		modeController.getActionFactory().registerActor(this,
				getDoActionClass());
		logger = modeController.getFrame().getLogger(this.getClass().getName());
	}

	public void actionPerformed(ActionEvent e) {
		toggleFolded();
	}

	public void toggleFolded() {
		toggleFolded(modeController.getSelecteds().listIterator());
	}

	public void toggleFolded(ListIterator listIterator) {

		boolean fold = CommonToggleFoldedAction
				.getFoldingState(CommonToggleFoldedAction.reset(listIterator));
		CompoundAction doAction = createFoldAction(
				CommonToggleFoldedAction.reset(listIterator), fold, false);
		CompoundAction undoAction = createFoldAction(
				CommonToggleFoldedAction.reset(listIterator), !fold, true);
		modeController.doTransaction(
				(String) getValue(NAME), new ActionPair(doAction, undoAction));
	}

	private CompoundAction createFoldAction(ListIterator iterator,
			boolean fold, boolean undo) {
		CompoundAction comp = new CompoundAction();
		MindMapNode lastNode = null;
		// sort selectedNodes list by depth, in order to guarantee that sons
		// are deleted first:
		for (ListIterator it = iterator; it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			FoldAction foldAction = createSingleFoldAction(fold, node, undo);
			if (foldAction != null) {
				if (!undo) {
					comp

					.addChoice(foldAction);
				} else {
					// reverse the order:
					comp

					.addAtChoice(0, foldAction);
				}
				lastNode = node;
			}
		}
		logger.finest("Compound contains " + comp.sizeChoiceList()
				+ " elements.");
		return comp;
	}

	/**
	 * @return null if node cannot be folded.
	 */
	private FoldAction createSingleFoldAction(boolean fold, MindMapNode node,
			boolean undo) {
		FoldAction foldAction = null;
		if ((undo && (node.isFolded() == fold))
				|| (!undo && (node.isFolded() != fold))) {
			if (node.hasChildren()
					|| Tools.safeEquals(
							modeController.getFrame().getProperty(
									"enable_leaves_folding"), "true")) {
				foldAction = new FoldAction();
				foldAction.setFolded(fold);
				foldAction.setNode(modeController.getNodeID(node));
			}
		}
		return foldAction;
	}

	public void act(XmlAction action) {
		if (action instanceof FoldAction) {
			FoldAction foldAction = (FoldAction) action;
			MindMapNode node = modeController.getNodeFromID(foldAction
					.getNode());
			boolean fold = foldAction.getFolded();
			modeController._setFolded(node, fold);
			if (Resources.getInstance().getBoolProperty(
					FreeMind.RESOURCES_SAVE_FOLDING_STATE)) {
				modeController.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return FoldAction.class;
	}

	/**
	 */
	public void setFolded(MindMapNode node, boolean folded) {
		FoldAction doAction = createSingleFoldAction(folded, node, false);
		FoldAction undoAction = createSingleFoldAction(!folded, node, true);
		if (doAction == null || undoAction == null) {
			return;
		}
		modeController.doTransaction(
				(String) getValue(NAME), new ActionPair(doAction, undoAction));
	}

}
