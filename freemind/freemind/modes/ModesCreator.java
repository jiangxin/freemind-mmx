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
/*$Id: ModesCreator.java,v 1.9.18.1 2004-11-16 16:42:36 christianfoltin Exp $*/

package freemind.modes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;

import freemind.controller.Controller;

/**
 * This class creates all the modes that are available. To add your own mode,
 * simply import it, and create it in getAllModes() (just do the same whats done
 * with MindMapMode). Thats all!
 */
public class ModesCreator {
	private Controller c;

	private Map modes;

	private Map modesTranslation;

	private static Logger logger;
	
	public ModesCreator(Controller c) {
		this.c = c;
	}

	public Set getAllModes() {
		if(logger==null) {
			logger = c.getFrame().getLogger(this.getClass().getName());
		}
		if (modes == null) {
			modes = new TreeMap();
			modesTranslation = new HashMap();
			String modestring = c.getFrame().getProperty("modes");

			StringTokenizer tokens = new StringTokenizer(modestring, ",");

			while (tokens.hasMoreTokens()) {
				String modename = tokens.nextToken();
				String modeAlias = tokens.nextToken();
				modes.put(modename, null);
				modesTranslation.put(modeAlias, modename);
			}
			logger.info("Modes:" + modes.keySet());
		}
		return modesTranslation.keySet();
	}

	public Mode getMode(String modeAlias) {
		getAllModes();
		Mode mode = null;
		if (!modesTranslation.containsKey(modeAlias)) {
			throw new IllegalArgumentException("Unknown mode " + modeAlias);
		}
		String modeName = (String) modesTranslation.get(modeAlias);
		if (modes.get(modeName) == null) {
			try {
				mode = (Mode) Class.forName(modeName).newInstance();
				logger.info("Initializing mode "+ modeAlias );
				mode.init(c);
				logger.info("Done: Initializing mode "+ modeAlias );
				modes.put(modeName, mode);
			} catch (Exception ex) {
				logger.severe("Mode " + modeName + " could not be loaded.");
				ex.printStackTrace();
			}
		}
		return (Mode) modes.get(modeName);
	}

}