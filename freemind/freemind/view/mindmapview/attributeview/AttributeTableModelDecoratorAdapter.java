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

import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
abstract class AttributeTableModelDecoratorAdapter extends AbstractTableModel  implements AttributeTableModel, TableModelListener, ChangeListener{
    protected NodeAttributeTableModel nodeAttributeModel;
    protected AttributeRegistry attributeRegistry;
    public AttributeTableModelDecoratorAdapter(AttributeView attrView) {
        super();
        this.nodeAttributeModel = attrView.getAttributes();
        this.attributeRegistry = attrView.getAttributeRegistry();
        MindMapNode node = nodeAttributeModel.getNode();
        addListeners();
    }
    public MindMapNode getNode() {
        return nodeAttributeModel.getNode();
    }
    public int getColumnCount() {
        return 2;
    }
    
    public Class getColumnClass(int columnIndex) {
        return nodeAttributeModel.getColumnClass(columnIndex);
    }
    public String getColumnName(int columnIndex) {
        return nodeAttributeModel.getColumnName(columnIndex);
    }
    public int getColumnWidth(int col) {
        return nodeAttributeModel.getColumnWidth(col);
    }
    public void setColumnWidth(int col, int width) {
        nodeAttributeModel.setColumnWidth(col, width);
    }
    private void addListeners() {
        nodeAttributeModel.addTableModelListener(this);
        this.attributeRegistry.addChangeListener(this);
    }
    private void removeListeners() {
        nodeAttributeModel.removeTableModelListener(this);
        this.attributeRegistry.removeChangeListener(this);
    }

    public void viewRemoved() {
        removeListeners();
    }
    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireTableDataChanged();        
    }
    public void editingCanceled() {
    }
    /**
     * @param view 
     */
    public abstract boolean areAttributesVisible() ;
}
