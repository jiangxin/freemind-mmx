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
/*$Id: MindMapLayout.java,v 1.14 2003-11-03 11:00:27 sviles Exp $*/

package freemind.view.mindmapview;

import freemind.main.FreeMindMain;
import java.awt.LayoutManager;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math;
import javax.swing.JLabel;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

    private final int BORDER = 30;//width of the border around the map.
    private final int HGAP_BASE = 20;//width of the horizontal gap that contains the edges
    private final int VGAP = 3;//height of the vertical gap between nodes
    private MapView map;
    private int ySize;
    private int totalXSize;

    public MindMapLayout(MapView map) {
        this.map = map;
        ySize = Integer.parseInt(getFrame().getProperty("mapysize"));
        totalXSize = Integer.parseInt(getFrame().getProperty("mapxsize")); }    

    public void addLayoutComponent(String name, Component comp){ }

    public void removeLayoutComponent(Component comp) {  }

    public void layoutContainer(Container parent) {
       layout( map.getRoot() ); }
   




    //
    // Absolute positioning
    //

    /**
     * Make abolute positioning for all nodes providing that relative and heights
     * are up to date.
     */

    public void layout() {
       layout(map.getRoot()); }

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

        //Recursion
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
            node.setBounds(totalXSize / 2 - node.getPreferredSize().width/2,
                           ySize / 2      - node.getPreferredSize().height/2,
                           node.getPreferredSize().width,
                           node.getPreferredSize().height); }
        else {
            //place the node-label
            int x = node.getParentView().getLocation().x + relativeX;
            int y = node.getParentView().getLocation().y + relativeY;

            //check if the map is to small
            if ( x < 0 || x + node.getPreferredSize().width > map.getSize().width ) {
                if (node.isLeft()) {
                   resizeMap(x); }
                else {
                  resizeMap(x + node.getPreferredSize().width); }
                  // Daniel: why do we return??
                  // Petr: perhaps: extension to the right needs no move 
                  //       of the already placed objects to place the new node.
                  //       The boundary is just bigger enough and that's it.
                  //       Perhaps the following code makes the move...
                  return; 
                }

            node.setBounds(x, y, node.getPreferredSize().width, node.getPreferredSize().height);
            
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


    /**
     *
     */
    public void resizeMap( int outmostX ) {
        // (public to reuse from MapView.scrollNodeToVisible  ...)

        // In principle, resize can be caused by:
        // 1) Unfold
        // 2) Insertion of a node
        // 3) Modification of a node in an enlarging way

        int oldTotalXSize = totalXSize;
        totalXSize = BORDER*2 + (outmostX < 0 ? totalXSize + -outmostX  : outmostX );

        getMapView().setSize(totalXSize, ySize);
        // Scroll by the amount, by which the Root node was shifted
        getMapView().scrollBy((totalXSize - oldTotalXSize) / 2 , 0);

        layout(map.getRoot());
        //      getMap().validate();
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
       if (node.isRoot()) {
          updateRelativeYOfChildren(node); }
       else {
          updateTreeHeightFromChildren(node);
          updateRelativeYOfChildren(node);
          updateTreeHeightsAndRelativeYOfAncestors(node.getParentView()); }}

    //
    // Relative positioning
    //

    // Definiton: relative vertical is either relative Y coord or Treeheight.

    void updateTreeHeightsAndRelativeYOfWholeMap() {
        updateTreeHeightsAndRelativeYOfDescendants(getRoot()); }

   
    void updateTreeHeightsAndRelativeYOfDescendants(NodeView node) {
        for (ListIterator e = node.getChildrenViews().listIterator(); e.hasNext();) {
           updateTreeHeightsAndRelativeYOfDescendants((NodeView)e.next()); }
        updateTreeHeightFromChildren(node);
        updateRelativeYOfChildren(node); } // The order of the two lines is irrelevant.

    private void updateRelativeYOfChildren(NodeView node) {
       // Current clients: updateTreeHeightsAndRelativePositions, subtreeChanged
        if (node.isRoot()) {
            // Left children
            int pointer = -(sumOfAlreadyComputedTreeHeights( getRoot().getLeft() ) / 2);
            for ( ListIterator e = getRoot().getLeft().listIterator(); e.hasNext(); ) {
                NodeView child = (NodeView)e.next();
                pointer += (child.getTreeHeight() / 2);
                child.relYPos = pointer - 2;
                pointer += (child.getTreeHeight() / 2); } 
            // Right children
            pointer = -(sumOfAlreadyComputedTreeHeights( getRoot().getRight() ) / 2);
            for ( ListIterator e = getRoot().getRight().listIterator(); e.hasNext(); ) {
                NodeView child = (NodeView)e.next();
                pointer += (child.getTreeHeight() / 2);
                child.relYPos = pointer - 2;
                pointer += (child.getTreeHeight() / 2); }}
        else {
            int pointer = (node.getPreferredSize().height - node.getTreeHeight()) / 2;
            ListIterator it = node.getChildrenViews().listIterator();
            while(it.hasNext()) {
                NodeView child = (NodeView)it.next();
                child.relYPos = pointer + (child.getTreeHeight() - child.getPreferredSize().height) / 2 - 2;
                //This point is called twice for every node. Why?
                pointer += child.getTreeHeight(); }}}

    private int sumOfAlreadyComputedTreeHeights( LinkedList v ) {
       if ( v == null || v.size() == 0 ) {
          return 0; }
       int height = 0;
       for (ListIterator e = v.listIterator(); e.hasNext(); ) {
           NodeView node = (NodeView)e.next();
           if (node != null) {
              height += node.getTreeHeight(); }}
       return height; }

    protected void updateTreeHeightFromChildren(NodeView node) {
       node.setTreeHeight(Math.max(sumOfAlreadyComputedTreeHeights(node.getChildrenViews()),
                                   node.getPreferredSize().height + VGAP)); }


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
        return new Dimension(totalXSize, ySize); }


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
