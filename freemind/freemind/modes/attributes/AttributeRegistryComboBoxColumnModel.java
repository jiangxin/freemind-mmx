/*
 * Created on 08.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import freemind.controller.filter.util.SortedListModel;


class AttributeRegistryComboBoxColumnModel extends AbstractListModel implements TableModelListener, ComboBoxModel, SortedListModel{
    private final AttributeRegistry model;
    public AttributeRegistryComboBoxColumnModel(AttributeRegistry model) {
        super();
        this.model = model;
        model.getTableModel().addTableModelListener(this);
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
        return model.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int row) {
        return model.getKey(row);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        if(e.getType() == TableModelEvent.DELETE)
        {
            fireIntervalRemoved(this, e.getFirstRow(), e.getLastRow());
            return;
        }
        if(e.getType() == TableModelEvent.UPDATE)
        {
            fireContentsChanged(this, e.getFirstRow(), e.getLastRow());
            return;
        }
        if(e.getType() == TableModelEvent.INSERT)
        {
            fireIntervalAdded(this, e.getFirstRow(), e.getLastRow());
            return;
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
        return model.containsElement(o.toString()); 
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.util.SortedListModel#add(java.lang.Object)
     */
    public void add(Object o) {
        String s = o.toString();
        model.getAttributeController().performRegistryAttribute(s);      
    }


    /* (non-Javadoc)
     * @see freemind.controller.filter.util.SortedListModel#replace(java.lang.Object, java.lang.Object)
     */
    public void replace(Object oldO, Object newO) {
        model.getAttributeController().performReplaceAtributeName(oldO.toString(), newO.toString());
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.util.SortedListModel#delete(java.lang.Object)
     */
    public void remove(Object o) {
        model.removeAtribute(o);              
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.util.SortedListModel#getIndexOf(java.lang.Object)
     */
    public int getIndexOf(Object o) {
        return this.model.indexOf(o.toString());
    }
}