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
/*$Id: MenuBar.java,v 1.24.10.1 2004-05-21 21:49:11 christianfoltin Exp $*/

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

    public static final String HELP_MENU = "help/";
    public static final String MODES_MENU = "modes/";
    public static final String MINDMAP_MENU = "mindmaps/";
    public static final String EDIT_MENU = "edit/";
    public static final String FILE_MENU = "file/";
    public static final String POPUP_MENU = "popup/";

	private StructuredMenuHolder menuHolder;
	private StructuredMenuHolder menuPopupHolder;
	
    JPopupMenu mapsPopupMenu;
    private JMenu filemenu;
    private JMenu editmenu;
	private JMenu mapsmenu;
    Controller c;
    ActionListener mapsMenuActionListener = new MapsMenuActionListener();
    ActionListener lastOpenedActionListener = new LastOpenedActionListener();

    public MenuBar(Controller controller) {
		this.c = controller;
		updateMenus();
    }//Constructor


	/**
	 * This is the only public method. It restores all menus.
	 */
	public void updateMenus() {
		this.removeAll();

		menuHolder = new StructuredMenuHolder();
		menuPopupHolder = new StructuredMenuHolder();


		filemenu = menuHolder.addMenu(new JMenu(c.getResourceString("file")), FILE_MENU+".");
		editmenu = menuHolder.addMenu(new JMenu(c.getResourceString("edit")), EDIT_MENU+".");
	
		//Mapsmenu
		mapsmenu = menuHolder.addMenu(new JMenu(c.getResourceString("mindmaps")), MINDMAP_MENU+".");

		//mapsPopupMenu = menuPopupHolder.addPopupMenu(new JPopupMenu(c.getResourceString("mindmaps")), POPUP_MENU+".");
		mapsPopupMenu = new JPopupMenu(c.getResourceString("mindmaps"));

		filemenu.setMnemonic(KeyEvent.VK_F);
		mapsmenu.setMnemonic(KeyEvent.VK_M);
	
		//Modesmenu
		JMenu modesmenu = menuHolder.addMenu(new JMenu(c.getResourceString("modes")), MODES_MENU+".");

		ButtonGroup group = new ButtonGroup();
		ActionListener modesMenuActionListener = new ModesMenuActionListener();
		List keys = new LinkedList(c.getModes().keySet());
		for (ListIterator i = keys.listIterator(); i.hasNext();) {
			String key = (String)i.next();
			JRadioButtonMenuItem newItem = (JRadioButtonMenuItem) menuHolder.addMenuItem(new JRadioButtonMenuItem(key), MODES_MENU+key);
			group.add(newItem);
			if (c.getMode() != null) {
                newItem.setSelected(c.getMode().toString().equals(key));
            } else  {
            	newItem.setSelected(false);
            }
			String keystroke = c.getFrame().getProperty("keystroke_mode_"+key);
			if (keystroke != null) {
				newItem.setAccelerator(KeyStroke.getKeyStroke(keystroke));
			}
			newItem.addActionListener(modesMenuActionListener);
		}
	
		menuHolder.addMenu(new JMenu(c.getResourceString("help")), HELP_MENU+".");
		menuHolder.addAction(c.documentation, HELP_MENU+"doc/documentation");
		menuHolder.addAction(c.faq, HELP_MENU+"doc/faq");
		menuHolder.addAction(c.license, HELP_MENU+"about/license");
		menuHolder.addSeparator(HELP_MENU+"about");
		menuHolder.addAction(c.about, HELP_MENU+"about/about");


		updateFileMenu();
		updateEditMenu();
		updateMapsMenu(menuHolder, MINDMAP_MENU);
		updateMapsMenu(menuPopupHolder, POPUP_MENU);
		addAdditionalPopupActions();

		menuHolder.updateMenus(this);
		menuPopupHolder.updateMenus(mapsPopupMenu);

	}


    private void addAdditionalPopupActions() {
		menuPopupHolder.addSeparator(POPUP_MENU);
        JMenuItem newPopupItem;
        
        newPopupItem = new JMenuItem(c.toggleMenubar);
        newPopupItem.setForeground(new Color(100,80,80));
        newPopupItem.setEnabled(c.getFrame().isApplet());
        // We have enabled hiding of menubar only in applets. It it because
        // when we hide menubar in application, the key accelerators from
        // menubar do not work.
        menuPopupHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleMenubar");
        
        newPopupItem = new JMenuItem(c.toggleToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        menuPopupHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleToolbar");
        
        newPopupItem = new JMenuItem(c.toggleLeftToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        menuPopupHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleLeftToolbar");
    }
	
    private void updateMapsMenu(StructuredMenuHolder holder, String basicKey) {
		if (c.getMapModuleManager().getMapModules() == null) {
		    return;
		}
		List keys = new LinkedList(c.getMapModuleManager().getMapModules().keySet());
        Collections.sort(keys);
		ButtonGroup group = new ButtonGroup();
		for (ListIterator i = keys.listIterator(); i.hasNext();) {
		    String key = (String)i.next();
		    JRadioButtonMenuItem newItem = new JRadioButtonMenuItem(key);
			newItem.setSelected(false);
		    group.add(newItem);
	
		    newItem.addActionListener(mapsMenuActionListener);
            newItem.setMnemonic(key.charAt(0));
	
		    if (c.getMapModuleManager().getMapModule() != null) {
				if (key.equals(c.getMapModuleManager().getMapModule().toString())) {
					newItem.setSelected(true);
				}
			}
			holder.addMenuItem(newItem, basicKey+key);
		}
    }



    private void updateFileMenu() {
		if ((c.getMode() != null)) {
			c.getMode().updateMenus(menuHolder);
		}
	
    	menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addAction(c.page, FILE_MENU+"print/pageSetup");
		JMenuItem print = menuHolder.addAction(c.print, FILE_MENU+"print/print");
		print.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_print")));
	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"last");	
		JMenuItem close = menuHolder.addAction(c.close, FILE_MENU+"quit/close");
		close.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_close")));
		
		JMenuItem quit = menuHolder.addAction(c.quit, FILE_MENU+"quit/quit");
		quit.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_quit")));
		updateLastOpenedList();
    }

    private void updateLastOpenedList() {
		menuHolder.addMenu(new JMenu("last files"), FILE_MENU+"last/.");
        boolean firstElement = true;
        LastOpenedList lst = c.getLastOpenedList();
        for (ListIterator it = lst.listIterator(); it.hasNext();) {
            String key = (String) it.next();
            JMenuItem item = new JMenuItem(key);
            if (firstElement) {
                firstElement = false;
                item.setAccelerator(
                    KeyStroke.getKeyStroke(
                        c.getFrame().getProperty(
                            "keystroke_open_first_in_history")));
            }
            item.addActionListener(lastOpenedActionListener);
            
            menuHolder.addMenuItem(item, FILE_MENU+"last/"+(key.replace('/', '_')));
        }
    }

    private void updateEditMenu() {
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
	                  new String[]{ "selection_method_direct",
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
