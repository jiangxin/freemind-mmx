/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapCloud;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.mindmapview.attributeview.AttributeView;

/**
 * This class represents a single Node of a MindMap (in analogy to
 * TreeCellRenderer).
 */
public class NodeView extends JComponent implements TreeModelListener {
	public void setFocusCycleRoot(boolean pFocusCycleRoot) {
		// FIXME: On purpose removed. test this!
		// super.setFocusCycleRoot(pFocusCycleRoot);
	}

	static private int FOLDING_SYMBOL_WIDTH = -1;

	protected MindMapNode model;
	protected MapView map;
	private MainView mainView;
	private AttributeView attributeView;
	protected final static Color dragColor = Color.lightGray; // the Color of
																// appearing
																// GradientBox
																// on
																// drag over
	private boolean left = true; // is the node left of root?
	private boolean isLong = false;

	public final static int DRAGGED_OVER_NO = 0;
	public final static int DRAGGED_OVER_SON = 1;
	public final static int DRAGGED_OVER_SIBLING = 2;
	/** For RootNodeView. */
	public final static int DRAGGED_OVER_SON_LEFT = 3;

	final static int ALIGN_BOTTOM = -1;
	final static int ALIGN_CENTER = 0;
	final static int ALIGN_TOP = 1;

	final private static Point zeroPoint = new Point(0, 0);
	private static Logger logger;
	private static FreemindPropertyListener sListener;
	//
	// Constructors
	//

	private Object viewDeletionEvent;
	private int maxToolTipWidth;
	private NodeView preferredChild;
	private JComponent contentPane;
	protected NodeMotionListenerView motionListenerView;

	static final int SPACE_AROUND = 50;

	private NodeFoldingComponent mFoldingListener;

	protected NodeView(MindMapNode model, int position, MapView map,
			Container parent) {
		if (logger == null) {
			logger = map.getController().getFrame()
					.getLogger(this.getClass().getName());
		}
		if(sListener == null){
			sListener = new FreemindPropertyListener() {
				
				public void propertyChanged(String pPropertyName,
						String pNewValue, String pOldValue) {
					if (Tools.safeEquals(pPropertyName,
							FreeMind.TOOLTIP_DISPLAY_TIME)) {
						// control tooltip times:
						ToolTipManager.sharedInstance().setDismissDelay(
								Resources.getInstance().getIntProperty(
										FreeMind.TOOLTIP_DISPLAY_TIME, 4000));
					}
				}
			};
			Controller.addPropertyChangeListenerAndPropagate(sListener);
		}

		setFocusCycleRoot(true);

		this.model = model;
		this.map = map;
		final TreeNode parentNode = model.getParent();
		final int index = parentNode == null ? 0 : parentNode.getIndex(model);
		createAttributeView();

		parent.add(this, index);

		addFoldingListener();
	}

