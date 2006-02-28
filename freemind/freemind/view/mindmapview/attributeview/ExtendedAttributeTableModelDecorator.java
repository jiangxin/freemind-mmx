/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.ChangeEvent;

import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
class ExtendedAttributeTableModelDecorator extends AttributeTableModelDecoratorAdapter{
    int newRow;
    private static final int AFTER_LAST_ROW = Integer.MAX_VALUE;
    public ExtendedAttributeTableModelDecorator(
            NodeAttributeTableModel nodeAttributeModel,
            AttributeRegistry attributeRegistry) {
        super(nodeAttributeModel, attributeRegistry);
        newRow = AFTER_LAST_ROW;
    }
    public int getRowCount() {
        if (newRow == AFTER_LAST_ROW)
            return nodeAttributeModel.getRowCount();
        return nodeAttributeModel.getRowCount() + 1;
    }
    public Object getValueAt(int row, int col) {
        if (row < newRow){
            return nodeAttributeModel.getValueAt(row, col);
        }
        if(row == newRow){
            return "";
        }
        return nodeAttributeModel.getValueAt(row-1, col);
    }
    public void insertRow(int index) {
        newRow = index;
        fireTableRowsInserted(index, index);
    }
    public boolean isCellEditable(int row, int col) {
        if(row != newRow){
            int rowInModel = row < newRow ? row : row - 1;
            return nodeAttributeModel.isCellEditable(rowInModel, col);
        }
        return col == 0;
    }
    
    public Object removeRow(int index) {
        return nodeAttributeModel.removeRow(index);
    }
    
    public void setValueAt(Object o, int row, int col) {
        if(row != newRow){
            if(col == 1 || o.toString().length() > 0){
                int rowInModel = row < newRow ? row : row - 1;
                nodeAttributeModel.setValueAt(o, rowInModel, col);
            }
            return;
        }
        else{
            newRow = AFTER_LAST_ROW;
            fireTableRowsDeleted(row, row);
            if(col == 0 && o != null && o.toString().length() > 0){
                nodeAttributeModel.insertRow(row, o.toString(), "");
            }
            return;
        }
        
    }
    public void stateChanged(ChangeEvent e) {
        fireTableDataChanged();     
    }
    
    public void editingCanceled() {
        if(newRow != AFTER_LAST_ROW){
            int row = newRow;
            newRow = AFTER_LAST_ROW;  
            fireTableRowsDeleted(row, row);
          
        }
    }
    /* (non-Javadoc)
     * @see freemind.view.mindmapview.attributeview.AttributeTableModelDecoratorAdapter#areAttributesVisible()
     */
    public boolean areAttributesVisible() {
        return getRowCount() !=0;
    }
    /**
     * @param row
     */
    public void moveRowUp(int row) {
        Attribute attribute = (Attribute)nodeAttributeModel.removeRow(row);
        nodeAttributeModel.insertRow(row-1, attribute.getName(), attribute.getValue());                
    }
    /**
     * @param row
     */
    public void moveRowDown(int row) {
        Attribute attribute = (Attribute)nodeAttributeModel.removeRow(row);
        nodeAttributeModel.insertRow(row+1, attribute.getName(), attribute.getValue());                
    }
}
