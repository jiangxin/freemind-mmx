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
/*$Id: MindMapLayout.java,v 1.15.14.5.4.12 2007/04/21 15:11:23 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * This class will Layout the Nodes and Edges of an MapView.
 */
public class MindMapLayout implements LayoutManager {

	final static int BORDER = 30;// width of the border around the map.
	// minimal width for input field of leaf or folded node (PN)
	// the MINIMAL_LEAF_WIDTH is reserved by calculation of the map width
	public final static int MINIMAL_LEAF_WIDTH = 150;
	protected static java.util.logging.Logger logger = null;

	public MindMapLayout() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public void layoutContainer(Container c) {
		final MapView mapView = (MapView) c;
		final int calcXBorderSize = calcXBorderSize(mapView);
		final int calcYBorderSize = calcYBorderSize(mapView);
		getRoot(mapView).validate();
		getRoot(mapView).setLocation(calcXBorderSize, calcYBorderSize);
		mapView.setSize(calcXBorderSize * 2 + getRoot(mapView).getWidth(),
				calcYBorderSize * 2 + getRoot(mapView).getHeight());
		final int componentCount = mapView.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			final Component component = mapView.getComponent(i);
			if (!component.isValid()) {
				component.validate();
			}
		}
	}

	//
	// Absolute positioning
	//

	//
	// Get Methods
	//

	private NodeView getRoot(Container c) {
		return ((MapView) c).getRoot();
	}

	// This is actually never used.
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(200, 200);
	} // For testing Purposes

	public Dimension preferredLayoutSize(Container c) {
		final MapView mapView = (MapView) c;
		final Dimension preferredSize = mapView.getRoot().getPreferredSize();
		return new Dimension(
				2 * calcXBorderSize(mapView) + preferredSize.width, 2
						* calcYBorderSize(mapView) + preferredSize.height);
	}

	/**
	 * @param map
	 *            TODO
	 */
	private int calcYBorderSize(MapView map) {
		int yBorderSize;
		final int minBorderHeight = map.getZoomed(MindMapLayout.BORDER);
		Dimension visibleSize = map.getViewportSize();
		if (visibleSize != null) {
			yBorderSize = Math.max(visibleSize.height, minBorderHeight);
		} else {
			yBorderSize = minBorderHeight;
		}
		return yBorderSize;
	}

	private int calcXBorderSize(MapView map) {
		int xBorderSize;
		Dimension visibleSize = map.getViewportSize();
		final int minBorderWidth = map.getZoomed(MindMapLayout.BORDER
				+ MindMapLayout.MINIMAL_LEAF_WIDTH);
		if (visibleSize != null) {
			xBorderSize = Math.max(visibleSize.width, minBorderWidth);
		} else {
			xBorderSize = minBorderWidth;

		}
		return xBorderSize;
	}

}// class MindMapLayout
