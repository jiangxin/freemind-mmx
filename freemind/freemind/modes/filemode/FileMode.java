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

package freemind.modes.filemode;

import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MapView;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu;

public class FileMode implements Mode {

    private Controller c;
    private FileController modecontroller;
    private JToolBar toolbar;
    private JPopupMenu popupmenu;
    private final String MODENAME = "File";

    public FileMode(Controller c) {
	this.c = c;
	modecontroller = new FileController(this);
	toolbar = new FileToolBar(modecontroller);
	popupmenu = new FilePopupMenu(modecontroller);
    }

    public String toString() {
	return MODENAME;
    }

    /**
     * Called whenever this mode is chosen in the program.
     * (updates Actions etc.)
     */
    public void activate(JMenu menu) {
	//	menu.add(new JMenuItem("test"));
    }
    
    public Controller getController() {
	return c;
    }

    public ModeController getModeController() {
	return modecontroller;
    }

    public JToolBar getModeToolBar() {
	return toolbar;
    }

    public JPopupMenu getPopupMenu() {
	return popupmenu;
    }
}
