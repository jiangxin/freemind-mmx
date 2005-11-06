/*
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview.attributeview;

import java.awt.Component;
import java.awt.Container;
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
import freemind.modes.attributes.AttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author dimitri
 * 12.06.2005
 */
class AttributeTable extends JTable implements NodeViewEventListener{
    private static final int MAX_HEIGTH = 300;
    private static final int MAX_WIDTH = 600;
    static private class MyFocusListener implements FocusListener{
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(FocusEvent event) { 
            Container newFocusCycleRoot = getFocusCycleRoot(event.getSource());
            Container oldFocusCycleRoot = getFocusCycleRoot(event.getOppositeComponent());
            if(newFocusCycleRoot != oldFocusCycleRoot 
                    && newFocusCycleRoot instanceof AttributeTable){
                AttributeTable table = (AttributeTable)newFocusCycleRoot;
                table.selectNode();
            }
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent event) {
            Container oldFocusCycleRoot = getFocusCycleRoot(event.getSource());
            Container newFocusCycleRoot = getFocusCycleRoot(event.getOppositeComponent());
            if(newFocusCycleRoot != oldFocusCycleRoot 
                    && oldFocusCycleRoot instanceof AttributeTable){
                ((AttributeTable)oldFocusCycleRoot).clearSelection();                 
                return;                 
            }
        }
        
        private Container getFocusCycleRoot(Object object) {
            if(object instanceof Container){
                Container source = (Container)object;
                if(source.isFocusCycleRoot()){
                    return source;
                }
            }
            if(object instanceof Component){
                Component source = (Component)object;
                return source.getFocusCycleRootAncestor();
            }
            return null;
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
    private AttributeTableModel currentModel;
    private int highRowIndex = 0;
    static private JComboBox comboBox = null; 
    static private ComboBoxModel defaultComboBoxModel = null; 
    static private DefaultCellEditor dce = null;
    static private DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
    private AttributeView attributeView;
    private static final int EXTRA_HEIGHT = 4;
    private static final float TABLE_ROW_HEIGHT = 4;
    AttributeTable(AttributeView attributeView) {
        super();
        this.attributeView = attributeView;
        addFocusListener(focusListener);
        getTableHeader().addMouseListener(componentListener);
        attributeView.getNodeView().getModel().addNodeViewEventListener(this);
        currentModel = attributeView.getCurrentAttributeTableModel();
        setModel(currentModel);
        updateFontSize(this);
        updateColumnWidths();       
        setDefaultEditor(Object.class, getDCE());
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);
        int h = getRowHeight();
        setRowHeight(highRowIndex, h + EXTRA_HEIGHT);
        setRowSelectionAllowed(false);
        setFocusCycleRoot(true);
    }
    
    
    public TableCellEditor getCellEditor(int row, int column) {
        return dce;
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
    /**
     * 
     */
    public Component prepareEditor(TableCellEditor tce, int row, int col) {
        ComboBoxModel model;
        MindMapNode node = currentModel.getNode();
        AttributeRegistry attributes = node.getMap().getRegistry().getAttributes();
        switch (col){
        case 0:        
            model = attributes.getComboBoxModel();
            comboBox.setEditable(! attributes.isRestricted());
            break;
        case 1:
            String attrName = currentModel.getValueAt(row, 0).toString();
            model = attributes.getDefaultComboBoxModel(attrName);
            comboBox.setEditable(! attributes.isRestricted(attrName));
            break;
        default:
            model = getDefaultComboBoxModel();
        }
        model.setSelectedItem("");
        comboBox.setModel(model);
        Component editor = super.prepareEditor(tce, row, col);
        updateFontSize(editor);
        return editor;
    }
    
    private void selectNode() {
        MindMapNode node = currentModel.getNode();
        NodeView viewer = node.getViewer();
        if(! viewer.isSelected()){
            viewer.getMap().selectAsTheOnlyOneSelected(viewer);
        }
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
    private static DefaultCellEditor getDCE() {
        if (dce == null)
        {
            comboBox = new JComboBox();
            comboBox.setEditable(true);
            comboBox.addFocusListener(focusListener);
            comboBox.getEditor().getEditorComponent().addFocusListener(focusListener);
            dce = new DefaultCellEditor(comboBox);
        }
        return dce;
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
        if (isEditing()){                
            getCellEditor().stopCellEditing();
        }
        changeSelectedRowHeight(0);
        super.clearSelection();
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
            int width = (int) (currentModel.getColumnWidth(i) * zoom);
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
        if (isEditing()
                && (e.getColumn() == TableModelEvent.ALL_COLUMNS 
                        || e.getFirstRow() == TableModelEvent.HEADER_ROW)){
            getCellEditor().cancelCellEditing();
        }
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
}
