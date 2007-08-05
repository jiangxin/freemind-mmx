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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;

import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
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
import freemind.view.mindmapview.NodeView;


//
//Node editing
//

public class EditAction extends AbstractAction implements ActorXml {
    private final MindMapController c;
    private EditNodeBase mCurrentEditDialog = null;
    public EditAction(MindMapController modeController) {
        super(modeController.getText("edit_node"));
        this.c = modeController;
        this.c.getActionFactory().registerActor(this, getDoActionClass());
    }
    public void actionPerformed(ActionEvent arg0) {
        MindMapNode selected = this.c.getSelected();
        this.c.edit(null, false, false);
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
        EditNodeAction editAction = (EditNodeAction) action;
        NodeAdapter node = this.c.getNodeFromID(editAction.getNode());
        if(!node.toString().equals(editAction.getText())) {
            node.setUserObject(editAction.getText());
            this.c.nodeChanged(node);
        }
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return EditNodeAction.class;
    }
    
    // edit begins with home/end or typing (PN 6.2)
    public void edit(KeyEvent e, boolean addNew, boolean editLong) {
        if (c.getView().getSelected() != null) {
            if (e == null || !addNew) {
                edit(c.getView().getSelected(),c.getView().getSelected(), e, false, false, editLong);
            }
            else if (!c.isBlocked()) {
                c.addNew(c.getSelected(), MindMapController.NEW_SIBLING_BEHIND, e);
            }
            if (e != null) {
                e.consume();
            }
        }
    }
    
    
    /**
     * @param prevSelected when new->esc: node be selected
     * @param isNewNode when new->esc: cut the node
     * @param parentFolded when new->esc: fold prevSelected
     */
    public void editLater(
            final NodeView node,
            final NodeView prevSelected,
            final KeyEvent firstEvent,
            final boolean isNewNode,
            final boolean parentFolded,
            final boolean editLong) {
        class DelayedEditor implements Runnable {
            
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            final NodeView node;
            final NodeView prevSelected;
            final KeyEvent firstEvent;
            final boolean isNewNode;
            final boolean parentFolded;
            final boolean editLong;
            DelayedEditor(
                    final NodeView node,
                    final NodeView prevSelected,
                    final KeyEvent firstEvent,
                    final boolean isNewNode,
                    final boolean parentFolded,
                    final boolean editLong){
                this.node = node;
                this.prevSelected = prevSelected;
                this.firstEvent = firstEvent;
                this.isNewNode = isNewNode;
                this.parentFolded = parentFolded;
                this.editLong = editLong;
            }
            public void run() {
                edit(node, prevSelected, firstEvent, isNewNode, parentFolded, editLong);				
            }
        };
        EventQueue.invokeLater(new DelayedEditor(node, prevSelected, firstEvent, isNewNode, parentFolded, editLong));
    }
    
