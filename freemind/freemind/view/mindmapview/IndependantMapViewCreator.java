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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import tests.freemind.FreeMindMainMock;
import freemind.controller.MapMouseMotionListener;
import freemind.controller.NodeKeyListener;
import freemind.controller.NodeMotionListener;
import freemind.controller.NodeMouseMotionListener;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHookSubstituteUnknown;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.MapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.view.mindmapview.MapView.ViewFeedback;

/**
 * @author foltin
 * @date 28.09.2011
 */
public class IndependantMapViewCreator implements ViewFeedback, MapFeedback {

	private MindMapMapModel mMap;

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		if (args.length != 2) {
			System.out
					.println("Export map to png.\nUsage:\n java -jar lib/freemind.jar freemind.view.mindmapview.IndependantMapViewCreator <map_path>.mm <picture_path>.png");
			System.exit(0);
		}
		FreeMindMainMock freeMindMain = new FreeMindMainMock();
		IndependantMapViewCreator creator = new IndependantMapViewCreator();
		try {
			String outputFileName = args[1];
			creator.exportFileToPng(args[0], outputFileName, freeMindMain);
			System.out.println("Export to " + outputFileName + " done.");
			System.exit(0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			freemind.main.Resources.getInstance().logException(e);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			freemind.main.Resources.getInstance().logException(e);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			freemind.main.Resources.getInstance().logException(e);

		}
		System.err.println("Error.");
		System.exit(1);
	}

	public MapView createMapViewForFile(String inputFileName, JPanel parent,
			FreeMindMain pFreeMindMain) throws FileNotFoundException,
			IOException, URISyntaxException {
		mMap = new MindMapMapModel(this);
		Tools.FileReaderCreator readerCreator = new Tools.FileReaderCreator(new File(inputFileName));
		MindMapNode node = mMap.loadTree(readerCreator, MapAdapter.sDontAskInstance);
		mMap.setRoot(node);
		MapView mapView = new MapView(mMap, this); 
		parent.add(mapView, BorderLayout.CENTER);
		mapView.setBounds(parent.getBounds());
		Tools.waitForEventQueue();
		mapView.addNotify();
		return mapView;
	}

	public void exportFileToPng(String inputFileName, String outputFileName,
			FreeMindMain pFreeMindMain) throws FileNotFoundException,
			IOException, URISyntaxException {
		JPanel parent = new JPanel();
		Rectangle bounds = new Rectangle(0, 0, 400, 600);
		parent.setBounds(bounds);
		MapView mapView = createMapViewForFile(inputFileName, parent,
				pFreeMindMain);
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
		backBuffer = backBuffer.getSubimage(dimI.x, dimI.y, dimI.width,
				dimI.height);

		FileOutputStream out1 = new FileOutputStream(outputFileName);
		ImageIO.write(backBuffer, "png", out1);
		out1.close();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#fireNodePreDeleteEvent(freemind.modes.MindMapNode)
	 */
	@Override
	public void fireNodePreDeleteEvent(MindMapNode pNode) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#firePreSaveEvent(freemind.modes.MindMapNode)
	 */
	@Override
	public void firePreSaveEvent(MindMapNode pNode) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#nodeChanged(freemind.modes.MindMapNode)
	 */
	@Override
	public void nodeChanged(MindMapNode pNode) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#nodeRefresh(freemind.modes.MindMapNode)
	 */
	@Override
	public void nodeRefresh(MindMapNode pNode) {
	}


	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#insertNodeInto(freemind.modes.MindMapNode, freemind.modes.MindMapNode, int)
	 */
	@Override
	public void insertNodeInto(MindMapNode pNewNode, MindMapNode pParent,
			int pIndex) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#paste(freemind.modes.MindMapNode, freemind.modes.MindMapNode)
	 */
	@Override
	public void paste(MindMapNode pNode, MindMapNode pParent) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#getResourceString(java.lang.String)
	 */
	@Override
	public String getResourceString(String pTextId) {
		return Resources.getInstance().getResourceString(pTextId);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String pResourceId) {
		return Resources.getInstance().getProperty(pResourceId);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#out(java.lang.String)
	 */
	@Override
	public void out(String pFormat) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#getDefaultFont()
	 */
	@Override
	public Font getDefaultFont() {
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#getFontThroughMap(java.awt.Font)
	 */
	@Override
	public Font getFontThroughMap(Font pFont) {
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#changeSelection(freemind.view.mindmapview.NodeView, boolean)
	 */
	@Override
	public void changeSelection(NodeView pNode, boolean pIsSelected) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#onLostFocusNode(freemind.view.mindmapview.NodeView)
	 */
	@Override
	public void onLostFocusNode(NodeView pNode) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#onFocusNode(freemind.view.mindmapview.NodeView)
	 */
	@Override
	public void onFocusNode(NodeView pNode) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#setFolded(freemind.modes.MindMapNode, boolean)
	 */
	@Override
	public void setFolded(MindMapNode pModel, boolean pB) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#onViewCreatedHook(freemind.view.mindmapview.NodeView)
	 */
	@Override
	public void onViewCreatedHook(NodeView pNewView) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#onViewRemovedHook(freemind.view.mindmapview.NodeView)
	 */
	@Override
	public void onViewRemovedHook(NodeView pNodeView) {
		
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MapFeedback#getMap()
	 */
	@Override
	public MindMap getMap() {
		return mMap;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MapFeedback#createNodeHook(java.lang.String, freemind.modes.MindMapNode)
	 */
	@Override
	public NodeHook createNodeHook(String pLoadName, MindMapNode pNode) {
		return new PermanentNodeHookSubstituteUnknown(pLoadName);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.MapFeedback#invokeHooksRecursively(freemind.modes.MindMapNode, freemind.modes.MindMap)
	 */
	@Override
	public void invokeHooksRecursively(MindMapNode pNode, MindMap pModel) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getNodeMouseMotionListener()
	 */
	@Override
	public NodeMouseMotionListener getNodeMouseMotionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getNodeMotionListener()
	 */
	@Override
	public NodeMotionListener getNodeMotionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getNodeKeyListener()
	 */
	@Override
	public NodeKeyListener getNodeKeyListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getNodeDragListener()
	 */
	@Override
	public DragGestureListener getNodeDragListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getNodeDropListener()
	 */
	@Override
	public DropTargetListener getNodeDropListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getMapMouseMotionListener()
	 */
	@Override
	public MapMouseMotionListener getMapMouseMotionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.view.mindmapview.MapView.ViewFeedback#getMapMouseWheelListener()
	 */
	@Override
	public MouseWheelListener getMapMouseWheelListener() {
		// TODO Auto-generated method stub
		return null;
	}

}
