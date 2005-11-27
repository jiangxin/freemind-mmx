/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.XMLElementAdapter;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class NodeAttributeTableModel extends AbstractTableModel implements AttributeTableModel{
    private MindMapNode node;
    private Vector attributes = null;
    private AttributeTableLayoutModel layout = null;
    private static final int CAPACITY_INCREMENT = 10;
    static private ImageIcon noteIcon = null;
    private static final String STATE_ICON = "AttributeExist";
    public NodeAttributeTableModel(MindMapNode node, int size) {
        super();
        allocateAttributes(size);
        this.node = node;
    }
    
    private void allocateAttributes(int size) {
        if(attributes == null && size > 0)
        attributes = new Vector(size, CAPACITY_INCREMENT);
    }

    public NodeAttributeTableModel(MindMapNode node) {
        this(node, 0);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return attributes == null ? 0 :attributes.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if (attributes != null){
            switch(col){
            case 0: 
                return getName(row);
            case 1: 
                return getValue(row);
            }
        }
        return null;
    }

    private Object getName(int row) {
        Attribute attr = (Attribute)attributes.get(row);
        return attr.getName();
    }

    private Object getValue(int row) {
        Attribute attr = (Attribute)attributes.get(row);
        return attr.getValue();
    }

    public void setValueAt(Object o, int row, int col) {
        Attribute attribute = (Attribute)attributes.get(row);
        String s = o.toString();
        AttributeRegistry attributes = node.getMap().getRegistry().getAttributes();
        switch(col){
        case 0: 
            if(attribute.getName().equals(s))
                return;
            attribute.setName(s);
            try{
                AttributeRegistryElement element = attributes.getElement(s);
                String value = getValueAt(row, 1).toString(); 
                int index = element.getValues().getIndexOf(value);
                if(index == -1){
                    setValueAt(element.getValues().firstElement(), row, 1);
                }
            }
            catch(NoSuchElementException ex)
            {
                attributes.registry(attribute);                
            }
            break;
        case 1: 
            if(attribute.getValue().equals(s))
                return;
            attribute.setValue(s);
            attributes.registry(attribute);
            break;
        }
        fireTableCellUpdated(row, col);
    }
    
    private void enableStateIcon() {
        if(getRowCount() == 1){
            if (noteIcon == null) {
                noteIcon = new ImageIcon(Resources.getInstance().getResource("images/showAttributes.gif"));
            }
            node.setStateIcon(STATE_ICON, noteIcon);
        }
    }
    private void disableStateIcon() {
        if(getRowCount() == 0){
            node.setStateIcon(STATE_ICON, null);
        }
    }
    public void insertRow(int index, Attribute newAttribute) {
        allocateAttributes(CAPACITY_INCREMENT);
        node.getMap().getRegistry().getAttributes().registry(newAttribute);
        attributes.add(index, newAttribute);
        enableStateIcon();
        fireTableRowsInserted(index, index);
    }
    public void addRow(Attribute newAttribute) {
        allocateAttributes(CAPACITY_INCREMENT);
        int index = getRowCount();
        node.getMap().getRegistry().getAttributes().registry(newAttribute);
        attributes.add(newAttribute);
        enableStateIcon();
        fireTableRowsInserted(index, index);
    }
    
    void replaceName(Object oldName, Object newName){
        for(int i = 0; i < getRowCount(); i++){
            if(getName(i).equals(oldName))
                setName(i, newName);            
        }
    }

    void replaceValue(Object name, Object oldValue, Object newValue){
        for(int i = 0; i < getRowCount(); i++){
            if(getName(i).equals(name) && getValue(i).equals(oldValue))
                setValue(i, newValue);            
        }
    }
    private void setName(int row, Object newName) {
        Attribute attr = (Attribute)attributes.get(row);
        attr.setName(newName.toString());
        fireTableRowsUpdated(row, row);
    }
    private void setValue(int row, Object newValue) {
        Attribute attr = (Attribute)attributes.get(row);
        attr.setValue(newValue.toString());
        fireTableRowsUpdated(row, row);
    }
    
    void removeAttribute(Object name){
        for(int i = 0; i < getRowCount(); i++){
            if(getName(i).equals(name))
                removeRow(i);            
        }
    }

    void removeValue(Object name, Object value){
        for(int i = 0; i < getRowCount(); i++){
            if(getName(i).equals(name) && getValue(i).equals(value))
                removeRow(i);            
        }
    }
    
    public Object removeRow(int index) {
        Object o = attributes.remove(index);
        disableStateIcon();
        fireTableRowsDeleted(index, index);
        return o;
    }
    public void  save(XMLElement node) {
        saveLayout(node);
        if (attributes != null){
            for (int i = 0; i < attributes.size(); i++) {
                saveAttribute(node, i);
            }
        }
    }
    
    private void saveAttribute(XMLElement node, int i) {
        XMLElement attributeElement = new XMLElement();
        attributeElement.setName(XMLElementAdapter.XML_NODE_ATTRIBUTE);
        Attribute attr = (Attribute) attributes.get(i);
        attributeElement.setAttribute("NAME", attr.getName());
        attributeElement.setAttribute("VALUE", attr.getValue());
        node.addChild(attributeElement);
    }

    private void saveLayout(XMLElement node) {
        if(layout != null)
        {
            XMLElement attributeElement = null;
            if(! layout.getViewType().equals(AttributeTableLayoutModel.SHOW_REDUCED)){
                attributeElement = initializeNodeAttributeLayoutXMLElement(attributeElement);
                attributeElement.setAttribute("VIEWTYPE", layout.getViewType());
            }
            if(layout.getColumnWidth(0)!= AttributeTableLayoutModel.DEFAULT_COLUMN_WIDTH){
                attributeElement = initializeNodeAttributeLayoutXMLElement(attributeElement);
                attributeElement.setIntAttribute("NAME_WIDTH", getColumnWidth(0));
            }
            if(layout.getColumnWidth(1)!= AttributeTableLayoutModel.DEFAULT_COLUMN_WIDTH){
                attributeElement = initializeNodeAttributeLayoutXMLElement(attributeElement);
                attributeElement.setIntAttribute("VALUE_WIDTH", layout.getColumnWidth(1));
            }
            if(attributeElement != null){
                node.addChild(attributeElement);
            }
        }
   }

    private XMLElement initializeNodeAttributeLayoutXMLElement(XMLElement attributeElement) {
        if(attributeElement == null){
            attributeElement = new XMLElement();
            attributeElement.setName(XMLElementAdapter.XML_NODE_ATTRIBUTE_LAYOUT);
        }
        return attributeElement;
    }

    public MindMapNode getNode() {
        return node;
    }

    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#get(int)
     */
    public Attribute getAttribute(int row) {
        return (Attribute)attributes.get(row);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int arg0, int arg1) {
        return ! node.getMap().isReadOnly();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return "";
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col) {
        return Object.class;
    }

    public int getColumnWidth(int col) {
        return getLayout().getColumnWidth(col);
    }
    public void setColumnWidth(int col, int width) {
        getLayout().setColumnWidth(col, width);
    }
       
    public String getViewType() {
        return  getLayout().getViewType();
    }
    
    public void setViewType(String viewType) {
        getLayout().setViewType(viewType);
        node.getMap().nodeChanged(node);
    }
    
    public AttributeTableLayoutModel getLayout() {
        if(layout == null)
            layout = new AttributeTableLayoutModel();
        return layout;
    }
 }
