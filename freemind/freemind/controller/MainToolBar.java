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
/*$Id: MainToolBar.java,v 1.5 2000-10-27 21:44:35 ponder Exp $*/

package freemind.controller;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

public class MainToolBar extends JToolBar {
    private static final String[] zooms = {"50%","75%","100%","125%","150%"};
    JComboBox zoom;	    

    public MainToolBar(final Controller c) {
	JButton button;

	button = add(c.previousMap);
	button.setText("");
	button = add(c.nextMap);
	button.setText("");

	zoom = new JComboBox(zooms);
	zoom.setSelectedItem("100%");
	add(zoom);
	zoom.addItemListener(new ItemListener(){
		public void itemStateChanged(ItemEvent e) {
		    //remove '%' sign
		    String dirty = (String)e.getItem();
		    String cleaned = dirty.substring(0,dirty.length()-1);
		    //change representation ("125" to 1.25)
		    c.setZoom(Integer.parseInt(cleaned,10)/100F);
		}
	    });

    }

    public void setAllActions(boolean enabled) {
	zoom.setEnabled(enabled);
    }
}
