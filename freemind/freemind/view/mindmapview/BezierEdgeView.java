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

import java.awt.geom.CubicCurve2D;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.Color;
import freemind.modes.MindMapEdge;

/**
 * This class represents a single Edge of a MindMap.
 */
public class BezierEdgeView extends EdgeView {

    CubicCurve2D.Float graph = new CubicCurve2D.Float();
    NodeView source,target;
    Point2D.Float start, end, one, two;
    private static final int XCTRL = 7;//the difference between endpoint and controlpoint
   
    public BezierEdgeView(NodeView source, NodeView target) {
	this.source = source;
	this.target = target;
	update();
    }

    public void update() {
	start = new Point2D.Float(source.getOutPoint().x, source.getOutPoint().y);
	end = new Point2D.Float(target.getInPoint().x, target.getInPoint().y);

	if(source.isRoot()) {
	    if( target.isLeft() ) {
		start = new Point2D.Float(source.getInPoint().x, source.getInPoint().y);
	    }
	}

	//YCTRL could be implemented but then we had to check whether target is above or below source.
	if(target.isLeft()) {
	    one = new Point2D.Float(start.x-XCTRL, start.y);
	    two = new Point2D.Float(end.x+XCTRL, end.y);
	} else {
	    one = new Point2D.Float(start.x+XCTRL, start.y);
	    two = new Point2D.Float(end.x-XCTRL, end.y);
	}
    }


    public void paint(Graphics2D g) {
	update();
	g.setColor(getColor());
	graph.setCurve(start.x,start.y,one.x,one.y,two.x,two.y,end.x,end.y);
	g.draw(graph);
    }

    public Color getColor() {
	return getModel().getColor();
    }

    ///////////
    // Private Methods. Internal Implementation
    /////////

    private MindMapEdge getModel() {
	return target.getModel().getEdge();
    }
    
}
