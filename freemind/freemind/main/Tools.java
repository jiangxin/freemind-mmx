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
/*$Id: Tools.java,v 1.11 2003-11-03 10:15:45 sviles Exp $*/

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
import javax.swing.JOptionPane;

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

    /**
     * Returns the lowercase of the extension of a file. Example: getExtension("fork.pork.MM") == "mm"
     */
    public static String getExtension(File f) {
       return getExtension(f.toString()); }

    /**
     * Returns the lowercase of the extension of a file name. Example: getExtension("fork.pork.MM") == "mm"
     */
    public static String getExtension(String s) {
	int i = s.lastIndexOf('.');
        return (i>0 && i<s.length()-1) ? s.substring(i+1).toLowerCase().trim() :  "";
    }

    public static String removeExtension(String s) {
        int i = s.lastIndexOf('.');
	return (i>0 && i<s.length()-1) ? s.substring(0,i) : "";
    }

    public static String toXMLEscapedText (String text) {
       return text.
          replaceAll("&","&amp;").
          replaceAll("<","&lt;").
          replaceAll(">","&gt;").
          replaceAll("\"","&quot;");
    }
   public static String toXMLUnescapedText (String text) {
      return text.
         replaceAll("&lt;","<").
         replaceAll("&gt;",">").
         replaceAll("&quot;","\"").
         replaceAll("&amp;","&");
    }

    public static boolean isAbsolutePath(String path) {
       // On Windows, we cannot just ask if the file name starts with file separator.
       // If path contains ":" at the second position, then it is not relative, I guess.
       // However, if it starts with separator, then it is absolute too.
     
       // Possible problems: Not tested on Macintosh, but should work.

       String osNameStart = System.getProperty("os.name").substring(0,3);
       String fileSeparator = System.getProperty("file.separator");
       if (osNameStart.equals("Win")) {
          return path.substring(1,2).equals(":") || path.startsWith(fileSeparator+fileSeparator);
       } else if (osNameStart.equals("Mac")) {
          return !path.startsWith(fileSeparator);
       } else {
          return path.startsWith(fileSeparator);
       }
    }

    /**
     * This is a correction of a method getFile of a class URL.  Namely, on Windows it
     * returned file paths like /C: etc., which are not valid on Windows. This correction
     * is heuristic to a great extend. One of the reasons is, that file:// is basically no
     * protocol at all, but rather something every browser and every system uses slightly
     * differently.
     */
    public static String urlGetFile(URL url) {
       String osNameStart = System.getProperty("os.name").substring(0,3);
       String fileSeparator = System.getProperty("file.separator");
       if (osNameStart.equals("Win") && url.getProtocol().equals("file")) {
          String fileName = url.getFile();
          
          if (fileName.startsWith(fileSeparator) && fileName.substring(2,3).equals(":")) {
             fileName = fileName.substring(1); }
          if (!url.getHost().equals("")) {
             fileName = "//" + url.getHost() + fileName; }
          // ^ This condition is necessary for URLs like
          // "file://winnthost/folder/file.txt".  In a situation like that, the getFile()
          // returns "/folder/file.txt" and getHost() returns "winnthost".
          return fileName; }
       else {
          return url.getFile(); }}

    /**
     * This method converts an absolute url to an url relative to a given base-url.
     * The algorithm is somewhat chaotic, but it works (Maybe rewrite it). 
     * Be careful, the method is ".mm"-specific. Something like this should be included
     * in the librarys, but I couldn't find it. You can create a new absolute url with
     * "new URL(URL context, URL relative)".
     */
    public static String toRelativeURL(URL base, URL target) {
        // Precondition: If URL is a path to folder, then it must end with '/' character. 
	if( (base.getProtocol().equals(target.getProtocol())) &&
	    (base.getHost().equals(target.getHost()))) {

	    String baseString = base.getFile();
	    String targetString = target.getFile();
	    String result = "";

		//remove filename from URL
		baseString = baseString.substring(0, baseString.lastIndexOf("/")+1);

		//remove filename from URL
		targetString = targetString.substring(0, targetString.lastIndexOf("/")+1);

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
   public static void errorMessage(Object message) {
      JOptionPane.showMessageDialog(null, message.toString(), "FreeMind", JOptionPane.ERROR_MESSAGE); }

}

