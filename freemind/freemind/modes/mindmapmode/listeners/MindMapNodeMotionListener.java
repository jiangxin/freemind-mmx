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

package freemind.modes.mindmapmode.listeners;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import freemind.controller.NodeMotionListener.NodeMotionAdapter;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeMotionListenerView;
import freemind.view.mindmapview.NodeView;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class MindMapNodeMotionListener extends NodeMotionAdapter {

	private final MindMapController c;

	// Logging:
	private static java.util.logging.Logger logger;

	public MindMapNodeMotionListener(MindMapController controller) {
		c = controller;
		if (logger == null)
			logger = c.getFrame().getLogger(this.getClass().getName());
	}

	public void mouseMoved(MouseEvent e) {
	}

	private Point dragStartingPoint = null;
	private int originalParentVGap;
	private int originalHGap;
	private int originalShiftY;

	/** Invoked when a mouse button is pressed on a component and then dragged. */
	public void mouseDragged(MouseEvent e) {
		logger.fine("Event: mouseDragged");
		if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == (InputEvent.BUTTON1_DOWN_MASK)) {
			final NodeMotionListenerView motionListenerView = (NodeMotionListenerView) e
					.getSource();
			final NodeView nodeView = getNodeView(e);
			final MapView mapView = nodeView.getMap();
			MindMapNode node = nodeView.getModel();
			Point point = e.getPoint();
			Tools.convertPointToAncestor(motionListenerView, point,
					JScrollPane.class);
			if (!isActive()) {
				setDragStartingPoint(point, node);
			} else {
				Point dragNextPoint = point;
				if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
					int nodeShiftY = getNodeShiftY(dragNextPoint, node,
							dragStartingPoint);
					int hGap = getHGap(dragNextPoint, node, dragStartingPoint);
					node.setShiftY(nodeShiftY);
					node.setHGap(hGap);
				} else {
					MindMapNode parentNode = nodeView.getVisibleParentView()
							.getModel();
					parentNode
							.setVGap(getVGap(dragNextPoint, dragStartingPoint));
					c.getModeController().nodeRefresh(parentNode);
				}
				dragStartingPoint = dragNextPoint;
				c.getModeController().nodeRefresh(node);
			}
			Point mapPoint = e.getPoint();
			Tools.convertPointToAncestor(motionListenerView, mapPoint, mapView);
			boolean isEventPointVisible = mapView.getVisibleRect().contains(
					mapPoint);
			if (!isEventPointVisible) {
				Rectangle r = new Rectangle(mapPoint);
				Rectangle bounds = mapView.getBounds();
				mapView.scrollRectToVisible(r);
				Rectangle bounds2 = mapView.getBounds();
				int diffx = bounds2.x - bounds.x;
				int diffy = bounds2.y - bounds.y;
				try {
					mapPoint.translate(diffx, diffy);
					// here, there are strange cases, when the mouse moves away.
					// Workaround.
					if (mapView.getVisibleRect().contains(mapPoint)) {
						(new Robot()).mouseMove(e.getXOnScreen() + diffx,
								e.getYOnScreen() + diffy);
					}
				} catch (AWTException e1) {
					freemind.main.Resources.getInstance().logException(e1);
				}
				dragStartingPoint.x += ((node.getHGap() < 0) ? 2 : 1) * diffx;
				dragStartingPoint.y += ((node.getShiftY() < 0) ? 2 : 1) * diffy;
			}
		}
	}

	private int getVGap(Point dragNextPoint, Point dragStartingPoint) {
		int oldVGap = originalParentVGap;
		int vGapChange = (int) ((dragNextPoint.y - dragStartingPoint.y) / c
				.getView().getZoom());
		oldVGap = Math.max(0, oldVGap - vGapChange);
		return oldVGap;
	}

	private int getHGap(Point dragNextPoint, MindMapNode node,
			Point dragStartingPoint) {
		int oldHGap = node.getHGap();
		int hGapChange = (int) ((dragNextPoint.x - dragStartingPoint.x) / c
				.getView().getZoom());
		if (node.isLeft())
			hGapChange = -hGapChange;
		oldHGap += +hGapChange;
		return oldHGap;
	}

	private int getNodeShiftY(Point dragNextPoint, MindMapNode pNode,
			Point dragStartingPoint) {
		int shiftY = pNode.getShiftY();
		int shiftYChange = (int) ((dragNextPoint.y - dragStartingPoint.y) / c
				.getView().getZoom());
		shiftY += shiftYChange;
		return shiftY;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1 && e.getClickCount() == 2) {
			if (e.getModifiersEx() == 0) {
				NodeView nodeV = getNodeView(e);
				MindMapNode node = nodeV.getModel();
				c.moveNodePosition(node, node.getVGap(), NodeAdapter.HGAP, 0);
				return;
			}
			if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
				NodeView nodeV = getNodeView(e);
				MindMapNode node = nodeV.getModel();
				c.moveNodePosition(node, NodeAdapter.VGAP, node.getHGap(),
						node.getShiftY());
				return;
			}
		}
	}

	/**
     */
	private NodeView getNodeView(MouseEvent e) {
		return ((NodeMotionListenerView) e.getSource()).getMovedView();
	}

	public void mouseEntered(MouseEvent e) {
		logger.fine("Event: mouseEntered");
		if (!JOptionPane.getFrameForComponent(e.getComponent()).isFocused())
			return;
		if (!isActive()) {
			NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
			v.setMouseEntered();
		}
	}

	public void mouseExited(MouseEvent e) {
		logger.fine("Event: mouseExited");
		if (!isActive()) {
			NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
			v.setMouseExited();
		}
	}

	private void stopDrag() {
		setDragStartingPoint(null, null);
	}

	public void mouseReleased(MouseEvent e) {
		logger.fine("Event: mouseReleased");
		NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
		if (!v.contains(e.getX(), e.getY()))
			v.setMouseExited();
		if (!isActive())
			return;
		NodeView nodeV = getNodeView(e);
		Point point = e.getPoint();
		Tools.convertPointToAncestor(nodeV, point, JScrollPane.class);
		// move node to end position.
		MindMapNode node = nodeV.getModel();
		MindMapNode parentNode = nodeV.getModel().getParentNode();
		final int parentVGap = parentNode.getVGap();
		final int hgap = node.getHGap();
		final int shiftY = node.getShiftY();
		resetPositions(node);
		c.moveNodePosition(node, parentVGap, hgap, shiftY);
		stopDrag();
	}

	/**
     */
	private void resetPositions(MindMapNode node) {
		node.getParentNode().setVGap(originalParentVGap);
		node.setHGap(originalHGap);
		node.setShiftY(originalShiftY);
	}

	public boolean isActive() {
		return getDragStartingPoint() != null;
	}

	void setDragStartingPoint(Point point, MindMapNode node) {
		dragStartingPoint = point;
		if (point != null) {
			originalParentVGap = node.getParentNode().getVGap();
			originalHGap = node.getHGap();
			originalShiftY = node.getShiftY();
		} else {
			originalParentVGap = originalHGap = originalShiftY = 0;
		}
	}

	Point getDragStartingPoint() {
		return dragStartingPoint;
	}

}
