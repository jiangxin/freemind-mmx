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
/*$Id: LastOpenedList.java,v 1.3 2003-11-03 10:15:44 sviles Exp $*/
package freemind.controller;

import freemind.view.MapModule;
import java.util.StringTokenizer;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class manages a list of the maps that were opened last.
 * It aims to provide persistence for the last recent maps.
 * Maps should be showed in the format:"mode:key",ie."mindmap:/home/joerg/freemind.mm"
 */
public class LastOpenedList {
    private Controller c;
    private int entrys = 10;
    private List lst = new LinkedList();
    private Map hash = new HashMap();

    LastOpenedList(Controller c, String restored) {
	this.c=c;
	load(restored);
    }

    void mapOpened(MapModule map) {
	if (map==null || map.getModel()==null) return;
	String rest = map.getModel().getRestoreable();
	if(rest==null) return;
	if (lst.contains(rest)) {
	    lst.remove(rest);
	}
	lst.add(0,rest);
	hash.put(rest,map.toString());

	while (lst.size()>entrys) {
	    lst.remove(lst.size()-1); //remove last elt
	}
    }

    void mapClosed(MapModule map) {
	//	hash.remove(map.getModel().getRestoreable());
	//not needed
    }

    String save() {
 	String str = new String();
	for(ListIterator it=listIterator();it.hasNext();) {
	    str=str.concat((String)it.next()+";");
	}
	return str;
    }

    /**
     * Take care that there are no ";" in restorable names!
     */
    void load(String data) {
	if(data!=null) {
	    StringTokenizer token = new StringTokenizer(data,";");
	    while (token.hasMoreTokens())
		lst.add(0,token.nextToken());
	}
    }

    public void open(String restoreable) {
	if( (restoreable != null) & 
            !(c.getMapModuleManager().tryToChangeToMapModule((String)hash.get(restoreable)))) {
           StringTokenizer token = new StringTokenizer(restoreable,":");
           if (token.hasMoreTokens()) {
              String mode = token.nextToken();
              if(c.changeToMode(mode)) {
                 c.getMode().restore(token.nextToken("").substring(1));//fix for windows
              }
           }
	}
    }
	
    ListIterator listIterator () {
	return lst.listIterator();
    }
}
