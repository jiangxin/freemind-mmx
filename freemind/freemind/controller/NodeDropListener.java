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

import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.MapView;
import freemind.modes.MindMapNode;
// For Drag&Drop
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class NodeDropListener implements DropTargetListener {

    private final Controller c;

    public NodeDropListener(Controller controller) {
	c = controller;
    }

    private boolean isDragAcceptable(DropTargetDragEvent event) {
	DataFlavor[] flavors = event.getCurrentDataFlavors();
	for (int i = 0; i < flavors.length; i++) {
	    if (flavors[i].equals(DataFlavor.stringFlavor)) {
		return true;
	    }
	}
	return false;
    }

    private boolean isDropAcceptable(DropTargetDropEvent event) {
	MindMapNode node = ((NodeView)event.getDropTargetContext().getComponent()).getModel();
	if(!node.isDescendantOf(c.getView().getSelected().getModel())){
	    DataFlavor[] flavors = event.getCurrentDataFlavors();
	    for (int i = 0; i < flavors.length; i++) {
		if (flavors[i].equals(DataFlavor.stringFlavor)) {
		    return true;
		}
	    }
	}
	return false;
    }

	
    public void drop (DropTargetDropEvent dtde) {
	if(!isDropAcceptable(dtde)) {
	    dtde.rejectDrop();
	    return;
	}
	dtde.acceptDrop(DnDConstants.ACTION_COPY);
	try {
	    Transferable t = dtde.getTransferable();
	    MindMapNode node = ((NodeView)dtde.getDropTargetContext().getComponent()).getModel();
	    c.getModel().paste(t,node);
	}
	catch (Exception e) {
	    System.out.println("Drop exception:"+e);
	    dtde.dropComplete(false);
	    return;
	}
	dtde.dropComplete(true);
    }

    public void dragEnter (DropTargetDragEvent dtde) {
	// TODO: check DataFlavor before say ok
	// then dtde.rejectDrag(); if not
	if(isDragAcceptable(dtde))
	    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
	else
	    dtde.rejectDrag();
    }

    public void dragOver (DropTargetDragEvent e) {}
    public void dragExit (DropTargetEvent e) {}
    public void dragScroll (DropTargetDragEvent e) {}
    public void dropActionChanged (DropTargetDragEvent e) {}    
}

