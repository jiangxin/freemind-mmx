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
/*$Id: NodeMotionListener.java,v 1.1.4.2.6.4 2005-12-25 20:03:41 dpolivaev Exp $*/

package freemind.controller;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeMotionListenerView;
import freemind.view.mindmapview.NodeView;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMotionListener extends MouseAdapter implements
        MouseMotionListener, MouseListener {

    private final Controller c;
    private Point originalStartingPoint;
    static private final Rectangle bounds = new Rectangle();

    public NodeMotionListener(Controller controller) {
        c = controller;
    }

    public void mouseMoved(MouseEvent e) {
    }

    private Point dragStartingPoint = null;
    private int originalHGap;
    private int originalVGap;
    private int originalShiftY;
    private int originalParentHGap;
    private int originalParentVGap;
    private int originalParentShiftY;

    /** Invoked when a mouse button is pressed on a component and then dragged. */
    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == (InputEvent.BUTTON1_DOWN_MASK)) {
            NodeView nodeV = getNodeView(e);
            final Component component = e.getComponent();

            Point point = e.getPoint();
            SwingUtilities.convertPointToScreen(point, component);
            if (!isActive()) {
                NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
                v.setMouseEntered();
                setDragStartingPoint(point,nodeV.getModel());
            } else {
                Point dragNextPoint = point;
                if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
                    MindMapNode node = nodeV.getModel();
                    node.setShiftY(getNodeShiftY(dragNextPoint, node, originalStartingPoint));
                    node.setHGap(getHGap(dragNextPoint, node, originalStartingPoint));
                    // Bad hack for keeping root node unmoved
                    nodeV.setLocation(0, 0);
                    //FIXME: Replace by nodeRefresh().
                    c.getModeController().nodeChanged(node);
                } else {
                    MindMapNode parentNode = nodeV.getModel().getParentNode();
                    parentNode.setVGap(getVGap(dragNextPoint, parentNode, originalStartingPoint));
                    //FIXME: Replace by nodeRefresh().
                    c.getModel().nodeChanged(parentNode);
                }
            }
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    final MapView mapView = (MapView)component.getParent();
                    component.getBounds(bounds);
                    mapView.scrollRectToVisible(bounds);
                };
            });

        }
    }

    /**
     * @param dragNextPoint
     * @param node
     * @param dragStartingPoint TODO
     * @return
     */
    private int getVGap(Point dragNextPoint, MindMapNode node, Point dragStartingPoint) {
        int oldVGap = originalParentVGap;
        int vGapChange = (int) ((dragNextPoint.y - dragStartingPoint.y) / c
                .getView().getZoom());
        oldVGap = Math.max(0, oldVGap - vGapChange);
        return oldVGap;
    }

    /**
     * @param dragNextPoint
     * @param node
     * @param dragStartingPoint TODO
     * @return
     */
    private int getHGap(Point dragNextPoint, MindMapNode node, Point dragStartingPoint) {
        int oldHGap = originalHGap;
        int hGapChange = (int) ((dragNextPoint.x - dragStartingPoint.x) / c
                .getView().getZoom());
        if (node.isLeft() != null && node.isLeft().getValue() == true)
            hGapChange = -hGapChange;
        oldHGap += +hGapChange;
        return oldHGap;
    }

    /**
     * @param dragNextPoint
     * @param node
     * @param dragStartingPoint TODO
     * @return
     */
    private int getNodeShiftY(Point dragNextPoint, MindMapNode node, Point dragStartingPoint) {
        int shiftY = originalShiftY;
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
                nodeV.setLocation(0, 0);
                c.getModeController().moveNodePosition(node, node.getVGap(), 0, 0);
                return;
            }
            if (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
                NodeView nodeV = getNodeView(e);
                MindMapNode node = nodeV.getModel().getParentNode();
                c.getModeController().moveNodePosition(node, MindMapNode.AUTO, node.getHGap(), node.getShiftY());
                return;
            }
        }
    }

    /**
     * @param e
     * @return
     */
    private NodeView getNodeView(MouseEvent e) {
        return ((NodeMotionListenerView) e.getSource()).getMovedView();
    }

    public void mouseEntered(MouseEvent e) {
        if (!isActive()) {
            NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
            v.setMouseEntered();
        }
    }

    public void mouseExited(MouseEvent e) {
        if (!isActive()) {
            NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
            v.setMouseExited();
        }
    }

    private void stopDrag() {
        setDragStartingPoint(null,null);
    }

    public void mouseReleased(MouseEvent e) {
        NodeMotionListenerView v = (NodeMotionListenerView) e.getSource();
        if (!v.contains(e.getX(), e.getY()))
            v.setMouseExited();
        if(!isActive())
            return;
        NodeView nodeV = getNodeView(e);
        Point point = e.getPoint();
        SwingUtilities.convertPointToScreen(point, e.getComponent());
        // reset node to orignial position:
        // move node to end position.
        if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0) {
	        MindMapNode node = nodeV.getModel();
	        MindMapNode parentNode = nodeV.getModel().getParentNode();
	        resetPositions(node, parentNode);
	        c.getModeController().moveNodePosition(node,
	                node.getVGap(),
	                getHGap(point, node, originalStartingPoint),
	                getNodeShiftY(point, node, originalStartingPoint));
	        c.getModeController().moveNodePosition(parentNode,
	                parentNode.getVGap(),
	                parentNode.getHGap(),
	                parentNode.getShiftY());
        } else {
	        MindMapNode node = nodeV.getModel();
	        MindMapNode parentNode = nodeV.getModel().getParentNode();
	        resetPositions(node, parentNode);
	        c.getModeController().moveNodePosition(node,
	                node.getVGap(),
	                node.getHGap(),
	                node.getShiftY());
	        c.getModeController().moveNodePosition(parentNode,
	                getVGap(point, parentNode, originalStartingPoint),
	                parentNode.getHGap(),
	                parentNode.getShiftY());
        }
        stopDrag();
    }

    /**
     * @param node
     * @param parentNode
     */
    private void resetPositions(MindMapNode node, MindMapNode parentNode) {
        node.setVGap(originalVGap);
        node.setHGap(originalHGap);
        node.setShiftY(originalShiftY);
        parentNode.setVGap(originalParentVGap);
        parentNode.setHGap(originalParentHGap);
        parentNode.setShiftY(originalParentShiftY);
    }

    public boolean isActive() {
        return getDragStartingPoint() != null;
    }

    void setDragStartingPoint(Point point, MindMapNode node) {
        if(this.dragStartingPoint == null) {
            // store old values:
            originalStartingPoint = new Point(point);
            originalHGap = node.getHGap();
            originalVGap = node.getVGap();
            originalShiftY = node.getShiftY();
            if (!node.isRoot()) {
                originalParentHGap =  node.getParentNode().getHGap();
                originalParentVGap =  node.getParentNode().getVGap();
                originalParentShiftY =node.getParentNode().getShiftY();
            }
        } else if (point == null) {
            originalStartingPoint = null;
            originalHGap = 0;
            originalVGap = 0;
            originalShiftY = 0;
            originalParentHGap = 0;
            originalParentVGap = 0;
            originalParentShiftY = 0;
        }
        this.dragStartingPoint = point;
    }

    Point getDragStartingPoint() {
        return dragStartingPoint;
    }

}
