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
/*$Id: ForkNodeView.java,v 1.10.16.3 2004-08-28 07:07:34 dpolivaev Exp $*/

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
		Dimension result =  new Dimension(super.getPreferredSize().width,
                             super.getPreferredSize().height + 3 + getEdge().getRealWidth());
		if (getModel().isFolded()){
			result.width +=  getZoomedFoldingSymbolWidth() ;
			result.height +=  getZoomedFoldingSymbolWidth() / 2 ;
		}
		
        return result;
    }	
    
	public void setExtendedLocation(int x,	int y){
		if(getModel().isFolded() && isLeft()){
				x += getZoomedFoldingSymbolWidth();
		}
		setLocation(x, y);
	}
    
	public void setExtendedSize(int width,	int height){
		if(getModel().isFolded()){
			height -= getZoomedFoldingSymbolWidth() / 2;
			width -= getZoomedFoldingSymbolWidth();
		}
		setSize(width, height);
	}
    
    public int getExtendedX(){
    	int x = getX();
		if(getModel().isFolded() && isLeft()){
			x -= getZoomedFoldingSymbolWidth();
		}
		return x;
    }
    
	public int getExtendedWidth()
	{
		int width = getWidth();
		if(getModel().isFolded()){
			width += getZoomedFoldingSymbolWidth();
		}
		return width;
	}
  
	public int getExtendedHeight()
	{
		int height = getHeight();
		if(getModel().isFolded()){
			height += getZoomedFoldingSymbolWidth() / 2;
		}
		return height;
	}
  
  
    /**
     * Paints the node
     */
	public void paintFoldingMark(Graphics2D g){ 
		if(getModel().isFolded()) {
			final Point out = getOutPoint(); 
			if (isLeft())
			{
				g.drawOval(out.x - getZoomedFoldingSymbolWidth() , out.y - getZoomedFoldingSymbolWidth()/2, getZoomedFoldingSymbolWidth(), getZoomedFoldingSymbolWidth());
			}
			else
			{
				g.drawOval(out.x, out.y - getZoomedFoldingSymbolWidth()/2, getZoomedFoldingSymbolWidth(), getZoomedFoldingSymbolWidth());
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









