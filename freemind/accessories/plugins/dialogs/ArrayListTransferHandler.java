/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: ArrayListTransferHandler.java,v 1.1.4.2 2006/04/09 13:34:38 dpolivaev Exp $*/
package accessories.plugins.dialogs;

/*
 * Fc, 8.4.06:
 * ArrayListTransferHandler.java was adapted from Sun Tutorial Examples.
 * License unknown.
 * We take it on "As is" basis.
 */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import accessories.plugins.dialogs.ManagePatternsPopupDialog.PatternListModel;

public class ArrayListTransferHandler extends TransferHandler {
	DataFlavor localArrayListFlavor;
	String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType
			+ ";class=java.util.ArrayList";
	JList source = null;
	int[] indices = null;
	int addIndex = -1; // Location where items were added
	int addCount = 0; // Number of items added

	public ArrayListTransferHandler() {
		try {
			localArrayListFlavor = new DataFlavor(localArrayListType);
		} catch (ClassNotFoundException e) {
			System.out
					.println("ArrayListTransferHandler: unable to create data flavor");
		}
	}

	public boolean importData(JComponent c, Transferable t) {
		JList target = null;
		ArrayList alist = null;
		if (!canImport(c, t.getTransferDataFlavors())) {
			return false;
		}
		try {
			target = (JList) c;
			if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
				alist = (ArrayList) t.getTransferData(localArrayListFlavor);
			} else {
				return false;
			}
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("importData: unsupported data flavor");
			return false;
		} catch (IOException ioe) {
			System.out.println("importData: I/O exception");
			return false;
		}

		// At this point we use the same code to retrieve the data
		// locally or serially.

		// We'll drop at the current selected index.
		int index = target.getSelectedIndex();

		// Prevent the user from dropping data back on itself.
		// For example, if the user is moving items #4,#5,#6 and #7 and
		// attempts to insert the items after item #5, this would
		// be problematic when removing the original items.
		// This is interpreted as dropping the same data on itself
		// and has no effect.
		if (source.equals(target)) {
			if (indices != null && index >= indices[0] - 1
					&& index <= indices[indices.length - 1]) {
				indices = null;
				return true;
			}
		}

		PatternListModel listModel = (PatternListModel) target.getModel();
		int max = listModel.getSize();
		if (index < 0) {
			index = max;
		} else {
			index++;
			if (index > max) {
				index = max;
			}
		}
		addIndex = index;
		addCount = alist.size();
		for (int i = 0; i < alist.size(); i++) {
			listModel.add(index++, alist.get(i));
		}
		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		if ((action == MOVE) && (indices != null)) {
			PatternListModel model = (PatternListModel) source.getModel();

			// If we are moving items around in the same list, we
			// need to adjust the indices accordingly since those
			// after the insertion point have moved.
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] > addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--)
				model.remove(indices[i]);
		}
		indices = null;
		addIndex = -1;
		addCount = 0;
	}

	private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
		if (localArrayListFlavor == null) {
			return false;
		}

		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(localArrayListFlavor)) {
				return true;
			}
		}
		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		if (hasLocalArrayListFlavor(flavors)) {
			return true;
		}
		return false;
	}

	protected Transferable createTransferable(JComponent c) {
		if (c instanceof JList) {
			source = (JList) c;
			indices = source.getSelectedIndices();
			Object[] values = source.getSelectedValues();
			if (values == null || values.length == 0) {
				return null;
			}
			ArrayList alist = new ArrayList(values.length);
			for (int i = 0; i < values.length; i++) {
				Object o = values[i];
				String str = o.toString();
				if (str == null)
					str = "";
				alist.add(str);
			}
			return new ArrayListTransferable(alist);
		}
		return null;
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public class ArrayListTransferable implements Transferable {
		ArrayList data;

		public ArrayListTransferable(ArrayList alist) {
			data = alist;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return data;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { localArrayListFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (localArrayListFlavor.equals(flavor)) {
				return true;
			}
			return false;
		}
	}
}
