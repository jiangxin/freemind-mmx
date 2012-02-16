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

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import freemind.controller.actions.generated.instance.Place;

public class MapSearchMarkerLocation implements MapMarker {

	public static final int CIRCLE_RADIUS = 5;
	private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;
	private final MapDialog mMapDialog;
	protected static java.util.logging.Logger logger = null;
	private final Place mPlace;
	
	
	/**
	 * @param pMapDialog
	 * @param pNewPlace
	 */
	public MapSearchMarkerLocation(MapDialog pMapDialog, Place pNewPlace) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mMapDialog = pMapDialog;
		mPlace = pNewPlace;
	}

	public double getLat() {
		return mPlace.getLat();
	}

	public double getLon() {
		return mPlace.getLon();
	}

	public void paint(Graphics g, Point position) {
		g.setColor(Color.RED);
		g.fillOval(position.x - CIRCLE_RADIUS, position.y - CIRCLE_RADIUS, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
		g.setColor(Color.BLACK);

	}

	/**
	 * @param pX
	 * @param pY
	 * @return true, if the map marker is hit by this relative coordinate (eg. 0,0 is likely a hit...).
	 */
	public boolean checkHit(int pX, int pY) {
		// distance to zero less than radius:
		return (pX*pX + pY*pY) <= CIRCLE_RADIUS * CIRCLE_RADIUS;
	}
	
	public String toString() {
		return "MapSearchMarkerLocation for search text "
				+ mPlace.getDisplayName() + " at " + getLat()
				+ " " + getLon();
	}

	public Place getPlace() {
		return mPlace;
	}
}
