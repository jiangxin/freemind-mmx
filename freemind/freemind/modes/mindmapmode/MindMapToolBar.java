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
/*$Id: MindMapToolBar.java,v 1.12.12.7 2004-05-26 06:01:20 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import freemind.controller.FreeMindToolBar;
import freemind.controller.StructuredMenuHolder;
import freemind.main.Tools;


public class MindMapToolBar extends FreeMindToolBar {

    private static final String[] sizes = {"8","10","12","14","16","18","20","24","28"};
    private MindMapController c;
    private JComboBox fonts, size;
    private JToolBar buttonToolBar;    
    private boolean fontSize_IgnoreChangeEvent = false;
    private boolean fontFamily_IgnoreChangeEvent = false;

    public MindMapToolBar(MindMapController controller) {
		super();
		this.c=controller;
        this.setRollover(true);
		fonts = new JComboBox(Tools.getAvailableFontFamilyNamesAsVector());
		size = new JComboBox(sizes);
		buttonToolBar = new FreeMindToolBar();
    }
    
    public void update(StructuredMenuHolder holder) {
		this.removeAll();
		holder.updateMenus(this, "mindmapmode_toolbar/");




	fonts.setMaximumRowCount(9);
	add(fonts);
	fonts.addItemListener(new ItemListener(){
              public void itemStateChanged(ItemEvent e) {
                 if (e.getStateChange() != ItemEvent.SELECTED) {
                    return; }
                 // TODO: this is super-dirty, why doesn't the toolbar know the model?
                 if (fontFamily_IgnoreChangeEvent) {
                    fontFamily_IgnoreChangeEvent = false;
                    return; }
                 
                 c.setFontFamily((String)e.getItem());
              }
           });

	size.setEditor(new BasicComboBoxEditor());
	size.setEditable(true);
	add(size);
	size.addItemListener(new ItemListener(){
              public void itemStateChanged(ItemEvent e) {
                 //System.err.println("ce:"+e);
                 if (e.getStateChange() != ItemEvent.SELECTED) {
                    return; }
                 // change the font size                 
                 // TODO: this is super-dirty, why doesn't the toolbar know the model?
                 if (fontSize_IgnoreChangeEvent) {
                    fontSize_IgnoreChangeEvent = false;
                    return; }
                 try {
		    c.setFontSize(Integer.parseInt((String)e.getItem(),10));
                 }
                 catch (NumberFormatException nfe) {
                 }
                 //  		    c.setFont(c.getFont()
                 //  			      .deriveFont((float)Integer.parseInt((String)e.getItem(),10)));
              }
           });
        
        // button tool bar.
        buttonToolBar.removeAll();
        buttonToolBar.setRollover(true);
        buttonToolBar.add(c.removeLastIcon);
        buttonToolBar.add(c.removeAllIcons);
        buttonToolBar.addSeparator();
        for(int i = 0; i < c.iconActions.size(); ++i) {
            buttonToolBar.add((Action) c.iconActions.get(i));
        }
        buttonToolBar.setOrientation(JToolBar.VERTICAL);
        //c.getFrame().getContentPane().add( buttonToolBar, BorderLayout.WEST );
   }

   // Daniel Polansky: both the following methods trigger item listeners above.
   // Those listeners obtain two events: first DESELECTED and then
   // SELECTED. Both events are to be ignored - we don't want to update
   // a node with its own font. The item listeners should react only
   // to a user change, not to our change.

   public void selectFontSize(String fontSize) // (DiPo)
   {
      fontSize_IgnoreChangeEvent = true;
      size.setSelectedItem(fontSize);
      fontSize_IgnoreChangeEvent = false;
   }

   JToolBar getLeftToolBar() {
       return buttonToolBar;
   }
   
   public void selectFontName(String fontName) // (DiPo)
   {
      fontFamily_IgnoreChangeEvent = true;
      fonts.setEditable(true);
      fonts.setSelectedItem(fontName) ;
      fonts.setEditable(false);
      fontFamily_IgnoreChangeEvent = false;
   }
    
    void setAllActions(boolean enabled) {
	fonts.setEnabled(enabled);
	size.setEnabled(enabled);
    }
}
