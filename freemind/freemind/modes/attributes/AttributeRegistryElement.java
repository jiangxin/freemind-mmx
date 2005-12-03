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
    class RegisteredAttributeValues extends SortedComboBoxModel{
        
        public void remove(Object element) {
            super.remove(element);
            registry.removeAtributeValue(key, element);
        }
        public void replace(Object oldO, Object newO) {
           super.replace(oldO, newO);
           registry.replaceAtributeValue(key, oldO, newO);
        }
}
    private Comparable key;
    private RegisteredAttributeValues values;
    private AttributeRegistry registry;
    private boolean isVisible;
    private boolean isRestricted;
    private Boolean visibilityModel;
    private Boolean restrictionModel;
    public AttributeRegistryElement(AttributeRegistry registry, Comparable key) {
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
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        visibilityModel = Boolean.valueOf(isVisible);
    }
    public SortedComboBoxModel getValues() {
        return values;
    }
    public void addValue(String s) {
        values.add(s);
    }
    public void removeAllValues() {
        values.clear();
    }
    public void removeValue(String s) {
        values.remove(s);
    }
    public boolean isRestricted() {
        return isRestricted;
    }
    public void setRestricted(boolean isRestricted) {
        this.isRestricted = isRestricted;
        restrictionModel = Boolean.valueOf(isRestricted);
    }
    public Comparable getKey() {
        return key;
    }
    public void setKey(Comparable key) {
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
    
    void resetChanges(){
        visibilityModel = Boolean.valueOf(isVisible);
        restrictionModel = Boolean.valueOf(isRestricted);
    }
    void applyChanges(){
        isVisible = visibilityModel.booleanValue(); 
        isRestricted = restrictionModel.booleanValue(); 
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
}
