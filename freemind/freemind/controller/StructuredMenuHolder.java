/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 21.05.2004
 */
/*$Id: StructuredMenuHolder.java,v 1.1.4.7.4.11 2010/09/30 22:38:47 christianfoltin Exp $*/

package freemind.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;

/**
 * @author foltin
 * 
 */
public class StructuredMenuHolder {

	/**
	 * 
	 */
	public static final String AMOUNT_OF_VISIBLE_MENU_ITEMS = "AMOUNT_OF_VISIBLE_MENU_ITEMS";
	public static final int ICON_SIZE = 16;
	private String mOutputString;
	private static Icon blindIcon = new BlindIcon(ICON_SIZE);
	private static final String SELECTED_ICON_PATH = "images/button_ok.png";

	private static final String SEPARATOR_TEXT = "000";
	private static final String ORDER_NAME = "/order";
	Map menuMap;
	private static java.util.logging.Logger logger = null;

	private int mIndent;
	private static ImageIcon sSelectedIcon;

	public StructuredMenuHolder() {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		menuMap = new HashMap();
		Vector order = new Vector();
		menuMap.put(ORDER_NAME, order);
		if (sSelectedIcon == null) {
			sSelectedIcon = new ImageIcon(Resources.getInstance().getResource(
					SELECTED_ICON_PATH));
		}

	}

	/**
	 */
	public JMenu addMenu(JMenu item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		return (JMenu) addMenu(item, tokens);
	}

