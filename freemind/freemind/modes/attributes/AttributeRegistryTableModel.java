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
public class AttributeRegistryTableModel extends AbstractTableModel  {
    private class AttributeComboBoxColumnModel extends AbstractListModel implements TableModelListener, ComboBoxModel, SortedListModel{
        public AttributeComboBoxColumnModel() {
            super();
            addTableModelListener(this);
        }
        private Object selectedItem;
        /* (non-Javadoc)
         * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
         */
        public void setSelectedItem(Object o) {
            selectedItem = o;
            fireContentsChanged(o, -1, -1);
        }

        /* (non-Javadoc)
         * @see javax.swing.ComboBoxModel#getSelectedItem()
         */
        public Object getSelectedItem() {
            return selectedItem;
        }
        public int getSize() {
            return elements.size();
        }

        /* (non-Javadoc)
         * @see javax.swing.ListModel#getElementAt(int)
         */
        public Object getElementAt(int row) {
             return getValueAt(row, 0);
        }

        /* (non-Javadoc)
         * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
         */
        public void tableChanged(TableModelEvent e) {
            if(e.getType() == TableModelEvent.UPDATE)
            {
                fireIntervalAdded(this, e.getFirstRow(), e.getLastRow());
            }
        }

        /* (non-Javadoc)
         * @see freemind.controller.filter.util.SortedListModel#clear()
         */
        public void clear() {
            
        }

        /* (non-Javadoc)
         * @see freemind.controller.filter.util.SortedListModel#contains(java.lang.Object)
         */
        public boolean contains(Object o) {
            return containsElement(o.toString()); 
        }

        /* (non-Javadoc)
         * @see freemind.controller.filter.util.SortedListModel#add(java.lang.Object)
         */
        public void add(Object o) {
            
        }

        /* (non-Javadoc)
         * @see freemind.controller.filter.util.SortedListModel#getIndexOf(java.lang.Object)
         */
        public int getIndexOf(Object o) {
            return elements.indexOf(o.toString());
        }
    }
    
    private static final int CAPACITY_INCREMENT = 10;
    
    private boolean isVisibilityChanged;
    private int visibleElementsNumber;
    
    MapRegistry registry;
    private SortedMapVector elements;
    private AttributeComboBoxColumnModel myComboBoxColumnModel = null;
    private ChangeEvent changeEvent;
    public AttributeRegistryTableModel(MapRegistry registry) {
        super();
        isVisibilityChanged = false;
        this.registry = registry;
        visibleElementsNumber = 0;
        elements = new SortedMapVector();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return elements.size();
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
        case 0: return getKey(row);
        case 1: return getElement(row).isVisible();
        }
        return null;
    }

    public Comparable getKey(int index) {
        return elements.getKey(index);
    }

    public AttributeRegistryElement getElement(int index) {
        return (AttributeRegistryElement)elements.getValue(index);
    }

    public void registry(Attribute newAttribute) {
        String name = newAttribute.getName();
        String value = newAttribute.getValue();
        try{
            AttributeRegistryElement elem = getElement(name);
            elem.addValue(value);
        }
        catch(NoSuchElementException ex)
        {
            AttributeRegistryElement attributeRegistryElement = new AttributeRegistryElement();
            attributeRegistryElement.addValue(value);
            int index = elements.add(name, attributeRegistryElement);
            fireTableRowsInserted(index, index);
        };
    }
    
    public void clear() {
        int rowCount = getRowCount();
        if (rowCount > 0)
            fireTableRowsDeleted(0, rowCount-1);
        elements.clear();
        if(visibleElementsNumber != 0){
            isVisibilityChanged = true;
            visibleElementsNumber = 0;
        }
    }
    
    public boolean containsElement(String name) {
        return elements.containsKey(name);
    }
    
    private AttributeComboBoxColumnModel getCombinedModel() {
        if(myComboBoxColumnModel== null)
            myComboBoxColumnModel = new AttributeComboBoxColumnModel();
        myComboBoxColumnModel.setSelectedItem("");
        return myComboBoxColumnModel;
    }
    public ComboBoxModel getComboBoxModel()
    {
        return getCombinedModel();
    }


    public SortedListModel getListBoxModel()
    {
        return getCombinedModel();
    }

    /**
     * @param attrName
     * @return
     */
    public ComboBoxModel getDefaultComboBoxModel(Comparable attrName) {
        try{
            AttributeRegistryElement elem = getElement(attrName);
            return elem.getValues();
        }
        catch(NoSuchElementException ex)
        {
            return getComboBoxModel();
        }
    }

    public AttributeRegistryElement getElement(Comparable attrName) {
        AttributeRegistryElement elem = (AttributeRegistryElement)elements.getValue(attrName);
        return elem;
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
    public void setValueAt(Object o, int row, int col) {
        Boolean visible = (Boolean)o;
        AttributeRegistryElement element = getElement(row);
        if(! element.isVisible().equals(visible)){
            element.setVisible(visible);
            if(visible.booleanValue()){
                visibleElementsNumber++;
                isVisibilityChanged = true;
            }
            else{
                visibleElementsNumber--;
                isVisibilityChanged = true;
            }
            fireTableCellUpdated(row, col);
        }
    }
    
    public int getVisibleElementsNumber() {
        return visibleElementsNumber;
    }
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     * @param l the listener to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    public void fireVisibilityChanged(){
        if(isVisibilityChanged){
            fireStateChanged();
            isVisibilityChanged = false;
            registry.repaintMap();
        }
    }
    
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
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

}
