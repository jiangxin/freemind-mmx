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

package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;

import freemind.controller.Controller;
import freemind.controller.NodeKeyListener;
import freemind.controller.NodeMotionListener;
import freemind.controller.NodeMouseMotionListener;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.Tools.Pair;
import freemind.modes.MindMap;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.preferences.FreemindPropertyListener;

/**
 * This class represents the view of a whole MindMap (in analogy to class
 * JTree).
 */
public class MapView extends JPanel implements Printable, Autoscroll {
	/**
	 * Currently, this listener does nothing. But it should move the map
	 * according to the resize event, such that the current map's center stays
	 * at the same location (seen relative).
	 */
	private final class ResizeListener extends ComponentAdapter {
		Dimension mSize;

		ResizeListener() {
			mSize = getSize();
		}

		public void componentResized(ComponentEvent pE) {
			logger.fine("Component resized " + pE + " old size " + mSize
					+ " new size " + getSize());
			// int deltaWidth = mSize.width - getWidth();
			// int deltaHeight = mSize.height - getHeight();
			// Point viewPosition = getViewPosition();
			// viewPosition.x += deltaWidth/2;
			// viewPosition.y += deltaHeight/2;
			// mapViewport.setViewPosition(viewPosition);
			mSize = getSize();

		}
	}

	static public class ScrollPane extends JScrollPane {
		public ScrollPane() {
			// /*
			// * Diagnosis for the input map, but I haven't
			// * managed to remove the ctrl pageup/down keys
			// * from it.
			// */
			// InputMap inputMap =
			// getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			// KeyStroke[] keys = inputMap.allKeys();
			// if (keys != null) {
			// for (int i = 0; i < keys.length; i++) {
			// KeyStroke stroke = keys[i];
			// logger.fine("Stroke: " + stroke);
			// }
			// } else {
			// logger.fine("No keys in input map");
			// }
		}

		protected boolean processKeyBinding(KeyStroke pKs, KeyEvent pE,
				int pCondition, boolean pPressed) {
			/*
			 * the scroll pane eats control page up and down. Moreover, the page
			 * up and down itself is not very useful, as the map hops away too
			 * far.
			 */
			if (pE.getKeyCode() == KeyEvent.VK_PAGE_DOWN
					|| pE.getKeyCode() == KeyEvent.VK_PAGE_UP)
				return false;
			return super.processKeyBinding(pKs, pE, pCondition, pPressed);
		}

		protected void validateTree() {
			final Component view = getViewport().getView();
			if (view != null) {
				view.validate();
			}
			super.validateTree();
		}

	}

	int mPaintingTime;
	int mPaintingAmount;
	static boolean printOnWhiteBackground;
	static Color standardMapBackgroundColor;
	static Color standardSelectColor;
	static Color standardSelectRectangleColor;
	public static Color standardNodeTextColor;
	static boolean standardDrawRectangleForSelection;
	private static Stroke standardSelectionStroke;
	static private FreemindPropertyListener propertyChangeListener;

	private class Selected {
		private Vector mySelected = new Vector();

		public Selected() {
		};

		public void clear() {
			if (size() > 0) {
				removeFocusForHooks(get(0));
			}
			for (Iterator it = mySelected.iterator(); it.hasNext();) {
				NodeView view = (NodeView) it.next();
				changeSelection(view, false);
			}
			mySelected.clear();
			logger.finest("Cleared selected.");
		}

		/**
		 * @param pNode
		 */
		private void changeSelection(NodeView pNode, boolean pIsSelected) {
			if (pNode.getModel() == null)
				return;
			getModel().getModeController().changeSelection(pNode, pIsSelected);
			
		}

		public int size() {
			return mySelected.size();
		}

		public void remove(NodeView node) {
			if (mySelected.indexOf(node) == 0) {
				removeFocusForHooks(node);
			}
			changeSelection(node, false);
			mySelected.remove(node);
			logger.finest("Removed focused " + node);
		}

		public void add(NodeView node) {
			if (size() > 0) {
				removeFocusForHooks(get(0));
			}
			mySelected.add(0, node);
			addFocusForHooks(node);
			changeSelection(node, true);
			logger.finest("Added focused " + node + "\nAll=" + mySelected);
		}

		private void removeFocusForHooks(NodeView node) {
			if (node.getModel() == null)
				return;
			getModel().getModeController().onLostFocusNode(node);
		}

		private void addFocusForHooks(NodeView node) {
			getModel().getModeController().onFocusNode(node);
		}

		public NodeView get(int i) {
			return (NodeView) mySelected.get(i);
		}

		public boolean contains(NodeView node) {
			return mySelected.contains(node);
		}

		/**
		 */
		public void moveToFirst(NodeView newSelected) {
			if (contains(newSelected)) {
				int pos = mySelected.indexOf(newSelected);
				if (pos > 0) { // move
					if (size() > 0) {
						removeFocusForHooks(get(0));
					}
					mySelected.remove(newSelected);
					mySelected.add(0, newSelected);
				}
			} else {
				add(newSelected);
			}
			addFocusForHooks(newSelected);
			logger.finest("MovedToFront selected " + newSelected + "\nAll="
					+ mySelected);
		}
	}

	// Logging:
	private static java.util.logging.Logger logger;

	private MindMap model;
	private NodeView rootView = null;
	private Selected selected = new Selected();
	private Controller controller = null;
	private float zoom = 1F;
	private boolean disableMoveCursor = true;
	private int siblingMaxLevel;
	private boolean isPrinting = false; // use for remove selection from print
	private NodeView shiftSelectionOrigin = null;
	private int maxNodeWidth = 0;
	private Color background = null;
	private Rectangle boundingRectangle = null;
	private boolean fitToPage = true;

	/** Used to identify a right click onto a link curve. */
	private Vector/* of ArrowLinkViews */mArrowLinkViews = new Vector();

	private Point rootContentLocation;

	private NodeView nodeToBeVisible = null;

	private int extraWidth;

	private boolean selectedsValid = true;
	//
	// Constructors
	//
	static boolean NEED_PREF_SIZE_BUG_FIX = Controller.JAVA_VERSION
			.compareTo("1.5.0") < 0;

