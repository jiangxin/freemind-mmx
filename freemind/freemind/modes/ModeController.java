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
/*$Id: ModeController.java,v 1.14.10.5 2004-04-08 18:54:56 christianfoltin Exp $*/

package freemind.modes;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JPopupMenu;

import freemind.main.FreeMindMain;
import freemind.main.XMLParseException;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.controller.Controller;
import freemind.extensions.ModeControllerHook;

public interface ModeController {

    void load(File file) throws FileNotFoundException, IOException, XMLParseException;
    boolean save(File file);
    void addNew(NodeView target, int newNodeMode, KeyEvent e);
	MindMapNode newNode();
    void newMap();
    boolean save();
    boolean saveAs();
    void open();
    //    void edit(NodeView node, NodeView toBeSelected);
    boolean close();
    /** This method is used to hide the map "under" another opened map. 
     * In fact, should remove the focus, stop plugins, if necessary, etc. */
    void setVisible(boolean visible);
    void doubleClick(MouseEvent e);
    void plainClick(MouseEvent e);
    void toggleFolded();

    boolean isBlocked();
    void edit(KeyEvent e, boolean addNew, boolean editLong);
    void mouseWheelMoved(MouseWheelEvent e);
	List getSelecteds();
	    /** This extends the currently selected nodes. 
        @return true, if the method changed the selection.*/
    boolean extendSelection(MouseEvent e);

    JPopupMenu getPopupMenu();
    void showPopupMenu(MouseEvent e);
    /** This returns a context menu for an object placed in the background pane.*/
    JPopupMenu getPopupForModel(java.lang.Object obj);

    void nodeChanged(MindMapNode n);
    void anotherNodeSelected(MindMapNode n);
	//hooks, fc 28.2.2004:
	void invokeHook(ModeControllerHook hook);
	void invokeHooksRecursively(NodeAdapter node, MindMap map);
	//end hooks
	FreeMindMain getFrame();
	MapView getView(); 
	MapAdapter getMap();
	Controller getController();

}
