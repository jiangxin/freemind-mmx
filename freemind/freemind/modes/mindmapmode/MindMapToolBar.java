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
/*$Id: MindMapToolBar.java,v 1.12.18.1.12.5 2009/07/04 20:38:27 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.controller.FreeMindToolBar;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.ZoomListener;
import freemind.main.Tools;

public class MindMapToolBar extends FreeMindToolBar implements ZoomListener {

	/**
	 * A combo box that doesn't fill the complete screen.
	 * See http://stackoverflow.com/questions/13345640/does-anyone-know-how-to-layout-a-jtoolbar-that-doest-move-or-re-size-any-compon
	 */
	private final class FreeMindComboBox extends JComboBox {
		private FreeMindComboBox(Vector pItems) {
			super(pItems);
		}

		public FreeMindComboBox(String[] pItems) {
			super(pItems);
		}

		public java.awt.Dimension getMaximumSize() {
			return getPreferredSize();
		}
	}

	private static final String[] sizes = { "8", "10", "12", "14", "16", "18",
			"20", "24", "28" };
	private MindMapController c;
	private JComboBox fonts, size;
	private JAutoScrollBarPane iconToolBarScrollPane;
	private JToolBar iconToolBar;
	private boolean fontSize_IgnoreChangeEvent = false;
	private boolean fontFamily_IgnoreChangeEvent = false;
	private ItemListener fontsListener;
	private ItemListener sizeListener;
	private JComboBox zoom;
	private String userDefinedZoom;

	protected static java.util.logging.Logger logger = null;
	
	public MindMapToolBar(MindMapController controller) {
		super();
		this.c = controller;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.setRollover(true);
		fonts = new FreeMindComboBox(Tools.getAvailableFontFamilyNamesAsVector());
		fonts.setFocusable(false);
		size = new FreeMindComboBox(sizes);
		size.setFocusable(false);
		iconToolBar = new FreeMindToolBar();
		iconToolBarScrollPane = new JAutoScrollBarPane(iconToolBar);
		iconToolBar.setOrientation(JToolBar.VERTICAL);
		iconToolBar.setRollover(true);
		iconToolBarScrollPane.getVerticalScrollBar().setUnitIncrement(100);
		fontsListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				// TODO: this is super-dirty, why doesn't the toolbar know the
				// model?
				if (fontFamily_IgnoreChangeEvent) {
					// fc, 27.8.2004: I don't understand, why the ignore type is
					// resetted here.
					// let's see: fontFamily_IgnoreChangeEvent = false;
					return;
				}
				fontFamily_IgnoreChangeEvent = true;
				c.fontFamily.actionPerformed((String) e.getItem());
				fontFamily_IgnoreChangeEvent = false;
			}
		};
		fonts.addItemListener(fontsListener);
		sizeListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// System.err.println("ce:"+e);
				if (e.getStateChange() != ItemEvent.SELECTED) {
					return;
				}
				// change the font size
				// TODO: this is super-dirty, why doesn't the toolbar know the
				// model?
				if (fontSize_IgnoreChangeEvent) {
					// fc, 27.8.2004: I don't understand, why the ignore type is
					// resetted here.
					// let's see: fontSize_IgnoreChangeEvent = false;
					return;
				}
				// call action:
				c.fontSize.actionPerformed((String) e.getItem());
			}
		};
		size.addItemListener(sizeListener);
		userDefinedZoom = controller.getText("user_defined_zoom");

		zoom = new FreeMindComboBox(controller.getController().getZooms());
		zoom.setSelectedItem("100%");
		zoom.addItem(userDefinedZoom);
		// Focus fix.
		zoom.setFocusable(false);
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
		if (((String) item).equals(userDefinedZoom))
			return;
		String dirty = (String) item;
		String cleaned = dirty.substring(0, dirty.length() - 1);
		// change representation ("125" to 1.25)
		float zoomValue = Float.parseFloat(cleaned) / 100F; // nothing to do...
		// remove '%' sign
		getController().setZoom(zoomValue);
	}

	protected Controller getController() {
		return c.getController();
	}
	
	public void update(StructuredMenuHolder holder) {
		this.removeAll();
		holder.updateMenus(this, "mindmapmode_toolbar/");

		add(zoom);

		fonts.setMaximumRowCount(30);
		add(fonts);

//		size.setEditor(new BasicComboBoxEditor());
//		size.setEditable(true);
		add(size);

		// button tool bar.
		iconToolBar.removeAll();
		iconToolBar.add(c.removeLastIconAction);
		iconToolBar.add(c.removeAllIconsAction);
		iconToolBar.addSeparator();
		for (int i = 0; i < c.iconActions.size(); ++i) {
			iconToolBar.add((Action) c.iconActions.get(i));
		}
	}

	// Daniel Polansky: both the following methods trigger item listeners above.
	// Those listeners obtain two events: first DESELECTED and then
	// SELECTED. Both events are to be ignored - we don't want to update
	// a node with its own font. The item listeners should react only
	// to a user change, not to our change.

	public void selectFontSize(String fontSize) // (DiPo)
	{
		fontSize_IgnoreChangeEvent = true;
		size.setSelectedItem(fontSize);
		fontSize_IgnoreChangeEvent = false;
	}

	Component getLeftToolBar() {
		return iconToolBarScrollPane;
	}

	public void selectFontName(String fontName) // (DiPo)
	{
		if (fontFamily_IgnoreChangeEvent) {
			return;
		}
		fontFamily_IgnoreChangeEvent = true;
		fonts.setEditable(true);
		fonts.setSelectedItem(fontName);
		fonts.setEditable(false);
		fontFamily_IgnoreChangeEvent = false;
	}

	void setAllActions(boolean enabled) {
		fonts.setEnabled(enabled);
		size.setEnabled(enabled);
	}

	/* (non-Javadoc)
	 * @see freemind.controller.ZoomListener#setZoom(float)
	 */
	public void setZoom(float f) {
		logger.fine("setZoomComboBox is called with " + f + ".");
		String toBeFound = getItemForZoom(f);
		for (int i = 0; i < zoom.getItemCount(); ++i) {
			if (toBeFound.equals((String) zoom.getItemAt(i))) {
				// found
				zoom.setSelectedItem(toBeFound);
				return;
			}
		}
		zoom.setSelectedItem(userDefinedZoom);
		
	}
	
	private String getItemForZoom(float f) {
		return (int) (f * 100F) + "%";
	}

	public void startup() {
		getController().registerZoomListener(this);
	}
		
	public void shutdown() {
		getController().deregisterZoomListener(this);
	}

}
