/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008 Christian Foltin and others.
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
 * Created on 22.03.2008
 */
/*$Id: Base64Tests.java,v 1.1.2.2 2008/04/17 19:32:30 christianfoltin Exp $*/

package tests.freemind;

import freemind.main.Base64Coding;

/**
 * @author foltin
 * 
 */
public class Base64Tests extends FreeMindTestBase {
	public void testDifferentBase64ers() throws Exception {
		String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String output = Base64Coding.encode64(input.getBytes());
		System.out.println(output);
		// byte[] output2 = CommonsCodecBase64.encodeBase64(input.getBytes());
		// String string2 = new String(output2);
		// System.out.println(string2);
		String expected = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWg==";
		assertEquals(expected, output);

		String back = new String(Base64Coding.decode64(output));
		assertEquals(input, back);
	}

}
