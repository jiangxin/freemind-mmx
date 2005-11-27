/*
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview.attributeview;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import freemind.modes.MindMapNode;
import freemind.modes.NodeViewEvent;
import freemind.modes.NodeViewEventListener;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author dimitri
 * 12.06.2005
 */
public class AttributeTable extends JTable implements NodeViewEventListener{
    private static final int MAX_HEIGTH = 300;
    private static final int MAX_WIDTH = 600;
    static private class MyFocusListener implements FocusListener{
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        boolean ignoreLostFocus = false;
        public void focusGained(FocusEvent event) { 
            ignoreLostFocus = false;
            Component source = (Component)event.getSource();
            Component oppositeComponent = event.getOppositeComponent();
            Component newNodeViewInFocus = getAncestorComponent(source, NodeView.class);
            Component oldNodeViewInFocus = getAncestorComponent(oppositeComponent, NodeView.class);
            if(newNodeViewInFocus != oldNodeViewInFocus 
                    && newNodeViewInFocus instanceof NodeView){
                NodeView viewer = (NodeView)newNodeViewInFocus;
                if(! viewer.isSelected()){
                    ignoreLostFocus = true;
                    viewer.getMap().selectAsTheOnlyOneSelected(viewer);
                    source.requestFocus();
                }
            }
            else if(source instanceof JComboBox){
                JComboBox comboBox = (JComboBox)source;
                if(! comboBox.isEditable()){
                    comboBox.setPopupVisible(true);
                }
            }
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent event) {
            if(ignoreLostFocus)
                return;
            Component source = (Component) event.getSource();
            Component oppositeComponent = event.getOppositeComponent();
            AttributeTable oldTable = (AttributeTable)getAncestorComponent(source, AttributeTable.class);
            if(oldTable != null){
                Component newTable = getAncestorComponent(oppositeComponent, AttributeTable.class);
                if(oldTable != newTable){
                    if (oldTable.isEditing()){                
                        oldTable.getCellEditor().stopCellEditing();
                    }
                    oldTable.changeSelectedRowHeight(0);
                    oldTable.clearSelection();
                }
            }
        }
        
        private Component getAncestorComponent(Component object, Class ancestorClass) {
            if(object == null || ancestorClass.isAssignableFrom(object.getClass())){
                return object;
            }
            return getAncestorComponent(object.getParent(), ancestorClass);
        }
    }
    static private class HeaderMouseListener extends MouseAdapter{
        public void mouseReleased(MouseEvent e) {
            JTableHeader header = (JTableHeader)e.getSource();
            AttributeTable table = (AttributeTable) header.getTable();
            float zoom = table.attributeView.getMapView().getZoom();
            Dimension preferredScrollableViewportSize = table.getPreferredScrollableViewportSize();
            JViewport port = (JViewport)table.getParent();
            Dimension extentSize = port.getExtentSize();
            if(preferredScrollableViewportSize.width !=extentSize.width){
                AttributeTableModel model = (AttributeTableModel)table.getModel();
                for(int col = 0; col < table.getColumnCount(); col++){
                    int modelColumnWidth = model.getColumnWidth(col);
                    int currentColumnWidth = (int) (table.getColumnModel().getColumn(col).getWidth() / zoom);
                    if(modelColumnWidth != currentColumnWidth){
                        model.setColumnWidth(col, currentColumnWidth);
                    }
                }
            }
        }
    }
    
