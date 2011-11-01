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
/*$Id: FilePopupMenu.java,v 1.8.34.1 2006/12/16 20:42:31 dpolivaev Exp $*/

package freemind.modes.filemode;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class FilePopupMenu extends JPopupMenu {

	private FileController c;

	protected void add(Action action, String keystroke) {
		JMenuItem item = add(action);
		item.setAccelerator(KeyStroke.getKeyStroke(c.getFrame()
				.getAdjustableProperty(keystroke)));
	}

	public FilePopupMenu(FileController c) {
		this.c = c;

		// Node menu
		this.add(c.center);
		this.addSeparator();
		this.add(c.find, "keystroke_find");
		this.add(c.findNext, "keystroke_find_next");

	}
}
