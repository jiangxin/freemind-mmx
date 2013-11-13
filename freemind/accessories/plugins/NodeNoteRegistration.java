/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Christian Foltin, Dimitry Polivaev and others.
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
 * Created on 11.09.2007
 */
/*$Id: NodeNoteRegistration.java,v 1.1.2.25 2010/10/07 21:19:51 christianfoltin Exp $*/

package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;

import com.inet.jortho.SpellChecker;
import com.lightdev.app.shtm.SHTMLPanel;
import com.lightdev.app.shtm.TextResources;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.FreeMindCommon;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.view.mindmapview.NodeView;

public class NodeNoteRegistration implements HookRegistration, ActorXml,
		MenuItemSelectedListener {
	public static final class SimplyHtmlResources implements TextResources {
		public String getString(String pKey) {
			// no splash for SimplyHtml.
			if (Tools.safeEquals("show_splash_screen", pKey)) {
				return "false";
			}
			if (Tools.safeEquals("default_paste_mode", pKey)) {
				return "PASTE_HTML";
			}
			pKey = "simplyhtml." + pKey;
			String resourceString;
			resourceString = Resources.getInstance().getResourceString(
					pKey, null);
			if (resourceString == null) {
				resourceString = Resources.getInstance().getProperty(pKey);
			}
//			if(resourceString == null) {
//				System.err.println("Can't find string " + pKey);
//			}
			return resourceString;
		}
	}

	private static class SouthPanel extends JPanel {
		private static final long serialVersionUID = -4624762713662343786L;

		public SouthPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		}

		protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
				int condition, boolean pressed) {
			return super.processKeyBinding(ks, e, condition, pressed)
					|| e.getKeyChar() == KeyEvent.VK_SPACE
					|| e.getKeyChar() == KeyEvent.VK_ALT;
		}
	}

	private final class NoteDocumentListener implements DocumentListener {
		private MindMapNode mNode;

		public void changedUpdate(DocumentEvent arg0) {
			docEvent();
		}

		private void docEvent() {
			// test if not already marked as dirty:
			if (getMindMapController().getMap().isSaved()) {
				// now test, if different:
				String documentText = normalizeString(getDocumentText());
				String noteText = normalizeString(mNode.getNoteText());
				logger.fine("Old doc =\n'" + noteText
						+ "', Current document: \n'" + documentText
						+ "'. Comparison: '"
						+ Tools.compareText(noteText, documentText) + "'.");
				if (!Tools.safeEquals(noteText, documentText)) {
					logger.finest("Making map dirty.");
					// make map dirty in order to enable automatic save on note
					// change.
					getMindMapController().getMap().setSaved(false);
				}
			}
		}

		public void insertUpdate(DocumentEvent arg0) {
			docEvent();
		}

		public void removeUpdate(DocumentEvent arg0) {
			docEvent();
		}

		public void setNode(MindMapNode pNode) {
			mNode = pNode;
		}
	}

	// private NodeTextListener listener;

	private final class NotesManager implements NodeSelectionListener,
			NodeLifetimeListener {

		private MindMapNode node;

		public NotesManager() {
		}

		public void onLostFocusNode(NodeView node) {
			// logger.info("onDeselectHook for node " + node +
			// " and noteViewerComponent=" + noteViewerComponent);
			getDocument().removeDocumentListener(
					mNoteDocumentListener);
			// store its content:
			onSaveNode(node.getModel());
			this.node = null;
			// getHtmlEditorPanel().setCurrentDocumentContent("Note", "");
		}

		public void onFocusNode(NodeView nodeView) {
			// logger.info("onSelectHook for node " + node +
			// " and noteViewerComponent=" + noteViewerComponent);
			this.node = nodeView.getModel();
			final HTMLDocument document = getDocument();
			// remove listener to avoid unnecessary dirty events.
			document.removeDocumentListener(mNoteDocumentListener);
			try {
				// Dimitry:
				// Images referenced from documents with bases given by
				// pFile.toURI().toURL() are not shown in SimplyHTML
				// (bug [ freemind-Bugs-2019223 ] Images are not shown in the
				// Notes view)
				// => the old method File.toURL() must be used again.
				document.setBase(node.getMap().getFile().toURI().toURL());
			} catch (Exception e) {
			}

			// logger.info("onReceiveFocuse for node " + node.toString());
			String note = node.getNoteText();
			if (note != null) {
				noteViewerComponent.setCurrentDocumentContent(note);
				mLastContentEmpty = false;
			} else if (!mLastContentEmpty) {
				noteViewerComponent.setCurrentDocumentContent("");
				mLastContentEmpty = true;
			}
			mNoteDocumentListener.setNode(node);
			document.addDocumentListener(mNoteDocumentListener);
		}

		public void onUpdateNodeHook(MindMapNode node) {
		}

		public void onSaveNode(MindMapNode node) {
			if (this.node != node) {
				return;
			}
			boolean editorContentEmpty = true;
			// // TODO: Save the style with the note.
			// StyleSheet styleSheet = noteViewerComponent.getDocument()
			// .getStyleSheet();
			// styleSheet.removeStyle("body");
			// styleSheet.removeStyle("p");
			JEditorPane editorPane = noteViewerComponent.getEditorPane();
			int caretPosition = editorPane.getCaretPosition();
			int selectionStart = editorPane.getSelectionStart();
			int selectionEnd = editorPane.getSelectionEnd();
			String documentText = getDocumentText();
			editorContentEmpty = documentText
					.equals(NodeNote.EMPTY_EDITOR_STRING)
					|| documentText
							.equals(NodeNote.EMPTY_EDITOR_STRING_ALTERNATIVE)
					|| documentText
							.equals(NodeNote.EMPTY_EDITOR_STRING_ALTERNATIVE2);
			// String noteText = node.getNoteText();
			// logger.info("Old doc =\n'" +
			// ((noteText==null)?noteText:noteText.replaceAll("\n", "\\\\n")) +
			// "', Current document: \n'" + documentText.replaceAll("\n",
			// "\\\\n") + "', empty="+editorContentEmpty);
			controller.deregisterNodeSelectionListener(this);
			if (noteViewerComponent.needsSaving()) {
				if (editorContentEmpty) {
					changeNoteText(null, node);
				} else {
					changeNoteText(documentText, node);
				}
				mLastContentEmpty = editorContentEmpty;
			}
			controller.registerNodeSelectionListener(this, false);
			try {
				// on inserting tabs, the caret position changes, as they are deleted:
				if (caretPosition < getDocument().getLength()) {
					editorPane.setCaretPosition(caretPosition);
				}
				editorPane.setSelectionEnd(selectionEnd);
				editorPane.setSelectionStart(selectionStart);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}

		public void onCreateNodeHook(MindMapNode node) {
			if (node.getXmlNoteText() != null) {
				setStateIcon(node, true);
			}
		}

		public void onPreDeleteNode(MindMapNode node) {
		}

		public void onPostDeleteNode(MindMapNode pNode, MindMapNode pParent) {
		}

		/* (non-Javadoc)
		 * @see freemind.modes.ModeController.NodeSelectionListener#onSelectionChange(freemind.modes.MindMapNode, boolean)
		 */
		public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
		}
	}

	private static SHTMLPanel htmlEditorPanel;

	/**
	 * Indicates, whether or not the main panel has to be refreshed with new
	 * content. The typical content will be empty, so this state is saved here.
	 */
	private static boolean mLastContentEmpty = true;

	private final MindMapController controller;

	protected SHTMLPanel noteViewerComponent;

	private final java.util.logging.Logger logger;

	private NotesManager mNotesManager;

	private static ImageIcon noteIcon = null;

	private NoteDocumentListener mNoteDocumentListener;

	static Integer sPositionToRecover = null;

	private JSplitPane mSplitPane = null;

	public NodeNoteRegistration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		logger = controller.getFrame().getLogger(this.getClass().getName());
	}

	public boolean shouldUseSplitPane() {
		return "true".equals(controller.getFrame().getProperty(
				FreeMind.RESOURCES_USE_SPLIT_PANE));
	}

	class JumpToMapAction extends AbstractAction {
		private static final long serialVersionUID = -531070508254258791L;

		public void actionPerformed(ActionEvent e) {
			if (sPositionToRecover != null) {
				mSplitPane.setDividerLocation(sPositionToRecover.intValue());
				sPositionToRecover = null;
			}
			logger.info("Jumping back to map!");
			controller.getController().obtainFocusForSelected();
		}
	};

	public void register() {
		logger.fine("Registration of note handler.");
		controller.getActionFactory().registerActor(this, getDoActionClass());
		// moved to registration:
		noteViewerComponent = getNoteViewerComponent();
		// register "leave note" action:
		Action jumpToMapAction = new JumpToMapAction();
		String keystroke = controller
				.getFrame()
				.getAdjustableProperty(
						"keystroke_accessories/plugins/NodeNote_jumpto.keystroke.alt_N");
		noteViewerComponent.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(keystroke), "jumpToMapAction");

		// Register action
		noteViewerComponent.getActionMap().put("jumpToMapAction",
				jumpToMapAction);

		if (shouldUseSplitPane()) {
			showNotesPanel();
		}
		mNotesManager = new NotesManager();
		controller.registerNodeSelectionListener(mNotesManager, false);
		controller.registerNodeLifetimeListener(mNotesManager, true);
		mNoteDocumentListener = new NoteDocumentListener();
	}

	public void deRegister() {
		controller.deregisterNodeSelectionListener(mNotesManager);
		controller.deregisterNodeLifetimeListener(mNotesManager);

		if (noteViewerComponent != null && shouldUseSplitPane()) {
			noteViewerComponent.getActionMap().remove("jumpToMapAction");
			hideNotesPanel();
			noteViewerComponent = null;
		}
		logger.fine("Deregistration of note undo handler.");
		controller.getActionFactory().deregisterActor(getDoActionClass());
	}

	public void showNotesPanel() {
		SouthPanel southPanel = new SouthPanel();
		southPanel.add(noteViewerComponent, BorderLayout.CENTER);
		noteViewerComponent.setVisible(true);
		if ("true".equals(controller.getFrame().getProperty(
				FreeMind.RESOURCES_USE_DEFAULT_FONT_FOR_NOTES_TOO))) {
			// set default font for notes:
			Font defaultFont = controller.getController().getDefaultFont();
			if (Resources.getInstance().getBoolProperty(
					"experimental_font_sizing_for_long_node_editors")) {
				/*
				 * This is a proposal of Dan, but it doesn't work as expected.
				 * 
				 * http://sourceforge.net/tracker/?func=detail&aid=2800933&group_id
				 * =7118&atid=107118
				 */
				defaultFont = Tools.updateFontSize(defaultFont,
						this.getMindMapController().getView().getZoom(),
						defaultFont.getSize());
			}
			String rule = "BODY {";
			rule += "font-family: " + defaultFont.getFamily() + ";";
			rule += "font-size: " + defaultFont.getSize() + "pt;";
			rule += "}\n";
			if ("true".equals(controller.getFrame().getProperty(
					FreeMind.RESOURCES_USE_MARGIN_TOP_ZERO_FOR_NOTES))) {
				/*
				 * this is used for paragraph spacing. I put it here, too, as
				 * the tooltip display uses the same spacing. But it is to be
				 * discussed. fc, 23.3.2009.
				 */
				rule += "p {";
				rule += "margin-top:0;";
				rule += "}\n";
			}
			getDocument().getStyleSheet().addRule(rule);
			// done setting default font.
		}
		noteViewerComponent.setOpenHyperlinkHandler(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				try {
					getMindMapController().getFrame().openDocument(
							new URL(pE.getActionCommand()));
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		});
		mSplitPane = controller.getFrame().insertComponentIntoSplitPane(
				southPanel);
		southPanel.revalidate();
	}

	public void hideNotesPanel() {
		// shut down the display:
		noteViewerComponent.setVisible(false);
		controller.getFrame().removeSplitPane();
		mSplitPane = null;
	}

	protected void setStateIcon(MindMapNode node, boolean enabled) {
		// icon
		if (noteIcon == null) {
			noteIcon = new ImageIcon(
					controller.getResource("images/knotes.png"));
		}
		boolean showIcon = enabled;
		if (Resources.getInstance().getBoolProperty(
				FreeMind.RESOURCES_DON_T_SHOW_NOTE_ICONS)) {
			showIcon = false;
		}
		node.setStateIcon(NodeNoteBase.NODE_NOTE_ICON, (showIcon) ? noteIcon
				: null);
		// tooltip, first try.
		if (!Resources.getInstance().getBoolProperty(
				FreeMind.RESOURCES_DON_T_SHOW_NOTE_TOOLTIPS)) {
			getMindMapController().setToolTip(node, "nodeNoteText",
					(enabled) ? node.getNoteText() : null);
		}
	}

	public void act(XmlAction action) {
		if (action instanceof EditNoteToNodeAction) {
			EditNoteToNodeAction noteTextAction = (EditNoteToNodeAction) action;
			MindMapNode node = controller.getNodeFromID(noteTextAction
					.getNode());
			String newText = noteTextAction.getText();
			String oldText = node.getNoteText();
			if (!Tools.safeEquals(newText, oldText)) {
				node.setNoteText(newText);
				// update display only, if the node is displayed.
				if (node == controller.getSelected()
						&& (!Tools.safeEquals(newText, getHtmlEditorPanel()
								.getDocumentText()))) {
					getHtmlEditorPanel().setCurrentDocumentContent(
							newText == null ? "" : newText);
				}
				setStateIcon(node, !(newText == null || newText.equals("")));
				controller.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return EditNoteToNodeAction.class;
	}

	/**
	 * Set text with undo:
	 * 
	 */
	public void changeNoteText(String text, MindMapNode node) {
		getMindMapController().setNoteText(node, text);
	}

	private MindMapController getMindMapController() {
		return controller;
	}

	protected SHTMLPanel getNoteViewerComponent() {
		return getHtmlEditorPanel();
	}

	public static SHTMLPanel getHtmlEditorPanel() {
		if (htmlEditorPanel == null) {
			SHTMLPanel.setResources(new SimplyHtmlResources());
			htmlEditorPanel = SHTMLPanel.createSHTMLPanel();
			htmlEditorPanel.setMinimumSize(new Dimension(100, 100));

	        boolean checkSpelling = Resources.getInstance().
	        		getBoolProperty(FreeMindCommon.CHECK_SPELLING);
			if (checkSpelling) {
				SpellChecker.register(htmlEditorPanel.getEditorPane());
			}
		}
		return htmlEditorPanel;
	}

	public JSplitPane getSplitPane() {
		return mSplitPane;
	}

	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		return getSplitPane() != null;
	}

	private String getDocumentText() {
		String documentText = noteViewerComponent.getDocumentText();
		// (?s) makes . matching newline as well.
		documentText = documentText.replaceFirst("(?s)<style.*?</style>", "");
		return documentText;
	}

	private String normalizeString(String input) {
		if (input == null)
			input = NodeNote.EMPTY_EDITOR_STRING;
		// return null;
		return input.replaceAll("\\s+", " ").replaceAll("  +", " ").trim();
	}

	protected HTMLDocument getDocument() {
		return noteViewerComponent.getDocument();
	}
}