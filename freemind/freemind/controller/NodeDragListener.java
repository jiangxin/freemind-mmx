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
import freemind.view.mindmapview.MainView;
import freemind.view.mindmapview.NodeView;
// Drag & Drop
import java.awt.dnd.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.Cursor;

/**
 * The NodeDragListener which belongs to every
 * NodeView
 */
public class NodeDragListener implements DragGestureListener {

   private final Controller c;
   
   public NodeDragListener(Controller controller) {
      c = controller;
   }

   public Cursor getCursorByAction(int dragAction) {
      switch (dragAction) {
      case DnDConstants.ACTION_COPY:
         return DragSource.DefaultCopyDrop;
      case DnDConstants.ACTION_LINK:
         return DragSource.DefaultLinkDrop;
      default:
         return DragSource.DefaultMoveDrop;
      }
   }

   public void dragGestureRecognized(DragGestureEvent e) {
      if(!c.getFrame().getProperty("draganddrop").equals("true")) return;

      MindMapNode node = ((MainView)e.getComponent()).getNodeView().getModel();
      if (node.isRoot()) return;

      //Transferable t; // = new StringSelection("");
      String dragAction = "MOVE";

      Cursor cursor = getCursorByAction(e.getDragAction());

      if ((e.getTriggerEvent().getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0) {
         // Change drag action
         cursor = DragSource.DefaultLinkDrop;
         dragAction = "LINK"; }

      if ((e.getTriggerEvent().getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) != 0) {
         // Change drag action
         cursor = DragSource.DefaultCopyDrop;
         dragAction = "COPY"; }

      Transferable t = c.getModel().copy();
      //new MindMapNodesSelection("Ahoj","Ahoj","Ahoj", dragAction);
      ((MindMapNodesSelection)t).setDropAction(dragAction);
      //public void setDropAction(String dropActionContent) {     

      // starts the dragging
      //	DragSource dragSource = DragSource.getDefaultDragSource();

      e.startDrag
         ( cursor , t,
          new DragSourceListener() {
             public void	dragDropEnd(DragSourceDropEvent dsde) {

             }

             public void dragEnter(DragSourceDragEvent e) {
             }
             public void	dragExit(DragSourceEvent dse) {
             }
             public void	dragOver(DragSourceDragEvent dsde) {
             }
             public void	dropActionChanged(DragSourceDragEvent dsde) {
                   dsde.getDragSourceContext().setCursor(getCursorByAction(dsde.getUserAction()));
             }
          }
       );
   }
}



