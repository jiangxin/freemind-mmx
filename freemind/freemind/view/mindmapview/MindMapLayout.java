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
/*$Id: MindMapLayout.java,v 1.15.14.1 2004-10-17 20:01:08 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.main.FreeMindMain;
import freemind.modes.MindMapCloud;

import java.awt.IllegalComponentStateException;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.JLabel;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

    private final static int BORDER = 30;//width of the border around the map.
    private final static int HGAP_BASE = 20;//width of the horizontal gap that contains the edges
	private final static int VGAP = 3;//height of the vertical gap between nodes
	private final static int SHIFT = 2;//height of the vertical shift between node and its closest child
	// minimal width for input field of leaf or folded node (PN)
	
	// the MINIMAL_LEAF_WIDTH is reserved by calculation of the map width
	public final static int MINIMAL_LEAF_WIDTH = 150;
	public final static int MINIMAL_WIDTH = 50;


    private MapView map;
    private int ySize;
    
    // Call xSize and ySize in the same manner
    private int xSize;
    
    // save root node coordinates
	private int rootX = 0;
	private int rootY = 0;

    public MindMapLayout(MapView map) {
        this.map = map;
    // properties "mapxsize" and "mapysize" are ignored
    }

    public void addLayoutComponent(String name, Component comp){ }

    public void removeLayoutComponent(Component comp) {  }

    public void layoutContainer(Container parent) {
       layout(); }
   




    //
    // Absolute positioning
    //

    /**
	* placeNode throws this exception 
	* if the map size changes require new layout calculations 
     */
	private class NewLayoutIterationNeeded extends Exception {
	}

    /**
     * This funcion resizes the map and do the layout.
     * All tree heights, widths and shifts should be already calculated.
     */
	public void layout() {
		NodeView selected = map.getSelected();
		boolean holdSelected =  (selected != null 
								&& selected.getX() != 0 && selected.getY() != 0);
		int oldRootX = getRoot().getX();
		int oldRootY = holdSelected ? selected.getY() : getRoot().getY();
		resizeMap(getRoot().getTreeWidth(), getRoot().getTreeHeight());
		calcNewRootCoord();
        layout(map.getRoot());
		try{
//			getRoot().getLocationOnScreen();
			int rootX = getRoot().getX();
			int rootY = holdSelected ? selected.getY() : getRoot().getY();
			getMapView().scrollBy(rootX - oldRootX, rootY - oldRootY, true );
		}
		catch(IllegalComponentStateException e){
		}

	}

    /**
     * This places the node's subtree if the relative
     * position of every node to its parent is known.
     */
    private void layout(NodeView node) {
        // The overall task of layout is: give me the sizes and I'll set the positions.

        // Relative Y positions of nodes are already calculated.  Here we
        // only calculate relative x positions of nodes.  Whenever we talk
        // about relative coordinates / positions, we always mean relative to
        // the coordinates of node's parent.

        int x = 0;
        int hgap = map.getZoomed(HGAP_BASE);
        if ( node.isRoot() ) {
           //System.err.println("layoutroot"+node.getModel()); 
           x = 0; }
        else if ( node.isLeft() ) {
           x = - hgap - node.getPreferredSize().width; }
        else {
           x = node.getParentView().getPreferredSize().width + hgap; }
        
        placeNode(node, x, node.relYPos);

        //Iterations
        for ( ListIterator e = node.getChildrenViews().listIterator(); e.hasNext(); ) {
           layout( (NodeView)e.next() ); }
    }

    /**
     * Set the position and the size of the node.
     * Set the position of the edge of the node (the edge of the node is the edge
     * connecting the node to its parent).
     *
     * Preconditions: Absolute position of the parent is already set correctly.
     *                Relative positions and TreeHeights are up to dare.
     */
    private void placeNode(NodeView node, int relativeX, int relativeY) {
        // The placeNode is currently called only by layout().

        // I don't undertand why I reset the size of the node according to its preferred size.
        // This should not be the task of this method.

        // relativeX, relativeY - already calculated coordinates of node relative to its parent.;
        if (node.isRoot()) {
            node.setExtendedBounds(getRootX(),
                           getRootY(),
                           node.getPreferredSize().width,
                           node.getPreferredSize().height); }
        else {
            //place the node-label
            int x = node.getParentView().getExtendedX() + relativeX;
            int y = node.getParentView().getExtendedY() + relativeY;
            node.setExtendedBounds(x, y, node.getPreferredSize().width, node.getPreferredSize().height);
            
            // It seems that there is a piece of coding ready for having labelled edges.
            // Having labelled edges is a nice thing, as sure as hell, but we do not
            // have the support yet.

            //place the edge-label
            JLabel label = node.getEdge().getLabel();
            Point start = node.getParentView().getOutPoint();
            Point end = node.getInPoint();
        
            if (node.getParentView().isRoot()) {
               if (node.isLeft()) {
                  start = node.getParentView().getInPoint(); }}

            node.getEdge().start = start;
            node.getEdge().end = end;

            int relX = (start.x - end.x) / 2; 
            int absX = start.x - relX;
            
            int relY = (start.y - end.y) / 2;
            int absY = start.y - relY;
                    
            label.setBounds(absX - label.getPreferredSize().width  / 2,
                            absY - label.getPreferredSize().height / 2,
                            label.getPreferredSize().width, label.getPreferredSize().height);
        }
    }

	/** returns the y coordinate of the root node. */
    private int getRootY() {
		return rootY;
    }

	/** returns the x coordinate of the root node. */
    private int getRootX() {
		return rootX;
    }

	private void calcNewRootCoord() {
		int middleX = (getXSize() - getRoot().getPreferredSize().width)/ 2; 
		rootX = calcXBorderSize()/2 + getRoot().getLeftTreeWidth();

		if (middleX > rootX){
			rootX = middleX;
			int freeWidth = getXSize() + getRoot().getLeftTreeWidth()
			 - rootX - getRoot().getTreeWidth() - calcXBorderSize()/2;				
			if (freeWidth < 0){
				rootX += freeWidth;
			}
		}
		
		rootY = (getYSize() - getRoot().getPreferredSize().height)/ 2;

		int freeHeigth = getYSize() - getRoot().getTreeHeight() - getRoot().getTreeShift() - calcYBorderSize();
		if (freeHeigth < 0){
			rootY -= freeHeigth;
		}
		
	}

    /**
     *
     */
        private void resizeMap(int width, int height) {
        // (public to reuse from MapView.scrollNodeToVisible  ...)

        // In principle, resize can be caused by:
        // 1) Unfold
        // 2) Insertion of a node
        // 3) Modification of a node in an enlarging way
            boolean bResized = false;
        	int oldXSize = getXSize();
        	int oldYSize = getYSize();
        
			int minXSize = width + calcXBorderSize();
            int minYSize = height  +  calcYBorderSize();

         	if (minXSize != getXSize()) {
        		setXSize(minXSize);
        		bResized = true;
        	}

        	if (minYSize != getYSize()) {
        		setYSize(minYSize);
        		bResized = true;
        	}

        	if (bResized){
        		getMapView().setSize(getXSize(), getYSize());
        	}
    }

    private int calcYBorderSize() {
        int yBorderSize;
        	{
        	Dimension visibleSize = map.getViewportSize();
        	if (visibleSize != null){
        		yBorderSize = Math.max(visibleSize.height, 2 *  BORDER);
        	}
        	else{
        		yBorderSize = 2 *  BORDER;
        		
        	}
        }
        return yBorderSize;
    }

    private int calcXBorderSize() {
        int xBorderSize;
        {
        	Dimension visibleSize = map.getViewportSize();
        	if (visibleSize != null){
        		xBorderSize = Math.max(visibleSize.width, 2 *(BORDER + MINIMAL_LEAF_WIDTH));
        	}
        	else{
        		xBorderSize = 2 *(BORDER + MINIMAL_LEAF_WIDTH);
        		
        	}
        }
        return xBorderSize;
    }


    void updateTreeHeightsAndRelativeYOfDescendantsAndAncestors(NodeView node) {
       updateTreeHeightsAndRelativeYOfDescendants(node);       
       updateTreeHeightsAndRelativeYOfAncestors(node); }

    /**
     * This is called by treeNodesChanged(), treeNodesRemoved() & treeNodesInserted(), so it's the
     * standard mechanism to update the graphical node structure. It updates the parent of the 
     * significant node, and follows recursivly the hierary upwards to root.
     */

    void updateTreeHeightsAndRelativeYOfAncestors(NodeView node) {
		updateTreeHeightWidthShiftFromChildren(node);
          updateRelativeYOfChildren(node);
       if ( !node.isRoot()){
          updateTreeHeightsAndRelativeYOfAncestors(node.getParentView()); }
    }

    //
    // Relative positioning
    //

    // Definiton: relative vertical is either relative Y coord or Treeheight.

    void updateTreeHeightsAndRelativeYOfWholeMap() {
        updateTreeHeightsAndRelativeYOfDescendants(getRoot()); 
		layout();
        }

   
    void updateTreeHeightsAndRelativeYOfDescendants(NodeView node) {
        for (ListIterator e = node.getChildrenViews().listIterator(); e.hasNext();) {
           updateTreeHeightsAndRelativeYOfDescendants((NodeView)e.next()); }
        updateTreeHeightWidthShiftFromChildren(node);
        updateRelativeYOfChildren(node); } // The order of the two lines is irrelevant.

    private void updateRelativeYOfChildren(NodeView node) {
       // Current clients: updateTreeHeightsAndRelativePositions, subtreeChanged
        if (node.isRoot()) {
            // Left children
            int pointer =  (node.getPreferredSize().height - sumOfAlreadyComputedTreeHeights( getRoot().getLeft()))/ 2;
			if (pointer < 0) {
			   pointer -= node.getTreeShift(); 
			}
            for ( ListIterator e = getRoot().getLeft().listIterator(); e.hasNext(); ) {
				pointer = updateRelativeYofChild(pointer, e, node.getTreeShift());
            } 
            // Right children
			pointer = (node.getPreferredSize().height - sumOfAlreadyComputedTreeHeights( getRoot().getRight()))/ 2;
			if (pointer < 0) {
				pointer -= node.getTreeShift(); 
			}
            for ( ListIterator e = getRoot().getRight().listIterator(); e.hasNext(); ) {
				pointer = updateRelativeYofChild(pointer, e, node.getTreeShift());
            }
        }
        else {
			int pointer  = (node.getPreferredSize().height - node.getTreeHeight()) / 2 - node.getTreeShift();
            ListIterator it = node.getChildrenViews().listIterator();
            while(it.hasNext()) {
				pointer = updateRelativeYofChild(pointer, it, node.getTreeShift());
		}}}

        private int getZoomedShift() {
            return (int )(SHIFT * getMapView().getZoom());
        }

        private int updateRelativeYofChild(int pointer, ListIterator e, int parentShift) {
			int vgap = (int) ( VGAP * getMapView().getZoom());
               NodeView child = (NodeView)e.next();
			   int iAdditionalCloudHeigth = child.getAdditionalCloudHeigth();
                child.relYPos = pointer  
                + (child.getTreeHeight() + iAdditionalCloudHeigth
                    - child.getPreferredSize().height                      
                ) / 2 + child.getTreeShift();
                return pointer + child.getTreeHeight() + vgap + iAdditionalCloudHeigth; 
        }

    private int sumOfAlreadyComputedTreeHeights( LinkedList v ) {
       if ( v == null || v.size() == 0 ) {
          return 0; }
       int height = 0;
       int vgap = (int) ( VGAP * getMapView().getZoom());
       for (ListIterator e = v.listIterator(); e.hasNext(); ) {
           NodeView node = (NodeView)e.next();
           if (node != null) {
              height += node.getTreeHeight() + vgap + node.getAdditionalCloudHeigth(); }}
       return height - vgap; }

    // renamed from updateTreeHeightAndShiftFromChildren
    protected void updateTreeHeightWidthShiftFromChildren(NodeView node) {
    	int sumHeights; 
		int preferredHeight = node.getPreferredSize().height;
		int preferredWidth = node.getPreferredSize().width;
    	int treeHeight;
    	int treeWidth;
    	int wholeShift;
    	if (node.isRoot()){
    		LinkedList leftNodeViews = getRoot().getLeft();
			LinkedList rightNodeViews = getRoot().getRight();
			int heightLeft = sumOfAlreadyComputedTreeHeights(leftNodeViews);
			int heightRight = sumOfAlreadyComputedTreeHeights(rightNodeViews);

			int widthLeft = calcTreeWidth(leftNodeViews);
			getRoot().setLeftTreeWidth(widthLeft);
			int widthRight = calcTreeWidth(rightNodeViews);
			treeWidth = widthLeft + widthRight + node.getPreferredSize().width;

			if (heightLeft > heightRight){
				wholeShift = calcShift(leftNodeViews);
				sumHeights = heightLeft; 
			}
			else {
				wholeShift = calcShift(rightNodeViews);
				sumHeights = heightRight;
			}
    	}
    	else{
			LinkedList childrenViews = node.getChildrenViews();
    		wholeShift = calcShift(childrenViews);
    		sumHeights = sumOfAlreadyComputedTreeHeights(childrenViews);
			treeWidth = node.getPreferredSize().width 
			+ node.getAdditionalCloudHeigth() 
			+ calcTreeWidth(childrenViews);
    	}

    	int shift = getZoomedShift();
    	if(preferredHeight >= sumHeights + shift){
    		treeHeight = preferredHeight;
    	}
    	else {
			wholeShift += shift;
    		if (preferredHeight + 2 * wholeShift > sumHeights){
				treeHeight = preferredHeight + wholeShift + (sumHeights-preferredHeight)/2;
	    	}
	    	else {
	    		treeHeight = sumHeights;
	    	}
		}
		node.setTreeHeight(treeHeight); 
		node.setTreeWidth(treeWidth); 
    	node.setTreeShift(wholeShift); }

	private int calcTreeWidth(LinkedList childrenViews) {
		int treeWidth = 0;
		for (ListIterator e = childrenViews.listIterator(); e.hasNext(); ) {
			NodeView childNode = (NodeView)e.next();
			if (childNode != null) {
				int childWidth = childNode.getTreeWidth();
				if(childWidth > treeWidth){
					treeWidth = childWidth;
				}
			}
		}
		int hgap = (int) ( HGAP_BASE * getMapView().getZoom());		
		return treeWidth + hgap;
	}
	
	private int calcShift(LinkedList childrenViews) {
		int iShift;
		try{ 
			NodeView nodeFirstChild = (NodeView)childrenViews.getFirst();
			iShift = nodeFirstChild.getTreeShift();
		}
		catch(NoSuchElementException e){
			iShift = 0;
		}
		return iShift;
	}

    //
    // Get Methods
    //

    private RootNodeView getRoot() {
       return (RootNodeView)map.getRoot(); }

    private MapView getMapView() {
       return map; }

    private FreeMindMain getFrame() {
       return map.getController().getFrame(); }


    // This is actually never used.
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(200,200); } //For testing Purposes

    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(getXSize(), getYSize()); }


    private int getXSize() {
        return xSize;
    }

    /**
     * @return
     */
    private int getYSize() {
        return ySize;
    }

    /**
     * @param i
     */
    private void setXSize(int i) {
        xSize = i;
    }

    /**
     * @param i
     */
    private void setYSize(int i) {
        ySize = i;
    }

}//class MindMapLayout
















