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
/*$Id: BrowseArrowLinkModel.java,v 1.1.14.1 2004-05-23 10:44:44 dpolivaev Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMapNode;
import freemind.modes.ArrowLinkAdapter;
import freemind.main.Tools;
import java.awt.Color;

import freemind.main.XMLElement;

public class BrowseArrowLinkModel extends ArrowLinkAdapter {

    public BrowseArrowLinkModel(MindMapNode source,MindMapNode target,FreeMindMain frame) {
        super(source,target,frame);
    }

    /* maybe this method is wrong here, but ...*/
    public Object clone() {
        return super.clone();
    }

    public XMLElement save() {
        return null;
    }

    public String toString() { return "Source="+getSource()+", target="+getTarget(); }

    /**
     * @see freemind.modes.MindMapArrowLink#changeInclination(int, int, int, int)
     */
    public void changeInclination(int oldX, int oldY, int deltaX, int deltaY) {
        
    }

}
