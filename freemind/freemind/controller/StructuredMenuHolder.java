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
/*$Id: StructuredMenuHolder.java,v 1.1.4.6.6.1 2005-07-12 15:41:13 dpolivaev Exp $*/

package freemind.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * @author foltin
 *
 */
public class StructuredMenuHolder {

//	 Logging: 
	//private static java.util.logging.Logger logger = getFrame().getLogger(this.getClass().getName());
	
	public static final int ICON_SIZE = 16;
    private String mOutputString;
	private static Icon blindIcon = new BlindIcon(ICON_SIZE);


    private static final String SEPARATOR_TEXT = "000";
    private static final String ORDER_NAME = "/order";
	Map menuMap; 

	private int mIndent;
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
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item.getAction());
		holder.setMenuItem(item);
		addMenu(holder, tokens);
		return item;
	}

	/**
	 * @param item
	 * @param category
	 * @return
	 */
	public JMenuItem addAction(Action item, String category) {
		StringTokenizer tokens = new StringTokenizer(category, "/");
		StructuredMenuItemHolder holder = new StructuredMenuItemHolder();
		holder.setAction(item);
		holder.setMenuItem(new JMenuItem(item));
		addMenu(holder, tokens);
		return holder.getMenuItem();
	}

	public void addCategory(String category) {
		StringTokenizer tokens = new StringTokenizer(category+"/blank", "/");
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
	 * @param item
	 * @param category
	 * @param menuMap
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
        	print("JMenuItem '"+holder.getMenuItem().getActionCommand()+"'");
        }
        public void addSeparator() {
        	print("Separator '"+"'");
        }
