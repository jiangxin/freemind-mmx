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
/*$Id: ForkNodeView.java,v 1.10.16.1 2004-08-22 14:28:12 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import java.awt.*;

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

    public Dimension getPreferredSize() {
	return new Dimension(super.getPreferredSize().width,
                             super.getPreferredSize().height + 3 + getEdge().getRealWidth());
    }	
  
    /**
     * Paints the node
     */
	public void paintFoldingMark(Graphics2D g){ 
		if(getModel().isFolded()) {
			final Point out = getOutPoint(); 
			if (isLeft())
			{
				g.drawOval(out.x - FOLDING_SYMBOL_WIDTH , out.y - FOLDING_SYMBOL_WIDTH/2, FOLDING_SYMBOL_WIDTH, FOLDING_SYMBOL_WIDTH);
			}
			else
			{
				g.drawOval(out.x, out.y - FOLDING_SYMBOL_WIDTH/2, FOLDING_SYMBOL_WIDTH, FOLDING_SYMBOL_WIDTH);
			}
		}        
	}

    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	//Dimension size = getPreferredSize();

	if (this.getModel()==null) return;

        paintSelected(g, size);
        paintDragOver(g, size);

        int edgeWidth = getEdge().getRealWidth();

	//Draw a standard node
        setRendering(g);
	g.setColor(getEdge().getColor());
	g.setStroke(getEdge().getStroke());
	g.drawLine(0,          size.height-edgeWidth/2-1,
                   size.width, size.height-edgeWidth/2-1);
   
	super.paint(g);
    }
}









