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
/*$Id: MindMapToolBar.java,v 1.12.18.1.12.5 2009/07/04 20:38:27 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import freemind.controller.FreeMindToolBar;
import freemind.controller.StructuredMenuHolder;
import freemind.main.Tools;


public class MindMapToolBar extends FreeMindToolBar {

    private static final String[] sizes = {"8","10","12","14","16","18","20","24","28"};
    private MindMapController c;
    private JComboBox fonts, size;
    private JAutoScrollBarPane iconToolBarScrollPane;    
    private JToolBar iconToolBar;    
    private boolean fontSize_IgnoreChangeEvent = false;
    private boolean fontFamily_IgnoreChangeEvent = false;
    private ItemListener fontsListener;
    private ItemListener sizeListener;

    public MindMapToolBar(MindMapController controller) {
		super();
		this.c=controller;
        this.setRollover(true);
		fonts = new JComboBox(Tools.getAvailableFontFamilyNamesAsVector());
		fonts.setFocusable(false);
		size = new JComboBox(sizes);
		size.setFocusable(false);
		iconToolBar = new FreeMindToolBar();
		iconToolBarScrollPane = new JAutoScrollBarPane(iconToolBar);
        iconToolBar.setOrientation(JToolBar.VERTICAL);
        iconToolBar.setRollover(true);
        iconToolBarScrollPane.getVerticalScrollBar().setUnitIncrement(100);
		fontsListener = new ItemListener(){
        	        public void itemStateChanged(ItemEvent e) {
        	            if (e.getStateChange() != ItemEvent.SELECTED) {
        	               return; }
        	            // TODO: this is super-dirty, why doesn't the toolbar know the model?
        	            if (fontFamily_IgnoreChangeEvent) {
        	               //fc, 27.8.2004: I don't understand, why the ignore type is resetted here. 
        	                // let's see: fontFamily_IgnoreChangeEvent = false;
        	               return; }
        	            fontFamily_IgnoreChangeEvent = true;
        	            c.fontFamily.actionPerformed((String)e.getItem());
        	            fontFamily_IgnoreChangeEvent = false;
        	         }
        	      };
		fonts.addItemListener(fontsListener);
        sizeListener = new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                //System.err.println("ce:"+e);
                if (e.getStateChange() != ItemEvent.SELECTED) {
                   return; }
                // change the font size                 
                // TODO: this is super-dirty, why doesn't the toolbar know the model?
                if (fontSize_IgnoreChangeEvent) {
                    //fc, 27.8.2004: I don't understand, why the ignore type is resetted here. 
                    // let's see: fontSize_IgnoreChangeEvent = false;
                   return; 
                }
                // call action:
                c.fontSize.actionPerformed((String) e.getItem());
             }
          };
		size.addItemListener(sizeListener);
    }
    
    public void update(StructuredMenuHolder holder) {
		this.removeAll();
		holder.updateMenus(this, "mindmapmode_toolbar/");

		fonts.setMaximumRowCount(9);
		add(fonts);
	
		size.setEditor(new BasicComboBoxEditor());
		size.setEditable(true);
		add(size);
        
        // button tool bar.
        iconToolBar.removeAll();
        iconToolBar.add(c.removeLastIconAction);
        iconToolBar.add(c.removeAllIconsAction);
        iconToolBar.addSeparator();
        for(int i = 0; i < c.iconActions.size(); ++i) {
            iconToolBar.add((Action) c.iconActions.get(i));
        }
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

   Component getLeftToolBar() {
       return iconToolBarScrollPane;
   }
   
   public void selectFontName(String fontName) // (DiPo)
   {
	   if (fontFamily_IgnoreChangeEvent) {
		   return; 
	   }
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
