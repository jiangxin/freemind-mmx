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
/*$Id: MindMapPopupMenu.java,v 1.11 2003-11-03 10:49:18 sviles Exp $*/

package freemind.modes.mindmapmode;

import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.KeyStroke;
import java.awt.Component;


public class MindMapPopupMenu extends JPopupMenu {

    private MindMapController c;

    public MindMapPopupMenu(MindMapController c) {
       	this.c = c;
        JMenu leading = c.getLeadingNodeMenu();
        Component[] mc = leading.getMenuComponents();
        for (int i = 0; i < mc.length; i++) {
           this.add(mc[i]); }

        this.addSeparator();
        
       	this.add(c.getNodeMenu());
       	this.add(c.getBranchMenu());
       	this.add(c.getEdgeMenu());
       	this.add(c.getExtensionMenu());
    }
}
