/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
abstract class AttributeTableModelDecoratorAdapter extends AbstractTableModel  implements AttributeTableModel, TableModelListener, ChangeListener, NodeViewEventListener{
    protected NodeAttributeTableModel nodeAttributeModel;
    protected AttributeRegistry attributeRegistry;
    public AttributeTableModelDecoratorAdapter(
            NodeAttributeTableModel nodeAttributeModel,
            AttributeRegistry attributeRegistry) {
        super();
        this.nodeAttributeModel = nodeAttributeModel;
        this.attributeRegistry = attributeRegistry;
        MindMapNode node = nodeAttributeModel.getNode();
        if(node.getViewer() != null){
            addListeners();
        }
        node.addNodeViewEventListener(this);        
    }
    public MindMapNode getNode() {
        return nodeAttributeModel.getNode();
    }
    public int getColumnCount() {
        return 2;
    }
    
    public Class getColumnClass(int columnIndex) {
        return nodeAttributeModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return nodeAttributeModel.getColumnName(columnIndex);
    }
    public int getColumnWidth(int col) {
        return nodeAttributeModel.getColumnWidth(col);
    }
    public void setColumnWidth(int col, int width) {
        nodeAttributeModel.setColumnWidth(col, width);
    }
    private void addListeners() {
        nodeAttributeModel.addTableModelListener(this);
        this.attributeRegistry.addChangeListener(this);
    }
    private void removeListeners() {
        nodeAttributeModel.removeTableModelListener(this);
        this.attributeRegistry.removeChangeListener(this);
        nodeAttributeModel.getNode().removeNodeViewEventListener(this);        
    }

    public void nodeViewCreated(NodeViewEvent event) {
        addListeners();
    }
    public void nodeViewRemoved(NodeViewEvent event) {
        removeListeners();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireTableDataChanged();        
    }
    public void editingCanceled() {
    }
    /**
     * @param view 
     * @return
     */
    public abstract boolean areAttributesVisible() ;
}
