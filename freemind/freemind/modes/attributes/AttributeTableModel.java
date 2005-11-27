/*
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.table.TableModel;

import freemind.modes.MindMapNode;

/**
 * @author Dimitri Polivaev
 * 10.07.2005
 */
public interface AttributeTableModel extends TableModel{
    int getRowCount();
    
    int getColumnWidth(int col);
    
    void setColumnWidth(int col, int width);

    Object getValueAt(int row, int col);

    void setValueAt(Object o, int row, int col);

    MindMapNode getNode();

    void fireTableDataChanged();
    }