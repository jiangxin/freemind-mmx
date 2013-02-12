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
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
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
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.AttributeController;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class MindMapControllerMock implements ModeController {

	private final FreeMindMainMock freeMindMain;
	private MindMapMock mindMapMock;

	public MindMapControllerMock(FreeMindMainMock freeMindMain,
			String pMapXmlString) {
		this.freeMindMain = freeMindMain;
		// TODO Auto-generated constructor stub
		mindMapMock = new MindMapMock(pMapXmlString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#centerNode(freemind.modes.MindMapNode)
	 */
	public void centerNode(MindMapNode node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#close(boolean,
	 * freemind.controller.MapModuleManager)
	 */
	public boolean close(boolean force, MapModuleManager mapModuleManager) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#createNodeTreeFromXml(java.io.Reader)
	 */
	public MindMapNode createNodeTreeFromXml(Reader pReader, HashMap pIDToTarget)
			throws XMLParseException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#deregisterNodeLifetimeListener(freemind
	 * .modes.ModeController.NodeLifetimeListener)
	 */
	public void deregisterNodeLifetimeListener(NodeLifetimeListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#deregisterNodeSelectionListener(freemind
	 * .modes.ModeController.NodeSelectionListener)
	 */
	public void deregisterNodeSelectionListener(NodeSelectionListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#displayNode(freemind.modes.MindMapNode)
	 */
	public void displayNode(MindMapNode node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#doubleClick(java.awt.event.MouseEvent)
	 */
	public void doubleClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#extendSelection(java.awt.event.MouseEvent)
	 */
	public boolean extendSelection(MouseEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#fireNodeDeleteEvent(freemind.modes.MindMapNode
	 * )
	 */
	public void fireNodePreDeleteEvent(MindMapNode node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#firePreSaveEvent(freemind.modes.MindMapNode
	 * )
	 */
	public void firePreSaveEvent(MindMapNode node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getAttributeController()
	 */
	public AttributeController getAttributeController() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getController()
	 */
	public Controller getController() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getFrame()
	 */
	public FreeMindMain getFrame() {
		// TODO Auto-generated method stub
		return freeMindMain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getHookFactory()
	 */
	public HookFactory getHookFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getLeftToolBar()
	 */
	public Component getLeftToolBar() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#getLinkShortText(freemind.modes.MindMapNode
	 * )
	 */
	public String getLinkShortText(MindMapNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getMap()
	 */
	public MindMap getMap() {
		// TODO Auto-generated method stub
		return mindMapMock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getMode()
	 */
	public Mode getMode() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getModeToolBar()
	 */
	public JToolBar getModeToolBar() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getNodeFromID(java.lang.String)
	 */
	public NodeAdapter getNodeFromID(String nodeID) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getNodeID(freemind.modes.MindMapNode)
	 */
	public String getNodeID(MindMapNode selected) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getPopupForModel(java.lang.Object)
	 */
	public JPopupMenu getPopupForModel(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getPopupMenu()
	 */
	public JPopupMenu getPopupMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getResource(java.lang.String)
	 */
	public URL getResource(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelected()
	 */
	public MindMapNode getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelecteds()
	 */
	public List getSelecteds() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelectedsByDepth()
	 */
	public List getSelectedsByDepth() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getSelectionColor()
	 */
	public Color getSelectionColor() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getText(java.lang.String)
	 */
	public String getText(String textId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#getView()
	 */
	public MapView getView() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#isBlocked()
	 */
	public boolean isBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#load(java.net.URL)
	 */
	public ModeController load(URL file) throws FileNotFoundException,
			IOException, XMLParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#loadURL(java.lang.String)
	 */
	public void loadURL(String relative) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#newMap()
	 */
	public MindMap newMap() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#nodeChanged(freemind.modes.MindMapNode)
	 */
	public void nodeChanged(MindMapNode n) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#nodeRefresh(freemind.modes.MindMapNode)
	 */
	public void nodeRefresh(MindMapNode node) {
		// TODO Auto-generated method stub

	}

	public Transferable cut(MindMapNode node) {
		return null;
	}

	public Transferable copy(MindMapNode node, boolean saveInvisible) {
		return null;
	}

	public Transferable copy(MindMapNode node) {
		return null;
	}

	public Transferable copy() {
		return null;
	}

	public Transferable copySingle() {
		return null;
	}

	public Transferable copy(List selectedNodes, boolean copyInvisible) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#onDeselectHook(freemind.modes.MindMapNode)
	 */
	public void onFocusNode(NodeView node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#onSelectHook(freemind.modes.MindMapNode)
	 */
	public void onLostFocusNode(NodeView node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#open()
	 */
	public void open() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#plainClick(java.awt.event.MouseEvent)
	 */
	public void plainClick(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#registerNodeLifetimeListener(freemind.modes
	 * .ModeController.NodeLifetimeListener)
	 */
	public void registerNodeLifetimeListener(NodeLifetimeListener listener, boolean pFireCreateEvent) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#registerNodeSelectionListener(freemind.
	 * modes.ModeController.NodeSelectionListener)
	 */
	public void registerNodeSelectionListener(NodeSelectionListener listener, boolean pCallWithCurrentSelection) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#save(java.io.File)
	 */
	public boolean save(File file) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#save()
	 */
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#saveAs()
	 */
	public boolean saveAs() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#select(freemind.view.mindmapview.NodeView)
	 */
	public void select(NodeView node) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#select(freemind.modes.MindMapNode)
	 */
	public void select(MindMapNode selected) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setFolded(freemind.modes.MindMapNode,
	 * boolean)
	 */
	public void setFolded(MindMapNode node, boolean folded) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setModel(freemind.modes.MapAdapter)
	 */
	public void setModel(MapAdapter model) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController#showPopupMenu(java.awt.event.MouseEvent)
	 */
	public void showPopupMenu(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#shutdownController()
	 */
	public void shutdownController() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#sortNodesByDepth(java.util.List)
	 */
	public void sortNodesByDepth(List inPlaceList) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#startupController()
	 */
	public void startupController() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updateMenus(freemind.controller.
	 * StructuredMenuHolder)
	 */
	public void updateMenus(StructuredMenuHolder holder) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updatePopupMenu(freemind.controller.
	 * StructuredMenuHolder)
	 */
	public void updatePopupMenu(StructuredMenuHolder holder) {
		// TODO Auto-generated method stub

	}

	public NodeView getSelectedView() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeView getNodeView(MindMapNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshMap() {
		// TODO Auto-generated method stub

	}

	public void onViewCreatedHook(NodeView newView) {
		// TODO Auto-generated method stub

	}

	public void onViewRemovedHook(NodeView newView) {
		// TODO Auto-generated method stub

	}

	public void setBackgroundColor(Color color) {
		// TODO Auto-generated method stub

	}

	public FreeMindFileDialog getFileChooser(FileFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public ModeController load(File pFile) throws FileNotFoundException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void select(MindMapNode pFocused, List pSelecteds) {
		// TODO Auto-generated method stub

	}

	public void selectBranch(NodeView pSelected, boolean pExtend) {
		// TODO Auto-generated method stub

	}

	public void setView(MapView pView) {
		// TODO Auto-generated method stub

	}

	public MapModule getMapModule() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.ModeController#changeSelection(freemind.view.mindmapview.NodeView, boolean)
	 */
	public void changeSelection(NodeView pNode, boolean pIsSelected) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.ModeController#setToolTip(freemind.modes.MindMapNode, java.lang.String, java.lang.String)
	 */
	public void setToolTip(MindMapNode pNode, String pKey, String pValue) {
		// TODO Auto-generated method stub
		
	}
}
