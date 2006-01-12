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
/*$Id: ToggleFoldedAction.java,v 1.1.2.1 2006-01-12 23:10:13 christianfoltin Exp $*/

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FoldAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
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

		boolean fold = CommonToggleFoldedAction.getFoldingState(CommonToggleFoldedAction.reset(listIterator));
		CompoundAction doAction = createFoldAction(CommonToggleFoldedAction.reset(listIterator), fold,
				false);
		CompoundAction undoAction = createFoldAction(CommonToggleFoldedAction.reset(listIterator),
				!fold, true);
		modeController.getActionFactory().startTransaction(
				(String) getValue(NAME));
		modeController.getActionFactory().executeAction(
				new ActionPair(doAction, undoAction));
		modeController.getActionFactory().endTransaction(
				(String) getValue(NAME));
	}

	private CompoundAction createFoldAction(ListIterator iterator,
			boolean fold, boolean undo) {
		try {
			CompoundAction comp = modeController.getActionXmlFactory()
					.createCompoundAction();
			MindMapNode lastNode = null;
			// sort selectedNodes list by depth, in order to guarantee that sons
			// are deleted first:
			for (ListIterator it = iterator; it.hasNext();) {
				MindMapNode node = (MindMapNode) it.next();
				FoldAction foldAction = createSingleFoldAction(fold, node, undo);
				if (foldAction != null) {
					if (!undo) {
						comp
								.getCompoundActionOrSelectNodeActionOrCutNodeAction()
								.add(foldAction);
					} else {
						// reverse the order:
						comp
								.getCompoundActionOrSelectNodeActionOrCutNodeAction()
								.add(0, foldAction);
					}
					lastNode = node;
				}
			}
			logger.finest("Compound contains "
					+ comp.getCompoundActionOrSelectNodeActionOrCutNodeAction()
							.size() + " elements.");
			return comp;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param fold
	 * @param node
	 * @return null if node cannot be folded.
	 * @throws JAXBException
	 */
	private FoldAction createSingleFoldAction(boolean fold, MindMapNode node,
			boolean undo) {
		FoldAction foldAction = null;
		try {
			if ((undo && (node.isFolded() == fold))
					|| (!undo && (node.isFolded() != fold))) {
				if (node.hasChildren()
						|| Tools.safeEquals(modeController.getFrame()
								.getProperty("enable_leaves_folding"), "true")) {
					foldAction = modeController.getActionXmlFactory()
							.createFoldAction();
					foldAction.setFolded(fold);
					foldAction.setNode(modeController.getNodeID(node));
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return foldAction;
	}

	public void act(XmlAction action) {
		if (action instanceof FoldAction) {
			FoldAction foldAction = (FoldAction) action;
			MindMapNode node = modeController.getNodeFromID(foldAction
					.getNode());
			boolean fold = foldAction.isFolded();
			modeController._setFolded(node, fold);
		}
	}

	public Class getDoActionClass() {
		return FoldAction.class;
	}

	/**
	 * @param node
	 * @param folded
	 */
	public void setFolded(MindMapNode node, boolean folded) {
		FoldAction doAction = createSingleFoldAction(folded, node, false);
		FoldAction undoAction = createSingleFoldAction(!folded, node, true);
		if (doAction == null || undoAction == null) {
			return;
		}
		modeController.getActionFactory().startTransaction(
				(String) getValue(NAME));
		modeController.getActionFactory().executeAction(
				new ActionPair(doAction, undoAction));
		modeController.getActionFactory().endTransaction(
				(String) getValue(NAME));
	}

}