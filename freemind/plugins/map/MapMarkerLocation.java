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

	private final MapNodePositionHolder mNodePositionHolder;

	/**
	 * @param pNodePositionHolder
	 */
	public MapMarkerLocation(MapNodePositionHolder pNodePositionHolder) {
		mNodePositionHolder = pNodePositionHolder;
		MindMapNode node = mNodePositionHolder.getNode();
		// TODO: Listener, if the text changes...
		setText(node.getText());
//		setFont(node.getFont());
		setForeground(node.getColor());
//		setBackground(Color.WHITE);
	}

	public double getLat() {
		return mNodePositionHolder.getPosition().getLat();
	}

	public double getLon() {
		return mNodePositionHolder.getPosition().getLon();
	}

	public void paint(Graphics g, Point position) {
		int size_h = 5;
		int size = size_h * 2;
		g.setColor(Color.BLACK);
		g.fillOval(position.x - size_h, position.y - size_h, size, size);
		g.setColor(getForeground());
		g.drawOval(position.x - size_h, position.y - size_h, size, size);
		g.setColor(Color.WHITE);
		int node_y = position.y; //+ size;
		int node_x = position.x;
		g.fillRect(node_x, node_y, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);

		g.translate(node_x, node_y);
		this.paint(g);
		g.translate(-node_x, -node_y);

	}

	public String toString() {
		return "MapMarkerLocation for node "
				+ mNodePositionHolder.getNode().getText() + " at " + getLat()
				+ " " + getLon();
	}

}
