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
import java.util.Vector;
import java.util.Enumeration;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

/**
 * This is a RootNode with different placing of children
 * and different painting than a normal NodeView
 */
public class RootNodeView extends NodeView {

    //
    // Constructors
    //
    
    public RootNodeView(MindMapNode model, MapView map) {
	super(model,map);
    }
    
    public NodeView getParentView() {
	return null;
    }
    

    /**
     * Returns the Point where the OutEdge
     * should leave the Node.
     */
    Point getOutPoint() {
	Dimension size = getSize();
	return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
    }

    /**
     * Returns the Point where the InEdge
     * should arrive the Node.
     */
    Point getInPoint() {
	Dimension size = getSize();
	return new Point(getLocation().x, getLocation().y + size.height / 2);
    }

    EdgeView getEdge() {
	return null;
    }

    void setEdge(EdgeView edge) {
    }

    Vector getLeft() {
	Vector all = getChildrenViews();
	Vector left = new Vector();
	for (Enumeration e = all.elements();e.hasMoreElements();) {
	    NodeView node = (NodeView)e.nextElement();
	    if (node == null) continue;
	    if (node.isLeft()) left.add(node);
	}
	return left;
    }

    Vector getRight() {
	Vector all = getChildrenViews();
	Vector right = new Vector();
	for (Enumeration e = all.elements();e.hasMoreElements();) {
	    NodeView node = (NodeView)e.nextElement();
	    if (node == null) continue;
	    if (!node.isLeft()) right.add(node);
	}
	return right;
    }

    void insert(MindMapNode newNode) {
  	NodeView newView = newNodeView(newNode,getMap());
	newView.update();
	//balancing should depend from Treeheight, not simply size
	if ( getRight().size() > getLeft().size() ) {
	    newView.setPosition(-1,0);
	    //Set it to be recognized left on next layout... bad solution
    	}
	for (Enumeration e = newNode.children();e.hasMoreElements();) {
	    MindMapNode child = (MindMapNode)e.nextElement();
	    newView.insert(child);
	}
      }	

    //
    // Navigation
    //

    public NodeView getNextSibling() {
	return this;
    }

    public NodeView getPreviousSibling() {
	return this;
    }


    //
    // Layout
    //

    /**
     * Determines the logical Position of all of its direct children.
     */
    public void layoutChildren() {
	if (getChildrenViews().size() == 0) return;//nothing to do
	layoutRoot();
    } 
  
    public Dimension getPreferredSize() {
	return new Dimension((int)(super.getPreferredSize().width*1.2),(int)(super.getPreferredSize().height*1.5));
    }	

    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	if (this.getModel()==null) return;
	//Draw a root node
	setHorizontalAlignment(CENTER);
	g.setColor(Color.red);
	g.setStroke(new BasicStroke(2.0f));
	g.drawOval(0,0,size.width-2,size.height-1);

	if( this.isSelected() ) {
	    g.setColor(selectedColor);
	    g.drawRect(0,2,size.width-1, size.height-5);
	}
	super.paint(g);
    }
	




    ////////
    // Private methods. Internal implementation.
    //////

    /**
     * Places all the direct children of root, including left/right balancing
     */
    private void layoutRoot() {
	    layout(getRight(),false);
	    layout(getLeft(),true);
    }

}









