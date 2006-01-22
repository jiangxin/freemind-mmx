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
/*$Id: BrowseController.java,v 1.13.18.4.2.1.2.2 2006-01-22 12:24:38 dpolivaev Exp $*/

package freemind.modes.browsemode;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.actions.GotoLinkNodeAction;

public class BrowseController extends ControllerAdapter {

    private JPopupMenu popupmenu;
    private JToolBar toolbar;

    Action followLink;
    Action nodeUp;
    Action nodeDown;

    // disable edit in browse mode (PN)
    public void edit(KeyEvent e, boolean addNew, boolean editLong) { }
    public MindMapNode addNew(final MindMapNode target, 
                        final int newNodeMode, 
                        final KeyEvent e) { return null;}

    public BrowseController(Mode mode) {
    	super(mode);

        // Daniel: Actions are initialized here and not above because of
        // some error it would produce. Not studied in more detail.
        followLink = new FollowLinkAction();

	popupmenu = new BrowsePopupMenu(this);
        toolbar = new BrowseToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new BrowseMapModel(getFrame(), this);
    }

    public void doubleClick() {
         if (getSelected().getLink() == null) { // If link exists, follow the link; toggle folded otherwise
             toggleFolded.toggleFolded();
         } else { 
	        loadURL();
        }
    }

    public MindMapNode newNode(Object userObject, MindMap map) {
    	return new BrowseNodeModel(userObject, getFrame(), map);
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

            arrowLinkPopup.add(getGotoLinkNodeAction(link.getSource())); 
            arrowLinkPopup.add(getGotoLinkNodeAction(link.getTarget())); 

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
                    arrowLinkPopup.add(getGotoLinkNodeAction(foreign_link.getTarget())); 
                }
                if(NodeAlreadyVisited.add(foreign_link.getSource())) {
                    arrowLinkPopup.add(getGotoLinkNodeAction(foreign_link.getSource())); 
                }
            }
            return arrowLinkPopup;
        }
        return null;
    }



    /**
     * @param destination
     * @return
     */
    private GotoLinkNodeAction getGotoLinkNodeAction(MindMapNode destination) {
        return new GotoLinkNodeAction(this, destination);
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
	//FIXME: Must activate hooks???
    }




    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
        super.setAllActions(enabled);
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
    /* (non-Javadoc)
     * @see freemind.modes.ModeController#updateMenus(freemind.controller.StructuredMenuHolder)
     */
    public void updateMenus(StructuredMenuHolder holder) {
		add(holder, MenuBar.EDIT_MENU+"/find/find", find, "keystroke_find");
		add(holder, MenuBar.EDIT_MENU+"/find/findNext", findNext, "keystroke_find_next");
		add(holder, MenuBar.EDIT_MENU+"/find/followLink", followLink, "keystroke_follow_link");
		holder.addSeparator(MenuBar.EDIT_MENU+"/find");
		add(holder, MenuBar.EDIT_MENU+"/find/toggleFolded", toggleFolded, "keystroke_toggle_folded");
		add(holder, MenuBar.EDIT_MENU+"/find/toggleChildrenFolded", toggleChildrenFolded, "keystroke_toggle_children_folded");
    }
}
