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
/*$Id: BrowseController.java,v 1.13.12.2 2004-03-11 06:28:41 christianfoltin Exp $*/

package freemind.modes.browsemode;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import java.util.HashSet;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.view.mindmapview.NodeView;

public class BrowseController extends ControllerAdapter {

    private JPopupMenu popupmenu;
    private JToolBar toolbar;

    Action toggleFolded;
    Action toggleChildrenFolded;
    Action find;
    Action findNext;
    Action followLink;
    Action nodeUp;
    Action nodeDown;

    // disable edit in browse mode (PN)
    public void edit(KeyEvent e, boolean addNew, boolean editLong) { }
    public void addNew(final NodeView target, 
                        final int newNodeMode, 
                        final KeyEvent e) { }

    public BrowseController(Mode mode) {
	super.setMode(mode);

        // Daniel: Actions are initialized here and not above because of
        // some error it would produce. Not studied in more detail.
        toggleFolded = new ToggleFoldedAction();
        toggleChildrenFolded = new ToggleChildrenFoldedAction();
        find = new FindAction();
        findNext = new FindNextAction();
        followLink = new FollowLinkAction();
        nodeUp = new NodeUpAction();
        nodeDown = new NodeDownAction();

	popupmenu = new BrowsePopupMenu(this);
        toolbar = new BrowseToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new BrowseMapModel(getFrame());
    }

    public void doubleClick() {
         if (getSelected().getLink() == null) { // If link exists, follow the link; toggle folded otherwise
             toggleFolded();
         } else { 
	        loadURL();
        }
    }

    public MindMapNode newNode() {
	return new BrowseNodeModel(getText("new_node"), getFrame());
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getPopupMenu());
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	return fileMenu;
    }

    public JPopupMenu getPopupMenu() {
	return popupmenu;
    }

    /** Link implementation: If this is a link, we want to make a popup with at least removelink available.*/
    public JPopupMenu getPopupForModel(java.lang.Object obj) {
        if( obj instanceof BrowseArrowLinkModel) {
            // yes, this is a link.
            BrowseArrowLinkModel link = (BrowseArrowLinkModel) obj;
            JPopupMenu arrowLinkPopup = new JPopupMenu();

            arrowLinkPopup.add(new GotoLinkNodeAction(link.getSource().toString(), link.getSource())); 
            arrowLinkPopup.add(new GotoLinkNodeAction(link.getTarget().toString(), link.getTarget())); 

            arrowLinkPopup.addSeparator();
            // add all links from target and from source:
            HashSet NodeAlreadyVisited = new HashSet();
            NodeAlreadyVisited.add(link.getSource());
            NodeAlreadyVisited.add(link.getTarget());
            Vector links = getModel().getLinkRegistry().getAllLinks(link.getSource());
            links.addAll(getModel().getLinkRegistry().getAllLinks(link.getTarget()));
            for(int i = 0; i < links.size(); ++i) {
                BrowseArrowLinkModel foreign_link = (BrowseArrowLinkModel) links.get(i);
                if(NodeAlreadyVisited.add(foreign_link.getTarget())) {
                    arrowLinkPopup.add(new GotoLinkNodeAction(foreign_link.getTarget().toString(), foreign_link.getTarget())); 
                }
                if(NodeAlreadyVisited.add(foreign_link.getSource())) {
                    arrowLinkPopup.add(new GotoLinkNodeAction(foreign_link.getSource().toString(), foreign_link.getSource())); 
                }
            }
            return arrowLinkPopup;
        }
        return null;
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
            getController().errorMessage(getText("url_error") + " " + ex.getMessage());
            //getFrame().err(getText("url_error"));
            return;
        }

        String type = Tools.getExtension(absolute.getFile());
        try {
            if (type.equals("mm")) {
                getFrame().setWaitingCursor(true);
                load(absolute);
            } else {
                getFrame().openDocument(absolute);
            }
        } catch (Exception ex) {
            getController().errorMessage(getText("url_load_error")+absolute);
            ex.printStackTrace();
            //for some reason, this exception is thrown anytime...
        } finally {
            getFrame().setWaitingCursor(false);
        }
        
    }

    public void loadURL() {
	String link = getSelected().getLink();
	if (link != null) {
	    loadURL(link);
	}
    }

    private void load(URL url) throws Exception {
	getToolBar().setURLField(url.toString());
    	BrowseMapModel model = (BrowseMapModel)newModel();
	model.load(url);
	newMap(model);
	mapOpened(true);
	//URGENT: Must activate hooks???
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
	    super(getText("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }
}
