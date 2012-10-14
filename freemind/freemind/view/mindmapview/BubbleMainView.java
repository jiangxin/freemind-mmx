/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import freemind.main.Tools;
import freemind.modes.MindMapNode;

class BubbleMainView extends MainView {
	private final static Stroke BOLD_STROKE = new BasicStroke(2.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 2f,
					2f }, 0f);
	final static Stroke DEF_STROKE = new BasicStroke();

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView.MainView#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		prefSize.width += getNodeView().getMap().getZoomed(5);
		return prefSize;
	}

	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		final NodeView nodeView = getNodeView();
		final MindMapNode model = nodeView.getModel();
		if (model == null)
			return;

		Object renderingHint = getController().setEdgesRenderingHint(g);
		paintSelected(g);
		paintDragOver(g);

		// change to bold stroke
		// g.setStroke(BOLD_STROKE); // Changed by Daniel

		// Draw a standard node
		g.setColor(model.getEdge().getColor());
		// g.drawOval(0,0,size.width-1,size.height-1); // Changed by Daniel

		// return to std stroke
		g.setStroke(DEF_STROKE);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
		Tools.restoreAntialiasing(g, renderingHint);

		super.paint(g);
	}

	public void paintSelected(Graphics2D graphics) {
		super.paintSelected(graphics);
		if (getNodeView().useSelectionColors()) {
			graphics.setColor(MapView.standardSelectColor);
			graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10,
					10);
		}
	}

	protected void paintBackground(Graphics2D graphics, Color color) {
		graphics.setColor(color);
		graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
	}

	Point getLeftPoint() {
		Point in = new Point(0, getHeight() / 2);
		return in;
	}

	Point getCenterPoint() {
		Point in = getLeftPoint();
		in.x = getWidth() / 2;
		return in;
	}

	Point getRightPoint() {
		Point in = getLeftPoint();
		in.x = getWidth() - 1;
		return in;
	}

	protected int getMainViewWidthWithFoldingMark() {
		int width = getWidth();
		int dW = getZoomedFoldingSymbolHalfWidth() * 2;
		if (getNodeView().getModel().isFolded()) {
			width += dW;
		}
		return width + dW;
	}

	public int getDeltaX() {
		if (getNodeView().getModel().isFolded() && getNodeView().isLeft()) {
			return super.getDeltaX() + getZoomedFoldingSymbolHalfWidth() * 2;
		}
		return super.getDeltaX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView#getStyle()
	 */
	String getStyle() {
		return MindMapNode.STYLE_BUBBLE;
	}

	/**
	 * Returns the relative position of the Edge
	 */
	int getAlignment() {
		return NodeView.ALIGN_CENTER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView#getTextWidth()
	 */
	public int getTextWidth() {
		return super.getTextWidth() + getNodeView().getMap().getZoomed(5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView#getTextX()
	 */
	public int getTextX() {
		return super.getTextX() + getNodeView().getMap().getZoomed(2);
	}

}