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
/*$Id: SchemeNodeModel.java,v 1.1 2000-11-15 22:27:20 ponder Exp $*/

package freemind.modes.schememode;

import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.File;
import java.awt.Color;


/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class SchemeNodeModel extends NodeAdapter {

    private Color color;
	
    //
    //  Constructors
    //

    public SchemeNodeModel() {
	super();
	children = new LinkedList();
	setEdge(new SchemeEdgeModel(this));
    }

    public String getCode() {
	String code = toString().trim();
	if(code.equals("")) {
	    code = "(";
	    ListIterator it = childrenUnfolded();
	    if (it != null) {
		while (it.hasNext()) {
		    code.concat(((SchemeNodeModel)it.next()).getCode() + " ");
		}
	    }
	    code.concat(")");
	}
	return code;
    }
}
