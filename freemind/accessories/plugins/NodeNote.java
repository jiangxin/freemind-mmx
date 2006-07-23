/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Christian Foltin and others
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
/* $Id: NodeNote.java,v 1.1.4.7.2.7 2006-07-23 03:29:02 christianfoltin Exp $ */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.KafenioPanelConfiguration;
import de.xeinfach.kafenio.SplashScreen;
import de.xeinfach.kafenio.interfaces.KafenioContainerInterface;
import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * @author foltin
 * 
 */
public class NodeNote extends NodeNoteBase {

	public final static String NODE_NOTE_PLUGIN = "accessories/plugins/NodeNote.properties";

	private static class KafenioPane extends Box implements
			KafenioContainerInterface {
		public KafenioPane() {
			super(BoxLayout.Y_AXIS);
			setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		}

		public void detachFrame() {
		}

		public void setJMenuBar(JMenuBar newMenuBar) {
			newMenuBar.setAlignmentX(LEFT_ALIGNMENT);
			add(newMenuBar, 0);
		}

		protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
				int condition, boolean pressed) {
			if (ks.getKeyCode() == KeyEvent.VK_SPACE && e.getModifiers() == 0
					&& pressed) {
				return true;
			}
			return super.processKeyBinding(ks, e, condition, pressed);
		}

	}

	public static class Registration implements HookRegistration, ActorXml {
		// private NodeTextListener listener;

		private final class NotesManager implements NodeSelectionListener {
			private NoteTextListener listener = null;

			public NotesManager() {
				listener = new NoteTextListener();
				getHtmlEditorPanel().getExtendedHtmlDoc().addDocumentListener(
						listener);
			}

			public void onLooseFocusHook(MindMapNode node) {
				// store its content:
				onSaveNode(node);
				getHtmlEditorPanel().setDocumentText("");
				getHtmlEditorPanel().purgeUndos();
			}

			public void onReceiveFocusHook(MindMapNode node) {
				String note = node.getXmlNoteText();
				if (note != null) {
					getHtmlEditorPanel().setDocumentText(note);
				} else {
					getHtmlEditorPanel().setDocumentText("");
				}
				listener.clearDirtyFlag();
			}

			public void onUpdateNodeHook(MindMapNode node) {
			}

			public void onSaveNode(MindMapNode node) {
				boolean editorContentEmpty = getHtmlEditorPanel()
				.getDocumentBody().matches("[\\s\\n]*");
				controller.deregisterNodeSelectionListener(this);
				if (listener.isDirty()) {
					if (editorContentEmpty) {
						changeNodeText(null, node);
					} else {
						changeNodeText(getHtmlEditorPanel().getDocumentText(),
								node);
					}
				}
				controller.registerNodeSelectionListener(this);
				
			}
		}

		private KafenioPanelConfiguration kafenioPanelConfiguration;

		private KafenioPanel htmlEditorPanel;

		private final MindMapController controller;

		protected Container noteViewerComponent;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		private NotesManager mNotesManager;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			logger.info("Registration of note undo handler.");
			controller.getActionFactory().registerActor(this,
					getDoActionClass());
			// moved to registration:
			noteViewerComponent = getNoteViewerComponent();
			FreeMindMain frame = controller.getFrame();
			frame.getSouthPanel().add(noteViewerComponent, BorderLayout.CENTER);
			noteViewerComponent.setVisible(true);
			frame.getSouthPanel().revalidate();

			mNotesManager = new NotesManager();
			controller.registerNodeSelectionListener(mNotesManager);
		}

		public void deRegister() {
			logger.info("Deregistration of note undo handler.");
			controller.getActionFactory().deregisterActor(getDoActionClass());
			if (noteViewerComponent != null) {
				// shut down the display:
				noteViewerComponent.setVisible(false);
				JPanel southPanel = controller.getFrame().getSouthPanel();
				southPanel.remove(noteViewerComponent);
				southPanel.revalidate();
				noteViewerComponent = null;
			}

		}

		public void act(XmlAction action) {
			if (action instanceof EditNoteToNodeAction) {
				EditNoteToNodeAction noteTextAction = (EditNoteToNodeAction) action;
				MindMapNode node = controller.getNodeFromID(noteTextAction
						.getNode());
				String newText = noteTextAction.getText();
				String oldText = node.getXmlNoteText();
				if (!Tools.safeEquals(newText, oldText)) {
					node.setXmlNoteText(newText);
					// update display only, if the node is displayed.
					if (node == controller.getSelected()
							&& (!Tools.safeEquals(newText, getHtmlEditorPanel()
									.getDocumentText()))) {
						getHtmlEditorPanel().setDocumentText(
								newText == null ? "" : newText);
					}
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
		 * @param text
		 */
		public void changeNodeText(String text, MindMapNode node) {
			EditNoteToNodeAction doAction = createEditNoteToNodeAction(node,
					text);
			EditNoteToNodeAction undoAction = createEditNoteToNodeAction(node,
					node.getXmlNoteText());
			getMindMapController().getActionFactory().startTransaction(
					this.getClass().getName());
			getMindMapController().getActionFactory().executeAction(
					new ActionPair(doAction, undoAction));
			getMindMapController().getActionFactory().endTransaction(
					this.getClass().getName());
		}

		/**
		 * @return
		 */
		private MindMapController getMindMapController() {
			return controller;
		}

		public EditNoteToNodeAction createEditNoteToNodeAction(
				MindMapNode node, String text) {
			EditNoteToNodeAction nodeAction = new EditNoteToNodeAction();
			nodeAction.setNode(node.getObjectId(controller));
			nodeAction.setText(text);
			return nodeAction;
		}

		protected Container getNoteViewerComponent() {
			// panel:
			createKafenioPanel();
			return htmlEditorPanel.getKafenioParent();
		}

		private void createKafenioPanel() {
			if (htmlEditorPanel == null) {
				final SplashScreen splashScreen = new SplashScreen();
				splashScreen.setVisible(true);
				final JRootPane rootPane = splashScreen.getRootPane();
				rootPane.paintImmediately(0, 0, rootPane.getWidth(), rootPane
						.getHeight());
				createKafenioConfiguration();
				htmlEditorPanel = new KafenioPanel(kafenioPanelConfiguration);
				htmlEditorPanel.getJToolBar1().setRollover(true);
				// htmlEditorPanel.getJToolBar2().setRollover(true);
				htmlEditorPanel.getHTMLScrollPane().setPreferredSize(
						new Dimension(1, 200));
				htmlEditorPanel.getSrcScrollPane().setPreferredSize(
						new Dimension(1, 200));
				htmlEditorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

				htmlEditorPanel.getKafenioParent().add(htmlEditorPanel);
				splashScreen.setVisible(false);
			}
		}

		private void createKafenioConfiguration() {
			if (kafenioPanelConfiguration == null) {
				String language = Resources.getInstance().getProperty(
						"language");
				HashMap countryMap = Resources.getInstance().getCountryMap();
				kafenioPanelConfiguration = new KafenioPanelConfiguration();
				kafenioPanelConfiguration.setImageDir("file://");
				kafenioPanelConfiguration.setDebugMode(true);
				// kafenioPanelConfiguration.setLanguage("sk");
				// kafenioPanelConfiguration.setCountry("SK");
				kafenioPanelConfiguration.setLanguage(language);
				kafenioPanelConfiguration.setCountry((String) countryMap
						.get(language));
				kafenioPanelConfiguration.setCustomMenuItems("edit" + " view"
						+ " font format insert table search tools help");
				// In the following excluded: new, open, styleselect
				kafenioPanelConfiguration
						.setCustomToolBar1("cut copy paste bold italic underline"
								+ " left center right justify ulist olist deindent indent anchor"
								+ " image clearformats strike superscript subscript insertcharacter"
								+ " find color table"
						// + " viewsource"
						);
				// All available tool bar items:
				// new open save cut copy paste bold italic underline left
				// center right justify styleselect ulist olist deindent indent
				// anchor
				// image clearformats viewsource strike superscript subscript
				// insertcharacter find color table

				kafenioPanelConfiguration.setShowToolbar2(false);
				kafenioPanelConfiguration.setProperty("escapeCloses", "false");
				// kafenioPanelConfiguration.setProperty("confirmRatherThanPost",
				// "true");
				// kafenioPanelConfiguration.setProperty("alternativeLanguage","en");
				// kafenioPanelConfiguration.setProperty("alternativeCountry","US");
				kafenioPanelConfiguration.setKafenioParent(new KafenioPane());
			}
		}

		public KafenioPanel getHtmlEditorPanel() {
			return htmlEditorPanel;
		}

	}

	public static class NoteTextListener implements DocumentListener {
		boolean dirty = false;

		public void changedUpdate(DocumentEvent arg0) {
			dirty = true;
		}

		public boolean isDirty() {
			return dirty;
		}

		public void clearDirtyFlag() {
			dirty = false;
		}

		public void insertUpdate(DocumentEvent arg0) {
			dirty = true;
		}

		public void removeUpdate(DocumentEvent arg0) {
			dirty = true;
		}
	}

	protected void nodeRefresh(MindMapNode node) {
	}

	protected void receiveFocusAddons() {
	}

	protected void looseFocusAddons() {
	}

}
