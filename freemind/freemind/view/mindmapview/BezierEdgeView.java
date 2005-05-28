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
/*$Id: BezierEdgeView.java,v 1.13.24.1 2005-05-17 19:34:32 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;


/**
 * This class represents a single Edge of a MindMap.
 */
public class BezierEdgeView extends EdgeView {

    CubicCurve2D.Float graph = new CubicCurve2D.Float();
    private static final int XCTRL = 12;//the distance between endpoint and controlpoint
    private static final int CHILD_XCTRL = 20; // -||- at the child's end
   
    public BezierEdgeView(NodeView source, NodeView target) {
	super(source,target);
	//	update();
    }

    public void update() {
        super.update();

	//YCTRL could be implemented but then we had to check whether target is above or below source.

        int xctrl = getMap().getZoomed(target.isLeft() ? -XCTRL : XCTRL);
        int childXctrl = getMap().getZoomed(target.isLeft() ? CHILD_XCTRL : -CHILD_XCTRL);

	int dy1=getSourceShift();
	int dy2=getTargetShift();

        int endXCorrection = target.isLeft() ? -1 : 0; // This is a workaround, which
                                                       // makes sure, that egde touches
                                                       // the node.

	graph.setCurve(start.x,                   start.y + dy1,
                       start.x + xctrl,           start.y + dy1,
                       end.x   + childXctrl,      end.y   + dy2,
                       end.x   + endXCorrection,  end.y   + dy2);
    }


    public void paint(Graphics2D g) {
        update();
	g.setColor(getColor());
	g.setStroke(getStroke());
        setRendering(g);
	g.draw(graph);
	
	if(isTargetEclipsed(g)){
		g.draw(graph);
	}
	
	super.paint(g);
    }

    public Color getColor() {
	return getModel().getColor();
    }
}
