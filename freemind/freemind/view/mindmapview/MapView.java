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
/*$Id: MapView.java,v 1.17 2003-11-03 10:39:53 sviles Exp $*/

package freemind.view.mindmapview;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.MindMap;
import freemind.controller.Controller;
import freemind.controller.NodeMouseMotionListener;
import freemind.controller.NodeKeyListener;
import java.util.*;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.JOptionPane;

/**
 * This class represents the view of a whole MindMap
 * (in analogy to class JTree).
 */
public class MapView extends JPanel implements Printable {

    private MindMap model;
    private NodeView rootView;
    private Vector selected = new Vector();
    private Controller controller;
    private float zoom=1F;
    private boolean isPrinting = false; // use for remove selection from print

    //
    // Constructors
    //

    public MapView( MindMap model, Controller controller ) {
	super();
		
	this.model = model;
	this.controller = controller;

	this.setAutoscrolls(true); //For some reason this doesn't work.

	getModel().addTreeModelListener( new MapModelHandler() );

	this.setLayout( new MindMapLayout( this ) );

	initRoot();

	setBackground(getModel().getBackgroundColor());

        addMouseListener( controller.getMapMouseMotionListener() );
        addMouseMotionListener( controller.getMapMouseMotionListener() );
        addMouseWheelListener( controller.getMapMouseWheelListener() );

    }

    public void initRoot() {
	rootView = NodeView.newNodeView( (MindMapNode)getModel().getRoot(), this );
 	rootView.insert();
	
	getMindMapLayout().updateTreeHeightsAndRelativeYOfWholeMap();

	revalidate();
    }


    //
    // Navigation
    //

    /**
     * Problem: Before  scrollRectToVisible is called, the node has the location (0,0), ie. the location first gets
     * calculated after the scrollpane is actually scrolled. Thus, as a workaround, I simply call scrollRectToVisible
     * twice, the first time the location of the node is calculated, the second time the scrollPane is actually
     * scrolled.
     */
    public void centerNode( NodeView node ) {
	JViewport viewPort = (JViewport)getParent();

	Dimension d = viewPort.getExtentSize();
        scrollRectToVisible(new Rectangle(node.getLocation().x + (node.getPreferredSize().width/2) - (d.width/2),
                                          node.getLocation().y + (node.getPreferredSize().height/2) - (d.height/2),
                                          d.width, d.height));
        scrollRectToVisible(new Rectangle(node.getLocation().x + (node.getPreferredSize().width/2) - (d.width/2),
                                          node.getLocation().y + (node.getPreferredSize().height/2) - (d.height/2),
                                          d.width, d.height));
    }

    public void scrollNodeToVisible( NodeView node ) {
	//this is buggy!
	//see centerNode()
	if (node != null) {
  	    scrollRectToVisible( new Rectangle(node.getLocation().x-20, node.getLocation().y,node.getSize().width+20,
                                               node.getSize().height) );
  	    scrollRectToVisible( new Rectangle(node.getLocation().x-20, node.getLocation().y,node.getSize().width+20,
                                               node.getSize().height) );
  	}
    }

    /**
      * Scroll the viewport of the map to the south-west, i.e. scroll the map itself to the north-east.
      */
    public void scrollBy(int x, int y) {
       JViewport mapViewport = (JViewport)getParent();
       Point currentPoint = mapViewport.getViewPosition();                 // Get View Position
       currentPoint.translate(x, y);                                       // Add the difference to it
       // Watch for the boundaries
       //    Low boundaries
       if (currentPoint.getX()<0) {
          currentPoint.setLocation(0, currentPoint.getY()); }
       if (currentPoint.getY()<0) {
          currentPoint.setLocation(currentPoint.getX(), 0); }
       //    High boundaries
       double maxX = getSize().getWidth() - mapViewport.getExtentSize().getWidth();     // getView() gets viewed area - JPanel
       double maxY = getSize().getHeight() - mapViewport.getExtentSize().getHeight();
       if (currentPoint.getX()>maxX) {
          currentPoint.setLocation(maxX, currentPoint.getY()); }
       if (currentPoint.getY()>maxY) {
          currentPoint.setLocation(currentPoint.getX(), maxY); }
       mapViewport.setViewPosition(currentPoint);                          // Set It Back
    }