	public MapView(MindMap model, Controller controller) {
		super();
		this.model = model;
		this.controller = controller;
		if (logger == null)
			logger = controller.getFrame().getLogger(this.getClass().getName());
		mCenterNodeTimer = new Timer();
		// initialize the standard colors.
		if (standardNodeTextColor == null) {
			try {
				String stdcolor = getController().getFrame().getProperty(
						FreeMind.RESOURCES_BACKGROUND_COLOR);
				standardMapBackgroundColor = Tools.xmlToColor(stdcolor);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				standardMapBackgroundColor = Color.WHITE;
			}
			try {
				String stdcolor = getController().getFrame().getProperty(
						FreeMind.RESOURCES_NODE_TEXT_COLOR);
				standardNodeTextColor = Tools.xmlToColor(stdcolor);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				standardSelectColor = Color.WHITE;
			}
			// initialize the selectedColor:
			try {
				String stdcolor = getController().getFrame().getProperty(
						FreeMind.RESOURCES_SELECTED_NODE_COLOR);
				standardSelectColor = Tools.xmlToColor(stdcolor);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				standardSelectColor = Color.BLUE.darker();
			}

			// initialize the selectedTextColor:
			try {
				String stdtextcolor = getController().getFrame().getProperty(
						FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR);
				standardSelectRectangleColor = Tools.xmlToColor(stdtextcolor);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				standardSelectRectangleColor = Color.WHITE;
			}
			try {
				String drawCircle = getController().getFrame().getProperty(
						FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION);
				standardDrawRectangleForSelection = Tools
						.xmlToBoolean(drawCircle);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				standardDrawRectangleForSelection = false;
			}

			try {
				String printOnWhite = getController().getFrame().getProperty(
						FreeMind.RESOURCE_PRINT_ON_WHITE_BACKGROUND);
				printOnWhiteBackground = Tools.xmlToBoolean(printOnWhite);
			} catch (Exception ex) {
				freemind.main.Resources.getInstance().logException(ex);
				printOnWhiteBackground = true;
			}

			createPropertyChangeListener();

		}
		this.setAutoscrolls(true);

		this.setLayout(new MindMapLayout());

		initRoot();

		setBackground(standardMapBackgroundColor);
		addMouseListener(controller.getMapMouseMotionListener());
		addMouseMotionListener(controller.getMapMouseMotionListener());
		addMouseWheelListener(controller.getMapMouseWheelListener());
		addKeyListener(getNodeKeyListener());

		// fc, 20.6.2004: to enable tab for insert.
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		setFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		// end change.

		// fc, 31.3.2013: set policy to achive that after note window close, the
		// current node is selected.
		setFocusTraversalPolicy(new FocusTraversalPolicy() {

			public Component getLastComponent(Container pAContainer) {
				return getDefaultComponent(pAContainer);
			}

			public Component getFirstComponent(Container pAContainer) {
				return getDefaultComponent(pAContainer);
			}

			public Component getDefaultComponent(Container pAContainer) {
				Component defaultComponent = getSelected();
				logger.fine("Focus traversal to: " + defaultComponent);
				return defaultComponent;
			}

			public Component getComponentBefore(Container pAContainer,
					Component pAComponent) {
				return getDefaultComponent(pAContainer);
			}

			public Component getComponentAfter(Container pAContainer,
					Component pAComponent) {
				return getDefaultComponent(pAContainer);
			}
		});
		this.setFocusTraversalPolicyProvider(true);
		// like in excel - write a letter means edit (PN)
		// on the other hand it doesn't allow key navigation (sdfe)
		disableMoveCursor = Resources.getInstance().getBoolProperty(
				"disable_cursor_move_paper");

		addComponentListener(new ResizeListener());
	}

	private void createPropertyChangeListener() {
		propertyChangeListener = new FreemindPropertyListener() {

			public void propertyChanged(String propertyName, String newValue,
					String oldValue) {
				if (propertyName.equals(FreeMind.RESOURCES_NODE_TEXT_COLOR)) {
					standardNodeTextColor = Tools.xmlToColor(newValue);
					controller.getMapModule().getView().getRoot().updateAll();
				} else if (propertyName
						.equals(FreeMind.RESOURCES_BACKGROUND_COLOR)) {
					standardMapBackgroundColor = Tools.xmlToColor(newValue);
					controller.getMapModule().getView()
							.setBackground(standardMapBackgroundColor);
				} else if (propertyName
						.equals(FreeMind.RESOURCES_SELECTED_NODE_COLOR)) {
					standardSelectColor = Tools.xmlToColor(newValue);
					controller.getMapModule().getView().repaintSelecteds();
				} else if (propertyName
						.equals(FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR)) {
					standardSelectRectangleColor = Tools.xmlToColor(newValue);
					controller.getMapModule().getView().repaintSelecteds();
				} else if (propertyName
						.equals(FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION)) {
					standardDrawRectangleForSelection = Tools
							.xmlToBoolean(newValue);
					controller.getMapModule().getView().repaintSelecteds();
				} else if (propertyName
						.equals(FreeMind.RESOURCE_PRINT_ON_WHITE_BACKGROUND)) {
					printOnWhiteBackground = Tools.xmlToBoolean(newValue);
				}
			}
		};
		Controller.addPropertyChangeListener(propertyChangeListener);
	}

	public void initRoot() {
		rootContentLocation = new Point();
		rootView = NodeViewFactory.getInstance().newNodeView(
				getModel().getRootNode(), 0, this, this);
		rootView.insert();
		revalidate();
	}

	public int getMaxNodeWidth() {
		if (maxNodeWidth == 0) {
			try {
				maxNodeWidth = Integer.parseInt(controller
						.getProperty("max_node_width"));
			} catch (NumberFormatException e) {
				freemind.main.Resources.getInstance().logException(e);
				maxNodeWidth = Integer.parseInt(controller
						.getProperty("el__max_default_window_width"));
			}
		}
		return maxNodeWidth;
	}

	//
	// Navigation
	//

	class CheckLaterForCenterNodeTask extends TimerTask {
		NodeView mNode;

		public CheckLaterForCenterNodeTask(NodeView pNode) {
			super();
			mNode = pNode;
		}

		public void run() {
			centerNode(mNode);
		}

	}

