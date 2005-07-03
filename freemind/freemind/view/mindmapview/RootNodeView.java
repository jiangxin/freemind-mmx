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
/*$Id: RootNodeView.java,v 1.14.14.6 2005-07-03 20:28:00 christianfoltin Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import java.util.LinkedList;
import java.util.ListIterator;
import java.awt.*;

/**
 * This is a RootNode with different placing of children
 * and different painting than a normal NodeView
 */
public class RootNodeView extends NodeView {
    // sum width of the left child tree
	protected int leftTreeWidth = 0;
	protected int rightTreeWidth = 0;
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

    /* fc, 26.06.2005 */
    /** Returns the point the edge should start given the index of the child node 
     * that should be connected.
     * @return
     */
    Point getOutPoint(Point destinationPoint, boolean isLeft) {
		if (false) {
			Dimension size = getSize();
			double nWidth = size.width;
			double nHeight = size.height;
			Point centerPoint = new Point(
					getLocation().x + (int) (nWidth / 2f), getLocation().y
							+ (int) (nHeight / 2f));
			// assume, that destinationPoint is on the right:
			double angle = Math.atan((destinationPoint.y - centerPoint.y + 0f)
					/ (destinationPoint.x - centerPoint.x + 0f));
			if (isLeft) {
				angle += Math.PI;
			}
			// now determine point on ellipsis corresponding to that angle:
			return new Point(centerPoint.x
					+ (int) (Math.cos(angle) * nWidth / 2f), centerPoint.y
					+ (int) (Math.sin(angle) * (nHeight) / 2f));
		}
        // old behaviour of 0.7.1:
		return (isLeft) ? getInPoint() : getOutPoint();
    }
    /* end fc, 26.06.2005 */

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

    LinkedList getLeft() {
	LinkedList all = getChildrenViews();
	LinkedList left = new LinkedList();
	for (ListIterator e = all.listIterator();e.hasNext();) {
	    NodeView node = (NodeView)e.next();
	    if (node == null) continue;
	    if (node.isLeft()) left.add(node);
	}
	return left;
    }

    LinkedList getRight() {
	LinkedList all = getChildrenViews();
	LinkedList right = new LinkedList();
	for (ListIterator e = all.listIterator();e.hasNext();) {
	    NodeView node = (NodeView)e.next();
	    if (node == null) continue;
	    if (!node.isLeft()) right.add(node);
	}
	return right;
    }

    void insert(MindMapNode newNode) {
        NodeView newView = newNodeView(newNode,getMap());
        newView.update();
        // decide left or right only if not actually set:
        if(newNode.isLeft()==null)
            newView.setLeft( getLeft().size() <= getRight().size() );
        ListIterator it = newNode.childrenFolded();
        if (it != null) {
            for (;it.hasNext();) {
                MindMapNode child = (MindMapNode)it.next();
                newView.insert(child);
            }
        }
    }	

    public boolean dropAsSibling (double xCoord) {
        return false;
    }

    /** @return true if should be on the left, false otherwise.*/
    public boolean dropPosition (double xCoord) {
       return xCoord < getSize().width*1/2 ; 
    }

    public void setDraggedOver(Point p) {
        setDraggedOver ((dropPosition(p.getX())) ? NodeView.DRAGGED_OVER_SON_LEFT : NodeView.DRAGGED_OVER_SON); 
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
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	if (this.getModel()==null) return;

        paintSelected(g, size);
        paintDragOver(g, size);

	//Draw a root node
	g.setColor(Color.gray);
	g.setStroke(new BasicStroke(1.0f));
        setRendering(g);
	g.drawOval(1,1,size.width-2,size.height-2);
        if (!getMap().getController().getAntialiasAll()) {
           g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); }

	super.paint(g);
    }

   public void paintDragOver(Graphics2D graphics, Dimension size) {
        if (getDraggedOver() == DRAGGED_OVER_SON) {
              graphics.setPaint( new GradientPaint(size.width/4,0,map.getBackground(), size.width*3/4, 0, dragColor));
              graphics.fillRect(size.width/4, 0, size.width-1, size.height-1); 
        } else if (getDraggedOver() == DRAGGED_OVER_SON_LEFT) {
              graphics.setPaint( new GradientPaint(size.width*3/4,0,map.getBackground(), size.width/4, 0, dragColor));
              graphics.fillRect(0, 0, size.width*3/4, size.height-1);
        }
    }


   protected void setRendering(Graphics2D g) {
      if (getMap().getController().getAntialiasEdges() || getMap().getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}


    protected void paintBackground(
        Graphics2D graphics,
        Dimension size,
        Color color) {
		graphics.setColor(color);
		graphics.fillOval(1,1,size.width-1,size.height-1);
    }

	public int getRightTreeWidth() {
		return rightTreeWidth;
	}
    public int getLeftTreeWidth() {
        return leftTreeWidth;
    }

    public void setTreeWidth(int w) {
    	throw new Error();
    }
    
   public void setRootTreeWidths(int left, int right) {
        leftTreeWidth = left - getPreferredSize().width;
        rightTreeWidth = right ;
        super.setTreeWidth(leftTreeWidth + rightTreeWidth );
    }
	public void setRootTreeHeights(int left, int right) {
		if(left > right){
			super.setTreeHeight(left);
		}
		else{
			super.setTreeHeight(right);
		}		
	}
	public void setRootUpperChildShift(int left, int right) {
		super.setUpperChildShift(Math.max(left,right));
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.NodeView#getStyle()
	 */
	String getStyle() {
		return model.getStyle();
	}

	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		prefSize.width += Math.max(10, prefSize.width / 10);
		prefSize.height *= 2;
	    return prefSize;
	}

}



//     /**
//      * Determines the logical Position of all of its direct children.
//      */
//     public void layoutChildren() {
// 	if (getChildrenViews().size() == 0) return;//nothing to do
// 	layoutRoot();
    //    } 
  
