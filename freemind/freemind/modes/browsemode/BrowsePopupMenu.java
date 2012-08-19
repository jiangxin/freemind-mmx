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
/*$Id: BrowsePopupMenu.java,v 1.4.34.2 2007/08/05 10:29:06 dpolivaev Exp $*/

package freemind.modes.browsemode;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class BrowsePopupMenu extends JPopupMenu implements PopupMenuListener {

	private BrowseController c;

	protected void add(Action action, String keystroke) {
		JMenuItem item = add(action);
		item.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(keystroke)));
	}

	public BrowsePopupMenu(BrowseController c) {
		this.c = c;
		add(c.find, "keystroke_find");
		add(c.findNext, "keystroke_find_next");
		add(c.followLink, "keystroke_follow_link");

		addSeparator();

		add(c.toggleFolded, "keystroke_toggle_folded");
		add(c.toggleChildrenFolded, "keystroke_toggle_children_folded");
		addSeparator();
		add(c.followMapLink, "keystroke_follow_map_link");
		addPopupMenuListener(this);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent pE) {
		c.followMapLink.setEnabled(c.followMapLink.isEnabled(null, null));
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent pE) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
	 */
	public void popupMenuCanceled(PopupMenuEvent pE) {
	}
	
}
