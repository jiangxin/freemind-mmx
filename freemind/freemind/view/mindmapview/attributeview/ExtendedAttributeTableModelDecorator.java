/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import javax.swing.event.ChangeEvent;

import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
class ExtendedAttributeTableModelDecorator extends AttributeTableModelDecoratorAdapter{
    int newRow;
    private static final int AFTER_LAST_ROW = Integer.MAX_VALUE;
    public ExtendedAttributeTableModelDecorator(AttributeView attrView) {
        super(attrView);
        newRow = AFTER_LAST_ROW;
    }
    public int getRowCount() {
        if (newRow == AFTER_LAST_ROW)
            return nodeAttributeModel.getRowCount();
        return nodeAttributeModel.getRowCount() + 1;
    }
    public Object getValueAt(int row, int col) {
        if (row < newRow){
            return nodeAttributeModel.getValueAt(row, col);
        }
        if(row == newRow){
            return "";
        }
        return nodeAttributeModel.getValueAt(row-1, col);
    }
    public void insertRow(int index) {
        newRow = index;
        fireTableRowsInserted(index, index);
    }
    public boolean isCellEditable(int row, int col) {
        if(row != newRow){
            int rowInModel = row < newRow ? row : row - 1;
            return nodeAttributeModel.isCellEditable(rowInModel, col);
        }
        return col == 0;
    }
    
    public Object removeRow(int index) {
        return nodeAttributeModel.removeRow(index);
    }
    
    public void setValueAt(Object o, int row, int col) {
        if(row != newRow){
            if(col == 1 || o.toString().length() > 0){
                int rowInModel = row < newRow ? row : row - 1;
                nodeAttributeModel.setValueAt(o, rowInModel, col);
            }
            return;
        }
        else{
            newRow = AFTER_LAST_ROW;
            fireTableRowsDeleted(row, row);
            if(col == 0 && o != null && o.toString().length() > 0){
                nodeAttributeModel.insertRow(row, o.toString(), "");
            }
            return;
        }
        
    }
    public void stateChanged(ChangeEvent e) {
        fireTableDataChanged();     
    }
    
    public void editingCanceled() {
        if(newRow != AFTER_LAST_ROW){
            int row = newRow;
            newRow = AFTER_LAST_ROW;  
            fireTableRowsDeleted(row, row);
          
        }
    }
    /* (non-Javadoc)
     * @see freemind.view.mindmapview.attributeview.AttributeTableModelDecoratorAdapter#areAttributesVisible()
     */
    public boolean areAttributesVisible() {
        return getRowCount() !=0;
    }
    /**
     */
    public void moveRowUp(int row) {
        Attribute attribute = (Attribute)nodeAttributeModel.removeRow(row);
        nodeAttributeModel.insertRow(row-1, attribute.getName(), attribute.getValue());                
    }
    /**
     */
    public void moveRowDown(int row) {
        Attribute attribute = (Attribute)nodeAttributeModel.removeRow(row);
        nodeAttributeModel.insertRow(row+1, attribute.getName(), attribute.getValue());                
    }
}
