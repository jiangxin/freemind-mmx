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


package freemind.modes;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import freemind.common.TextTranslator;
import freemind.controller.Controller;
import freemind.controller.MapModuleManager;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.HookFactory;
import freemind.main.FreeMindMain;
import freemind.main.XMLParseException;
import freemind.modes.attributes.AttributeController;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

public interface ModeController extends TextTranslator {

	public static final String NODESEPARATOR = "<nodeseparator>";

	/**
	 * @param file
	 *            Nowadays this is an URL to unify the behaviour of the browser
	 *            and the other modes.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XMLParseException
	 * @return returns the new mode controller created for this url.
	 * @throws URISyntaxException
	 */
	ModeController load(URL file) throws FileNotFoundException, IOException,
			XMLParseException, URISyntaxException;

	/**
	 * This is the same as load(URL) for those points where you have a file
	 * instead of an url (conversion is difficult between them...).
	 */
	ModeController load(File file) throws FileNotFoundException, IOException;

	/**
	 * Opens a link in * the opened map * another map * another file.
	 */
	void loadURL(String relative);

	boolean save(File file);

	MindMap newMap();

	/**
	 * @return true, if successfully saved. False, if canceled or exception.
	 */
	boolean save();

	boolean saveAs();

	void open();

	boolean close(boolean force, MapModuleManager mapModuleManager);

	/**
	 * Given a valid Xml parameterization of a node (tree), this method returns
	 * freshly created nodes.
	 */
	MindMapNode createNodeTreeFromXml(Reader pReader, HashMap pIDToTarget)
			throws XMLParseException, IOException;

	// activation methods:
	void startupController();

	void shutdownController();

	// end activation methods.

	// listener -> controller handling
	void doubleClick(MouseEvent e);

	void plainClick(MouseEvent e);

	/**
	 * This method is used to hide the map "under" another opened map. In fact,
	 * should remove the focus, stop plugins, if necessary, etc.
	 */
	void setVisible(boolean visible);

	boolean isBlocked();

	// node identifier (fc, 2.5.2004):
	/**
	 * Given a node identifier, this method returns the corresponding node.
	 * 
	 * @throws IllegalArgumentException
	 *             if the id is unknown.
	 */
	NodeAdapter getNodeFromID(String nodeID);

	/**
	 * Calling this method the map-unique identifier of the node is returned
	 * (and created before, if not present)
	 */
	String getNodeID(MindMapNode selected);

	/**
	 * Single selection: the node is the only one selected after calling this
	 * method.
	 */
	public void select(NodeView node);

	/**
	 * Multiple selection. All MindMapNode s from the selecteds list are
	 * selected, and the focused is moreover focused.
	 */
	public void select(MindMapNode focused, List selecteds);

	public void selectBranch(NodeView selected, boolean extend);

	MindMapNode getSelected();

	NodeView getSelectedView();

	/**
	 * @return a List of MindMapNode s.
	 */
	List getSelecteds();

	/**
	 * @return a LinkedList of MindMapNodes ordered by depth. nodes with greater
	 *         depth occur first.
	 */
	List getSelectedsByDepth();

	/**
	 * nodes with greater depth occur first.
	 * 
	 * @param inPlaceList
	 *            the given list is sorted by reference.
	 */
	public void sortNodesByDepth(List inPlaceList);

	/**
	 * This extends the currently selected nodes.
	 * 
	 * @return true, if the method changed the selection.
	 */
	boolean extendSelection(MouseEvent e);

	/**
	 * Invoke this method after you've changed how a node is to be represented
	 * in the tree.
	 */
	void nodeChanged(MindMapNode n);

	/**
	 * Is called when a node is deselected.
	 */
	void onLostFocusNode(NodeView node);

	/**
	 * Is called when a node is selected.
	 */
	void onFocusNode(NodeView node);

	void onViewCreatedHook(NodeView newView);

	void onViewRemovedHook(NodeView newView);

	/** */
	public interface NodeSelectionListener {

		/**
		 * Sent, if a node is changed
		 * */
		void onUpdateNodeHook(MindMapNode node);

		/**
		 * Is sent when a node is focused (this means, that it is *the* selected node, 
		 * there may only be one!).
		 */
		void onFocusNode(NodeView node);

		/**
		 * Is sent when a node has lost its focus (see {@link onSelectHook()}).
		 */
		void onLostFocusNode(NodeView node);

		/**
		 * Is issued before a node is saved (eg. to save its notes, too, even if
		 * the notes is currently edited).
		 */
		void onSaveNode(MindMapNode node);

