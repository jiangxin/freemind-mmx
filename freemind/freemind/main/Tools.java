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
/*$Id: Tools.java,v 1.10 2001-04-22 15:02:50 ponder Exp $*/

package freemind.main;
//maybe move this class to another package like tools or something...

import java.io.File;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.net.URL;
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

    /**
     * Converts a String in the format "value;value;value" to 
     * a List with the values (as strings)
     */
    public static List stringToList(String string) {
	StringTokenizer tok = new StringTokenizer(string,";");
	List list = new LinkedList();
	while (tok.hasMoreTokens()) {
	    list.add(tok.nextToken());
	}
	return list;
    }

    public static String listToString(List list) {
	ListIterator it = list.listIterator(0);
	String str = new String();
	while (it.hasNext()) {
	    str.concat(it.next().toString() + ";");
	}
	return str;
    }

    /**
     * Replaces a ~ in a filename with the users home directory
     */
    public static String expandFileName(String file) {
	//replace ~ with the users home dir
	if (file.startsWith("~")) {
	    file = System.getProperty("user.home") + file.substring(1);
	}
	return file;
    }

    public static Vector getAllFonts() {
	GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	String envFonts[] = gEnv.getAvailableFontFamilyNames();
	Vector vector = new Vector();
	for( int i=0;i<envFonts.length;i++ ) {
	    vector.addElement(envFonts[i]);
	}
	return vector;
    }

    public static boolean isValidFont(String font) {
	return getAllFonts().contains(font);
    }

    public static String getExtension(String s) {
	String ext = null;
	int i = s.lastIndexOf('.');

	if (i>0 && i<s.length()-1) {
	    ext = s.substring(i+1).toLowerCase();
	}
	if (ext==null) ext="";
	return ext.trim();
    }

    /**
     * This method converts an absolute url to an url relative to a given base-url.
     * The algorithm is somewhat chaotic, but it works (Maybe rewrite it). 
     * Be careful, the method is ".mm"-specific. Something like this should be included
     * in the librarys, but I couldn't find it. You can create a new absolute url with
     * "new URL(URL context, URL relative)".
     */
    public static String toRelativeURL(URL base, URL target) {
	if( (base.getProtocol().equals(target.getProtocol())) &&
	    (base.getHost().equals(target.getHost()))) {

	    String baseString = base.getFile();
	    String targetString = target.getFile();
	    String result = "";

	    if (baseString.endsWith(".mm")) {
		//remove filename from URL
		baseString = baseString.substring(0, baseString.lastIndexOf("/")+1);
	    }

	    if (targetString.endsWith(".mm")) {
		//remove filename from URL
		targetString = targetString.substring(0, targetString.lastIndexOf("/")+1);
	    }
	    
	    StringTokenizer baseTokens = new StringTokenizer(baseString,"/");//Maybe this causes problems under windows
	    StringTokenizer targetTokens = new StringTokenizer(targetString,"/");//Maybe this causes problems under windows

	    String nextBaseToken = "", nextTargetToken = "";

	    //Algorithm

	    while(baseTokens.hasMoreTokens() && targetTokens.hasMoreTokens()) {
		nextBaseToken = baseTokens.nextToken();
		nextTargetToken = targetTokens.nextToken();
		if (!(nextBaseToken.equals(nextTargetToken))) {
		    while(true) {
			result = result.concat("../");
			if (!baseTokens.hasMoreTokens()) {
			    break;
			}
			nextBaseToken = baseTokens.nextToken();
		    }
		    while(true) {
			result = result.concat(nextTargetToken+"/");
			if (!targetTokens.hasMoreTokens()) {
			    break;
			}
			nextTargetToken = targetTokens.nextToken();
		    }
		    String temp = target.getFile();
		    result = result.concat(temp.substring(temp.lastIndexOf("/")+1,temp.length()));
		    return result;
		}
	    }

	    while(baseTokens.hasMoreTokens()) {
		result = result.concat("../");
		baseTokens.nextToken();
	    }

	    while(targetTokens.hasMoreTokens()) {
		nextTargetToken = targetTokens.nextToken();
		result = result.concat(nextTargetToken + "/");
	    }

	    String temp = target.getFile();
	    result = result.concat(temp.substring(temp.lastIndexOf("/")+1,temp.length()));
	    return result;
	}
	return target.toString();
    }
}

