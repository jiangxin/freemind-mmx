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
/*$Id: NodeMotionListenerView.java,v 1.1.4.4.4.9 2009/03/29 19:37:23 christianfoltin Exp $*/
package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JComponent;

import freemind.controller.Controller;
import freemind.main.Resources;
import freemind.main.Tools;

/**
 * 
 * The oval appearing to move nodes to other positions.
 * 
 * @author Dimitri
 * 
 */
public class NodeMotionListenerView extends JComponent {
	protected static java.util.logging.Logger logger = null;
	public NodeMotionListenerView(NodeView view) {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.movedView = view;
		MapView map = view.getMap();
		addMouseListener(map.getNodeMotionListener());
		addMouseMotionListener(map.getNodeMotionListener());
		// fc, 16.6.2005: to emphasis the possible movement.
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		final String helpMsg = Resources.getInstance().getResourceString(
				"node_location_help");
		this.setToolTipText(helpMsg);
	}

	private NodeView movedView;
	private boolean isMouseEntered;

	public NodeView getMovedView() {
		return movedView;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isMouseEntered()) {
			Graphics2D g2 = (Graphics2D) g;
			Controller controller = movedView.getMap().getController();
			// set antialiasing.
			Object renderingHint = controller.setEdgesRenderingHint(g2);
			Color color = g2.getColor();
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke());
			if (movedView.getModel().getHGap() <= 0) {
				g2.setColor(Color.RED);
				g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
			} else {
				g2.setColor(Color.BLACK);
				g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
			}
			g2.setStroke(oldStroke);
			g2.setColor(color);
			Tools.restoreAntialiasing(g2, renderingHint);
		}
	}

	public boolean isMouseEntered() {
		return isMouseEntered;
	}

	public void setMouseEntered() {
		this.isMouseEntered = true;
		// fc, 13.3.2008: variable is not used:
		// final FreeMindMain frame =
		// movedView.getMap().getModel().getModeController().getFrame();
		repaint();
	}

	public void setMouseExited() {
		this.isMouseEntered = false;
		repaint();
	}
}
