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
/*$Id: BubbleNodeView.java,v 1.6 2001-04-06 20:50:11 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.modes.NodeAdapter;//This should not be done.
import freemind.modes.MindMapNode;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;


/**
 * This class represents a single Bubble-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class BubbleNodeView extends NodeView {

	private final static Stroke BOLD_STROKE =
			new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
						1f, new float[] {2f, 2f}, 0f);  
	private final static Stroke DEF_STROKE = new BasicStroke();

    //
    // Constructors
    //
    
    public BubbleNodeView(MindMapNode model, MapView map) {
	super(model,map);
	setHorizontalAlignment(CENTER);
	setVerticalAlignment(CENTER);
    }

    public Dimension getPreferredSize() {
	return new Dimension(super.getPreferredSize().width+10, super.getPreferredSize().height+4);
    }	
  
    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	if (this.getModel()==null) return;

	// change to bold stroke
	g.setStroke(BOLD_STROKE);

	//Draw a standard node
	g.setColor(getEdge().getColor());
	g.drawOval(0,0,size.width-1,size.height-1);

	// return to std stroke
	g.setStroke(DEF_STROKE);
	
	if( this.isSelected() ) {
	    g.setColor(selectedColor);
	    g.drawRect(0,2,size.width-1, size.height-5);
	}

	if (((NodeAdapter)getModel()).getLink() != null) {//THIS IS NO GOOD! NodeAdapter is too special.
	    graphics.setColor(Color.red);
	    graphics.drawLine(0,getSize().height-3,getSize().width,getSize().height-3);
	}
    
	super.paint(g);
    }
    /**
     * Returns the Point where the OutEdge
     * should leave the Node.
     */
    Point getOutPoint() {
	Dimension size = getSize();
	if (isLeft()) {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	} else {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
	}
    }

    /**
     * Returns the Point where the InEdge
     * should arrive the Node.
     */
    Point getInPoint() {
	Dimension size = getSize();
	if (isLeft()) {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
	} else {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	}
    }

    /**
     * Returns the relative position of the Edge
     */
    int getAlignment() {
	    return ALIGN_CENTER;
	}
}

