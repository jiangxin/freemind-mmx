/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/* WindowClosingAdapter.java */

package accessories.plugins.util.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowClosingAdapter extends WindowAdapter {
	private boolean exitSystem;

	/**
	 * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Ist
	 * exitSystem true, wird das komplette Programm beendet.
	 */
	public WindowClosingAdapter(boolean exitSystem) {
		this.exitSystem = exitSystem;
	}

	/**
	 * Erzeugt einen WindowClosingAdapter zum Schliessen des Fensters. Das
	 * Programm wird nicht beendet.
	 */
	public WindowClosingAdapter() {
		this(false);
	}

	public void windowClosing(WindowEvent event) {
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		if (exitSystem) {
			System.exit(0);
		}
	}

}