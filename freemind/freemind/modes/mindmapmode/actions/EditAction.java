/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.view.mindmapview.EditNodeBase;
import freemind.view.mindmapview.EditNodeDialog;
import freemind.view.mindmapview.EditNodeExternalApplication;
import freemind.view.mindmapview.EditNodeTextField;
import freemind.view.mindmapview.EditNodeWYSIWYG;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

//
//Node editing
//

public class EditAction extends AbstractAction implements ActorXml {
	private static final Pattern HTML_HEAD = Pattern.compile(
			"\\s*<head>.*</head>", Pattern.DOTALL);
	private final MindMapController mMindMapController;
	private EditNodeBase mCurrentEditDialog = null;

	public EditAction(MindMapController modeController) {
		super(modeController.getText("edit_node"));
		this.mMindMapController = modeController;
		this.mMindMapController.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent arg0) {
		MindMapNode selected = this.mMindMapController.getSelected();
		this.mMindMapController.edit(null, false, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		EditNodeAction editAction = (EditNodeAction) action;
		NodeAdapter node = this.mMindMapController.getNodeFromID(editAction
				.getNode());
		if (!node.toString().equals(editAction.getText())) {
			node.setUserObject(editAction.getText());
			this.mMindMapController.nodeChanged(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EditNodeAction.class;
	}

	// edit begins with home/end or typing (PN 6.2)
	public void edit(KeyEvent e, boolean addNew, boolean editLong) {
		NodeView selectedNodeView = mMindMapController.getView().getSelected();
		if (selectedNodeView != null) {
			if (e == null || !addNew) {
				edit(selectedNodeView, selectedNodeView, e, false, false,
						editLong);
			} else if (!mMindMapController.isBlocked()) {
				mMindMapController.addNew(mMindMapController.getSelected(),
						MindMapController.NEW_SIBLING_BEHIND, e);
			}
			if (e != null) {
				e.consume();
			}
		}
	}

	public void edit(final NodeView node, final NodeView prevSelected,
			final KeyEvent firstEvent, final boolean isNewNode,
			final boolean parentFolded, final boolean editLong) {
		if (node == null) {
			return;
		}
		final MapView map = node.getMap();
		map.validate();
		map.invalidate();

		stopEditing();
		// EditNodeBase.closeEdit();
		mMindMapController.setBlocked(true); // locally "modal" stated

		String text = node.getModel().toString();
		String htmlEditingOption = mMindMapController.getController()
				.getProperty("html_editing_option");

		boolean isHtmlNode = HtmlTools.isHtmlNode(text);
		boolean isLongNode = node.getIsLong();

		// do we need a decision if plain or HTML editing?
		String useRichTextInNewLongNodes = (isHtmlNode) ? "true" : "false";
		// if the node is not already html, we ask if rich text or plain text
		// edit.
		if (!isHtmlNode && !isLongNode && editLong) {
			// ask user:
			int showResult = new OptionalDontShowMeAgainDialog(
					mMindMapController.getFrame().getJFrame(),
					mMindMapController.getSelectedView(),
					"edit.edit_rich_text",
					"edit.decision",
					mMindMapController,
					new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
							mMindMapController.getController(),
							FreeMind.RESOURCES_REMIND_USE_RICH_TEXT_IN_NEW_LONG_NODES),
					OptionalDontShowMeAgainDialog.BOTH_OK_AND_CANCEL_OPTIONS_ARE_STORED)
					.show().getResult();
			useRichTextInNewLongNodes = (showResult == JOptionPane.OK_OPTION) ? "true"
					: "false";
		}
		// useRichTextInNewLongNodes =
		// c.getController().getProperty("use_rich_text_in_new_long_nodes");
		boolean editHtml = isHtmlNode
				|| (editLong && Tools.safeEquals(useRichTextInNewLongNodes,
						"true"));
		boolean editInternalWysiwyg = editHtml
				&& Tools.safeEquals(htmlEditingOption, "internal-wysiwyg");
		boolean editExternal = editHtml
				&& Tools.safeEquals(htmlEditingOption, "external");

		if (editHtml && !isHtmlNode) {
			text = HtmlTools.plainToHTML(text);
		}
		if (editInternalWysiwyg) {
			EditNodeWYSIWYG editNodeWYSIWYG = new EditNodeWYSIWYG(node, text,
					firstEvent, mMindMapController,
					new EditNodeBase.EditControl() {
						public void cancel() {
							mMindMapController.setBlocked(false);
							mCurrentEditDialog = null;
							mMindMapController.getController()
									.obtainFocusForSelected();
						}

						public void ok(String newText) {
							setHtmlText(node, newText);
							cancel();
						}

						public void split(String newText, int position) {
							mMindMapController.splitNode(node.getModel(),
									position, newText);
							cancel();
						}
					}); // focus fix
			mCurrentEditDialog = editNodeWYSIWYG;
			editNodeWYSIWYG.show();
			return;
		}

		if (editExternal) {
			EditNodeExternalApplication editNodeExternalApplication = new EditNodeExternalApplication(
					node, text, firstEvent, mMindMapController,
					new EditNodeBase.EditControl() {
						public void cancel() {
							mMindMapController.setBlocked(false);
							mCurrentEditDialog = null;
							mMindMapController.getController()
									.obtainFocusForSelected();
						}

						public void ok(String newText) {
							setHtmlText(node, newText);
							cancel();
						}

						public void split(String newText, int position) {
							mMindMapController.splitNode(node.getModel(),
									position, newText);
							cancel();
						}
					}); // focus fix
			mCurrentEditDialog = editNodeExternalApplication;
			editNodeExternalApplication.show();
			// We come here before quitting the editor window.
			return;
		}

		if (isLongNode || editLong) {
			EditNodeDialog nodeEditDialog = new EditNodeDialog(node, text,
					firstEvent, mMindMapController,
					new EditNodeBase.EditControl() {

						public void cancel() {
							mMindMapController.setBlocked(false);
							mCurrentEditDialog = null;
							mMindMapController.getController()
									.obtainFocusForSelected(); // focus fix
						}

						public void ok(String newText) {
							setNodeText(node.getModel(), newText);
							cancel();
						}

						public void split(String newText, int position) {
							mMindMapController.splitNode(node.getModel(),
									position, newText);
							cancel();
						}
					});
			mCurrentEditDialog = nodeEditDialog;
			nodeEditDialog.show();
			return;
		}
		// inline editing:
		EditNodeTextField textfield = new EditNodeTextField(node, text,
				firstEvent, mMindMapController, new EditNodeBase.EditControl() {

					public void cancel() {
						if (isNewNode) { // delete also the node and set focus
											// to the parent
							mMindMapController.getView()
									.selectAsTheOnlyOneSelected(node);
							Vector nodeList = new Vector();
							nodeList.add(node.getModel());
							mMindMapController.cut(nodeList);
							mMindMapController.select(prevSelected);
							// include max level for navigation
							if (parentFolded) {
								mMindMapController.setFolded(
										prevSelected.getModel(), true);
							}
						}
						endEdit();
					}

					public void ok(String newText) {
						setNodeText(node.getModel(), newText);
						endEdit();
					}

					private void endEdit() {
						mMindMapController.obtainFocusForSelected();
						mMindMapController.setBlocked(false);
						mCurrentEditDialog = null;
					}

					public void split(String newText, int position) {
					}
				});
		mCurrentEditDialog = textfield;
		textfield.show();

	}

	public void setNodeText(MindMapNode selected, String newText) {
		String oldText = selected.toString();

		EditNodeAction EditAction = new EditNodeAction();
		EditAction.setNode(mMindMapController.getNodeID(selected));
		EditAction.setText(newText);

		EditNodeAction undoEditAction = new EditNodeAction();
		undoEditAction.setNode(mMindMapController.getNodeID(selected));
		undoEditAction.setText(oldText);

		mMindMapController.doTransaction(
				mMindMapController.getText("edit_node"),
				new ActionPair(EditAction, undoEditAction));
	}

	protected MindMapController getMindMapController() {
		return mMindMapController;
	}

	private void setHtmlText(final NodeView node, String newText) {
		final String body = HTML_HEAD.matcher(newText).replaceFirst("");
		setNodeText(node.getModel(), body);
	}

	public void stopEditing() {
		if (mCurrentEditDialog != null) {
			// there was previous editing.
			mCurrentEditDialog.closeEdit();
			mCurrentEditDialog = null;
		}
	}

}
