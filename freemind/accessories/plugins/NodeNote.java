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
/* $Id: NodeNote.java,v 1.1.4.7.2.1 2006-04-05 21:26:24 dpolivaev Exp $ */
package accessories.plugins;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookFactory;
import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.common.plugins.NodeNoteBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

/**
 * @author foltin
 *
 */
public class NodeNote extends NodeNoteBase {

    public static class Registration implements HookRegistration, ActorXml {

        private final MindMapController controller;

        private final MindMap mMap;

        private final java.util.logging.Logger logger;

        public Registration(ModeController controller, MindMap map) {
            this.controller = (MindMapController) controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
        }

        public void register() {
            logger.info("Registration of note undo handler.");
            controller.getActionFactory().registerActor(this,
                    getDoActionClass());
        }

        public void deRegister() {
            logger.info("Deregistration of note undo handler.");
            controller.getActionFactory().deregisterActor(getDoActionClass());
        }

        public void act(XmlAction action) {
            if (action instanceof EditNoteToNodeAction) {
                EditNoteToNodeAction noteTextAction = (EditNoteToNodeAction) action;
                MindMapNode node = controller.getNodeFromID(noteTextAction
                        .getNode());
                String newText = noteTextAction.getText();
                // check if plugin present for that node:
                HookFactory factory = controller.getHookFactory();
                NodeNote hook = (NodeNote) factory.getHookInNode(node,
                        NodeNoteBase.HOOK_NAME);
                if (hook == null) {
                    // create hook
                    throw new IllegalArgumentException("Not implemented yet");
                }
                // hook is present, get text:
                String oldText = hook.getMyNodeText();
                if (!Tools.safeEquals(newText, oldText)) {
                    hook.setMyNodeText(newText);
                    // FIXME: This is ugly code as we are fishing in the waters
                    // of NodeNote.
                    if (hook.text != null) {
                        // check if document is different:
                        try {
                            if (!newText.equals(getDocumentText(hook.text
                                    .getDocument()))) {
                                hook.text.setText(newText);
                            }
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                    controller.nodeChanged(node);
                }
            }
        }

        public Class getDoActionClass() {
            return EditNoteToNodeAction.class;
        }

    }

    private NodeTextListener listener;

    /*
     * (non-Javadoc)
     *
     * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
     */
    public void save(XMLElement xml) {
        super.save(xml);
        XMLElement child = new XMLElement();
        child.setName("text");
        child.setContent(getMyNodeText());
        xml.addChild(child);
    }

    public class NodeTextListener implements DocumentListener {
        private NodeNote pNote;

        public NodeTextListener() {
            pNote = null;
        }

        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(DocumentEvent)
         */
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(DocumentEvent)
         */
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(DocumentEvent)
         */
        public void changedUpdate(DocumentEvent e) {
            try {
                if (pNote != null) {
                    Document document = e.getDocument();
                    String text = getDocumentText(document);
                    pNote.changeNodeText(text);
                }
            } catch (BadLocationException ex) {
                System.err.println("Could not fetch nodeText content"
                        + ex.toString());
            }
        }

        /**
         * @param note
         */
        public void setNote(NodeNote note) {
            pNote = note;
        }

    }

    /**
     * @param document
     * @return
     * @throws BadLocationException
     */
    private static String getDocumentText(Document document)
            throws BadLocationException {
        return document.getText(0, document.getLength());
    }

    protected void nodeRefresh(MindMapNode node) {
        getMindMapController().nodeRefresh(node);
    }

    /**
     * Set text with undo:
     *
     * @param text
     */
    public void changeNodeText(String text) {
            EditNoteToNodeAction doAction = createEditNoteToNodeAction(
                    getNode(), text);
            EditNoteToNodeAction undoAction = createEditNoteToNodeAction(
                    getNode(), getMyNodeText());
            getMindMapController().getActionFactory().startTransaction(
                    this.getClass().getName());
            getMindMapController().getActionFactory().executeAction(
                    new ActionPair(doAction, undoAction));
            getMindMapController().getActionFactory().endTransaction(
                    this.getClass().getName());
    }

    public EditNoteToNodeAction createEditNoteToNodeAction(MindMapNode node,
            String text)  {
        EditNoteToNodeAction nodeAction = new EditNoteToNodeAction();
        nodeAction.setNode(node.getObjectId(getController()));
        nodeAction.setText(text);
        return nodeAction;
    }

    /**
     * @return
     */
    private MindMapController getMindMapController() {
        return ((MindMapController) getController());
    }

    protected void receiveFocusAddons() {
        listener = new NodeTextListener();
        listener.setNote(this);
        text.getDocument().addDocumentListener(listener);
    }

    protected void looseFocusAddons() {
        listener.setNote(null);

    }

}
