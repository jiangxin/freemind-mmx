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
/*$Id: BrowseNodeModel.java,v 1.5 2003-11-03 10:39:52 sviles Exp $*/

package freemind.modes.browsemode;

import freemind.main.Tools;
import freemind.main.FreeMindMain;
import freemind.modes.NodeAdapter;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
import java.awt.Font;

import freemind.main.XMLElement;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class BrowseNodeModel extends NodeAdapter {
	
    //
    //  Constructors
    //

    public BrowseNodeModel(FreeMindMain frame) {
	super(frame);
	children = new LinkedList();
	setEdge(new BrowseEdgeModel(this,getFrame())); }
	    
    public BrowseNodeModel( Object userObject, FreeMindMain frame ) {
	super(userObject,frame);
	children = new LinkedList();
	setEdge(new BrowseEdgeModel(this,getFrame())); }

    //Overwritten get Methods
    public String getStyle() {
       return isFolded() ? "bubble" : super.getStyle(); }

    public boolean isLong() {
       return toString().length() > 100; }

    //
    // The mandatory load and save methods
    //

    //NanoXML save method
    public XMLElement save() {
	return null;
    }
}
