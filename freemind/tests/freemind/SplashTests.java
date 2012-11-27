/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2007  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: SplashTests.java,v 1.1.2.3 2008/04/19 22:29:23 christianfoltin Exp $*/

package tests.freemind;

import freemind.main.FreeMindSplashModern;
import freemind.main.IFreeMindSplash;

/** */
public class SplashTests extends FreeMindTestBase {
	public void testLightBuldSplash() throws InterruptedException {
		IFreeMindSplash splash = new FreeMindSplashModern(mFreeMindMain);
		splash.setVisible(true);
		splash.getFeedBack().setMaximumValue(11);
		for (int i = 0; i < 10; i++) {
			splash.getFeedBack().increase("test: " + i, null);
			Thread.sleep(1000l);
		}
	}
}

// private static java.util.logging.Logger logger =
// freemind.main.Resources.getInstance().getLogger(SplashTests.class.getName());
