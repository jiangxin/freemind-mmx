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
/*$Id: BrowseController.java,v 1.3 2001-03-28 19:17:37 ponder Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import java.io.File;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

public class BrowseController extends ControllerAdapter {

    private JPopupMenu popupmenu;
    private JToolBar toolbar;

    Action toggleFolded;
    Action toggleChildrenFolded;
    Action followLink;
    Action nodeUp;
    Action nodeDown;

    public BrowseController(Mode mode) {
	super.setMode(mode);
	toggleFolded  = new ToggleFoldedAction();
	toggleChildrenFolded  = new toggleChildrenFoldedAction();
	followLink  = new FollowLinkAction();
	nodeUp  = new NodeUpAction();
	nodeDown  = new NodeDownAction();
	popupmenu = new BrowsePopupMenu(this);
	toolbar = new BrowseToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new BrowseMapModel(getFrame());
    }

    public void save(File file) {
	getModel().save(file);
    }

    public void doubleClick() {
	if (getFrame().getProperty("mindmap_doubleclick").equals("toggle_folded")) {
	    toggleFolded();
	} else {
	    loadURL();
	}
    }

    protected MindMapNode newNode() {
	return new BrowseNodeModel(getFrame().getResources().getString("new_node"),getFrame());
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getNodeMenu());
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	return fileMenu;
    }


    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(getFrame().getResources().getString("node"));
	JMenuItem followLinkItem = nodeMenu.add(followLink);
 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));

	JMenuItem toggleFoldedItem = nodeMenu.add(toggleFolded);
 	toggleFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_folded")));
	JMenuItem toggleChildrenFoldedItem = nodeMenu.add(toggleChildrenFolded);
	toggleChildrenFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_children_folded")));

	return nodeMenu;
    }

    JPopupMenu getPopupMenu() {
	return popupmenu;
    }

    //convenience methods
    private BrowseMapModel getModel() {
	return (BrowseMapModel)getController().getModel();
    }

    private BrowseNodeModel getSelected() {
	return (BrowseNodeModel)getView().getSelected().getModel();
    }

    BrowseToolBar getToolBar() {
	return (BrowseToolBar)toolbar;
    }

    public void loadURL(String relative) {
	URL absolute = null;
	try {
	    BrowseMapModel map = (BrowseMapModel)getMap();
	    if (map != null) {
		absolute = new URL( map.getURL(), relative);	
	    } else {
		absolute = new URL( relative );
	    }
	    //	    absolute = new URL(relative);
	    getFrame().out(absolute.toString());
	} catch (MalformedURLException ex) {
	    getFrame().err(getFrame().getResources().getString("url_error"));
	    return;
	}

	//try {
	    //	    String fileName = absolute.getFile();
	    // File file = new File(fileName);
	    //if(!getController().tryToChangeToMapModule(file.getName())) {//this can lead to confusion if the user handles multiple maps with the same name.
	    //	load(file);
	    //}
	    //	} catch (FileNotFoundException e) {
	    //int returnVal = JOptionPane.showConfirmDialog(getView(), getFrame().getResources().getString("repair_link_question"), getFrame().getResources().getString("repair_link"),JOptionPane.YES_NO_OPTION);
	    //if (returnVal==JOptionPane.YES_OPTION) {
	    //	setLink();
	    //} 
	//}
	String type = Tools.getExtension(absolute.getFile());
	try {
	    if (type.equals("mm")) {
		load(absolute);
	    } else {
		getFrame().openDocument(absolute);
	    }
	} catch (Exception ex) {
	    //	    getFrame().err(getFrame().getResources().getString("url_load_error")+absolute);
	    //for some reason, this exception is thrown anytime...
	} 
    }

    protected void loadURL() {
	String link = getSelected().getLink();
	if (link != null) {
	    loadURL(link);
	}
    }

    private void load(URL url) throws Exception {
	getToolBar().setURLField(url.toString());
    	BrowseMapModel model = (BrowseMapModel)newModel();
	model.load(url);
	getController().newMapModule(model);
	mapOpened(true);
    }




    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
	toggleFolded.setEnabled(enabled);
	toggleChildrenFolded.setEnabled(enabled);
	followLink.setEnabled(enabled);
    }

    //////////
    // Actions
    /////////

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(getFrame().getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }
}
