/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import freemind.controller.filter.util.SortedComboBoxModel;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class AttributeRegistryElement {
    private SortedComboBoxModel values;
    Boolean isVisible;
    public AttributeRegistryElement() {
        super();
        values = new SortedComboBoxModel();
        isVisible = new Boolean(false);
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
        if(! s.equals(""))
            values.add(s);
    }
    public void removeAllValues() {
        values.clear();
    }
    public void removeValue(String s) {
        values.removeElement(s);
    }
}
