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
/* $Id: NodeNote.java,v 1.1.4.7.2.25 2006-11-06 19:38:07 christianfoltin Exp $ */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.lightdev.app.shtm.SHTMLPanel;
import com.lightdev.app.shtm.Util;

import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * 
 */
public class NodeNote extends MindMapNodeHookAdapter {

    public final static String NODE_NOTE_PLUGIN = "accessories/plugins/NodeNote.properties";

    public final static String EMPTY_EDITOR_STRING = "<html>\n  <head>\n\n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

    public final static String EMPTY_EDITOR_STRING_ALTERNATIVE = "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

    // <html>\n <head>\n \n </head>\n <body>\n <p>\n \n </p>\n
    // </body>\n</html>\n
    public static class Registration implements HookRegistration, ActorXml, PropertyChangeListener {
        

		private final class NoteDocumentListener implements DocumentListener {
            public void changedUpdate(DocumentEvent arg0) {
                docEvent();
            }

            private void docEvent() {
                // make map dirty in order to enable automatic save on note
                // change.
                getMindMapController().getMap().setSaved(false);
            }

            public void insertUpdate(DocumentEvent arg0) {
                docEvent();
            }

            public void removeUpdate(DocumentEvent arg0) {
                docEvent();
            }
        }

        // private NodeTextListener listener;

        private final class NotesManager implements NodeSelectionListener,
                NodeLifetimeListener {

            private MindMapNode node;

            public NotesManager() {
            }

            public void onLooseFocusHook(MindMapNode node) {
                // logger.info("onLooseFocuse for node " + node.toString() + "
                // and noteViewerComponent=" + noteViewerComponent);
                noteViewerComponent.getDocument().removeDocumentListener(
                        mNoteDocumentListener);
                // store its content:
                onSaveNode(node);
                this.node = null;
                // getHtmlEditorPanel().setCurrentDocumentContent("Note", "");
            }

            public void onReceiveFocusHook(MindMapNode node) {
                this.node = node;
                // logger.info("onReceiveFocuse for node " + node.toString());
                String note = node.getNoteText();
                if (note != null) {
                    getHtmlEditorPanel().setCurrentDocumentContent(note);
                    mLastContentEmpty = false;
                } else if (!mLastContentEmpty) {
                    getHtmlEditorPanel().setCurrentDocumentContent("");
                    mLastContentEmpty = true;
                }
                noteViewerComponent.getDocument().addDocumentListener(
                        mNoteDocumentListener);
            }

            public void onUpdateNodeHook(MindMapNode node) {
            }

            public void onSaveNode(MindMapNode node) {
                if (this.node != node) {
                    return;
                }
                boolean editorContentEmpty = true;
                String documentText = getHtmlEditorPanel().getDocumentText();
                // editorContentEmpty =
                // HtmlTools.removeAllTagsFromString(documentText).matches("[\\s\\n]*");
                editorContentEmpty = documentText.equals(EMPTY_EDITOR_STRING)
                        || documentText.equals(EMPTY_EDITOR_STRING_ALTERNATIVE);
                // logger.info("Current document: '" +
                // documentText.replaceAll("\n", "\\\\n") + "', empty="+
                // editorContentEmpty);
                controller.deregisterNodeSelectionListener(this);
                if (getHtmlEditorPanel().needsSaving()) {
                    if (editorContentEmpty) {
                        changeNoteText(null, node);
                    } else {
                        changeNoteText(documentText, node);
                    }
                    mLastContentEmpty = editorContentEmpty;
                    setStateIcon(node, !editorContentEmpty);
                }
                controller.registerNodeSelectionListener(this);

            }

            public void onCreateNodeHook(MindMapNode node) {
                if (node.getXmlNoteText() != null) {
                    setStateIcon(node, true);
                }
            }

            public void onDeleteNodeHook(MindMapNode node) {
            }
        }

        private static SHTMLPanel htmlEditorPanel;

        /**
         * Indicates, whether or not the main panel has to be refreshed with new
         * content. The typical content will be empty, so this state is saved
         * here.
         */
        private static boolean mLastContentEmpty = true;

        private final MindMapController controller;

        protected SHTMLPanel noteViewerComponent;

        private final MindMap mMap;

        private final java.util.logging.Logger logger;

        private NotesManager mNotesManager;

        private static ImageIcon noteIcon = null;

        private NoteDocumentListener mNoteDocumentListener;

        public Registration(ModeController controller, MindMap map) {
            this.controller = (MindMapController) controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
        }

