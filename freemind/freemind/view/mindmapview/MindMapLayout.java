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
/*$Id: MindMapLayout.java,v 1.5 2000-11-03 22:49:20 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.main.FreeMind;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.Math;
import javax.swing.JLabel;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

    private int BORDER = 30;//width of the border around the map.
    private int hgap = 15;//width of the horizontal gap that contains the edges
    private int VGAP = 3;//height of the vertical gap between nodes
    private MapView map;
    private int ySize = Integer.parseInt(FreeMind.userProps.getProperty("mapysize"));
    private int totalXSize = Integer.parseInt(FreeMind.userProps.getProperty("mapxsize"));


    public MindMapLayout(MapView map) {
	this.map = map;
    }
    

    public void addLayoutComponent(String name, Component comp){
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void layoutContainer(Container parent) {
	layout( map.getRoot() );
    }

    /**
     * This places the node's subtree if the relative
     * position of every node to its parent is known.
     */
    private void layout(NodeView node) {

	int x = 0;
	if ( node.isRoot() ) {
	    x = 0;
	} else if ( node.isLeft() ) {
	    x = - hgap - node.getPreferredSize().width;
	} else {
	    x = node.getParentView().getPreferredSize().width + hgap;
	}
	
	placeNode(node, x,node.relYPos);

	//Recursion
	for ( Enumeration e = node.getChildrenViews().elements(); e.hasMoreElements(); ) {
	    layout( (NodeView)e.nextElement() );
	}
    }

    /**
     * Place a node relative to its parent.
     */
    private void placeNode( NodeView node, int relativeX, int relativeY) {
	int x, y;
	if(node.isRoot()) {
	    x = totalXSize/2 - node.getPreferredSize().width/2;
	    y = ySize / 2 - node.getPreferredSize().height/2;
	    node.setBounds(x,y,node.getPreferredSize().width,node.getPreferredSize().height);
	} else {

	    //place the node-label
		x = node.getParentView().getLocation().x + relativeX;
		y = node.getParentView().getLocation().y + relativeY;

	    //check if the map is to small
	    if ( x<0 || x + node.getPreferredSize().width > map.getSize().width ) {
		if (node.isLeft()) {
		    resizeMap(x);
		} else {
		    resizeMap(x + node.getPreferredSize().width);
		}
		return;
	    }

	    node.setBounds(x,y,node.getPreferredSize().width,node.getPreferredSize().height);
	    
	    //place the edge-label
	    JLabel label = node.getEdge().getLabel();
	    Point start = node.getParentView().getOutPoint();
	    Point end = node.getInPoint();
	
	    if(node.getParentView().isRoot()) {
		if( node.isLeft() ) {
		    start = node.getParentView().getInPoint();
		}
	    }

	    node.getEdge().start = start;
	    node.getEdge().end = end;

	    int relX = (start.x - end.x) / 2;
	    int absX = start.x - relX;
	    
	    int relY = (start.y - end.y) /2;
	    int absY = start.y - relY;
	    
	    Point loc = new Point(absX - label.getPreferredSize().width / 2, absY - label.getPreferredSize().height / 2);
	    
	    label.setBounds(loc.x,loc.y,label.getPreferredSize().width,label.getPreferredSize().height);
	}
    }


    /**
     * This is buggy!
     */
    private void resizeMap( int outmostX ) {
	int newXSize = totalXSize;
	if (outmostX < 0) {
	    newXSize = totalXSize + -outmostX + BORDER*2;
	} else {
	    newXSize = outmostX + BORDER*2;
	}

	totalXSize = newXSize;
	getMap().setSize(new Dimension(totalXSize, ySize));
	//	layoutContainer(getMap());
	layout(map.getRoot());
	//	getMap().validate();
    }


    /**
     * This is called by treeNodesChanged(), treeNodesRemoved() & treeNodesInserted(), so it's the
     * standard mechanism to update the graphical node structure. It updates the parent of the 
     * significant node, and follows recursivly the hierary upwards to root.
     */
    void subtreeChanged(NodeView parent) {
	if (!parent.isRoot()) {
	    calcTreeHeight(parent);
	    calcRelYPos(parent);
	    subtreeChanged(parent.getParentView());
	} else {
	    calcRelYPos(parent);
	}
	layout(map.getRoot());//inefficient!
    }


    void reinitialize() {
	reinitialize(getRoot());
    }

    void reinitialize(NodeView node) {
	for (Enumeration e = node.getChildrenViews().elements(); e.hasMoreElements();) {
	    reinitialize((NodeView)e.nextElement());
	}
	calcTreeHeight(node);
	calcRelYPos(node);
    }
	

    private void calcRelYPos( NodeView node ) {
	if (node.isRoot()) {
	    //first left...
	    int pointer = -(calcTreeHeight( getRoot().getLeft() ) / 2);
	    for ( Enumeration e = getRoot().getLeft().elements(); e.hasMoreElements(); ) {
		NodeView child = (NodeView)e.nextElement();
		
		//Calculate y position in pixels relative to Root
		pointer += (child.getTreeHeight() / 2);
		
		child.relYPos = pointer - 2;

		//		calcRelYPos( child );
		pointer += (child.getTreeHeight() / 2);
	    } //for (every Node)
	    //...then right
	    pointer = -(calcTreeHeight( getRoot().getRight() ) / 2);
	    for ( Enumeration e = getRoot().getRight().elements(); e.hasMoreElements(); ) {
		NodeView child = (NodeView)e.nextElement();
		
		//Calculate y position in pixels relative to Root
		pointer += (child.getTreeHeight() / 2);
		
		child.relYPos = pointer - 2;

		//		calcRelYPos( child );
		pointer += (child.getTreeHeight() / 2);
	    } //for (every Node)
	} else {
	    int pointer = -(node.getTreeHeight() / 2);
	    for ( Enumeration e = node.getChildrenViews().elements(); e.hasMoreElements(); ) {
		NodeView child = (NodeView)e.nextElement();
		//Calculate y position
		pointer += (child.getTreeHeight() / 2);
		int y = pointer;
	    
		child.relYPos = pointer - 2;

		//		calcRelYPos(child);

		pointer += (child.getTreeHeight() / 2);
	    } //for (every Node)
	}
    }

    private RootNodeView getRoot() {
	return (RootNodeView)map.getRoot();
    }

    private MapView getMap() {
	return map;
    }

    public Dimension minimumLayoutSize(Container parent) {
	return new Dimension(200,200);//For testing Purposes
    }

    public Dimension preferredLayoutSize(Container parent) {
	return new Dimension(totalXSize, ySize);
    }

    /**
     * 
     */
    private int calcTreeHeight( Vector v ) { //Returns the height of all NodeViews in the Vector
	if ( v == null || v.size() == 0 ) {
	    return 0;
	}
	int height = 0;
	for ( Enumeration e = v.elements(); e.hasMoreElements(); ) {
	    NodeView node = (NodeView)e.nextElement();
	    if (node == null) {
		break;
	    }
	    //	    calcTreeHeight(node);
	    height += node.getTreeHeight();
	}
	return height;
    }
    
    /**
     * 
     */
    protected void calcTreeHeight(NodeView node) { //Returns the height of this subtree in pixels;
	Vector v = node.getChildrenViews();
	int treeheight = calcTreeHeight(v);

	if (treeheight > node.getPreferredSize().height + VGAP) {
	    node.setTreeHeight(treeheight);
	} else {
	    node.setTreeHeight(node.getPreferredSize().height + VGAP);
	}
    }

}//class MindMapLayout
















