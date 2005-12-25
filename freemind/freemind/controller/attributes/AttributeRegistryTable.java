/*
 * Created on 08.10.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import freemind.controller.filter.util.SortedListModel;
import freemind.main.Resources;

/**
 * @author Dimitri Polivaev
 * 08.10.2005
 */
class AttributeRegistryTable extends JTable {

    static private class ButtonEditor extends AbstractCellEditor implements TableCellEditor{
        final private JButton editButton;

        public ButtonEditor(Icon image) {
            editButton = new JButton(image);
            editButton.setFocusable(false);
        }
        
        public ButtonEditor(Action action) {
            editButton = new JButton(action);
            editButton.setFocusable(false);
        }
        private Object value;

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
         */
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.value = value;            
            return editButton;
        }

        /* (non-Javadoc)
         * @see javax.swing.CellEditor#getCellEditorValue()
         */
        public Object getCellEditorValue() {
            return value;
        }
        
        public void setAction(Action a) {
            editButton.setAction(a);
        }
    }
    static private class ButtonRenderer implements TableCellRenderer{
        public ButtonRenderer(Icon image, String toolTip) {
            renderingEditButton = new JButton(image);
            renderingEditButton.setFocusable(false);
            renderingEditButton.setToolTipText(toolTip);
        }
        final private JButton renderingEditButton;

        /* (non-Javadoc)
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return renderingEditButton;
        }
        
        public void setEnabled(boolean b) {
            renderingEditButton.setEnabled(b);
        }
    }

    private class ToggleAllAction extends AbstractAction{
        public ToggleAllAction() {
            super("", checkBoxImage);
        }
         public void actionPerformed(ActionEvent e) {
            Boolean checked = (Boolean)getValueAt(1, 1);
            checked = Boolean.valueOf( ! checked.booleanValue());
            for(int i = 1; i < getRowCount(); i++){
                setValueAt(checked, i, 1);
            }
        }
    }
    private static final ButtonRenderer editButtonRenderer = new ButtonRenderer(AttributeManagerDialog.editButtonImage,
            Resources.getInstance().getResourceString("attributes_edit_tooltip"));
    static final private Icon checkBoxImage = new ImageIcon(Resources.getInstance().getResource("images/checkbox12.png"));
    final private ButtonRenderer selectAllButtonRenderer;
    final private ButtonEditor selectAllButtonEditor;
    final private AttributeManagerDialog.EditListAction editListAction;
    public AttributeRegistryTable(AttributeManagerDialog.EditListAction editListAction) {
        super();  
        this.editListAction = editListAction;
        getTableHeader().setReorderingAllowed(false);
        selectAllButtonRenderer = new ButtonRenderer(checkBoxImage,
                Resources.getInstance().getResourceString("attributes_select_all_tooltip"));
        selectAllButtonEditor = new ButtonEditor(new ToggleAllAction());
        setDefaultEditor(SortedListModel.class, new ButtonEditor(editListAction));
        setDefaultRenderer(SortedListModel.class, editButtonRenderer);
        setRowHeight(20);
        setRowSelectionAllowed(false);
        }
    
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if(dataModel.getColumnCount() >= 1){
            for(int i = 1; i < getColumnCount(); i++){
                getColumnModel().getColumn(i).setMinWidth(20);
                int prefWidth = getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
                        this, getColumnName(i), false, false, -1, i)
                        .getPreferredSize().width;
                getColumnModel().getColumn(i).setPreferredWidth(prefWidth);
            }
        }
    }
    public TableCellEditor getCellEditor(int row, int column) {
        if(row == 0 && column == 1){
            return selectAllButtonEditor;
        }
        return super.getCellEditor(row, column);
    }
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(row == 0 && column == 1){
            return selectAllButtonRenderer;
        }
        TableCellRenderer tableCellRenderer = super.getCellRenderer(row, column);
        
        if(tableCellRenderer instanceof JLabel){
            JLabel label = (JLabel) tableCellRenderer;
            if(row == 0){
                label.setHorizontalAlignment(JLabel.CENTER);
            }else{
                label.setHorizontalAlignment(JLabel.LEFT);
            }
        }
        else if(tableCellRenderer instanceof JComponent){
            JComponent label = (JComponent) tableCellRenderer;
            switch (column){
            case 1: 
                label.setToolTipText(Resources.getInstance().getResourceString("attributes_visible_tooltip"));
                break;
            case 2:
                if(row == 0){
                    label.setToolTipText(Resources.getInstance().getResourceString("attributes_restricted_attributes_tooltip"));
                }
                else{
                    label.setToolTipText(Resources.getInstance().getResourceString("attributes_restricted_values_tooltip"));
                }
                break;
            }            
        }
        return tableCellRenderer;
    }
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        if(column == 3){
            SortedListModel list = (SortedListModel) getModel().getValueAt(row, column);
            String title = getModel().getValueAt(row, 0).toString();
            String labelText = Resources.getInstance().getResourceString("attribute_list_box_label_text");
            editListAction.setListBoxModel(title,labelText, list);
        }
        return super.prepareEditor(editor, row, column);
    }
}
