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
/* $Id: NodeNote.java,v 1.1.4.7.2.14 2006-09-16 21:38:22 dpolivaev Exp $ */
package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.lightdev.app.shtm.SHTMLPanel;
import com.lightdev.app.shtm.Util;

import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeLifetimeListener;
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
    public final static String EMPTY_EDITOR_STRING = "<html>\n  <head>\n\n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";
    public final static String EMPTY_EDITOR_STRING_ALTERNATIVE = "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";
                                                                //<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n
    public static class Registration implements HookRegistration, ActorXml {
        // private NodeTextListener listener;
        static private boolean mLastContentEmpty = true;

        private final class NotesManager implements NodeSelectionListener, NodeLifetimeListener  {

            private MindMapNode node;
            
            public NotesManager() {
            }

            public void onLooseFocusHook(MindMapNode node) {
                // store its content:
                onSaveNode(node);
                this.node = null;
//                getHtmlEditorPanel().setCurrentDocumentContent("Note", "");
            }

            public void onReceiveFocusHook(MindMapNode node) {
                this.node = node;
                String note = node.getXmlNoteText();
                if (note != null) {
                    getHtmlEditorPanel().setCurrentDocumentContent("Note", note);
                } else if(!mLastContentEmpty){
                    getHtmlEditorPanel().setCurrentDocumentContent("Note", "");
                }
            }

            public void onUpdateNodeHook(MindMapNode node) {
            }

            public void onSaveNode(MindMapNode node) {
                if(this.node != node){
                    return;
                }
                boolean editorContentEmpty = true;
                String documentText = getHtmlEditorPanel().getDocumentText();
//				editorContentEmpty = HtmlTools.removeAllTagsFromString(documentText).matches("[\\s\\n]*");
				editorContentEmpty = documentText.equals(EMPTY_EDITOR_STRING)||
					documentText.equals(EMPTY_EDITOR_STRING_ALTERNATIVE);
//				logger.info("Current document: '" + documentText.replaceAll("\n", "\\\\n") + "', empty="+ editorContentEmpty);
                controller.deregisterNodeSelectionListener(this);
                if (getHtmlEditorPanel().needsSaving()) {
                    if (editorContentEmpty) {
                        changeNoteText(null, node);
                    } else {
                        changeNoteText(documentText,
                                node);
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

        static private SHTMLPanel htmlEditorPanel;

        private final MindMapController controller;

        protected Container noteViewerComponent;

        private final MindMap mMap;

        private final java.util.logging.Logger logger;

        private NotesManager mNotesManager;
        
        private static ImageIcon noteIcon = null;

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
            FreeMindMain frame = controller.getFrame();
            frame.getSouthPanel().add(noteViewerComponent, BorderLayout.CENTER);
            noteViewerComponent.setVisible(true);
            frame.getSouthPanel().revalidate();

            mNotesManager = new NotesManager();
            controller.registerNodeSelectionListener(mNotesManager);
            controller.registerNodeLifetimeListener(mNotesManager);
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
        
        private void setStateIcon(MindMapNode node, boolean enabled) {
            // icon
            if (noteIcon == null) {
                noteIcon  = new ImageIcon(controller.getResource("images/knotes.png"));
            }
            node.setStateIcon(this.getClass().getName(), (enabled)?noteIcon:null);
            getMindMapController().nodeRefresh(node);
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
                        getHtmlEditorPanel().setCurrentDocumentContent("Note",
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
             return getHtmlEditorPanel();
        }

        public SHTMLPanel getHtmlEditorPanel() {
            if (htmlEditorPanel == null) {
                try {
                    ResourceBundle resources = ResourceBundle.getBundle(
                            "accessories.plugins.SimplyHTML", Locale.getDefault());
                    SHTMLPanel.setResources(resources);
                }
                catch(MissingResourceException mre) {
                    Util.errMsg(null, "resources/SimplyHTML.properties not found", mre);
                }
                htmlEditorPanel = SHTMLPanel.createSHTMLPanel();
            }
            return htmlEditorPanel;
        }
    }

    protected void nodeRefresh(MindMapNode node) {
    }

    protected void receiveFocusAddons() {
    }

    protected void looseFocusAddons() {
    }

}
