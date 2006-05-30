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
/* $Id: NodeNote.java,v 1.1.4.7.2.5 2006-05-30 21:36:17 christianfoltin Exp $ */
package accessories.plugins;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuBar;
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
import freemind.extensions.HookFactory;
import freemind.extensions.HookRegistration;
import freemind.main.Resources;
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

    private class KafenioPane extends Box implements KafenioContainerInterface{
        public KafenioPane() {
            super(BoxLayout.Y_AXIS ); 
            setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        }

        public void detachFrame() {
        }

        public void setJMenuBar(JMenuBar newMenuBar) {
            newMenuBar.setAlignmentX(LEFT_ALIGNMENT);
            add(newMenuBar, 0);
        }

        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
            if( ks.getKeyCode() == KeyEvent.VK_SPACE
                    && e.getModifiers() == 0
                    && pressed){
                return true;            
            }
            return super.processKeyBinding(ks, e, condition, pressed);
        }
        
    }
    static private NodeTextListener listener;
    static private KafenioPanelConfiguration kafenioPanelConfiguration;
    static private KafenioPanel htmlEditorPanel;

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
                    if (htmlEditorPanel != null) {
                        if (!newText.equals(htmlEditorPanel
                                .getDocumentText())) {
                            htmlEditorPanel.setDocumentText(newText);
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
            if (pNote != null) {
                String text = htmlEditorPanel.getDocumentText();
                pNote.changeNodeText(text);
            }
        }

        /**
         * @param note
         */
        public void setNote(NodeNote note) {
            pNote = note;
        }

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
        if(listener == null){
            listener = new NodeTextListener();
        }
        listener.setNote(this);
        htmlEditorPanel.getExtendedHtmlDoc().addDocumentListener(listener);
    }

    protected void looseFocusAddons() {
        htmlEditorPanel.getExtendedHtmlDoc().removeDocumentListener(listener);
        listener.setNote(null);
    }

    protected Container getNoteViewerComponent() throws Exception {
        createKafenioPanel();
        logger.fine("Text ctrl. set for node "+getNode()+" as "+getMyNodeText());
        // panel:
        
        htmlEditorPanel.setDocumentText(getMyNodeText());
        htmlEditorPanel.setDocumentConfirmed(false);
        return htmlEditorPanel.getKafenioParent();
    }
    private void createKafenioPanel() throws Exception {
        if(htmlEditorPanel == null){
            final SplashScreen splashScreen = new SplashScreen();
            splashScreen.setVisible(true);
            final JRootPane rootPane = splashScreen.getRootPane();
            rootPane.paintImmediately(0, 0, rootPane.getWidth(), rootPane.getHeight());
            createKafenioConfiguration();
            htmlEditorPanel  = new KafenioPanel(kafenioPanelConfiguration);
            htmlEditorPanel.getJToolBar1().setRollover(true);
            //htmlEditorPanel.getJToolBar2().setRollover(true);          
            htmlEditorPanel.getHTMLScrollPane().setPreferredSize(new Dimension(1, 200));
            htmlEditorPanel.getSrcScrollPane().setPreferredSize(new Dimension(1, 200));
            htmlEditorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            htmlEditorPanel.getKafenioParent().add(htmlEditorPanel);
            splashScreen.setVisible(false);
        }
    }

    private void createKafenioConfiguration() {
        if(kafenioPanelConfiguration  == null){
            String language = Resources.getInstance().getProperty("language");
            HashMap countryMap = Resources.getInstance().getCountryMap(); 
            kafenioPanelConfiguration = new KafenioPanelConfiguration();
            kafenioPanelConfiguration.setImageDir("file://");
            kafenioPanelConfiguration.setDebugMode(true); 
            //kafenioPanelConfiguration.setLanguage("sk");
            //kafenioPanelConfiguration.setCountry("SK");
            kafenioPanelConfiguration.setLanguage(language);
            kafenioPanelConfiguration.setCountry((String)countryMap.get(language));
            kafenioPanelConfiguration.setCustomMenuItems("edit" 
                    // + " view"
                    +" font format insert table forms search tools help");
            // In the following excluded: new, open, styleselect
            kafenioPanelConfiguration.setCustomToolBar1(
                    "cut copy paste bold italic underline" 
                    + " left center right justify ulist olist deindent indent anchor" 
                    +" image clearformats strike superscript subscript insertcharacter"
                    + " find color table"
                    // + " viewsource"
                    );
            // All available tool bar items:
            // new open save cut copy paste bold italic underline left center right justify styleselect ulist olist deindent indent anchor
            // image clearformats viewsource strike superscript subscript insertcharacter find color table
            
            kafenioPanelConfiguration.setShowToolbar2(false);
            kafenioPanelConfiguration.setProperty("escapeCloses","false");
            kafenioPanelConfiguration.setProperty("confirmRatherThanPost","true");
            //kafenioPanelConfiguration.setProperty("alternativeLanguage","en");
            //kafenioPanelConfiguration.setProperty("alternativeCountry","US");
            kafenioPanelConfiguration.setKafenioParent(new KafenioPane());
        }
    }
    
}
