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
 * Created on 29.08.2004
 */


package plugins.help;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import freemind.extensions.ModeControllerHookAdapter;

/**
 * @author foltin
 * 
 */
public class FreemindHelpStarter extends ModeControllerHookAdapter {

	/**
     *
     */

	public void startupMapHook() {
		super.startupMapHook();
		// Find the HelpSet file and create the HelpSet object:
		String helpHS = "plugins/help/doc/freemind.hs";
		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			URL hsURL = HelpSet.findHelpSet(classLoader, helpHS);
			HelpSet hs = new HelpSet(classLoader, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			hb.initPresentation();
			hb.setDisplayed(true);
			hb.setViewDisplayed(true);
		} catch (Exception ee) {
			// Say what the exception really is
			freemind.main.Resources.getInstance().logException(ee);
			logger.warning("HelpSet " + ee.getMessage() + ee);
			logger.warning("HelpSet " + helpHS + " not found");
			return;
		}
	}
}
