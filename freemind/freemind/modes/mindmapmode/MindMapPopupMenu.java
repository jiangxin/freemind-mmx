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
/*$Id: MindMapPopupMenu.java,v 1.4 2000-10-17 17:20:28 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MindMapPopupMenu extends JPopupMenu {

    private MindMapController c;

    public MindMapPopupMenu(MindMapController c) {
	this.c = c;

	//Node menu
	JMenu nodeMenu = new JMenu(FreeMind.getResources().getString("node"));
	this.add(nodeMenu);

	JMenuItem edit = nodeMenu.add(c.edit);
 	edit.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_edit")));
 	JMenuItem addNew = nodeMenu.add(c.addNew);
 	addNew.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_add")));
 	JMenuItem remove = nodeMenu.add(c.remove);
 	remove.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_remove")));

	nodeMenu.add(c.followLink);
	nodeMenu.add(c.setLink);
	
	JMenu nodeStyle = new JMenu(FreeMind.getResources().getString("style"));
	nodeMenu.add(nodeStyle);

	nodeStyle.add(c.fork);

	nodeStyle.add(c.bubble);

	JMenu nodeFont = new JMenu(FreeMind.getResources().getString("font"));
	nodeMenu.add(nodeFont);

	nodeFont.add(c.italic);

	nodeFont.add(c.bold);

	//	nodeFont.add(c.underline);

	nodeMenu.add(c.nodeColor);

	//Edge menu
	JMenu edgeMenu = new JMenu(FreeMind.getResources().getString("edge"));
	this.add(edgeMenu);

	JMenu edgeStyle = new JMenu(FreeMind.getResources().getString("style"));
	edgeMenu.add(edgeStyle);
	
	edgeStyle.add(c.linear);

	edgeStyle.add(c.bezier);

	edgeMenu.add(c.edgeColor);
    }
}

			   
			   
			   
