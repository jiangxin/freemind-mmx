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
/*$Id: MenuBar.java,v 1.24.14.8.4.4 2006-02-28 20:58:08 dpolivaev Exp $*/

package freemind.controller;

import java.util.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**This is the menu bar for FreeMind. Actions are defined in MenuListener.
 * Moreover, the StructuredMenuHolder of all menus are hold here.
 *  */
public class MenuBar extends JMenuBar {

    private static java.util.logging.Logger logger ;
	public static final String MENU_BAR_PREFIX = "menu_bar/";
	public static final String GENERAL_POPUP_PREFIX = "popup/";

	public static final String POPUP_MENU = GENERAL_POPUP_PREFIX+"popup/";

	public static final String INSERT_MENU = MENU_BAR_PREFIX+"insert/";
	public static final String NAVIGATE_MENU = MENU_BAR_PREFIX+"navigate/";
	public static final String VIEW_MENU = MENU_BAR_PREFIX+"view/";
    public static final String HELP_MENU = MENU_BAR_PREFIX+"help/";
    public static final String MODES_MENU = MENU_BAR_PREFIX+"modes/";
    public static final String MINDMAP_MENU = MENU_BAR_PREFIX+"mindmaps/";
    public static final String EDIT_MENU = MENU_BAR_PREFIX+"edit/";
    public static final String FILE_MENU = MENU_BAR_PREFIX+"file/";
    public static final String FORMAT_MENU = MENU_BAR_PREFIX+"format/";
    public static final String EXTRAS_MENU = MENU_BAR_PREFIX+"extras/";


	private StructuredMenuHolder menuHolder;
	
    JPopupMenu mapsPopupMenu;
    private JMenu filemenu;
    private JMenu editmenu;
	private JMenu mapsmenu;
    Controller c;
    ActionListener mapsMenuActionListener = new MapsMenuActionListener();
    ActionListener lastOpenedActionListener = new LastOpenedActionListener();
    private JMenu formatmenu;

    public MenuBar(Controller controller) {
		this.c = controller;
		if(logger==null) {
		    logger = controller.getFrame().getLogger(this.getClass().getName());
		}
		//updateMenus();
    }//Constructor


