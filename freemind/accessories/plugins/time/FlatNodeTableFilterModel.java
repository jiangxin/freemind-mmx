/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 *
 * Created on 02.05.2006
 */
/*$Id: FlatNodeTableFilterModel.java,v 1.1.2.2 2008/11/01 21:11:42 christianfoltin Exp $*/
package accessories.plugins.time;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import accessories.plugins.time.TimeList.NodeHolder;
import accessories.plugins.time.TimeList.NotesHolder;

/**
 * @author foltin
 * 
 */
public class FlatNodeTableFilterModel extends AbstractTableModel {

	private final TableModel mTableModel;
	private String mFilterRegexp;
	/**
	 * Contains indices or rows matching the filter criteria.
	 */
	private ArrayList mIndexArray;
	private Pattern mPattern;
	/**
	 * The column that contains the NodeHolder items
	 */
	private final int mNodeTextColumn;
	private int mNoteTextColumn;

	/**
	 * @param node_text_column
	 * @param note_text_column TODO
	 * 
	 */
	public FlatNodeTableFilterModel(TableModel tableModel, int node_text_column, int note_text_column) {
		super();
		this.mTableModel = tableModel;
		this.mNodeTextColumn = node_text_column;
		mNoteTextColumn = note_text_column;
		tableModel.addTableModelListener(new TableModelHandler());
		resetFilter();
	}

	public void resetFilter() {
		setFilter(".*");
	}

	public void setFilter(String filterRegexp) {
		this.mFilterRegexp = filterRegexp;
		// System.out.println("Setting filter to '"+mFilterRegexp+"'");
		mPattern = Pattern.compile(mFilterRegexp, Pattern.CASE_INSENSITIVE);
		updateIndexArray();
		fireTableDataChanged();
	}

	private void updateIndexArray() {
		ArrayList newIndexArray = new ArrayList();
		for (int i = 0; i < mTableModel.getRowCount(); i++) {
			NodeHolder nodeContent = (NodeHolder) mTableModel.getValueAt(i,
					mNodeTextColumn);
			if (mPattern.matcher(nodeContent.toString()).matches()) {
				// add index to array:
				newIndexArray.add(new Integer(i));
			} else {
				// only check notes, when not already a hit.
				NotesHolder noteContent = (NotesHolder) mTableModel.getValueAt(i,
						mNoteTextColumn);
				if (mPattern.matcher(noteContent.toString()).matches()) {
					// add index to array:
					newIndexArray.add(new Integer(i));
				}
			}
		}
		mIndexArray = newIndexArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return mIndexArray.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return mTableModel.getColumnCount();
	}

	public String getColumnName(int pColumnIndex) {
		return mTableModel.getColumnName(pColumnIndex);
	}

	public Class getColumnClass(int arg0) {
		return mTableModel.getColumnClass(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {
		if (row < 0 || row >= getRowCount()) {
			throw new IllegalArgumentException("Illegal Row specified: " + row);
		}
		int origRow = ((Integer) mIndexArray.get(row)).intValue();
		return mTableModel.getValueAt(origRow, column);
	}

	private class TableModelHandler implements TableModelListener {

		public void tableChanged(TableModelEvent arg0) {
//			updateIndexArray();
			fireTableDataChanged();
		}
	}
}
