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

import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import freemind.modes.MindMapEdge;

/**
 * This class represents a single Edge of a MindMap.
 */
public class LinearEdgeView extends EdgeView {

    NodeView source, target;
    Point start, end;

    public LinearEdgeView(NodeView source, NodeView target) {
	this.source = source;
	this.target = target;
	update();
    }

    public void update() {
	start = source.getOutPoint();
	end = target.getInPoint();
	if(source.isRoot()) {
	    if( target.isLeft() ) {
		start = source.getInPoint();
	    }
	}
    }

    public void paint(Graphics2D g) {
	update();
	g.setColor(getColor());
	g.drawLine(start.x,start.y,end.x,end.y);
    }
    
    public Color getColor() {
	return getModel().getColor();
    }

    private MindMapEdge getModel() {
	return target.getModel().getEdge();
    }
}
