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

import freemind.main.FreeMind;
import freemind.controller.Controller;
import freemind.view.MapModule;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.view.mindmapview.MapView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileController extends ControllerAdapter {

    Action newMap = new NewMapAction(this);

    Action center = new CenterAction();

    public FileController(Mode mode) {
	super(mode);
    }

    public MapAdapter newModel() {
	return new FileMapModel();
    }

    public MindMapNode newNode() {
	File newNode = new File(((FileNodeModel)getSelected()).getFile(), "new_Directory");
	newNode.mkdir();
	return new FileNodeModel(newNode);
    }




    //private
    private MindMap getModel() {
 	return (MindMap)getController().getModel();
    }

    private MindMapNode getSelected() {
	if (getView() != null) {
	    return (MindMapNode)getView().getSelected().getModel();
	} else {
	    return null;
	}
    }

    private class CenterAction extends AbstractAction {
	CenterAction() {
	    super(FreeMind.getResources().getString("center"));
	}
	public void actionPerformed(ActionEvent e) {
	    if (getSelected() != null) {
		MindMap map = new FileMapModel(((FileNodeModel)getSelected()).getFile());
		getController().newMapModule(map);
	    }
	}
    }


}
