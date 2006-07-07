/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 * Created on 06.07.2006
 */
/*$Id: FreeMindStarter.java,v 1.1.2.1 2006-07-07 04:26:26 christianfoltin Exp $*/
package freemind.main;

import javax.swing.JOptionPane;

/**
 * @author foltin
 * 
 */
public class FreeMindStarter {

	public static final String JAVA_VERSION = System
			.getProperty("java.version");

	public static void checkJavaVersion() {
		System.out.println("Checking Java Version...");
		if (JAVA_VERSION.compareTo("1.4.0") < 0) {
			String message = "Warning: FreeMind requires version Java 1.4.0 or higher (your version: "
					+ System.getProperty("java.version")
					+ ", installed in "
					+ System.getProperty("java.home") + ").";
			System.err.println(message);
			JOptionPane.showMessageDialog(null, message, "FreeMind",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		// First check version of Java
		FreeMindStarter.checkJavaVersion();
		FreeMind.main(args);
	}
}
