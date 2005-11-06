/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.ChangeEvent;

import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
class ExtendedAttributeTableModelDecorator extends AttributeTableModelDecoratorAdapter{
    public ExtendedAttributeTableModelDecorator(
            AttributeTableModel nodeAttributeModel,
            AttributeRegistry attributeRegistry) {
        super(nodeAttributeModel, attributeRegistry);
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
    public void stateChanged(ChangeEvent e) {
        fireTableDataChanged();     
    }
}
