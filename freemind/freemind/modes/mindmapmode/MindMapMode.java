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

package freemind.modes.mindmapmode;

import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MapView;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu;

public class MindMapMode implements Mode {

    private Controller c;
    private MindMapController modecontroller;
    private JToolBar toolbar;
    private JPopupMenu popupmenu;
    private final String MODENAME = "MindMap";

    public MindMapMode(Controller c) {
	this.c = c;
	modecontroller = new MindMapController(this);
	toolbar = new MindMapToolBar(modecontroller);
	popupmenu = new MindMapPopupMenu(modecontroller);
    }

    public String toString() {
	return MODENAME;
    }

    /**
     * Called whenever this mode is chosen in the program.
     * (updates Actions etc.)
     */
    public void activate(JMenu menu) {
	menu.add(getMindMapController().newMap);
	menu.add(getMindMapController().open);
	menu.add(getMindMapController().save);
	menu.add(getMindMapController().saveAs);
    }
    
    public Controller getController() {
	return c;
    }

    public ModeController getModeController() {
	return modecontroller;
    }

    public MindMapController getMindMapController() {
	return (MindMapController)getModeController();
    }

    public JToolBar getModeToolBar() {
	return toolbar;
    }

    public JPopupMenu getPopupMenu() {
	return popupmenu;
    }
}
