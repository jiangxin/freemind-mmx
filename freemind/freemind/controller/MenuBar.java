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
/*$Id: MenuBar.java,v 1.22 2004-01-17 23:20:57 christianfoltin Exp $*/

package freemind.controller;

import java.util.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**This is the menu bar for FreeMind. Actions are defined in MenuListener. */
public class MenuBar extends JMenuBar {

    JMenu mapsmenu;
    JPopupMenu mapsPopupMenu;
    private JMenu filemenu;
    private JMenu editmenu;
    Controller c;
    private LinkedList lastOpenedItems = new LinkedList();
    ActionListener mapsMenuActionListener = new MapsMenuActionListener();
    ActionListener lastOpenedActionListener = new LastOpenedActionListener();

    public MenuBar(Controller controller) {
	this.c = controller;
	filemenu = new JMenu(c.getResourceString("file"));
	editmenu = new JMenu(c.getResourceString("edit"));
	this.add(filemenu);
	this.add(editmenu);

	//Mapsmenu
	mapsmenu = new JMenu(c.getResourceString("mindmaps"));
        //mapsmenu.setMnemonic(KeyEvent.VK_M);
	mapsPopupMenu = new JPopupMenu(c.getResourceString("mindmaps"));
	this.add(mapsmenu);

	//Modesmenu
	JMenu modesmenu = new JMenu(c.getResourceString("modes"));
	this.add(modesmenu);

	ActionListener modesMenuActionListener = new ModesMenuActionListener();
	List keys = new LinkedList(c.getModes().keySet());
	for (ListIterator i = keys.listIterator(); i.hasNext();) {
	    String key = (String)i.next();
	    JMenuItem newItem = new JMenuItem(key);
	    modesmenu.add(newItem);
	    String keystroke = c.getFrame().getProperty("keystroke_mode_"+key);
	    if (keystroke != null) {
		newItem.setAccelerator(KeyStroke.getKeyStroke(keystroke));
	    }
	    newItem.addActionListener(modesMenuActionListener);
	    //if (key.equals(c.getMode().toString())) {
	    //		newItem.setBackground(Color.blue);
		//}
	}

        filemenu.setMnemonic(KeyEvent.VK_F);
        mapsmenu.setMnemonic(KeyEvent.VK_M);

	//Helpmenu
	JMenu helpmenu = new JMenu(c.getResourceString("help"));
	this.add(helpmenu);

	helpmenu.add(c.documentation);
	helpmenu.add(c.faq);
	helpmenu.add(c.license);
	helpmenu.add(c.about);

	
    }//Constructor

