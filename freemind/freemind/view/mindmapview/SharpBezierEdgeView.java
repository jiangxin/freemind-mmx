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
/*$Id: SharpBezierEdgeView.java,v 1.5.34.6 2007/10/25 15:32:59 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import freemind.main.Tools;

/**
 * This class represents a sharp Edge of a MindMap.
 */
public class SharpBezierEdgeView extends EdgeView {

	CubicCurve2D.Float line1 = new CubicCurve2D.Float();
	CubicCurve2D.Float line2 = new CubicCurve2D.Float();
	GeneralPath graph = new GeneralPath();
	Point2D.Float one, two;
	private int deltaX;
	private int deltaY;

	private static final float XCTRL = 12;// the distance between endpoint and
											// controlpoint

	public SharpBezierEdgeView() {
		super();
	}

	private void update() {
		float zoom = getMap().getZoom();
		float xctrlRelative = XCTRL * zoom;
		// YCTRL could be implemented but then we had to check whether target is
		// above or below source.
		if (getTarget().isLeft()) {
			one = new Point2D.Float(start.x - xctrlRelative, start.y);
			two = new Point2D.Float(end.x + xctrlRelative, end.y);
		} else {
			one = new Point2D.Float(start.x + xctrlRelative, start.y);
			two = new Point2D.Float(end.x - xctrlRelative, end.y);
		}
		float w = (getWidth() / 2f + 1) * zoom;
		float w2 = w / 2;
		line1.setCurve(start.x - deltaX, start.y - deltaY, one.x - deltaX,
				one.y - deltaY, two.x, two.y - w2, end.x, end.y);
		line2.setCurve(end.x, end.y, two.x, two.y + w2, one.x + deltaX, one.y
				+ deltaY, start.x + deltaX, start.y + deltaY);
		graph.reset();
		graph.append(line1, true);
		graph.append(line2, true);
		graph.closePath();
	}

	protected void paint(Graphics2D g) {
		update();
		g.setColor(getColor());
		g.setPaint(getColor());
		g.setStroke(DEF_STROKE);
		g.fill(graph);
		g.draw(graph);
	}

	public Color getColor() {
		return getModel().getColor();
	}

	protected void createStart() {
		if (source.isRoot()) {
			start = source.getMainViewOutPoint(getTarget(), end);
			final MainView mainView = source.getMainView();
			final double w = mainView.getWidth() / 2;
			final double x0 = start.x - w;
			final double w2 = w * w;
			final double x02 = x0 * x0;
			if (w2 == x02) {
				final int delta = getMap().getZoomed(getWidth() / 2 + 1);
				deltaX = 0;
				deltaY = delta;
			} else {
				final double delta = getMap().getZoom() * (getWidth() / 2 + 1);
				final int h = mainView.getHeight() / 2;
				final int y0 = start.y - h;
				final double k = h / w * x0 / Math.sqrt(w2 - x02);
				final double dx = delta / Math.sqrt(1 + k * k);
				deltaX = (int) dx;
				deltaY = (int) (k * dx);
				if (y0 > 0) {
					deltaY = -deltaY;
				}
			}
			Tools.convertPointToAncestor(mainView, start, source);
		} else {
			final int delta = getMap().getZoomed(getWidth() / 2 + 1);
			super.createStart();
			deltaX = 0;
			deltaY = delta;
		}
	}

}
