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
/*$Id: MindMapArrowLinkModel.java,v 1.1 2003-11-09 22:09:26 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMapNode;
import freemind.modes.ArrowLinkAdapter;
import freemind.main.Tools;
import java.awt.Color;

import freemind.main.XMLElement;

public class MindMapArrowLinkModel extends ArrowLinkAdapter {

    public MindMapArrowLinkModel(MindMapNode node,FreeMindMain frame) {
        super(node,frame);
    }

    public XMLElement save() {
	    XMLElement arrowLink = new XMLElement();
	    arrowLink.setName("arrowlink");

	    if (style != null) {
            arrowLink.setAttribute("style",style);
	    }
	    if (color != null) {
            arrowLink.setAttribute("color",Tools.colorToXml(color));
	    }
        if(getDestinationLabel() != null) {
            arrowLink.setAttribute("destination",getDestinationLabel());
        }
        if(getReferenceText() != null) {
            arrowLink.setAttribute("referenceText",getReferenceText());
        }
        if(getStartInclination() != null) {
            arrowLink.setAttribute("startInclination",getStartInclination().toString());
        }
        if(getEndInclination() != null) {
            arrowLink.setAttribute("endInclination",getEndInclination().toString());
        }
        arrowLink.setAttribute("startHasArrow",Boolean.toString(startHasArrow()));
        arrowLink.setAttribute("endHasArrow",Boolean.toString(endHasArrow()));
	    return arrowLink;
    }

    public String toString() { return save().toString(); }

}
