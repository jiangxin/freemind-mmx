/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package tests.freemind;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;
import java.util.Vector;

import javax.swing.JPanel;

import freemind.controller.Controller;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapMode;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.MapView.ScrollPane;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * @date 13.08.2013
 */
public class LayoutTests extends FreeMindTestBase {

	private MindMapNodeModel mRoot;
	private MindMapNodeModel mChild1;
	private MindMapNodeModel mChild2;
	private MapView mMapView;
	private MindMapMapModel mModel;
	private ScrollPane mScrollPane;

	protected void setUp() throws Exception {
		super.setUp();
		JPanel parent = new JPanel();
		Rectangle bounds = new Rectangle(0, 0, 400, 600);
		parent.setBounds(bounds);
		mScrollPane = new MapView.ScrollPane();
		parent.add(mScrollPane, BorderLayout.CENTER);
		Controller controller = new Controller(mFreeMindMain);
		controller.initialization();
		MindMapMode mode = new MindMapMode() {
			public freemind.modes.ModeController createModeController() {
				return new MindMapController(this) {
					protected void init() {
					}
				};
			};
		};
		mode.init(controller);
		MindMapController mc = (MindMapController) mode.createModeController();
		mModel = new MindMapMapModel(mFreeMindMain, mc);
		mc.setModel(mModel);
		mRoot = new MindMapNodeModel("ROOT", mFreeMindMain,
				mModel);
		mChild1 = new MindMapNodeModel("CHILD1", mFreeMindMain,
				mModel);
		mRoot.insert(mChild1, 0);
		mChild2 = new MindMapNodeModel("CHILD2", mFreeMindMain,
				mModel);
		mRoot.insert(mChild2, 1);
		mModel.setRoot(mRoot);
		mMapView = new MapView(mModel, controller) {
			DragGestureListener getNodeDragListener() {
				return null;
			}

			DropTargetListener getNodeDropListener() {
				return null;
			}

			public void selectAsTheOnlyOneSelected(NodeView pNewSelected,
					boolean pRequestFocus) {
			}

		};
		mScrollPane.setViewportView(mMapView);
		mc.setView(mMapView);
		mMapView.setBounds(parent.getBounds());
		parent.setOpaque(true);
		parent.setDoubleBuffered(false); // for better performance
		parent.doLayout();
		Tools.waitForEventQueue();
		mMapView.addNotify();
	}

	public void testYShift() throws Exception {
		layout(mMapView);
		int yCoordinate = getYCoordinate(mChild2);
		int yCoordinateRoot = getYCoordinate(mRoot);
		mChild2.setShiftY(10);
		layout(mMapView);
		assertTrue(getYCoordinate(mChild1) != getYCoordinate(mChild2));
		assertEquals(yCoordinateRoot, getYCoordinate(mRoot));
		assertEquals(yCoordinate + 10, getYCoordinate(mChild2));
	}

	public void testYShiftNegative() throws Exception {
		layout(mMapView);
		int yCoordinate = getYCoordinate(mChild2);
		int yCoordinateRoot = getYCoordinate(mRoot);
		int yCoordinateChild1 = getYCoordinate(mChild1);
		int delta = -10;
		mChild2.setShiftY(delta);
		layout(mMapView);
		assertTrue(getYCoordinate(mChild1) != getYCoordinate(mChild2));
		assertEquals(yCoordinateRoot - delta, getYCoordinate(mRoot));
		assertEquals(yCoordinateChild1, getYCoordinate(mChild1));
		assertEquals(yCoordinate, getYCoordinate(mChild2));
	}
	
	public void testYShiftNegativeWith3Childs() throws Exception {
		MindMapNodeModel child3 = new MindMapNodeModel("CHILD3", mFreeMindMain,
				mModel);
		mModel.insertNodeInto(child3, mRoot, 2);
		layout(mMapView);
		int yCoordinate = getYCoordinate(mChild2);
		int yCoordinateRoot = getYCoordinate(mRoot);
		int yCoordinateChild1 = getYCoordinate(mChild1);
		int yCoordinate3 = getYCoordinate(child3);
		int delta = -10;
		mChild2.setShiftY(delta);
//		mModel.save(new File("/tmp/testYShiftNegativeWith3Childs.mm"));
		layout(mMapView);
		assertTrue(getYCoordinate(mChild1) != getYCoordinate(mChild2));
		assertEquals(yCoordinateRoot - delta, getYCoordinate(mRoot));
		assertEquals(yCoordinateChild1, getYCoordinate(mChild1));
		assertEquals(yCoordinate, getYCoordinate(mChild2));
		assertEquals(yCoordinate3 - delta, getYCoordinate(child3));
	}
	
