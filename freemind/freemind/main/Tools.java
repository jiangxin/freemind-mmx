/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.main;
//maybe move this class to another Package like tools or something...

import java.io.File;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class Tools {
    
    public static String colorToXml(Color col) {
	if (col == null) throw new IllegalArgumentException("Color was null");
	String red = Integer.toHexString(col.getRed());
	if (col.getRed()<16) red = "0"+red;
	String green = Integer.toHexString(col.getGreen());
	if (col.getGreen()<16) green = "0"+green;
	String blue = Integer.toHexString(col.getBlue());
	if (col.getBlue()<16) blue = "0"+blue;
	return "#"+red+green+blue;
    }

    public static Color xmlToColor(String string) {
	int red = Integer.parseInt(string.substring(1,3),16);
	int green = Integer.parseInt(string.substring(3,5),16);
	int blue = Integer.parseInt(string.substring(5,7),16);
	return new Color(red,green,blue);
    }

    public static Vector getAllFonts() {
	GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	String envFonts[] = gEnv.getAvailableFontFamilyNames();
	Vector vector = new Vector();
	for( int i=1;i<envFonts.length;i++ ) {
	    vector.addElement(envFonts[i]);
	}
	return vector;
    }

    public static boolean isValidFont(String font) {
	return getAllFonts().contains(font);
    }

    public static String getExtension(File f) {
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf('.');

	if (i>0 && i<s.length()-1) {
	    ext = s.substring(i+1).toLowerCase();
	}
	if (ext==null) ext="";
	return ext;
    }
}

