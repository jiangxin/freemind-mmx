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
/*$Id: MindMapToolBar.java,v 1.5 2000-12-05 17:32:56 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.Tools;
import freemind.controller.Controller;
import java.lang.Integer;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;

public class MindMapToolBar extends JToolBar {

    private static final String[] sizes = {"8","10","12","14","16","18","20","24","28"};
    private MindMapController c;
    private JComboBox fonts, size;

    public MindMapToolBar(MindMapController controller) {
	
	this.c=controller;
	JButton button;

	button = add(c.newMap);
	button.setText("");
	button = add(c.open);
	button.setText("");
	button = add(c.save);
	button.setText("");
	button = add(c.saveAs);
	button.setText("");

	button = add(c.cut);
	button.setText("");
	button = add(c.paste);
	button.setText("");

	button = add(c.italic);
	button.setText("");
	button = add(c.bold);
	button.setText("");
	//	button = add(c.underlined);
	//	button.setText("");
	button = add(c.normalFont);
	button.setText("");

	fonts = new JComboBox(Tools.getAllFonts());
	fonts.setMaximumRowCount(9);
	add(fonts);
	fonts.addItemListener(new ItemListener(){
		public void itemStateChanged(ItemEvent e) {
		    // ** this is super-dirty, why doesn't the toolbar know the model?
		    c.setFont((String)e.getItem());
		}
	    });

	size = new JComboBox(sizes);
	add(size);
	size.addItemListener(new ItemListener(){
		public void itemStateChanged(ItemEvent e) {
		    // ** change the font size

		    // ** this is super-dirty, why doesn't the toolbar know the model?
		    c.setFontSize(Integer.parseInt((String)e.getItem(),10));
//  		    c.setFont(c.getFont()
//  			      .deriveFont((float)Integer.parseInt((String)e.getItem(),10)));
		}
	    });

    }

    void setAllActions(boolean enabled) {
	fonts.setEnabled(enabled);
	size.setEnabled(enabled);
    }
}
