/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import java.util.Vector;

import javax.swing.event.ChangeEvent;

import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
class ReducedAttributeTableModelDecorator extends AttributeTableModelDecoratorAdapter{
    private Vector index = null;
    private int visibleRowCount;
    ReducedAttributeTableModelDecorator(
            NodeAttributeTableModel nodeAttributeModel,
            AttributeRegistry attributeRegistry) {
        super(nodeAttributeModel, attributeRegistry);
        rebuildTableModel();
    }

    private Vector getIndex() {
        if(index == null && this.attributeRegistry.getVisibleElementsNumber() > 0)
            index = new Vector(this.nodeAttributeModel.getRowCount(), 10);
        return index;
    }
    public int getRowCount() {
        return visibleRowCount;
    }
    public Object getValueAt(int row, int col) {
        return nodeAttributeModel.getValueAt(calcRow(row), col);
    }
    
    public boolean isCellEditable(int row, int col) {
        if(nodeAttributeModel.isCellEditable(row, col)){
            return col == 1;
        }
        return false;
    }

    private int calcRow(int row){
        return ((Integer) index.get(row)).intValue();
    }
    public void setValueAt(Object o, int row, int col) {
        nodeAttributeModel.setValueAt(o, calcRow(row), col);
    	fireTableCellUpdated(row,col);
    }
    
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#insertRow(int, freemind.modes.attributes.Attribute)
     */
    public void insertRow(int index, Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#addRow(freemind.modes.attributes.Attribute)
     */
    public void addRow(Attribute newAttribute) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see freemind.modes.attributes.AttributeTableModel#removeRow(int)
     */
    public Object removeRow(int index) {
        throw new Error();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        rebuildTableModel();
        if(index != null){
            fireTableDataChanged();    
        }
        
    }

    private void rebuildTableModel() {
        getIndex();
        if(index != null){
            visibleRowCount= 0;
            index.clear();
            for(int i = 0; i < nodeAttributeModel.getRowCount(); i++){
                String name = (String)nodeAttributeModel.getValueAt(i, 0);
                if(attributeRegistry.getElement(name).isVisible()){
                    index.add(new Integer(i));
                    visibleRowCount++;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see freemind.view.mindmapview.attributeview.AttributeTableModelDecoratorAdapter#areAttributesVisible()
     */
    public boolean areAttributesVisible() {
        return getRowCount() !=0;
    }
}
