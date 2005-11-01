/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;

import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
class ExtendedAttributeTableModelDecorator   implements AttributeTableModel, ChangeListener, NodeViewEventListener{
    private AttributeTableModel nodeAttributeModel;
    private AttributeRegistry attributeRegistry;
    public ExtendedAttributeTableModelDecorator(
            AttributeView attributeView, 
            AttributeTableModel nodeAttributeModel,
            AttributeRegistry attributeRegistry) {
        super();
        this.nodeAttributeModel = nodeAttributeModel;
        this.attributeRegistry = attributeRegistry;
        nodeAttributeModel.getNode().addNodeViewEventListener(this);        
    }
    public int getColumnCount() {
        return 2;
    }
    public int getRowCount() {
        if (nodeAttributeModel.getNode().getMap().getRegistry().getAttributes().isRestricted())
            return nodeAttributeModel.getRowCount();
        return nodeAttributeModel.getRowCount() + 1;
    }
    public Object getValueAt(int row, int col) {
        if (row < nodeAttributeModel.getRowCount()){
            return nodeAttributeModel.getValueAt(row, col);
        }
        return "";
    }
    public void insertRow(int index, Attribute newAttribute) {
        nodeAttributeModel.insertRow(index, newAttribute);
    }
    public boolean isCellEditable(int row, int col) {
        if(nodeAttributeModel.isCellEditable(row, col)){
            if (col == 0)
                return true;
            if (row < nodeAttributeModel.getRowCount())
                return nodeAttributeModel.getValueAt(row, 0).toString().length() > 0;
        }
        return false;
    }
    
    public Object removeRow(int index) {
        return nodeAttributeModel.removeRow(index);
    }
    
    public void setValueAt(Object o, int row, int col) {
        if(row < nodeAttributeModel.getRowCount()){
            if(col == 1 || o.toString().length() > 0){
                nodeAttributeModel.setValueAt(o, row, col);
            }
            else{
                nodeAttributeModel.removeRow(row);
            }
            return;
        }
        if(col == 0 && o.toString().length() > 0){
            addRow(new Attribute(o.toString()));
        }
    }
    public void addRow(Attribute attr) {
        nodeAttributeModel.addRow(attr);
    }
    public MindMapNode getNode() {
        return nodeAttributeModel.getNode();
    }
    public String toString() {
        return nodeAttributeModel.toString();
    }
    
    public void addTableModelListener(TableModelListener l) {
        nodeAttributeModel.addTableModelListener(l);
    }
    public void removeTableModelListener(TableModelListener l) {
        nodeAttributeModel.removeTableModelListener(l);
    }
    public Class getColumnClass(int columnIndex) {
        return nodeAttributeModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return nodeAttributeModel.getColumnName(columnIndex);
    }
    public int getColumnWidth(int col) {
        return nodeAttributeModel.getColumnWidth(col);
    }
    public void setColumnWidth(int col, int width) {
        nodeAttributeModel.setColumnWidth(col, width);
    }
    private void addListeners() {
        this.attributeRegistry.addChangeListener(this);
    }
    private void removeListeners() {
        this.attributeRegistry.removeChangeListener(this);
        nodeAttributeModel.getNode().removeNodeViewEventListener(this);        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        nodeAttributeModel.fireTableDataChanged();     
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#fireTableStructureChanged()
     */
    public void nodeViewCreated(NodeViewEvent event) {
        addListeners();
    }
    public void nodeViewRemoved(NodeViewEvent event) {
        removeListeners();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#fireTableDataChanged()
     */
    public void fireTableDataChanged() {
        nodeAttributeModel.fireTableDataChanged();        
    }
}
