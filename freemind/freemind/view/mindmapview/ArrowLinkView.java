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
/*$Id: ArrowLinkView.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.view.mindmapview;
import freemind.modes.MindMapArrowLink;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

// Convex Hull:
import freemind.view.mindmapview.ConvexHull;
import java.awt.Polygon;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.RenderingHints;
// end Convex Hull


/**
 * This class represents a ArrowLink around a node.
 */
public class ArrowLinkView {
    protected MindMapArrowLink arrowLinkModel;
    protected NodeView source, target;
    protected int iterativeLevel;
    protected CubicCurve2D arrowLinkCurve;
    static final Stroke DEF_STROKE = new BasicStroke(1);

    protected ArrowLinkView(MindMapArrowLink arrowLinkModel, NodeView source, NodeView target) {
        this.arrowLinkModel = arrowLinkModel;
        this.source = source;
        this.target = target;
    }

    /** \param iterativeLevel describes the n-th nested arrowLink that is to be painted.*/
    public void paint(Graphics graphics) {
        Point p1, p2, p3, p4;
        Graphics2D g = (Graphics2D) graphics.create();
        /* antialias */  setRendering(g);
        g.setColor(getColor());
        /* set a bigger stroke to prevent not filled areas.*/
        g.setStroke(getStroke());
        // determine, whether destination exists:
        if(target == null)
            return;

        p1 = source.getLinkPoint();
        p2 = target.getLinkPoint();
        // determine point 2 and 3:
        p3 = new Point( p1 );
        double delx, dely;
        delx = p2.x - p1.x; /* direction of p1 -> p3*/
        dely = p2.y - p1.y;
        double dellength = Math.sqrt(delx*delx + dely*dely);
        int deltax = (int) (getZoom() * dellength);
        if(arrowLinkModel.getStartInclination() != null) {
            p3.translate( arrowLinkModel.getStartInclination().x, arrowLinkModel.getStartInclination().y);
        } else {
            // automatic translation in outside direction:
            p3.translate(((source.isLeft())?-1:1) * deltax, 0);
        }            
        p4 = new Point( p2 );
        if(arrowLinkModel.getEndInclination() != null) {
            p4.translate( arrowLinkModel.getEndInclination().x, arrowLinkModel.getEndInclination().y);
        } else {
            // automatic translation in outside direction:
            p4.translate(((target.isLeft())?-1:1) * deltax, 0);
        }            
        arrowLinkCurve = new CubicCurve2D.Double();
        arrowLinkCurve.setCurve(p1,p3,p4,p2);
        g.draw(arrowLinkCurve);
        // arrow source:
        if(arrowLinkModel.startHasArrow()) {
            double dx, dy, dxn, dyn;
            dx = p3.x - p1.x; /* direction of p1 -> p3*/
            dy = p3.y - p1.y;
            double length = Math.sqrt(dx*dx + dy*dy) / (getZoom() * 10/*=zoom factor for arrows*/);
            dxn = dx/length; /* normalized direction of p1 -> p3 */
            dyn = dy/length;
            Polygon p = new Polygon();
            p.addPoint((int) (p1.x),(int) (p1.y));
            p.addPoint((int) (p1.x + dxn + dyn),(int) (p1.y +dyn -dxn));
            p.addPoint((int) (p1.x + dxn - dyn),(int) (p1.y +dyn +dxn));
            p.addPoint((int) (p1.x),(int) (p1.y));
            g.fillPolygon(p);
        }
        // arrow target:
        if(arrowLinkModel.endHasArrow()) {
            double dx, dy, dxn, dyn;
            dx = p4.x - p2.x; /* direction of p2 -> p4*/
            dy = p4.y - p2.y;
            double length = Math.sqrt(dx*dx + dy*dy) / (getZoom() * 10/*=zoom factor for arrows*/);
            dxn = dx/length; /* normalized direction of p2 -> p4 */
            dyn = dy/length;
            Polygon p = new Polygon();
            p.addPoint((int) (p2.x),(int) (p2.y));
            p.addPoint((int) (p2.x + dxn + dyn),(int) (p2.y +dyn -dxn));
            p.addPoint((int) (p2.x + dxn - dyn),(int) (p2.y +dyn +dxn));
            p.addPoint((int) (p2.x),(int) (p2.y));
            g.fillPolygon(p);
        }
    }

    /** Determines, whether or not a given point p is in an epsilon-neighbourhood for the cubic curve.*/
    public boolean detectCollision(Point p) {
        if(arrowLinkCurve == null)
            return false;
        return arrowLinkCurve.intersects(getControlPoint(p)); 
    }

    protected Rectangle2D getControlPoint(Point2D p) {
        // Create a small square around the given point.
        int side = 8;
        return new Rectangle2D.Double(p.getX() - side / 2, p.getY() - side / 2,
                                      side, side);
    }

   public Color getColor() { 
        return getModel().getColor(); /*new Color(240,240,240)*/ /*selectedColor*/
    }

    public Stroke getStroke() {
        Stroke result = getModel().getStroke();
        if (result==null)
            return DEF_STROKE;
        return result;
    }

    public int getWidth() {
        return getModel().getWidth(); 
    }

    /**
     * Get the width in pixels rather than in width constant (like -1)
     */
    public int getRealWidth() {
       int width = getWidth();
       return (width < 1) ? 1 : width; }

    protected MapView getMap() {
       return source.getMap(); }

    /** fc: This getter is public, because the view gets the model by click on the curve.*/
    public MindMapArrowLink getModel() {
       return arrowLinkModel; }
    

    protected double getZoom() {
       return getMap().getZoom(); }

	
   protected void setRendering(Graphics2D g) {
      if (getMap().getController().getAntialiasEdges() || getMap().getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}


}
