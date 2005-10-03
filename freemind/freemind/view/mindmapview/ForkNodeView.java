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
/*$Id: ForkNodeView.java,v 1.10.18.2.6.3 2005-10-03 15:07:59 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.attributeview.AttributeView;

import java.awt.*;

import javax.swing.JLabel;

/**
 * This class represents a single Fork-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class ForkNodeView extends MoveableNodeView {

	public final int FOLDING_WIDTH_OVERHEAD = 3;

    
    //
    // Constructors
    //
    
    public ForkNodeView(MindMapNode model, MapView map) {
	super(model,map);
    }
    
    public int getDeltaX(){
		if(getModel().isFolded() && isLeft()){
		    return super.getDeltaX()+ getZoomedFoldingSymbolHalfWidth() * 2 + FOLDING_WIDTH_OVERHEAD;
		}
		return super.getDeltaX();
    }
    
    protected int getMainViewWidthWithFoldingMark( )
	{
        int width = getMainView().getWidth();
		if(getModel().isFolded()){
			width += getZoomedFoldingSymbolHalfWidth() * 2 + FOLDING_WIDTH_OVERHEAD;
		}
		return width;
	}
  
	protected int getMainViewHeightWithFoldingMark()
	{
	    int height = getMainView().getHeight();
		if(getModel().isFolded()){
			height += getZoomedFoldingSymbolHalfWidth();
		}
		return height;
	}
  
  
    /**
     * Paints the node
     */
	public void paintFoldingMark(Graphics2D g){ 
		if(getModel().isFolded()) {
			Point out = getOutPoint();
			out.translate(0, 1); 
			if (isLeft())
			{
				g.drawLine(out.x - FOLDING_WIDTH_OVERHEAD, out.y, out.x, out.y);
				g.drawOval(out.x - (getZoomedFoldingSymbolHalfWidth() * 2 + FOLDING_WIDTH_OVERHEAD) , out.y - getZoomedFoldingSymbolHalfWidth(), getZoomedFoldingSymbolHalfWidth() * 2, getZoomedFoldingSymbolHalfWidth() * 2);
			}
			else
			{
				g.drawLine(out.x, out.y, out.x + FOLDING_WIDTH_OVERHEAD, out.y);
				g.drawOval(out.x + FOLDING_WIDTH_OVERHEAD, out.y - getZoomedFoldingSymbolHalfWidth(), getZoomedFoldingSymbolHalfWidth() * 2, getZoomedFoldingSymbolHalfWidth() * 2);
			}
		}        
	}

    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getMainView().getSize();
	//Dimension size = getPreferredSize();

	if (this.getModel()==null) return;

        paintSelected(g);
        paintDragOver(g);

        int edgeWidth = getEdge().getRealWidth();

	//Draw a standard node
        setRendering(g);
	g.setColor(getEdge().getColor());
	g.setStroke(getEdge().getStroke());
	g.drawLine(getMainView().getX(),          
	        getMainView().getY() + size.height-edgeWidth/2-1,
                   getMainView().getX() + size.width, 
                   getMainView().getY() + size.height-edgeWidth/2-1);
	super.paint(g);
    }

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.NodeView#getStyle()
	 */
	String getStyle() {
		return MindMapNode.STYLE_FORK;
	}
}









