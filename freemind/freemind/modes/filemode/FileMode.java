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


package freemind.modes.filemode;

import javax.swing.JToolBar;

import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.ModeController;

public class FileMode extends Mode {

	private Controller c;
	private FileController modecontroller;
	private JToolBar toolbar;
	private static final String MODENAME = "File";
	private static boolean isRunning = false;

	public FileMode() {
	}

	public void init(Controller c) {
		this.c = c;
		modecontroller = new FileController(this);
		toolbar = new FileToolBar(modecontroller);
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public String toString() {
		return MODENAME;
	}

	/**
	 * Called whenever this mode is chosen in the program. (updates Actions
	 * etc.)
	 */
	public void activate() {
		getDefaultModeController().newMap();
		c.getMapModuleManager().changeToMapOfMode(this);
		if (!isRunning) {
			isRunning = true;
		} else {
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

	public ModeController createModeController() {
		return new FileController(this);
	}

}
