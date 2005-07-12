/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class ExtendedAttributeTableModel  extends AttributeTableModelAdapter implements AttributeTableModel{
    private AttributeTableModel concreteModel;
    public ExtendedAttributeTableModel(AttributeTableModel model) {
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
        fireTableRowsInserted(index, index);
    }
    public boolean isCellEditable(int row, int col) {
        if (col == 0)
            return true;
        if (row < concreteModel.getRowCount())
            return concreteModel.getValueAt(row, 0).toString().length() > 0;
        return false;
    }
    
    public Object removeRow(int index) {
        return concreteModel.removeRow(index);
    }
    
    public void setValueAt(Object o, int row, int col) {
        if(row < concreteModel.getRowCount()){
            if(col == 1 || o.toString().length() > 0){
                concreteModel.setValueAt(o, row, col);
            	fireTableCellUpdated(row,col);
            }
            else{
                concreteModel.removeRow(row);
                fireTableRowsDeleted(row, row);
            }
            return;
        }
        if(col == 0 && o.toString().length() > 0){
            addRow(new Attribute(o.toString()));
        }
    }
    public void addRow(Attribute attr) {
        concreteModel.addRow(attr);
        fireTableRowsInserted(concreteModel.getRowCount(), concreteModel.getRowCount());
    }
    public MindMapNode getNode() {
        return concreteModel.getNode();
    }
    public String toString() {
        return concreteModel.toString();
    }
    
}
