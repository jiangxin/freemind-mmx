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
/*$Id: EdgeView.java,v 1.13.14.1.6.1.2.1 2005-07-12 15:41:18 dpolivaev Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapEdge;
import java.awt.*;

import javax.swing.JLabel;

/**
 * This class represents a single Edge of a MindMap.
 */
public abstract class EdgeView {
    protected NodeView source,target;
    private JLabel label = new JLabel();
    protected Point start, end;
    private static int i;

    static final Stroke DEF_STROKE = new BasicStroke();
    static Stroke ECLIPSED_STROKE = null;
	
    protected EdgeView(NodeView source, NodeView target) {
	this.source = source;
	this.target = target;
	label.setFont(getMap().getController().getFontThroughMap
                      (new Font("Sans Serif",0,10)));

	label.setText(getModel().toString());//Calling update() crashes BezierEdgeView

	getMap().add(label);
    }


    /**
     * This should be a task of MindMapLayout
     * start,end must be initialized...
     */
    public void update() {
	label.setText(getModel().toString());	
    }

    public void paint(Graphics2D g) {
	//	label.repaint();
		target.paintFoldingMark(g);
        // if node is folded, then add a plus sign:
        // feature not approved by Daniel. Disabled, fc, 10.1.2004.
//         if(target.getModel().isFolded()) {
//             int height = target.getSize().height/2;
//             // implement a maximum:
//             final int MAX_HEIGHT = 50;
//             if(height > MAX_HEIGHT)
//                 height = MAX_HEIGHT;
//             g.drawArc( end.x - height/2 , end.y - height/2, height,   height,(target.isLeft())?270:90,180);
//         }
        
    }

    public JLabel getLabel() {
	return label;
    }

    void remove() {
	getMap().remove(label);
    }

    public abstract Color getColor();

    public Stroke getStroke() {
       Stroke result = getModel().getStroke();
       if (result==null)
          return DEF_STROKE;
       return result; }

    public int getWidth() {
       return getModel().getWidth(); }

    /**
     * Get the width in pixels rather than in width constant (like -1)
     */
    public int getRealWidth() {
       int width = getWidth();
       return (width < 1) ? 1 : width; }

    protected MindMapEdge getModel() {
       return target.getModel().getEdge(); }

    protected MapView getMap() {
       return source.getMap(); }

	
   /**
    *  Get the vertical shift due to alignment of node connexion and edge width.
    *  Bold edges are centered by Graphic. Applies this shift to change this.
    */
   protected int getNodeShift(NodeView node) {
      if (node.getAlignment()==NodeView.ALIGN_CENTER) return 0;
      // ALIGN_BOTTOM is the case of fork style nodes.
      if (node.getAlignment()==NodeView.ALIGN_BOTTOM) return -getRealWidth()/2+1;
      //if(node.getAlignment()==NodeView.ALIGN_TOP) return w/2; // Daniel: probably never used
      return 0;
   }
	
   protected int getTargetShift() {
      return getNodeShift(target);
   }
	
   protected int getSourceShift() {
      return getNodeShift(source);
   }

   protected void setRendering(Graphics2D g) {
      if (getMap().getController().getAntialiasEdges() || getMap().getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}

   protected static Stroke getEclipsedStroke() {
       if(ECLIPSED_STROKE == null){
           float dash[] = {3.0f, 9.0f};           
           ECLIPSED_STROKE = new BasicStroke(3.0f,
                   BasicStroke.CAP_BUTT,
                   BasicStroke.JOIN_MITER,
                   12.0f, dash, 0.0f);            
       }
       return ECLIPSED_STROKE;
   }

   protected boolean isTargetEclipsed(Graphics2D g) {
       if( target.isParentHidden()){
           g.setColor(g.getBackground());
           g.setStroke(getEclipsedStroke());
	    return true;
       }
       return false;
   }
}
