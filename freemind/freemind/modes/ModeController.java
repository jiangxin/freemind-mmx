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
/*$Id: ModeController.java,v 1.10 2003-11-03 10:39:51 sviles Exp $*/

package freemind.modes;

import freemind.view.mindmapview.NodeView;
import freemind.main.XMLParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.MouseEvent;

public interface ModeController {
    public void load(File file) throws FileNotFoundException, IOException, XMLParseException;
    public void save(File file);
    public void addNew(NodeView parent);
    public void remove(NodeView node);
    public void newMap();
    public void save();
    public void saveAs();
    public void open();
    //    public void edit(NodeView node, NodeView toBeSelected);
    public void close() throws Exception;
    public void doubleClick(MouseEvent e);
    public void plainClick(MouseEvent e);
}
