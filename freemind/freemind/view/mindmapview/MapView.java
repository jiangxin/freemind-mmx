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
/*$Id: MapView.java,v 1.11 2001-04-06 20:50:11 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import freemind.modes.MindMap;
import freemind.view.MapModule;
import freemind.controller.Controller;
import freemind.controller.NodeMouseListener;
import freemind.controller.NodeKeyListener;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;


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


    //
    // Constructors
    //

    public MapView( MindMap model, Controller controller ) {
	super();
		
	this.model = model;
	this.controller = controller;

	getModel().addTreeModelListener( new MapModelHandler() );

	this.setLayout( new MindMapLayout( this ) );

	initRoot();
    }

    public void initRoot() {
	rootView = NodeView.newNodeView( (MindMapNode)getModel().getRoot(), this );
 	rootView.insert();
	
	getMindMapLayout().reinitialize();

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
	Rectangle scrollTo = new Rectangle(node.getLocation().x + (node.getPreferredSize().width/2) - (d.width/2), node.getLocation().y + (node.getPreferredSize().height/2) - (d.height/2), d.width, d.height);
	scrollRectToVisible(scrollTo);

	Rectangle scrollTo2 = new Rectangle(node.getLocation().x + (node.getPreferredSize().width/2) - (d.width/2), node.getLocation().y + (node.getPreferredSize().height/2) - (d.height/2), d.width, d.height);
	scrollRectToVisible(scrollTo2);
    }

    public void scrollNodeToVisible( NodeView node ) {
	//this is buggy!
	//see centerNode()
	if (node != null) {
  	    scrollRectToVisible( new Rectangle(node.getLocation().x-20, node.getLocation().y,node.getSize().width+20,node.getSize().height) );
  	    scrollRectToVisible( new Rectangle(node.getLocation().x-20, node.getLocation().y,node.getSize().width+20,node.getSize().height) );
  	}
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
     * Add this onde to the selection.
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

    //
    // get/set methods
    //

    public MindMap getModel() {
	return model;
    }

    NodeMouseListener getNodeMouseListener() {
	return getController().getNodeMouseListener();
    }

    NodeKeyListener getNodeKeyListener() {
  	return getController().getNodeKeyListener();
    }

    public NodeView getSelected() {
	return (NodeView)selected.get(0);
    }

    private NodeView getSelected(int i) {
	return (NodeView)selected.get(i);
    }

    public LinkedList getSelecteds() {
	LinkedList result = new LinkedList();
	for (int i=0; i<selected.size();i++) {
		result.add( getSelected(i) );
	}
	return result;
    }


    public boolean isSelected(NodeView n) {
	return selected.contains(n);
    }

    float getZoom() {
	return zoom;
    }

    public void setZoom(float zoom) {
	this.zoom = zoom;
	getRoot().updateAll();
	getMindMapLayout().reinitialize();
	revalidate();
    }

    public void paint(Graphics graphics){
	Graphics2D g = (Graphics2D)graphics;
	setBackground(getModel().getBackgroundColor());
	super.paint(g);
	paintEdges(rootView, g);
    }

    public int print(Graphics graphics, PageFormat pf, int pi) {
	if (pi >= 1) {
	    return Printable.NO_SUCH_PAGE;
	}
	Graphics2D g = (Graphics2D)graphics;
	pf.setOrientation(PageFormat.LANDSCAPE);
	Color background = getBackground();
	setBackground(Color.white);
	//In future, automatically fit map to one page
	g.scale(0.7,0.7);
	super.paint(g);
	paintEdges(rootView,g);
	setBackground(background);
	return Printable.PAGE_EXISTS;
    }

    public Dimension getPreferredSize() {
	return getLayout().preferredLayoutSize(this);
    }


    ///////////
    // private methods. Internal implementation
    /////////


    private void paintEdges(NodeView source, Graphics2D g) {
	for(ListIterator e = source.getChildrenViews().listIterator(); e.hasNext(); ) {
	    NodeView target = (NodeView)e.next();
	    
	    target.getEdge().paint(g);
		
	    paintEdges( target, g );//recursive
	}
    }
    
    protected NodeView getRoot() {
	return rootView;
    }

    private MindMapLayout getMindMapLayout() {
	return (MindMapLayout)getLayout();
    }


//     private void layoutAll() {
// 	System.out.println("layoutAll()");
	
// 	//	setPreferredSize(getLayout().preferredLayoutSize(this));
// 	((MindMapLayout)getLayout()).calcRelYPos(getRoot());
// 	revalidate();
// 	scrollNodeToVisible(getSelected());
// 	repaint();
//     }

//     private void updateAll() {
// 	getRoot().updateAll();
// 	revalidate();
// 	repaint();
//     }

    /**
     * This method is a workaround to allow the inner class access to "this".
     * Change it as soon the correct syntax is known
     */
    private MapView getMap() {
	return this;
    }

    Controller getController() {
	return controller;
    }







    ///////////
    // Inner Class MapModelHandler
    //////////

    /**
     * This inner class updates the Tree when the model is changed.
     */
    private class MapModelHandler implements TreeModelListener {
	
	public void treeNodesChanged( TreeModelEvent e ) {
	    NodeView node;
	    try {
		node = ( (MindMapNode)e.getChildren()[0] ).getViewer();
	    } catch (Exception ex) {//thrown if changed is root
		node = ( (MindMapNode)e.getTreePath().getLastPathComponent() ).getViewer();
	    } //This is not a good solution, but it works
	    node.update();
	    //	    ((MindMapLayout)getLayout()).layoutChildren(node);//layout the node and its children,
	    //because length has changed. But maybe height has changed, too! Fix that.
	    //MapWidth
	    if (node.isLeft()) {
	    } else {
	    }
	    getMindMapLayout().calcTreeHeight( node );
	    if (!node.isRoot()) {
		getMindMapLayout().subtreeChanged( node.getParentView() );
	    } else {
		getMindMapLayout().subtreeChanged( node );
	    }
	    //	    revalidate();//length of the node changes, so whole map has to be repainted
	    repaint();
	}

	
	public void treeNodesInserted( TreeModelEvent e ) {
	    MindMapNode parent = (MindMapNode)e.getTreePath().getLastPathComponent();
	    NodeView parentView = parent.getViewer();
	    //works only with one child
	    MindMapNode child = (MindMapNode)e.getChildren()[0];
	    parentView.insert( child );
	    getMindMapLayout().calcTreeHeight( child.getViewer() );
	    getMindMapLayout().subtreeChanged(parentView);
	    //revalidate();
	    repaint();
	}

	public void treeNodesRemoved( TreeModelEvent e ) {
	    //This is called once for each removed node, with just one child (the removed)
	    NodeView node = ( (MindMapNode)e.getChildren()[0] ).getViewer();//Take care not to remove root in Controller
	    NodeView parent = ( (MindMapNode)e.getTreePath().getLastPathComponent() ).getViewer();
	    node.remove();//Just that one
	    select(parent);
	    getMindMapLayout().subtreeChanged( parent );
	    //	    revalidate();
	    repaint();
	}

	/**
	 * 
	 */
	public void treeStructureChanged( TreeModelEvent e ) {
	    
	    //This implementation simply rereads the whole mindmap
	    removeAll();
	    MindMapNode newRoot = (MindMapNode)e.getTreePath().getLastPathComponent();
	    if(!newRoot.isRoot()) {
		newRoot=getRoot().getModel();
	    }
	    NodeView newRootView = NodeView.newNodeView(newRoot,getMap());
	    rootView=newRootView;
	    rootView.insert();
	    getMindMapLayout().reinitialize();
	    revalidate();
	    repaint();
	}

    }
}
