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
/*$Id: MenuBar.java,v 1.24.14.17.2.22 2008/11/12 21:44:33 christianfoltin Exp $*/

package freemind.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import freemind.main.FreeMind;
import freemind.modes.ModeController;
import freemind.view.MapModule;

/**
 * This is the menu bar for FreeMind. Actions are defined in MenuListener.
 * Moreover, the StructuredMenuHolder of all menus are hold here.
 * */
public class MenuBar extends JMenuBar {

	private static java.util.logging.Logger logger;
	public static final String MENU_BAR_PREFIX = "menu_bar/";
	public static final String GENERAL_POPUP_PREFIX = "popup/";

	public static final String POPUP_MENU = GENERAL_POPUP_PREFIX + "popup/";

	public static final String INSERT_MENU = MENU_BAR_PREFIX + "insert/";
	public static final String NAVIGATE_MENU = MENU_BAR_PREFIX + "navigate/";
	public static final String VIEW_MENU = MENU_BAR_PREFIX + "view/";
	public static final String HELP_MENU = MENU_BAR_PREFIX + "help/";
	public static final String MINDMAP_MENU = MENU_BAR_PREFIX + "mindmaps/";
	private static final String MENU_MINDMAP_CATEGORY = MINDMAP_MENU
			+ "mindmaps";
	public static final String MODES_MENU = MINDMAP_MENU;
	// public static final String MODES_MENU = MENU_BAR_PREFIX+"modes/";
	public static final String EDIT_MENU = MENU_BAR_PREFIX + "edit/";
	public static final String FILE_MENU = MENU_BAR_PREFIX + "file/";
	public static final String FORMAT_MENU = MENU_BAR_PREFIX + "format/";
	public static final String EXTRAS_MENU = MENU_BAR_PREFIX + "extras/";

	private StructuredMenuHolder menuHolder;

	JPopupMenu mapsPopupMenu;
	private JMenu filemenu;
	private JMenu editmenu;
	private JMenu mapsmenu;
	Controller c;
	ActionListener mapsMenuActionListener = new MapsMenuActionListener();
	private JMenu formatmenu;

	public MenuBar(Controller controller) {
		this.c = controller;
		if (logger == null) {
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}
		// updateMenus();
	}// Constructor

