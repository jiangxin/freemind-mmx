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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.view.mindmapview.NodeView;

//import ublic class MindMapNodesSelection implements Transferable, ClipboardOwner {
   //   public static DataFlavor fileListFlavor = null;


public class NodeDropListener implements DropTargetListener {

    private final Controller c;

    public NodeDropListener(Controller controller) {
	c = controller;
    }

    // See DropTargetListener for the meaning.
    private boolean isDragAcceptable(DropTargetDragEvent ev) {
       if (ev.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          return true; }
       if (ev.isDataFlavorSupported(MindMapNodesSelection.fileListFlavor)) {
          return true; }
       return false; }

    private boolean isDropAcceptable(DropTargetDropEvent event) {
        MindMapNode node = ((NodeView)event.getDropTargetContext().getComponent().getParent()).getModel();
	MindMapNode selected = c.getView().getSelected().getModel();
	return ((node!=selected) && !node.isDescendantOf(selected));
        // I think (node!=selected) is a hack for windows
    }

	
    public void drop (DropTargetDropEvent dtde) {
        try {
           int dropAction = dtde.getDropAction();
	   Transferable t = dtde.getTransferable();

           NodeView targetNodeView = (NodeView)dtde.getDropTargetContext().getComponent().getParent();
           MindMapNode targetNode = targetNodeView.getModel();
           MindMapNodeModel targetNodeModel = (MindMapNodeModel)targetNode;          

           // Intra application DnD

           // For some reason, getting sourceAction is only possible for local
           // transfer. When I try to remove clause dtde.isLocalTransfer, I get an answer
           // like "no drop current". One hypothesis is that with nonlocal transfers, I
           // have to accept drop action before I can get transfer data. However, this is
           // not what I want in this particular situation. A part of the problem lies in
           // the hackery of sending source action using data flavour too.
           if (dtde.isLocalTransfer() && t.isDataFlavorSupported(MindMapNodesSelection.dropActionFlavor)) {
              String sourceAction = (String)t.getTransferData(MindMapNodesSelection.dropActionFlavor);
              if (sourceAction.equals("LINK")) {
                 dropAction = DnDConstants.ACTION_LINK; }
              if (sourceAction.equals("COPY")) {
                 dropAction = DnDConstants.ACTION_COPY; }}

           targetNodeView.setDraggedOver(NodeView.DRAGGED_OVER_NO);
           targetNodeView.repaint();

           if (dtde.isLocalTransfer() && (dropAction == DnDConstants.ACTION_MOVE) && !isDropAcceptable(dtde)) {
              dtde.rejectDrop();
              return; }

           dtde.acceptDrop(dtde.getDropAction());

           if (!dtde.isLocalTransfer()) {
              //if (dtde.isDataFlavorSupported(MindMapNodesSelection.fileListFlavor)) {
              //System.err.println("filelist");
              c.getModeController().paste (t, targetNode, 
                                  targetNodeView.dropAsSibling(dtde.getLocation().getX()),
                                  targetNodeView.dropPosition (dtde.getLocation().getX()));
              dtde.dropComplete(true);
              return; }

           // This all is specific to MindMap model. Needs rewrite to work for other modes.
           // We ignore data transfer in dtde. We take selected nodes as drag sources.
           // <problem>
           // The behaviour is not so fine, when some of selected nodes is an ancestor of other selected nodes.
           // Ideally, we would first unselect all nodes, which have an ancestor among selected nodes.
           // I don't have time/lust to do this. This is just a minor problem.
           // </problem>
           
           // By transferable object we only transfer source action. This will be a problem, when we want
           // to implement extra application dnd or dnd between different Java Virtual Machines.

           if (dropAction == DnDConstants.ACTION_LINK) {
              // ACTION_LINK means for us change the color, style and font.
              
              // This is not very clean. This all should probably be
              // implemented on the mindMapMapModel level. On the other
              // hand, one would have to downcast to MindMapMapModel anyway.
              
              //MindMapNode selectedNode = c.getView().getSelected().getModel();
              MindMapMapModel mindMapMapModel = (MindMapMapModel)c.getModel();
              
              // link feature continues here. fc, 01.11.2003:
              // if there are more than 4 nodes, then ask the user:
              int yesorno = JOptionPane.YES_OPTION;
              if(c.getView().getSelecteds().size() >= 5)
                  {
                      yesorno = JOptionPane.showConfirmDialog(c.getFrame().getContentPane(),
                                                              c.getResourceString("lots_of_links_warning"),
                                                              Integer.toString(c.getView().getSelecteds().size())+" links to the same node",
                                                              JOptionPane.YES_NO_OPTION);
                  }
              if(yesorno == JOptionPane.YES_OPTION) {
                  for(ListIterator it = c.getView().getSelecteds().listIterator();it.hasNext();) {
                      MindMapNodeModel selectedNodeModel = (MindMapNodeModel)((NodeView)it.next()).getModel();
                      //                  mindMapMapModel.setNodeColor(selectedNodeModel,targetNode.getColor());
                      //                  mindMapMapModel.setNodeFont(selectedNodeModel,targetNode.getFont()); 
                      c.getModeController().addLink(selectedNodeModel, targetNodeModel);
                  }
              }
           }
           else {
           	Transferable trans = null;
				// if move, verify, that the target is not a son of the sources.
				if (DnDConstants.ACTION_MOVE == dropAction) {
					List selecteds = c.getModeController().getSelecteds();
					MindMapNode actualNode = targetNode;
					do {
						if (selecteds.contains(actualNode)) {
							String message = c.getResourceString("cannot_move_to_child");
		                      JOptionPane.showMessageDialog(c.getFrame().getContentPane(),
                                    message,"Freemind", JOptionPane.WARNING_MESSAGE);
							dtde.dropComplete(true);
							return;
						}
						actualNode = (actualNode.isRoot())?null:actualNode.getParentNode();
					} while(actualNode != null);
					trans = c.getModeController().cut();
				} else {
					trans = c.getModel().copy();
				}

				c.getView().selectAsTheOnlyOneSelected(
						targetNodeModel.getViewer());
				c.getModeController().paste(
						trans,
						targetNode,
						targetNode.getViewer().dropAsSibling(
								dtde.getLocation().getX()),
						targetNode.getViewer().dropPosition(
								dtde.getLocation().getX()));
              }
           }
	catch (Exception e) {
	    System.err.println("Drop exception:"+e);
        e.printStackTrace();
	    dtde.dropComplete(false);
	    return; }
	dtde.dropComplete(true); }

