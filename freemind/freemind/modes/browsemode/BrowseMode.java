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
/*$Id: BrowseMode.java,v 1.8 2003-12-02 22:50:22 christianfoltin Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMindApplet;
import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import java.net.URL;

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

        String map = getController().getFrame().getProperty("browsemode_initial_map");
        if (map != null && map.startsWith("."))  {
            /* new handling for relative urls. fc, 29.10.2003.*/
            try {
                if(getController().getFrame() instanceof FreeMindApplet) {
                    FreeMindApplet applet = (FreeMindApplet) getController().getFrame();
                    URL documentBaseUrl = new URL( applet.getDocumentBase(), map);
                    map = documentBaseUrl.toString();
                } else {
                    map = "file:"+System.getProperty("user.dir") + map.substring(1);//remove "." and make url
                }
            }  catch (java.net.MalformedURLException e) { 
                getController().errorMessage("Could not open relative URL "+map+". It is malformed.");
                System.err.println(e);
                return;
            }
            /* end: new handling for relative urls. fc, 29.10.2003.*/
        }    
        if (map != "") {
            ((BrowseController)getModeController()).loadURL(map);
        }
    }

    public void restore(String restoreable) {
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

    public JToolBar getLeftToolBar() {
        return null;
    }

    public JMenu getModeFileMenu() {
        return ((BrowseController)getModeController()).getFileMenu();
    }

    public JMenu getModeEditMenu() {
        return ((BrowseController)getModeController()).getEditMenu();
    }
}
