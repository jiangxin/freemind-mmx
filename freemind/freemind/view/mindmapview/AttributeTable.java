/*
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview;

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
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;

import freemind.modes.attributes.AttributeTableModel;

/**
 * @author dimitri
 * 12.06.2005
 */
public class AttributeTable extends JTable {
    private static final int MAX_HEIGTH = 300;
    private static final int MAX_WIDTH = 600;
    private static final int TABLE_FONT_SIZE = 12;
    static private AttributeTable selectedTable = null;
    static private class MyFocusListener implements FocusListener{
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(FocusEvent event) { 
            AttributeTable table = (AttributeTable)event.getSource();
            NodeView nodeView = table.node;
            AttributeTable.clearOldSelection();
            nodeView.getMap().scrollNodeToVisible(nodeView);
            selectedTable = table;
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent event) {
        }
    }
    
    static private class HeaderMouseListener extends MouseAdapter{
        public void mouseReleased(MouseEvent e) {
            JTableHeader header = (JTableHeader)e.getSource();
            AttributeTable table = (AttributeTable) header.getTable();
            float zoom = table.node.getMap().getZoom();
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
    private NodeView node;
    private static final int EXTRA_HEIGHT = 4;
    private static final float TABLE_ROW_HEIGHT = 16;
    public AttributeTable(NodeView node) {
        super();
        this.node = node;
        addFocusListener(focusListener);
        getTableHeader().addMouseListener(componentListener);
        currentModel = node.getCurrentAttributeTableModel();
        setModel(currentModel);
        updateAttributeTable();
        setDefaultEditor(Object.class, getDCE());
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);
        int h = getRowHeight();
        setRowHeight(highRowIndex, h + EXTRA_HEIGHT);
    }

    /**
     * 
     */
    public static void clearOldSelection() {
        if(selectedTable != null && selectedTable != null){
            if (selectedTable.isEditing()){                
                selectedTable.getCellEditor().stopCellEditing();
            }
            selectedTable.clearSelection(); 
            selectedTable = null;
        }
    }

    /**
     * 
     */
    public Component prepareEditor(TableCellEditor tce, int row, int col) {
        ComboBoxModel model;
        switch (col){
        case 0:        
            model = currentModel.getNode().getMap().getRegistry().getAttributes().getComboBoxModel();
            break;
        case 1:
            String attrName = currentModel.getValueAt(row, 0).toString();
            model = currentModel.getNode().getMap().getRegistry().getAttributes().getDefaultComboBoxModel(attrName);
            break;
        default:
            model = getDefaultComboBoxModel();
        }
        model.setSelectedItem("");
        if(selectedTable != this)
        {
            node.getMap().selectAsTheOnlyOneSelected(node);
            selectedTable = this;
        }
        comboBox.setModel(model);
        Component editor = super.prepareEditor(tce, row, col);
        updateFontSize(editor);
        return editor;
    }

    public Dimension getPreferredScrollableViewportSize() {
        if(! isValid())
            validate();
        float zoom = node.getMap().getZoom();
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
            dce = new DefaultCellEditor(comboBox);
        }
        return dce;
    }
    public static ComboBoxModel getDefaultComboBoxModel() {
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
            int h = getRowHeight(highRowIndex);
            setRowHeight(highRowIndex, h - EXTRA_HEIGHT);
            h = getRowHeight(rowIndex);
            setRowHeight(rowIndex, h + EXTRA_HEIGHT);
            highRowIndex = rowIndex;
        }
    }

    public void clearSelection() {
        changeSelectedRowHeight(0);
        super.clearSelection();
    }

    /**
     * 
     */
    public void updateAttributeTable() {
        float zoom = updateFontSize(this);
        // 2) Determine row heights
        int constHeight = getTableHeader().getPreferredSize().height + EXTRA_HEIGHT;
        int rowCount = getRowCount();
        int newHeight = (int)((zoom * TABLE_ROW_HEIGHT * rowCount + (zoom - 1)* constHeight) / rowCount);
        int highRowsNumber = (int)((zoom * TABLE_ROW_HEIGHT - newHeight)* rowCount);
        for (int i = 0; i < highRowsNumber; i++)
        {
            setRowHeight(1 + newHeight + (i == highRowIndex ? EXTRA_HEIGHT : 0));
        }
        for (int i = highRowsNumber; i < rowCount; i++)
        {
            setRowHeight(newHeight + (i == highRowIndex ? EXTRA_HEIGHT : 0));
        }
        
        // 3) Determine row widths
        for(int i = 0; i < 2; i++){
            int width = (int) (currentModel.getColumnWidth(i) * zoom);
            getColumnModel().getColumn(i).setPreferredWidth(width);            
        }
        
    }

    private float updateFontSize(Component c) {
        float zoom = node.getMap().getZoom();
        // 1) Determine font
        Font font = c.getFont();
        if (font != null) {
            float oldFontSize = font.getSize2D();
            float newFontSize = TABLE_FONT_SIZE*zoom;
            if(oldFontSize != newFontSize)
            {
                font = font.deriveFont(newFontSize);
                c.setFont(font); 
            }
        }
        return zoom;
    }
}
