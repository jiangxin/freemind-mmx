/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/* $Id: ForkMainView.java,v 1.1.4.5 2007-08-21 19:54:11 christianfoltin Exp $ */
package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;

import freemind.modes.MindMapNode;

class ForkMainView extends MainView{
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics;
        
        final NodeView nodeView = getNodeView();
        final MindMapNode model = nodeView.getModel();
        if (model==null) return;
        
        final Object renderingHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		getController().setEdgesRenderingHint(g);
        paintSelected(g);
        paintDragOver(g);
        
        int edgeWidth = model.getEdge().getWidth();
        if(edgeWidth==0) {
        	edgeWidth = 1;
        }
        Color oldColor = g.getColor();
        //Draw a standard node
        g.setColor(model.getEdge().getColor());
        g.drawLine(0,          
                getHeight()-edgeWidth/2-1,
                getWidth(), 
                getHeight()-edgeWidth/2-1);
        g.setColor(oldColor);
        super.paint(g);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
    }

    void paintFoldingMark(Graphics2D g, Point p) {
        final int zoomedFoldingSymbolHalfWidth = getZoomedFoldingSymbolHalfWidth();
        if(p.x == -1){
            p.x -= zoomedFoldingSymbolHalfWidth;
        }
        else if(p.x == getWidth()){
            p.x += zoomedFoldingSymbolHalfWidth;
        }        
        super.paintFoldingMark(g, p);
    }
    
    protected int getMainViewWidthWithFoldingMark( )
    {
        int width = getWidth();
        if(getNodeView().getModel().isFolded()){
            width += getZoomedFoldingSymbolHalfWidth() * 2 + getZoomedFoldingSymbolHalfWidth();
        }
        return width;
    }

    protected int getMainViewHeightWithFoldingMark()
    {
        int height = getHeight();
        if(getNodeView().getModel().isFolded()){
            height += getZoomedFoldingSymbolHalfWidth();
        }
        return height;
    }

    public int getDeltaX(){
        if(getNodeView().getModel().isFolded() && getNodeView().isLeft()){
            return super.getDeltaX()+ getZoomedFoldingSymbolHalfWidth() * 3;
        }
        return super.getDeltaX();
    }
    
    /* (non-Javadoc)
     * @see freemind.view.mindmapview.NodeView#getStyle()
     */
    String getStyle() {
        return MindMapNode.STYLE_FORK;
    }
    /**
     * Returns the relative position of the Edge
     */
    int getAlignment() {
        return NodeView.ALIGN_BOTTOM;
    }

    Point getCenterPoint() {
        int edgeWidth = getNodeView().getModel().getEdge().getWidth();
        if(edgeWidth==0) {
        	edgeWidth = 1;
        }
        Point in= new Point(getWidth() / 2, getHeight() - edgeWidth/2 - 1);
        return in;
    }
    
}