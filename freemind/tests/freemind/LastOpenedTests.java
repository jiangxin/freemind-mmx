/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import java.io.File;

import freemind.controller.Controller;
import freemind.controller.LastOpenedList;

/**
 * @author foltin
 * @date 27.08.2013
 */
public class LastOpenedTests extends FreeMindTestBase {
	public void testStrangeCharsInList() throws Exception {
		LastOpenedList list = new LastOpenedList(new Controller(getFrame()), null);
		String name = "test.mm";
		String file = "/home/user/tmp&tmp/" +name;
		System.out.println(new File(file).getAbsoluteFile());
		String restorable = "MindMap:";
		list.add(restorable+file, name);
		assertEquals(restorable + file + ";", list.save());
	}
}
