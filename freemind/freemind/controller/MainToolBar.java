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
/*$Id: MainToolBar.java,v 1.13 2003-11-03 11:00:05 sviles Exp $*/

package freemind.controller;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class MainToolBar extends JToolBar {
    JComboBox zoom;	    
    Controller c;

    public MainToolBar(final Controller c) {
        this.setRollover(true);
        this.c = c;
	JButton button;

	button = add(c.navigationPreviousMap);
	button.setText("");
	button = add(c.navigationNextMap);
	button.setText("");
	button = add(c.printDirect);
	button.setText("");

        zoom = new JComboBox(c.getZooms());
        zoom.setSelectedItem("100%");
        add(zoom);
        zoom.addItemListener(new ItemListener(){
              public void itemStateChanged(ItemEvent e) {
                 setZoomByItem(e.getItem()); }}); }

    private void setZoomByItem(Object item) {
       //remove '%' sign
      String dirty = (String)item;
      String cleaned = dirty.substring(0,dirty.length()-1);
      //change representation ("125" to 1.25)
      c.setZoom(Integer.parseInt(cleaned,10)/100F); }

    public void zoomOut() {
       if (zoom.getSelectedIndex() > 0) {
          setZoomByItem(zoom.getItemAt(zoom.getSelectedIndex() - 1));
          zoom.setSelectedItem(zoom.getItemAt(zoom.getSelectedIndex() - 1)); }}

    public void zoomIn() {
       if (zoom.getSelectedIndex() < zoom.getItemCount() - 1) {
          setZoomByItem(zoom.getItemAt(zoom.getSelectedIndex() + 1));
          zoom.setSelectedItem(zoom.getItemAt(zoom.getSelectedIndex() + 1)); }}

    public String getItemForZoom(float f) {
       return (int)(f*100F)+"%"; }

    public void setZoomComboBox(float f) {
       // Todo: checking
       zoom.setSelectedItem(getItemForZoom(f)); }

    public void setAllActions(boolean enabled) {
	if (zoom != null) {
           zoom.setEnabled(enabled); }}
}
