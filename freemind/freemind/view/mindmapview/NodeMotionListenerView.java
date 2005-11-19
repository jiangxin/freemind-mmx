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
/*$Id: NodeMotionListenerView.java,v 1.1.4.2.6.2 2005-11-19 11:36:00 dpolivaev Exp $*/
package freemind.view.mindmapview;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * @author Dimitri
 *
 */
public class NodeMotionListenerView extends JComponent {
	public NodeMotionListenerView(NodeView view) {
		super();
		this.movedView = view;
		MapView map = view.getMap();
		addMouseListener( map.getNodeMotionListener() );
		addMouseMotionListener( map.getNodeMotionListener() );
		setAutoscrolls(true);
	}
	
	private NodeView movedView;
	private boolean isMouseEntered;
	public NodeView getMovedView() {
		return movedView;
	}
	
   protected void setRendering(Graphics2D g) {
       if (movedView.getMap().getController().getAntialiasEdges() || movedView.getMap().getController().getAntialiasAll()) {
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}


	public void paint(Graphics g) {
		super.paint(g);
		if(isMouseEntered){
		    Graphics2D g2 = (Graphics2D )g;
		    setRendering(g2);
			g.drawOval(0, 0, getWidth()-1, getHeight()-1);
			//fc, 16.6.2005: to emphasis the possible movement.
            this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
	}
	public boolean isMouseEntered() {
		return isMouseEntered;
	}
	public void setMouseEntered() {
		this.isMouseEntered = true;
		repaint();
	}

	public void setMouseExited() {
		this.isMouseEntered = false;
		repaint();
	}
}