	/**
	 * Problem: Before scrollRectToVisible is called, the node has the location
	 * (0,0), ie. the location first gets calculated after the scrollpane is
	 * actually scrolled. Thus, as a workaround, I simply call
	 * scrollRectToVisible twice, the first time the location of the node is
	 * calculated, the second time the scrollPane is actually scrolled.
	 */
	public void centerNode(final NodeView node) {
		// FIXME: Correct the resize map behaviour.
		Tools.waitForEventQueue();
		if (!isValid()) {
			mCenterNodeTimer.schedule(new CheckLaterForCenterNodeTask(node),
					100);
			return;
		}
		Dimension d = getViewportSize();
		JComponent content = node.getContent();
		Rectangle rect = new Rectangle(content.getWidth() / 2 - d.width / 2,
				content.getHeight() / 2 - d.height / 2, d.width, d.height);
		logger.fine("Scroll to " + rect + ", " + this.getPreferredSize());

		// One call of scrollRectToVisible suffices
		// after patching the FreeMind.java
		// and the FreeMindApplet
		content.scrollRectToVisible(rect);
	}

	// scroll with extension (PN 6.2)
	public void scrollNodeToVisible(NodeView node) {
		scrollNodeToVisible(node, 0);
	}

	// scroll with extension (PN)
	// e.g. the input field is bigger than the node view => scroll in order to
	// fit the input field into the screen
	public void scrollNodeToVisible(NodeView node, int extraWidth) {
		// see centerNode()
		if (!isValid()) {
			nodeToBeVisible = node;
			this.extraWidth = extraWidth;
			return;
		}
		final int HORIZ_SPACE = 10;
		final int HORIZ_SPACE2 = 20;
		final int VERT_SPACE = 5;
		final int VERT_SPACE2 = 10;

		// get left/right dimension
		final JComponent nodeContent = node.getContent();
		int width = nodeContent.getWidth();
		if (extraWidth < 0) { // extra left width
			width -= extraWidth;
			nodeContent.scrollRectToVisible(new Rectangle(-HORIZ_SPACE
					+ extraWidth, -VERT_SPACE, width + HORIZ_SPACE2,
					nodeContent.getHeight() + VERT_SPACE2));
		} else { // extra right width
			width += extraWidth;
			nodeContent.scrollRectToVisible(new Rectangle(-HORIZ_SPACE,
					-VERT_SPACE, width + HORIZ_SPACE2, nodeContent.getHeight()
							+ VERT_SPACE2));
		}
	}

	/**
	 * Scroll the viewport of the map to the south-west, i.e. scroll the map
	 * itself to the north-east.
	 */
	public void scrollBy(int x, int y) {
		Point currentPoint = getViewPosition();
		currentPoint.translate(x, y); // Add the difference to it
		setViewLocation(currentPoint.x, currentPoint.y);
	}

	public void setViewLocation(int x, int y) {
		Point currentPoint = new Point(x, y);
		// Watch for the boundaries
		// Low boundaries
		if (currentPoint.getX() < 0) {
			currentPoint.setLocation(0, currentPoint.getY());
		}
		if (currentPoint.getY() < 0) {
			currentPoint.setLocation(currentPoint.getX(), 0);
		}
		// High boundaries
		Dimension viewportSize = getViewportSize();
		if (viewportSize == null) {
			return;
		}
		Dimension size = getSize();
		// getView() gets viewed area - JPanel
		double maxX = size.getWidth() - viewportSize.getWidth();
		double maxY = size.getHeight() - viewportSize.getHeight();
		if (currentPoint.getX() > maxX) {
			currentPoint.setLocation(maxX, currentPoint.getY());
		}
		if (currentPoint.getY() > maxY) {
			currentPoint.setLocation(currentPoint.getX(), maxY);
		}
		setViewPosition(currentPoint);

	}

	protected void setViewPosition(Point currentPoint) {
		if (getParent() instanceof JViewport) {
			JViewport mapViewport = (JViewport) getParent();
			mapViewport.setViewPosition(currentPoint);
		}
	}

	//
	// Node Navigation
	//

	private NodeView getVisibleLeft(NodeView oldSelected) {
		NodeView newSelected = oldSelected;
		if (oldSelected.getModel().isRoot()) {
			newSelected = oldSelected.getPreferredVisibleChild(true);
		} else if (!oldSelected.isLeft()) {
			newSelected = oldSelected.getVisibleParentView();
		} else {
			// If folded in the direction, unfold
			if (oldSelected.getModel().isFolded()) {
				model.getModeController().setFolded(oldSelected.getModel(),
						false);
				return oldSelected;
			}

			newSelected = oldSelected.getPreferredVisibleChild(true);
			while (newSelected != null && !newSelected.isContentVisible()) {
				newSelected = newSelected.getPreferredVisibleChild(true);
			}
		}
		return newSelected;
	}

	private NodeView getVisibleRight(NodeView oldSelected) {
		NodeView newSelected = oldSelected;
		if (oldSelected.getModel().isRoot()) {
			newSelected = oldSelected.getPreferredVisibleChild(false);
		} else if (oldSelected.isLeft()) {
			newSelected = oldSelected.getVisibleParentView();
		} else {
			// If folded in the direction, unfold
			if (oldSelected.getModel().isFolded()) {
				model.getModeController().setFolded(oldSelected.getModel(),
						false);
				return oldSelected;
			}

			newSelected = oldSelected.getPreferredVisibleChild(false);
			while (newSelected != null && !newSelected.isContentVisible()) {
				newSelected = newSelected.getPreferredVisibleChild(false);
			}
		}
		return newSelected;
	}

	private NodeView getVisibleNeighbour(int directionCode) {
		NodeView oldSelected = getSelected();
		logger.fine("Old selected: " + oldSelected);
		NodeView newSelected = null;

		switch (directionCode) {
		case KeyEvent.VK_LEFT:
			newSelected = getVisibleLeft(oldSelected);
			if (newSelected != null) {
				setSiblingMaxLevel(newSelected.getModel().getNodeLevel());
			}
			return newSelected;

		case KeyEvent.VK_RIGHT:
			newSelected = getVisibleRight(oldSelected);
			if (newSelected != null) {
				setSiblingMaxLevel(newSelected.getModel().getNodeLevel());
			}
			return newSelected;

		case KeyEvent.VK_UP:
			newSelected = oldSelected.getPreviousVisibleSibling();
			break;

		case KeyEvent.VK_DOWN:
			newSelected = oldSelected.getNextVisibleSibling();
			break;

		case KeyEvent.VK_PAGE_UP:
			newSelected = oldSelected.getPreviousPage();
			break;

		case KeyEvent.VK_PAGE_DOWN:
			newSelected = oldSelected.getNextPage();
			break;
		}
		return newSelected != oldSelected ? newSelected : null;
	}