    static private MyFocusListener focusListener = new MyFocusListener();
    static private MouseListener componentListener = new HeaderMouseListener();
    private int highRowIndex = 0;
    static private ComboBoxModel defaultComboBoxModel = null; 
    static private DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    private AttributeView attributeView;
    private static final int EXTRA_HEIGHT = 4;
    private static final float TABLE_ROW_HEIGHT = 4;
    private static final Dimension prefHeaderSize = new Dimension(5, 5);
    AttributeTable(AttributeView attributeView) {
        super();
        this.attributeView = attributeView;
        addFocusListener(focusListener);
        getTableHeader().addMouseListener(componentListener);
        attributeView.getNodeView().getModel().addNodeViewEventListener(this);
        setModel(attributeView.getCurrentAttributeTableModel());
        updateFontSize(this);
        updateColumnWidths();       
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(prefHeaderSize);
        int h = getRowHeight();
        setRowHeight(highRowIndex, h + EXTRA_HEIGHT);
        setRowSelectionAllowed(false);
    }
    
    
    public TableCellRenderer getCellRenderer(int row, int column) {
        String text = getValueAt(row, column).toString();
        dtcr.setText(text);
        int prefWidth = dtcr.getPreferredSize().width;
        int width = getColumnModel().getColumn(column).getWidth();
        if(prefWidth > width){
            dtcr.setToolTipText(text);
        }
        else{
            dtcr.setToolTipText(null);
        }
        return dtcr;
    }
    public TableCellEditor getCellEditor(int row, int column) {
        JComboBox comboBox = new JComboBox();
        DefaultCellEditor dce = new DefaultCellEditor(comboBox);
        return dce;
    }
    /**
     * 
     */
    public Component prepareEditor(TableCellEditor tce, int row, int col) {
        ComboBoxModel model;
        JComboBox comboBox = (JComboBox) ((DefaultCellEditor) tce).getComponent();
        MindMapNode node = getAttributeTableModel().getNode();
        AttributeRegistry attributes = node.getMap().getRegistry().getAttributes();
        switch (col){
        case 0:        
            model = attributes.getComboBoxModel();
            comboBox.setEditable(! attributes.isRestricted());
            break;
        case 1:
            String attrName = getAttributeTableModel().getValueAt(row, 0).toString();
            model = attributes.getDefaultComboBoxModel(attrName);
            comboBox.setEditable(! attributes.isRestricted(attrName));
            break;
        default:
            model = getDefaultComboBoxModel();
        }
        comboBox.setModel(model);
        model.setSelectedItem(getValueAt(row, col));
               
        comboBox.addFocusListener(focusListener);
        comboBox.getEditor().getEditorComponent().addFocusListener(focusListener);
        Component editor = super.prepareEditor(tce, row, col);
        updateFontSize(editor);
        return editor;
    }
    
    public Dimension getPreferredScrollableViewportSize() {
        if(! isValid())
            validate();
        float zoom = getZoom();
        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.min((int)(MAX_WIDTH * zoom) , dimension.width);
        dimension.height = Math.min((int)(MAX_HEIGTH * zoom) - getTableHeader().getPreferredSize().height, dimension.height);
        return dimension;
    }

