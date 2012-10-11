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


package freemind.modes.schememode;

import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.main.FreeMindMain;
import freemind.modes.Mode;
import freemind.modes.ModeController;

public class SchemeMode extends Mode {

	private Controller c;
	private SchemeController modecontroller;
	private JToolBar toolbar;
	private static final String MODENAME = "Scheme";
	private static boolean isRunning = false;

	public SchemeMode() {
	}

	public void init(Controller c) {
		this.c = c;
		modecontroller = new SchemeController(this);
		toolbar = new SchemeToolBar(modecontroller);
	}

	public String toString() {
		return MODENAME;
	}

	/**
	 * Called whenever this mode is chosen in the program. (updates Actions
	 * etc.)
	 */
	public void activate() {
		if (!isRunning) {
			getDefaultModeController().newMap();
			isRunning = true;
		} else {
			c.getMapModuleManager().changeToMapOfMode(this);
		}
	}

	public void restore(String restoreable) {
	}

	public Controller getController() {
		return c;
	}

	public ModeController getDefaultModeController() {
		return modecontroller;
	}

	public FreeMindMain getFrame() {
		return c.getFrame();
	}

	public ModeController createModeController() {
		return new SchemeController(this);
	}

}
