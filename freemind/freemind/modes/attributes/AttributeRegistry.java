/*
 * Created on 08.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.io.IOException;
import java.io.Writer;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableModel;

import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.controller.filter.util.SortedListModel;
import freemind.controller.filter.util.SortedMapVector;
import freemind.main.XMLElement;
import freemind.modes.MapRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.XMLElementAdapter;

/**
 * @author Dimitri Polivaev
 * 08.10.2005
 */
public class AttributeRegistry{
    private static interface Visitor{
        void visit(NodeAttributeTableModel model);
    }
    
    private static class AttributeRenamer implements Visitor{

        private Object oldName;
        private Object newName;

        public AttributeRenamer(Object oldName, Object newName) {
            super();
            this.newName = newName;
            this.oldName = oldName;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) {
            model.replaceName(oldName, newName);            
        }
        
    }
    
    private static class AttributeChanger implements Visitor{
        private Object name;
        private Object oldValue;
        private Object newValue;

        public AttributeChanger(Object name, Object oldValue, Object newValue) {
            super();
            this.name = name;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) {
            model.replaceValue(name, oldValue, newValue);            
        }
    }
    private static class AttributeRemover implements Visitor{
        private Object name;

        public AttributeRemover(Object name) {
            super();
            this.name = name;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) {
            model.removeAttribute(name);
         }
    }
    
    private static class AttributeValueRemover implements Visitor{

        private Object name;
        private Object value;

        public AttributeValueRemover(Object name, Object value) {
            super();
            this.name = name;
            this.value = value;
        }
        /* (non-Javadoc)
         * @see freemind.modes.attributes.AttributeRegistry.Visitor#visit(freemind.modes.attributes.ConcreteAttributeTableModel)
         */
        public void visit(NodeAttributeTableModel model) {
            model.removeValue(name, value);            
        }
    }
    private class Iterator{
        private Visitor visitor;
        Iterator(Visitor v){
            this.visitor = v;
        }
        void iterate(){
            MindMapNode root = (MindMapNode)registry.getMap().getRoot();
            iterate(root);
        }
        /**
         * @param root
         */
        private void iterate(MindMapNode node) {
            visitor.visit(node.getAttributes());            
            ListIterator iterator = node.childrenUnfolded();
            while(iterator.hasNext()){
                MindMapNode child = (MindMapNode)iterator.next();
                iterate(child);           
            }            
        }
    }
    
    /**
     * 
     */
    public AttributeRegistry() {
        super();
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
    
    private Boolean restrictionModel;
    private boolean isRestricted;
    static final int GLOBAL = -1;
    private static final int TABLE_FONT_SIZE = 12;
    private int fontSize = TABLE_FONT_SIZE;
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
        isRestricted = false;
        restrictionModel= Boolean.FALSE;
    }
    
    public Comparable getKey(int index) {
        return elements.getKey(index);
    }

    public AttributeRegistryElement getElement(int index) {
        return (AttributeRegistryElement)elements.getValue(index);
    }

