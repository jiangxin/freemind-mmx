/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.main.XMLElement;
import freemind.modes.XMLElementAdapter;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class AttributeRegistryElement {
    private class RegisteredAttributeValues extends SortedComboBoxModel{
        
        public AttributeRegistry getRegistry(){
            return registry;
        }
        public String getKey() {
            return key;
        }
        public void add(Object element) {
            registry.getAttributeController().performRegistryAttributeValue(getKey(), element.toString());
        }
        public void _add(Object element) {
            super.add(element);
        }
        public void remove(Object element) {
            registry.getAttributeController().performRemoveAttributeValue(getKey(), element.toString());

        }
        public void _remove(Object element) {
            super.remove(element);
        }
        public void replace(Object oldO, Object newO) {
            registry.getAttributeController().performReplaceAttributeValue(getKey(), oldO.toString(), newO.toString());
        }
        public void _replace(Object oldO, Object newO) {
           super.replace(oldO, newO);
        }
}
    private String key;
    private RegisteredAttributeValues values;
    private AttributeRegistry registry;
    private boolean isVisible;
    private boolean isRestricted;
    private Boolean visibilityModel;
    private Boolean restrictionModel;
    public AttributeRegistryElement(AttributeRegistry registry, String key) {
        super();
        this.key = key;
        this.registry = registry;
        values = new RegisteredAttributeValues();
        isVisible = false;
        visibilityModel = new Boolean(isVisible);
        isRestricted = false;
        restrictionModel = new Boolean(isRestricted);
    }
    public boolean isVisible() {
        return isVisible;
    }
    public SortedComboBoxModel getValues() {
        return values;
    }
    public void addValue(String s) {
        values._add(s);
        registry.fireAttributesChanged();
    }
    public void removeAllValues() {
        values.clear();
        registry.fireAttributesChanged();
    }
    public void removeValue(String s) {
        values._remove(s);
        registry.fireAttributesChanged();
    }
    public boolean isRestricted() {
        return isRestricted;
    }
    public Comparable getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * @return
     */
    public XMLElement save() {
        XMLElement element = new XMLElement();
        if(isVisible()){
            element.setAttribute("VISIBLE", "true");
        }
        if(isRestricted()){
            element.setAttribute("RESTRICTED", "true");
        }
        for (int i = 0; i < values.getSize(); i++){
            XMLElement value = new XMLElement();
            value.setName(XMLElementAdapter.XML_NODE_REGISTERED_ATTRIBUTE_VALUE);
            value.setAttribute("VALUE", values.getElementAt(i).toString());
            element.addChild(value);
        }
        element.setName(XMLElementAdapter.XML_NODE_REGISTERED_ATTRIBUTE_NAME);
        element.setAttribute("NAME", key.toString());
        return element;
    }
    
    public void setVisibility(boolean isVisible){
        if(this.isVisible != isVisible)
        {
            this.isVisible = isVisible; 
            visibilityModel= Boolean.valueOf(isVisible);
            if(isVisible)
                registry.incrementVisibleElementsNumber();
            else
                registry.decrementVisibleElementsNumber();
            registry.fireAttributeLayoutChanged();
        }
    }
    public void setRestriction(boolean isRestricted){
        this.isRestricted = isRestricted; 
        restrictionModel = Boolean.valueOf(isRestricted);
        registry.fireAttributesChanged();
    }
    Boolean getRestriction() {
        return restrictionModel;
    }
    void setRestrictionModel(Boolean restrictionModel) {
        this.restrictionModel = restrictionModel;
    }
    Boolean getVisibilityModel() {
        return visibilityModel;
    }
    void setVisibilityModel(Boolean visibilityModel) {
        this.visibilityModel = visibilityModel;
    }
    public void replaceValue(String oldValue, String newValue) {
        values._replace(oldValue, newValue);        
        registry.fireAttributesChanged();
    }
    
}
