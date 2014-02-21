/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 *
 * Created on 10.10.2006
 */
/*$Id: MindMapControllerMock.java,v 1.1.2.12 2008/12/09 21:09:43 christianfoltin Exp $*/
package tests.freemind;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.HookFactory;
import freemind.main.FreeMindMain;
import freemind.main.XMLParseException;
import freemind.modes.FreeMindFileDialog;
import freemind.modes.MapAdapter;
import freemind.modes.MapFeedbackAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class MindMapControllerMock extends MapFeedbackAdapter implements
		ModeController {

	private final FreeMindMainMock freeMindMain;
	private MindMapMock mindMapMock;

	public MindMapControllerMock(FreeMindMainMock freeMindMain,
			String pMapXmlString) {
		this.freeMindMain = freeMindMain;

		mindMapMock = new MindMapMock(pMapXmlString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getFrame()
	 */
	@Override
	public FreeMindMain getFrame() {
		return freeMindMain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getMap()
	 */
	@Override
	public MindMap getMap() {
		return mindMapMock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#load(java.net.URL)
	 */
	@Override
	public ModeController load(URL pFile) throws FileNotFoundException,
			IOException, XMLParseException, URISyntaxException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#load(java.io.File)
	 */
	@Override
	public ModeController load(File pFile) throws FileNotFoundException,
			IOException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#loadURL(java.lang.String)
	 */
	@Override
	public void loadURL(String pRelative) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#save(java.io.File)
	 */
	@Override
	public boolean save(File pFile) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#newMap()
	 */
	@Override
	public ModeController newMap() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#save()
	 */
	@Override
	public boolean save() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#saveAs()
	 */
	@Override
	public boolean saveAs() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#open()
	 */
	@Override
	public void open() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#close(boolean,
	 * freemind.controller.MapModuleManager)
	 */
	@Override
	public boolean close(boolean pForce, MapModuleManager pMapModuleManager) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#startupController()
	 */
	@Override
	public void startupController() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#shutdownController()
	 */
	@Override
	public void shutdownController() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#doubleClick(java.awt.event.MouseEvent)
	 */
	@Override
	public void doubleClick(MouseEvent pE) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#plainClick(java.awt.event.MouseEvent)
	 */
	@Override
	public void plainClick(MouseEvent pE) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean pVisible) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#isBlocked()
	 */
	@Override
	public boolean isBlocked() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getNodeFromID(java.lang.String)
	 */
	@Override
	public NodeAdapter getNodeFromID(String pNodeID) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getNodeID(freemind.modes.MindMapNode)
	 */
	@Override
	public String getNodeID(MindMapNode pSelected) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#select(freemind.view.mindmapview.NodeView)
	 */
	@Override
	public void select(NodeView pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#select(freemind.modes.MindMapNode,
	 * java.util.List)
	 */
	@Override
	public void select(MindMapNode pFocused, List pSelecteds) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#selectBranch(freemind.view.mindmapview.
	 * NodeView, boolean)
	 */
	@Override
	public void selectBranch(NodeView pSelected, boolean pExtend) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelected()
	 */
	@Override
	public MindMapNode getSelected() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelectedView()
	 */
	@Override
	public NodeView getSelectedView() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelecteds()
	 */
	@Override
	public List getSelecteds() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelectedsByDepth()
	 */
	@Override
	public List getSelectedsByDepth() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#sortNodesByDepth(java.util.List)
	 */
	@Override
	public void sortNodesByDepth(List pInPlaceList) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#extendSelection(java.awt.event.MouseEvent)
	 */
	@Override
	public boolean extendSelection(MouseEvent pE) {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setSaved(boolean)
	 */
	@Override
	public void setSaved(boolean pIsClean) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#registerNodeSelectionListener(freemind.
	 * modes.ModeController.NodeSelectionListener, boolean)
	 */
	@Override
	public void registerNodeSelectionListener(NodeSelectionListener pListener,
			boolean pCallWithCurrentSelection) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#deregisterNodeSelectionListener(freemind
	 * .modes.ModeController.NodeSelectionListener)
	 */
	@Override
	public void deregisterNodeSelectionListener(NodeSelectionListener pListener) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#registerNodeLifetimeListener(freemind.modes
	 * .ModeController.NodeLifetimeListener, boolean)
	 */
	@Override
	public void registerNodeLifetimeListener(NodeLifetimeListener pListener,
			boolean pFireCreateEvent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#deregisterNodeLifetimeListener(freemind
	 * .modes.ModeController.NodeLifetimeListener)
	 */
	@Override
	public void deregisterNodeLifetimeListener(NodeLifetimeListener pListener) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#displayNode(freemind.modes.MindMapNode)
	 */
	@Override
	public void displayNode(MindMapNode pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#centerNode(freemind.modes.MindMapNode)
	 */
	@Override
	public void centerNode(MindMapNode pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#getLinkShortText(freemind.modes.MindMapNode
	 * )
	 */
	@Override
	public String getLinkShortText(MindMapNode pNode) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getModeToolBar()
	 */
	@Override
	public JToolBar getModeToolBar() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getLeftToolBar()
	 */
	@Override
	public Component getLeftToolBar() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updateMenus(freemind.controller.
	 * StructuredMenuHolder)
	 */
	@Override
	public void updateMenus(StructuredMenuHolder pHolder) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updatePopupMenu(freemind.controller.
	 * StructuredMenuHolder)
	 */
	@Override
	public void updatePopupMenu(StructuredMenuHolder pHolder) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getPopupMenu()
	 */
	@Override
	public JPopupMenu getPopupMenu() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#showPopupMenu(java.awt.event.MouseEvent)
	 */
	@Override
	public void showPopupMenu(MouseEvent pE) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getPopupForModel(java.lang.Object)
	 */
	@Override
	public JPopupMenu getPopupForModel(Object pObj) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getView()
	 */
	@Override
	public MapView getView() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setModel(freemind.modes.MapAdapter)
	 */
	@Override
	public void setModel(MapAdapter pModel) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getMode()
	 */
	@Override
	public Mode getMode() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getMapModule()
	 */
	@Override
	public MapModule getMapModule() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getController()
	 */
	@Override
	public Controller getController() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getHookFactory()
	 */
	@Override
	public HookFactory getHookFactory() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelectionColor()
	 */
	@Override
	public Color getSelectionColor() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getText(java.lang.String)
	 */
	@Override
	public String getText(String pTextId) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String pPath) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#getNodeView(freemind.modes.MindMapNode)
	 */
	@Override
	public NodeView getNodeView(MindMapNode pNode) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#refreshMap()
	 */
	@Override
	public void refreshMap() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#copy(freemind.modes.MindMapNode,
	 * boolean)
	 */
	@Override
	public Transferable copy(MindMapNode pNode, boolean pSaveInvisible) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#copy()
	 */
	@Override
	public Transferable copy() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#copySingle()
	 */
	@Override
	public Transferable copySingle() {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#copy(java.util.List, boolean)
	 */
	@Override
	public Transferable copy(List pSelectedNodes, boolean pCopyInvisible) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#getFileChooser(javax.swing.filechooser.
	 * FileFilter)
	 */
	@Override
	public FreeMindFileDialog getFileChooser(FileFilter pFilter) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#setView(freemind.view.mindmapview.MapView)
	 */
	@Override
	public void setView(MapView pView) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setToolTip(freemind.modes.MindMapNode,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setToolTip(MindMapNode pNode, String pKey, String pValue) {

	}

}
