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

package freemind.modes.mindmapmode;

import freemind.modes.MindMapNode;
import freemind.modes.EdgeAdapter;
import freemind.main.Tools;
import java.awt.Color;

import  org.w3c.dom.Document;
import org.w3c.dom.Element;


public class MindMapEdgeModel extends EdgeAdapter {

    public MindMapEdgeModel(MindMapNode node) {
	super(node);
    }

    public void save(Document doc, Element xmlParent) {
	if (style!=null || color!=null) {
	    Element edge = doc.createElement("edge");
	    xmlParent.appendChild(edge);
	    if (style != null) {
		edge.setAttribute("style",style);
	    }
	    if (color != null) {
		edge.setAttribute("color",Tools.colorToXml(color));
	    }
	}
    }

    public void load(Element edge) {
	if (!edge.getAttribute("style").equals("")) {
	    setStyle(edge.getAttribute("style"));
	}
	if (!edge.getAttribute("color").equals("")) {
	    setColor(Tools.xmlToColor(edge.getAttribute("color") ) );
	}
    }

    public void setColor(Color color) {
	super.setColor(color);
    }

    public void setStyle(String style) {
	super.setStyle(style);
    }
}