//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getLeftTreeHeight() {
// 	int leftTreeHeight = 0;	
// 	for (Enumeration e = getLeft().elements(); e.hasMoreElements();) {
// 	    leftTreeHeight +=((NodeView)e.nextElement()).getTreeHeight();
// 	}
// 	return leftTreeHeight;
//     }

//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getRightTreeHeight() {
// 	int rightTreeHeight = 0;	
// 	for (Enumeration e = getRight().elements(); e.hasMoreElements();) {
// 	    rightTreeHeight +=((NodeView)e.nextElement()).getTreeHeight();
// 	}
// 	return rightTreeHeight;
//     }









//     public int getTotalXSize() {
// 	return totalXSize;
//     }




    //EXPERIMENTAL!
    

//     /**
//      * Belongs only to updateTotalXSize()
//      */
//     public int getXPos(NodeView node) {
// 	int hgap = (int)(this.hgap * map.getZoom());
// 	int x = 0;
// 	if(node.isRoot()) {
// 	} else if(node.isLeft()) {
// 	    while (!node.isRoot()) {
// 		x -= node.getPreferredSize().width + hgap;
// 		node = node.getParentView();
// 	    }
// 	} else {
// 	    while (!node.isRoot()) {
// 		NodeView parentView = node.getParentView();
// 		x += parentView.getPreferredSize().width + hgap;
// 		node = parentView;
// 	    }
// 	}
// 	return x;
//     }

//     /**
//      * This sucks to much computing power.
//      */
//     public void updateTotalXSize() {
// 	int maxX = 0;
// 	int minX = 0;
// 	for (int i = 0;i < map.getComponentCount();i++) {
// 	    Component next = map.getComponent(i);
// 	    if ( next instanceof NodeView ) {
// 		NodeView c = (NodeView)next;
// 		int x = getXPos(c);
// 		if (c.isLeft()) { //left
// 		    if (x < minX) {
// 			minX = x;
// 		    }
// 		} else { //right
// 		    //getXPos returns the upper left point of node, but we need the total distance from root:
// 		    x += c.getPreferredSize().width;
// 		    if (x > maxX) {
// 			maxX = x;
// 		    }
// 		}
// 	    }
// 	    minX = -minX;
// 	    if (maxX > minX) {
// 		totalXSize = (maxX * 2)+30;
// 	    } else {
// 		totalXSize = (minX * 2)+30;
// 	    }
// 	    if (totalXSize < minXSize) {
// 		totalXSize = minXSize;
// 	    }
// 	}
//     }
