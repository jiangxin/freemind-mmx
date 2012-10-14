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


package freemind.modes.browsemode;

import java.io.File;

import freemind.controller.Controller;
import freemind.modes.Mode;
import freemind.modes.ModeController;

public class BrowseMode extends Mode {

	private Controller c;
	private BrowseController modecontroller;
	public final static String MODENAME = "Browse";
	private boolean isRunning = false;

	public BrowseMode() {
	}

	public void init(Controller c) {
		this.c = c;
		modecontroller = new BrowseController(this);
	}

	public String toString() {
		return MODENAME;
	}

	/**
	 * Called whenever this mode is chosen in the program. (updates Actions
	 * etc.)
	 */
	public void activate() {
		if (isRunning) {
			c.getMapModuleManager().changeToMapOfMode(this);
		} else {
			isRunning = true;
		}

	}

	public void restore(String restoreable) {
		try {
			getDefaultModeController().load(new File(restoreable));
		} catch (Exception e) {
			c.errorMessage("An error occured on opening the file: "
					+ restoreable + ".");
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public Controller getController() {
		return c;
	}

	public ModeController getDefaultModeController() {
		// no url should be visible for the empty controller.
		modecontroller.getToolBar().setURLField("");
		return modecontroller;
	}

	public ModeController createModeController() {
		return new BrowseController(this);
	}

}