//        public void addAction(Action action) {
//        	print("Action    '"+action.getValue(Action.NAME)+"'");
//        }
        public void addCategory(String category) {
        	print("Category: '"+category+"'");
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
				return new MapTokenPair(thisMap, nextToken, order);
			}
		}
		// error case?
		return null;
	}

    
    public void updateMenus(final JMenuBar myItem, String prefix) {
    	
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
    	updateMenus(new MenuAdder() {

            public void addMenuItem(StructuredMenuItemHolder holder) {
            	myItem.add(holder.getMenuItem());
            }

            public void addSeparator() {
				throw new NoSuchMethodError("addSeparator for JMenuBar");
            }

//            public void addAction(Action action) {
//				throw new NoSuchMethodError("addAction for JMenuBar");
//            }

            public void addCategory(String category) {
            }}, myMap, new DefaultMenuAdderCreator());
    }

	public void updateMenus(final JPopupMenu myItem, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			StructuredMenuListener listener = new StructuredMenuListener();
			
            public void addMenuItem(StructuredMenuItemHolder holder) {
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
                if(lastItemIsASeparator(myItem))
                    return;
            	myItem.addSeparator();
            }

//            public void addAction(Action action) {
//            	myItem.add(action);
//            }

            public void addCategory(String category) {
            }}, myMap, new DefaultMenuAdderCreator());
	}
	
	/**
	 * @param bar
	 */
	public void updateMenus(final JToolBar bar, String prefix) {
		MapTokenPair pair = getCategoryMap(new StringTokenizer(prefix, "/"), menuMap);
		Map myMap = (Map) pair.map.get(pair.token);
		updateMenus(new MenuAdder() {

			public void addMenuItem(StructuredMenuItemHolder holder) {
				bar.add(holder.getAction());
			}

			public void addSeparator() {
				// no separators to save place. But they look good. fc, 16.6.2005.
				bar.addSeparator();
			}

//			public void addAction(Action action) {
//				bar.add(action);
//			}

            public void addCategory(String category) {
            }}, myMap, new DefaultMenuAdderCreator());
	}


	
	private interface MenuAdder {
		void addMenuItem(StructuredMenuItemHolder holder);
		void addSeparator();
//		void addAction(Action action);
		void addCategory(String category);
	}
	
	private static class MenuItemAdder implements MenuAdder {

		private JMenu myItem;

		private StructuredMenuListener listener;

		public MenuItemAdder(JMenu myItem) {
			this.myItem = myItem;
			listener = new StructuredMenuListener();
			myItem.addMenuListener(listener);
		}

		public void addMenuItem(StructuredMenuItemHolder holder) {
			JMenuItem item = holder.getMenuItem();
			adjustMenuItem(item);
			listener.addItem(holder);
			myItem.add(item);
		}

        public void addSeparator() {
		    if(lastItemIsASeparator(myItem)) {
		        return;
		    }
			myItem.addSeparator();
		}

        //        public void addAction(Action action) {
		//        	myItem.add(action);
		//        }

		public void addCategory(String category) {
		}
	}
    
	/**
     * @param item
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

        /* (non-Javadoc)
         * @see freemind.controller.StructuredMenuHolder.MenuAdderCreator#createAdder(javax.swing.JMenu)
         */
        public MenuAdder createAdder(JMenu baseObject) {
            return new MenuItemAdder(baseObject);
        }
	}

	private class SeparatorHolder {
		public SeparatorHolder() {
		}
	}
    
	private void updateMenus(MenuAdder menuAdder, Map thisMap, MenuAdderCreator factory) {
		//System.out.println(thisMap);
		// iterate through maps and do the changes:
		Vector myVector = (Vector) thisMap.get(ORDER_NAME);
		for (Iterator i = myVector.iterator(); i.hasNext();) {
			String category = (String) i.next();
			// The "." target was handled earlier.
			if(category.equals("."))
				continue;
			Object nextObject = thisMap.get(category);
			if(nextObject instanceof SeparatorHolder ) {
				menuAdder.addSeparator();
				continue;
			}
			if (nextObject instanceof StructuredMenuItemHolder) {
				StructuredMenuItemHolder holder = (StructuredMenuItemHolder) nextObject;
				menuAdder.addMenuItem(holder);
			}/*if(nextObject instanceof JMenuItem) {
				menuAdder.addMenuItem((JMenuItem) nextObject);
			} */ /*else if(nextObject instanceof Action){
				menuAdder.addAction((Action) nextObject);
			} */ else if( nextObject instanceof Map) {
				menuAdder.addCategory(category);
				Map nextMap = (Map) nextObject;
				MenuAdder nextItem ;
				if(nextMap.containsKey(".")) {
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

	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        mIndent = 0;
        mOutputString = "";
		updateMenus(new PrintMenuAdder(), menuMap, new PrintMenuAdderCreator());
    	
        return mOutputString;
    }

	private class PrintMenuAdderCreator implements MenuAdderCreator {

		/* (non-Javadoc)
		 * @see freemind.controller.StructuredMenuHolder.MenuAdderCreator#createAdder(javax.swing.JMenu)
		 */
		public MenuAdder createAdder(JMenu baseObject) {
			return new PrintMenuAdder();
		}
	}
    


	private void print(String string) {
		for(int i=0; i < mIndent; ++i) {
			mOutputString+=("  ");
		}
		mOutputString += (string)+"\n";
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
			//System.out.println("Selected menu items " + item);
			for (Iterator i = menuItemHolder.iterator(); i.hasNext();) {
				StructuredMenuItemHolder holder = (StructuredMenuItemHolder) i.next();
				if(holder.getEnabledListener() != null) {
					boolean isEnabled = false;
                    try {
                        isEnabled = holder.getEnabledListener().isEnabled(
                                holder.getMenuItem(), holder.getAction());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    holder.getMenuItem().setEnabled(isEnabled);
				}
				if(holder.getSelectedListener() != null) {
					if (holder.getMenuItem() instanceof JCheckBoxMenuItem) {
						JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) holder.getMenuItem();
						checkItem.setSelected(holder.getSelectedListener().isSelected(checkItem, holder.getAction()));
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
	    if(menu.getItemCount() >0){
	        if(menu.getMenuComponents()[menu.getItemCount()-1] instanceof JSeparator) {
	            // no separator, if the last was such.
	            return true;
	        }
	    }
        return false;
    }

    public static boolean lastItemIsASeparator(JPopupMenu menu) {
	    if(menu.getComponentCount() >0){
	        if(menu.getComponent(menu.getComponentCount()-1) instanceof JPopupMenu.Separator) {
	            // no separator, if the last was such.
	            return true;
	        }
	    }
        return false;
    }



}
