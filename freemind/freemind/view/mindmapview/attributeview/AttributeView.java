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
/*$Id: AttributeView.java,v 1.1.2.1 2005-09-17 19:02:07 dpolivaev Exp $*/

package freemind.view.mindmapview.attributeview;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.TreeNode;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeRegistryTableModel;
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
public class AttributeView implements ChangeListener, ColumnWidthChangeListener {    
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
    
    public AttributeView(NodeView nodeView) {
        super();
        this.nodeView = nodeView;
        ConcreteAttributeTableModel attributes = getModel().getAttributes();
        AttributeRegistryTableModel registryTable = getModel().getMap().getRegistry().getAttributes();
        reducedAttributeTableModel = new ReducedAttributeTableModelDecorator(attributes, registryTable);
        currentAttributeTableModel = reducedAttributeTableModel;
        getModel().getAttributes().getLayout().addStateChangeListener(this);
        getModel().getAttributes().getLayout().addColumnWidthChangeListener(this);
        getModel().getAttributes().setViewType(attributes.getViewType());
        setViewType(attributes.getViewType());
    }
    public JComponent syncronizeAttributeView() {
        if (attributeTable == null && currentAttributeTableModel.getRowCount() > 0){
            attributeTable = new AttributeTable(this);
            attributeTable.getModel().addTableModelListener(new AttributeChangeListener());
            attributeViewScrollPane = new AttributeViewScrollPane(attributeTable);
            attributeViewScrollPane.setColumnHeaderView(attributeTable.getTableHeader());
            getNodeView().add(attributeViewScrollPane);
        }
        return attributeViewScrollPane;
    }
    /**
     * @return
     */
    public void update() {
        if(attributeTable != null){
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
}