	public void testScrollMap() throws Exception {
		layout(mMapView);
		int yCoordinateRoot = getYCoordinateToViewport(mRoot);
		int delta = 10;
		Point viewPosition = mMapView.getViewPosition();
		mMapView.scrollBy(0, delta);
		layout(mMapView);
		assertEquals(viewPosition.y + delta, mMapView.getViewPosition().y);
		assertEquals(yCoordinateRoot - delta, getYCoordinateToViewport(mRoot));
	}
	
	public void testYShiftNegativeWith3ChildsWithRootMovement() throws Exception {
		MindMapNodeModel child3 = new MindMapNodeModel("CHILD3", mFreeMindMain,
				mModel);
		mModel.insertNodeInto(child3, mRoot, 2);
		layout(mMapView);
		int yCoordinate = getYCoordinateToViewport(mChild2);
		int yCoordinateRoot = getYCoordinateToViewport(mRoot);
		int yCoordinateChild1 = getYCoordinateToViewport(mChild1);
		int yCoordinate3 = getYCoordinateToViewport(child3);
		int delta = -10;
		mChild2.setShiftY(delta);
		mMapView.scrollBy(0, -delta);
		layout(mMapView);
		assertTrue(getYCoordinateToViewport(mChild1) != getYCoordinateToViewport(mChild2));
		assertEquals(yCoordinateRoot, getYCoordinateToViewport(mRoot));
		assertEquals(yCoordinateChild1 + delta, getYCoordinateToViewport(mChild1));
		assertEquals(yCoordinate + delta, getYCoordinateToViewport(mChild2));
		assertEquals(yCoordinate3, getYCoordinateToViewport(child3));
	}
	
	public void testYShiftNegativeWith3ChildsYCalcToRoot() throws Exception {
		MindMapNodeModel child3 = new MindMapNodeModel("CHILD3", mFreeMindMain,
				mModel);
		mModel.insertNodeInto(child3, mRoot, 2);
		layout(mMapView);
		int yCoordinateRoot = getYCoordinate(mRoot);
		int yCoordinate = getYCoordinate(mChild2) - yCoordinateRoot;
		int yCoordinateChild1 = getYCoordinate(mChild1) - yCoordinateRoot;
		int yCoordinate3 = getYCoordinate(child3) - yCoordinateRoot;
		int delta = -10;
		mChild2.setShiftY(delta);
		layout(mMapView);
		int yCoordinateRoot2 = getYCoordinate(mRoot);
		assertEquals(yCoordinateChild1 + delta, getYCoordinate(mChild1)-yCoordinateRoot2);
		assertEquals(yCoordinate + delta, getYCoordinate(mChild2)-yCoordinateRoot2);
		assertEquals(yCoordinate3, getYCoordinate(child3)-yCoordinateRoot2);
	}
	
	protected void layout(MapView mapView) {
		NodeView root = mapView.getRoot();
		LayoutManager layout = root.getLayout();
		layout.layoutContainer(root);
		root.getMainView().doLayout();
		Vector nodes = Tools.getVectorWithSingleElement(root);
		// print summary
		System.out.println("------------------");
		while(!nodes.isEmpty()) {
			NodeView view = (NodeView) nodes.lastElement();
			nodes.remove(view);
			nodes.addAll(view.getChildrenViews());
			int yCoordinate = getYCoordinate(view.getModel());
			int yCoordinateRoot = getYCoordinate(root.getModel());
			System.out.println("Y coordinate of node " + view.getModel() + " is " + (yCoordinate-yCoordinateRoot));
		}
		System.out.println("------------------");
	}

	protected int getYCoordinate(MindMapNode child2) {
		assertTrue(child2.getViewers().size() > 0);
		NodeView nodeView = (NodeView) child2.getViewers().iterator().next();
		Point point = nodeView.getMainView().getLocation();
		Tools.convertPointToAncestor(nodeView, point, mMapView);
		return point.y;
	}

	protected int getYCoordinateToViewport(MindMapNode child2) {
		assertTrue(child2.getViewers().size() > 0);
		NodeView nodeView = (NodeView) child2.getViewers().iterator().next();
		Point point = nodeView.getMainView().getLocation();
		Tools.convertPointToAncestor(nodeView, point, mScrollPane);
		return point.y;
	}
	
}
