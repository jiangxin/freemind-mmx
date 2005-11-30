/*
 * Created on 26.11.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.attributes;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.JTableHeader;

import freemind.main.Resources;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.AttributeTableModel;
import freemind.view.mindmapview.attributeview.AttributeTable;

/**
 * @author Dimitri Polivaev
 * 26.11.2005
 */
public class AttributePopupMenu extends JPopupMenu implements MouseListener {
    private JMenuItem edit = null;
    private JMenuItem optimalWidth = null;
    private JMenuItem hide = null;
    private JMenuItem insert = null;
    private JMenuItem delete = null;
    private JMenuItem up = null;
    private JMenuItem down = null;
    private AttributeTable table;
    private int row;
    
    /**
     * @return Returns the edit.
     */
    private JMenuItem getEdit() {
        if(edit == null){
            edit = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_edit"));
            edit.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.editAttributes();                    
                }                
            });
        }
        return edit;
    }

    /**
     * @return Returns the optimalWidth.
     */
    private JMenuItem getOptimalWidth() {
        if(optimalWidth == null){
            optimalWidth = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_optimal_width"));
            optimalWidth.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.setOptimalColumnWidths();                    
                }                
            });
        }
        return optimalWidth;
    }

    /**
     * @return Returns the hide.
     */
    private JMenuItem getHide() {
        if(hide == null){
            hide = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_hide"));
            hide.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.hideAttributes();                    
                }                
            });
        }
        return hide;
    }

    /**
     * @return Returns the insert.
     */
    private JMenuItem getInsert() {
        if(insert == null){
            insert = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_new"));
            insert.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.insertRow(row + 1);
                }                
            });
        }
        return insert;
    }

    /**
     * @return Returns the delete.
     */
    private JMenuItem getDelete() {
        if(delete == null){
            delete = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_delete"));
            delete.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.removeRow(row);                  
                }                
            });
        }
        return delete;
    }

    /**
     * @return Returns the up.
     */
    private JMenuItem getUp() {
        if(up == null){
            up = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_up"));
            up.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.moveRowUp(row);                    
                }                
            });
        }
        return up;
    }

    /**
     * @return Returns the down.
     */
    private JMenuItem getDown() {
        if(down == null){
            down = new JMenuItem(Resources.getInstance().getResourceString("attributes_popup_down"));
            down.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    table.moveRowDown(row);                    
                }                
            });
        }
        return down;
    }
    
   /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            selectTable(e.getComponent(), e.getPoint());
            make();
            show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }

    /**
     * @param j
     * @param i
     * @param component
     * 
     */
    private void make() {
        String attributeViewType = table.getAttributeView().getAttributeViewType();
        AttributeTableModel model = table.getAttributeTableModel();
        int rowCount = model.getRowCount();
        if(attributeViewType.equals(AttributeTableLayoutModel.SHOW_EXTENDED)){
            add(getHide());
            if(rowCount != 0){
                add(getOptimalWidth());   
            }            
            add(getInsert());
            if(row != -1){
                add(getDelete());
                if(row != 0){
                    add(getUp());
                }
                if(row != rowCount - 1){
                    add(getDown());
                }
            }
        }
        else{
            add(getEdit());
            if(rowCount != 0){
                add(getOptimalWidth());   
            }            
        }
        
    }

    private void selectTable(Component component, Point point) throws AssertionError {
        int componentCount = getComponentCount();
        for(int i = componentCount; i > 0;){
            remove(--i);
        }
        if(component instanceof AttributeTable){
            table = (AttributeTable)component;  
            row = table.rowAtPoint(point);
            int selectedRow = table.getSelectedRow();
        }
        else if (component instanceof JTableHeader){
            JTableHeader header = (JTableHeader)component;
            table = (AttributeTable)header.getTable();
            row = -1;
        }
        else{
            throw new AssertionError();
        }
        table.requestFocus();
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    }

    protected void firePopupMenuWillBecomeInvisible() {
        if(row != -1){
            table.removeRowSelectionInterval(row, row);
            table.setRowSelectionAllowed(false);
        }
    }
    protected void firePopupMenuWillBecomeVisible() {
        if(row != -1){
            table.setRowSelectionAllowed(true);
            table.addRowSelectionInterval(row, row);
        }
    }
}
