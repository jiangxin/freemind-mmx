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

import freemind.main.FreeMind;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;

public class MindMapPopupMenu extends JPopupMenu {

    private MindMapController c;

    public MindMapPopupMenu(MindMapController c) {
	this.c = c;

	this.add(c.followLink);
	this.add(c.setLink);
	
	//Node menu
	JMenu nodeMenu = new JMenu(FreeMind.getResources().getString("node"));
	this.add(nodeMenu);

	JMenu nodeStyle = new JMenu(FreeMind.getResources().getString("style"));
	nodeMenu.add(nodeStyle);

	nodeStyle.add(c.fork);

	nodeStyle.add(c.bubble);

	JMenu nodeFont = new JMenu(FreeMind.getResources().getString("font"));
	nodeMenu.add(nodeFont);

	nodeFont.add(c.italic);

	nodeFont.add(c.bold);

	nodeFont.add(c.underline);

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

			   
			   
			   
