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
/*$Id: MindMapArrowLink.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.modes;

import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import java.awt.Point;

public interface MindMapArrowLink extends MindMapLink {

    //     public Color getColor();
    //     public String getStyle();
    //     public Stroke getStroke();
    //     public int getWidth();
    //     public String toString();

//     public String getDestinationLabel();
//     public String getReferenceText();
    /* for arrows: */
    public Point getStartInclination(); // the zero is the start point of the line;
    public Point getEndInclination(); // the zero is the end point of the line;
    public boolean startHasArrow(); 
    public boolean endHasArrow();

}
