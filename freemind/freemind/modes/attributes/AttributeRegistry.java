/*
 * Created on 08.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.Writer;
import java.util.NoSuchElementException;

import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.SortedMapVector;
import freemind.main.XMLElement;
import freemind.modes.MapRegistry;
import freemind.modes.XMLElementAdapter;
import freemind.view.mindmapview.attributeview.AttributeView;

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
    }

    private static final int CAPACITY_INCREMENT = 10;
    protected boolean isAttributeLayoutChanged;
    protected int visibleElementsNumber;
    protected MapRegistry registry;
    protected SortedMapVector elements;
    private AttributeRegistryComboBoxColumnModel myComboBoxColumnModel = null;
    private AttributeRegistryTableModel myTableModel = null;
    private EventListenerList listenerList = null; 
    
    private Boolean restrictionModel;
    private boolean isRestricted;
    static public final int GLOBAL = -1;
    private static final int TABLE_FONT_SIZE = 12;
    private int fontSize = TABLE_FONT_SIZE;
    private boolean isAttributeLayoutFired = false;
    private ChangeEvent changeEvent;
    private ChangeEvent attributesEvent;
    private String attributeViewType;
    public int size() {
        return elements.size();
    }

    public AttributeRegistry(MapRegistry registry) {
        super();
        listenerList = new EventListenerList();
        isAttributeLayoutChanged = false;
        this.registry = registry;
        visibleElementsNumber = 0;
        elements = new SortedMapVector();
        myTableModel = new AttributeRegistryTableModel(this);
        isRestricted = false;
        restrictionModel= Boolean.FALSE;
        attributeViewType = AttributeTableLayoutModel.SHOW_SELECTED;
    }
    
    public Comparable getKey(int index) {
        return elements.getKey(index);
    }

    public AttributeRegistryElement getElement(int index) {
        return (AttributeRegistryElement)elements.getValue(index);
    }

    public AttributeController getAttributeController() {
        return registry.getModeController().getAttributeController();
    }
    /**
     * @param o
     */
    void removeAtribute(Object o) {
        getAttributeController().performRemoveAttribute(o.toString());
    }
    
    public boolean containsElement(String name) {
        return elements.containsKey(name);
    }

    private AttributeRegistryComboBoxColumnModel getCombinedModel() {
        if(myComboBoxColumnModel== null)
            myComboBoxColumnModel = new AttributeRegistryComboBoxColumnModel(this);
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

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    public void addAttributesListener(AttributesListener l) {
        listenerList.add(AttributesListener.class, l);
    }

    public void removeAttributesListener(AttributesListener l) {
        listenerList.remove(AttributesListener.class, l);
    }

    public void fireAttributeLayoutChanged() {
        if(isAttributeLayoutFired  == false){
            isAttributeLayoutFired  = true;
            EventQueue.invokeLater(new Runnable(){
                public void run() {
                    if(isAttributeLayoutFired){
                        fireStateChanged();
                        isAttributeLayoutFired = false;
                    }
                }                
            });
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

    protected void fireAttributesChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==AttributesListener.class) {
                // Lazily create the event:
                if (attributesEvent == null)
                    attributesEvent = new ChangeEvent(this);
                ((AttributesListener)listeners[i+1]).attributesChanged(changeEvent);
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

    public void setVisibilityModel(int row, Boolean visible) {
        AttributeRegistryElement element = getElement(row);
        if(! element.getVisibilityModel().equals(visible)){
            element.setVisibilityModel(visible);
            setAttributeLayoutChanged();
            myTableModel.fireVisibilityUpdated(row); 
        }
    }

    /**
     * @return
     */
    public AttributeRegistryTableModel getTableModel() {
        return myTableModel;
    }

    /**
     * @param i
     * @param value
     */
    public void setRestrictionModel(int row, Boolean value) {
        if(row == GLOBAL){
            restrictionModel = value;   
        }
        else{
            getElement(row).setRestrictionModel(value);
        }
        setAttributeLayoutChanged();
        myTableModel.fireRestrictionsUpdated(row);
    }
    
    Boolean getRestriction(int row){
        if(row == GLOBAL){
            return restrictionModel;   
           }
       else{
           return getElement(row).getRestriction();
       }
    }
    
    public boolean isRestricted(String s){
        return getRestriction(indexOf(s)).booleanValue();
    }

    public void setRestricted(String s, boolean b){
        setRestricted(indexOf(s),b);
    }

    /**
     * @param i
     * @param b
     */
    private void setRestricted(int row, boolean b) {
        getElement(row).setRestriction(b);        
    }

    /**
     * @return
     */
    public SortedListModel getValues(int row) {
        if(row == GLOBAL){
            return getListBoxModel();
        }
        return  getElement(row).getValues();
    }

    /**
     * @return
     */
    public boolean isRestricted() {
        return isRestricted;
    }
    /**
     * @param b
     */
    public void setRestricted(boolean b) {
        isRestricted = b; 
        restrictionModel = Boolean.valueOf(isRestricted);
        fireAttributesChanged();
    }


    /**
     * @return Returns the fontSize.
     */
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int size) {
        if(fontSize != size){
            fontSize = size;
            fireAttributeLayoutChanged();
        }
    }

    /**
     * @param fileout
     * @throws IOException
     */
    public void save(Writer fileout) throws IOException{
        XMLElement attributeRegistry = new XMLElement();
        boolean toBeSaved = false;
        if(isRestricted()){
            attributeRegistry.setAttribute("RESTRICTED", "true");
            toBeSaved = true;
        }
        if(getFontSize() != TABLE_FONT_SIZE){
            attributeRegistry.setIntAttribute("FONT_SIZE", getFontSize());
            toBeSaved = true;
        }
        for (int i = 0; i < size(); i++){
            XMLElement attributeData = getElement(i).save();
            attributeRegistry.addChild(attributeData);
            toBeSaved = true;
        }
        if(toBeSaved){
            attributeRegistry.setName(XMLElementAdapter.XML_NODE_ATTRIBUTE_REGISTRY);
            attributeRegistry.write(fileout);
        }
    }

    /**
     * @param attributeName
     * @param b
     */
     public void resetChanges(){
        if(isAttributeLayoutChanged == false)
            return;
        restrictionModel = Boolean.valueOf(isRestricted);
        for(int i = 0; i < elements.size(); i++) {
            AttributeRegistryElement element = getElement(i);
            element.setVisibilityModel(Boolean.valueOf(element.isVisible()));
            element.setRestrictionModel(Boolean.valueOf(element.isRestricted()));
        }
        isAttributeLayoutChanged = false;
    }
    
    public void applyChanges(){
        if(isAttributeLayoutChanged == false)
            return;
        getAttributeController().performSetRestriction(GLOBAL, restrictionModel.booleanValue()); 
        for(int i = 0; i < elements.size(); i++){
            AttributeRegistryElement element = getElement(i);
            getAttributeController().performSetVisibility(i, element.getVisibilityModel().booleanValue());
            getAttributeController().performSetRestriction(i, element.getRestriction().booleanValue());
        }
        isAttributeLayoutChanged = false;
    }

    public boolean exist(String attributeName, Object element) {
        int index = indexOf(attributeName);
        if(index == -1){
            return false;
        }
        SortedComboBoxModel values = getElement(index).getValues();
        for(int i = 0; i < values.getSize(); i++){
            if(element.equals(values.getElementAt(i))){
                return true;
            }
        }
        return false;        
    }

    public SortedMapVector getElements() {
        return elements;
    }

    public void decrementVisibleElementsNumber() {
        visibleElementsNumber--;        
    }

    public void incrementVisibleElementsNumber() {
        visibleElementsNumber++;        
    }

    public void registry(Attribute newAttribute) {
        String name = newAttribute.getName();
        if(name.equals(""))
            return;
        String value = newAttribute.getValue();
        try{
            AttributeRegistryElement elem = getElement(name);
            elem.addValue(value);
        }
        catch(NoSuchElementException ex)
        {
            AttributeRegistryElement attributeRegistryElement = new AttributeRegistryElement(this, name);
            attributeRegistryElement.addValue(value);
            int index = getElements().add(name, attributeRegistryElement);
            getTableModel().fireTableRowsInserted(index, index);
        };        
        fireAttributesChanged();
    }
    public void registry(String name) {
            AttributeRegistryElement attributeRegistryElement = new AttributeRegistryElement(this, name);
            int index = getElements().add(name, attributeRegistryElement);
            getTableModel().fireTableRowsInserted(index, index);
    }

    public void setAttributeLayoutChanged() {
        isAttributeLayoutChanged = true;        
    }

    public void unregistry(String name) {
        int index = elements.indexOf(name);
        if(getElement(index).isVisible())
            decrementVisibleElementsNumber();
        elements.remove(index );
        getTableModel().fireTableRowsDeleted(index, index); 
        fireAttributesChanged(); 
    }

    public String getAttributeViewType() {
        return attributeViewType;
    }

    public void setAttributeViewType(String attributeViewType) {
        this.attributeViewType = attributeViewType;
        fireAttributeLayoutChanged();
    }
}
