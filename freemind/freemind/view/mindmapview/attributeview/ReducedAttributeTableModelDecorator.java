/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
class ReducedAttributeTableModelDecorator extends AbstractTableModel implements AttributeTableModel, ChangeListener, TableModelListener, NodeViewEventListener{
    private AttributeTableModel nodeAttributeModel;
    private AttributeRegistry attributeRegistry;
    private Vector index = null;
    private int visibleRowCount;
    ReducedAttributeTableModelDecorator(AttributeView attributeView, AttributeTableModel model, AttributeRegistry registryTable) {
        super();
        this.nodeAttributeModel = model;        
        this.attributeRegistry = registryTable;
        stateChanged(null);
        nodeAttributeModel.getNode().addNodeViewEventListener(this);        
    }
    private void addListeners() {
        nodeAttributeModel.addTableModelListener(this);
        this.attributeRegistry.addChangeListener(this);
    }
    private void removeListeners() {
        nodeAttributeModel.removeTableModelListener(this);
        this.attributeRegistry.removeChangeListener(this);
        nodeAttributeModel.getNode().removeNodeViewEventListener(this);        
    }
    private Vector getIndex() {
        if(index == null && this.attributeRegistry.getVisibleElementsNumber() > 0)
            index = new Vector(this.nodeAttributeModel.getRowCount(), 10);
        return index;
    }
    public int getColumnCount() {
        return 2;
    }
    public int getRowCount() {
        return visibleRowCount;
    }
    public Object getValueAt(int row, int col) {
        return nodeAttributeModel.getValueAt(calcRow(row), col);
    }
    
    public boolean isCellEditable(int row, int col) {
        if(nodeAttributeModel.isCellEditable(row, col)){
            return col == 1;
        }
        return false;
    }

    private int calcRow(int row){
        return ((Integer) index.get(row)).intValue();
    }
    public void setValueAt(Object o, int row, int col) {
        nodeAttributeModel.setValueAt(o, calcRow(row), col);
    	fireTableCellUpdated(row,col);
    }
    
    public MindMapNode getNode() {
        return nodeAttributeModel.getNode();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#insertRow(int, freemind.modes.attributes.Attribute)
     */
    public void insertRow(int index, Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#addRow(freemind.modes.attributes.Attribute)
     */
    public void addRow(Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#removeRow(int)
     */
    public Object removeRow(int index) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        Vector index = getIndex();
        if(index != null){
            visibleRowCount= 0;
            index.clear();
            for(int i = 0; i < nodeAttributeModel.getRowCount(); i++){
                String name = (String)nodeAttributeModel.getValueAt(i, 0);
                if(attributeRegistry.getElement(name).isVisible().booleanValue()){
                    index.add(new Integer(i));
                    visibleRowCount++;
                }
            }
        fireTableStructureChanged();    
        }
        
    }
    public Class getColumnClass(int columnIndex) {
        return nodeAttributeModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return nodeAttributeModel.getColumnName(columnIndex);
    }
    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireTableDataChanged();        
    }
    
    public int getColumnWidth(int col) {
        return nodeAttributeModel.getColumnWidth(col);
    }
    
    public void setColumnWidth(int col, int width) {
        nodeAttributeModel.setColumnWidth(col, width);
    }
    public void nodeViewCreated(NodeViewEvent event) {
        addListeners();
    }
    public void nodeViewRemoved(NodeViewEvent event) {
        removeListeners();
    }
}
