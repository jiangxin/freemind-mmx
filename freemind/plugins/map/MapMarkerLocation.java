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

public class MapMarkerLocation  extends JLabel implements MapMarker {

    private double lat = 0.0;
    private double lon = 0.0;
    private Color color = null;
    private String label = null;

    public MapMarkerLocation(String label, Color color, double lat, double lon) {
        super(label);
        this.label = label;
        this.color = color;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void paint(Graphics g, Point position) {
        int size_h = 5;
        int size = size_h * 2;
        g.setColor(color);
        g.fillOval(position.x - size_h, position.y - size_h, size, size);
        g.setColor(Color.BLACK);
        g.drawOval(position.x - size_h, position.y - size_h, size, size);

        if (label != null) {
            g.translate(position.x, position.y);
            this.paint(g);
            g.translate(-position.x, -position.y);
        }

    }

    public String toString() {
        return "MapMarkerLocation at " + lat + " " + lon;
    }

}
