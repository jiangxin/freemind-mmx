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


package freemind.modes.mindmapmode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import freemind.controller.Controller;
import freemind.main.XMLParseException;
import freemind.modes.Mode;
import freemind.modes.ModeController;

public class MindMapMode extends Mode {

	private Controller c;
	private MindMapController modecontroller;
	private final String MODENAME = "MindMap";
	private boolean isRunning = false;
	private static Logger logger = null;

	public MindMapMode() {
	}

	public void init(Controller c) {
		this.c = c;
		if (logger == null) {
			logger = c.getFrame().getLogger(this.getClass().getName());
		}
		modecontroller = (MindMapController) createModeController();
	}

	public ModeController createModeController() {
		logger.finest("Creating new MindMapController...");
		MindMapController mindMapController = new MindMapController(this);
		logger.finest("Creating new MindMapController. Done:"
				+ mindMapController);
		return mindMapController;
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

	public void restore(String restoreable) throws FileNotFoundException,
			XMLParseException, MalformedURLException, IOException,
			URISyntaxException {
		getDefaultModeController().load(new File(restoreable));
	}

	public Controller getController() {
		return c;
	}

	public ModeController getDefaultModeController() {
		return modecontroller;
	}

}
