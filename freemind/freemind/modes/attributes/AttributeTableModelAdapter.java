/*
 * Created on 11.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

import javax.swing.table.AbstractTableModel;

/**
 * @author Dimitri Polivaev
 * 11.07.2005
 */
public abstract class AttributeTableModelAdapter extends AbstractTableModel {
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return "";
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col) {
        return Object.class;
    }

}
