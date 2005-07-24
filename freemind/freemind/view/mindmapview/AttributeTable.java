/*
 * Created on 12.06.2005
 *
 */
package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
            JTable table = header.getTable();
            Dimension preferredScrollableViewportSize = table.getPreferredScrollableViewportSize();
            JViewport port = (JViewport)table.getParent();
            Dimension extentSize = port.getExtentSize();
            if(preferredScrollableViewportSize.width !=extentSize.width){
                JComponent map = (JComponent)port.getParent().getParent().getParent();
                map.revalidate();
            }
        }
    }

    static private MyFocusListener focusListener = new MyFocusListener();
    static private MouseListener componentListener = new HeaderMouseListener();
    private AttributeTableModel currentModel;
    private int highRowIndex = 0;
    static private JComboBox comboBox = null; 
    static private ComboBoxModel defaultModel = null; 
    static private DefaultCellEditor dce = null;
    private NodeView node;
    private static final int EXTRA_HEIGHT = 3;
    public AttributeTable(NodeView node) {
        super();
        this.node = node;
        addFocusListener(focusListener);
        getTableHeader().addMouseListener(componentListener);
        currentModel = node.getCurrentAttributeTableModel();
        setModel(currentModel);
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
            model = getDefaultModel();
        }
        model.setSelectedItem("");
        if(selectedTable != this)
        {
            node.getMap().selectAsTheOnlyOneSelected(node);
            selectedTable = this;
        }
        comboBox.setModel(model);
        return super.prepareEditor(tce, row, col);
    }

    public Dimension getPreferredScrollableViewportSize() {
        if(! isValid())
            validate();
        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.min(600, dimension.width);
        dimension.height = Math.min(300, dimension.height);
        return dimension;
    }
    public boolean getScrollableTracksViewportWidth() {
        return false; // getWidth() < 300;
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
    public static ComboBoxModel getDefaultModel() {
        if (defaultModel == null)
        {
            defaultModel = new DefaultComboBoxModel();
         }
        return defaultModel;
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
}
