/*
 * Created on 14.05.2005
 *
 */
package freemind.controller.filter.util;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author dimitri
 * 14.05.2005
 */
public class ExtendedComboBoxModel extends DefaultComboBoxModel {

    private class ExtensionDataListener implements ListDataListener{

        /* (non-Javadoc)
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        public void intervalAdded(ListDataEvent e) {
            int size = getOwnSize();
            fireIntervalAdded(getModel(), size + e.getIndex0(), size + e.getIndex1());            
        }

        /* (non-Javadoc)
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        public void intervalRemoved(ListDataEvent e) {
            int size = getOwnSize();
            fireIntervalRemoved(getModel(), size + e.getIndex0(), size + e.getIndex1());                        
        }

        /* (non-Javadoc)
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        public void contentsChanged(ListDataEvent e) {
            int size = getOwnSize();
            fireContentsChanged(getModel(), size + e.getIndex0(), size + e.getIndex1());                                    
        }
        
    }
    private SortedListModel extension = null;
    private ExtensionDataListener extensionDataListener = new ExtensionDataListener();
 
    /**
     * @param sortedListModel
     */
    public void setExtensionList(final SortedListModel sortedListModel) {
        final int ownSize = getOwnSize();
        {
            if (extension != null){
                extension.removeListDataListener(extensionDataListener);
                final int extensionSize = getExtensionSize();
                if (extensionSize > 0){
                    fireIntervalRemoved(this, ownSize, ownSize + extensionSize - 1);
                }
            }
        }
        {
            extension = sortedListModel;
            final int extensionSize = getExtensionSize();
            if (extensionSize > 0){
                fireIntervalAdded(this, ownSize, ownSize + extensionSize - 1);
            }
            if (extension != null){
                extension.addListDataListener(extensionDataListener);
            }
        }
    }

    public ExtendedComboBoxModel() {
        super();
    }

    public ExtendedComboBoxModel(Object[] o) {
        super(o);
    }

    public ExtendedComboBoxModel(Vector v) {
        super(v);
    }

    public Object getElementAt(int i) {
        int s = getOwnSize();
        if (i < s || extension == null)
            return super.getElementAt(i);
        return extension.getElementAt(i - s);
    }
    
    public int getSize() {
        return getOwnSize() + getExtensionSize();
    }
    
    private int getExtensionSize() {
        return extension != null ? extension.getSize() : 0;
    }

    /**
     * @return
     */
    private int getOwnSize() {
        return super.getSize();
    }
    
    private ExtendedComboBoxModel getModel(){
        return this;
    }

    public void insertElementAt(Object o, int i) {
    	super.insertElementAt(o, Math.min(getOwnSize(), i));
    }
    
    public void removeAllElements() {
        super.removeAllElements();
        if (extension != null){
            extension.clear();
        }
    }
    
    public void removeElement(Object o) {
    	super.removeElement(o);
    }
    public void removeElementAt(int i) {
        if (i < getOwnSize())
            super.removeElementAt(i);
    }
    
    public void addSortedElement(Object o){
        if (extension != null && ! extension.contains(o)){
            extension.add(o);
        }
    }
    
    public int getIndexOf(Object o) {
        int idx =  super.getIndexOf(o);
        if (idx > -1 || extension == null)
        {
            return idx;
        }
        int extIdx = extension.getIndexOf(o);
        return extIdx>-1 ? extIdx + getOwnSize() : -1;
    }
}
