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
/*$Id: BrowseToolBar.java,v 1.1 2001-03-13 16:01:42 ponder Exp $*/

package freemind.modes.browsemode;

import freemind.main.Tools;
import freemind.controller.Controller;
import java.lang.Integer;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;

public class BrowseToolBar extends JToolBar {

    private BrowseController c;
    JTextField urlfield = new JTextField();

    public BrowseToolBar(BrowseController controller) {
	
	this.c=controller;

	urlfield.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    c.loadURL(urlfield.getText());		    
		}
	    });


	add(new JLabel("URL:"));
	add(urlfield);
	String map = c.getFrame().getProperty("browsemode_initial_map");
	if (map != null  && map != "") {
	    urlfield.setText(map);
	}

    }
}