    public void moveLeft() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()){
	    LinkedList left = ((RootNodeView)oldSelected).getLeft();
	    if (left.size() == 0) return;
	    newSelected = (NodeView)left.getFirst();
	} else if(!oldSelected.isLeft()) {
	    newSelected = oldSelected.getParentView();
	} else {
	    if (oldSelected.getChildrenViews().size() == 0) return;
	    newSelected = (NodeView)oldSelected.getChildrenViews().getFirst();
	}
	select(newSelected);
	scrollNodeToVisible( newSelected );
    }

    public void moveRight() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()) {
	    LinkedList right = ((RootNodeView)oldSelected).getRight();
	    if (right.size() == 0) return;
	    newSelected = (NodeView)right.getFirst();
	} else if(oldSelected.isLeft()) {
	    newSelected = oldSelected.getParentView();
	} else {
	    if (oldSelected.getChildrenViews().size() == 0) return;
	    newSelected = (NodeView)oldSelected.getChildrenViews().getFirst();
	}
	select(newSelected);
	scrollNodeToVisible(newSelected);
    }

    public void moveUp() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()){
	    return;
	} else {
	    newSelected = oldSelected.getPreviousSibling();
	}
	select(newSelected);
	scrollNodeToVisible(newSelected);
    }

    public void moveDown() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()){
	    return;
	} else {
	    newSelected = oldSelected.getNextSibling();
	}
	select(newSelected);
	scrollNodeToVisible(newSelected);
    }

    public void moveToRoot() {
	select( getRoot() );
	centerNode( getRoot() );
    }

    /**
     * Select a node.
     * If extend is false, it will be the only one.
     * If yes, this node will be added.
     */
    public void select(NodeView newSelected, boolean extend) {
	if (extend)
	    toggleSelect(newSelected);
	else
	    select(newSelected);
    }

    /**
     * Select only one node.
     */
    public void select(NodeView newSelected) {
/*
	NodeView oldSelected = getSelected();
	//select new node
	this.selected = newSelected;
	newSelected.requestFocus();
	//	scrollNodeToVisible(newSelected);
	newSelected.repaint();
	if (oldSelected != null) {
	    oldSelected.repaint();
	}
*/
       LinkedList oldSelecteds = getSelecteds();
	//select new node
	this.selected.clear();
	this.selected.addElement(newSelected);
	newSelected.requestFocus();
	//	scrollNodeToVisible(newSelected);
	newSelected.repaint();

	for(ListIterator e = oldSelecteds.listIterator();e.hasNext();) {
           NodeView oldSelected = (NodeView)e.next();
           oldSelected.repaint();
	}
    }

    /**
     * Add this node to the selection.
     */
    public void toggleSelect(NodeView newSelected) {
	NodeView oldSelected = getSelected();
	if (isSelected(newSelected)) {
		if (selected.size()>1) {
			selected.remove(newSelected);
			oldSelected=newSelected;
		}
	}
	else {
		selected.add(0,newSelected);
	}
	getSelected().requestFocus();
	getSelected().repaint();
	oldSelected.repaint();
	}

    /**
     * Select this node and its children.
	 * if extend is false, the past selection will be empty.
	 * if yes, the selection will extended with this node and its children
     */
    public void selectBranch(NodeView newSelected, boolean extend) {
	if (!extend || !isSelected(newSelected))
		select(newSelected,extend);
	for(ListIterator e = newSelected.getChildrenViews().listIterator(); e.hasNext(); ) {
	    NodeView target = (NodeView)e.next();
		selectBranch(target,true);
	}
    }

    public void selectContinuous(NodeView newSelected) {
       // Precondition: newSelected != getSelected()
       NodeView oldSelected = getSelected();
       ListIterator i = newSelected.getSiblingViews().listIterator();
       while (i.hasNext()) {
          NodeView nodeView = (NodeView)i.next();
          if ( nodeView == newSelected || nodeView == oldSelected ) {
             select(nodeView,false);
             break; }}
       while (i.hasNext()) {
          NodeView nodeView = (NodeView)i.next();
          select(nodeView,true);
          if ( nodeView == newSelected || nodeView == oldSelected ) {
             break; }}}

    //
    // get/set methods
    //

    public MindMap getModel() {
	return model;
    }

    NodeMouseMotionListener getNodeMouseMotionListener() {
	return getController().getNodeMouseMotionListener();
    }

    NodeKeyListener getNodeKeyListener() {
  	return getController().getNodeKeyListener();
    }

    DragGestureListener getNodeDragListener() {
	return getController().getNodeDragListener();
    }

    DropTargetListener getNodeDropListener() {
	return getController().getNodeDropListener();
    }

    public NodeView getSelected() {
	return (NodeView)selected.get(0);
    }

    private NodeView getSelected(int i) {
	return (NodeView)selected.get(i);
    }

    public LinkedList getSelecteds() {
        // return an ArrayList of NodeViews.
	LinkedList result = new LinkedList();
	for (int i=0; i<selected.size();i++) {
		result.add( getSelected(i) );
	}
	return result;
    }

    public ArrayList /*of NodeViews*/ getSelectedsSortedByY() {
        TreeMap sortedNodes = new TreeMap();
	for (int i=0; i<selected.size();i++) {
           sortedNodes.put(new Integer(getSelected(i).getY()), getSelected(i)); }

        ArrayList selectedNodes = new ArrayList();
        for(Iterator it = sortedNodes.entrySet().iterator();it.hasNext();) {
           selectedNodes.add( ((Map.Entry)it.next()).getValue() ); }

        return selectedNodes;
   }

    public ArrayList /*of MindMapNodes*/ getSelectedNodesSortedByY() {
        TreeMap sortedNodes = new TreeMap();
	for (int i=0; i<selected.size();i++) {
           sortedNodes.put(new Integer(getSelected(i).getY()), getSelected(i).getModel()); }

        ArrayList selectedNodes = new ArrayList();
        for(Iterator it = sortedNodes.entrySet().iterator();it.hasNext();) {
           selectedNodes.add( ((Map.Entry)it.next()).getValue() ); }

        return selectedNodes;
   }

    public boolean isSelected(NodeView n) {
	if (isPrinting) return false;
	return selected.contains(n);
    }


    public float getZoom() {
       return zoom; }

    public int getZoomed(int number) {
       return (int)(number*zoom); }

    public void setZoom(float zoom) {
	this.zoom = zoom;
	getRoot().updateAll();
	getMindMapLayout().updateTreeHeightsAndRelativeYOfWholeMap();
        getMindMapLayout().layout();
	revalidate();
    }

    public void paint(Graphics graphics){
	Graphics2D g = (Graphics2D)graphics;
	super.paint(g);
	paintEdges(rootView, g);
    }

	// Todo:
	// ask user for :
	// - center in page (in page format ?)
	// - print zoom or maximize (in page format ?)
	// - print selection only
	// remember those parameters from one session to another
	//    (as orientation & margin from pf)
    public int print(Graphics graphics, PageFormat pf, int pi) {
	if (pi >= 1) {
	    return Printable.NO_SUCH_PAGE;
	}
	isPrinting = true;
	Graphics2D g = (Graphics2D)graphics;
	Color background = getBackground();
	setBackground(Color.white);
	//In future, automatically fit map to one page
	double zoom = 0.7;
	// getImageable are in 1/72 inch, g.transform is also in 72 dpi for print g
	// so nothing to do ?!?
	int margeX = (int)(pf.getImageableX()/zoom);
	int margeY = (int)(pf.getImageableY()/zoom);
	// double pageW = pf.getImageableWidth(); unit ?
	// double pageH = pf.getImageableHeight();
	Rectangle r = getInnerBounds(rootView);
	g.scale(zoom,zoom);
	g.translate(-r.getX()+margeX,-r.getY()+margeY);
	print(g);
	setBackground(background);
	isPrinting = false;
	return Printable.PAGE_EXISTS;
    }

    public Dimension getPreferredSize() {
	return getLayout().preferredLayoutSize(this);
    }


    ///////////
    // private methods. Internal implementation
    /////////
	
    /**
    * Return the exact bounding box of children of source (without BORDER)
    * Could be implemented in LayoutManager as Minimum size ?
        */
    private Rectangle getInnerBounds(NodeView source) {
       Rectangle r = source.getBounds();
       for(ListIterator e = source.getChildrenViews().listIterator(); e.hasNext(); ) {
          NodeView target = (NodeView)e.next();
          r.add(getInnerBounds(target));//recursive
       }
       return r;
    }

    private void paintEdges(NodeView source, Graphics2D g) {
	for(ListIterator e = source.getChildrenViews().listIterator(); e.hasNext(); ) {
	    NodeView target = (NodeView)e.next();
	    
	    target.getEdge().paint(g);
		
	    paintEdges( target, g );//recursive
	}
    }
    
    protected NodeView getRoot() {
	return rootView; }

    private MindMapLayout getMindMapLayout() {
	return (MindMapLayout)getLayout();  }

    /**
     * This method is a workaround to allow the inner class access to "this".
     * Change it as soon the correct syntax is known.
     */
    private MapView getMap() {
	return this; }

    public Controller getController() {
	return controller;  }


    ///////////
    // Inner Class MapModelHandler
    //////////

    /**
     * This inner class updates the Tree when the model is changed.
     */
    private class MapModelHandler implements TreeModelListener {
	
	public void treeNodesChanged( TreeModelEvent e ) {
            // must be in structureChanged instead ?
            // or in is own Listerner
            setBackground(getModel().getBackgroundColor());
            // Daniel: Why would I want to change background because some
            // nodes have changed? That's really strange.
	    NodeView node;
	    try {
               node = ( (MindMapNode)e.getChildren()[0] ).getViewer(); }
            catch (Exception ex) {    //thrown if changed is root
               node = ( (MindMapNode)e.getTreePath().getLastPathComponent() ).getViewer(); }
            // ^ This is not a good solution, but it works
	    node.update();
            getMindMapLayout().updateTreeHeightsAndRelativeYOfDescendantsAndAncestors(node);
            getMindMapLayout().layout();
	    repaint();
	}
	
	public void treeNodesInserted( TreeModelEvent e ) {
	    MindMapNode parent = (MindMapNode)e.getTreePath().getLastPathComponent();
	    NodeView parentView = parent.getViewer();
	    //works only with one child
	    MindMapNode child = (MindMapNode)e.getChildren()[0];

            if (child != null) {  // if a node is folded, child is null.
               parentView.insert(child);
               getMindMapLayout().updateTreeHeightFromChildren(child.getViewer()); } 
	    getMindMapLayout().updateTreeHeightsAndRelativeYOfAncestors(parentView);
            getMindMapLayout().layout();
            parentView.requestFocus();
	    repaint();
	}

	public void treeNodesRemoved (TreeModelEvent e) {
	    //This is called once for each removed node, with just one child (the removed)
	    NodeView node = ( (MindMapNode)e.getChildren()[0] ).getViewer();
            //Take care not to remove root in Controller
	    NodeView parent = ( (MindMapNode)e.getTreePath().getLastPathComponent() ).getViewer();
	    node.remove();   //Just that one
	    select(parent);
	    getMindMapLayout().updateTreeHeightsAndRelativeYOfAncestors(parent);
            getMindMapLayout().layout();
            parent.requestFocus();
	    repaint();
	}

	/**
	 *
	 */

	public void treeStructureChanged (TreeModelEvent e) {

            // Keep selected nodes

            ArrayList selectedNodes = new ArrayList();
            for (ListIterator it = getSelecteds().listIterator();it.hasNext();) {
               selectedNodes.add(((NodeView)it.next()).getModel()); }
            MindMapNode selectedNode = getSelected().getModel();

            // Update everything

	    MindMapNode subtreeRoot = (MindMapNode)e.getTreePath().getLastPathComponent();

            if (subtreeRoot.isRoot()) {
               subtreeRoot.getViewer().remove();               
               rootView = NodeView.newNodeView(getRoot().getModel(), getMap());
               rootView.insert(); }
            else {
               boolean nodeIsLeft = subtreeRoot.getViewer().isLeft();
               subtreeRoot.getViewer().remove();
               NodeView nodeView = NodeView.newNodeView(subtreeRoot, getMap());
               nodeView.setLeft(nodeIsLeft);
               nodeView.insert(); }

            getMindMapLayout().updateTreeHeightsAndRelativeYOfDescendantsAndAncestors(subtreeRoot.getViewer());
            getMindMapLayout().layout();

            // Restore selected nodes

            // Warning, the old views still exist, because JVM has not deleted them. But don't use them!
            selected.clear();
            for (ListIterator it = selectedNodes.listIterator();it.hasNext();) {
               selected.addElement(((MindMapNode)it.next()).getViewer()); }
            selectedNode.getViewer().requestFocus();
	    repaint();
	}
    }
}
