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
/*$Id: BrowseNodeModel.java,v 1.7.18.1.10.1 2005-06-12 12:59:55 dpolivaev Exp $*/

package freemind.modes.browsemode;

import java.util.LinkedList;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class BrowseNodeModel extends NodeAdapter {
	
    //
    //  Constructors
    //

    public BrowseNodeModel(FreeMindMain frame, MindMap map) {
	super(frame, map);
	children = new LinkedList();
	setEdge(new BrowseEdgeModel(this,getFrame())); }
	    
    public BrowseNodeModel( Object userObject, FreeMindMain frame, MindMap map ) {
	super(userObject, frame, map);
	children = new LinkedList();
	setEdge(new BrowseEdgeModel(this,getFrame())); }

    //
    // The mandatory load and save methods
    //

    //NanoXML save method
    public XMLElement save() {
	return null;
    }
}
