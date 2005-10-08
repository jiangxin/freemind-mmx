/*
 * Created on 08.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.NoSuchElementException;

import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;

import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.SortedMapVector;
import freemind.modes.MapRegistry;

/**
 * @author Dimitri Polivaev
 * 08.10.2005
 */
public class AttributeRegistry{

    /**
     * 
     */
    public AttributeRegistry() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final int CAPACITY_INCREMENT = 10;
    protected boolean isVisibilityChanged;
    protected int visibleElementsNumber;
    protected MapRegistry registry;
    protected SortedMapVector elements;
    private ChangeEvent changeEvent;
    private AttributeRegistryComboBoxColumnModel myComboBoxColumnModel = null;
    private AttributeRegistryTableModel myTableModel = null;
    private EventListenerList listenerList = null; 

    public int size() {
        return elements.size();
    }

    public AttributeRegistry(MapRegistry registry) {
        super();
        listenerList = new EventListenerList();
        isVisibilityChanged = false;
        this.registry = registry;
        visibleElementsNumber = 0;
        elements = new SortedMapVector();
        myTableModel = new AttributeRegistryTableModel(this);
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
            myTableModel.fireTableRowsInserted(index, index);
        };
    }

    public void clear() {
        myTableModel.fireTableRowsDeleted();
        elements.clear();
        if(visibleElementsNumber != 0){
            isVisibilityChanged = true;
            visibleElementsNumber = 0;
        }
    }

    public boolean containsElement(String name) {
        return elements.containsKey(name);
    }

    private AttributeRegistryComboBoxColumnModel getCombinedModel() {
        if(myComboBoxColumnModel== null)
            myComboBoxColumnModel = new AttributeRegistryComboBoxColumnModel(this);
        myComboBoxColumnModel.setSelectedItem("");
        return myComboBoxColumnModel;
    }

    public ComboBoxModel getComboBoxModel() {
        return getCombinedModel();
    }

    public SortedListModel getListBoxModel() {
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

    public void fireVisibilityChanged() {
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

    /**
     * @param string
     * @return
     */
    public int indexOf(String string) {
        return elements.indexOf(string);
    }

    public void setVisible(int row, Boolean visible) {
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
            myTableModel.fireVisibilityUpdated(row, 1); 
        }
    }

    /**
     * @return
     */
    public TableModel getTableModel() {
        return myTableModel;
    }
}