    public void edit(
            final NodeView node,
            final NodeView prevSelected,
            final KeyEvent firstEvent,
            final boolean isNewNode,
            final boolean parentFolded,
            final boolean editLong) {
        if (node == null) {
            return;
        }
        if(! node.focused()){
            node.requestFocus();
            editLater(node, prevSelected, firstEvent, isNewNode, parentFolded, editLong);            
            return;
        }
        if(mCurrentEditDialog != null) {
        		// there was presvious editing.
        		mCurrentEditDialog.closeEdit();
        		mCurrentEditDialog = null;
        }
        //EditNodeBase.closeEdit();
        c.setBlocked(true); // locally "modal" stated
        
        String text = node.getModel().toString();
        String htmlEditingOption = c.getController().getProperty("html_editing_option");
        String useRichTextInNewLongNodes = c.getController().getProperty("use_rich_text_in_new_long_nodes");
        
        if ((node.getIsLong() || editLong) && 
                Tools.safeEquals(useRichTextInNewLongNodes,"true") &&
                !text.startsWith("<html>")) {
            text = HtmlTools.plainToHTML(text); }
        
        if (text.startsWith("<html>") && Tools.safeEquals(htmlEditingOption,"internal-wysiwyg")) {
            EditNodeWYSIWYG editNodeWYSIWYG = new EditNodeWYSIWYG
            (node, text, firstEvent, c,
                    new EditNodeBase.EditControl() {
                public void cancel() {
                    c.setBlocked(false);                                        
                    mCurrentEditDialog = null;
                }
                public void ok(String newText) {
                    setNodeText(node.getModel(), newText); 
                    cancel();                    
                    }
                public void split(String newText, int position) {
                    c.splitNode(node.getModel(), position, newText);
                    c.getController().obtainFocusForSelected(); 
                    cancel();                    
                    }}); // focus fix 
            mCurrentEditDialog = editNodeWYSIWYG;
            editNodeWYSIWYG.show();
            return; 
        }
        
        if (text.startsWith("<html>") && Tools.safeEquals(htmlEditingOption,"external")) {
            EditNodeExternalApplication editNodeExternalApplication = new EditNodeExternalApplication
            (node, text, firstEvent, c,
                    new EditNodeBase.EditControl() {
                public void cancel() {
                    c.setBlocked(false);                    
                    mCurrentEditDialog = null;
                }
                public void ok(String newText) {
                    setNodeText(node.getModel(), newText);
                    cancel();                    
                }
                public void split(String newText, int position) {
                    c.splitNode(node.getModel(), position, newText);
                    c.getController().obtainFocusForSelected(); 
                    cancel();                    
                    }}); // focus fix 
            mCurrentEditDialog = editNodeExternalApplication;
            editNodeExternalApplication.show();
            // We come here before quitting the editor window.
            return; }
        
        if (node.getIsLong() || editLong) {
            EditNodeDialog nodeEditDialog =
                new EditNodeDialog(
                        node,
                        text,
                        firstEvent,
                        c,
                        new EditNodeBase.EditControl() {
                            
                            public void cancel() {
                                c.setBlocked(false);
                                mCurrentEditDialog = null;
                            }
                            
                            public void ok(String newText) {
                                setNodeText(node.getModel(), newText);
                                cancel();
                            }
                            
                            public void split(String newText, int position) {
                                c.splitNode(node.getModel(), position, newText);
                                c.getController().obtainFocusForSelected(); // focus fix
                                cancel();
                            }
                        });
            mCurrentEditDialog = nodeEditDialog;
            nodeEditDialog.show();
            return;
        }
        // inline editing:
        EditNodeTextField textfield =
            new EditNodeTextField(node, text, firstEvent, c, new EditNodeBase.EditControl(){
                
                public void cancel() {
                    if (isNewNode) { // delete also the node and set focus to the parent
                        c.getView().selectAsTheOnlyOneSelected(node);
                        Vector nodeList = new Vector();
                        nodeList.add(node.getModel());
                        c.cut(nodeList);
                        c.select(prevSelected);
                        // include max level for navigation
                        if (parentFolded) {
                            c.setFolded(prevSelected.getModel(), true);
                        }
                    }
                    endEdit();
                }
                
                public void ok(String newText) {
                    setNodeText(node.getModel(), newText);
                    endEdit();
                }
                
                private void endEdit() {
                    c.getController().obtainFocusForSelected();
                    c.setBlocked(false);
                    mCurrentEditDialog = null;
                }
                
                public void split(String newText, int position) {
                }});
        mCurrentEditDialog = textfield;
        textfield.show();
        
    }
    
    public void setNodeText(MindMapNode selected, String newText){
        String oldText = selected.toString();
        
        c.getActionFactory().startTransaction(c.getText("edit_node"));
        EditNodeAction EditAction = new EditNodeAction();
        EditAction.setNode(c.getNodeID(selected));
        EditAction.setText(newText);
        
        EditNodeAction undoEditAction = new EditNodeAction();
        undoEditAction.setNode(c.getNodeID(selected));
        undoEditAction.setText(oldText);
        
        c.getActionFactory().executeAction(new ActionPair(EditAction, undoEditAction));
        c.getActionFactory().endTransaction(c.getText("edit_node"));
        
    }
    
    protected MindMapController getMindMapController() {
        return c;
    }
    
    
    
}