	/**
	 * This is the only public method. It restores all menus.
	 */
	public void updateMenus() {
		this.removeAll();

		menuHolder = new StructuredMenuHolder();

		// filemenu
		filemenu = menuHolder.addMenu(new JMenu(c.getResourceString("file")), FILE_MENU+".");
		filemenu.setMnemonic(KeyEvent.VK_F);

		menuHolder.addCategory(FILE_MENU+"open");	
		menuHolder.addCategory(FILE_MENU+"close");	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"export");	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"import");	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"print");	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"last");	
		menuHolder.addSeparator(FILE_MENU);	
		menuHolder.addCategory(FILE_MENU+"quit");	

		// editmenu
		editmenu = menuHolder.addMenu(new JMenu(c.getResourceString("edit")), EDIT_MENU+".");
		menuHolder.addCategory(EDIT_MENU+"undo");	
		menuHolder.addSeparator(EDIT_MENU);	
		menuHolder.addCategory(EDIT_MENU+"select");	
		menuHolder.addSeparator(EDIT_MENU);	
		menuHolder.addCategory(EDIT_MENU+"paste");	
		menuHolder.addSeparator(EDIT_MENU);	
		menuHolder.addCategory(EDIT_MENU+"edit");	
		menuHolder.addSeparator(EDIT_MENU);	
		menuHolder.addCategory(EDIT_MENU+"find");	

		//view menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_view")), VIEW_MENU+".");

		//insert menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_insert")), INSERT_MENU+".");
		menuHolder.addCategory(INSERT_MENU+"nodes");	
		menuHolder.addSeparator(INSERT_MENU);	
		menuHolder.addCategory(INSERT_MENU+"icons");	
		menuHolder.addSeparator(INSERT_MENU);	

		//format menu
		formatmenu = menuHolder.addMenu(new JMenu(c.getResourceString("menu_format")), FORMAT_MENU+".");
		menuHolder.addCategory(FORMAT_MENU+"patterns");	
		menuHolder.addSeparator(FORMAT_MENU);	

		//navigate menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_navigate")), NAVIGATE_MENU+".");


        //extras menu
        menuHolder.addMenu(new JMenu(c.getResourceString("menu_extras")), EXTRAS_MENU+".");
        menuHolder.addCategory(EXTRAS_MENU+"first");    
        menuHolder.addSeparator(EXTRAS_MENU);   
        menuHolder.addCategory(EXTRAS_MENU+"last"); 

		//Mapsmenu
		mapsmenu = menuHolder.addMenu(new JMenu(c.getResourceString("mindmaps")), MINDMAP_MENU+".");
		mapsmenu.setMnemonic(KeyEvent.VK_M);
		menuHolder.addCategory(MINDMAP_MENU+"navigate");	
		menuHolder.addSeparator(MINDMAP_MENU);	

		// maps popup menu
		mapsPopupMenu = new JPopupMenu(c.getResourceString("mindmaps"));
		menuHolder.addCategory(POPUP_MENU+"navigate");	
		//menuHolder.addSeparator(POPUP_MENU);	
	
		//Modesmenu
		JMenu modesmenu = menuHolder.addMenu(new JMenu(c.getResourceString("modes")), MODES_MENU+".");
	
		menuHolder.addMenu(new JMenu(c.getResourceString("help")), HELP_MENU+".");
		menuHolder.addAction(c.documentation, HELP_MENU+"doc/documentation");
		menuHolder.addAction(c.faq, HELP_MENU+"doc/faq");
		menuHolder.addSeparator(HELP_MENU);
		menuHolder.addAction(c.license, HELP_MENU+"about/license");
		menuHolder.addAction(c.about, HELP_MENU+"about/about");

		updateFileMenu();
		updateEditMenu();
		updateModeMenu();
		updateMapsMenu(menuHolder, MINDMAP_MENU);
		updateMapsMenu(menuHolder, POPUP_MENU);
		addAdditionalPopupActions();
		// the modes:
		if ((c.getMode() != null)) {
			c.getMode().getModeController().updateMenus(menuHolder);
		}
		menuHolder.updateMenus(this, MENU_BAR_PREFIX);
		menuHolder.updateMenus(mapsPopupMenu, GENERAL_POPUP_PREFIX);
		
	}


    private void updateModeMenu() {
        ButtonGroup group = new ButtonGroup();
        ActionListener modesMenuActionListener = new ModesMenuActionListener();
        List keys = new LinkedList(c.getModes());
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
    }


    private void addAdditionalPopupActions() {
		menuHolder.addSeparator(POPUP_MENU);
        JMenuItem newPopupItem;
        
        newPopupItem = new JMenuItem(c.toggleMenubar);
        newPopupItem.setForeground(new Color(100,80,80));
        newPopupItem.setEnabled(c.getFrame().isApplet());
        // We have enabled hiding of menubar only in applets. It it because
        // when we hide menubar in application, the key accelerators from
        // menubar do not work.
        menuHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleMenubar");
        
        newPopupItem = new JMenuItem(c.toggleToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        menuHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleToolbar");
        
        newPopupItem = new JMenuItem(c.toggleLeftToolbar);
        newPopupItem.setForeground(new Color(100,80,80));
        menuHolder.addMenuItem(newPopupItem, POPUP_MENU+"toggleLeftToolbar");
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
	
		menuHolder.addAction(c.page, FILE_MENU+"print/pageSetup");
		JMenuItem print = menuHolder.addAction(c.print, FILE_MENU+"print/print");
		print.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_print")));
	
		JMenuItem close = menuHolder.addAction(c.close, FILE_MENU+"close/close");
		close.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_close")));
		
		JMenuItem quit = menuHolder.addAction(c.quit, FILE_MENU+"quit/quit");
		quit.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_quit")));
		updateLastOpenedList();
    }

    private void updateLastOpenedList() {
		menuHolder.addMenu(new JMenu(c.getResourceString("most_recent_files")), FILE_MENU+"last/.");
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
		JMenuItem toggleToolbar = menuHolder.addAction(c.toggleToolbar, VIEW_MENU+"toolbars/toggleToolbar");
		JMenuItem toggleLeftToolbar = menuHolder.addAction(c.toggleLeftToolbar, VIEW_MENU+"toolbars/toggleLeftToolbar");
		
		menuHolder.addSeparator(VIEW_MENU);
		
		JMenuItem zoomIn = menuHolder.addAction(c.zoomIn, VIEW_MENU+"zoom/zoomIn");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_zoom_in")));
	
		JMenuItem zoomOut = menuHolder.addAction(c.zoomOut, VIEW_MENU+"zoom/zoomOut");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_zoom_out")));
	
		JMenuItem moveToRoot = menuHolder.addAction(c.moveToRoot, NAVIGATE_MENU+"nodes/moveToRoot");
		moveToRoot.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_moveToRoot")));
	

		JMenuItem previousMap = menuHolder.addAction(c.navigationPreviousMap, MINDMAP_MENU+"navigate/navigationPreviousMap");
		previousMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_previousMap")));
	
		JMenuItem nextMap = menuHolder.addAction(c.navigationNextMap, MINDMAP_MENU+"navigate/navigationNextMap");
		nextMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_nextMap")));
	
	
//		JMenu preferences = menuHolder.addMenu(new JMenu(c.getResourceString("preferences")), EXTRAS_MENU+"last/options/.");
		JMenuItem prefDialog = menuHolder.addAction(c.propertyAction, EXTRAS_MENU+"last/option_dialog");
		prefDialog.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_option_dialog")));
	
//	        if (false) {
//	           preferences.add(c.background);
//	           // Background is disabled from preferences, because it has no real function.
//	           // To complete the function, one would either make sure that the color is
//	           // saved and read from auto.properties or think about storing the background
//	           // color into map (just like <map backgroud="#eeeee0">).
//	        }
	
	
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
    

    /**
     * @return
     */
    public StructuredMenuHolder getMenuHolder() {
        return menuHolder;
    }

}
