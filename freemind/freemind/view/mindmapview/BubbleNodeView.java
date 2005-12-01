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
/*$Id: BubbleNodeView.java,v 1.14.14.4.6.4 2005-12-01 18:21:41 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.attributeview.AttributeView;

import java.awt.*;

import javax.swing.JLabel;

/**
 * This class represents a single Bubble-Style Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public class BubbleNodeView extends MoveableNodeView {

    private final static Stroke BOLD_STROKE =
		new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
					1f, new float[] {2f, 2f}, 0f);  
    private final static Stroke DEF_STROKE = new BasicStroke();

    //
    // Constructors
    //
    
    public BubbleNodeView(MindMapNode model, MapView map) {
    	super(model,map);
    }

  
	protected int getMainViewWidthWithFoldingMark()
	{
	    int width = getMainView().getWidth();
	    int dW = getZoomedFoldingSymbolHalfWidth() * 2;
		if(getModel().isFolded()){
			width += dW;
		}
		return width + dW;
	}
  
	public int getDeltaX()
	{
		if(getModel().isFolded() && isLeft()){
				return super.getDeltaX()+getZoomedFoldingSymbolHalfWidth() * 2;
		}
		return super.getDeltaX();
	}
  
    public void paintSelected(Graphics2D graphics) {
        super.paintSelected(graphics);
        if (this.isSelected()) {
            graphics.setColor(selectedColor);
            graphics.fillRoundRect(getMainView().getX(), getMainView().getY(), getMainView().getWidth()-1, getMainView().getHeight()-1, 10,
                    10);
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
			if (isLeft())
			{
				ovalStartPoint.translate(- getZoomedFoldingSymbolHalfWidth() * 2 , - getZoomedFoldingSymbolHalfWidth());
			}
			else
			{
				ovalStartPoint.translate(0 , - getZoomedFoldingSymbolHalfWidth());
			}
			g.drawOval(ovalStartPoint.x , ovalStartPoint.y , getZoomedFoldingSymbolHalfWidth() * 2, getZoomedFoldingSymbolHalfWidth() * 2);
		}
        
	}

    /**
     * Paints the node
     */
    public void paint(Graphics graphics) {
	Graphics2D g = (Graphics2D)graphics;
	Dimension size = getMainView().getSize();
	if (this.getModel()==null) return;

        paintSelected(g);
        paintDragOver(g);

	// change to bold stroke
	//g.setStroke(BOLD_STROKE);                     // Changed by Daniel

        setRendering(g);

	//Draw a standard node
	g.setColor(getEdge().getColor());
	//g.drawOval(0,0,size.width-1,size.height-1);   // Changed by Daniel

        if (map.getController().getAntialiasEdges()) {
           g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
        g.drawRoundRect(0,0, size.width-1, size.height-1,10,10);
        // this disables the font antialias if only AntialiasEdges is requested.
        if (map.getController().getAntialiasEdges()) {
           g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); }

	// return to std stroke
	g.setStroke(DEF_STROKE);

	super.paint(g);
    }

    /**
     * Returns the Point where the OutEdge
     * should leave the Node.
     */
    Point getOutPoint() {
        if( isLeft() ) {
            return new Point(getX() - 1, getY() + getMainView().getHeight() / 2);
        } else {
            return new Point(getX() + getMainView().getWidth(), getY() + getMainView().getHeight() / 2);
        } 
    }

    /**
     * Returns the Point where the InEdge
     * should arrive the Node.
     */
    Point getInPoint() {
        if (isLeft()) {
            return new Point(getX() + getMainView().getWidth(), getY() + getMainView().getHeight() / 2);
        } else {
            return new Point(getX() - 1, getY() + getMainView().getHeight() / 2);
        }
    }

    /**
     * Returns the relative position of the Edge
     */
    int getAlignment() {
	    return ALIGN_CENTER;
	}
 
    protected void paintBackground(
        Graphics2D graphics,
        Dimension size,
        Color color) {
			graphics.setColor(color);
			graphics.fillRoundRect(0,0,size.width-1,size.height-1,10,10);
    }

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.NodeView#getStyle()
	 */
	String getStyle() {
		return MindMapNode.STYLE_BUBBLE;
	}
	protected Dimension getMainViewPreferredSize() {
		Dimension prefSize = super.getMainViewPreferredSize();
		prefSize.width  += 5;
	    return prefSize;
	}


}

