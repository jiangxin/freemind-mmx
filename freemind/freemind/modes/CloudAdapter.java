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
/*$Id: CloudAdapter.java,v 1.1.12.2 2004-10-24 06:54:00 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Color;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;

public abstract class CloudAdapter extends LineAdapter implements MindMapCloud {

    //
    // Constructors
    //
    public CloudAdapter(MindMapNode target,FreeMindMain frame) {
        this(target, frame, "standardcloudcolor", "standardcloudstyle");
    }

    /** For derived classes.*/
    protected  CloudAdapter(MindMapNode target,FreeMindMain frame, String standardColorPropertyString, String standardStylePropertyString)  {
        super(target, frame, standardColorPropertyString, standardStylePropertyString);
        NORMAL_WIDTH = 3;
    }

    public Color getExteriorColor() {
        return getColor().darker();
    }

    public XMLElement save() {
        XMLElement cloud = new XMLElement();
        cloud.setName("cloud");
    
        if (style != null) {
            cloud.setAttribute("STYLE",style);
        }
        if (color != null) {
            cloud.setAttribute("COLOR",Tools.colorToXml(color));
        }
        if(width != DEFAULT_WIDTH) {
            cloud.setAttribute("WIDTH",Integer.toString(width));
        }
        return cloud;
    }

}
