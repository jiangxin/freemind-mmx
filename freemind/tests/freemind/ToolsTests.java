/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package tests.freemind;

import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import freemind.main.Tools;

/**
 * @author foltin
 * @date 30.06.2011
 */
public class ToolsTests extends FreeMindTestBase {

	/* (non-Javadoc)
	 * @see tests.freemind.FreeMindTestBase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testArgsToUrlConversion() throws Exception {
		String[] args = new String[]{"/home/bla", "--quiet", "c:\\test.mm"};
		String arrayToUrls = Tools.arrayToUrls(args);
		Vector urlVector = Tools.urlStringToUrls(arrayToUrls);
		assertEquals(args.length, urlVector.size());
		for (Iterator it = urlVector.iterator(); it.hasNext();) {
			URL urli = (URL) it.next();
			System.out.println(urli);
		}
	}

}
