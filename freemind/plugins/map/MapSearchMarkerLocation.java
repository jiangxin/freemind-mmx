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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import freemind.controller.actions.generated.instance.Place;

public class MapSearchMarkerLocation extends MapMarkerBase {

	public static final int CIRCLE_SELECTED_FACTOR = 2;
	private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;
	private final Place mPlace;

	/**
	 * @param pMapDialog
	 * @param pNewPlace
	 */
	public MapSearchMarkerLocation(MapDialog pMapDialog, Place pNewPlace) {
		super(pMapDialog);
		mBulletColor = Color.RED;
		mPlace = pNewPlace;
		update();
	}

	/**
	 * Either start or when something changes on the node, this method is called.
	 */
	public void update() {
		setText(mPlace.getDisplayName());
		setForeground(mBulletColor);
		setSize(getPreferredSize());
	}

	public double getLat() {
		return mPlace.getLat();
	}

	public double getLon() {
		return mPlace.getLon();
	}


	/* (non-Javadoc)
	 * @see plugins.map.MapMarkerBase#paintCenter(java.awt.Graphics, java.awt.Point)
	 */
	protected void paintCenter(Graphics pG, Point pPosition) {
		if (isSelected()) {
			Graphics2D g2 = (Graphics2D) pG;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(4));
			int xo = pPosition.x - CIRCLE_RADIUS * CIRCLE_SELECTED_FACTOR;
			int xu = pPosition.x + CIRCLE_RADIUS * CIRCLE_SELECTED_FACTOR;
			int yo = pPosition.y - CIRCLE_RADIUS * CIRCLE_SELECTED_FACTOR;
			int yu = pPosition.y + CIRCLE_RADIUS * CIRCLE_SELECTED_FACTOR;
			g2.drawLine(xo, yo, xu, yu);
			g2.drawLine(xu, yo, xo, yu);
			g2.setStroke(oldStroke);
		} else {
			super.paintCenter(pG, pPosition);
		}
	}
	public String toString() {
		return "MapSearchMarkerLocation for search text "
				+ mPlace.getDisplayName() + " at " + getLat() + " " + getLon();
	}

	public Place getPlace() {
		return mPlace;
	}

}
