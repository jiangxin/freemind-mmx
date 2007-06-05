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
/* $Id: NodeNote.java,v 1.1.4.7.2.34 2007-06-05 21:01:24 dpolivaev Exp $ */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
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
import javax.swing.text.html.HTMLDocument;

import com.lightdev.app.shtm.DefaultTextResources;
import com.lightdev.app.shtm.SHTMLPanel;
import com.lightdev.app.shtm.TextResources;
import com.lightdev.app.shtm.Util;

import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
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
import freemind.view.mindmapview.NodeView;

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
    public static class Registration implements HookRegistration, ActorXml {
        

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

            public void onDeselectHook(NodeView node) {
                // logger.info("onLooseFocuse for node " + node.toString() + "
                // and noteViewerComponent=" + noteViewerComponent);
                noteViewerComponent.getDocument().removeDocumentListener(
                        mNoteDocumentListener);
                // store its content:
                onSaveNode(node.getModel());
                this.node = null;
                // getHtmlEditorPanel().setCurrentDocumentContent("Note", "");
            }

            public void onSelectHook(NodeView nodeView) {
                this.node = nodeView.getModel();
                HTMLDocument document = noteViewerComponent.getDocument();
                // remove listener to avoid unnecessary dirty events.
                document.removeDocumentListener(
                        mNoteDocumentListener);
                // logger.info("onReceiveFocuse for node " + node.toString());
                String note = node.getNoteText();
                if (note != null) {
                    noteViewerComponent.setCurrentDocumentContent(note);
                    mLastContentEmpty = false;
                } else if (!mLastContentEmpty) {
                    noteViewerComponent.setCurrentDocumentContent("");
                    mLastContentEmpty = true;
                }
                document.addDocumentListener(
                        mNoteDocumentListener);
            }

            public void onUpdateNodeHook(MindMapNode node) {
            }

            public void onSaveNode(MindMapNode node) {
                if (this.node != node) {
                    return;
                }
                boolean editorContentEmpty = true;
                String documentText = noteViewerComponent.getDocumentText();
                // editorContentEmpty =
                // HtmlTools.removeAllTagsFromString(documentText).matches("[\\s\\n]*");
                editorContentEmpty = documentText.equals(EMPTY_EDITOR_STRING)
                        || documentText.equals(EMPTY_EDITOR_STRING_ALTERNATIVE);
                // logger.info("Current document: '" +
                // documentText.replaceAll("\n", "\\\\n") + "', empty="+
                // editorContentEmpty);
                controller.deregisterNodeSelectionListener(this);
                if (noteViewerComponent.needsSaving()) {
                    if (editorContentEmpty) {
                        changeNoteText(null, node);
                    } else {
                        changeNoteText(documentText, node);
                    }
                    mLastContentEmpty = editorContentEmpty;
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

        private static Integer sPositionToRecover = null;
        
        public Registration(ModeController controller, MindMap map) {
            this.controller = (MindMapController) controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
        }

        class JumpToMapAction extends AbstractAction{
            public void actionPerformed(ActionEvent e) {
                JSplitPane splitPane = controller.getFrame().getSplitPane();
                if (sPositionToRecover != null) {
                    splitPane.setDividerLocation(sPositionToRecover
                            .intValue());
                    sPositionToRecover = null;
                }
                controller.getView().getSelected().requestFocus();
            }
        };
        public void register() {
            logger.fine("Registration of note handler.");
            controller.getActionFactory().registerActor(this,
                    getDoActionClass());
            // moved to registration:
            noteViewerComponent = getNoteViewerComponent();
            // register "leave note" action:
            Action jumpToMapAction = new JumpToMapAction();
            String keystroke = controller
                    .getFrame()
                    .getAdjustableProperty(
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
        }

        public void deRegister() {
            controller.deregisterNodeSelectionListener(mNotesManager);
            controller.deregisterNodeLifetimeListener(mNotesManager);
            noteViewerComponent.getActionMap().remove("jumpToMapAction");

            if (noteViewerComponent != null) {
                // shut down the display:
                noteViewerComponent.setVisible(false);
                JPanel southPanel = controller.getFrame().getSouthPanel();
                southPanel.remove(noteViewerComponent);
                southPanel.revalidate();
                noteViewerComponent = null;
            }
            logger.fine("Deregistration of note undo handler.");
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
                    setStateIcon(node, ! (newText == null || newText.equals("")));
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
                final TextResources resources = new DefaultTextResources(Resources.getInstance().getResources(), Resources.getInstance().getProperties());
                SHTMLPanel.setResources(new TextResources(){
                    public String getString(String pKey) {
                        return resources.getString("simplyhtml." + pKey);
                    }                        
                });
                htmlEditorPanel = SHTMLPanel.createSHTMLPanel();
            }
            return htmlEditorPanel;
        }

    }

    public void startupMapHook() {
        super.startupMapHook();
        JSplitPane splitPane = getController().getFrame().getSplitPane();
        int maximumDividerLocation = splitPane.getMaximumDividerLocation();
        String foldingType = getResourceString("command");
        if (foldingType.equals("jump")) {
            // jump to the notes:
            int oldSize = splitPane.getDividerLocation();
            if (maximumDividerLocation < oldSize) {
                openSplitPane(splitPane, maximumDividerLocation);
                Registration.sPositionToRecover = new Integer(oldSize);
            } else {
                Registration.sPositionToRecover = null;
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            EventQueue.invokeLater(new Runnable(){
                public void run() {
                    Registration.getHtmlEditorPanel()
                    .requestFocus();
                }                
            });
        } else {
            // show hide window:
            if(splitPane.getDividerLocation() > maximumDividerLocation) {
                // the window is currently hidden. show it:
                openSplitPane(splitPane, maximumDividerLocation);
            } else {
                // it is shown, hide it:
                splitPane.setDividerLocation(1.0);
            }

        }
    }

    private void openSplitPane(JSplitPane splitPane, int maximumDividerLocation) {
        int newSize = splitPane.getLastDividerLocation();
        if(newSize > maximumDividerLocation) {
            newSize = maximumDividerLocation;
        }
        splitPane.setDividerLocation(newSize);
    }

}
