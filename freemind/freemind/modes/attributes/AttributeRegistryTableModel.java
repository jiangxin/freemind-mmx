/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.table.AbstractTableModel;

import freemind.controller.filter.util.SortedListModel;
import freemind.main.Resources;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class AttributeRegistryTableModel   extends AbstractTableModel {
    private final AttributeRegistry attributeRegistry;
    private String allAttributes = Resources.getInstance().getResourceString("attributes_all");

    AttributeRegistryTableModel(AttributeRegistry registry)
    {
        this.attributeRegistry = registry;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return attributeRegistry.size() + 1;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 4;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if(row == 0 && col < 2){
            if(col == 0)
                return allAttributes;
            return null;
        }
        row--;
        switch(col){
        case 0: return attributeRegistry.getKey(row);
        case 1: return attributeRegistry.getElement(row).getVisibilityModel();
        case 2: return attributeRegistry.getRestriction(row);
        case 3: return attributeRegistry.getValues(row);        
        }
        return null;
    }

    public void setValueAt(Object o, int row, int col) {
        if(row == 0 && col != 2)
        {
            return;
        }
        if(col == 3)
        {
            return;
        }
        Boolean value = (Boolean)o;
        switch (col){
        case 1:
            attributeRegistry.setVisibilityModel(row-1, value);
            break;
        case 2:
            attributeRegistry.setRestrictionModel(row-1, value);
            break;
        }
    }


    public Class getColumnClass(int c) {
        switch(c){
        case 0:
            return String.class;
        case 1:
            return Boolean.class;
        case 2:
            return Boolean.class;
        case 3:
            return SortedListModel.class;
        }
        return Object.class;
    }
    
    public boolean isCellEditable(int row, int col) {
        return col >= 1;
    }
    
    static private String attributeColumnName = null; 
    static private String visibilityColumnName = null;
    static private String restrictionColumnName = null;
    static private String editorColumnName = null;

    public String getColumnName(int column) {
        
        switch(column){
        case 0: 
            if (attributeColumnName == null)
                attributeColumnName = Resources.getInstance().getResourceString("attributes_attribute");
            return  attributeColumnName;           
        case 1: 
            if (visibilityColumnName == null)
                visibilityColumnName = Resources.getInstance().getResourceString("attributes_visible");
            return  visibilityColumnName;           
        case 2: 
            if (restrictionColumnName == null)
                restrictionColumnName = Resources.getInstance().getResourceString("attributes_restriction");
            return  restrictionColumnName;           
        case 3: 
            if (editorColumnName == null)
                editorColumnName = Resources.getInstance().getResourceString("attributes_edit");
            return  editorColumnName;           
        }
        return null;
    }
    
    public void fireTableRowsDeleted() {
        if (getRowCount() > 1)
            fireTableRowsDeleted(1, getRowCount()-1);
    }
    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row+1, column);
    }
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        super.fireTableRowsDeleted(firstRow+1, lastRow+1);
    }
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        super.fireTableRowsInserted(firstRow+1, lastRow+1);
    }
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        super.fireTableRowsUpdated(firstRow+1, lastRow+1);
    }
    /**
     * @param row
     * @param i
     */
    public void fireVisibilityUpdated(int row) {
        fireTableCellUpdated(row+1, 1);
    }
    /**
     * @param row
     */
    public void fireRestrictionsUpdated(int row) {
        fireTableRowsUpdated(row+1, row+1);    }
}
