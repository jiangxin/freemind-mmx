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
/*$Id: AttributeView.java,v 1.1.2.13 2006-03-02 21:00:55 dpolivaev Exp $*/

package freemind.view.mindmapview.attributeview;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import freemind.controller.attributes.AttributePopupMenu;
import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;


/**
 * This class represents a single Node of a MindMap (in analogy to
 * TreeCellRenderer).
 */
public class AttributeView implements ChangeListener, NodeViewEventListener, TableModelListener {    
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
        AttributeRegistry attributeRegistry = getAttributeRegistry();
        reducedAttributeTableModel = new ReducedAttributeTableModelDecorator(attributes, attributeRegistry);
        currentAttributeTableModel = reducedAttributeTableModel;
        setViewType(attributeRegistry.getAttributeViewType());
    }
    private AttributeRegistry getAttributeRegistry() {
        return getNode().getMap().getRegistry().getAttributes();
    }
    public NodeAttributeTableModel getAttributes() {
        return getNode().getAttributes();
    }
    public void syncronizeAttributeView() {
        if (attributeTable == null && currentAttributeTableModel.areAttributesVisible()){
            provideAttributeTable();
        }
    }
    private void provideAttributeTable() {
        if (attributeTable == null){            
            attributeTable = new AttributeTable(this);
            if(getAttributeRegistry().getAttributeViewType().equals(AttributeTableLayoutModel.SHOW_ALL)){
                attributeTable.getTableHeader().setBackground(EXTENDED_HEADER_BACKGROUND);
            }
            addTableModelListeners();
            attributeViewScrollPane = new AttributeViewScrollPane(attributeTable);
            attributeViewScrollPane.setColumnHeaderView(attributeTable.getTableHeader());
            getNodeView().add(attributeViewScrollPane);
            getAttributes().removeTableModelListener(this);
        }
    }
    private void addListeners() {
        getAttributeRegistry().addChangeListener(this);
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
        else {
            getAttributes().addTableModelListener(this);
        }
    }
    
    private void removeListeners() {
        getAttributeRegistry().removeChangeListener(this);
        if(attributeTable != null)
            getAttributes().getLayout().removeColumnWidthChangeListener(attributeTable);
        nodeView.getModel().removeNodeViewEventListener(this);
        if(attributeTable != null){
            attributeTable.getParent().remove(attributeTable);
            attributeTable.getModel().removeTableModelListener(attributeTable);
            attributeTable.removeMouseListener(tablePopupMenu);                
            attributeTable.getTableHeader().removeMouseListener(tablePopupMenu);
        }
        else {
            getAttributes().removeTableModelListener(this);
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
        final String viewType = getViewType();
        return  viewType !=AttributeTableLayoutModel.HIDE_ALL 
        && (currentAttributeTableModel.areAttributesVisible() || viewType != getAttributeRegistry().getAttributeViewType());
    }
    /**
     * @return Returns the extendedAttributeTableModel.
     */
    private ExtendedAttributeTableModelDecorator getExtendedAttributeTableModel() {
        if(extendedAttributeTableModel == null){
            extendedAttributeTableModel = new ExtendedAttributeTableModelDecorator(
                    getAttributes(),
                    getAttributeRegistry());
        }
        return extendedAttributeTableModel;
    }
    
    private void setViewType(String viewType) {
        Color headerBackground = USUAL_HEADER_BACKGROUND;
        if(viewType == AttributeTableLayoutModel.SHOW_ALL){
            currentAttributeTableModel = getExtendedAttributeTableModel();
            headerBackground = EXTENDED_HEADER_BACKGROUND;
        }
        else {
            currentAttributeTableModel = reducedAttributeTableModel;
            reducedAttributeTableModel.stateChanged(null);
        }
        if(attributeTable != null){
            attributeTable.setModel(currentAttributeTableModel);
            attributeTable.getTableHeader().setBackground(headerBackground);
            attributeViewScrollPane.invalidate();
        }
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
        setViewType(getAttributeRegistry().getAttributeViewType());
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
    public void tableChanged(TableModelEvent e) {
        MapView map = getNodeView().getMap();
        map.getModel().nodeChanged(getNode());
    }
    public String getViewType() {
        return currentAttributeTableModel == reducedAttributeTableModel 
        ? getAttributeRegistry().getAttributeViewType()
        : AttributeTableLayoutModel.SHOW_ALL;
    }
    public void edit() {
        provideAttributeTable();
        final String registryAttributeViewType = getAttributeRegistry().getAttributeViewType();
        if(registryAttributeViewType == AttributeTableLayoutModel.SHOW_SELECTED
                || registryAttributeViewType == AttributeTableLayoutModel.HIDE_ALL){
            setViewType(AttributeTableLayoutModel.SHOW_ALL);
        }
        if(currentAttributeTableModel.getRowCount() == 0){
            attributeTable.insertRow(0);
        }
        else{
            if(attributeTable.isVisible()){
                attributeTable.requestFocus();
            }
            else{
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        attributeTable.requestFocus();
                    }
                });
            }
        }
    }
    boolean isPopupShown(){
        return attributeTable != null
        && (tablePopupMenu.getTable() == attributeTable);       
    }
    static public Component getAncestorComponent(Component object, Class ancestorClass) {
        if(object == null || ancestorClass.isAssignableFrom(object.getClass())){
            return object;
        }
        return getAncestorComponent(object.getParent(), ancestorClass);
    }
}
