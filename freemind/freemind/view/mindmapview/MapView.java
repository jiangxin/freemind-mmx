/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import freemind.modes.MindMap;
import freemind.view.MapModule;
import freemind.controller.Controller;
import freemind.controller.NodeMouseListener;
import freemind.controller.NodeKeyListener;
import java.util.Enumeration;
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
    private NodeView selected;
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

	init();

	this.setLayout( new MindMapLayout( this ) );
    }

    public void init() {
	removeAll();
	//Initialize MindMap
	rootView = NodeView.newNodeView( (MindMapNode)getModel().getRoot(), this );
	//	( (MindMapNode)mapModel.getRoot() ).setViewer(rootView);
 	rootView.insert();
// 	add(rootView);
	rootView.setPosition(0,0);

	layoutAll();
    }


    //
    // Navigation
    //

    public void centerNode( NodeView node ) {
	JViewport viewPort = (JViewport)getParent();

	Dimension d = viewPort.getExtentSize();
	Rectangle scrollTo = new Rectangle(node.getLocation().x - d.width/2, node.getLocation().y - d.height/2, d.width, d.height);
	scrollRectToVisible(scrollTo);
    }

    public void scrollNodeToVisible( NodeView node ) {
	if (node != null) {
	    scrollRectToVisible( new Rectangle(node.getLocation().x-20, node.getLocation().y,node.getSize().width+20,node.getSize().height) );
	}
    }

    public void moveLeft() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()){
	    Vector left = ((RootNodeView)oldSelected).getLeft();
	    if (left.size() == 0) return;
	    newSelected = (NodeView)left.firstElement();
	} else if(!oldSelected.isLeft()) {
	    newSelected = oldSelected.getParentView();
	} else {
	    if (oldSelected.getChildrenViews().size() == 0) return;
	    newSelected = (NodeView)oldSelected.getChildrenViews().firstElement();
	}
	select(newSelected);
	scrollNodeToVisible( newSelected );
    }

    public void moveRight() {
	NodeView oldSelected = getSelected();
	NodeView newSelected;
	if(oldSelected.isRoot()) {
	    Vector right = ((RootNodeView)oldSelected).getRight();
	    if (right.size() == 0) return;
	    newSelected = (NodeView)right.firstElement();
	} else if(oldSelected.isLeft()) {
	    newSelected = oldSelected.getParentView();
	} else {
	    if (oldSelected.getChildrenViews().size() == 0) return;
	    newSelected = (NodeView)oldSelected.getChildrenViews().firstElement();
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

    public void select(NodeView newSelected) {
	NodeView oldSelected = getSelected();
	//select new node
	this.selected = newSelected;
	newSelected.requestFocus();
	//	scrollNodeToVisible(newSelected);
	newSelected.repaint();
	if (oldSelected != null) {
	    oldSelected.repaint();
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
	return selected;
    }

    float getZoom() {
	return zoom;
    }

    public void setZoom(float zoom) {
	this.zoom = zoom;
	updateAll();
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
	for(Enumeration e = source.getChildrenViews().elements(); e.hasMoreElements(); ) {
	    NodeView target = (NodeView)e.nextElement();
	    
	    target.getEdge().update();
	    target.getEdge().paint(g);
		
	    paintEdges( target, g );//recursive
	}
    }
    
    private NodeView getRoot() {
	return rootView;
    }


    private void layoutAll() {
	getRoot().layoutChildren();
	setPreferredSize(getLayout().preferredLayoutSize(this));
	validate();
	//scrollNodeToVisible(getSelected());
	repaint();
    }

    private void updateAll() {
	getRoot().updateAll();
	getRoot().layoutChildren();
	revalidate();
	repaint();
    }

    /**
     * This class is a workaround to allow the inner class access to "this".
     * Change it as soon the correct syntax is known
     */
    private MapView getMap() {
	return this;
    }

    private Controller getController() {
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
	    repaint();//length of the node changes, so whole map has to be repainted
	}

	
	public void treeNodesInserted( TreeModelEvent e ) {
	    MindMapNode parent = (MindMapNode)e.getTreePath().getLastPathComponent();
	    NodeView parentView = parent.getViewer();
	    //works only with one child
	    MindMapNode child = (MindMapNode)e.getChildren()[0];
	    parentView.insert( child );
	    layoutAll();
	}

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
	    rootView.setPosition(0,0);
	    layoutAll();
	}

	public void treeNodesRemoved( TreeModelEvent e ) {
	    //This is called once for each removed node, with just one child (the removed)
	    NodeView node = ( (MindMapNode)e.getChildren()[0] ).getViewer();//Take care not to remove root in Controller
	    NodeView parent = ( (MindMapNode)e.getTreePath().getLastPathComponent() ).getViewer();
	    node.remove();//Just that one
	    select(parent);
	    layoutAll();
	}
    }
}
