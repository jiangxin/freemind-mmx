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
 * Created on 10.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.view.mindmapview.attributeview;

import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;

import freemind.modes.attributes.Attribute;

/**
 * @author Dimitri Polivaev 10.07.2005
 */
class ReducedAttributeTableModelDecorator extends
		AttributeTableModelDecoratorAdapter {
	private Vector index = null;
	private int visibleRowCount;

	ReducedAttributeTableModelDecorator(AttributeView attrView) {
		super(attrView);
		rebuildTableModel();
	}

	private Vector getIndex() {
		if (index == null
				&& this.attributeRegistry.getVisibleElementsNumber() > 0)
			index = new Vector(this.nodeAttributeModel.getRowCount(), 10);
		return index;
	}

	public int getRowCount() {
		return visibleRowCount;
	}

	public Object getValueAt(int row, int col) {
		return nodeAttributeModel.getValueAt(calcRow(row), col);
	}

	public boolean isCellEditable(int row, int col) {
		if (nodeAttributeModel.isCellEditable(row, col)) {
			return col == 1;
		}
		return false;
	}

	private int calcRow(int row) {
		return ((Integer) index.get(row)).intValue();
	}

	public void setValueAt(Object o, int row, int col) {
		nodeAttributeModel.setValueAt(o, calcRow(row), col);
		fireTableCellUpdated(row, col);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.attributes.AttributeTableModel#insertRow(int,
	 * freemind.modes.attributes.Attribute)
	 */
	public void insertRow(int index, Attribute newAttribute) {
		throw new Error();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.attributes.AttributeTableModel#addRow(freemind.modes.
	 * attributes.Attribute)
	 */
	public void addRow(Attribute newAttribute) {
		throw new Error();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.attributes.AttributeTableModel#removeRow(int)
	 */
	public Object removeRow(int index) {
		throw new Error();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent e) {
		rebuildTableModel();
		if (index != null) {
			fireTableDataChanged();
		}

	}

	private void rebuildTableModel() {
		getIndex();
		if (index != null) {
			visibleRowCount = 0;
			index.clear();
			for (int i = 0; i < nodeAttributeModel.getRowCount(); i++) {
				String name = (String) nodeAttributeModel.getValueAt(i, 0);
				if (attributeRegistry.getElement(name).isVisible()) {
					index.add(new Integer(i));
					visibleRowCount++;
				}
			}
		}
	}

	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		if (e.getType() != TableModelEvent.UPDATE || e.getColumn() != 0) {
			rebuildTableModel();
			fireTableDataChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.view.mindmapview.attributeview.AttributeTableModelDecoratorAdapter
	 * #areAttributesVisible()
	 */
	public boolean areAttributesVisible() {
		return getRowCount() != 0;
	}
}
