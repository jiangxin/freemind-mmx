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
/*$Id: LastOpenedList.java,v 1.8.18.2.2.2 2008/04/11 16:58:31 christianfoltin Exp $*/
package freemind.controller;

import freemind.main.XMLParseException;
import freemind.view.MapModule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This class manages a list of the maps that were opened last.
 * It aims to provide persistence for the last recent maps.
 * Maps should be shown in the format:"mode\:key",ie."Mindmap\:/home/joerg/freemind.mm"
 */
public class LastOpenedList {
    private Controller c;
    private int maxEntries = 25; // is rewritten from property anyway
    /**
     * Contains Restore strings.
     */
    private List lastOpenedList = new LinkedList();
    /**
     * Contains Restore string => map name (map.toString()).
     */
    private Map mRestorableToMapName = new HashMap();

    LastOpenedList(Controller c, String restored) {
        this.c=c;
        maxEntries = new Integer(c.getFrame().getProperty("last_opened_list_length")).intValue();
	load(restored);
    }

    void mapOpened(MapModule mapModule) {
	if (mapModule==null || mapModule.getModel()==null) return;
	String restoreString = mapModule.getModel().getRestoreable();
	if (restoreString==null) return;
	if (lastOpenedList.contains(restoreString)) {
	    lastOpenedList.remove(restoreString);
	}
	lastOpenedList.add(0,restoreString);
	mRestorableToMapName.put(restoreString,mapModule.toString());

	while (lastOpenedList.size()>maxEntries) {
	    lastOpenedList.remove(lastOpenedList.size()-1); //remove last elt
	}
    }

    void mapClosed(MapModule map) {
	//	hash.remove(map.getModel().getRestoreable());
	//not needed
    }

    /** fc, 8.8.2004: This method returns a string representation of this class. */
    String save() {
 	String str = new String();
	for(ListIterator it=listIterator();it.hasNext();) {
	    str=str.concat((String)it.next()+";");
	}
	return str;
    }

    /**
     * 
     */
    void load(String data) {
        // Take care that there are no ";" in restorable names!
	if (data != null) {
	    StringTokenizer token = new StringTokenizer(data,";");
	    while (token.hasMoreTokens())
		lastOpenedList.add(token.nextToken());
	}
    }

    public void open(String restoreable) throws FileNotFoundException, XMLParseException, MalformedURLException, IOException, URISyntaxException {
		boolean changedToMapModule = c.getMapModuleManager()
				.tryToChangeToMapModule(
						(String) mRestorableToMapName.get(restoreable));
		if ((restoreable != null) && !(changedToMapModule)) {
			StringTokenizer token = new StringTokenizer(restoreable, ":");
			if (token.hasMoreTokens()) {
				String mode = token.nextToken();
				if (c.createNewMode(mode)) {
					// fix for windows (??, fc, 25.11.2005).
					String fileName = token.nextToken("").substring(1);
					c.getMode().restore(fileName);
				}
			}
		}
	}
	
    ListIterator listIterator () {
	return lastOpenedList.listIterator();
    }
}