    public void updateMapsMenu() {
        mapsPopupMenu.removeAll();
	mapsmenu.removeAll();
	if (c.getMapModuleManager().getMapModules() == null) {
	    return;
	}
	List keys = new LinkedList(c.getMapModuleManager().getMapModules().keySet());
        Collections.sort(keys);
	for (ListIterator i = keys.listIterator(); i.hasNext();) {
	    String key = (String)i.next();
	    JMenuItem newItem = new JMenuItem(key);
	    JMenuItem newPopupItem = new JMenuItem(key);

	    newItem.addActionListener(mapsMenuActionListener);
            newPopupItem.addActionListener(mapsMenuActionListener);

            newItem.setMnemonic(key.charAt(0));
            newPopupItem.setMnemonic(key.charAt(0));

	    if (c.getMapModuleManager().getMapModule() != null) {
		if (key.equals(c.getMapModuleManager().getMapModule().toString())) {
		    //This could be done more elegant
		    newItem.setBackground(Color.lightGray);
                    newPopupItem.setBackground(Color.lightGray);
		}
	    }
            mapsPopupMenu.add(newPopupItem);
	    mapsmenu.add(newItem);
	}
        mapsPopupMenu.addSeparator();

        JMenuItem newPopupItem;

        newPopupItem = new JMenuItem(c.toggleMenubar);
        newPopupItem.setForeground(new Color(100,80,80));
        newPopupItem.setEnabled(c.getFrame().isApplet());
        // We have enabled hiding of menubar only in applets. It it because
        // when we hide menubar in application, the key accelerators from
        // menubar do not work.
        mapsPopupMenu.add(newPopupItem);

        newPopupItem = new JMenuItem(c.toggleToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        mapsPopupMenu.add(newPopupItem);

        newPopupItem = new JMenuItem(c.toggleLeftToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        mapsPopupMenu.add(newPopupItem);
    }

    public void updateFileMenu() {
	filemenu.removeAll();
	if ((c.getMode() != null) && (c.getMode().getModeFileMenu() != null)) {
	    copyMenuItems(c.getMode().getModeFileMenu(), filemenu);
	}

	filemenu.addSeparator();
	JMenuItem page = filemenu.add(c.page);
	JMenuItem print = filemenu.add(c.print);
	print.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_print")));

	filemenu.addSeparator();

	JMenuItem close = filemenu.add(c.close);
	close.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_close")));
	
	JMenuItem quit = filemenu.add(c.quit);
	quit.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_quit")));
	filemenu.addSeparator();
	updateLastOpenedList();
    }

    public void updateLastOpenedList() {
	for(ListIterator it=lastOpenedItems.listIterator();it.hasNext();) {
	    filemenu.remove((JMenuItem)it.next());
	}
	lastOpenedItems.clear();
    boolean firstElement = true;
	LastOpenedList lst = c.getLastOpenedList();
	for(ListIterator it=lst.listIterator();it.hasNext();) {
            JMenuItem item = new JMenuItem((String)it.next());
            if(firstElement) {
                firstElement = false;
                item.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_open_first_in_history")));
            }
	    item.addActionListener(lastOpenedActionListener);
	    lastOpenedItems.add(item);
	    filemenu.add(item);
	}
    }

    public void updateEditMenu() {
	editmenu.removeAll();

	if ((c.getMode() != null) && (c.getMode().getModeEditMenu() != null)) {
	    copyMenuItems(c.getMode().getModeEditMenu(), editmenu);
	}

	editmenu.addSeparator();

	JMenuItem zoomIn = editmenu.add(c.zoomIn);
	zoomIn.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_zoom_in")));

	JMenuItem zoomOut = editmenu.add(c.zoomOut);
	zoomOut.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_zoom_out")));

	editmenu.addSeparator();

	JMenuItem moveToRoot = editmenu.add(c.moveToRoot);
	moveToRoot.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_moveToRoot")));

	JMenuItem previousMap = editmenu.add(c.navigationPreviousMap);
	previousMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_previousMap")));

	JMenuItem nextMap = editmenu.add(c.navigationNextMap);
	nextMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_nextMap")));

	//JMenuItem historyPreviousMap = editmenu.add(c.historyPreviousMap);
	//JMenuItem historyNextMap = editmenu.add(c.historyNextMap);
        // ^ Daniel: This does not work as expected.

	editmenu.addSeparator();

	JMenu preferences = new JMenu(c.getResourceString("preferences"));
	editmenu.add(preferences);

        if (false) {
           preferences.add(c.background);
           // Background is disabled from preferences, because it has no real function.
           // To complete the function, one would either make sure that the color is
           // saved and read from auto.properties or think about storing the background
           // color into map (just like <map backgroud="#eeeee0">).
        }

        // Daniel: The way antialias option is represented now is not nice,
        // there should be only one variable controlling it. Fix it if you
        // have time, this kind of classical quality is low pri now.
        String antialiasSelected = c.getAntialiasAll() ? "antialias_all" : 
           ( c.getAntialiasEdges() ? "antialias_edges" : "antialias_none" );
        addOptionSet( c.optionAntialiasAction,
                      new String[]{ "antialias_none",
                                       "antialias_edges",
                                       "antialias_all" },
                      preferences, antialiasSelected );
	preferences.addSeparator();
        addOptionSet( c.optionHTMLExportFoldingAction,
                      new String[]{ "html_export_no_folding",
                                       "html_export_fold_currently_folded",
                                       "html_export_fold_all",
                                       "html_export_based_on_headings" },
                      preferences, c.getProperty("html_export_folding") );

	preferences.addSeparator();
    addOptionSet( c.optionSelectionMechanismAction,
                  new String[]{ "selection_method_delayed",
                                "selection_method_direct",
                                "selection_method_by_click"},
                  preferences, c.getProperty("selection_method") );

    }

   private void addOptionSet(Action action, String[] textIDs, JMenu menu, String selectedTextID) {
      ButtonGroup group = new ButtonGroup();
      for (int optionIdx = 0; optionIdx < textIDs.length; optionIdx++) {
         JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
         item.setText(c.getResourceString(textIDs[optionIdx]));
         item.setActionCommand(textIDs[optionIdx]);
         group.add(item);
         menu.add(item);
         if (selectedTextID != null) {
            item.setSelected(selectedTextID.equals(textIDs[optionIdx])); 
         }
         // keystroke present?
         String keystroke = c.getFrame().getProperty("keystroke_"+textIDs[optionIdx]);
         if(keystroke != null)
             item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
      }
   }

    JPopupMenu getMapsPopupMenu()  { // visible only in controller package
       return mapsPopupMenu; }


    /**
     * This method simpy copy's all elements of the source Menu
     * to the end of the second menu.
     */
    private void copyMenuItems (JMenu source, JMenu dest) {
	Component[] items = source.getMenuComponents();
	for (int i=0; i<items.length; i++) {
	    dest.add(items[i]);
	}
    }

    private class MapsMenuActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    c.getMapModuleManager().changeToMapModule(e.getActionCommand());
	}
    }

    private class LastOpenedActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    c.getLastOpenedList().open(e.getActionCommand());
	}
    }

    private class ModesMenuActionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    c.changeToMode(e.getActionCommand());
	}
    }
}