//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getLeftTreeHeight() {
//      int leftTreeHeight = 0; 
//      for (ListIterator e = getLeft().listIterator(); e.hasNext();) {
//          leftTreeHeight +=((NodeView)e.next()).getTreeHeight();
//      }
//      return leftTreeHeight;
//     }

//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getRightTreeHeight() {
//      int rightTreeHeight = 0;        
//      for (ListIterator e = getRight().listIterator(); e.hasNext();) {
//          rightTreeHeight +=((NodeView)e.next()).getTreeHeight();
//      }
//      return rightTreeHeight;
//     }









//     public int getTotalXSize() {
//      return totalXSize;
//     }




    //EXPERIMENTAL!
    

//     /**
//      * Belongs only to updateTotalXSize()
//      */
//     public int getXPos(NodeView node) {
//      int hgap = (int)(this.hgap * map.getZoom());
//      int x = 0;
//      if(node.isRoot()) {
//      } else if(node.isLeft()) {
//          while (!node.isRoot()) {
//              x -= node.getPreferredSize().width + hgap;
//              node = node.getParentView();
//          }
//      } else {
//          while (!node.isRoot()) {
//              NodeView parentView = node.getParentView();
//              x += parentView.getPreferredSize().width + hgap;
//              node = parentView;
//          }
//      }
//      return x;
//     }

//     /**
//      * This sucks to much computing power.
//      */
//     public void updateTotalXSize() {
//      int maxX = 0;
//      int minX = 0;
//      for (int i = 0;i < map.getComponentCount();i++) {
//          Component next = map.getComponent(i);
//          if ( next instanceof NodeView ) {
//              NodeView c = (NodeView)next;
//              int x = getXPos(c);
//              if (c.isLeft()) { //left
//                  if (x < minX) {
//                      minX = x;
//                  }
//              } else { //right
//                  //getXPos returns the upper left point of node, but we need the total distance from root:
//                  x += c.getPreferredSize().width;
//                  if (x > maxX) {
//                      maxX = x;
//                  }
//              }
//          }
//          minX = -minX;
//          if (maxX > minX) {
//              totalXSize = (maxX * 2)+30;
//          } else {
//              totalXSize = (minX * 2)+30;
//          }
//          if (totalXSize < minXSize) {
//              totalXSize = minXSize;
//          }
//      }
//     }
