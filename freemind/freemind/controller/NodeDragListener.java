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

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;
// Drag & Drop
import java.awt.dnd.DragSource;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;

/**
 * The NodeDragListener which belongs to every
 * NodeView
 */
public class NodeDragListener implements DragGestureListener {

    private final Controller c;

    public NodeDragListener(Controller controller) {
	c = controller;
    }

    public void dragGestureRecognized(DragGestureEvent e) {
	if(!c.getFrame().getProperty("draganddrop").equals("true")) return;

	MindMapNode node = ((NodeView)e.getComponent()).getModel();
	if (node.isRoot()) return;
	Transferable t = c.getModel().copy(node);
	// starts the dragging
	//	DragSource dragSource = DragSource.getDefaultDragSource();
	e.startDrag (DragSource.DefaultMoveDrop, t,
			      new DragSourceListener() {
				      public void	dragDropEnd(DragSourceDropEvent dsde) {
					  // if not ok, go back
					  if ( !dsde.getDropSuccess()){
					      //					      c.getModel().paste(oldnode,(MindMapNode)oldnode.getParent());
					  } else if (dsde.getDropAction()==DnDConstants.ACTION_MOVE) {
					      //successful
					      MindMapNode oldnode = ((NodeView)dsde.getDragSourceContext().getComponent()).getModel();
					      c.getModel().cut(oldnode);
					  }
				      }
				      public void	dragEnter(DragSourceDragEvent dsde) {
				      }
				      public void	dragExit(DragSourceEvent dse) {
				      }
				      public void	dragOver(DragSourceDragEvent dsde) {
				      }
				      public void	dropActionChanged(DragSourceDragEvent dsde) {
				      }
				  }
			      );
    }
}

