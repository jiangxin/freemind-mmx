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
/*$Id: MindMapMode.java,v 1.6 2000-10-23 21:38:17 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

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
    public void activate() {
	getController().changeToMapOfMode(this);
// 	menu.add(getMindMapController().newMap);
// 	menu.add(getMindMapController().open);
// 	menu.add(getMindMapController().save);
// 	menu.add(getMindMapController().saveAs);
// 	JMenuItem edit = menu.add(getMindMapController().edit);
//  	edit.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_edit")));
// 	JMenuItem addNew = menu.add(getMindMapController().addNew);
// 	addNew.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_add")));
//  	JMenuItem remove = menu.add(getMindMapController().remove);
//  	remove.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_remove")));

	getMindMapController().cut.setEnabled(true);
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

    public JMenu getModeFileMenu() {
	return null;
    }

    public JMenu getModeEditMenu() {
	return null;
    }

    public JPopupMenu getPopupMenu() {
	return popupmenu;
    }
}
