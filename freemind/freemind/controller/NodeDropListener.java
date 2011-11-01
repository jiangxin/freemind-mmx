/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.controller;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

// import ublic class MindMapNodesSelection implements Transferable,
// ClipboardOwner {
// public static DataFlavor fileListFlavor = null;

public class NodeDropListener implements DropTargetListener {

	private final Controller c;

	private DropTargetListener mListener;

	public NodeDropListener(Controller controller) {
		c = controller;
	}

	public void register(DropTargetListener listener) {
		this.mListener = listener;
	}

	public void deregister() {
		mListener = null;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		if (mListener != null)
			mListener.dragEnter(dtde);
	}

	public void dragExit(DropTargetEvent dte) {
		if (mListener != null)
			mListener.dragExit(dte);
	}

	public void dragOver(DropTargetDragEvent dtde) {
		if (mListener != null)
			mListener.dragOver(dtde);
	}

	public void drop(DropTargetDropEvent dtde) {
		if (mListener != null)
			mListener.drop(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		if (mListener != null)
			mListener.dropActionChanged(dtde);
	}

}
