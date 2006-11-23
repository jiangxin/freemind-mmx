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
/*$Id: MindMapLayout.java,v 1.15.14.5.4.10 2006-11-23 21:09:44 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.IllegalComponentStateException;
import java.awt.LayoutManager;
import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import freemind.main.FreeMindMain;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

    private MapView map;
    private int ySize;
    
    // Call xSize and ySize in the same manner
    private int xSize;
    
    // save root node coordinates
	private int rootX = 0;
	private int rootY = 0;
    final static int BORDER = 30;//width of the border around the map.
    // minimal width for input field of leaf or folded node (PN)
    // the MINIMAL_LEAF_WIDTH is reserved by calculation of the map width
    public final static int MINIMAL_LEAF_WIDTH = 150;

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
     * This funcion resizes the map and do the layout.
     * All tree heights, widths and shifts should be already calculated.
     */
	private void layout() {
        final Point oldPoint;
        final int oldRootX = getRoot().getX();
        final int oldRootY = getRoot().getY();
        oldPoint = new Point(oldRootX, oldRootY);
        SwingUtilities.convertPointToScreen(oldPoint, map);
        preLayout();
        layout(map.getRoot());
        int rootX = getRoot().getX();
        int rootY = getRoot().getY();
        Point newPoint = new Point(rootX, rootY);
        SwingUtilities.convertPointToScreen(newPoint, map);
        final int scrollX = newPoint.x - oldPoint.x;
        final int scrollY = newPoint.y - oldPoint.y;
        if(scrollX == 0 && scrollY == 0){
            return;
        }
        try{
            getMapView().scrollBy(scrollX, scrollY, true );
        }
        catch(IllegalComponentStateException e){
        }
	}

    private void preLayout() {
        updateTreeHeightsAndRelativeYOfDescendants(getRoot()); 
		resizeMap(getRoot().getTreeWidth(), getRoot().getTreeHeight());
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

        node.validate();
        int x = 0;
        int hgap = node.getHGap();
        if ( node.isRoot() ) {
           //System.err.println("layoutroot"+node.getModel()); 
           x = 0; }
        else if ( node.isLeft() ) {
           x = - hgap - node.getWidth(); }
        else {
           x = node.getParentView().getWidth() + hgap; }
        
        placeNode(node, x, node.relYPos);

        //Iterations
        for ( ListIterator e = node.getChildrenViews(true).listIterator(); e.hasNext(); ) {
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
            node.setLocation(getRootX() , getRootY() ); 
            }
        else {
            //place the node-label
            int x = node.getParentView().getX() + relativeX;
            int y = node.getParentView().getY() + relativeY;
            node.setLocation(x , y );
            
            // It seems that there is a piece of coding ready for having labelled edges.
            // Having labelled edges is a nice thing, as sure as hell, but we do not
            // have the support yet.

            //place the edge-label
            JLabel label = node.getEdge().getLabel();
            /* fc, 26.06.2005 */
            Point end = node.getInPoint();
            Point start = node.getParentView().getOutPoint(end, node.isLeft());
            /* end fc, 26.06.2005 */
        

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

    /**
	 */
	private int getRootY() {
		return calcYBorderSize() + getRoot().getUpperChildShift();
	}

	/**
	 */
	private int getRootX() {
		return calcXBorderSize() + getRoot().getLeftTreeWidth();
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
			final int x = calcXBorderSize();
            int minXSize = width + x * 2;
			final int y = calcYBorderSize();
            int minYSize = height  +  y * 2;
			setXSize(minXSize);
			setYSize(minYSize);
            map.setSize(minXSize, minYSize);
    }

    //
    // Relative positioning
    //

    // Definiton: relative vertical is either relative Y coord or Treeheight.

    void updateTreeHeightsAndRelativeYOfDescendants(NodeView node) {
        if(node.getTreeHeight() != 0)
            return;
        if (node.getParentView() != null) node.setVisible(node.getModel().isVisible());
        for (ListIterator e = node.getChildrenViews(false).listIterator(); e.hasNext();) {
	           updateTreeHeightsAndRelativeYOfDescendants((NodeView)e.next()); }
        if(node.isVisible())
        updateTreeGeometry(node);
   	}
    
	private void updateRelativeYOfChildren(NodeView node, LinkedList childrenViews) {
		ListIterator e = childrenViews.listIterator();
		if (! e.hasNext())
			return ;
		int vgap = node.getVGap();
		NodeView child = (NodeView)childrenViews.getFirst();
		int pointer  = 0;  
		int upperShift = 0;
		while (e.hasNext()) {
			child = (NodeView)e.next();
			int shiftUp = getShiftUp(child);
			int shiftDown = getShiftDown(child);
			int iAdditionalCloudHeigth = child.getAdditionalCloudHeigth();
			int upperChildShift = child.getUpperChildShift();
			child.relYPos = pointer + upperChildShift
			+ iAdditionalCloudHeigth / 2  + shiftDown; 
			upperShift += upperChildShift + shiftUp;
			pointer +=  child.getTreeHeight()+ shiftUp + shiftDown + iAdditionalCloudHeigth + vgap;
		}
		upperShift += calcStandardTreeShift(node, childrenViews);
		while (e.hasPrevious()) {
			child = (NodeView)e.previous();
			child.relYPos -= upperShift; 
		}
	}

	  protected int getShiftUp(NodeView node) {
	  	int shift = node.getShift();
	  	if (shift < 0)
	  		return -shift;
	  	else
	  		return 0;
	}

	  protected int getShiftDown(NodeView node) {
	  	int shift = node.getShift();
	  	if (shift > 0)
	  		return shift;
	  	else
	  		return 0;
	}
    protected void updateTreeGeometry(NodeView node) {
    	if (node.isRoot()){
    		LinkedList leftNodeViews = getRoot().getLeft(true);
			LinkedList rightNodeViews = getRoot().getRight(true);

			int leftWidth = calcTreeWidth(node, leftNodeViews);
			int rightWidth = calcTreeWidth(node, rightNodeViews);
			getRoot().setRootTreeWidths(leftWidth, rightWidth);
			
			updateRelativeYOfChildren(node, leftNodeViews);
			updateRelativeYOfChildren(node, rightNodeViews);
			
			int leftTreeShift = calcUpperChildShift(node, leftNodeViews);
			int rightTreeShift = calcUpperChildShift(node, rightNodeViews);
			
			getRoot().setRootUpperChildShift(leftTreeShift, rightTreeShift);
            
            int minY = calcTopPosition(leftNodeViews, 0);
            minY = calcTopPosition(rightNodeViews, minY);
            int maxY = calcBottomPosition(leftNodeViews, node.getPreferredSize().height);
            maxY = calcBottomPosition(rightNodeViews, maxY);
            int treeHeight =  maxY - minY;			
            node.setTreeHeight(treeHeight);			
    	}
    	else{
			LinkedList childrenViews = node.getChildrenViews(true);

			int treeWidth = calcTreeWidth(node, childrenViews);
    		node.setTreeWidth(treeWidth); 

    		updateRelativeYOfChildren(node, childrenViews);

			int treeShift = calcUpperChildShift(node, childrenViews);
			node.setUpperChildShift(treeShift);
            int minY = calcTopPosition(childrenViews, 0);
            int maxY = calcBottomPosition(childrenViews, node.getPreferredSize().height);
			int treeHeight = maxY - minY;        	
    		node.setTreeHeight(treeHeight);    		
    	}
    }

    private int calcBottomPosition(LinkedList childrenViews, int maxY) {
        if(childrenViews.size() > 0){
        NodeView lastChild = (NodeView) childrenViews.getLast();
            int leftMaxY = lastChild.relYPos 
            - lastChild.getUpperChildShift()
            + lastChild.getTreeHeight() + lastChild.getAdditionalCloudHeigth()/2;
            if(leftMaxY > maxY){
                maxY = leftMaxY;
            }
        }
        return maxY;
    }

    private int calcTopPosition(LinkedList childrenViews, int minY) {
        if(childrenViews.size() > 0){
            NodeView firstChild = (NodeView) childrenViews.getFirst();
            int leftMinY = 
                firstChild.relYPos 
                - firstChild.getUpperChildShift()
                - firstChild.getAdditionalCloudHeigth()/2;
            if(leftMinY < minY){
                minY = leftMinY;
            }
        }
        return minY;
    }

	/**
	 */
	private int calcUpperChildShift(NodeView node, LinkedList childrenViews) {
		try{
			NodeView firstChild = (NodeView) childrenViews.getFirst();
			int childShift = -firstChild.relYPos 
				+ firstChild.getAdditionalCloudHeigth()/2
				+ firstChild.getUpperChildShift(); 
			return childShift > 0 ? childShift : 0;			
		}
		catch(NoSuchElementException e)
		{
			return 0;
		}
	}

	private int calcStandardTreeShift( NodeView parent, LinkedList childrenViews ) {
    	int parentHeight = parent.getPreferredSize().height;
       if ( childrenViews == null || childrenViews.size() == 0 ) {
          return 0; }
       int height = 0;
       int vgap = parent.getVGap();
       for (ListIterator e = childrenViews.listIterator(); e.hasNext(); ) {
           NodeView node = (NodeView)e.next();
           if (node != null) {
              height += node.getPreferredSize().height + vgap + node.getAdditionalCloudHeigth(); 
           }
       }
       return Math.max(height - vgap - parentHeight, 0) / 2; 
    }

	private int calcTreeWidth(NodeView parent, LinkedList childrenViews) {
		int treeWidth = 0;
		for (ListIterator e = childrenViews.listIterator(); e.hasNext(); ) {
			NodeView childNode = (NodeView)e.next();
			if (childNode != null) {
				int childWidth = childNode.getTreeWidth() + childNode.getHGap();
				if(childWidth > treeWidth){
					treeWidth = childWidth;
				}
			}
		}
		return parent.getPreferredSize().width 
		+ 2 * parent.getAdditionalCloudHeigth() 
		+ treeWidth;
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
    
    public Dimension preferredLayoutSize(Container c) {
        preLayout();
        return new Dimension(getXSize(), getYSize()); 
    }


    private int getXSize() {
        return xSize;
    }

    /**
     */
    private int getYSize() {
        return ySize;
    }

    /**
     */
    private void setXSize(int i) {
        xSize = i;
    }

    /**
     */
    private void setYSize(int i) {
        ySize = i;
    }

    int calcYBorderSize() {
        int yBorderSize;
        final int minBorderHeight = map.getZoomed(MindMapLayout.BORDER);
        Dimension visibleSize = map.getViewportSize();
        if (visibleSize != null){
            yBorderSize = Math.max(visibleSize.height, minBorderHeight);
        }
        else{
            yBorderSize = minBorderHeight;
        }
        return yBorderSize;
    }
    
    int calcXBorderSize() {
        int xBorderSize;
        Dimension visibleSize = map.getViewportSize();
        final int minBorderWidth = map.getZoomed(MindMapLayout.BORDER + MindMapLayout.MINIMAL_LEAF_WIDTH);
        if (visibleSize != null){
            xBorderSize = Math.max(visibleSize.width/2, minBorderWidth);
        }
        else{
            xBorderSize = minBorderWidth;
            
        }
        return xBorderSize;
    }

}//class MindMapLayout