    /**
     * @param name
     * @param oldElement
     */
    public int registry(String name, AttributeRegistryElement oldElement){
        if(name.equals(""))
            return -1;
        int index = elements.add(name, oldElement);
        myTableModel.fireTableRowsInserted(index, index);
        return index;
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
            int index = elements.add(name, attributeRegistryElement);
            myTableModel.fireTableRowsInserted(index, index);
        };
    }

    /**
     * @param oldO
     * @param newO
     */
    public void replaceAtributeName(Object oldO, Object newO) {
        String sOld = oldO.toString();
        String sNew = newO.toString();
        if(sOld.equals("") || sNew.equals(""))
            return;
        
        int iOld = elements.indexOf(sOld);
        AttributeRegistryElement oldElement = getElement(iOld);
        unregistry(iOld);
        int iNew = registry(sNew, oldElement);
        myTableModel.fireTableRowsUpdated(iNew, iNew);
        Visitor replacer = new AttributeRenamer(oldO, newO); 
        Iterator iterator = new Iterator(replacer);
        setVisibilityChanged();
        iterator.iterate();
    }

    private void unregistry(String name) {
        int index = elements.indexOf(name);
        if(getElement(index).isVisible())
            visibleElementsNumber--;
        unregistry(index);
    }

    private void unregistry(int index) {
        elements.remove(index);
        myTableModel.fireTableRowsDeleted(index, index);
    }

    /**
     * @param o
     */
    public void removeAtribute(Object o) {
        unregistry(o.toString());
        Visitor remover = new AttributeRemover(o); 
        Iterator iterator = new Iterator(remover);
        iterator.iterate();
        setVisibilityChanged();
        fireVisibilityChanged();
    }
    /**
     * @param oldO
     * @param newO
     */
    public void replaceAtributeValue(Comparable key, Object oldV, Object newV) {
        Visitor replacer = new AttributeChanger(key, oldV, newV); 
        Iterator iterator = new Iterator(replacer);
        iterator.iterate();
    }

    /**
     * @param o
     */
    public void removeAtributeValue(Comparable key, Object o) {
        Visitor remover = new AttributeValueRemover(key, o); 
        Iterator iterator = new Iterator(remover);
        iterator.iterate();
        setVisibilityChanged();
        fireVisibilityChanged();
    }
    public void clear() {
        myTableModel.fireTableRowsDeleted();
        elements.clear();
        if(visibleElementsNumber != 0){
            setVisibilityChanged();
            visibleElementsNumber = 0;
        }
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

    public void setVisibility(int row, Boolean visible) {
        AttributeRegistryElement element = getElement(row);
        if(! element.getVisibilityModel().equals(visible)){
            element.setVisibilityModel(visible);
            setVisibilityChanged();
            myTableModel.fireVisibilityUpdated(row); 
        }
    }

    /**
     * @return
     */
    public TableModel getTableModel() {
        return myTableModel;
    }

    /**
     * @param i
     * @param value
     */
    public void setRestriction(int row, Boolean value) {
        if(row == GLOBAL){
            restrictionModel = value;   
            setVisibilityChanged();
        }
        else{
            getElement(row).setRestrictionModel(value);
        }
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
        getElement(row).setRestricted(b);
        
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
    }

    /**
     * 
     */
    public void setVisibilityChanged() {
        isVisibilityChanged = true;        
    }

    /**
     * @return Returns the fontSize.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param size
     */
    public void setFontSize(int size) {
        if(fontSize != size){
            fontSize = size;
            setVisibilityChanged();
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
    public void setVisible(String attributeName, boolean b){
        setVisible(indexOf(attributeName), b);        
    }

    /**
     * @param i
     * @param b
     */
    private void setVisible(int row, boolean isVisible) {
        AttributeRegistryElement element = getElement(row);
        if(element.isVisible() == isVisible)
            return;
        if(isVisible){
            visibleElementsNumber++;
        }
        else{
            visibleElementsNumber--;
        }
        element.setVisible(isVisible);        
    }

    /**
     * @param s
     */
    public void registry(String s) {
        if (s != "")
            registry(s, new AttributeRegistryElement(this, s));
    }

    public void resetChanges(){
        if(isVisibilityChanged == false)
            return;
        restrictionModel = Boolean.valueOf(isRestricted);
        for(int i = 0; i < elements.size(); i++)
            getElement(i).resetChanges();
        isVisibilityChanged = false;
    }
    
    public void applyChanges(){
        if(isVisibilityChanged == false)
            return;
        isRestricted = restrictionModel.booleanValue(); 
        visibleElementsNumber = 0;
        for(int i = 0; i < elements.size(); i++){
            AttributeRegistryElement element = getElement(i);
            element.applyChanges();
            if(element.isVisible())
                visibleElementsNumber ++;
        }
        fireVisibilityChanged();
        isVisibilityChanged = false;
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
}
