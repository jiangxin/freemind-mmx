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
/*$Id: CloudView.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.view.mindmapview;
import freemind.modes.MindMapCloud;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

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
import java.awt.geom.QuadCurve2D;
import java.awt.RenderingHints;
// end Convex Hull


/**
 * This class represents a Cloud around a node.
 */
public class CloudView {
    protected MindMapCloud cloudModel;
    protected NodeView source;
    protected int iterativeLevel;
    static final Stroke DEF_STROKE = new BasicStroke(3);

    protected CloudView(MindMapCloud cloudModel, NodeView source, int iterativeLevel) {
        this.cloudModel = cloudModel;
        this.source = source;
        this.iterativeLevel = iterativeLevel;
    }

    /** \param iterativeLevel describes the n-th nested cloud that is to be painted.*/
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        /* antialias */  setRendering(g);
        Graphics2D gstroke = (Graphics2D) g.create();
        g.setColor(getColor());
        /* set a bigger stroke to prevent not filled areas.*/
        g.setStroke(getStroke());
        /* now bold */
        gstroke.setColor(getExteriorColor());
        gstroke.setStroke(getStroke());
        /* calculate the distances between two points on the convex hull depending on the iterativeLevel.*/
        double distanceBetweenPoints = 50 / (iterativeLevel+1) * getZoom();
        if(distanceBetweenPoints < 15* getZoom()) 
            distanceBetweenPoints = 100 * getZoom(); /* flat*/
        double distanceToConvexHull = 25 / (iterativeLevel+1) * getZoom();
        /** get coordinates */
        LinkedList coordinates = new LinkedList();
        ConvexHull hull = new ConvexHull();
        source.getCoordinates(coordinates, (iterativeLevel==0)?(int)(5* getZoom()):0 /* = additionalDistanceForConvexHull */);
        Vector/*<Point>*/ res = hull.calculateHull(coordinates);
        Polygon p = new Polygon();
        for(int i = 0 ; i < res.size(); ++i) {
            Point pt = (Point) res.get(i);
            p.addPoint(pt.x, pt.y);
        }
        g.fillPolygon(p);
        g.drawPolygon(p);
        /* ok, now the arcs: */
        Point lastPoint = new Point((Point) res.get(0));
        double x0,y0;
        x0 = (double) lastPoint.x;
        y0 = (double) lastPoint.y;
        /* close the path:*/
        res.add(res.get(0));
        double x2,y2; /* the drawing start points.*/
        x2 = x0; y2 = y0;
        for(int i = res.size()-1; i >= 0; --i) {
            Point nextPoint = new Point((Point) res.get(i));
            double x1,y1,x3,y3,dx,dy, dxn, dyn;
            x1 = (double) nextPoint.x;
            y1 = (double) nextPoint.y;
            dx = x1 -x0; /* direction of p0 -> p1*/
            dy = y1 -y0;
            double length = Math.sqrt(dx*dx + dy*dy);
            dxn = dx/length; /* normalized direction of p0 -> p1 */
            dyn = dy/length;
            if(length > distanceBetweenPoints) {
                for(int j = 0 ; j < length/distanceBetweenPoints - 1; ++j) {
                    if((j+2)*distanceBetweenPoints < length) {
                        x3 = x0 + (j+1)*distanceBetweenPoints * dxn ; /* the drawing end point.*/
                        y3 = y0 + (j+1)*distanceBetweenPoints * dyn ;
                    } else {
                        /* last point*/
                        x3 = x1; y3 = y1;
                    }
                    paintClouds(g, gstroke, x2,y2,x3,y3, distanceToConvexHull);
                    x2 = x3; y2 = y3;
                }
            } else {
                paintClouds(g, gstroke, x2,y2,x1,y1, distanceToConvexHull);
                x2 = x1; y2 = y1;
            }
            x0 = x1; y0 = y1;
        }
    }

    /** \param iterativeLevel describes the n-th nested cloud that is to be painted.*/
    private void paintClouds(Graphics2D g, Graphics2D gstroke, double x0,double y0,double x1,double y1, double distanceToConvexHull) {
        //System.out.println("double=" +  x0+ ", double=" +  y0+ ", double=" +  x1+ ", double=" +  y1);
        double x2,y2,dx,dy;
        dx = x1 -x0;
        dy = y1 -y0;
        double length = Math.sqrt(dx*dx + dy*dy);
        // nothing to do for length zero.
        if(length == 0f)
            return;
        double dxn, dyn;
        dxn = dx/length;
        dyn = dy/length;
        x2 = x0 + .5f * dx - distanceToConvexHull * dyn;
        y2 = y0 + .5f * dy + distanceToConvexHull * dxn;
        //System.out.println("Line from " + x0+ ", " +y0+ ", " +x2+ ", " +y2+ ", " +x1+ ", " +y1+".");
        Shape shape = new QuadCurve2D.Double(x0,y0,x2,y2,x1,y1);
        g.fill(shape);
        gstroke.draw(shape);
    }


    public Color getColor() { 
        return getModel().getColor(); /*new Color(240,240,240)*/ /*selectedColor*/
    }

    public Color getExteriorColor() { 
        return getModel().getExteriorColor();
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

    protected MindMapCloud getModel() {
       return cloudModel; }
    

    protected double getZoom() {
       return getMap().getZoom(); }

	
   protected void setRendering(Graphics2D g) {
      if (getMap().getController().getAntialiasEdges() || getMap().getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}


}
