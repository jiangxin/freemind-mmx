/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.util;

import javax.swing.ComboBoxModel;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
public class SortedComboBoxModel extends SortedMapListModel implements SortedListModel, ComboBoxModel {
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
}
