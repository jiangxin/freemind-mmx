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
/*$Id: MindMapNodeModel.java,v 1.3 2000-08-11 10:22:38 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.Tools;
import freemind.modes.NodeAdapter;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
// //XML Definition (Interfaces)
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class MindMapNodeModel extends NodeAdapter {
	
    //
    //  Constructors
    //

    public MindMapNodeModel() {
	super();
	setEdge(new MindMapEdgeModel(this));
	children = new Vector();
    }

	    
    public MindMapNodeModel( Object userObject ) {
	super(userObject);
	setEdge(new MindMapEdgeModel(this));
	children = new Vector();
    }

    //
    // set Methods for Attributes. They are simply set through the ModeController
    //

    void setStyle(String style) {
	super.style = style;
    }

    void setColor(Color color) {
	super.color = color;
    }

    void setBold(boolean bold) {
	super.bold = bold;
    }

    void setItalic(boolean italic) {
	super.italic = italic;
    }

    void setUnderlined(boolean underlined) {
	super.underlined = underlined;
    }

    void setFontSize(int fontSize) {
	super.fontSize = fontSize;
    }
    
    void setFont(String font) {
	super.font = font;
    }

    //Overwritten get Methods
    public String getStyle() {
	if(isFolded()) {
	    return "bubble";
	} else {
	    return super.getStyle();
	}
    }

    //
    // The mandatory load and save methods
    //

    public void save(Document doc, Element xmlParent) {
	Element node = doc.createElement( "node" );
	xmlParent.appendChild(node);
	((MindMapEdgeModel)getEdge()).save(doc,node);
	node.setAttribute("text",this.toString());
	
	if (color != null) {
	    node.setAttribute("color", Tools.colorToXml(getColor()));
	}

	if (style != null) {
	    node.setAttribute("style", getStyle());
	}

	//link
	if (getLink() != null) {
	    node.setAttribute("link", getLink().toExternalForm());
	}

	//font
	if (font!=null || fontSize!=0 || isBold() || isItalic() || isUnderlined() ) {
	    Element fontElement = doc.createElement( "font" );
	    if (font != null) {
		fontElement.setAttribute("name",getFont());
	    }
	    if (fontSize != 0) {
		fontElement.setAttribute("size",Integer.toString(getFontSize()));
	    }
	    if (isBold()) {
		fontElement.setAttribute("bold","true");
	    }
	    if (isItalic()) {
		fontElement.setAttribute("italic","true");
	    }
	    if (isUnderlined()) {
		fontElement.setAttribute("underline","true");
	    }
	    node.appendChild(fontElement);
	}

	//recursive
	for (Enumeration e = children(); e.hasMoreElements(); ) {
	    MindMapNodeModel child = (MindMapNodeModel)e.nextElement();
	    child.save(doc, node);
	}
    }

    public void load(Node node_) {
	Element node = (Element)node_;
	setUserObject( node.getAttribute("text") );
	if (node.getAttribute("color").length() == 7) {
	    setColor(Tools.xmlToColor(node.getAttribute("color") ) );
	}
	if (!node.getAttribute("style").equals("")) {
	    setStyle(node.getAttribute("style"));
	}
	if (!node.getAttribute("link").equals("")) {
	    try {
		setLink(new URL(node.getAttribute("link")));
	    } catch (MalformedURLException ex) {
		System.err.println("Malformed URL: "+node.getAttribute("link"));
	    }
	}
	NodeList childNodes = node.getChildNodes();
	for(int i=0; i < childNodes.getLength(); i++) {
	    //this has to be improved!
	    //edge
	    if ( ((Node)childNodes.item(i)).getNodeName().equals("edge")) {
		Element edge = (Element)childNodes.item(i);
		setEdge(new MindMapEdgeModel(this) );
		((MindMapEdgeModel)getEdge()).load(edge);
	    }
	    //font
	    if ( ((Node)childNodes.item(i)).getNodeName().equals("font")) {
		Element font =((Element)childNodes.item(i));
		String name = font.getAttribute("name"); 
		if (Tools.isValidFont(name)) {
		    setFont(name);
		}
		if (font.getAttribute("bold").equals("true")) setBold(true);
		if (font.getAttribute("italic").equals("true")) setItalic(true);
		if (font.getAttribute("underline").equals("true")) setUnderlined(true);
		if (font.getAttribute("size")!="") {
		    setFontSize(Integer.parseInt(font.getAttribute("size")));
		}
	    }
	    //node
	    if ( ((Node)childNodes.item(i)).getNodeName().equals("node")) {
		MindMapNodeModel child = new MindMapNodeModel();
		insert(child,getChildCount());
		child.load( (Node)childNodes.item(i) );//recursive
	    }
	}
    }
}





