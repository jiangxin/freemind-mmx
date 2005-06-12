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
/*$Id: SchemeNodeModel.java,v 1.8.28.1 2005-06-12 12:59:55 dpolivaev Exp $*/

package freemind.modes.schememode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMap;
import freemind.modes.NodeAdapter;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class SchemeNodeModel extends NodeAdapter {

    //
    //  Constructors
    //

    public SchemeNodeModel(FreeMindMain frame, MindMap map) {
	super(frame, map);
	children = new LinkedList();
	setEdge(new SchemeEdgeModel(this,getFrame()));
    }

    public String toString() {
	if (this.isRoot()) {
	    return "Scheme";
	} else {
	    String ret = super.toString();
	    if (ret == "no text") {
		ret = " ";
	    }
	    return ret;
	}
    }

    public String getCodeMathStyle() {
	String code="";
	if (this.isRoot()) {
	    ListIterator it = childrenUnfolded();
	    if (it != null) {
		while (it.hasNext()) {
		    code = code +  ((SchemeNodeModel)it.next()).getCodeMathStyle();
		}
	    }
	} else {
	    code = toString().trim()+" ";
	    if (getChildCount() > 0) {
		code = "(" + code;
		ListIterator it = childrenUnfolded();
		if (it != null) {
		    while (it.hasNext()) {
			code = code + ((SchemeNodeModel)it.next()).getCodeMathStyle();
		    }
		}
		code = code +")";		
	    }
	}
	return code;
    }

    public String getCodeClassicStyle() {
	String code="";
	if (this.isRoot()) {
	    ListIterator it = childrenUnfolded();
	    if (it != null) {
		while (it.hasNext()) {
		    code = code + ((SchemeNodeModel)it.next()).getCodeClassicStyle() + ",";
		}
	    }
	} else {
	    code = toString().trim();
	    if(code.equals("")) {
		code = "(";
		ListIterator it = childrenUnfolded();
		if (it != null) {
		    while (it.hasNext()) {
			code = code + ((SchemeNodeModel)it.next()).getCodeClassicStyle() + " ";
		    }
		}
		code = code +")";
	    }
	}
	return code;
    }
}
