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
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import freemind.controller.filter.util.SortedListModel;

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
        public ButtonRenderer(Icon image) {
            renderingEditButton = new JButton(image);
            renderingEditButton.setFocusable(false);
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
    private static final ButtonRenderer editButtonRenderer = new ButtonRenderer(AttributeDialog.editButtonImage);
    static final private Icon checkBoxImage = new ImageIcon("images/CheckBox12.gif");
    final private ButtonRenderer selectAllButtonRenderer;
    final private ButtonEditor selectAllButtonEditor;
    final private AttributeDialog.EditListAction editListAction;
    public AttributeRegistryTable(AttributeDialog.EditListAction editListAction) {
        super();  
        this.editListAction = editListAction;
        getTableHeader().setReorderingAllowed(false);
        selectAllButtonRenderer = new ButtonRenderer(checkBoxImage);
        selectAllButtonEditor = new ButtonEditor(new ToggleAllAction());
        setDefaultEditor(SortedListModel.class, new ButtonEditor(editListAction));
        setDefaultRenderer(SortedListModel.class, editButtonRenderer);
        setRowHeight(20);
    }
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if(dataModel.getColumnCount() >= 1){
            for(int i = 1; i < getColumnCount(); i++){
                getColumnModel().getColumn(i).setMinWidth(20);
                getColumnModel().getColumn(i).setPreferredWidth(20);
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
        return super.getCellRenderer(row, column);
    }
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        if(column == 3){
            SortedListModel list = (SortedListModel) getModel().getValueAt(row, column);
            editListAction.setListBoxModel(list);
        }
        return super.prepareEditor(editor, row, column);
    }
}
