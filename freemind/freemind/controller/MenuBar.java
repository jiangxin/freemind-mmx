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
/*$Id: MenuBar.java,v 1.13 2003-11-03 10:15:44 sviles Exp $*/

package freemind.controller;

import freemind.main.FreeMind;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;
import java.awt.Component;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


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
	filemenu = new JMenu(c.getFrame().getResources().getString("file"));
	editmenu = new JMenu(c.getFrame().getResources().getString("edit"));
	this.add(filemenu);
	this.add(editmenu);

	//Mapsmenu
	mapsmenu = new JMenu(c.getFrame().getResources().getString("mindmaps"));
	mapsPopupMenu = new JPopupMenu(c.getFrame().getResources().getString("mindmaps"));
	this.add(mapsmenu);

	//Modesmenu
	JMenu modesmenu = new JMenu(c.getFrame().getResources().getString("modes"));
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

	//Helpmenu
	JMenu helpmenu = new JMenu(c.getFrame().getResources().getString("help"));
	this.add(helpmenu);

	helpmenu.add(c.documentation);

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
	for (ListIterator i = keys.listIterator(); i.hasNext();) {
	    String key = (String)i.next();
	    JMenuItem newItem = new JMenuItem(key);
	    JMenuItem newPopupItem = new JMenuItem(key);

	    newItem.addActionListener(mapsMenuActionListener);
            newPopupItem.addActionListener(mapsMenuActionListener);

	    if (c.getMapModuleManager().getMapModule() != null) {
		if (key.equals(c.getMapModuleManager().getMapModule().toString())) {
		    //This could be done more elegant
		    newItem.setBackground(Color.lightGray);
                    newPopupItem.setBackground(Color.lightGray);
		}
	    }
            mapsPopupMenu.add(newItem);
	    mapsmenu.add(newPopupItem);
	}
    }

    public void updateFileMenu() {
	filemenu.removeAll();
	if ((c.getMode() != null) && (c.getMode().getModeFileMenu() != null)) {
	    copyMenuItems(c.getMode().getModeFileMenu(), filemenu);
	}

	filemenu.addSeparator();
	JMenuItem page = filemenu.add(c.page);
	JMenuItem print = filemenu.add(c.print);

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
	LastOpenedList lst = c.getLastOpenedList();
	for(ListIterator it=lst.listIterator();it.hasNext();) {
            JMenuItem item = new JMenuItem((String)it.next());
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

	JMenuItem moveToRoot = editmenu.add(c.moveToRoot);
	moveToRoot.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_moveToRoot")));

	JMenuItem previousMap = editmenu.add(c.navigationPreviousMap);
	previousMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_previousMap")));

	JMenuItem nextMap = editmenu.add(c.navigationNextMap);
	nextMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame().getProperty("keystroke_nextMap")));

	JMenuItem historyPreviousMap = editmenu.add(c.historyPreviousMap);
	JMenuItem historyNextMap = editmenu.add(c.historyNextMap);

	editmenu.addSeparator();

	JMenu preferences = new JMenu(c.getFrame().getResources().getString("preferences"));
	editmenu.add(preferences);

	preferences.add(c.background);
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