    static ComboBoxModel getDefaultComboBoxModel() {
        if (defaultComboBoxModel == null)
        {
            defaultComboBoxModel = new DefaultComboBoxModel();
        }
        return defaultComboBoxModel;
    }
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
            boolean extend) {
        changeSelectedRowHeight(rowIndex);
        super.changeSelection(rowIndex, columnIndex, toggle, extend);        
    }
    private void changeSelectedRowHeight(int rowIndex) {
        if(highRowIndex != rowIndex){
            if(highRowIndex < getRowCount()){
                int h = getRowHeight(highRowIndex);
                setRowHeight(highRowIndex, h - EXTRA_HEIGHT);
            }
            int h = getRowHeight(rowIndex);
            setRowHeight(rowIndex, h + EXTRA_HEIGHT);
            highRowIndex = rowIndex;
        }
    }
    
    public void clearSelection() {
    }
    
    /**
     * 
     */
    void updateAttributeTable() {
        updateFontSize(this);
        updateRowHeights();
        updateColumnWidths();       
    }
    
    private void updateColumnWidths() {
        float zoom = getZoom();
        for(int i = 0; i < 2; i++){
            int width = (int) (getAttributeTableModel().getColumnWidth(i) * zoom);
            getColumnModel().getColumn(i).setPreferredWidth(width);            
        }
    }
    
    private void updateRowHeights() {
        int rowCount = getRowCount();
        if(rowCount == 0)
            return;
        
        int constHeight = getTableHeader().getPreferredSize().height + EXTRA_HEIGHT;        
        float zoom = getZoom();
        float fontSize = getFontSize();
        float tableRowHeight = fontSize + zoom * TABLE_ROW_HEIGHT;
        int newHeight = (int)((tableRowHeight * rowCount + (zoom - 1)* constHeight) / rowCount);
        int highRowsNumber = (int)((tableRowHeight - newHeight)* rowCount);
        for (int i = 0; i < highRowsNumber; i++)
        {
            setRowHeight(i, 1 + newHeight + (i == highRowIndex ? EXTRA_HEIGHT : 0));
        }
        for (int i = highRowsNumber; i < rowCount; i++)
        {
            setRowHeight(i, newHeight + (i == highRowIndex ? EXTRA_HEIGHT : 0));
        }
    }
    
    private void updateFontSize(Component c) {
        // 1) Determine font
        Font font = c.getFont();
        if (font != null) {
            float oldFontSize = font.getSize2D();
            float newFontSize = getFontSize();
            if(oldFontSize != newFontSize)
            {
                font = font.deriveFont(newFontSize);
                c.setFont(font); 
            }
        }
    }
    private float getZoom() {
        return attributeView.getMapView().getZoom();
    }
    
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        updateRowHeights();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    public void nodeViewCreated(NodeViewEvent event) {
        getModel().addTableModelListener(this);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    public void nodeViewRemoved(NodeViewEvent event) {
        getModel().removeTableModelListener(this);
        attributeView.getNodeView().getModel().removeNodeViewEventListener(this);
    }
    
    
    private float getFontSize() {
        float zoom = getZoom();
        return (attributeView.getNodeView().getModel().getMap().getRegistry().getAttributes().getFontSize() * zoom);
    }
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        getModel().removeTableModelListener(this);
    }
    
    private void removeListenerFromEditor() {
        JComboBox comboBox = (JComboBox) getEditorComponent();
        comboBox.removeFocusListener(focusListener);
        comboBox.getEditor().getEditorComponent().removeFocusListener(focusListener);
        comboBox.setModel(new DefaultComboBoxModel());
    }
    
    public void removeEditor() {
        removeListenerFromEditor();
        getAttributeTableModel().editingCanceled();
        super.removeEditor();
    }


    /**
     * @return Returns the currentModel.
     */
    public AttributeTableModelDecoratorAdapter getAttributeTableModel() {
        return (AttributeTableModelDecoratorAdapter)getModel();
    }
    public AttributeView getAttributeView() {
        return attributeView;
    }


    /**
     * 
     */
    public void setOptimalColumnWidths() {
        // TODO Auto-generated method stub
        
    }


    /**
     * 
     */
    public void editAttributes() {
        attributeView.getAttributes().setViewType(AttributeTableLayoutModel.SHOW_EXTENDED);
    }
    
    /**
     * 
     */
    public void hideAttributes() {
        attributeView.getAttributes().setViewType(AttributeTableLayoutModel.SHOW_REDUCED);
    }


    /**
     * @param row
     */
    public void insertRow(int row) {
        if(getModel() instanceof ExtendedAttributeTableModelDecorator){
            ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator)getModel();
            model.insertRow(row);
            setEditingRow(row);
            setEditingColumn(0);
            editCellAt(row, 0);
            
            getEditorComponent().requestFocus();
        }
    }


    /**
     * @param row
     */
    public void removeRow(int row) {
        if(getModel() instanceof ExtendedAttributeTableModelDecorator){
            ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator)getModel();
            model.removeRow(row);
        }
    }


    /**
     * @param row
     */
    public void moveRowUp(int row) {
        if(getModel() instanceof ExtendedAttributeTableModelDecorator){
            ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator)getModel();
            model.moveRowUp(row);
        }
    }


    /**
     * @param row
     */
    public void moveRowDown(int row) {
        if(getModel() instanceof ExtendedAttributeTableModelDecorator){
            ExtendedAttributeTableModelDecorator model = (ExtendedAttributeTableModelDecorator)getModel();
            model.moveRowDown(row);
        }
    }
}
