/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2001 Joerg Mueller <joergmueller@bigfoot.com> See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */


package freemind.modes;

import java.awt.Color;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.preferences.FreemindPropertyListener;

public abstract class EdgeAdapter extends LineAdapter implements MindMapEdge {

	public static final String EDGE_WIDTH_THIN_STRING = "thin";
	private static Color standardColor = null;
	private static String standardStyle = null;
	private static EdgeAdapterListener listener = null;

	public static final int WIDTH_PARENT = -1;

	public static final int WIDTH_THIN = 0;

	public final static String EDGESTYLE_LINEAR = "linear";
	public final static String EDGESTYLE_BEZIER = "bezier";
	public final static String EDGESTYLE_SHARP_LINEAR = "sharp_linear";
	public final static String EDGESTYLE_SHARP_BEZIER = "sharp_bezier";
	public final static int INT_EDGESTYLE_LINEAR = 0;
	public final static int INT_EDGESTYLE_BEZIER = 1;
	public final static int INT_EDGESTYLE_SHARP_LINEAR = 2;
	public final static int INT_EDGESTYLE_SHARP_BEZIER = 3;

	// private static Color standardEdgeColor = new Color(0);

	public EdgeAdapter(MindMapNode target, FreeMindMain frame) {
		super(target, frame);
		NORMAL_WIDTH = WIDTH_PARENT;
		if (listener == null) {
			listener = new EdgeAdapterListener();
			Controller.addPropertyChangeListener(listener);
		}
	}

	//
	// Attributes
	//

	public Color getColor() {
		if (color == null) {
			if (getTarget().isRoot()) {
				return getStandardColor();
			}
			return getSource().getEdge().getColor();
		}
		return color;
	}

	public Color getRealColor() {
		return color;
	}

	public int getWidth() {
		if (width == WIDTH_PARENT) {
			if (getTarget().isRoot()) {
				return WIDTH_THIN;
			}
			return getSource().getEdge().getWidth();
		}
		return width;
	}

	public int getRealWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getStyle() {
		if (style == null) {
			if (getTarget().isRoot()) {
				return getFrame().getProperty(getStandardStylePropertyString());
			}
			return getSource().getEdge().getStyle();
		}
		return style;
	}

	public boolean hasStyle() {
		return style != null;
	}

	// /////////
	// Private Methods
	// ///////

	private MindMapNode getSource() {
		return target.getParentNode();
	}

	public XMLElement save() {
		if (style != null || color != null || width != WIDTH_PARENT) {
			XMLElement edge = new XMLElement();
			edge.setName("edge");

			if (style != null) {
				edge.setAttribute("STYLE", style);
			}
			if (color != null) {
				edge.setAttribute("COLOR", Tools.colorToXml(color));
			}
			if (width != WIDTH_PARENT) {
				if (width == WIDTH_THIN)
					edge.setAttribute("WIDTH", EDGE_WIDTH_THIN_STRING);
				else
					edge.setAttribute("WIDTH", Integer.toString(width));
			}
			return edge;
		}
		return null;
	}

	protected Color getStandardColor() {
		return standardColor;
	}

	protected void setStandardColor(Color standardColor) {
		EdgeAdapter.standardColor = standardColor;
	}

	protected String getStandardStyle() {
		return standardStyle;
	}

	protected void setStandardStyle(String standardStyle) {
		EdgeAdapter.standardStyle = standardStyle;
	}

	protected String getStandardColorPropertyString() {
		return FreeMind.RESOURCES_EDGE_COLOR;
	}

	protected String getStandardStylePropertyString() {
		return FreeMind.RESOURCES_EDGE_STYLE;
	}

	protected static class EdgeAdapterListener implements
			FreemindPropertyListener {
		public void propertyChanged(String propertyName, String newValue,
				String oldValue) {
			if (propertyName.equals(FreeMind.RESOURCES_EDGE_COLOR)) {
				EdgeAdapter.standardColor = Tools.xmlToColor(newValue);
			}
			if (propertyName.equals(FreeMind.RESOURCES_EDGE_STYLE)) {
				EdgeAdapter.standardStyle = newValue;
			}
		}
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMapEdge#getStyleAsInt()
	 */
	public int getStyleAsInt() {
		final String edgeStyle = getStyle();
		if (Tools.safeEquals(edgeStyle, EDGESTYLE_LINEAR)) {
			return INT_EDGESTYLE_LINEAR;
		} else if (Tools.safeEquals(edgeStyle, EDGESTYLE_BEZIER)) {
			return INT_EDGESTYLE_BEZIER;
		} else if (Tools.safeEquals(edgeStyle, EDGESTYLE_SHARP_LINEAR)) {
			return INT_EDGESTYLE_SHARP_LINEAR;
		} else if (Tools.safeEquals(edgeStyle, EDGESTYLE_SHARP_BEZIER)) {
			return INT_EDGESTYLE_SHARP_BEZIER;
		} else {
			throw new IllegalArgumentException("Unknown Edge Style "+edgeStyle);
		}
	}
	
}
