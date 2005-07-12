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
/*$Id: ArrowLinkView.java,v 1.8.14.3.2.1.2.1 2005-07-12 15:41:17 dpolivaev Exp $*/

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
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;

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
import java.awt.Rectangle;
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

    /* Note, that source and target are nodeviews and not nodemodels!.*/
    protected ArrowLinkView(MindMapArrowLink arrowLinkModel, NodeView source, NodeView target) {
        this.arrowLinkModel = arrowLinkModel;
        this.source = source;
        this.target = target;
    }
    
    public Rectangle getBounds() {
        if(arrowLinkCurve == null)
            return new Rectangle();
        return arrowLinkCurve.getBounds();
    }

    /** \param iterativeLevel describes the n-th nested arrowLink that is to be painted.*/
	public void paint(Graphics graphics) {
	    if(! isSourceVisible() && ! isTargetVisible())
	        return;
	    Point p1 = null, p2 = null, p3 = null, p4 = null;
	    boolean targetIsLeft = false;
	    boolean sourceIsLeft = false;
	    Graphics2D g = (Graphics2D) graphics.create();
	    /* antialias */  setRendering(g);
	    g.setColor(getColor());
	    /* set stroke.*/
	    g.setStroke(getStroke());
	    // if one of the nodes is not present then draw a dashed line:
	    if(! isSourceVisible() || ! isTargetVisible())
	        g.setStroke(new BasicStroke(getWidth(), BasicStroke.CAP_ROUND,
	                                    BasicStroke.JOIN_ROUND, 0, new float[]{0,3,0,3}, 0));
	
	    // determine, whether destination exists:
	    if(isSourceVisible()) {
	        p1 = source.getLinkPoint(arrowLinkModel.getStartInclination());
	        sourceIsLeft = source.isLeft();
	    }
	    if(isTargetVisible()) {
	        p2 = target.getLinkPoint(arrowLinkModel.getEndInclination());
	        targetIsLeft = target.isLeft();
	    }
	    // determine point 2 and 3:
		if (arrowLinkModel.getEndInclination() == null
		   || arrowLinkModel.getStartInclination() == null) {
			double dellength = isSourceVisible() && isTargetVisible() ? p1.distance(p2) / getZoom() : 30;
			if(isSourceVisible() && arrowLinkModel.getStartInclination() == null){
				Point incl = calcInclination(source, dellength);
				arrowLinkModel.setStartInclination(incl);
		        p1 = source.getLinkPoint(arrowLinkModel.getStartInclination());
			}
			if(isTargetVisible() && arrowLinkModel.getEndInclination() == null){
				Point incl = calcInclination(target, dellength);
				incl.y = -incl.y;
				arrowLinkModel.setEndInclination(incl);
		        p2 = target.getLinkPoint(arrowLinkModel.getEndInclination());
			}
		}
		
		arrowLinkCurve = new CubicCurve2D.Double();
		if (p1 != null){
	        p3 = new Point( p1 );
	        p3.translate( ((sourceIsLeft)?-1:1) * getMap().getZoomed(arrowLinkModel.getStartInclination().x), getMap().getZoomed(arrowLinkModel.getStartInclination().y));
	        if(p2 == null){
				arrowLinkCurve.setCurve(p1,p3,p1,p3);
	        }
		}
		if (p2 != null){
	    	p4 = new Point( p2 );
	    	p4.translate( ((targetIsLeft)?-1:1) * getMap().getZoomed(arrowLinkModel.getEndInclination().x), getMap().getZoomed(arrowLinkModel.getEndInclination().y));
			if(p1 == null){
				arrowLinkCurve.setCurve(p2,p4,p2,p4);
			}
		}
		
		if(p1 != null && p2 != null){
	        arrowLinkCurve.setCurve(p1,p3,p4,p2);
	        g.draw(arrowLinkCurve);
	        // arrow source:
		}
	    if(isSourceVisible() && !arrowLinkModel.getStartArrow().equals("None")) {
	        paintArrow(p1, p3, g);
	    }
	    // arrow target:
	    if(isTargetVisible() && !arrowLinkModel.getEndArrow().equals("None")) {
	        paintArrow(p2, p4, g);
	    }
	    // Control Points
	    if(arrowLinkModel.getShowControlPointsFlag() || ! isSourceVisible() || ! isTargetVisible()){
			g.setStroke(new BasicStroke(getWidth(), BasicStroke.CAP_ROUND,
										BasicStroke.JOIN_ROUND, 0, new float[]{0,3,0,3}, 0));
			if (p1 != null){
				g.drawLine(p1.x, p1.y, p3.x, p3.y);
			}
			if (p2 != null){
				g.drawLine(p2.x, p2.y, p4.x, p4.y);
			}
	    }
	}

    /**
     * @return
     */
    private boolean isTargetVisible() {
        return (target != null && target.isVisible());
    }

    /**
     * @return
     */
    private boolean isSourceVisible() {
        return (source != null && source.isVisible());
    }

    /**
	 * @param dellength
	 */
	private Point calcInclination(NodeView node, double dellength) {
/*	    
		int w = node.getWidth();
		int h = node.getHeight();
		double r = Math.sqrt(w*w+h*h);
		double wr = dellength * w / r;
		double hr = dellength * h / r;
		return  new Point((int)wr, (int)hr);
*/		
		return  new Point((int) dellength, 0);
	}

	/** @param p1 is the start point 
        @param p3 is the another point indicating the direction of the arrow.*/
    private void paintArrow(Point p1, Point p3, Graphics2D g) {
        double dx, dy, dxn, dyn;
        dx = p3.x - p1.x; /* direction of p1 -> p3*/
        dy = p3.y - p1.y;
        double length = Math.sqrt(dx*dx + dy*dy) / (getZoom() * 10/*=zoom factor for arrows*/);
        dxn = dx/length; /* normalized direction of p1 -> p3 */
        dyn = dy/length;
        // suggestion of daniel to have arrows that are not so wide open. fc, 7.12.2003.
        double width = .5f;
        Polygon p = new Polygon();
        p.addPoint((int) (p1.x),(int) (p1.y));
        p.addPoint((int) (p1.x + dxn + width * dyn),(int) (p1.y +dyn - width * dxn));
        p.addPoint((int) (p1.x + dxn - width * dyn),(int) (p1.y +dyn + width * dxn));
        p.addPoint((int) (p1.x),(int) (p1.y));
        g.fillPolygon(p);
    }


    /** MAXIMAL_RECTANGLE_SIZE_FOR_COLLISION_DETECTION describes itself. */
    private final int MAXIMAL_RECTANGLE_SIZE_FOR_COLLISION_DETECTION = 16; 


    /** Determines, whether or not a given point p is in an epsilon-neighbourhood for the cubic curve.*/
    public boolean detectCollision(Point p) {
        if(arrowLinkCurve == null)
            return false;
        Rectangle2D rec=getControlPoint(p);
        // flatten the curve and test for intersection (bug fix, fc, 16.1.2004).
        FlatteningPathIterator pi = new FlatteningPathIterator(arrowLinkCurve.getPathIterator(null),MAXIMAL_RECTANGLE_SIZE_FOR_COLLISION_DETECTION/4,10/*=maximal 2^10=1024 points.*/);
        double oldCoordinateX=0, oldCoordinateY=0;
        while (pi.isDone() == false) {
            double[] coordinates = new double[6];
            int type = pi.currentSegment(coordinates);
            switch(type) {
            case PathIterator.SEG_LINETO:
                if(rec.intersectsLine(oldCoordinateX, oldCoordinateY, coordinates[0], coordinates[1]))
                    return true;
                /* this case needs the same action as the next case, thus no "break" */
            case PathIterator.SEG_MOVETO:
                oldCoordinateX=coordinates[0];
                oldCoordinateY=coordinates[1];
                break;
            case PathIterator.SEG_QUADTO:
            case PathIterator.SEG_CUBICTO:
            case PathIterator.SEG_CLOSE:
            default:
                break;
            }
            pi.next();
        }
        return false;
    }


    protected Rectangle2D getControlPoint(Point2D p) {
        // Create a small square around the given point.
        int side = MAXIMAL_RECTANGLE_SIZE_FOR_COLLISION_DETECTION;
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
       return (source == null)?target.getMap():source.getMap(); }

    /** fc: This getter is public, because the view gets the model by click on the curve.*/
    public MindMapArrowLink getModel() {
       return arrowLinkModel; }
    

    protected double getZoom() {
       return getMap().getZoom(); }

	
   protected void setRendering(Graphics2D g) {
      if (getMap().getController().getAntialiasEdges() || getMap().getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}

    /**
     * @param originX
     * @param originY
     * @param newX
     * @param newY
     */
    public void changeInclination(int originX, int originY, int newX, int newY) {
        // TODO Auto-generated method stub
        
    }
}
