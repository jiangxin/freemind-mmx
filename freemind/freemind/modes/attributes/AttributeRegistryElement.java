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
    class RegisteredAttributeValues extends SortedComboBoxModel {
        
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
    private Boolean isVisible;
    private Boolean isRestricted;
    public AttributeRegistryElement(AttributeRegistry registry, Comparable key) {
        super();
        this.key = key;
        this.registry = registry;
        values = new RegisteredAttributeValues();
        isVisible = new Boolean(false);
        isRestricted = new Boolean(false);
    }
    public Boolean isVisible() {
        return isVisible;
    }
    public void setVisible(Boolean isVisible) {
        this.isVisible = isVisible;
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
    public Boolean isRestricted() {
        return isRestricted;
    }
    public void setRestricted(Boolean isRestricted) {
        this.isRestricted = isRestricted;
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
        boolean mustSave = false;
        XMLElement element = new XMLElement();
        if(isVisible().booleanValue()){
            element.setAttribute("VISIBLE", "true");
            mustSave = true;
        }
        if(isRestricted().booleanValue()){
            mustSave = true;
            for (int i = 0; i < values.getSize(); i++){
                XMLElement value = new XMLElement();
                value.setName(XMLElementAdapter.XML_NODE_REGISTERED_ATTRIBUTE_VALUE);
                value.setAttribute("VALUE", values.getElementAt(i).toString());
                element.addChild(value);
            }
        }
        if(mustSave){
            element.setName(XMLElementAdapter.XML_NODE_REGISTERED_ATTRIBUTE_NAME);
            element.setAttribute("NAME", key.toString());
            return element;
        }
        return null;
    }
}
