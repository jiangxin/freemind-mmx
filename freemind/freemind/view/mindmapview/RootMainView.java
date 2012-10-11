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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;

class RootMainView extends MainView {

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView.MainView#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		prefSize.width *= 1.1;
		prefSize.height *= 2;
		return prefSize;
	}

	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;

		if (getNodeView().getModel() == null)
			return;

		Object renderingHint = getController().setEdgesRenderingHint(g);
		paintSelected(g);
		paintDragOver(g);

		// Draw a root node
		g.setColor(Color.gray);
		g.setStroke(new BasicStroke(1.0f));
		g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
		Tools.restoreAntialiasing(g, renderingHint);
		super.paint(g);
	}

	public void paintDragOver(Graphics2D graphics) {
		final int draggedOver = getDraggedOver();
		if (draggedOver == NodeView.DRAGGED_OVER_SON) {
			graphics.setPaint(new GradientPaint(getWidth() / 4, 0,
					getNodeView().getMap().getBackground(), getWidth() * 3 / 4,
					0, NodeView.dragColor));
			graphics.fillRect(getWidth() / 4, 0, getWidth() - 1,
					getHeight() - 1);
		} else if (draggedOver == NodeView.DRAGGED_OVER_SON_LEFT) {
			graphics.setPaint(new GradientPaint(getWidth() * 3 / 4, 0,
					getNodeView().getMap().getBackground(), getWidth() / 4, 0,
					NodeView.dragColor));
			graphics.fillRect(0, 0, getWidth() * 3 / 4, getHeight() - 1);
		}
	}

	public void paintSelected(Graphics2D graphics) {
		if (getNodeView().useSelectionColors()) {
			paintBackground(graphics, getNodeView().getSelectedColor());
		} else {
			paintBackground(graphics, getNodeView().getTextBackground());
		}
	}

	protected void paintBackground(Graphics2D graphics, Color color) {
		graphics.setColor(color);
		graphics.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
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

	public void setDraggedOver(Point p) {
		setDraggedOver((dropPosition(p.getX())) ? NodeView.DRAGGED_OVER_SON_LEFT
				: NodeView.DRAGGED_OVER_SON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView#getStyle()
	 */
	String getStyle() {
		return Resources.getInstance().getProperty(
				FreeMind.RESOURCES_ROOT_NODE_STYLE);
	}

	/**
	 * Returns the relative position of the Edge
	 */
	int getAlignment() {
		return NodeView.ALIGN_CENTER;
	}

	public int getTextWidth() {
		return super.getTextWidth() - getWidth() / 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.view.mindmapview.NodeView#getTextX()
	 */
	public int getTextX() {
		return getIconWidth() + getWidth() / 20;
	}

	public boolean dropAsSibling(double xCoord) {
		return false;
	}

	/** @return true if should be on the left, false otherwise. */
	public boolean dropPosition(double xCoord) {
		return xCoord < getSize().width * 1 / 2;
	}

}