	/**
	 */
	public JMenuItem addMenuItem(JMenuItem item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item.getAction());
		holder.setMenuItem(item);
		adjustTooltips(holder);
		addMenu(holder, tokens);
		return item;
	}

	/**
	 * @param item is an action. If it derives from MenuItemSelectedListener, 
	 * a check box is used.
	 */
	public JMenuItem addAction(Action item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item);
		/*
		 * Dimitry, Eric and Dan requested to have the check marks with the
		 * original JCheckBoxMenuItem.
		 */
		if (item instanceof MenuItemSelectedListener) {
			holder.setMenuItem(new JCheckBoxMenuItem(item));
		} else {
			holder.setMenuItem(new JMenuItem(item));
		}
		adjustTooltips(holder);
		addMenu(holder, tokens);
		return holder.getMenuItem();
	}

	/**
	 * Under Mac, no HTML is rendered for menus.
	 * 
	 * @param holder
	 */
	private void adjustTooltips(StructuredMenuItemHolder holder) {
		if (Tools.isMacOsX()) {
			// remove html tags from tooltips:
			String toolTipText = holder.getMenuItem().getToolTipText();
			if (toolTipText != null) {
				String toolTipTextWithoutTags = HtmlTools
						.removeHtmlTagsFromString(toolTipText);
				logger.finest("Old tool tip: " + toolTipText
						+ ", New tool tip: " + toolTipTextWithoutTags);
				holder.getMenuItem().setToolTipText(toolTipTextWithoutTags);
			}
		}
	}

	public void addCategory(String category) {
		StringTokenizer tokens = new StringTokenizer(category + "/blank", "/");
		// with this call, the category is created.
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
	}

	public void addSeparator(String category) {
		String sep = category;
		if (!sep.endsWith("/")) {
			sep += "/";
		}
		sep += SEPARATOR_TEXT;
		StringTokenizer tokens = new StringTokenizer(sep, "/");
		// separators can occur as doubles.
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		// add an separator
		categoryPair.map.put(categoryPair.token, new SeparatorHolder());
		categoryPair.order.add(categoryPair.token);
	}

	/**
	 */
	private Object addMenu(Object item, StringTokenizer tokens) {
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		// add the item:
		categoryPair.map.put(categoryPair.token, item);
		categoryPair.order.add(categoryPair.token);
		return item;
	}

	private final class PrintMenuAdder implements MenuAdder {
		public void addMenuItem(StructuredMenuItemHolder holder) {
			print("JMenuItem '" + holder.getMenuItem().getActionCommand() + "'");
		}

		public void addSeparator() {
			print("Separator '" + "'");
		}

		// public void addAction(Action action) {
		// print("Action    '"+action.getValue(Action.NAME)+"'");
		// }
		public void addCategory(String category) {
			print("Category: '" + category + "'");
		}
	}

	private class MapTokenPair {
		Map map;
		String token;
		Vector order;

		MapTokenPair(Map map, String token, Vector order) {
			this.map = map;
			this.token = token;
			this.order = order;
		}
	}

	private MapTokenPair getCategoryMap(StringTokenizer tokens, Map thisMap) {
		if (tokens.hasMoreTokens()) {
			String nextToken = tokens.nextToken();
			if (tokens.hasMoreTokens()) {
				if (!thisMap.containsKey(nextToken)) {
					Map newMap = new HashMap();
					Vector newOrder = new Vector();
					newMap.put(ORDER_NAME, newOrder);
					thisMap.put(nextToken, newMap);
				}
				Map nextMap = (Map) thisMap.get(nextToken);
				Vector order = (Vector) thisMap.get(ORDER_NAME);
				if (!order.contains(nextToken)) {
					order.add(nextToken);
				}
				return getCategoryMap(tokens, nextMap);
			} else {
				Vector order = (Vector) thisMap.get(ORDER_NAME);
				return new MapTokenPair(thisMap, nextToken, order);
			}
		}
		// error case?
		return null;
	}

	public void updateMenus(final JMenuBar myItem, String prefix) {

		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"),
				menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			public void addMenuItem(StructuredMenuItemHolder holder) {
				Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
				myItem.add(holder.getMenuItem());
			}

			public void addSeparator() {
				throw new NoSuchMethodError("addSeparator for JMenuBar");
			}

			// public void addAction(Action action) {
			// throw new NoSuchMethodError("addAction for JMenuBar");
			// }

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());
	}

	public void updateMenus(final JPopupMenu myItem, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"),
				menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			StructuredMenuListener listener = new StructuredMenuListener();

			public void addMenuItem(StructuredMenuItemHolder holder) {
				Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
				JMenuItem menuItem = holder.getMenuItem();
				adjustMenuItem(menuItem);
				myItem.add(menuItem);
				if (myItem instanceof MenuEventSupplier) {
					MenuEventSupplier receiver = (MenuEventSupplier) myItem;
					receiver.addMenuListener(listener);
					listener.addItem(holder);
				}

			}

			public void addSeparator() {
				if (lastItemIsASeparator(myItem))
					return;
				myItem.addSeparator();
			}

			// public void addAction(Action action) {
			// myItem.add(action);
			// }

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());
	}

	/**
	 */
	public void updateMenus(final JToolBar bar, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"),
				menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			public void addMenuItem(StructuredMenuItemHolder holder) {
				bar.add(holder.getAction());
			}

			public void addSeparator() {
				// no separators to save place. But they look good. fc,
				// 16.6.2005.
				bar.addSeparator();
			}

			// public void addAction(Action action) {
			// bar.add(action);
			// }

			public void addCategory(String category) {
			}
		}, myMap, new DefaultMenuAdderCreator());

	}

	private interface MenuAdder {
		void addMenuItem(StructuredMenuItemHolder holder);

		void addSeparator();

		// void addAction(Action action);
		void addCategory(String category);
	}

	private static class MenuItemAdder implements MenuAdder {

		/**
		 * 
		 */
		private int mAmountOfVisibleMenuItems = 20;
		private int mItemCounter = 0;
		private int mMenuCounter = 0;
		
		private JMenu mBaseMenuItem;

		private JMenu myMenuItem;

		private StructuredMenuListener listener;

		public MenuItemAdder(JMenu pMenuItem) {
			this.myMenuItem = pMenuItem;
			this.mBaseMenuItem = myMenuItem;
			mAmountOfVisibleMenuItems = Resources.getInstance().getIntProperty(AMOUNT_OF_VISIBLE_MENU_ITEMS, 20);
			listener = new StructuredMenuListener();
			pMenuItem.addMenuListener(listener);
		}

		public void addMenuItem(StructuredMenuItemHolder holder) {
			mItemCounter++;
			if(mItemCounter > mAmountOfVisibleMenuItems) {
				String label = Resources.getInstance().getResourceString("StructuredMenuHolder.next");
				if(mMenuCounter > 0) {
					label += " " + mMenuCounter;
				}
				JMenu jMenu = new JMenu(label);
				mBaseMenuItem.add(jMenu);
				myMenuItem = jMenu;
				mItemCounter = 0;
				mMenuCounter++;
			}
			Tools.setLabelAndMnemonic(holder.getMenuItem(), null);
			JMenuItem item = holder.getMenuItem();
			adjustMenuItem(item);
			listener.addItem(holder);
			myMenuItem.add(item);
		}

		public void addSeparator() {
			if (lastItemIsASeparator(myMenuItem)) {
				return;
			}
			myMenuItem.addSeparator();
		}

		// public void addAction(Action action) {
		// myItem.add(action);
		// }

		public void addCategory(String category) {
		}
	}

	/**
     */
	static private void adjustMenuItem(JMenuItem item) {
		if (item.getIcon() == null) {
			item.setIcon(blindIcon);
		} else {
			// align
			if (item.getIcon().getIconWidth() < ICON_SIZE) {
				item.setIconTextGap(item.getIconTextGap()
						+ (ICON_SIZE - item.getIcon().getIconWidth()));
			}
		}
	}

	private interface MenuAdderCreator {
		MenuAdder createAdder(JMenu baseObject);
	}

	private class DefaultMenuAdderCreator implements MenuAdderCreator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.StructuredMenuHolder.MenuAdderCreator#createAdder
		 * (javax.swing.JMenu)
		 */
		public MenuAdder createAdder(JMenu baseObject) {
			return new MenuItemAdder(baseObject);
		}
	}

	private class SeparatorHolder {
		public SeparatorHolder() {
		}
	}

	private void updateMenus(MenuAdder menuAdder, Map thisMap,
			MenuAdderCreator factory) {
		// System.out.println(thisMap);
		// iterate through maps and do the changes:
		Vector myVector = (Vector) thisMap.get(ORDER_NAME);
		for (Iterator i = myVector.iterator(); i.hasNext();) {
			String category = (String) i.next();
			// The "." target was handled earlier.
			if (category.equals("."))
				continue;
			Object nextObject = thisMap.get(category);
			if (nextObject instanceof SeparatorHolder) {
				menuAdder.addSeparator();
				continue;
			}
			if (nextObject instanceof StructuredMenuItemHolder) {
				StructuredMenuItemHolder holder = (StructuredMenuItemHolder) nextObject;
				menuAdder.addMenuItem(holder);
			}/*
			 * if(nextObject instanceof JMenuItem) {
			 * menuAdder.addMenuItem((JMenuItem) nextObject); }
			 *//*
				 * else if(nextObject instanceof Action){
				 * menuAdder.addAction((Action) nextObject); }
				 */else if (nextObject instanceof Map) {
				menuAdder.addCategory(category);
				Map nextMap = (Map) nextObject;
				MenuAdder nextItem;
				if (nextMap.containsKey(".")) {
					// add this item to the current place:
					JMenu baseObject = (JMenu) nextMap.get(".");
					StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
					holder.setMenuItem(baseObject);
					menuAdder.addMenuItem(holder);
					nextItem = factory.createAdder(baseObject);
				} else {
					nextItem = menuAdder;
				}
				mIndent++;
				updateMenus(nextItem, nextMap, factory);
				mIndent--;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		mIndent = 0;
		mOutputString = "";
		updateMenus(new PrintMenuAdder(), menuMap, new PrintMenuAdderCreator());

		return mOutputString;
	}

	private class PrintMenuAdderCreator implements MenuAdderCreator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.StructuredMenuHolder.MenuAdderCreator#createAdder
		 * (javax.swing.JMenu)
		 */
		public MenuAdder createAdder(JMenu baseObject) {
			return new PrintMenuAdder();
		}
	}

	private void print(String string) {
		for (int i = 0; i < mIndent; ++i) {
			mOutputString += ("  ");
		}
		mOutputString += (string) + "\n";
	}

	public interface MenuEventSupplier {
		void addMenuListener(MenuListener listener);

		void removeMenuListener(MenuListener listener);

	}

	public static class StructuredMenuListener implements
			javax.swing.event.MenuListener {
		private Vector menuItemHolder = new Vector();

		public StructuredMenuListener() {
		}

		public void menuSelected(MenuEvent arg0) {
//			System.out.println("Selected menu items " + arg0);
			for (Iterator i = menuItemHolder.iterator(); i.hasNext();) {
				StructuredMenuItemHolder holder = (StructuredMenuItemHolder) i
						.next();
				Action action = holder.getAction();
				boolean isEnabled = false;
				JMenuItem menuItem = holder.getMenuItem();
				if (holder.getEnabledListener() != null) {
					try {
						isEnabled = holder.getEnabledListener().isEnabled(
								menuItem, action);
					} catch (Exception e) {
						Resources.getInstance().logException(e);
					}
					action.setEnabled(isEnabled);
//					menuItem.setEnabled(isEnabled);
				}
				isEnabled = menuItem.isEnabled();
				if (isEnabled && holder.getSelectionListener() != null) {
					boolean selected = false;
					try {
						selected = holder.getSelectionListener().isSelected(
								menuItem, action);
					} catch (Exception e) {
						Resources.getInstance().logException(e);
					}
					if (menuItem instanceof JCheckBoxMenuItem) {
						JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) menuItem;
						checkItem.setSelected(selected);
					} else {
						// Do icon change if not a check box menu!
						setSelected(menuItem, selected);
					}
				}
			}
		}

		public void menuDeselected(MenuEvent arg0) {
		}

		public void menuCanceled(MenuEvent arg0) {
		}

		public void addItem(StructuredMenuItemHolder holder) {
			menuItemHolder.add(holder);
		}
	}

	public static boolean lastItemIsASeparator(JMenu menu) {
		if (menu.getItemCount() > 0) {
			if (menu.getMenuComponents()[menu.getItemCount() - 1] instanceof JSeparator) {
				// no separator, if the last was such.
				return true;
			}
		}
		return false;
	}

	public static boolean lastItemIsASeparator(JPopupMenu menu) {
		if (menu.getComponentCount() > 0) {
			if (menu.getComponent(menu.getComponentCount() - 1) instanceof JPopupMenu.Separator) {
				// no separator, if the last was such.
				return true;
			}
		}
		return false;
	}

	private static void setSelected(JMenuItem menuItem, boolean state) {
		if (state) {
			menuItem.setIcon(sSelectedIcon);
		} else {
			Icon normalIcon = (Icon) menuItem.getAction().getValue(
					Action.SMALL_ICON);
			if (normalIcon == null) {
				normalIcon = blindIcon;
			}
			menuItem.setIcon(normalIcon);
		}
	}

}
