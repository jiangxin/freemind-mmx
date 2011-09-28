/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.view.mindmapview;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import freemind.controller.Controller;
import freemind.extensions.HookFactory;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.HookRegistration;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.HookFactory.RegistrationContainer;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapMode;

/**
 * @author foltin
 * @date 28.09.2011
 */
public class IndependantMapViewCreator {

	public MapView createMapViewForFile(String inputFileName, JPanel parent, FreeMindMain pFreeMindMain)
			throws FileNotFoundException, IOException, URISyntaxException {
		Controller controller = new Controller(pFreeMindMain);
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
		setNodeHookFactory(mc);
		MindMapMapModel model = new MindMapMapModel(pFreeMindMain, mc);
		mc.setModel(model);
		model.load(ClassLoader.getSystemResource(inputFileName));
		MapView mapView = createMapView(controller, model);
		parent.add(mapView, BorderLayout.CENTER);
		mc.setView(mapView);
		mapView.setBounds(parent.getBounds());
		Tools.waitForEventQueue();
		mapView.addNotify();
		return mapView;
	}

	public void exportFileToPng(String inputFileName, String outputFileName, FreeMindMain pFreeMindMain)
			throws FileNotFoundException, IOException, URISyntaxException {
		System.setProperty("java.awt.headless", "true");
		JPanel parent = new JPanel();
		Rectangle bounds = new Rectangle(0,0,400,600);
		parent.setBounds(bounds);
		MapView mapView = createMapViewForFile(inputFileName, parent, pFreeMindMain);
		// layout components:
		mapView.getRoot().getMainView().doLayout();
		parent.setOpaque(true);
		parent.setDoubleBuffered(false); // for better performance
		parent.doLayout();
		parent.validate(); // this might not be necessary
		mapView.preparePrinting();
		Rectangle dim = mapView.getBounds();
		Rectangle dimI = mapView.getInnerBounds();
		parent.setBounds(dim);
		// do print
		BufferedImage backBuffer = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = backBuffer.createGraphics();
		g.translate(-dim.x, -dim.y);
		g.clipRect(dim.x, dim.y, dim.width, dim.height);
		parent.print(g); // this might not be necessary
		backBuffer = backBuffer
				.getSubimage(dimI.x, dimI.y, dimI.width, dimI.height);

		FileOutputStream out1 = new FileOutputStream(outputFileName);
		ImageIO.write(backBuffer, "png", out1);
		out1.close();
	}


	
	protected MapView createMapView(Controller controller,
			MindMapMapModel model) {
		MapView mapView = new MapView(model, controller) {
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
		return mapView;
	}

	protected static void setNodeHookFactory(MindMapController mc) {
		mc.setNodeHookFactory(new HookFactory() {

			public Vector getPossibleNodeHooks() {
				// TODO Auto-generated method stub
				return new Vector();
			}

			public Vector getPossibleModeControllerHooks() {
				// TODO Auto-generated method stub
				return new Vector();
			}

			public ModeControllerHook createModeControllerHook(String pHookName) {
				// TODO Auto-generated method stub
				return null;
			}

			public NodeHook createNodeHook(String pHookName) {
				// TODO Auto-generated method stub
				return null;
			}

			public PermanentNodeHook getHookInNode(MindMapNode pNode,
					String pHookName) {
				// TODO Auto-generated method stub
				return null;
			}

			public List getHookMenuPositions(String pHookName) {
				// TODO Auto-generated method stub
				return new Vector();
			}

			public HookInstanciationMethod getInstanciationMethod(
					String pHookName) {
				// TODO Auto-generated method stub
				return null;
			}

			public List getRegistrations() {
				// TODO Auto-generated method stub
				return null;
			}

			public void registerRegistrationContainer(
					RegistrationContainer pContainer,
					HookRegistration pInstanciatedRegistrationObject) {
				// TODO Auto-generated method stub

			}

			public void deregisterAllRegistrationContainer() {
				// TODO Auto-generated method stub

			}

			public Object getPluginBaseClass(String pHookName) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}


}