	/**
	 * This is the only public method. It restores all menus.
	 * 
	 * @param newModeController
	 */
	public void updateMenus(ModeController newModeController) {
		this.removeAll();

		menuHolder = new StructuredMenuHolder();

		// filemenu
		filemenu = menuHolder.addMenu(new JMenu(c.getResourceString("file")),
				FILE_MENU + ".");
		// filemenu.setMnemonic(KeyEvent.VK_F);

		menuHolder.addCategory(FILE_MENU + "open");
		menuHolder.addCategory(FILE_MENU + "close");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "export");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "import");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "print");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "last");
		menuHolder.addSeparator(FILE_MENU);
		menuHolder.addCategory(FILE_MENU + "quit");

		// editmenu
		editmenu = menuHolder.addMenu(new JMenu(c.getResourceString("edit")),
				EDIT_MENU + ".");
		menuHolder.addCategory(EDIT_MENU + "undo");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "select");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "paste");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "edit");
		menuHolder.addSeparator(EDIT_MENU);
		menuHolder.addCategory(EDIT_MENU + "find");

		// view menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_view")),
				VIEW_MENU + ".");

		// insert menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_insert")),
				INSERT_MENU + ".");
		menuHolder.addCategory(INSERT_MENU + "nodes");
		menuHolder.addSeparator(INSERT_MENU);
		menuHolder.addCategory(INSERT_MENU + "icons");
		menuHolder.addSeparator(INSERT_MENU);

		// format menu
		formatmenu = menuHolder.addMenu(
				new JMenu(c.getResourceString("menu_format")), FORMAT_MENU
						+ ".");

		// navigate menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_navigate")),
				NAVIGATE_MENU + ".");

		// extras menu
		menuHolder.addMenu(new JMenu(c.getResourceString("menu_extras")),
				EXTRAS_MENU + ".");
		menuHolder.addCategory(EXTRAS_MENU + "first");

		// Mapsmenu
		mapsmenu = menuHolder.addMenu(
				new JMenu(c.getResourceString("mindmaps")), MINDMAP_MENU + ".");
		// mapsmenu.setMnemonic(KeyEvent.VK_M);
		menuHolder.addCategory(MINDMAP_MENU + "navigate");
		menuHolder.addSeparator(MINDMAP_MENU);
		menuHolder.addCategory(MENU_MINDMAP_CATEGORY);
		menuHolder.addSeparator(MINDMAP_MENU);
		// Modesmenu
		menuHolder.addCategory(MODES_MENU);

		// maps popup menu
		mapsPopupMenu = new FreeMindPopupMenu();
		mapsPopupMenu.setName(c.getResourceString("mindmaps"));
		menuHolder.addCategory(POPUP_MENU + "navigate");
		// menuHolder.addSeparator(POPUP_MENU);

		// formerly, the modes menu was an own menu, but to need less place for
		// the menus,
		// we integrated it into the maps menu.
		// JMenu modesmenu = menuHolder.addMenu(new
		// JMenu(c.getResourceString("modes")), MODES_MENU+".");

		menuHolder.addMenu(new JMenu(c.getResourceString("help")), HELP_MENU
				+ ".");
		menuHolder.addAction(c.documentation, HELP_MENU + "doc/documentation");
		menuHolder.addAction(c.freemindUrl, HELP_MENU + "doc/freemind");
		menuHolder.addAction(c.faq, HELP_MENU + "doc/faq");
		menuHolder.addAction(c.keyDocumentation, HELP_MENU
				+ "doc/keyDocumentation");
		menuHolder.addSeparator(HELP_MENU);
		menuHolder.addCategory(HELP_MENU + "bugs");
		menuHolder.addSeparator(HELP_MENU);
		menuHolder.addAction(c.license, HELP_MENU + "about/license");
		menuHolder.addAction(c.about, HELP_MENU + "about/about");

		updateFileMenu();
		updateViewMenu();
		updateEditMenu();
		updateModeMenu();
		updateMapsMenu(menuHolder, MENU_MINDMAP_CATEGORY + "/");
		updateMapsMenu(menuHolder, POPUP_MENU);
		addAdditionalPopupActions();
		// the modes:
		newModeController.updateMenus(menuHolder);
		menuHolder.updateMenus(this, MENU_BAR_PREFIX);
		menuHolder.updateMenus(mapsPopupMenu, GENERAL_POPUP_PREFIX);

	}

	private void updateModeMenu() {
		ButtonGroup group = new ButtonGroup();
		ActionListener modesMenuActionListener = new ModesMenuActionListener();
		List keys = new LinkedList(c.getModes());
		for (ListIterator i = keys.listIterator(); i.hasNext();) {
			String key = (String) i.next();
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(
					c.getResourceString("mode_" + key));
			item.setActionCommand(key);
			JRadioButtonMenuItem newItem = (JRadioButtonMenuItem) menuHolder
					.addMenuItem(item, MODES_MENU + key);
			group.add(newItem);
			if (c.getMode() != null) {
				newItem.setSelected(c.getMode().toString().equals(key));
			} else {
				newItem.setSelected(false);
			}
			String keystroke = c.getFrame().getAdjustableProperty(
					"keystroke_mode_" + key);
			if (keystroke != null) {
				newItem.setAccelerator(KeyStroke.getKeyStroke(keystroke));
			}
			newItem.addActionListener(modesMenuActionListener);
		}
	}

	private void addAdditionalPopupActions() {
		menuHolder.addSeparator(POPUP_MENU);
		JMenuItem newPopupItem;

		if (c.getFrame().isApplet()) {
			// We have enabled hiding of menubar only in applets. It it because
			// when we hide menubar in application, the key accelerators from
			// menubar do not work.
			newPopupItem = menuHolder.addAction(c.toggleMenubar, POPUP_MENU
					+ "toggleMenubar");
			newPopupItem.setForeground(new Color(100, 80, 80));
		}

		newPopupItem = menuHolder.addAction(c.toggleToolbar, POPUP_MENU
				+ "toggleToolbar");
		newPopupItem.setForeground(new Color(100, 80, 80));

		newPopupItem = menuHolder.addAction(c.toggleLeftToolbar, POPUP_MENU
				+ "toggleLeftToolbar");
		newPopupItem.setForeground(new Color(100, 80, 80));
	}

	private void updateMapsMenu(StructuredMenuHolder holder, String basicKey) {
		MapModuleManager mapModuleManager = c.getMapModuleManager();
		List mapModuleVector = mapModuleManager.getMapModuleVector();
		if (mapModuleVector == null) {
			return;
		}
		ButtonGroup group = new ButtonGroup();
		for (Iterator iterator = mapModuleVector.iterator(); iterator.hasNext();) {
			MapModule mapModule = (MapModule) iterator.next();
			String displayName = mapModule.getDisplayName();
			JRadioButtonMenuItem newItem = new JRadioButtonMenuItem(displayName);
			newItem.setSelected(false);
			group.add(newItem);

			newItem.addActionListener(mapsMenuActionListener);
			newItem.setMnemonic(displayName.charAt(0));

			MapModule currentMapModule = mapModuleManager.getMapModule();
			if (currentMapModule != null) {
				if (mapModule == currentMapModule) {
					newItem.setSelected(true);
				}
			}
			holder.addMenuItem(newItem, basicKey + displayName);
		}
	}

	private void updateFileMenu() {

		menuHolder.addAction(c.page, FILE_MENU + "print/pageSetup");
		JMenuItem print = menuHolder.addAction(c.print, FILE_MENU
				+ "print/print");
		print.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_print")));

		JMenuItem printPreview = menuHolder.addAction(c.printPreview, FILE_MENU
				+ "print/printPreview");
		printPreview.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_print_preview")));

		JMenuItem close = menuHolder.addAction(c.close, FILE_MENU
				+ "close/close");
		close.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_close")));

		JMenuItem quit = menuHolder.addAction(c.quit, FILE_MENU + "quit/quit");
		quit.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_quit")));
		updateLastOpenedList();
	}

	private void updateLastOpenedList() {
		menuHolder.addMenu(new JMenu(c.getResourceString("most_recent_files")),
				FILE_MENU + "last/.");
		boolean firstElement = true;
		LastOpenedList lst = c.getLastOpenedList();
		for (ListIterator it = lst.listIterator(); it.hasNext();) {
			String key = (String) it.next();
			JMenuItem item = new JMenuItem(key);
			if (firstElement) {
				firstElement = false;
				item.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
						.getAdjustableProperty(
								"keystroke_open_first_in_history")));
			}
			item.addActionListener(new LastOpenedActionListener(key));

			menuHolder.addMenuItem(item,
					FILE_MENU + "last/" + (key.replace('/', '_')));
		}
	}

	private void updateEditMenu() {
		JMenuItem moveToRoot = menuHolder.addAction(c.moveToRoot, NAVIGATE_MENU
				+ "nodes/moveToRoot");
		moveToRoot.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_moveToRoot")));

		JMenuItem previousMap = menuHolder.addAction(c.navigationPreviousMap,
				MINDMAP_MENU + "navigate/navigationPreviousMap");
		previousMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(FreeMind.KEYSTROKE_PREVIOUS_MAP)));

		JMenuItem nextMap = menuHolder.addAction(c.navigationNextMap,
				MINDMAP_MENU + "navigate/navigationNextMap");
		nextMap.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(FreeMind.KEYSTROKE_NEXT_MAP)));

		JMenuItem MoveMapLeft = menuHolder.addAction(
				c.navigationMoveMapLeftAction, MINDMAP_MENU
						+ "navigate/navigationMoveMapLeft");
		MoveMapLeft.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(FreeMind.KEYSTROKE_MOVE_MAP_LEFT)));

		JMenuItem MoveMapRight = menuHolder.addAction(
				c.navigationMoveMapRightAction, MINDMAP_MENU
						+ "navigate/navigationMoveMapRight");
		MoveMapRight.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(FreeMind.KEYSTROKE_MOVE_MAP_RIGHT)));

		// option menu item moved to mindmap_menus.xml

		// if (false) {
		// preferences.add(c.background);
		// // Background is disabled from preferences, because it has no real
		// function.
		// // To complete the function, one would either make sure that the
		// color is
		// // saved and read from auto.properties or think about storing the
		// background
		// // color into map (just like <map backgroud="#eeeee0">).
		// }

	}

	private void updateViewMenu() {
		JMenuItem toggleToolbar = menuHolder.addAction(c.toggleToolbar,
				VIEW_MENU + "toolbars/toggleToolbar");
		JMenuItem toggleLeftToolbar = menuHolder.addAction(c.toggleLeftToolbar,
				VIEW_MENU + "toolbars/toggleLeftToolbar");

		menuHolder.addSeparator(VIEW_MENU);

		JMenuItem showSelectionAsRectangle = menuHolder.addAction(
				c.showSelectionAsRectangle, VIEW_MENU
						+ "general/selectionAsRectangle");

		JMenuItem zoomIn = menuHolder.addAction(c.zoomIn, VIEW_MENU
				+ "zoom/zoomIn");
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_zoom_in")));

		JMenuItem zoomOut = menuHolder.addAction(c.zoomOut, VIEW_MENU
				+ "zoom/zoomOut");
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_zoom_out")));

		menuHolder.addSeparator(VIEW_MENU);
		menuHolder.addCategory(VIEW_MENU + "note_window");
		menuHolder.addSeparator(VIEW_MENU);
		JMenu attributes = menuHolder.addMenu(
				new JMenu(c.getResourceString("menu_attributes")), VIEW_MENU
						+ "attributes/.");
		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem itemShowAll = (JRadioButtonMenuItem) menuHolder
				.addMenuItem(new JRadioButtonMenuItem(c.showAllAttributes),
						VIEW_MENU + "attributes/showAllAttributes");
		itemShowAll.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_show_all_attributes")));
		buttonGroup.add(itemShowAll);

		JRadioButtonMenuItem itemShowSelected = (JRadioButtonMenuItem) menuHolder
				.addMenuItem(
						new JRadioButtonMenuItem(c.showSelectedAttributes),
						VIEW_MENU + "attributes/showSelectedAttributes");
		itemShowSelected.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_show_selected_attributes")));
		buttonGroup.add(itemShowSelected);

		JRadioButtonMenuItem itemHideAll = (JRadioButtonMenuItem) menuHolder
				.addMenuItem(new JRadioButtonMenuItem(c.hideAllAttributes),
						VIEW_MENU + "attributes/hideAllAttributes");
		itemHideAll.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty("keystroke_hide_all_attributes")));
		buttonGroup.add(itemHideAll);
	}

	private void addOptionSet(Action action, String[] textIDs, JMenu menu,
			String selectedTextID) {
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
			String keystroke = c.getFrame().getAdjustableProperty(
					"keystroke_" + textIDs[optionIdx]);
			if (keystroke != null)
				item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
		}
	}

	JPopupMenu getMapsPopupMenu() { // visible only in controller package
		return mapsPopupMenu;
	}

	/**
	 * This method simpy copy's all elements of the source Menu to the end of
	 * the second menu.
	 */
	private void copyMenuItems(JMenu source, JMenu dest) {
		Component[] items = source.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			dest.add(items[i]);
		}
	}

	private class MapsMenuActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					c.getMapModuleManager().changeToMapModule(
							e.getActionCommand());
				}
			});
		}
	}

	private class LastOpenedActionListener implements ActionListener {
		private String mKey;

		public LastOpenedActionListener(String pKey) {
			mKey = pKey;
		}

		public void actionPerformed(ActionEvent e) {

			String restoreable = mKey;
			try {
				c.getLastOpenedList().open(restoreable);
			} catch (Exception ex) {
				c.errorMessage("An error occured on opening the file: "
						+ restoreable + ".");
				freemind.main.Resources.getInstance().logException(ex);
			}
		}
	}

	private class ModesMenuActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					c.createNewMode(e.getActionCommand());
				}
			});
		}
	}

	/**
     */
	public StructuredMenuHolder getMenuHolder() {
		return menuHolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JMenuBar#processKeyBinding(javax.swing.KeyStroke,
	 * java.awt.event.KeyEvent, int, boolean)
	 */
	public boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition,
			boolean pressed) {
		return super.processKeyBinding(ks, e, condition, pressed);
	}

}
