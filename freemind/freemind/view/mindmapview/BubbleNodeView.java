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
/*$Id: BubbleNodeView.java,v 1.13.4.1 2004-08-22 14:28:11 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import java.awt.*;

/**
 * This class represents a single Bubble-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class BubbleNodeView extends NodeView {

    private final static Stroke BOLD_STROKE =
		new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
					1f, new float[] {2f, 2f}, 0f);  
    private final static Stroke DEF_STROKE = new BasicStroke();


    public final int LEFT_WIDTH_OVERHEAD = 5;
    public final int LEFT_HEIGHT_OVERHEAD = 2;

    //
    // Constructors
    //
    
    public BubbleNodeView(MindMapNode model, MapView map) {
	super(model,map);
	setHorizontalAlignment(CENTER);
	setVerticalAlignment(CENTER);
    }



    public Dimension getPreferredSize() {
    	Dimension result = new Dimension(super.getPreferredSize().width+2*LEFT_WIDTH_OVERHEAD,
			super.getPreferredSize().height+4); 
    	if (getModel().isFolded())
			result.width +=  FOLDING_SYMBOL_WIDTH / 2 ;
		return result; 
    }	  

    public void paintSelected(Graphics2D graphics, Dimension size) {
       if( this.isSelected() ) {
          graphics.setColor(selectedColor);
          graphics.fillRoundRect(0,0,size.width-1,size.height-1,10,10);
       }
    }

	public void paintFoldingMark(Graphics2D g){ 
		if(getModel().isFolded()) {
            int height = getSize().height/2;
			// implement a maximum:
			final int MAX_HEIGHT = 50;
			if(height > MAX_HEIGHT)
				height = MAX_HEIGHT;

			Point ovalStartPoint = getOutPoint(); 
			Point arcStartPoint = getInPoint(); 
			arcStartPoint.translate( - height/2,  - height/2);
			if (isLeft())
			{
				g.drawArc( arcStartPoint.x , arcStartPoint.y , height,   height, 270,180);
				ovalStartPoint.translate(- FOLDING_SYMBOL_WIDTH/2 + 1 , - FOLDING_SYMBOL_WIDTH/2);
			}
			else
			{
				g.drawArc( arcStartPoint.x , arcStartPoint.y, height,   height,90,180);
				ovalStartPoint.translate(- FOLDING_SYMBOL_WIDTH/2 - 1 , - FOLDING_SYMBOL_WIDTH/2);
			}
			Color actualColor = g.getColor();
			g.setColor(model.getBackgroundColor());
			g.fillOval(ovalStartPoint.x + 1 , ovalStartPoint.y + 1, FOLDING_SYMBOL_WIDTH - 2, FOLDING_SYMBOL_WIDTH - 2);
			g.setColor(actualColor);
			g.drawOval(ovalStartPoint.x , ovalStartPoint.y , FOLDING_SYMBOL_WIDTH, FOLDING_SYMBOL_WIDTH);
		}
        
	}

    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getSize();
	if (this.getModel()==null) return;

        paintSelected(g, size);
        paintDragOver(g, size);

	// change to bold stroke
	//g.setStroke(BOLD_STROKE);                     // Changed by Daniel

        setRendering(g);

	//Draw a standard node
	g.setColor(getEdge().getColor());
	//g.drawOval(0,0,size.width-1,size.height-1);   // Changed by Daniel

        if (map.getController().getAntialiasEdges()) {
           g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        }
        g.drawRoundRect(0, 0,size.width-1,size.height-1,10,10);
        // return to old rendering, fc, 21.12.2003.
        setRendering(g);

	// return to std stroke
	g.setStroke(DEF_STROKE);

	super.paint(g);
    }

    public int getLeftWidthOverhead() {
       return LEFT_WIDTH_OVERHEAD; }

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

