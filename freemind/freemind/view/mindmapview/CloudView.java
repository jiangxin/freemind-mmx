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
/*$Id: CloudView.java,v 1.1.16.2.12.4 2008/03/06 20:00:07 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;
import java.util.LinkedList;
import java.util.Vector;

import freemind.modes.MindMapCloud;

// end Convex Hull

/**
 * This class represents a Cloud around a node.
 */
public class CloudView {
	static final Stroke DEF_STROKE = new BasicStroke(1);
	protected MindMapCloud cloudModel;
	protected NodeView source;

	/**
	 * getIterativeLevel() describes the n-th nested cloud that is to be
	 * painted.
	 */
	protected int getIterativeLevel() {
		return cloudModel.getIterativeLevel();
	}

	static private CloudView heightCalculator = new CloudView(null, null);

	protected CloudView(MindMapCloud cloudModel, NodeView source) {
		this.cloudModel = cloudModel;
		this.source = source;
	}

	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics.create();
		Graphics2D gstroke = (Graphics2D) g.create();
		g.setColor(getColor());
		/* set a bigger stroke to prevent not filled areas. */
		g.setStroke(getStroke());
		/* now bold */
		gstroke.setColor(getExteriorColor());
		gstroke.setStroke(getStroke());
		/*
		 * calculate the distances between two points on the convex hull
		 * depending on the getIterativeLevel().
		 */
		double distanceBetweenPoints = 3 * getDistanceToConvexHull();
		if (getIterativeLevel() > 4)
			distanceBetweenPoints = 100 * getZoom(); /* flat */
		double distanceToConvexHull = getDistanceToConvexHull();
		/** get coordinates */
		LinkedList coordinates = new LinkedList();
		ConvexHull hull = new ConvexHull();
		source.getCoordinates(coordinates);
		// source.getCoordinates(coordinates, (getIterativeLevel()==0)?(int)(5*
		// getZoom()):0 /* = additionalDistanceForConvexHull */);
		Vector/* <Point> */res = hull.calculateHull(coordinates);
		Polygon p = new Polygon();
		for (int i = 0; i < res.size(); ++i) {
			Point pt = (Point) res.get(i);
			p.addPoint(pt.x, pt.y);
		}
		g.fillPolygon(p);
		g.drawPolygon(p);
		/* ok, now the arcs: */
		Point lastPoint = new Point((Point) res.get(0));
		double x0, y0;
		x0 = (double) lastPoint.x;
		y0 = (double) lastPoint.y;
		/* close the path: */
		res.add(res.get(0));
		double x2, y2; /* the drawing start points. */
		x2 = x0;
		y2 = y0;
		for (int i = res.size() - 1; i >= 0; --i) {
			Point nextPoint = new Point((Point) res.get(i));
			double x1, y1, x3, y3, dx, dy, dxn, dyn;
			x1 = (double) nextPoint.x;
			y1 = (double) nextPoint.y;
			dx = x1 - x0; /* direction of p0 -> p1 */
			dy = y1 - y0;
			double length = Math.sqrt(dx * dx + dy * dy);
			dxn = dx / length; /* normalized direction of p0 -> p1 */
			dyn = dy / length;
			if (length > distanceBetweenPoints) {
				for (int j = 0; j < length / distanceBetweenPoints - 1; ++j) {
					if ((j + 2) * distanceBetweenPoints < length) {
						x3 = x0 + (j + 1) * distanceBetweenPoints * dxn; /*
																		 * the
																		 * drawing
																		 * end
																		 * point
																		 * .
																		 */
						y3 = y0 + (j + 1) * distanceBetweenPoints * dyn;
					} else {
						/* last point */
						x3 = x1;
						y3 = y1;
					}
					paintClouds(g, gstroke, x2, y2, x3, y3,
							distanceToConvexHull);
					x2 = x3;
					y2 = y3;
				}
			} else {
				paintClouds(g, gstroke, x2, y2, x1, y1, distanceToConvexHull);
				x2 = x1;
				y2 = y1;
			}
			x0 = x1;
			y0 = y1;
		}
		g.dispose();
	}

	private void paintClouds(Graphics2D g, Graphics2D gstroke, double x0,
			double y0, double x1, double y1, double distanceToConvexHull) {
		// System.out.println("double=" + x0+ ", double=" + y0+ ", double=" +
		// x1+ ", double=" + y1);
		double x2, y2, dx, dy;
		dx = x1 - x0;
		dy = y1 - y0;
		double length = Math.sqrt(dx * dx + dy * dy);
		// nothing to do for length zero.
		if (length == 0f)
			return;
		double dxn, dyn;
		dxn = dx / length;
		dyn = dy / length;
		x2 = x0 + .5f * dx - distanceToConvexHull * dyn;
		y2 = y0 + .5f * dy + distanceToConvexHull * dxn;
		// System.out.println("Line from " + x0+ ", " +y0+ ", " +x2+ ", " +y2+
		// ", " +x1+ ", " +y1+".");
		Shape shape = new QuadCurve2D.Double(x0, y0, x2, y2, x1, y1);
		g.fill(shape);
		gstroke.draw(shape);
	}

	public Color getColor() {
		return getModel().getColor(); /* new Color(240,240,240) *//* selectedColor */
	}

	public Color getExteriorColor() {
		return getModel().getExteriorColor();
	}

	public Stroke getStroke() {
		int width = getWidth();
		if (width < 1) {
			return DEF_STROKE;
		}
		return new BasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public int getWidth() {
		return getModel().getWidth();
	}

	/**
	 * Get the width in pixels rather than in width constant (like -1)
	 */
	public int getRealWidth() {
		int width = getWidth();
		return (width < 1) ? 1 : width;
	}

	private double getDistanceToConvexHull() {
		return 40 / (getIterativeLevel() + 1) * getZoom();
	}

	/** the layout functions can get the additional height of the clouded node . */
	static public int getAdditionalHeigth(MindMapCloud cloudModel,
			NodeView source) {
		heightCalculator.cloudModel = cloudModel;
		heightCalculator.source = source;
		return (int) (1.1 * heightCalculator.getDistanceToConvexHull());
	}

	protected MapView getMap() {
		return source.getMap();
	}

	protected MindMapCloud getModel() {
		return cloudModel;
	}

	protected double getZoom() {
		return getMap().getZoom();
	}

}
