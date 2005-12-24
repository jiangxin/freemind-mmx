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
/*$Id: MainToolBar.java,v 1.16.14.1.6.1.2.3 2005-12-24 13:45:18 dpolivaev Exp $*/

package freemind.controller;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;

public class MainToolBar extends FreeMindToolBar {
    private JComboBox zoom;	    
    Controller c;
    String userDefinedZoom;
    private static Logger logger= null;
	
    public MainToolBar(final Controller c) {
    	super();
        this.setRollover(true);
        this.c = c;
        if(logger == null) {
            logger = c.getFrame().getLogger(this.getClass().getName());
        }
        userDefinedZoom = c.getResourceString("user_defined_zoom");
	JButton button;

	button = add(c.navigationPreviousMap);
	button = add(c.navigationNextMap);
	button = add(c.printDirect);
	JToggleButton btnFilter = new JToggleButton (c.showFilterToolbarAction);
	btnFilter.setToolTipText(c.getResourceString("filter_toolbar"));
	add(btnFilter);
	button = add(c.showAttributeManagerAction);

        zoom = new JComboBox(c.getZooms());
        zoom.setSelectedItem("100%");
        zoom.addItem(userDefinedZoom);
        add(zoom);
		zoom.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// todo: dialog with user zoom value, if user zoom is chosen.
				// change proposed by dimitri:
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setZoomByItem(e.getItem());
				}
			}
		});
	}

    private void setZoomByItem(Object item) {
        if(((String) item).equals(userDefinedZoom))
            return; // nothing to do...
       //remove '%' sign
      String dirty = (String)item;
      String cleaned = dirty.substring(0,dirty.length()-1);
      //change representation ("125" to 1.25)
      c.setZoom(Integer.parseInt(cleaned,10)/100F); }

    public void zoomOut() {
       if (zoom.getSelectedIndex() > 0) {
          setZoomByItem(zoom.getItemAt(zoom.getSelectedIndex() - 1));
          /*zoom.setSelectedItem(zoom.getItemAt(zoom.getSelectedIndex() - 1));*/ }}

    public void zoomIn() {
       if (zoom.getSelectedIndex() < zoom.getItemCount() - 1) {
          setZoomByItem(zoom.getItemAt(zoom.getSelectedIndex() + 1));
          /*zoom.setSelectedItem(zoom.getItemAt(zoom.getSelectedIndex() + 1));*/ }}

    public String getItemForZoom(float f) {
       return (int)(f*100F)+"%"; }

    public void setZoomComboBox(float f) {
        logger.info("setZoomComboBox is called with "+f+".");
        String toBeFound = getItemForZoom(f);
        for(int i = 0; i < zoom.getItemCount(); ++i) {
            if(toBeFound.equals((String) zoom.getItemAt(i))) {
                // found
                zoom.setSelectedItem(toBeFound);
                return;
            }
        }
        zoom.setSelectedItem(userDefinedZoom);
    }

    public void setAllActions(boolean enabled) {
	if (zoom != null) {
           zoom.setEnabled(enabled); }}

}
