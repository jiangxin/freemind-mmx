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
/*$Id: AttributeView.java,v 1.1.2.3 2005-10-08 09:45:23 dpolivaev Exp $*/

package freemind.view.mindmapview.attributeview;

import javax.swing.JScrollPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.TreeNode;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.ColumnWidthChangeEvent;
import freemind.modes.attributes.ColumnWidthChangeListener;
import freemind.modes.attributes.ConcreteAttributeTableModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;


/**
 * This class represents a single Node of a MindMap (in analogy to
 * TreeCellRenderer).
 */
public class AttributeView implements ChangeListener, ColumnWidthChangeListener, AncestorListener {    
    private class AttributeChangeListener implements TableModelListener{
        public void tableChanged(TableModelEvent arg0) {
            MapView map = getNodeView().getMap();
            map.getModel().nodeChanged(getModel());
        }
        
        /**
         * @return
         */
        private TreeNode getModel() {
            return getNodeView().getModel();
        }
        
    }
    private AttributeTable attributeTable;
    private ReducedAttributeTableModelDecorator reducedAttributeTableModel;
    private AttributeTableModel extendedAttributeTableModel = null;
    private AttributeTableModel currentAttributeTableModel;
    private JScrollPane attributeViewScrollPane;
    private NodeView nodeView;
    private AttributeChangeListener attributeChangeListener;
    
    public AttributeView(NodeView nodeView) {
        super();
        this.nodeView = nodeView;
        nodeView.addAncestorListener(this);
        ConcreteAttributeTableModel attributes = getModel().getAttributes();
        AttributeRegistry attributeRegistry = getModel().getMap().getRegistry().getAttributes();
        reducedAttributeTableModel = new ReducedAttributeTableModelDecorator(this, attributes, attributeRegistry);
        currentAttributeTableModel = reducedAttributeTableModel;
        getModel().getAttributes().setViewType(attributes.getViewType());
        setViewType(attributes.getViewType());
    }
    private void addListeners() {
        getModel().getAttributes().getLayout().addStateChangeListener(this);
        getModel().getAttributes().getLayout().addColumnWidthChangeListener(this);
        addTableModelListener();
    }
    public void syncronizeAttributeView() {
        if (attributeTable == null && currentAttributeTableModel.getRowCount() > 0){
            attributeTable = new AttributeTable(this);
            addTableModelListener();
            attributeViewScrollPane = new AttributeViewScrollPane(attributeTable);
            attributeViewScrollPane.setColumnHeaderView(attributeTable.getTableHeader());
            getNodeView().add(attributeViewScrollPane);
        }
    }
    private void addTableModelListener() {
        if(attributeTable != null){
            if(attributeChangeListener == null){
                attributeChangeListener = new AttributeChangeListener();
                attributeTable.getModel().addTableModelListener(attributeChangeListener);
            }
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
        return  currentAttributeTableModel.getRowCount() > 0;
    }
    /**
     * @return Returns the extendedAttributeTableModel.
     */
    private AttributeTableModel getExtendedAttributeTableModel() {
        if(extendedAttributeTableModel == null){
            extendedAttributeTableModel = new ExtendedAttributeTableModelDecorator(getModel().getAttributes());
        }
        return extendedAttributeTableModel;
    }
    
    private void setViewType(String viewType) {
        if(viewType.equals( AttributeTableLayoutModel.SHOW_EXTENDED)){
            currentAttributeTableModel = getExtendedAttributeTableModel();
        }
        else if(viewType.equals( AttributeTableLayoutModel.SHOW_REDUCED)){
            currentAttributeTableModel = reducedAttributeTableModel;
            reducedAttributeTableModel.stateChanged(null);
        }
        if(attributeTable != null && attributeTable.getModel() != attributeTable){
            attributeTable.setModel(currentAttributeTableModel);
            attributeViewScrollPane.invalidate();
        }
    }
    public String getAttributeViewType(){
        return getModel().getAttributes().getViewType();           
    }
    
    public AttributeTableModel getCurrentAttributeTableModel() {
        return currentAttributeTableModel;
    }
    public void columnWidthChanged(ColumnWidthChangeEvent event) {
        int col = event.getColumnNumber();
        AttributeTableLayoutModel layoutModel = (AttributeTableLayoutModel) event.getSource();
        int width = layoutModel.getColumnWidth(col);
        attributeTable.getColumnModel().getColumn(col).setPreferredWidth(width);
        getModel().getMap().nodeChanged(getModel());
    }
    /**
     * @return
     */
    private MindMapNode getModel() {
        return getNodeView().getModel();
    }
    
    public void stateChanged(ChangeEvent event){
        setViewType(getModel().getAttributes().getLayout().getViewType());
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
    /**
     * 
     */
    public static void clearOldSelection() {
        AttributeTable.clearOldSelection();        
    }
    /**
     * 
     */
    public void removeListeners() {
        getModel().getAttributes().getLayout().removeStateChangeListener(this);
        getModel().getAttributes().getLayout().removeColumnWidthChangeListener(this);
        if(attributeTable != null)
            attributeTable.getModel().removeTableModelListener(attributeChangeListener);
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    public void ancestorAdded(AncestorEvent event) {
        addListeners();        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorRemoved(AncestorEvent event) {
        removeListeners();        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorMoved(AncestorEvent event) {
    }
}