        public void register() {
            logger.info("Registration of note handler.");
            controller.getActionFactory().registerActor(this,
                    getDoActionClass());
            // moved to registration:
            noteViewerComponent = getNoteViewerComponent();
            // register "leave note" action:
            Action jumpToMapAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    controller.getView().getSelected().requestFocus();
                }
            };
            String keystroke = controller
                    .getFrame()
                    .getProperty(
                            "keystroke_accessories/plugins/NodeNote_jumpto.keystroke.alt_N");
            noteViewerComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke(keystroke), "jumpToMapAction");

            // Register action
            noteViewerComponent.getActionMap().put("jumpToMapAction",
                    jumpToMapAction);

            FreeMindMain frame = controller.getFrame();
            frame.getSouthPanel().add(noteViewerComponent, BorderLayout.CENTER);
            noteViewerComponent.setVisible(true);
            frame.getSouthPanel().revalidate();

            mNotesManager = new NotesManager();
            controller.registerNodeSelectionListener(mNotesManager);
            controller.registerNodeLifetimeListener(mNotesManager);
            mNoteDocumentListener = new NoteDocumentListener();
            // register listener for split pane changes:
            controller.getFrame().getSplitPane().addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        }

        public void deRegister() {
        		controller.getFrame().getSplitPane().removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
            controller.deregisterNodeSelectionListener(mNotesManager);
            controller.deregisterNodeLifetimeListener(mNotesManager);
            if (noteViewerComponent != null) {
                // shut down the display:
                noteViewerComponent.setVisible(false);
                JPanel southPanel = controller.getFrame().getSouthPanel();
                southPanel.remove(noteViewerComponent);
                southPanel.revalidate();
                noteViewerComponent = null;
            }
            logger.info("Deregistration of note undo handler.");
            controller.getActionFactory().deregisterActor(getDoActionClass());
        }

        private void setStateIcon(MindMapNode node, boolean enabled) {
            // icon
            if (noteIcon == null) {
                noteIcon = new ImageIcon(controller
                        .getResource("images/knotes.png"));
            }
            node.setStateIcon(this.getClass().getName(), (enabled) ? noteIcon
                    : null);
            // tooltip, first try.
            getMindMapController().setToolTip(node, "nodeNoteText", (enabled)?node.getNoteText():null);
            getMindMapController().nodeRefresh(node);
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
            String oldNoteText = node.getNoteText();
            if(Tools.safeEquals(text, oldNoteText)) {
                // they are equal.
                return;
            }
            EditNoteToNodeAction doAction = createEditNoteToNodeAction(node,
                    text);
            EditNoteToNodeAction undoAction = createEditNoteToNodeAction(node,
                    oldNoteText);
            getMindMapController().getActionFactory().startTransaction(
                    this.getClass().getName());
            getMindMapController().getActionFactory().executeAction(
                    new ActionPair(doAction, undoAction));
            getMindMapController().getActionFactory().endTransaction(
                    this.getClass().getName());
        }

        /**
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

        protected SHTMLPanel getNoteViewerComponent() {
            return getHtmlEditorPanel();
        }

        public static SHTMLPanel getHtmlEditorPanel() {
            if (htmlEditorPanel == null) {
                try {
                    ResourceBundle resources = ResourceBundle.getBundle(
                            "accessories.plugins.SimplyHTML", Locale
                                    .getDefault());
                    SHTMLPanel.setResources(resources);
                } catch (MissingResourceException mre) {
                    Util.errMsg(null,
                            "resources/SimplyHTML.properties not found", mre);
                }
                htmlEditorPanel = SHTMLPanel.createSHTMLPanel();
            }
            return htmlEditorPanel;
        }

        private static int mLastNonMaxSplitPaneDividerPosition = -1;
        
		public void propertyChange(PropertyChangeEvent pChangeEvent) {
			JSplitPane splitPane = controller.getFrame().getSplitPane();
			int oldValue = ((Integer) pChangeEvent
								.getOldValue()).intValue();
			int newValue = ((Integer) pChangeEvent
					.getNewValue()).intValue();
			// now, lets see, whether to new location is max. otherwise, store it.
			if (splitPane.getMaximumDividerLocation() != newValue) {
				mLastNonMaxSplitPaneDividerPosition = newValue;
			}

		}
    }

    public void startupMapHook() {
        super.startupMapHook();
//        String foldingType = getResourceString("direction");
//        if (foldingType.equals("note")) {
            // jump to the notes:
		JSplitPane splitPane = getController().getFrame().getSplitPane();
		logger.info("Setting divider location?");
		if(true || splitPane.isMinimumSizeSet()) {
			int newSize = Registration.mLastNonMaxSplitPaneDividerPosition;
			if(newSize < 0) {
				newSize = splitPane.getMaximumDividerLocation() / 2;
			}
			logger.info("Setting divider location to :" + newSize);
			splitPane.setDividerLocation(newSize);
		}
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            EventQueue.invokeLater(new Runnable(){
                public void run() {
                    Registration.getHtmlEditorPanel()
                    .requestFocus();
                }                
            });
//        } else {
//            // jump back from notes:
////            getController().getView().requestFocusInWindow();
//
//        }
    }

}
