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
/*$Id: ModeController.java,v 1.11 2003-11-03 10:49:17 sviles Exp $*/

package freemind.modes;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPopupMenu;

import freemind.main.XMLParseException;
import freemind.view.mindmapview.NodeView;

public interface ModeController {

    public void load(File file) throws FileNotFoundException, IOException, XMLParseException;
    public boolean save(File file);
    public void addNew(NodeView target, int newNodeMode, KeyEvent e);
    public void newMap();
    public boolean save();
    public boolean saveAs();
    public void open();
    //    public void edit(NodeView node, NodeView toBeSelected);
    public boolean close();
    public void doubleClick(MouseEvent e);
    public void plainClick(MouseEvent e);
    public void toggleFolded();

    public boolean isBlocked();
    public void edit(KeyEvent e, boolean addNew, boolean editLong);
    public void mouseWheelMoved(MouseWheelEvent e);
    public void select(MouseEvent e);

    public JPopupMenu getPopupMenu();
    public void showPopupMenu(MouseEvent e);
}