	public void move(KeyEvent e) {
		NodeView newSelected = getVisibleNeighbour(e.getKeyCode());
		logger.fine("New selected: " + newSelected);
		if (newSelected != null) {
			if (!(newSelected == getSelected())) {
				extendSelectionWithKeyMove(newSelected, e);
				scrollNodeToVisible(newSelected);
			}
			e.consume();
		}
	}

	public void resetShiftSelectionOrigin() {
		shiftSelectionOrigin = null;
	}

	private void extendSelectionWithKeyMove(NodeView newlySelectedNodeView,
			KeyEvent e) {
		if (e.isShiftDown()) {
			// left or right
			if (e.getKeyCode() == KeyEvent.VK_LEFT
					|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
				shiftSelectionOrigin = null;
				NodeView toBeNewSelected = newlySelectedNodeView
						.isParentOf(getSelected()) ? newlySelectedNodeView
						: getSelected();

				selectBranch(toBeNewSelected, false);
				makeTheSelected(toBeNewSelected);
				return;
			}

			if (shiftSelectionOrigin == null) {
				shiftSelectionOrigin = getSelected();
			}

			final int newY = getMainViewY(newlySelectedNodeView);
			final int selectionOriginY = getMainViewY(shiftSelectionOrigin);
			int deltaY = newY - selectionOriginY;
			NodeView currentSelected = getSelected();

			// page up and page down
			if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
				for (;;) {
					final int currentSelectedY = getMainViewY(currentSelected);
					if (currentSelectedY > selectionOriginY)
						deselect(currentSelected);
					else
						makeTheSelected(currentSelected);
					if (currentSelectedY <= newY)
						break;
					currentSelected = currentSelected
							.getPreviousVisibleSibling();
				}
				return;
			}

			if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
				for (;;) {
					final int currentSelectedY = getMainViewY(currentSelected);
					if (currentSelectedY < selectionOriginY)
						deselect(currentSelected);
					else
						makeTheSelected(currentSelected);
					if (currentSelectedY >= newY)
						break;
					currentSelected = currentSelected.getNextVisibleSibling();
				}
				return;
			}

			boolean enlargingMove = (deltaY > 0)
					&& (e.getKeyCode() == KeyEvent.VK_DOWN) || (deltaY < 0)
					&& (e.getKeyCode() == KeyEvent.VK_UP);

