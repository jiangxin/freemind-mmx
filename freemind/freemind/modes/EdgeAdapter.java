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
/*$Id: EdgeAdapter.java,v 1.4 2000-10-17 17:20:28 ponder Exp $*/

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.Tools;
import java.awt.Color;

public abstract class EdgeAdapter implements MindMapEdge {

    private MindMapNode target;
    
    //recursive attributes. may be accessed directly by the save() method.
    protected Color color; 
    protected String style;

    //
    // Constructors
    //
    public EdgeAdapter(MindMapNode target) {
	this.target = target;
    }

    //
    // Attributes
    //

    public Color getColor() {
	if(color==null) {
	    if (getTarget().isRoot()) {
		String stdcolor = FreeMind.userProps.getProperty("standardedgecolor");
		if (stdcolor.length() == 7) {
		    return Tools.xmlToColor(stdcolor);
		}
		return Color.blue;
	    }
	    return getSource().getEdge().getColor();
	}
	return color;
    }

    protected void setColor(Color color) {
	this.color = color;
    }

    public String getStyle() {
	if(style==null) {
	    if (getTarget().isRoot()) {
		return FreeMind.userProps.getProperty("standardedgestyle");
	    }
	    return getSource().getEdge().getStyle();
	}
	return style;
    }

    protected void setStyle(String style) {
	this.style = style;
    }

    public String toString() {
	return "";
    }

    ///////////
    // Private Methods
    /////////

    private MindMapNode getTarget() {
  	return target;
    }
    
    private MindMapNode getSource() {
 	return (MindMapNode)target.getParent();
    }
}
