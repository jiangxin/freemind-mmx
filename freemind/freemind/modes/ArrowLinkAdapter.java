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
/*$Id: ArrowLinkAdapter.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.modes;
import freemind.modes.LinkAdapter;
import freemind.main.FreeMindMain;

import java.awt.Point;

public abstract class ArrowLinkAdapter extends LinkAdapter implements MindMapArrowLink {

    /** the zero is the start point of the line;*/
    protected Point startInclination; 
    /** the zero is the start point of the line;*/
    protected Point endInclination; 
    protected boolean startHasArrow; 
    protected boolean endHasArrow;


    public ArrowLinkAdapter(MindMapNode target,FreeMindMain frame)  {
        super(target, frame, "standardlinkcolor", "standardlinkstyle");
        startHasArrow = false;
        endHasArrow = true;
    }

    public Point getStartInclination() { return startInclination; }
    public Point getEndInclination() { return endInclination; } 
    public boolean startHasArrow() { return startHasArrow; } 
    public boolean endHasArrow() { return endHasArrow; }

    public void  setStartInclination(Point startInclination) {  this.startInclination=startInclination; }
    public void  setEndInclination(Point endInclination) {  this.endInclination=endInclination; } 
    public void  setStartArrow(boolean startHasArrow) {  this.startHasArrow=startHasArrow; } 
    public void  setEndArrow(boolean endHasArrow) {  this.endHasArrow=endHasArrow; }


}