		/**
		 * Informs whether or not the node belongs to the group of selected
		 * nodes (in contrast to the focused node above).
		 * @param pNode
		 * @param pIsSelected true, if the node is selected now.
		 */
		void onSelectionChange(NodeView pNode, boolean pIsSelected);
	}

	/**
	 * @param listener
	 * @param pCallWithCurrentSelection if true, the methods for focused and selected nodes
	 * are called directly with the current selection. Otherwise, the first selection change
	 * would provoke the first call to the given listener.
	 */
	void registerNodeSelectionListener(NodeSelectionListener listener, boolean pCallWithCurrentSelection);

	void deregisterNodeSelectionListener(NodeSelectionListener listener);

	/**
	 * Is issued before a node is saved (eg. to save its notes, too, even if the
	 * notes is currently edited). It is issued via NodeSelectionListener.
	 */
	void firePreSaveEvent(MindMapNode node);

	/** */
	public interface NodeLifetimeListener {

		/**
		 * Sent, if a node is created (on map startup or during operations).
		 * */
		void onCreateNodeHook(MindMapNode node);

		/**
		 * Is sent before a node is deleted (on map shutdown, too).
		 */
		void onPreDeleteNode(MindMapNode node);

		/**
		 * Is sent before after a node is deleted (on map shutdown, this event
		 * is *not* send).
		 */
		void onPostDeleteNode(MindMapNode node, MindMapNode parent);

	}

	/**
	 * The onCreateNodeHook is called for every node (depest nodes first) after
	 * registration.
	 * @param pFireCreateEvent TODO
	 */
	void registerNodeLifetimeListener(NodeLifetimeListener listener, boolean pFireCreateEvent);

	void deregisterNodeLifetimeListener(NodeLifetimeListener listener);

	/**
	 * Is issued before a node is deleted. It is issued via
	 * NodeLifetimeListener.
	 */
	void fireNodePreDeleteEvent(MindMapNode node);

	/**
	 * The position of this method is an exception. Normally, every method that
	 * changes nodes must be contained in the specific mode controllers but as
	 * this method is also used by the MapView to switch to neighbours (private
	 * NodeView getNeighbour(int directionCode)), we make this exception here
	 * (fc, 6.11.2005).
	 */
	void setFolded(MindMapNode node, boolean folded);

	/**
	 * Unfolds a node if necessary.
	 */
	void displayNode(MindMapNode node);

	/**
	 * Node is displayed and selected as the only one selected. It is moved to
	 * the center of the screen.
	 */
	void centerNode(MindMapNode node);

	String getLinkShortText(MindMapNode node);

	public JToolBar getModeToolBar();

	/** For the toolbar on the left hand side of the window. */
	public Component getLeftToolBar();

	/** Use this method to get menus to the screen. */
	public void updateMenus(StructuredMenuHolder holder);

	public void updatePopupMenu(StructuredMenuHolder holder);

	JPopupMenu getPopupMenu();

	void showPopupMenu(MouseEvent e);

	/** This returns a context menu for an object placed in the background pane. */
	JPopupMenu getPopupForModel(java.lang.Object obj);

	FreeMindMain getFrame();

	MapView getView();

	MindMap getMap();

	/**
	 * This method must only be used by the model itself at creation time. Don't
	 * use this method.
	 */
	void setModel(MapAdapter model);

	Mode getMode();

	MapModule getMapModule();

	Controller getController();

	HookFactory getHookFactory();

	Color getSelectionColor();

	/**
	 * Get text from resource file
	 */
	String getText(String textId);

	URL getResource(String path);

	AttributeController getAttributeController();

	void nodeRefresh(MindMapNode node);

	NodeView getNodeView(MindMapNode node);

	void refreshMap();

	Transferable copy(MindMapNode node, boolean saveInvisible);

	Transferable copy();

	Transferable copySingle();

	public Transferable copy(List selectedNodes, boolean copyInvisible);

	FreeMindFileDialog getFileChooser(FileFilter filter);

	void setView(MapView pView);

	/**
	 * @see NodeSelectionListener
	 * @param pNode
	 * @param pIsSelected
	 */
	void changeSelection(NodeView pNode, boolean pIsSelected);

	/**
	 * @param key
	 *            key value patterns is used to ensure, that more than one
	 *            tooltip can be displayed.
	 * @param value
	 *            null if you want to delete this tooltip.
	 */
	public void setToolTip(MindMapNode node, String key, String value);

}
