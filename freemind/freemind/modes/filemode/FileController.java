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
/*$Id: FileController.java,v 1.11.12.1 2004-03-04 20:26:19 christianfoltin Exp $*/

package freemind.modes.filemode;

import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ControllerAdapter;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class FileController extends ControllerAdapter {

    Action find = new FindAction();
    Action findNext = new FindNextAction();

    Action newMap = new NewMapAction(this);
    Action center = new CenterAction();
    Action openPath = new OpenPathAction();

    private JPopupMenu popupmenu = new FilePopupMenu(this);


    public FileController(Mode mode) {
	super(mode);
    }

    public MapAdapter newModel() {
	return new FileMapModel(getFrame());
    }

    public MindMapNode newNode() {
	File newNode = new File(((FileNodeModel)getSelected()).getFile(),"new_Directory");
	newNode.mkdir();
	return new FileNodeModel(newNode,getFrame());
    }

    public JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
        add(editMenu, find, "keystroke_find");
        add(editMenu, findNext, "keystroke_find_next"); 
        add(editMenu, openPath);
        return editMenu; }

    public JPopupMenu getPopupMenu() {
      return this.popupmenu;
    }
    //-----------------------------------------------------------------------------------

    // Private
    // 

//    private MindMap getModel() {
// 	return (MindMap)getController().getModel();
//    }

    private MindMapNode getSelected() {
	if (getView() != null) {
	    return (MindMapNode)getView().getSelected().getModel();
	} else {
	    return null;
	}
    }
    
    private class CenterAction extends AbstractAction {
	CenterAction() {
	    super(getController().getResourceString("center"));
	}
	public void actionPerformed(ActionEvent e) {
	    if (getSelected() != null) {
		MindMap map = new FileMapModel(((FileNodeModel)getSelected()).getFile(), getFrame());
		newMap(map);
	    }
	}
    }

    private class OpenPathAction extends AbstractAction {
	OpenPathAction() {
	    super(getController().getResourceString("open"));
	}
	public void actionPerformed(ActionEvent e) {
           String inputValue = JOptionPane.showInputDialog
              (getText("open"), "");
           if (inputValue != null) {
              File newCenter = new File(inputValue);
              if (newCenter.exists()) { // and is a folder
		MindMap map = new FileMapModel(newCenter, getFrame());
		newMap(map);
              }
           }
        }
    }

}
