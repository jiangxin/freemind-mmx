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
/*$Id: ArrowLinkAdapter.java,v 1.4 2003-12-07 21:00:19 christianfoltin Exp $*/

package freemind.modes;
import freemind.modes.LinkAdapter;
import freemind.main.FreeMindMain;

import java.awt.Point;

public abstract class ArrowLinkAdapter extends LinkAdapter implements MindMapArrowLink {

    /** the zero is the start point of the line;*/
    protected Point startInclination; 
    /** the zero is the start point of the line;*/
    protected Point endInclination; 
    protected String startArrow; 
    protected String endArrow;


    public ArrowLinkAdapter(MindMapNode source,MindMapNode target,FreeMindMain frame)  {
        super(source, target, frame, "standardlinkcolor", "standardlinkstyle");
        startArrow = "None";
        endArrow = "Default";
    }

    public Point getStartInclination() { return startInclination; }
    public Point getEndInclination() { return endInclination; } 
    public String getStartArrow() { return startArrow; } 
    public String getEndArrow() { return endArrow; }

    public void  setStartInclination(Point startInclination) {  this.startInclination=startInclination; }
    public void  setEndInclination(Point endInclination) {  this.endInclination=endInclination; } 
    public void  setStartArrow(String startArrow) {  
        if(startArrow == null || startArrow.toUpperCase().equals("NONE")) {
            this.startArrow = "None";
            return;
        } else if(startArrow.toUpperCase().equals("DEFAULT")) {
            this.startArrow = "Default";
            return;
        }
        // dont change:
        System.err.println("Cannot set the start arrow type to " + startArrow);
    } 
    public void  setEndArrow(String endArrow) {  
        if(endArrow == null || endArrow.toUpperCase().equals("NONE")) {
            this.endArrow = "None";
            return;
        } else if(endArrow.toUpperCase().equals("DEFAULT")) {
            this.endArrow = "Default";
            return;
        }
        // dont change:
        System.err.println("Cannot set the end arrow type to " + endArrow);
    }

    public Object clone() {
        ArrowLinkAdapter arrowLink = (ArrowLinkAdapter) super.clone();
        // now replace the points:
        arrowLink.startInclination = (startInclination==null)?null:new Point(startInclination.x, startInclination.y);
        arrowLink.endInclination = (endInclination==null)?null:new Point(endInclination.x, endInclination.y);
        arrowLink.startArrow = (startArrow==null)?null:new String(startArrow);
        arrowLink.endArrow = (endArrow==null)?null:new String(endArrow);
        return arrowLink;
    }

}
