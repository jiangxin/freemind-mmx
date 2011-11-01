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
/*$Id: BezierEdgeView.java,v 1.13.18.1.2.6 2008/06/09 21:01:15 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;

/**
 * This class represents a single Edge of a MindMap.
 */
public class BezierEdgeView extends EdgeView {

	CubicCurve2D.Float graph = new CubicCurve2D.Float();
	private static final int XCTRL = 12;// the distance between endpoint and
										// controlpoint
	private static final int CHILD_XCTRL = 20; // -||- at the child's end

	public BezierEdgeView() {
		super();
	}

	private void update() {

		// YCTRL could be implemented but then we had to check whether target is
		// above or below source.
		int sign = (getTarget().isLeft()) ? -1 : 1;
		int sourceSign = 1;
		if (getSource().isRoot()
				&& !VerticalRootNodeViewLayout.USE_COMMON_OUT_POINT_FOR_ROOT_NODE) {
			sourceSign = 0;
		}
		int xctrl = getMap().getZoomed(sourceSign * sign * XCTRL);
		int childXctrl = getMap().getZoomed(-1 * sign * CHILD_XCTRL);

		graph.setCurve(start.x, start.y, start.x + xctrl, start.y, end.x
				+ childXctrl, end.y, end.x, end.y);
	}

	protected void paint(Graphics2D g) {
		update();
		final Color color = getColor();
		g.setColor(color);
		final Stroke stroke = getStroke();
		g.setStroke(stroke);
		g.draw(graph);

		if (isTargetEclipsed()) {
			g.setColor(g.getBackground());
			g.setStroke(getEclipsedStroke());
			g.draw(graph);
			g.setStroke(stroke);
			g.setColor(color);
		}
	}

	public Color getColor() {
		return getModel().getColor();
	}
}
