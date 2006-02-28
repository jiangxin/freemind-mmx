/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.XMLElementAdapter;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class NodeAttributeTableModel extends AbstractTableModel implements AttributeTableModel{
    private static boolean SHOW_ATTRIBUTE_ICON = 
        Tools.safeEquals("true", Resources.getInstance().getProperty("el__show_icon_for_attributes"));
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

    public Object getName(int row) {
        Attribute attr = (Attribute)attributes.get(row);
        return attr.getName();
    }

    public Object getValue(int row) {
        Attribute attr = (Attribute)attributes.get(row);
        return attr.getValue();
    }
    
    public AttributeController getAttributeController(){
        return node.getMap().getRegistry().getModeController().getAttributeController();
    }

    public void setValueAt(Object o, int row, int col) {
        getAttributeController().performSetValueAt(this, o, row, col);
    }
    
    public void enableStateIcon() {
        if(SHOW_ATTRIBUTE_ICON && getRowCount() == 1){
            if (noteIcon == null) {
                noteIcon = new ImageIcon(Resources.getInstance().getResource("images/showAttributes.gif"));
            }
            node.setStateIcon(STATE_ICON, noteIcon);
        }
    }
    public void disableStateIcon() {
        if(SHOW_ATTRIBUTE_ICON && getRowCount() == 0){
            node.setStateIcon(STATE_ICON, null);
        }
    }
    public void insertRow(int index, String name, String value) {
        getAttributeController().performInsertRow(this, index, name, value);
    }
    
    public void addRowNoUndo(Attribute newAttribute) {
        allocateAttributes(CAPACITY_INCREMENT);
        int index = getRowCount();
        node.getMap().getRegistry().getAttributes().registry(newAttribute);
        attributes.add(newAttribute);
        enableStateIcon();
        fireTableRowsInserted(index, index);
    }
    
    public void setName(int row, Object newName) {
        Attribute attr = (Attribute)attributes.get(row);
        attr.setName(newName.toString());
        fireTableRowsUpdated(row, row);
    }
    public void setValue(int row, Object newValue) {
        Attribute attr = (Attribute)attributes.get(row);
        attr.setValue(newValue.toString());
        fireTableRowsUpdated(row, row);
    }
    
    public Object removeRow(int index) {
        Object o = getAttributes().elementAt(index);
        getAttributeController().performRemoveRow(this, index);
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
        getAttributeController().performSetColumnWidth(this, col, width);
    }

    public AttributeTableLayoutModel getLayout() {
        if(layout == null)
            layout = new AttributeTableLayoutModel();
        return layout;
    }

    public Vector getAttributes() {
        allocateAttributes(NodeAttributeTableModel.CAPACITY_INCREMENT);
        return attributes;
    }

 }
