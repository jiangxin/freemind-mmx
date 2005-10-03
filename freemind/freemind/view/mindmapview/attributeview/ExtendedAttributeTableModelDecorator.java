/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.TableModelListener;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
class ExtendedAttributeTableModelDecorator   implements AttributeTableModel{
    private AttributeTableModel concreteModel;
    ExtendedAttributeTableModelDecorator(AttributeTableModel model) {
        super();
        this.concreteModel = model;
    }
    public int getColumnCount() {
        return 2;
    }
    public int getRowCount() {
        return concreteModel.getRowCount() + 1;
    }
    public Object getValueAt(int row, int col) {
        if (row < concreteModel.getRowCount()){
            return concreteModel.getValueAt(row, col);
        }
        return "";
    }
    public void insertRow(int index, Attribute newAttribute) {
        concreteModel.insertRow(index, newAttribute);
    }
    public boolean isCellEditable(int row, int col) {
        if(concreteModel.isCellEditable(row, col)){
            if (col == 0)
                return true;
            if (row < concreteModel.getRowCount())
                return concreteModel.getValueAt(row, 0).toString().length() > 0;
        }
        return false;
    }
    
    public Object removeRow(int index) {
        return concreteModel.removeRow(index);
    }
    
    public void setValueAt(Object o, int row, int col) {
        if(row < concreteModel.getRowCount()){
            if(col == 1 || o.toString().length() > 0){
                concreteModel.setValueAt(o, row, col);
            }
            else{
                concreteModel.removeRow(row);
            }
            return;
        }
        if(col == 0 && o.toString().length() > 0){
            addRow(new Attribute(o.toString()));
        }
    }
    public void addRow(Attribute attr) {
        concreteModel.addRow(attr);
    }
    public MindMapNode getNode() {
        return concreteModel.getNode();
    }
    public String toString() {
        return concreteModel.toString();
    }
    
    public void addTableModelListener(TableModelListener l) {
        concreteModel.addTableModelListener(l);
    }
    public void removeTableModelListener(TableModelListener l) {
        concreteModel.removeTableModelListener(l);
    }
    public Class getColumnClass(int columnIndex) {
        return concreteModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return concreteModel.getColumnName(columnIndex);
    }
    public int getColumnWidth(int col) {
        return concreteModel.getColumnWidth(col);
    }
    public void setColumnWidth(int col, int width) {
        concreteModel.setColumnWidth(col, width);
    }
}
