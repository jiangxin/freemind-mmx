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
/*$Id: ForkNodeView.java,v 1.5 2001-03-24 22:45:46 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.modes.NodeAdapter;//This should not be done.
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import freemind.modes.MindMapNode;


/**
 * This class represents a single Fork-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class ForkNodeView extends NodeView {


    //
    // Constructors
    //
    
    public ForkNodeView(MindMapNode model, MapView map) {
	super(model,map);
    }

  
    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	if (this.getModel()==null) return;
	//Draw a standard node
	g.setColor(getEdge().getColor());
	g.drawLine(0,size.height-2,size.width,size.height-2);

	if( this.isSelected() ) {
	    g.setColor(selectedColor);
	    g.drawRect(0,0,size.width-1, size.height-1);
	}

	if (((NodeAdapter)getModel()).getLink() != null) {//THIS IS NO GOOD! NodeAdapter is too special.
	    graphics.setColor(Color.red);
	    graphics.drawLine(0,getSize().height-3,getSize().width,getSize().height-3);
	}
   
	super.paint(g);
    }
}