    /**
     * The method is called when the cursor carrying the dragged item enteres
     * the area of the node. The name "dragEnter" seems to be confusing to me.
     *
     * I think the difference between dragAcceptable and dropAcceptable is
     * that in dragAcceptable, you tell if the type of the thing being dragged
     * is OK, where in dropAcceptable, you tell if your really willing to
     * accept the item.
     */
    public void dragEnter (DropTargetDragEvent dtde) {
       // TODO: Accepting the action ACTION_MOVE is false, because we cannot
       // know if the action is really ACTION_MOVE.
       if(isDragAcceptable(dtde)) {
          dtde.acceptDrag(DnDConstants.ACTION_MOVE); }
       else {
          dtde.rejectDrag(); }
    }

    public void dragOver (DropTargetDragEvent e) {
       NodeView draggedNode = (NodeView)e.getDropTargetContext().getComponent().getParent();
       int oldDraggedOver = draggedNode.getDraggedOver();
       // let the node decide, which dragged over type it is:
       draggedNode.setDraggedOver(e.getLocation());
       int newDraggedOver = draggedNode.getDraggedOver();
       boolean repaint = newDraggedOver != oldDraggedOver;
       if (repaint) {
          draggedNode.repaint(); }
    }
   public void dragExit (DropTargetEvent e) {
       NodeView draggedNode = (NodeView)e.getDropTargetContext().getComponent().getParent();
       draggedNode.setDraggedOver(NodeView.DRAGGED_OVER_NO);
       draggedNode.repaint();
   }
    public void dragScroll (DropTargetDragEvent e) {}
    public void dropActionChanged (DropTargetDragEvent e) {}    
}





