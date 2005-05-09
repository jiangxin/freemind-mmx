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
/*$Id: FileMode.java,v 1.14.18.1.6.1 2005-05-09 23:45:46 dpolivaev Exp $*/

package freemind.modes.filemode;

import java.awt.Component;

import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.controller.Controller;
import freemind.controller.StructuredMenuHolder;

import javax.swing.JMenu;
import javax.swing.JToolBar;

public class FileMode implements Mode {

    private Controller c;
    private FileController modecontroller;
    private JToolBar toolbar;
    private static final String MODENAME = "File";
    private static boolean isRunning = false;

    public FileMode() {
    }

    public void init(Controller c) {
	this.c = c;
	modecontroller = new FileController(this);
	toolbar = new FileToolBar(modecontroller);
    }
    

    public String toString() {
	return MODENAME;
    }

    /**
     * Called whenever this mode is chosen in the program.
     * (updates Actions etc.)
     */
    public void activate() {
        if (!isRunning) {
            getModeController().newMap();
            isRunning = true;
        } else {
            c.getMapModuleManager().changeToMapOfMode(this);
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

    public JMenu getModeFileMenu() {
	return null;
    }

    public JToolBar getModeToolBar() {
	return toolbar;
    }

    public Component getLeftToolBar() {
	return null;
    }

}
