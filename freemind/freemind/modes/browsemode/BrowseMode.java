/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: BrowseMode.java,v 1.1 2001-03-13 16:01:42 ponder Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMind;
import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu;

public class BrowseMode implements Mode {

    private Controller c;
    private BrowseController modecontroller;
    private final String MODENAME = "Browse";

    public BrowseMode() {
    }

    public void init(Controller c) {
	this.c = c;
	modecontroller = new BrowseController(this);
    }

    public String toString() {
	return MODENAME;
    }

    /**
     * Called whenever this mode is chosen in the program.
     * (updates Actions etc.)
     */
    public void activate() {
	//	getController().changeToMapOfMode(this);
    }
    
    public Controller getController() {
	return c;
    }


    public ModeController getModeController() {
	return modecontroller;
    }

    public BrowseController getBrowseController() {
	return (BrowseController)getModeController();
    }

    public JToolBar getModeToolBar() {
	return ((BrowseController)getModeController()).getToolBar();
    }

    public JMenu getModeFileMenu() {
	return ((BrowseController)getModeController()).getFileMenu();
    }

    public JMenu getModeEditMenu() {
	return ((BrowseController)getModeController()).getEditMenu();
    }

    public JPopupMenu getPopupMenu() {
	return ((BrowseController)getModeController()).getPopupMenu();
    }
}
