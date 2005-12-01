/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: AttributeView.java,v 1.1.2.9 2005-12-01 18:21:41 dpolivaev Exp $*/

package freemind.view.mindmapview.attributeview;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;

import freemind.controller.attributes.AttributePopupMenu;
import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.ColumnWidthChangeEvent;
import freemind.modes.attributes.ColumnWidthChangeListener;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;


/**
 * This class represents a single Node of a MindMap (in analogy to
 * TreeCellRenderer).
 */
public class AttributeView implements ChangeListener, NodeViewEventListener {    
    private AttributeTable attributeTable;
    private ReducedAttributeTableModelDecorator reducedAttributeTableModel;
    private ExtendedAttributeTableModelDecorator extendedAttributeTableModel = null;
    private AttributeTableModelDecoratorAdapter currentAttributeTableModel;
    private JScrollPane attributeViewScrollPane;
    private NodeView nodeView;
    static private AttributePopupMenu tablePopupMenu;
    private static final Color USUAL_HEADER_BACKGROUND = UIManager.getColor("TableHeader.background");
    private static final Color EXTENDED_HEADER_BACKGROUND = Color.RED.darker();
    
    public AttributeView(NodeView nodeView) {
        super();
        this.nodeView = nodeView;
        nodeView.getModel().addNodeViewEventListener(this);
        NodeAttributeTableModel attributes = getAttributes();
        AttributeRegistry attributeRegistry = getNode().getMap().getRegistry().getAttributes();
        reducedAttributeTableModel = new ReducedAttributeTableModelDecorator(attributes, attributeRegistry);
        currentAttributeTableModel = reducedAttributeTableModel;
        getAttributes().getLayout().setViewType(attributes.getViewType());
        setViewType(attributes.getViewType());
    }
    public NodeAttributeTableModel getAttributes() {
        return getNode().getAttributes();
    }
    public void syncronizeAttributeView() {
        if (attributeTable == null && currentAttributeTableModel.areAttributesVisible()){
            attributeTable = new AttributeTable(this);
            if(getAttributes().getViewType().equals(AttributeTableLayoutModel.SHOW_EXTENDED)){
                attributeTable.getTableHeader().setBackground(EXTENDED_HEADER_BACKGROUND);
            }
            addTableModelListeners();
            attributeViewScrollPane = new AttributeViewScrollPane(attributeTable);
            attributeViewScrollPane.setColumnHeaderView(attributeTable.getTableHeader());
            getNodeView().add(attributeViewScrollPane);
        }
    }
    private void addListeners() {
        getAttributes().getLayout().addStateChangeListener(this);
        addTableModelListeners();
    }
    private void addTableModelListeners() {
        if(attributeTable != null){
             if(tablePopupMenu == null){
                tablePopupMenu = new AttributePopupMenu();
            }
            getAttributes().getLayout().addColumnWidthChangeListener(attributeTable);
            attributeTable.addMouseListener(tablePopupMenu);
            attributeTable.getTableHeader().addMouseListener(tablePopupMenu);
        }
    }
    
    private void removeListeners() {
        getAttributes().getLayout().removeStateChangeListener(this);
        if(attributeTable != null)
            getAttributes().getLayout().removeColumnWidthChangeListener(attributeTable);
        nodeView.getModel().removeNodeViewEventListener(this);
        if(attributeTable != null){
            attributeTable.getParent().remove(attributeTable);
            attributeTable.getModel().removeTableModelListener(attributeTable);
            attributeTable.removeMouseListener(tablePopupMenu);                
            attributeTable.getTableHeader().removeMouseListener(tablePopupMenu);
        }
    }
    /**
     * @return
     */
    public void update() {
        if(attributeTable != null && attributeTable.isVisible()){
            attributeTable.updateAttributeTable();
        }
    }
    /**
     * @return
     */
    public boolean areAttributesVisible() {
        return  currentAttributeTableModel.areAttributesVisible();
    }
    /**
     * @return Returns the extendedAttributeTableModel.
     */
    private ExtendedAttributeTableModelDecorator getExtendedAttributeTableModel() {
        if(extendedAttributeTableModel == null){
            extendedAttributeTableModel = new ExtendedAttributeTableModelDecorator(
                    getAttributes(),
                    getNode().getMap().getRegistry().getAttributes());
        }
        return extendedAttributeTableModel;
    }
    
    private void setViewType(String viewType) {
        Color headerBackground = USUAL_HEADER_BACKGROUND;
        if(viewType.equals( AttributeTableLayoutModel.SHOW_EXTENDED)){
            currentAttributeTableModel = getExtendedAttributeTableModel();
            headerBackground = EXTENDED_HEADER_BACKGROUND;
        }
        else if(viewType.equals( AttributeTableLayoutModel.SHOW_REDUCED)){
            currentAttributeTableModel = reducedAttributeTableModel;
            reducedAttributeTableModel.stateChanged(null);
        }
        if(attributeTable != null && attributeTable.getModel() != attributeTable){
            attributeTable.setModel(currentAttributeTableModel);
            attributeTable.getTableHeader().setBackground(headerBackground);
            attributeViewScrollPane.invalidate();
        }
    }
    public String getAttributeViewType(){
        return getAttributes().getViewType();           
    }
    
    public AttributeTableModel getCurrentAttributeTableModel() {
        return currentAttributeTableModel;
    }
     /**
     * @return
     */
    MindMapNode getNode() {
        return getNodeView().getModel();
    }
    
    public void stateChanged(ChangeEvent event){
        setViewType(getAttributes().getLayout().getViewType());
    }
    /**
     * @return
     */
    public NodeView getNodeView() {
        return nodeView;
    }
    /**
     * @return
     */
    public MapView getMapView() {
        return getNodeView().getMap();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    public void nodeViewCreated(NodeViewEvent event){
        addListeners();        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    public void nodeViewRemoved(NodeViewEvent event) {
        removeListeners();        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorMoved(AncestorEvent event) {
    }
}