			if (enlargingMove) {
				toggleSelected(newlySelectedNodeView);
			} else {
				toggleSelected(getSelected());
				makeTheSelected(newlySelectedNodeView);
			}
		} else {
			shiftSelectionOrigin = null;
			selectAsTheOnlyOneSelected(newlySelectedNodeView);
		}
	}

	private int getMainViewY(NodeView node) {
		Point newSelectedLocation = new Point();
		Tools.convertPointToAncestor(node.getMainView(), newSelectedLocation,
				this);
		final int newY = newSelectedLocation.y;
		return newY;
	}

	public void moveToRoot() {
		selectAsTheOnlyOneSelected(getRoot());
		centerNode(getRoot());
	}

	/**
	 * Select the node, resulting in only that one being selected.
	 */
	public void selectAsTheOnlyOneSelected(NodeView newSelected) {
		logger.finest("selectAsTheOnlyOneSelected");
		LinkedList oldSelecteds = getSelecteds();
		// select new node
		this.selected.clear();
		this.selected.add(newSelected);

		// getController().getMode().getDefaultModeController().onSelectHook(newSelected.getModel());
		// set last focused as preferred (PN)
		if (newSelected.getModel().getParentNode() != null) {
			((NodeView) newSelected.getParent()).setPreferredChild(newSelected);
		}

		scrollNodeToVisible(newSelected);
		newSelected.repaintSelected();

		for (ListIterator e = oldSelecteds.listIterator(); e.hasNext();) {
			NodeView oldSelected = (NodeView) e.next();
			if (oldSelected != null) {
				oldSelected.repaintSelected();
			}
		}
	}

	/**
	 * Add the node to the selection if it is not yet there, remove it
	 * otherwise.
	 */
	public void toggleSelected(NodeView newSelected) {
		logger.finest("toggleSelected");
		NodeView oldSelected = getSelected();
		if (isSelected(newSelected)) {
			if (selected.size() > 1) {
				selected.remove(newSelected);
				oldSelected = newSelected;
			}
		} else {
			selected.add(newSelected);
		}
		getSelected().repaintSelected();
		if (oldSelected != null)
			oldSelected.repaintSelected();
	}

	/**
	 * Add the node to the selection if it is not yet there, making it the
	 * focused selected node.
	 */

	public void makeTheSelected(NodeView newSelected) {
		logger.finest("makeTheSelected");
		if (isSelected(newSelected)) {
			selected.moveToFirst(newSelected);
		} else {
			selected.add(newSelected);
		}
		getSelected().repaintSelected();
	}

	public void deselect(NodeView newSelected) {
		if (isSelected(newSelected)) {
			selected.remove(newSelected);
			newSelected.repaintSelected();
		}
	}

	/**
	 * Select the node and his descendants. On extend = false clear up the
	 * previous selection. if extend is false, the past selection will be empty.
	 * if yes, the selection will extended with this node and its children
	 */
	public void selectBranch(NodeView newlySelectedNodeView, boolean extend) {
		// if (!extend || !isSelected(newlySelectedNodeView))
		// toggleSelected(newlySelectedNodeView);
		if (!extend) {
			selectAsTheOnlyOneSelected(newlySelectedNodeView);
		} else if (!isSelected(newlySelectedNodeView)
				&& newlySelectedNodeView.isContentVisible()) {
			toggleSelected(newlySelectedNodeView);
		}
		// select(newSelected,extend);
		for (ListIterator e = newlySelectedNodeView.getChildrenViews()
				.listIterator(); e.hasNext();) {
			NodeView target = (NodeView) e.next();
			selectBranch(target, true);
		}
	}

	public boolean selectContinuous(NodeView newSelected) {
		/* fc, 25.1.2004: corrected due to completely inconsistent behaviour. */
		NodeView oldSelected = null;
		// search for the last already selected item among the siblings:
		LinkedList selList = getSelecteds();
		ListIterator j = selList.listIterator(/* selList.size() */);
		while (j.hasNext()) {
			NodeView selectedNode = (NodeView) j.next();
			if (selectedNode != newSelected
					&& newSelected.isSiblingOf(selectedNode)) {
				oldSelected = selectedNode;
				break;
			}
		}
		// no such sibling found. select the new one, and good bye.
		if (oldSelected == null) {
			if (!isSelected(newSelected) && newSelected.isContentVisible()) {
				toggleSelected(newSelected);
				return true;
			}
			return false;
		}
		// fc, bug fix: only select the nodes on the same side:
		boolean oldPositionLeft = oldSelected.isLeft();
		boolean newPositionLeft = newSelected.isLeft();
		/* find old starting point. */
		ListIterator i = newSelected.getSiblingViews().listIterator();
		while (i.hasNext()) {
			NodeView nodeView = (NodeView) i.next();
			if (nodeView == oldSelected) {
				break;
			}
		}
		/*
		 * Remove all selections for the siblings in the connected component
		 * between old and new.
		 */
		ListIterator i_backup = i;
		while (i.hasNext()) {
			NodeView nodeView = (NodeView) i.next();
			if ((nodeView.isLeft() == oldPositionLeft || nodeView.isLeft() == newPositionLeft)) {
				if (isSelected(nodeView))
					deselect(nodeView);
				else
					break;
			}
		}
		/* other direction. */
		i = i_backup;
		if (i.hasPrevious()) {
			i.previous(); /* this is old selected! */
			while (i.hasPrevious()) {
				NodeView nodeView = (NodeView) i.previous();
				if (nodeView.isLeft() == oldPositionLeft
						|| nodeView.isLeft() == newPositionLeft) {
					if (isSelected(nodeView))
						deselect(nodeView);
					else
						break;
				}
			}
		}
		/* reset iterator */
		i = newSelected.getSiblingViews().listIterator();
		/* find starting point. */
		i = newSelected.getSiblingViews().listIterator();
		while (i.hasNext()) {
			NodeView nodeView = (NodeView) i.next();
			if (nodeView == newSelected || nodeView == oldSelected) {
				if (!isSelected(nodeView) && nodeView.isContentVisible())
					toggleSelected(nodeView);
				break;
			}
		}
		/* select all up to the end point. */
		while (i.hasNext()) {
			NodeView nodeView = (NodeView) i.next();
			if ((nodeView.isLeft() == oldPositionLeft || nodeView.isLeft() == newPositionLeft)
					&& !isSelected(nodeView) && nodeView.isContentVisible())
				toggleSelected(nodeView);
			if (nodeView == newSelected || nodeView == oldSelected) {
				break;
			}
		}
		// now, make oldSelected the last of the list in order to make this
		// repeatable:
		toggleSelected(oldSelected);
		toggleSelected(oldSelected);
		return true;
	}

	//
	// get/set methods
	//

	public MindMap getModel() {
		return model;
	}

	// e.g. for dragging cursor (PN)
	public void setMoveCursor(boolean isHand) {
		int requiredCursor = (isHand && !disableMoveCursor) ? Cursor.MOVE_CURSOR
				: Cursor.DEFAULT_CURSOR;
		if (getCursor().getType() != requiredCursor) {
			setCursor(requiredCursor != Cursor.DEFAULT_CURSOR ? new Cursor(
					requiredCursor) : null);
		}
	}

	NodeMouseMotionListener getNodeMouseMotionListener() {
		return getController().getNodeMouseMotionListener();
	}

	NodeMotionListener getNodeMotionListener() {
		return getController().getNodeMotionListener();
	}

	NodeKeyListener getNodeKeyListener() {
		return getController().getNodeKeyListener();
	}

	DragGestureListener getNodeDragListener() {
		return getController().getNodeDragListener();
	}

	DropTargetListener getNodeDropListener() {
		return getController().getNodeDropListener();
	}

	public NodeView getSelected() {
		if (selected.size() > 0)
			return selected.get(0);
		else
			return null;
	}

	private NodeView getSelected(int i) {
		return selected.get(i);
	}

	public LinkedList getSelecteds() {
		// return an ArrayList of NodeViews.
		LinkedList result = new LinkedList();
		for (int i = 0; i < selected.size(); i++) {
			result.add(getSelected(i));
		}
		return result;
	}

	/**
	 * @return an ArrayList of MindMapNode objects. If both ancestor and
	 *         descendant node are selected, only the ancestor is returned
	 */
	public ArrayList /* of MindMapNodes */getSelectedNodesSortedByY() {
		final HashSet selectedNodesSet = new HashSet();
		for (int i = 0; i < selected.size(); i++) {
			selectedNodesSet.add(getSelected(i).getModel());
		}
		LinkedList pointNodePairs = new LinkedList();

		Point point = new Point();
		iteration: for (int i = 0; i < selected.size(); i++) {
			final NodeView view = getSelected(i);
			final MindMapNode node = view.getModel();
			for (MindMapNode parent = node.getParentNode(); parent != null; parent = parent
					.getParentNode()) {
				if (selectedNodesSet.contains(parent)) {
					continue iteration;
				}
			}
			view.getContent().getLocation(point);
			Tools.convertPointToAncestor(view, point, this);
			pointNodePairs.add(new Pair(new Integer(point.y), node));
		}
		// do the sorting:
		Collections.sort(pointNodePairs, new Comparator() {

			public int compare(Object arg0, Object arg1) {
				if (arg0 instanceof Pair) {
					Pair pair0 = (Pair) arg0;
					if (arg1 instanceof Pair) {
						Pair pair1 = (Pair) arg1;
						Integer int0 = (Integer) pair0.getFirst();
						Integer int1 = (Integer) pair1.getFirst();
						return int0.compareTo(int1);
					}
				}
				throw new IllegalArgumentException("Wrong compare arguments "
						+ arg0 + ", " + arg1);
			}
		});

		ArrayList selectedNodes = new ArrayList();
		for (Iterator it = pointNodePairs.iterator(); it.hasNext();) {
			selectedNodes.add(((Pair) it.next()).getSecond());
		}

		// logger.fine("Cutting #" + selectedNodes.size());
		// for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
		// MindMapNode node = (MindMapNode) it.next();
		// logger.fine("Cutting " + node);
		// }
		return selectedNodes;
	}

	/**
	 * @return an ArrayList of MindMapNode objects. If both ancestor and
	 *         descandant node are selected, only the ancestor ist returned
	 */
	public ArrayList /* of MindMapNodes */getSingleSelectedNodes() {
		ArrayList selectedNodes = new ArrayList(selected.size());
		for (int i = selected.size() - 1; i >= 0; i--) {
			selectedNodes.add(getSelected(i).getModel().shallowCopy());
		}

		return selectedNodes;
	}

	public boolean isSelected(NodeView n) {
		if (isPrinting)
			return false;
		return selected.contains(n);
	}

	public float getZoom() {
		return zoom;
	}

	public int getZoomed(int number) {
		return (int) (number * zoom);
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
		getRoot().updateAll();
		revalidate();
		nodeToBeVisible = getSelected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#validateTree()
	 */
	protected void validateTree() {
		validateSelecteds();
		super.validateTree();
		setViewPositionAfterValidate();
	}

	private void setViewPositionAfterValidate() {
		Point viewPosition = getViewPosition();
		Point oldRootContentLocation = rootContentLocation;
		final NodeView root = getRoot();
		Point newRootContentLocation = root.getContent().getLocation();
		Tools.convertPointToAncestor(getRoot(), newRootContentLocation,
				getParent());

		final int deltaX = newRootContentLocation.x - oldRootContentLocation.x;
		final int deltaY = newRootContentLocation.y - oldRootContentLocation.y;
		if (deltaX != 0 || deltaY != 0) {
			viewPosition.x += deltaX;
			viewPosition.y += deltaY;
			final int scrollMode = getScrollMode();
			// avoid immediate scrolling here:
			setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			setViewPosition(viewPosition);
			setScrollMode(scrollMode);
		} else {
			// FIXME: fc, 7.9.2011: Here, a viewport->repaint was previously.
			// Test if really needed.
			repaint();
		}
		if (nodeToBeVisible != null) {
			final int scrollMode = getScrollMode();
			setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			scrollNodeToVisible(nodeToBeVisible, extraWidth);
			setScrollMode(scrollMode);
			nodeToBeVisible = null;
		}
	}

	/*****************************************************************
	 ** P A I N T I N G **
	 *****************************************************************/

	// private static Image image = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		long startMilli = System.currentTimeMillis();
		if (isValid()) {
			getRoot().getContent().getLocation(rootContentLocation);
			Tools.convertPointToAncestor(getRoot(), rootContentLocation,
					getParent());
		}
		final Graphics2D g2 = (Graphics2D) g;
		final Object renderingHint = g2
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		final Object renderingTextHint = g2
				.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		getController().setTextRenderingHint(g2);
		final Object oldRenderingHintFM = g2
				.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
		final Object newRenderingHintFM = getZoom() != 1F ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
				: RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
		if (oldRenderingHintFM != newRenderingHintFM) {
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					newRenderingHintFM);
		}
		super.paint(g);
		if (oldRenderingHintFM != newRenderingHintFM
				&& RenderingHints.KEY_FRACTIONALMETRICS
						.isCompatibleValue(oldRenderingHintFM)) {
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					oldRenderingHintFM);
		}
		if (RenderingHints.KEY_ANTIALIASING.isCompatibleValue(renderingHint)) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
		}
		if (RenderingHints.KEY_TEXT_ANTIALIASING
				.isCompatibleValue(renderingTextHint)) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					renderingTextHint);
		}

		// final Rectangle rect = getInnerBounds();
		// g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		long localTime = System.currentTimeMillis() - startMilli;
		mPaintingAmount++;
		mPaintingTime += localTime;
		logger.fine("End paint of " + getModel().getRestorable() + " in "
				+ localTime + ". Mean time:"
				+ (mPaintingTime / mPaintingAmount));
	}

	public void paintChildren(Graphics graphics) {
		// first tries for background images.
		// if(image == null) {
		// image =
		// MindIcon.factory("ksmiletris").getIcon(controller.getFrame()).getImage();
		// }
		// graphics.drawImage(image, 0, 0, getHeight(), getWidth(), null);
		HashMap labels = new HashMap();
		mArrowLinkViews = new Vector();
		collectLabels(rootView, labels);
		super.paintChildren(graphics);
		Graphics2D graphics2d = (Graphics2D) graphics;
		Object renderingHint = getController()
				.setEdgesRenderingHint(graphics2d);
		paintLinks(rootView, graphics2d, labels, null);
		Tools.restoreAntialiasing(graphics2d, renderingHint);
		paintSelecteds(graphics2d);
	}

	private void paintSelecteds(Graphics2D g) {
		if (!standardDrawRectangleForSelection || isCurrentlyPrinting()) {
			return;
		}
		final Color c = g.getColor();
		final Stroke s = g.getStroke();
		g.setColor(MapView.standardSelectRectangleColor);
		if (standardSelectionStroke == null) {
			standardSelectionStroke = new BasicStroke(2.0f);
		}
		g.setStroke(standardSelectionStroke);
		Object renderingHint = getController().setEdgesRenderingHint(g);
		final Iterator i = getSelecteds().iterator();
		while (i.hasNext()) {
			NodeView selected = (NodeView) i.next();
			paintSelected(g, selected);
		}
		Tools.restoreAntialiasing(g, renderingHint);
		g.setColor(c);
		g.setStroke(s);
	}

	private void paintSelected(Graphics2D g, NodeView selected) {
		final int arcWidth = 4;
		final JComponent content = selected.getContent();
		Point contentLocation = new Point();
		Tools.convertPointToAncestor(content, contentLocation, this);
		g.drawRoundRect(contentLocation.x - arcWidth, contentLocation.y
				- arcWidth, content.getWidth() + 2 * arcWidth,
				content.getHeight() + 2 * arcWidth, 15, 15);
	}

	/** collect all existing labels in the current map. */
	protected void collectLabels(NodeView source, HashMap labels) {
		// check for existing registry:
		if (getModel().getLinkRegistry() == null)
			return;
		// apply own label:
		String label = getModel().getLinkRegistry().getLabel(source.getModel());
		if (label != null)
			labels.put(label, source);
		for (ListIterator e = source.getChildrenViews().listIterator(); e
				.hasNext();) {
			NodeView target = (NodeView) e.next();
			collectLabels(target, labels);
		}
	}

	protected void paintLinks(NodeView source, Graphics2D graphics,
			HashMap labels, HashSet /* MindMapLink s */LinkAlreadyVisited) {
		// check for existing registry:
		if (getModel().getLinkRegistry() == null)
			return;
		if (LinkAlreadyVisited == null)
			LinkAlreadyVisited = new HashSet();
		// references first
		// logger.fine("Searching for links of " +
		// source.getModel().toString());
		// paint own labels:
		Vector vec = getModel().getLinkRegistry()
				.getAllLinks(source.getModel());
		for (int i = 0; i < vec.size(); ++i) {
			MindMapLink ref = (MindMapLink) vec.get(i);
			if (LinkAlreadyVisited.add(ref)) {
				// determine type of link
				if (ref instanceof MindMapArrowLink) {
					ArrowLinkView arrowLink = new ArrowLinkView(
							(MindMapArrowLink) ref,
							getNodeView(ref.getSource()),
							getNodeView(ref.getTarget()));
					arrowLink.paint(graphics);
					mArrowLinkViews.add(arrowLink);
					// resize map?
					// adjust container size
					// Rectangle rec = arrowLink.getBounds();
					// the following does not work correctly. fc, 23.10.2003:
					// if (rec.x < 0) {
					// getMindMapLayout().resizeMap(rec.x);
					// } else if (rec.x+rec.width > getSize().width) {
					// getMindMapLayout().resizeMap(rec.x+rec.width);
					// }

				}
			}
		}
		for (ListIterator e = source.getChildrenViews().listIterator(); e
				.hasNext();) {
			NodeView target = (NodeView) e.next();
			paintLinks(target, graphics, labels, LinkAlreadyVisited);
		}
	}

	public MindMapArrowLink detectCollision(Point p) {
		if (mArrowLinkViews == null)
			return null;
		for (int i = 0; i < mArrowLinkViews.size(); ++i) {
			ArrowLinkView arrowView = (ArrowLinkView) mArrowLinkViews.get(i);
			if (arrowView.detectCollision(p))
				return arrowView.getModel();
		}
		return null;
	}

	/**
	 * Call preparePrinting() before printing and endPrinting() after printing
	 * to minimize calculation efforts
	 */
	public void preparePrinting() {
		if (!isPrinting) {
			isPrinting = true;
			/* repaint for printing: */
			if (NEED_PREF_SIZE_BUG_FIX) {
				getRoot().updateAll();
				validate();
			} else {
				repaintSelecteds();
			}
			if (printOnWhiteBackground) {
				background = getBackground();
				setBackground(Color.WHITE);
			}
			boundingRectangle = getInnerBounds();
			fitToPage = Resources.getInstance().getBoolProperty("fit_to_page");
		} else {
			logger.warning("Called preparePrinting although isPrinting is true.");
		}
	}

	private void repaintSelecteds() {
		final Iterator iterator = getSelecteds().iterator();
		while (iterator.hasNext()) {
			NodeView next = (NodeView) iterator.next();
			next.repaintSelected();
		}
		// repaint();
	}

	/**
	 * Call preparePrinting() before printing and endPrinting() after printing
	 * to minimize calculation efforts
	 */
	public void endPrinting() {
		if (isPrinting) {
			isPrinting = false;
			if (printOnWhiteBackground) {
				setBackground(background);
			}
			/* repaint for end printing: */
			if (NEED_PREF_SIZE_BUG_FIX) {
				getRoot().updateAll();
				validate();
			} else {
				repaintSelecteds();
			}
		} else {
			logger.warning("Called endPrinting although isPrinting is false.");
		}
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		// TODO:
		// ask user for :
		// - center in page (in page format ?)
		// - print zoom or maximize (in page format ?)
		// - print selection only
		// remember those parameters from one session to another
		// (as orientation & margin from pf)

		// User parameters

		double userZoomFactor = 1;
		try {
			userZoomFactor = Double.parseDouble(controller
					.getProperty("user_zoom"));
		} catch (Exception e) {
			// freemind.main.Resources.getInstance().logException(e);
		}
		userZoomFactor = Math.max(0, userZoomFactor);
		userZoomFactor = Math.min(2, userZoomFactor);

		// TODO: read user parameters from properties, make sure the multiple
		// page
		// printing really works, have look at Book class.

		if (fitToPage && pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		}

		Graphics2D graphics2D = (Graphics2D) graphics;

		try {
			preparePrinting();

			double zoomFactor = 1;
			if (fitToPage) {
				double zoomFactorX = pageFormat.getImageableWidth()
						/ boundingRectangle.getWidth();
				double zoomFactorY = pageFormat.getImageableHeight()
						/ boundingRectangle.getHeight();
				zoomFactor = Math.min(zoomFactorX, zoomFactorY);
			} else {
				zoomFactor = userZoomFactor;

				int nrPagesInWidth = (int) Math.ceil(zoomFactor
						* boundingRectangle.getWidth()
						/ pageFormat.getImageableWidth());
				int nrPagesInHeight = (int) Math.ceil(zoomFactor
						* boundingRectangle.getHeight()
						/ pageFormat.getImageableHeight());
				if (pageIndex >= nrPagesInWidth * nrPagesInHeight) {
					return Printable.NO_SUCH_PAGE;
				}
				int yPageCoord = (int) Math.floor(pageIndex / nrPagesInWidth);
				int xPageCoord = pageIndex - yPageCoord * nrPagesInWidth;

				graphics2D.translate(-pageFormat.getImageableWidth()
						* xPageCoord, -pageFormat.getImageableHeight()
						* yPageCoord);
			}

			graphics2D.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());
			graphics2D.scale(zoomFactor, zoomFactor);
			graphics2D.translate(-boundingRectangle.getX(),
					-boundingRectangle.getY());

			print(graphics2D);
		} finally {
			endPrinting();
		}
		return Printable.PAGE_EXISTS;
	}

	// public void print(Graphics g) {
	// try{
	// preparePrinting();
	// super.print(g);
	// }
	// finally{
	// endPrinting();
	// }
	// }

	/**
	 * For nodes, they can ask, whether or not the width must be bigger to
	 * prevent the "..." at the output. (Bug of java).
	 */
	public boolean isCurrentlyPrinting() {
		return isPrinting;
	};

	/**
	 * Return the bounding box of all the descendants of the source view, that
	 * without BORDER. Should that be implemented in LayoutManager as minimum
	 * size?
	 */
	public Rectangle getInnerBounds() {
		final Rectangle innerBounds = getRoot().getInnerBounds();
		innerBounds.x += getRoot().getX();
		innerBounds.y += getRoot().getY();
		final Rectangle maxBounds = new Rectangle(0, 0, getWidth(), getHeight());
		for (int i = 0; i < mArrowLinkViews.size(); ++i) {
			ArrowLinkView arrowView = (ArrowLinkView) mArrowLinkViews.get(i);
			final CubicCurve2D arrowLinkCurve = arrowView.arrowLinkCurve;
			if (arrowLinkCurve == null) {
				continue;
			}
			Rectangle arrowViewBigBounds = arrowLinkCurve.getBounds();
			if (!innerBounds.contains(arrowViewBigBounds)) {
				Rectangle arrowViewBounds = PathBBox.getBBox(arrowLinkCurve)
						.getBounds();
				innerBounds.add(arrowViewBounds);
			}
		}
		return innerBounds.intersection(maxBounds);
	}

	public NodeView getRoot() {
		return rootView;
	}

	private MindMapLayout getMindMapLayout() {
		return (MindMapLayout) getLayout();
	}

	/**
	 * This method is a workaround to allow the inner class access to "this".
	 * Change it as soon the correct syntax is known.
	 */
	private MapView getMap() {
		return this;
	}

	public Controller getController() {
		return controller;
	}

	// this property is used when the user navigates up/down using cursor keys
	// (PN)
	// it will keep the level of nodes that are understand as "siblings"

	public int getSiblingMaxLevel() {
		return this.siblingMaxLevel;
	}

	public void setSiblingMaxLevel(int level) {
		this.siblingMaxLevel = level;
	}

	private static final int margin = 20;
	private Timer mCenterNodeTimer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.Autoscroll#getAutoscrollInsets()
	 */
	public Insets getAutoscrollInsets() {
		Rectangle outer = getBounds();
		Rectangle inner = getParent().getBounds();
		return new Insets(inner.y - outer.y + margin, inner.x - outer.x
				+ margin, outer.height - inner.height - inner.y + outer.y
				+ margin, outer.width - inner.width - inner.x + outer.x
				+ margin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.Autoscroll#autoscroll(java.awt.Point)
	 */
	public void autoscroll(Point cursorLocn) {
		Rectangle r = new Rectangle((int) cursorLocn.getX() - margin,
				(int) cursorLocn.getY() - margin, 1 + 2 * margin,
				1 + 2 * margin);
		scrollRectToVisible(r);
	}

	public NodeView getNodeView(MindMapNode node) {
		if (node == null) {
			return null;
		}
		Collection viewers = node.getViewers();
		final Iterator iterator = viewers.iterator();
		while (iterator.hasNext()) {
			NodeView candidateView = (NodeView) iterator.next();
			if (candidateView.getMap() == this) {
				return candidateView;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		if (!getParent().isValid()) {
			final Dimension preferredLayoutSize = getLayout()
					.preferredLayoutSize(this);
			return preferredLayoutSize;
		}
		return super.getPreferredSize();
	}

	void revalidateSelecteds() {
		selectedsValid = false;
	}

	private void validateSelecteds() {
		if (selectedsValid) {
			return;
		}
		selectedsValid = true;
		// Keep selected nodes
		logger.finest("validateSelecteds");
		ArrayList selectedNodes = new ArrayList();
		for (ListIterator it = getSelecteds().listIterator(); it.hasNext();) {
			NodeView nodeView = (NodeView) it.next();
			if (nodeView != null) {
				selectedNodes.add(nodeView);
			}
		}
		// Warning, the old views still exist, because JVM has not deleted them.
		// But don't use them!
		selected.clear();
		for (ListIterator it = selectedNodes.listIterator(); it.hasNext();) {
			NodeView oldNodeView = ((NodeView) it.next());
			if (oldNodeView.isContentVisible()) {
				NodeView newNodeView = getNodeView(oldNodeView.getModel());
				// test, whether or not the node is still visible:
				if (newNodeView != null) {
					selected.add(newNodeView);
				}
			}
		}
	}

	public Point getNodeContentLocation(NodeView nodeView) {
		Point contentXY = new Point(0, 0);
		Tools.convertPointToAncestor(nodeView.getContent(), contentXY, this);
		return contentXY;
	}

	/**
	 * Returns the size of the visible part of the view in view coordinates.
	 */
	public Dimension getViewportSize() {
		if (getParent() instanceof JViewport) {
			JViewport mapViewport = (JViewport) getParent();
			return mapViewport == null ? null : mapViewport.getSize();
		}
		return null;
	}

	/**
	 * @return the position of the view or null, if not present.
	 */
	public Point getViewPosition() {
		Point viewPosition = new Point(0, 0);
		if (getParent() instanceof JViewport) {
			JViewport mapViewport = (JViewport) getParent();
			viewPosition = mapViewport.getViewPosition();
		}
		return viewPosition;
	}

	/**
	 * @param pSimpleScrollMode
	 */
	private void setScrollMode(int pSimpleScrollMode) {
		if (getParent() instanceof JViewport) {
			JViewport mapViewport = (JViewport) getParent();
			mapViewport.setScrollMode(pSimpleScrollMode);
		}
	}

	/**
	 * @return
	 */
	private int getScrollMode() {
		if (getParent() instanceof JViewport) {
			JViewport mapViewport = (JViewport) getParent();
			return mapViewport.getScrollMode();
		}
		return 0;
	}

}
