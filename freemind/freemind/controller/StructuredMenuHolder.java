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
/*$Id: StructuredMenuHolder.java,v 1.1.2.1 2004-05-21 21:49:11 christianfoltin Exp $*/

package freemind.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author foltin
 *
 */
public class StructuredMenuHolder {

	private static final String SEPARATOR_TEXT = "000";
    private static final String ORDER_NAME = "/order";
	Map menuMap; 

	public StructuredMenuHolder() {
		menuMap = new HashMap();
		Vector order = new Vector();
		menuMap.put(ORDER_NAME, order); 
	}
	/**
	 * @param item
	 * @param category
	 * @return
	 */
	public JMenu addMenu(JMenu item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		return (JMenu) addMenu(item, tokens);
	}

	/**
	 * @param item
	 * @param category
	 * @return
	 */
	public JMenuItem addMenuItem(JMenuItem item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		return (JMenuItem) addMenu(item, tokens);
	}

	/**
	 * @param item
	 * @param category
	 * @return
	 */
	public JMenuItem addAction(Action item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		return (JMenuItem) addMenu(new JMenuItem(item), tokens);
	}

	public void addCategory(String category) {
		StringTokenizer tokens = new StringTokenizer(category+"/blank", "/");
		addMenu(null, tokens);
		StringTokenizer tokensII = new StringTokenizer(category+"/blank", "/");
		removeItem(tokensII);
	}

	public void addSeparator(String category) {
		addMenu((JMenu)null, category+"/"+SEPARATOR_TEXT);
	}
	/**
	 * @param item
	 * @param category
	 * @param menuMap
	 */
	private Object addMenu(Object item, StringTokenizer tokens) {
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		categoryPair.map.put(categoryPair.token, item);
		return item;
	}

	private void removeItem(StringTokenizer tokens) {
		MapTokenPair categoryPair = getCategoryMap(tokens, menuMap);
		categoryPair.map.remove(categoryPair.token);
	}

	private class MapTokenPair {
		Map map;
		String token;
		MapTokenPair(Map map, String token) {
			this.map = map;
			this.token = token;
		}
	}

	private MapTokenPair getCategoryMap(StringTokenizer tokens, Map thisMap) {
		if (tokens.hasMoreTokens()) {
			String nextToken = tokens.nextToken();
			if(tokens.hasMoreTokens()) {
				if(!thisMap.containsKey(nextToken)) {
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
				Vector order = (Vector)thisMap.get(ORDER_NAME);
				order.add(nextToken);
				return new MapTokenPair(thisMap, nextToken);
			}
		}
		// error case?
		return null;
	}

    
    public void updateMenus(final JMenuBar myItem) {
    	updateMenus(new MenuAdder() {

            public void addMenuItem(JMenuItem item) {
            	myItem.add(item);
            }

            public void addSeparator() {
				throw new NoSuchMethodError("addSeparator for JMenuBar");
            }

            public void addAction(Action action) {
				throw new NoSuchMethodError("addAction for JMenuBar");
            }}, menuMap);
    }

	public void updateMenus(final JPopupMenu myItem) {
		updateMenus(new MenuAdder() {

            public void addMenuItem(JMenuItem item) {
            	myItem.add(item);
            }

            public void addSeparator() {
            	myItem.addSeparator();
            }

            public void addAction(Action action) {
            	myItem.add(action);
            }}, menuMap);
	}
	
	private interface MenuAdder {
		void addMenuItem(JMenuItem item);
		void addSeparator();
		void addAction(Action action);
	}
	
	private static class MenuItemAdder implements MenuAdder {
		private JMenu myItem;


        public MenuItemAdder(JMenu myItem) {
			this.myItem = myItem;
		}


        public void addMenuItem(JMenuItem item) {
        	myItem.add(item);
        }

        public void addSeparator() {
        	myItem.addSeparator();
        }

        public void addAction(Action action) {
        	myItem.add(action);
        } 
	}
    
	private void updateMenus(MenuAdder menuAdder, Map thisMap) {
		//System.out.println(thisMap);
		// iterate through maps and do the changes:
		Vector myVector = (Vector) thisMap.get(ORDER_NAME);
		// The "." target was handled earlier.
		myVector.remove("."); 
		for (Iterator i = myVector.iterator(); i.hasNext();) {
			String category = (String) i.next();
			Object nextObject = thisMap.get(category);
			if(nextObject == null ) {
				menuAdder.addSeparator();
				continue;
			}
			if(nextObject instanceof JMenuItem) {
				menuAdder.addMenuItem((JMenuItem) nextObject);
			}  else if(nextObject instanceof Action){
				menuAdder.addAction((Action) nextObject);
			} else if( nextObject instanceof Map) {
				Map nextMap = (Map) nextObject;
				MenuAdder nextItem ;
				if(nextMap.containsKey(".")) {
					// add this item to the current place:
					JMenu baseObject = (JMenu) nextMap.get(".");
					menuAdder.addMenuItem(baseObject);
					nextItem = new MenuItemAdder(baseObject);
				} else {
					nextItem = menuAdder;					
				}
				updateMenus(nextItem, nextMap);
			}
		}
	}


}
