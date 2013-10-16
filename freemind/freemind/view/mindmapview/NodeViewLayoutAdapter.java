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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;

import freemind.main.Tools;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 * @date 11.07.2013
 */
abstract public class NodeViewLayoutAdapter implements NodeViewLayout {
	protected final int LISTENER_VIEW_WIDTH = 10;
	protected static java.util.logging.Logger logger = null;
	protected Point location = new Point();
	private static Dimension minDimension;
	private NodeView view;
	private MindMapNode model;
	private int childCount;
	private JComponent content;
	private int vGap;
	private int spaceAround;

	public NodeViewLayoutAdapter() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}
	
	public void addLayoutComponent(String arg0, Component arg1) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	public void removeLayoutComponent(Component arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container arg0) {
		if (minDimension == null)
			minDimension = new Dimension(0, 0);
		return minDimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(Container c) {
		if (!c.isValid()) {
			c.validate();
		}
		return c.getSize();
	}

	public void layoutContainer(Container c) {
		setUp(c);
		layout();
		Point location2 = view.getLocation();
		Tools.convertPointToAncestor(view, location2, view.getMap());
//		logger.info("Layouting node '" + view.getModel() + "' to " + location2);
		layoutOtherItems();
		shutDown();
	}

	/**
	 * 
	 */
	private void layoutOtherItems() {
		final int componentCount = view.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			final Component component = view.getComponent(i);
			if (component instanceof NodeMotionListenerView) {
				NodeMotionListenerView nodeMotionListenerView = (NodeMotionListenerView) component;
				layoutNodeMotionListenerView(nodeMotionListenerView);
			} else if (component instanceof NodeFoldingComponent) {
				NodeFoldingComponent foldingComponent = (NodeFoldingComponent) component;
				layoutNodeFoldingComponent(foldingComponent);
			}
		}

	}

	abstract protected void layout();

	private void setUp(Container c) {
		final NodeView localView = (NodeView) c;
		localView.syncronizeAttributeView();
		final int localChildCount = localView.getComponentCount() - 1;

		for (int i = 0; i < localChildCount; i++) {
			localView.getComponent(i).validate();
		}

		this.view = localView;
		this.model = localView.getModel();
		this.childCount = localChildCount;
		this.content = localView.getContent();

		if (getModel().isVisible()) {
			this.vGap = getView().getVGap();
		} else {
			this.vGap = getView().getVisibleParentView().getVGap();
		}
		spaceAround = view.getMap().getZoomed(NodeView.SPACE_AROUND);
	}

	private void shutDown() {
		this.view = null;
		this.model = null;
		this.content = null;
		this.childCount = 0;
		this.vGap = 0;
		this.spaceAround = 0;
	}

	/**
	 * @return Returns the view.
	 */
	protected NodeView getView() {
		return view;
	}

	/**
	 * @return Returns the model.
	 */
	protected MindMapNode getModel() {
		return model;
	}

	/**
	 * @return Returns the childCount.
	 */
	protected int getChildCount() {
		return childCount;
	}

	/**
	 * @return Returns the content.
	 */
	protected JComponent getContent() {
		return content;
	}

	protected int getChildContentHeight(boolean isLeft) {
		final int childCount = getChildCount();
		if (childCount == 0) {
			return 0;
		}
		int height = 0;
		int count = 0;
		for (int i = 0; i < childCount; i++) {
			Component component = getView().getComponent(i);
			if (component instanceof NodeView) {
				NodeView child = (NodeView) component;
				if (child.isLeft() == isLeft) {
					final int additionalCloudHeigth = child
							.getAdditionalCloudHeigth();
					final int contentHeight = child.getContent().getHeight();
					height += contentHeight + additionalCloudHeigth;
					count++;
				}
			}
		}
		return height + vGap * (count - 1);
	}

	/**
	 * @param isLeft
	 * @return a shift, which is less than or equal zero
	 */
	protected int getChildVerticalShift(boolean isLeft) {
		int shift = 0;
		boolean isFirstNodeView = false;
		boolean foundNodeView = false;
		for (int i = 0; i < getChildCount(); i++) {
			Component component = getView().getComponent(i);
			if (component instanceof NodeView) {
				NodeView child = (NodeView) component;
				if (child.isLeft() == isLeft) {
					final int childShift = child.getShift();
					if (childShift < 0 || isFirstNodeView) {
						shift += childShift;
						isFirstNodeView = false;
					}
					shift -= (child.getContent().getY() - getSpaceAround());
					foundNodeView = true;
				}
			}
		}
		if(foundNodeView) {
			shift -= getSpaceAround();
		}
		return shift;
	}

	protected int getChildHorizontalShift() {
		if (getChildCount() == 0)
			return 0;
		int shift = 0;
		for (int i = 0; i < getChildCount(); i++) {
			Component component = getView().getComponent(i);
			if (component instanceof NodeView) {
				NodeView child = (NodeView) component;
				int shiftCandidate;
				if (child.isLeft()) {
					shiftCandidate = -child.getContent().getX()
							- child.getContent().getWidth();
					if (child.isContentVisible()) {
						shiftCandidate -= child.getHGap()
								+ child.getAdditionalCloudHeigth() / 2;
					}
				} else {
					shiftCandidate = -child.getContent().getX();
					if (child.isContentVisible()) {
						shiftCandidate += child.getHGap();
					}
				}
	
				shift = Math.min(shift, shiftCandidate);
			}
		}
		return shift;
	}

	/**
	 * Implemented in the base class, as the root layout needs it, too.
	 * @param childVerticalShift
	 */
	protected void placeRightChildren(int childVerticalShift) {
		final int baseX = getContent().getX() + getContent().getWidth();
		int y = getContent().getY() + childVerticalShift;
		int right = baseX + getSpaceAround();

		NodeView child = null;
		for (int i = 0; i < getChildCount(); i++) {
			Component componentC = getView().getComponent(i);
			if (componentC instanceof NodeView) {
				NodeView component = (NodeView) componentC;
				if (component.isLeft()) {
					continue;
				}
				child = component;
				final int additionalCloudHeigth = child.getAdditionalCloudHeigth() / 2;
				y += additionalCloudHeigth;
				int shiftY = child.getShift();
				final int childHGap = child.getContent().isVisible() ? child
						.getHGap() : 0;
				int x = baseX + childHGap - child.getContent().getX();
				if (shiftY < 0) {
					child.setLocation(x, y);
					y -= shiftY;
				} else {
					y += shiftY;
					child.setLocation(x, y);
				}
//				logger.info("Place of child " + component.getModel().getText() + ": " + child.getLocation());
				
				y += child.getHeight() - 2 * getSpaceAround() + getVGap()
						+ additionalCloudHeigth;
				right = Math.max(right, x + child.getWidth()
						+ additionalCloudHeigth);
			}
		}
		int bottom = getContent().getY() + getContent().getHeight()
				+ getSpaceAround();

		if (child != null) {
			getView().setSize(
					right,
					Math.max(
							bottom,
							child.getY() + child.getHeight()
									+ child.getAdditionalCloudHeigth() / 2));
		} else {
			getView().setSize(right, bottom);
		}
	}

	/**
 	 * Implemented in the base class, as the root layout needs it, too.
	 * @param childVerticalShift
	 */
	protected void placeLeftChildren(int childVerticalShift) {
		final int baseX = getContent().getX();
		int y = getContent().getY() + childVerticalShift;
		int right = baseX + getContent().getWidth() + getSpaceAround();
		NodeView child = null;
		for (int i = 0; i < getChildCount(); i++) {
			Component componentC = getView().getComponent(i);
			if (componentC instanceof NodeView) {
				NodeView component = (NodeView) componentC;
				if (!component.isLeft()) {
					continue;
				}
				child = component;
				final int additionalCloudHeigth = child.getAdditionalCloudHeigth() / 2;
				y += additionalCloudHeigth;
				int shiftY = child.getShift();
				final int childHGap = child.getContent().isVisible() ? child
						.getHGap() : 0;
				int x = baseX - childHGap - child.getContent().getX()
						- child.getContent().getWidth();
				if (shiftY < 0) {
					child.setLocation(x, y);
					y -= shiftY;
				} else {
					y += shiftY;
					child.setLocation(x, y);
				}
				y += child.getHeight() - 2 * getSpaceAround() + getVGap()
						+ additionalCloudHeigth;
				right = Math.max(right, x + child.getWidth());
			}
		}
		int bottom = getContent().getY() + getContent().getHeight()
				+ getSpaceAround();

		if (child != null) {
			getView().setSize(
					right,
					Math.max(
							bottom,
							child.getY() + child.getHeight()
									+ child.getAdditionalCloudHeigth() / 2));
		} else {
			getView().setSize(right, bottom);
		}
	}

	/**
	 * @return Returns the vGap.
	 */
	int getVGap() {
		return vGap;
	}

	/**
	 * @return Returns the spaceAround.
	 */
	int getSpaceAround() {
		return spaceAround;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.NodeViewLayout#layoutNodeFoldingComponent(freemind.view.mindmapview.NodeFoldingListenerView)
	 */
	public void layoutNodeFoldingComponent(
			NodeFoldingComponent pFoldingComponent) {
		NodeView movedView = pFoldingComponent.getNodeView();

		Point location = movedView.getFoldingMarkPosition();
		JComponent content = movedView.getContent();
		Tools.convertPointToAncestor(content, location, pFoldingComponent.getParent());
		pFoldingComponent.setCorrectedLocation(location);

		Dimension preferredSize = pFoldingComponent.getPreferredSize();
		pFoldingComponent.setSize(preferredSize.width, preferredSize.height);
	}
	
}
