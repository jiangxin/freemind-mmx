/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.SortedMapVector;
import freemind.main.Resources;
import freemind.modes.MapRegistry;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class AttributeRegistryTableModel   extends AbstractTableModel {
    private final AttributeRegistry attributeRegistry;

    AttributeRegistryTableModel(AttributeRegistry registry)
    {
        this.attributeRegistry = registry;
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return attributeRegistry.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
         switch(col){
        case 0: return attributeRegistry.getKey(row);
        case 1: return attributeRegistry.getElement(row).isVisible();
        }
        return null;
    }

    public void setValueAt(Object o, int row, int col) {
        Boolean visible = (Boolean)o;
        attributeRegistry.setVisible(row, visible);
    }


    public Class getColumnClass(int c) {
        switch(c){
        case 0:
            return String.class;
        case 1:
            return Boolean.class;
        }
        return Object.class;
    }
    
    public boolean isCellEditable(int row, int col) {
        return col == 1;
    }
    
    static private String attributeColumnName = null; 
    static private String visibilityColumnName = null;

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
        }
        return null;
    }
    
    void fireTableRowsDeleted() {
        if (getRowCount() > 0)
            fireTableRowsDeleted(0, getRowCount()-1);
    }
    /**
     * @param row
     * @param i
     */
    public void fireVisibilityUpdated(int row, int col) {
        fireTableCellUpdated(row, col);
    }
}
