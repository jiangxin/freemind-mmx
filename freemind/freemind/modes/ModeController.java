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
/*$Id: ModeController.java,v 1.14.14.6.6.3 2006-01-22 12:24:38 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JPopupMenu;

import freemind.controller.Controller;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.ActionFactory;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.main.FreeMindMain;
import freemind.main.XMLParseException;
import freemind.modes.actions.MindMapActions;
import freemind.modes.attributes.AttributeController;
import freemind.view.mindmapview.MapView;

public interface ModeController extends MindMapActions {

    void load(File file) throws FileNotFoundException, IOException, XMLParseException;
    boolean save(File file);
    //void addNew(NodeView target, int newNodeMode, KeyEvent e);
    /**
     * @param userObject is typically a text. 
     * */
	MindMapNode newNode(Object userObject, MindMap map);
    MindMap newMap();
    boolean save();
    boolean saveAs();
    void open();
    //    void edit(NodeView node, NodeView toBeSelected);
    boolean close(boolean force);
    // activation methods:
    void startupController();
    void shutdownController();
    /** This method is used to hide the map "under" another opened map. 
     * In fact, should remove the focus, stop plugins, if necessary, etc. */
    void setVisible(boolean visible);
    // end activation methods.
    void doubleClick(MouseEvent e);
    void plainClick(MouseEvent e);

    boolean isBlocked();
    void edit(KeyEvent e, boolean addNew, boolean editLong);
    public interface  MouseWheelEventHandler {
        /** @return true if the event was sucessfully processed and false if the event did not apply.*/
        boolean handleMouseWheelEvent(MouseWheelEvent e);
    }
    void registerMouseWheelEventHandler(MouseWheelEventHandler handler);
    void deRegisterMouseWheelEventHandler(MouseWheelEventHandler handler);
    void mouseWheelMoved(MouseWheelEvent e);
    MindMapNode getSelected();
    
	/**
	 * @return a List of MindMapNode s.
	 */
	List getSelecteds();
	/** @return a LinkedList of MindMapNodes ordered by depth. nodes with greater depth occur first. */
    List getSelectedsByDepth();
    /** nodes with greater depth occur first.
	 * @param inPlaceList the given list is sorted by reference.
	 */
    public void sortNodesByDepth(List inPlaceList) ;
	    /** This extends the currently selected nodes. 
        @return true, if the method changed the selection.*/
    boolean extendSelection(MouseEvent e);

	/** Use this method to get menus to the screen. */
	public void updateMenus(StructuredMenuHolder holder);
	public void updatePopupMenu(StructuredMenuHolder holder);

    JPopupMenu getPopupMenu();
    void showPopupMenu(MouseEvent e);
    /** This returns a context menu for an object placed in the background pane.*/
    JPopupMenu getPopupForModel(java.lang.Object obj);

	/**
	  * Invoke this method after you've changed how a node is to be
	  * represented in the tree. 
	  */
    void nodeChanged(MindMapNode n);
    void anotherNodeSelected(MindMapNode n);
    // node identifier (fc, 2.5.2004):
	/** Given a node identifier, this method returns the corresponding node. */
	NodeAdapter getNodeFromID(String nodeID);
	/** Calling this method the map-unique identifier of the node is returned 
	 * (and created before, if not present)*/
	String getNodeID(MindMapNode selected);

	//hooks, fc 28.2.2004:
	/** 
	 * This is the only way to instanciate new Hooks. THEY HAVE TO BE INVOKED AFTERWARDS!
	 * The hook is equipped with the map and controller information.
	 * Furthermore, the hook is added to the node, if it is an instance of 
	 * the PermanentNodeHook.
	 * If the hook policy specifies, that only one instance may exist per node,
	 * it returns this instance if it already exists.
	 * @param map may be null if not known. But it has to be set afterwards!
	 * */
	NodeHook createNodeHook(String hookName, MindMapNode node, MindMap map);
	void invokeHook(ModeControllerHook hook);

	void invokeHooksRecursively(NodeAdapter node, MindMap map);
	//end hooks
	FreeMindMain getFrame();
	MapView getView(); 
	MapAdapter getMap();
	Controller getController();
	Clipboard getClipboard();
	ActionFactory getActionFactory();
	ObjectFactory getActionXmlFactory();
	Color getSelectionColor();
    /**
     * Get text from resource file
     */
    String getText(String textId);

	// XML Actions:
	public String marshall(XmlAction action);	
	public XmlAction unMarshall(String inputString);
	
	/** undo in progress? */
	boolean isUndoAction();
    void mapChanged(MindMap newMap);
    
    AttributeController getAttributeController();
}
