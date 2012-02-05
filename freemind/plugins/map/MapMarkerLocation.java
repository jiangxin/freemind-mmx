/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
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
package plugins.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLabel;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import freemind.modes.MindMapNode;

public class MapMarkerLocation extends JLabel implements MapMarker {

	/**
	 * 
	 */
	public static final int CIRCLE_RADIUS = 5;
	/**
	 * 
	 */
	private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;
	private final MapNodePositionHolder mNodePositionHolder;
	private boolean mSelected = false;
	private final MapDialog mMapDialog;
	protected static java.util.logging.Logger logger = null;
	
	
	/**
	 * @param pNodePositionHolder
	 * @param pMapDialog 
	 */
	public MapMarkerLocation(MapNodePositionHolder pNodePositionHolder, MapDialog pMapDialog) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mNodePositionHolder = pNodePositionHolder;
		mMapDialog = pMapDialog;
		update();
	}

	/**
	 * Either start or when something changes on the node, this method is called.
	 */
	public void update() {
		MindMapNode node = mNodePositionHolder.getNode();
		setText(node.getText());
		// setFont(node.getFont());
		setForeground(node.getColor());
		// setBackground(Color.WHITE);
		setSize(getPreferredSize());
	}

	public double getLat() {
		return mNodePositionHolder.getPosition().getLat();
	}

	public double getLon() {
		return mNodePositionHolder.getPosition().getLon();
	}

	public void paint(Graphics g, Point position) {
		g.setColor(Color.BLACK);
		g.fillOval(position.x - CIRCLE_RADIUS, position.y - CIRCLE_RADIUS, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
		g.setColor(getForeground());
		g.drawOval(position.x - CIRCLE_RADIUS, position.y - CIRCLE_RADIUS, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
//		g.fillPolygon(new int[] {position.x, position.x-CIRCLE_RADIUS, position.x+CIRCLE_RADIUS}, 
//				new int[] {position.y, position.y+CIRCLE_RADIUS, position.y+CIRCLE_RADIUS}, 3);
		if (mSelected) {
			g.setColor(Color.GRAY);
		} else {
			g.setColor(Color.WHITE);
		}
		Point newPoint = adjustToTextfieldLocation(position);
		int node_y = newPoint.y;
		int node_x = newPoint.x;
		g.fillRect(node_x, node_y, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);

		g.translate(node_x, node_y);
		this.paint(g);
		g.translate(-node_x, -node_y);

	}

	public static Point adjustToTextfieldLocation(Point position) {
		Point newPoint = new Point(position);
		newPoint.x = newPoint.x + CIRCLE_RADIUS;
		newPoint.y = newPoint.y - CIRCLE_RADIUS;
		return newPoint;
	}

	/**
	 * @param pX
	 * @param pY
	 * @return true, if the map marker is hit by this relative coordinate (eg. 0,0 is likely a hit...).
	 */
	public boolean checkHit(int pX, int pY) {
		int x = pX;
		int y = pY;
		// translation:
		x -= CIRCLE_RADIUS;
		y += CIRCLE_RADIUS;
		if(x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight())
			return true;
		// distance to zero less than radius:
		return (pX*pX + pY*pY) <= CIRCLE_RADIUS * CIRCLE_RADIUS;
	}
	
	public String toString() {
		return "MapMarkerLocation for node "
				+ mNodePositionHolder.getNode().getText() + " at " + getLat()
				+ " " + getLon();
	}

	/**
	 * @param pSel
	 */
	public void setSelected(boolean pSelected) {
		mSelected  = pSelected;
	}


}
