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
/*$Id: MindMapNodeModel.java,v 1.8 2001-03-13 15:50:06 ponder Exp $*/

package freemind.modes.mindmapmode;

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
// //XML Definition (Interfaces)
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Node;
import freemind.main.XMLElement;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class MindMapNodeModel extends NodeAdapter {
	
    //
    //  Constructors
    //

    public MindMapNodeModel(FreeMindMain frame) {
	super(frame);
	children = new LinkedList();
	setEdge(new MindMapEdgeModel(this,getFrame()));
    }

	    
    public MindMapNodeModel( Object userObject, FreeMindMain frame ) {
	super(userObject,frame);
	children = new LinkedList();
	setEdge(new MindMapEdgeModel(this,getFrame()));
    }

//     //
//     // set Methods for Attributes. They are simply set through the ModeController
//     //

//     void setStyle(String style) {
// 	super.style = style;
//     }

//     void setColor(Color color) {
// 	super.color = color;
//     }

//     void setBold(boolean bold) {
// 	// ** use font object
// 	// super.bold = bold;
// 	if(bold && font.isBold()) return;
// 	if(!bold && !font.isBold()) return;

// 	if(bold) setFont(font.deriveFont(font.getStyle()+Font.BOLD));
// 	if(!bold) setFont(font.deriveFont(font.getStyle()-Font.BOLD));
//     }

//     void setItalic(boolean italic) {
// 	// ** use font object
// 	// super.italic = italic;

// 	if(italic && font.isItalic()) return;
// 	if(!italic && !font.isItalic()) return;

// 	if(italic) setFont(font.deriveFont(font.getStyle()+Font.ITALIC));
// 	if(!italic) setFont(font.deriveFont(font.getStyle()-Font.ITALIC));
//     }

//     void setUnderlined(boolean underlined) {
// 	super.underlined = underlined;
//     }

//     void setFont(Font font) {
// 	this.font = font;
//     }
    
//      void setFontSize(int fontSize) {
//  	super.fontSize = fontSize;
//      }

//      void setFont(String font) {
//  	super.font = font;
//      }

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

    /*
      XERCES SAVE Method
    public void save(Document doc, Element xmlParent) {
	Element node = doc.createElement( "node" );
	xmlParent.appendChild(node);
	((MindMapEdgeModel)getEdge()).save(doc,node);
	node.setAttribute("text",this.toString());

	if (isFolded()) {
	    node.setAttribute("folded","true");
	}
	
	if (color != null) {
	    node.setAttribute("color", Tools.colorToXml(getColor()));
	}

	if (style != null) {
	    node.setAttribute("style", getStyle());
	}

	//link
	if (getLink() != null) {
	    node.setAttribute("link", getLink());
	}

	//font
	if (font!=null || font.getSize()!=0 || isBold() || isItalic() || isUnderlined() ) {
	    Element fontElement = doc.createElement( "font" );
	    if (font != null) {
		fontElement.setAttribute("name",getFont().getFontName());
	    }
	    if (font.getSize() != 0) {
		fontElement.setAttribute("size",Integer.toString(getFont().getSize()));
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
	for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
	    MindMapNodeModel child = (MindMapNodeModel)e.next();
	    child.save(doc, node);
	}
    }
    */

    //NanoXML save method
    public XMLElement save() {
	XMLElement node = new XMLElement();
	node.setTagName("node");

	node.addProperty("text",this.toString());

	//	((MindMapEdgeModel)getEdge()).save(doc,node);

	XMLElement edge = ((MindMapEdgeModel)getEdge()).save();
	if (edge != null) {
	    node.addChild(edge);
	}

	if (isFolded()) {
	    node.addProperty("folded","true");
	}
	
	if (color != null) {
	    node.addProperty("color", Tools.colorToXml(getColor()));
	}

	if (style != null) {
	    node.addProperty("style", getStyle());
	}

	//link
	if (getLink() != null) {
	    node.addProperty("link", getLink());
	}

	//font
	if (font!=null || font.getSize()!=0 || isBold() || isItalic() || isUnderlined() ) {
	    XMLElement fontElement = new XMLElement();
	    fontElement.setTagName("font");

	    if (font != null) {
		fontElement.addProperty("name",getFont().getFontName());
	    }
	    if (font.getSize() != 0) {
		fontElement.addProperty("size",Integer.toString(getFont().getSize()));
	    }
	    if (isBold()) {
		fontElement.addProperty("bold","true");
	    }
	    if (isItalic()) {
		fontElement.addProperty("italic","true");
	    }
	    if (isUnderlined()) {
		fontElement.addProperty("underline","true");
	    }
	    node.addChild(fontElement);
	}

	//recursive
	for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
	    MindMapNodeModel child = (MindMapNodeModel)e.next();
	    node.addChild(child.save());
	}
	return node;
    }


    /*
    public void load(Node node_) {
	Element node = (Element)node_;
	setUserObject( node.getAttribute("text") );

	if (node.getAttribute("folded").equals("true")) {
	    setFolded(true);
	}
	if (node.getAttribute("color").length() == 7) {
	    setColor(Tools.xmlToColor(node.getAttribute("color") ) );
	}
	if (!node.getAttribute("style").equals("")) {
	    setStyle(node.getAttribute("style"));
	}
	if (!node.getAttribute("link").equals("")) {
	    setLink(node.getAttribute("link"));
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
		int style=0;
		int size=0;

		if (!Tools.isValidFont(name)) {
		    name = "Sans Serif";
		}

		if (font.getAttribute("bold").equals("true")) style+=Font.BOLD;;
		if (font.getAttribute("italic").equals("true")) style+=Font.ITALIC;
		if (font.getAttribute("underline").equals("true")) setUnderlined(true);

		if (font.getAttribute("size")!="") {
		    size = Integer.parseInt(font.getAttribute("size"));
		    // getFont().setSize(Integer.parseInt(font.getAttribute("size")));
		}

		setFont(new Font(name, style, size));

	    }
	    //node
	    if ( ((Node)childNodes.item(i)).getNodeName().equals("node")) {
		MindMapNodeModel child = new MindMapNodeModel(getFrame());
		insert(child,getChildCount());
		child.load( (Node)childNodes.item(i) );//recursive
	    }
	}
    }
    */

    public void load(XMLElement node) {
	setUserObject( node.getProperty("text") );


	if (node.getProperty("folded")!=null && node.getProperty("folded").equals("true")) {
	    setFolded(true);
	}
	if (node.getProperty("color")!=null && node.getProperty("color").length() == 7) {
	    setColor(Tools.xmlToColor(node.getProperty("color") ) );
	}
	if (node.getProperty("style")!=null) {
	    setStyle(node.getProperty("style"));
	}
	if (node.getProperty("link")!=null) {
	    setLink(node.getProperty("link"));
	}
	Vector childNodes = node.getChildren();
	for(int i=0; i < childNodes.size(); i++) {
	    //this has to be improved!
	    //edge
	    if ( ((XMLElement)childNodes.elementAt(i)).getTagName().equals("edge")) {
		XMLElement edge = (XMLElement)childNodes.elementAt(i);
		setEdge(new MindMapEdgeModel(this,getFrame()) );
		((MindMapEdgeModel)getEdge()).load(edge);
	    }
	    //font
	    if ( ((XMLElement)childNodes.elementAt(i)).getTagName().equals("font")) {
		XMLElement font =((XMLElement)childNodes.elementAt(i));
		String name = font.getProperty("name"); 
		int style=0;
		int size=0;

		if (!Tools.isValidFont(name)) {
		    name = "Sans Serif";
		}

		if (font.getProperty("bold")!=null && font.getProperty("bold").equals("true")) style+=Font.BOLD;;
		if (font.getProperty("italic")!=null && font.getProperty("italic").equals("true")) style+=Font.ITALIC;
		if (font.getProperty("underline")!=null && font.getProperty("underline").equals("true")) setUnderlined(true);

		if (font.getProperty("size")!=null) {
		    size = Integer.parseInt(font.getProperty("size"));
		    // getFont().setSize(Integer.parseInt(font.getProperty("size")));
		}

		setFont(new Font(name, style, size));

	    }
	    //node
	    if ( ((XMLElement)childNodes.elementAt(i)).getTagName().equals("node")) {
		MindMapNodeModel child = new MindMapNodeModel(getFrame());
		insert(child,getChildCount());
		child.load( (XMLElement)childNodes.elementAt(i) );//recursive
	    }
	}
    }
}





