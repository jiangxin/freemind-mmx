/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * @author foltin
 * @date 28.08.2012
 */
public abstract class MapMarkerBase extends JLabel implements MapMarker {

	/**
	 * 
	 */
	public static final int CIRCLE_RADIUS = 5;
	/**
	 * 
	 */
	private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;
	protected MapDialog mMapDialog;
	boolean mSelected = false;
	protected static java.util.logging.Logger logger = null;
	protected Color mBulletColor = Color.BLACK;
	protected Color mSelectedBackgroundColor = Color.GRAY;
	protected Color mBackgroundColor = Color.WHITE;

	protected float[] mTextWidthShorteningPerZoom = new float[] { 0f, 0f, 0.1f, 0.2f,
			0.3f, 0.4f, 0.5f, 0.75f, 0.8f, 0.9f, 0.95f, 0.97f };
	protected float[] mTextHeightShorteningPerZoom = new float[] { 0f, 0f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.1f, 0.2f, 0.4f, 0.8f, 0.80f, 0.80f };

	/**
	 * 
	 */
	public MapMarkerBase(MapDialog pMapDialog) {
		super();
		mMapDialog = pMapDialog;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}

	public void paint(Graphics pGraphics, Point position) {
		Graphics g = pGraphics.create();
		paintCenter(g, position);
		if (isSelected()) {
			g.setColor(mSelectedBackgroundColor);
		} else {
			g.setColor(mBackgroundColor);
		}
		Point newPoint = adjustToTextfieldLocation(position);
		final JCursorMapViewer map = mMapDialog.getMap();
		int inversZoom = map.getFreeMindMapController().getMaxZoom()
				- map.getZoom();
		inversZoom = Math.min(mTextWidthShorteningPerZoom.length-1, inversZoom);
		final int destWidth = Math.min(this.getWidth(), map.getWidth());
		int normalWidth = (int) (destWidth * (1f - mTextWidthShorteningPerZoom[inversZoom]));
		final int destHeight = Math.min(this.getHeight(), map.getHeight());
		int normalHeight = (int) (destHeight * (1f - mTextHeightShorteningPerZoom[inversZoom]));
		int node_y = newPoint.y;
		int node_x = newPoint.x;
		g.fillRect(node_x, node_y, normalWidth, normalHeight);
		g.setColor(mBulletColor);

		g.translate(node_x, node_y);
		g.clipRect(0, 0, normalWidth, normalHeight);
		this.paint(g);
		g.translate(-node_x, -node_y);

	}

	protected void paintCenter(Graphics g, Point position) {
		g.setColor(mBulletColor);
		g.fillOval(position.x - CIRCLE_RADIUS, position.y - CIRCLE_RADIUS,
				CIRCLE_DIAMETER, CIRCLE_DIAMETER);
		g.setColor(getForeground());
		g.drawOval(position.x - CIRCLE_RADIUS, position.y - CIRCLE_RADIUS,
				CIRCLE_DIAMETER, CIRCLE_DIAMETER);
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
	 * @return true, if the map marker is hit by this relative coordinate (eg.
	 *         0,0 is likely a hit...).
	 */
	public boolean checkHit(int pX, int pY) {
		int x = pX;
		int y = pY;
		// translation:
		x -= CIRCLE_RADIUS;
		y += CIRCLE_RADIUS;
		if (x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight())
			return true;
		// distance to zero less than radius:
		return (pX * pX + pY * pY) <= CIRCLE_RADIUS * CIRCLE_RADIUS;
	}

	/**
	 * @param pSel
	 */
	public void setSelected(boolean pSelected) {
		mSelected = pSelected;
	}

	public boolean isSelected() {
		return mSelected;
	}
	
	public Coordinate getCoordinate() {
		return new Coordinate(getLat(), getLon());
	}

}