	protected void addFoldingListener() {
		if(mFoldingListener == null && getModel().hasVisibleChilds() && !getModel().isRoot()) {
			mFoldingListener = new NodeFoldingComponent(this);
			add(mFoldingListener, getComponentCount()-1);

			mFoldingListener.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent pE) {
					getModeController().setFolded(getModel(), !getModel().isFolded());
				}
			});
		}
	}

	protected void removeFoldingListener() {
		if(mFoldingListener != null) {
			mFoldingListener.dispose();
			remove(mFoldingListener);
			mFoldingListener = null;
		}
	}
	
	public void propertyChanged(String pPropertyName, String pNewValue,
			String pOldValue) {
	}
	

	void setMainView(MainView newMainView) {
		if (mainView != null) {
			final Container c = mainView.getParent();
			int i;
			for (i = c.getComponentCount() - 1; i >= 0
					&& mainView != c.getComponent(i); i--) {
				// left blank on purpose
			}
			c.remove(i);
			ToolTipManager.sharedInstance().unregisterComponent(mainView);
			mainView.removeMouseListener(this.map.getNodeMouseMotionListener());
			mainView.removeMouseMotionListener(this.map
					.getNodeMouseMotionListener());
			c.add(newMainView, i);
		} else {
			add(newMainView);
		}
		this.mainView = newMainView;
		ToolTipManager.sharedInstance().registerComponent(mainView);
		mainView.addMouseListener(this.map.getNodeMouseMotionListener());
		mainView.addMouseMotionListener(this.map.getNodeMouseMotionListener());
		addDragListener(map.getNodeDragListener());
		addDropListener(map.getNodeDropListener());
		if (!model.isRoot() && "true".equals(map.getController().getProperty(FreeMindMain.ENABLE_NODE_MOVEMENT))) {
			motionListenerView = new NodeMotionListenerView(this);
			add(motionListenerView);
		}

	}

	protected void removeFromMap() {
		setFocusCycleRoot(false);
		getParent().remove(this);
		if (motionListenerView != null) {
			remove(motionListenerView);
			motionListenerView = null;
		}
		removeFoldingListener();
		ToolTipManager.sharedInstance().unregisterComponent(mainView);
	}

	void addDragListener(DragGestureListener dgl) {
		if (dgl == null)
			return;
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(getMainView(),
				DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE
						| DnDConstants.ACTION_LINK, dgl);
	}

	void addDropListener(DropTargetListener dtl) {
		if (dtl == null)
			return;
		DropTarget dropTarget = new DropTarget(getMainView(), dtl);
		dropTarget.setActive(true);
	}

	public boolean isRoot() {
		return getModel().isRoot();
	}

	public boolean getIsLong() {
		return isLong;
	}

	/* fc, 25.1.2004: Refactoring necessary: should call the model. */
	public boolean isSiblingOf(NodeView myNodeView) {
		return getParentView() == myNodeView.getParentView();
	}

	/* fc, 25.1.2004: Refactoring necessary: should call the model. */
	public boolean isChildOf(NodeView myNodeView) {
		return getParentView() == myNodeView;
	}

	/* fc, 25.1.2004: Refactoring necessary: should call the model. */
	public boolean isParentOf(NodeView myNodeView) {
		return (this == myNodeView.getParentView());
	}

	public MindMapNode getModel() {
		return model;
	}

	/**
	 * Returns the coordinates occupied by the node and its children as a vector
	 * of four point per node.
	 */
	public void getCoordinates(LinkedList inList) {
		getCoordinates(inList, 0, false, 0, 0);
	}

	private void getCoordinates(LinkedList inList,
			int additionalDistanceForConvexHull, boolean byChildren,
			int transX, int transY) {
		if (!isVisible())
			return;

		if (isContentVisible()) {
			MindMapCloud cloud = getModel().getCloud();

			// consider existing clouds of children
			if (byChildren && cloud != null) {
				additionalDistanceForConvexHull += CloudView
						.getAdditionalHeigth(cloud, this) / 5;
			}
			final int x = transX + getContent().getX() - getDeltaX();
			final int y = transY + getContent().getY() - getDeltaY();
			final int width = getMainViewWidthWithFoldingMark();
			int heightWithFoldingMark = getMainViewHeightWithFoldingMark();
			final int height = Math.max(heightWithFoldingMark, getContent()
					.getHeight());
			inList.addLast(new Point(-additionalDistanceForConvexHull + x,
					-additionalDistanceForConvexHull + y));
			inList.addLast(new Point(-additionalDistanceForConvexHull + x,
					additionalDistanceForConvexHull + y + height));
			inList.addLast(new Point(additionalDistanceForConvexHull + x
					+ width, additionalDistanceForConvexHull + y + height));
			inList.addLast(new Point(additionalDistanceForConvexHull + x
					+ width, -additionalDistanceForConvexHull + y));
		}

		LinkedList childrenViews = getChildrenViews();
		ListIterator children_it = childrenViews.listIterator();
		while (children_it.hasNext()) {
			NodeView child = (NodeView) children_it.next();
			child.getCoordinates(inList, additionalDistanceForConvexHull, true,
					transX + child.getX(), transY + child.getY());
		}
	}

	/**
     */
	public void setText(String string) {
		mainView.setText(string);
	}

	/**
     */
	public String getText() {
		return mainView.getText();
	}

	protected int getMainViewWidthWithFoldingMark() {
		return mainView.getMainViewWidthWithFoldingMark();
	}

	/** get height including folding symbol */
	protected int getMainViewHeightWithFoldingMark() {
		return mainView.getMainViewHeightWithFoldingMark();
	}

	/** get x coordinate including folding symbol */
	public int getDeltaX() {
		return mainView.getDeltaX();
	}

	/** get y coordinate including folding symbol */
	public int getDeltaY() {
		return mainView.getDeltaY();
	}

	public void requestFocus() {
		// delegate to mapview:
		getController().obtainFocusForSelected();
	}


	//
	// get/set methods
	//

	/**
	 * Calculates the tree height increment because of the clouds.
	 */
	public int getAdditionalCloudHeigth() {
		if (!isContentVisible()) {
			return 0;
		}

		MindMapCloud cloud = getModel().getCloud();
		if (cloud != null) {
			return CloudView.getAdditionalHeigth(cloud, this);
		} else {
			return 0;
		}
	}

	public boolean isSelected() {
		return (getMap().isSelected(this));
	}

	/** Is the node left of root? */
	public boolean isLeft() {
		return getModel().isLeft();
	}

	protected void setModel(MindMapNode model) {
		this.model = model;
	}

	public MapView getMap() {
		return map;
	}

	protected Controller getController() {
		return map.getController();
	}

	protected ModeController getModeController() {
		return getMap().getModel().getModeController();
	}

	protected FreeMindMain getFrame() {
		return getController().getFrame();
	}

	boolean isParentHidden() {
		final Container parent = getParent();
		if (!(parent instanceof NodeView))
			return false;
		NodeView parentView = (NodeView) parent;
		return !parentView.isContentVisible();
	}

	public NodeView getParentView() {
		final Container parent = getParent();
		if (parent instanceof NodeView)
			return (NodeView) parent;
		return null;
	}

	public NodeView getVisibleParentView() {
		final Container parent = getParent();
		if (!(parent instanceof NodeView))
			return null;
		NodeView parentView = (NodeView) parent;
		if (parentView.isContentVisible()) {
			return parentView;
		}
		return parentView.getVisibleParentView();
	}

	/**
	 * This method returns the NodeViews that are children of this node.
	 */
	public LinkedList getChildrenViews() {
		LinkedList childrenViews = new LinkedList();
		final Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			if (!(components[i] instanceof NodeView)) {
				continue;
			}
			NodeView view = (NodeView) components[i];
			childrenViews.add(view); // child.getViewer() );
		}
		return childrenViews;
	}

	protected LinkedList getSiblingViews() {
		return getParentView().getChildrenViews();
	}

	/**
	 * Returns the point the edge should start given the point of the child node
	 * that should be connected.
	 * 
	 * @param targetView
	 *            TODO
	 */
	Point getMainViewOutPoint(NodeView targetView, Point destinationPoint) {
		final NodeViewLayout layoutManager = (NodeViewLayout) getLayout();
		Point out = layoutManager.getMainViewOutPoint(this, targetView,
				destinationPoint);
		return out;
	}

	/**
	 * Returns the Point where the InEdge should arrive the Node.
	 */
	Point getMainViewInPoint() {
		final NodeViewLayout layoutManager = (NodeViewLayout) getLayout();
		Point in = layoutManager.getMainViewInPoint(this);
		return in;
	}

	/**
	 * Returns the Point where the Links should arrive the Node.
	 */
	public Point getLinkPoint(Point declination) {
		int x, y;
		Point linkPoint;
		if (declination != null) {
			x = getMap().getZoomed(declination.x);
			y = getMap().getZoomed(declination.y);
		} else {
			x = 1;
			y = 0;
		}
		if (isLeft()) {
			x = -x;
		}
		if (y != 0) {
			double ctgRect = Math.abs((double) getContent().getWidth()
					/ getContent().getHeight());
			double ctgLine = Math.abs((double) x / y);
			int absLinkX, absLinkY;
			if (ctgRect > ctgLine) {
				absLinkX = Math.abs(x * getContent().getHeight() / (2 * y));
				absLinkY = getContent().getHeight() / 2;
			} else {
				absLinkX = getContent().getWidth() / 2;
				absLinkY = Math.abs(y * getContent().getWidth() / (2 * x));
			}
			linkPoint = new Point(getContent().getWidth() / 2
					+ (x > 0 ? absLinkX : -absLinkX), getContent().getHeight()
					/ 2 + (y > 0 ? absLinkY : -absLinkY));
		} else {
			linkPoint = new Point((x > 0 ? getContent().getWidth() : 0),
					(getContent().getHeight() / 2));
		}
		linkPoint.translate(getContent().getX(), getContent().getY());
		convertPointToMap(linkPoint);
		return linkPoint;
	}

	protected Point convertPointToMap(Point p) {
		return Tools.convertPointToAncestor(this, p, getMap());
	}

	/**
	 * Returns the relative position of the Edge. This is used by bold edge to
	 * know how to shift the line.
	 */
	int getAlignment() {
		return mainView.getAlignment();
	}

	//
	// Navigation
	//
	/**
	 * The algorithm should be here the following (see Eclipse editor):
	 * Selected is the n-th node from above.
	 * Look for the last node visible on the screen and make this node the first one.
	 * Now select the n-th node from above.
	 * 
	 * Easier idea to implement:
	 * Store node y position as y0.
	 * Search for a node with the same parent with y position y0+height
	 * Scroll the window by height.
	 */
	protected NodeView getNextPage() {
		// from root we cannot jump
		if (getModel().isRoot()) {
			return this; // I'm root
		}
		int y0 = getInPointInMap().y + getMap().getViewportSize().height;
		NodeView sibling = getNextVisibleSibling();
		if (sibling == this) {
			return this; // at the end
		}
		// if (sibling.getParentView() != this.getParentView()) {
		// return sibling; // sibling on another page (has different parent)
		// }
		NodeView nextSibling = sibling.getNextVisibleSibling();
		while (nextSibling != sibling
				&& sibling.getParentView() == nextSibling.getParentView()) {
			// has the same position after one page?
			if(nextSibling.getInPointInMap().y >= y0) {
				break;
			}
			sibling = nextSibling;
			nextSibling = nextSibling.getNextVisibleSibling();
		}
		return sibling; // last on the page
	}
	/**
	 * @return the position of the in-point of this node in view coordinates
	 */
	protected Point getInPointInMap() {
		return convertPointToMap(getMainViewInPoint());
	}

	protected NodeView getPreviousPage() {
		if (getModel().isRoot()) {
			return this; // I'm root
		}
		int y0 = getInPointInMap().y - getMap().getViewportSize().height;
		NodeView sibling = getPreviousVisibleSibling();
		if (sibling == this) {
			return this; // at the end
		}
		// if (sibling.getParentView() != this.getParentView()) {
		// return sibling; // sibling on another page (has different parent)
		// }
		NodeView previousSibling = sibling.getPreviousVisibleSibling();
		while (previousSibling != sibling
				&& sibling.getParentView() == previousSibling.getParentView()) {
			// has the same position after one page?
			if(previousSibling.getInPointInMap().y <= y0) {
				break;
			}
			sibling = previousSibling;
			previousSibling = previousSibling.getPreviousVisibleSibling();
		}
		return sibling; // last on the page
	}

	protected NodeView getNextVisibleSibling() {
		NodeView sibling;
		NodeView lastSibling = this;
		// get next sibling even in higher levels
		for (sibling = this; !sibling.getModel().isRoot(); sibling = sibling
				.getParentView()) {
			lastSibling = sibling;
			sibling = sibling.getNextSiblingSingle();
			if (sibling != lastSibling) {
				break; // found sibling
			}
		}

		// we have the nextSibling, search in childs
		// untill: leaf, closed node, max level
		while (sibling.getModel().getNodeLevel() < getMap()
				.getSiblingMaxLevel()) {
			// can we drill down?
			NodeView first = sibling.getFirst(sibling.isRoot() ? lastSibling
					: null, this.isLeft(), !this.isLeft());
			if (first == null) {
				break;
			}
			sibling = first;
		}
		if (sibling.isRoot()) {
			return this; // didn't find (we are at the end)
		}

		return sibling;
	}

	/**
	 * @param startAfter
	 *            TODO
	 */
	NodeView getFirst(Component startAfter, boolean leftOnly, boolean rightOnly) {
		final Component[] components = getComponents();
		for (int i = 0; i < components.length; i++) {
			if (startAfter != null) {
				if (components[i] == startAfter) {
					startAfter = null;
				}
				continue;
			}
			if (!(components[i] instanceof NodeView)) {
				continue;
			}
			NodeView view = (NodeView) components[i];
			if (leftOnly && !view.isLeft() || rightOnly && view.isLeft()) {
				continue;
			}
			if (view.isContentVisible()) {
				return view;
			}
			NodeView child = view.getFirst(null, leftOnly, rightOnly);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	/**
	 */
	public boolean isContentVisible() {
		return getModel().isVisible();
	}

	private NodeView getLast(Component startBefore, boolean leftOnly,
			boolean rightOnly) {
		final Component[] components = getComponents();
		for (int i = components.length - 1; i >= 0; i--) {
			if (startBefore != null) {
				if (components[i] == startBefore) {
					startBefore = null;
				}
				continue;
			}
			if (!(components[i] instanceof NodeView)) {
				continue;
			}
			NodeView view = (NodeView) components[i];
			if (leftOnly && !view.isLeft() || rightOnly && view.isLeft()) {
				continue;
			}
			if (view.isContentVisible()) {
				return view;
			}
			NodeView child = view.getLast(null, leftOnly, rightOnly);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	LinkedList getLeft(boolean onlyVisible) {
		LinkedList all = getChildrenViews();
		LinkedList left = new LinkedList();
		for (ListIterator e = all.listIterator(); e.hasNext();) {
			NodeView node = (NodeView) e.next();
			if (node == null)
				continue;
			if (node.isLeft())
				left.add(node);
		}
		return left;
	}

	LinkedList getRight(boolean onlyVisible) {
		LinkedList all = getChildrenViews();
		LinkedList right = new LinkedList();
		for (ListIterator e = all.listIterator(); e.hasNext();) {
			NodeView node = (NodeView) e.next();
			if (node == null)
				continue;
			if (!node.isLeft())
				right.add(node);
		}
		return right;
	}

	protected NodeView getPreviousVisibleSibling() {
		NodeView sibling;
		NodeView previousSibling = this;

		// get Previous sibling even in higher levels
		for (sibling = this; !sibling.getModel().isRoot(); sibling = sibling
				.getParentView()) {
			previousSibling = sibling;
			sibling = sibling.getPreviousSiblingSingle();
			if (sibling != previousSibling) {
				break; // found sibling
			}
		}
		// we have the PreviousSibling, search in childs
		// untill: leaf, closed node, max level
		while (sibling.getModel().getNodeLevel() < getMap()
				.getSiblingMaxLevel()) {
			NodeView last = sibling.getLast(sibling.isRoot() ? previousSibling
					: null, this.isLeft(), !this.isLeft());
			if (last == null) {
				break;
			}
			sibling = last;
		}
		if (sibling.isRoot()) {
			return this; // didn't find (we are at the end)
		}

		return sibling;
	}

	protected NodeView getNextSiblingSingle() {
		LinkedList v = null;
		if (getParentView().getModel().isRoot()) {
			if (this.isLeft()) {
				v = (getParentView()).getLeft(true);
			} else {
				v = (getParentView()).getRight(true);
			}
		} else {
			v = getParentView().getChildrenViews();
		}
		final int index = v.indexOf(this);
		for (int i = index + 1; i < v.size(); i++) {
			final NodeView nextView = (NodeView) v.get(i);
			if (nextView.isContentVisible()) {
				return nextView;
			} else {
				final NodeView first = nextView.getFirst(null, false, false);
				if (first != null) {
					return first;
				}
			}
		}
		return this;
	}

	protected NodeView getPreviousSiblingSingle() {
		LinkedList v = null;
		if (getParentView().getModel().isRoot()) {
			if (this.isLeft()) {
				v = (getParentView()).getLeft(true);
			} else {
				v = (getParentView()).getRight(true);
			}
		} else {
			v = getParentView().getChildrenViews();
		}
		final int index = v.indexOf(this);
		for (int i = index - 1; i >= 0; i--) {
			final NodeView nextView = (NodeView) v.get(i);
			if (nextView.isContentVisible()) {
				return nextView;
			} else {
				final NodeView last = nextView.getLast(null, false, false);
				if (last != null) {
					return last;
				}
			}
		}
		return this;
	}

	//
	// Update from Model
	//

	void insert() {
		ListIterator it = getModel().childrenFolded();
		while (it.hasNext()) {
			insert((MindMapNode) it.next(), 0);
		}
	}

	/**
	 * Create views for the newNode and all his descendants, set their isLeft
	 * attribute according to this view.
	 */

	NodeView insert(MindMapNode newNode, int position) {
		NodeView newView = NodeViewFactory.getInstance().newNodeView(newNode,
				position, getMap(), this);
		newView.insert();
		return newView;
	}

	/**
	 * This is a bit problematic, because getChildrenViews() only works if model
	 * is not yet removed. (So do not _really_ delete the model before the view
	 * removed (it needs to stay in memory)
	 */
	void remove() {
		for (ListIterator e = getChildrenViews().listIterator(); e.hasNext();) {
			((NodeView) e.next()).remove();
		}
		if (isSelected()) {
			getMap().deselect(this);
		}
		getModeController().onViewRemovedHook(this);
		removeFromMap();
		if (attributeView != null) {
			attributeView.viewRemoved();
		}
		getModel().removeViewer(this); // Let the model know he is invisible
	}

	void update() {
		updateStyle();
		if (!isContentVisible()) {
			// not visible at all
			removeFoldingListener();
			mainView.setVisible(false);
			return;
		}
		mainView.setVisible(true);
		updateTextColor();
		updateFont();
		updateIcons();
		createAttributeView();
		if (attributeView != null) {
			attributeView.update();
		}
		// visible. has it still visible children?
		if(getModel().hasVisibleChilds()) {
			addFoldingListener();
		} else {
			removeFoldingListener();
		}
		updateText();
		updateToolTip();
		revalidate(); // Because of zoom?
	}

	public void createAttributeView() {
		if (attributeView == null && model.getAttributes().getNode() != null) {
			attributeView = new AttributeView(this);
		}
	}

	void repaintSelected() {
		updateTextColor();
		repaint();
	}

	private void updateText() {
		String nodeText = getModel().toString();
		final boolean isHtml = nodeText.startsWith("<html>");
		// 6) Set the text
		// Right now, this implementation is quite logical, although it allows
		// for nonconvex feature of nodes starting with <html>.

		// For plain text, tell if node is long and its width has to be
		// restricted
		// boolean isMultiline = nodeText.indexOf("\n") >= 0;
		boolean widthMustBeRestricted = false;
		if (!isHtml) {
			String[] lines = nodeText.split("\n");
			for (int line = 0; line < lines.length; line++) {
				// Compute the width the node would spontaneously take,
				// by preliminarily setting the text.
				setText(lines[line]);
				widthMustBeRestricted = mainView.getPreferredSize().width > map
						.getZoomed(map.getMaxNodeWidth())
						+ mainView.getIconWidth();
				if (widthMustBeRestricted) {
					break;
				}
			}
			isLong = widthMustBeRestricted || lines.length > 1;
		}

		if (isHtml) {
			// Make it possible to use relative img references in HTML using tag
			// <base>.
			if (nodeText.indexOf("<img") >= 0 && nodeText.indexOf("<base ") < 0) {
				try {
					nodeText = "<html><base href=\"" + map.getModel().getURL()
							+ "\">" + nodeText.substring(6);
				} catch (MalformedURLException e) {
				}
			}
			// If user does not want us to set the width automatically, he'll
			// use <body width="">,
			// <body width="800">, or avoid the <body> tag altogether.

			// Set user HTML head
			String htmlLongNodeHead = getFrame()
					.getProperty("html_long_node_head");
			if (htmlLongNodeHead != null && !htmlLongNodeHead.equals("")) {
				if (nodeText.matches("(?ims).*<head>.*")) {
					nodeText = nodeText.replaceFirst("(?ims).*<head>.*",
							"<head>" + htmlLongNodeHead);
				} else {
					nodeText = nodeText.replaceFirst("(?ims)<html>",
							"<html><head>" + htmlLongNodeHead + "</head>");
				}
			}

			// Find out if the width has to be restricted.
			if (nodeText.length() < 30000) {
				// Empirically determined limit, above which we restrict the
				// width without actually checking it.
				// The purpose of that is to speed up rendering of very long
				// nodes.
				setText(nodeText);
				widthMustBeRestricted = mainView.getPreferredSize().width > map
						.getZoomed(map.getMaxNodeWidth())
						+ mainView.getIconWidth();
			} else {
				widthMustBeRestricted = true;
			}

			if (widthMustBeRestricted) {
				nodeText = nodeText.replaceFirst("(?i)<body>", "<body width=\""
						+ map.getMaxNodeWidth() + "\">");
			}
			setText(nodeText);
		} else if (nodeText.startsWith("<table>")) {
			String[] lines = nodeText.split("\n");
			lines[0] = lines[0].substring(7); // remove <table> tag
			int startingLine = lines[0].matches("\\s*") ? 1 : 0;
			// ^ If the remaining first line is empty, do not draw it

			String text = "<html><table border=1 style=\"border-color: white\">";
			// String[] lines = nodeText.split("\n");
			for (int line = startingLine; line < lines.length; line++) {
				text += "<tr><td style=\"border-color: white;\">"
						+ HtmlTools.toXMLEscapedText(lines[line]).replaceAll(
								"\t", "<td style=\"border-color: white\">");
			}
			setText(text);
		} else if (isLong) {
			String text = HtmlTools.plainToHTML(nodeText);
			if (widthMustBeRestricted) {
				text = text.replaceFirst("(?i)<p>",
						"<p width=\"" + map.getMaxNodeWidth() + "\">");
			}
			setText(text);
		} else {
			setText(nodeText);
		}
	}

	private void updateFont() {
		Font font = getModel().getFont();
		font = font == null ? getController().getDefaultFont() : font;
		if (font != null) {
			mainView.setFont(font);
		} else {
			// We can survive this trouble.
			System.err.println("NodeView.update(): default font is null.");
		}
	}

	private void updateIcons() {
		updateIconPosition();
		MultipleImage iconImages = new MultipleImage(1.0f);
		boolean iconPresent = false;
		/* fc, 06.10.2003: images? */

		FreeMindMain frame = getFrame();
		Map stateIcons = (getModel()).getStateIcons();
		for (Iterator i = stateIcons.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			iconPresent = true;
			ImageIcon myIcon = (ImageIcon) stateIcons.get(key);
			iconImages.addImage(myIcon);

		}

		List icons = (getModel()).getIcons();
		for (Iterator i = icons.iterator(); i.hasNext();) {
			MindIcon myIcon = (MindIcon) i.next();
			iconPresent = true;
			// System.out.println("print the icon " + myicon.toString());
			iconImages.addImage(myIcon.getIcon());
		}
		String link = ((NodeAdapter) getModel()).getLink();
		if (link != null) {
			iconPresent = true;
			String iconPath = "images/Link.png";
			if (link.startsWith("#")) {
				iconPath = "images/LinkLocal.png";
			} else if (link.startsWith("mailto:")) {
				iconPath = "images/Mail.png";
			} else if (Tools.executableByExtension(link)) {
				iconPath = "images/Executable.png";
			}
			ImageIcon icon = new ImageIcon(frame.getResource(iconPath));
			iconImages.addImage(icon);
		}
		// /* Folded icon by Matthias Schade (mascha2), fc, 20.12.2003*/
		// if (((NodeAdapter)getModel()).isFolded()) {
		// iconPresent = true;
		// ImageIcon icon = new
		// ImageIcon(((NodeAdapter)getModel()).getFrame().getResource("images/Folded.png"));
		// iconImages.addImage(icon);
		// }
		// DanielPolansky: set icon only if icon is present, because
		// we don't want to insert any additional white space.
		setIcon(iconPresent ? iconImages : null);
	}

	private void updateIconPosition() {
		getMainView().setHorizontalTextPosition(
				isLeft() ? SwingConstants.LEADING : SwingConstants.TRAILING);
	}

	private void updateTextColor() {
		Color color;
		color = getModel().getColor();
		if (color == null) {
			color = MapView.standardNodeTextColor;
		}
		mainView.setForeground(color);
	}

	boolean useSelectionColors() {
		return isSelected() && !MapView.standardDrawRectangleForSelection
				&& !map.isCurrentlyPrinting();
	}

	void updateStyle() {
		if (mainView != null
				&& (mainView.getStyle().equals(model.getStyle()) || model
						.isRoot())) {
			return;
		}
		final MainView newMainView = NodeViewFactory.getInstance().newMainView(
				model);
		setMainView(newMainView);

	}

	/**
	 * Updates the tool tip of the node.
	 */
	public void updateToolTip() {
		Map tooltips = getModel().getToolTip();
		/*
		 * if(tooltips.size() == 1) { String toolTipText = (String)
		 * tooltips.values().iterator().next();
		 * logger.finest("setting tooltip to "+toolTipText);
		 * mainView.setToolTipText(toolTipText); } else
		 */if (tooltips.size() == 0) {
			mainView.setToolTipText(null);
		} else {
			// html table
			StringBuffer text = new StringBuffer("<html><table width=\""
					+ getMaxToolTipWidth() + "\">");
			for (Iterator i = tooltips.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				String value = (String) tooltips.get(key);
				// no html end inside the value:
				value = value.replaceAll("</html>", "");
				text.append("<tr><td>");
				text.append(value);
				text.append("</td></tr>");
			}
			text.append("</table></html>");
			mainView.setToolTipText(text.toString());
		}
	}

	public int getMaxToolTipWidth() {
		if (maxToolTipWidth == 0) {
			try {
				maxToolTipWidth = getController().getIntProperty(
						"max_tooltip_width", 600);
			} catch (NumberFormatException e) {
				maxToolTipWidth = 600;
			}
		}
		return maxToolTipWidth;
	}

	/**
     */
	public void setIcon(MultipleImage image) {
		mainView.setIcon(image);
	}

	void updateAll() {
		update();
		invalidate();
		for (ListIterator e = getChildrenViews().listIterator(); e.hasNext();) {
			NodeView child = (NodeView) e.next();
			child.updateAll();
		}
	}

	String getStyle() {
		return mainView.getStyle();
	}

	/**
	 * @return returns the color that should used to select the node.
	 */
	protected Color getSelectedColor() {
		// Color backgroundColor = getModel().getBackgroundColor();
		// // if(backgroundColor != null) {
		// // Color backBrighter = backgroundColor.brighter();
		// // // white?
		// // if(backBrighter.getRGB() == Color.WHITE.getRGB()) {
		// // return standardSelectColor;
		// // }
		// // // == standard??
		// // if (backBrighter.equals (standardSelectColor) ) {
		// // return backgroundColor.darker();
		// // }
		// // return backBrighter;
		// // }
		// // == standard??
		// if (backgroundColor != null /*&&
		// backgroundColor.equals(standardSelectColor)*/ ) {
		// // bad hack:
		// return getAntiColor1(backgroundColor);
		// // return new Color(0xFFFFFF - backgroundColor.getRGB());
		// }
		return MapView.standardSelectColor;
	}

	/*
	 * http://groups.google.de/groups?hl=de&lr=&ie=UTF-8&threadm=9i5bbo%24h1kmi%243
	 * %
	 * 40ID-77081.news.dfncis.de&rnum=1&prev=/groups%3Fq%3Djava%2520komplement%25
	 * C3
	 * %25A4rfarbe%2520helligkeit%26hl%3Dde%26lr%3D%26ie%3DUTF-8%26sa%3DN%26as_qdr
	 * %3Dall%26tab%3Dwg
	 */
	/**
	 * Determines to a given color a color, that is the best contrary color. It
	 * is different from {@link #getAntiColor2}.
	 * 
	 * @since PPS 1.1.1
	 */
	protected static Color getAntiColor1(Color c) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null);
		hsb[0] += 0.40;
		if (hsb[0] > 1)
			hsb[0]--;
		hsb[1] = 1;
		hsb[2] = 0.7f;
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	/**
	 * Determines to a given color a color, that is the best contrary color. It
	 * is different from {@link #getAntiColor1}.
	 * 
	 * @since PPS 1.1.1
	 */
	protected static Color getAntiColor2(Color c) {
		float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(),
				null);
		hsb[0] -= 0.40;
		if (hsb[0] < 0)
			hsb[0]++;
		hsb[1] = 1;
		hsb[2] = (float) 0.8;
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	/**
	 * @return Returns the sHIFT.
	 */

	public int getShift() {
		return map.getZoomed(model.calcShiftY());
	}

	/**
	 * @return Returns the VGAP.
	 */
	public int getVGap() {
		return map.getZoomed(model.getVGap());
	}

	public int getHGap() {
		return map.getZoomed(model.getHGap());
	}

	public MainView getMainView() {
		return mainView;
	}

	void syncronizeAttributeView() {
		if (attributeView != null) {
			attributeView.syncronizeAttributeView();
		}
	}

	public Font getTextFont() {
		return getMainView().getFont();
	}

	public Color getTextColor() {
		Color color = getModel().getColor();
		if (color == null) {
			color = MapView.standardNodeTextColor;
		}
		return color;

	}

	/**
     */
	public AttributeView getAttributeView() {
		if (attributeView == null) {
			model.createAttributeTableModel();
			attributeView = new AttributeView(this);
		}

		return attributeView;
	}

	public NodeView getPreferredVisibleChild(boolean left) { // mind preferred
																// child
		// :-) (PN)
		if (preferredChild != null && (left == preferredChild.isLeft())
				&& this.preferredChild.getParent() == this) {
			if (preferredChild.isContentVisible()) {
				return preferredChild;
			} else {
				NodeView newSelected = preferredChild
						.getPreferredVisibleChild(left);
				if (newSelected != null) {
					return newSelected;
				}
			}
		}
		if (!getModel().isLeaf()) {
			int yGap = Integer.MAX_VALUE;
			final NodeView baseComponent;
			if (isContentVisible()) {
				baseComponent = this;
			} else {
				baseComponent = getVisibleParentView();
			}
			int ownY = baseComponent.getMainView().getY()
					+ baseComponent.getMainView().getHeight() / 2;
			NodeView newSelected = null;
			for (int i = 0; i < getComponentCount(); i++) {
				Component c = getComponent(i);
				if (!(c instanceof NodeView)) {
					continue;
				}
				NodeView childView = (NodeView) c;
				if (!(childView.isLeft() == left)) {
					continue;
				}
				if (!childView.isContentVisible()) {
					childView = childView.getPreferredVisibleChild(left);
					if (childView == null) {
						continue;
					}
				}
				Point childPoint = new Point(0, childView.getMainView()
						.getHeight() / 2);
				Tools.convertPointToAncestor(childView.getMainView(),
						childPoint, baseComponent);
				final int gapToChild = Math.abs(childPoint.y - ownY);
				if (gapToChild < yGap) {
					newSelected = childView;
					preferredChild = (NodeView) c;
					yGap = gapToChild;
				} else {
					break;
				}
			}
			return newSelected;
		}
		return null;
	}

	public void setPreferredChild(NodeView view) {
		this.preferredChild = view;
		final Container parent = this.getParent();
		if (view == null) {
			return;
		} else if (parent instanceof NodeView) {
			// set also preffered child of parents...
			((NodeView) parent).setPreferredChild(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event
	 * .TreeModelEvent)
	 */
	public void treeNodesChanged(TreeModelEvent e) {
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event
	 * .TreeModelEvent)
	 */
	public void treeNodesInserted(TreeModelEvent e) {
		addFoldingListener();
		if (getModel().isFolded()) {
			return;
		}
		final int[] childIndices = e.getChildIndices();

		for (int i = 0; i < childIndices.length; i++) {
			int index = childIndices[i];
			insert((MindMapNode) getModel().getChildAt(index), index);
		}
		revalidate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event
	 * .TreeModelEvent)
	 */
	public void treeNodesRemoved(TreeModelEvent e) {
		if(!getModel().hasVisibleChilds()) {
			removeFoldingListener();
		}
		getMap().resetShiftSelectionOrigin();
		if (getModel().isFolded()) {
			return;
		}

		final int[] childIndices = e.getChildIndices();
		boolean preferredChildIsLeft = preferredChild != null
				&& preferredChild.isLeft();

		for (int i = childIndices.length - 1; i >= 0; i--) {
			final int index = childIndices[i];
			final NodeView node = (NodeView) getComponent(index);
			if (node == this.preferredChild) { // mind preferred child :-) (PN)
				this.preferredChild = null;
				for (int j = index + 1; j < getComponentCount(); j++) {
					final Component c = getComponent(j);
					if (!(c instanceof NodeView)) {
						break;
					}
					NodeView candidate = (NodeView) c;
					if (candidate.isVisible()
							&& node.isLeft() == candidate.isLeft()) {
						this.preferredChild = candidate;
						break;
					}
				}
				if (this.preferredChild == null) {
					for (int j = index - 1; j >= 0; j--) {
						final Component c = getComponent(j);
						if (!(c instanceof NodeView)) {
							break;
						}
						NodeView candidate = (NodeView) c;
						if (candidate.isVisible()
								&& node.isLeft() == candidate.isLeft()) {
							this.preferredChild = candidate;
							break;
						}
					}
				}
			}
			node.remove();
		}
		NodeView preferred = getPreferredVisibleChild(preferredChildIsLeft);
		if (preferred != null) { // after delete focus on a brother (PN)
			getMap().selectAsTheOnlyOneSelected(preferred);
		} else {
			getMap().selectAsTheOnlyOneSelected(this);
		}
		revalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.
	 * event.TreeModelEvent)
	 */
	public void treeStructureChanged(TreeModelEvent e) {
		getMap().resetShiftSelectionOrigin();
		for (ListIterator i = getChildrenViews().listIterator(); i.hasNext();) {
			((NodeView) i.next()).remove();
		}
		insert();
		if (map.getSelected() == null) {
			map.selectAsTheOnlyOneSelected(this);
		}
		map.revalidateSelecteds();
		revalidate();
	}

	public int getZoomedFoldingSymbolHalfWidth() {
		int preferredFoldingSymbolHalfWidth = (int) ((getFoldingSymbolWidth() * map
				.getZoom()) / 2);
		return Math.min(preferredFoldingSymbolHalfWidth, getHeight() / 2);
	}

	/**
	 * @return the left/right point of the folding circle. To receive its
	 * center, the amount has to be moved to left/right (depending on its side) 
	 * by the folding circle width.
	 */
	public Point getFoldingMarkPosition() {
		Point out = getMainViewOutPoint(this, new Point());
		return out;
	}

	public JComponent getContent() {
		return contentPane == null ? mainView : contentPane;
	}

	public Container getContentPane() {
		if (contentPane == null) {
			contentPane = NodeViewFactory.getInstance().newContentPane(this);
			remove(mainView);
			contentPane.add(mainView);
			add(contentPane);
		}
		return contentPane;
	}

	public NodeMotionListenerView getMotionListenerView() {
		return motionListenerView;
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		if (motionListenerView != null) {
			motionListenerView.invalidate();
		}
		if (mFoldingListener != null) {
			mFoldingListener.invalidate();
		}
	}

	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (motionListenerView != null) {
			motionListenerView.setVisible(isVisible);
		}
		if (mFoldingListener != null) {
			mFoldingListener.setVisible(isVisible);
		}
	}

	private void paintCloudsAndEdges(Graphics2D g) {
		Object renderingHint = getController().setEdgesRenderingHint(g);
		for (int i = 0; i < getComponentCount(); i++) {
			final Component component = getComponent(i);
			if (!(component instanceof NodeView)) {
				continue;
			}
			NodeView nodeView = (NodeView) component;
			if (nodeView.isContentVisible()) {
				Point p = new Point();
				Tools.convertPointToAncestor(nodeView, p, this);
				g.translate(p.x, p.y);
				nodeView.paintCloud(g);
				g.translate(-p.x, -p.y);
				EdgeView edge = NodeViewFactory.getInstance().getEdge(nodeView);
				edge.paint(nodeView, g);
			} else {
				nodeView.paintCloudsAndEdges(g);
			}
		}
		Tools.restoreAntialiasing(g, renderingHint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		final boolean isRoot = isRoot();
		if (isRoot) {
			paintCloud(g);
		}
		if (isContentVisible()) {
			Graphics2D g2d = (Graphics2D) g;
			paintCloudsAndEdges(g2d);
			super.paint(g);
			// return to std stroke
			g2d.setStroke(BubbleMainView.DEF_STROKE);
//			if (!isRoot) {
//				paintFoldingMark(g2d);
//			}
		} else {
			super.paint(g);
		}
		// g.setColor(Color.BLACK);
		// g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}

	private void paintCloud(Graphics g) {
		if (isContentVisible() && model.getCloud() != null) {
			CloudView cloud = new CloudView(model.getCloud(), this);
			cloud.paint(g);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#toString()
	 */
	public String toString() {
		return getModel().toString() + ", " + super.toString();
	}

	public Rectangle getInnerBounds() {
		final int space = getMap().getZoomed(SPACE_AROUND);
		return new Rectangle(space, space, getWidth() - 2 * space, getHeight()
				- 2 * space);
	}

	public boolean contains(int x, int y) {
		final int space = getMap().getZoomed(SPACE_AROUND) - 2
				* getZoomedFoldingSymbolHalfWidth();
		return (x >= space) && (x < getWidth() - space) && (y >= space)
				&& (y < getHeight() - space);
	}

	public Color getTextBackground() {
		final Color modelBackgroundColor = getModel().getBackgroundColor();
		if (modelBackgroundColor != null) {
			return modelBackgroundColor;
		}
		return getBackgroundColor();
	}

	private Color getBackgroundColor() {
		final MindMapCloud cloud = getModel().getCloud();
		if (cloud != null) {
			return cloud.getColor();
		}
		if (isRoot()) {
			return getMap().getBackground();
		}
		return getParentView().getBackgroundColor();
	}

	public static int getFoldingSymbolWidth() {
		if (FOLDING_SYMBOL_WIDTH == -1) {
			FOLDING_SYMBOL_WIDTH = Resources.getInstance().getIntProperty(
					"foldingsymbolwidth", 8);
		}
		return FOLDING_SYMBOL_WIDTH;
	}

}
