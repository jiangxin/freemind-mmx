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

import java.awt.Color;
import freemind.modes.MindMapEdge;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.NodeView;
import freemind.main.Tools;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import freemind.main.FreeMind;
//XML Definition (Interfaces)
import  org.w3c.dom.Document;
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

	


















//     private Object userObject;
//     private URL link;//Change this to vector in future for full graph support
//     private String style;
//     private Color color;
//     private MindMapNodeVector children;
//     private MutableTreeNode parent;
//     private EdgeModel edge;//the edge which leads to this node, only root has none
//     //In future it has to hold more than one view, maybe with a Vector in which the index specifies
//     //the MapView which contains the NodeViews
//     private NodeView viewer;
//     private static final boolean ALLOWSCHILDREN = true;
//     private static final boolean ISLEAF = false; //all nodes may have children
//     private boolean bold;
//     private boolean italic;
//     private boolean underlined;
//     private int fontSize;
//     private String font;
	
//     /**The Foreground/Font Color*/
//     public Color getColor() {
// 	if(color==null) {
// 	    if(this.isRoot()) {
// 		String stdcolor = FreeMind.userProps.getProperty("standardnodecolor");
// 		if (stdcolor.length() == 7) {
// 		    return Tools.xmlToColor(stdcolor);
// 		}
// 		return Color.blue;
// 	    }
// 	    return ( (MindMapNode)getParent() ).getColor();
// 	}
// 	return color;
//     }

    //
    // Interface MindMapNode
    //

    //
    // get/set methods
    //

//     public NodeView getViewer() {
// 	return viewer;
//     }

//     public void setViewer( NodeView viewer ) {
// 	this.viewer = viewer;
//     }

//     /** Creates the TreePath recursively */
//     public TreePath getPath() {
// 	Vector pathVector = new Vector();
// 	TreePath treePath;
// 	this.addToPathVector( pathVector );
// 	treePath = new TreePath( pathVector.toArray() );
// 	return treePath;
//     }

//     public MindMapEdge getEdge() {
// 	return edge;
//     }

//     public void setEdge( EdgeModel edge ) {
// 	this.edge = edge;
//     }

//     /**A Node-Style like "fork" or "bubble"*/
//     public String getStyle() {
// 	if(style==null) {
// 	    if(this.isRoot()) {
// 		return FreeMind.userProps.getProperty("standardnodestyle");
// 	    }
// 	    return ( (MindMapNode)getParent() ).getStyle();
// 	}
// 	return style;
//     }
//     public boolean isBold() {
// 	return bold;
//     }
//     public boolean isItalic() {
// 	return italic;
//     }
//     public boolean isUnderlined() {
// 	return underlined;
//     }
//     public int getFontSize() {
// 	if (fontSize==0) return Integer.parseInt(FreeMind.userProps.getProperty("standardfontsize"));
// 	return fontSize;
//     }
//     /**Maybe implement handling for cases when the font is not available on this system*/
//     public String getFont() {
// 	if (font==null) return FreeMind.userProps.getProperty("standardfont");
// 	return font;
//     }

//     //
//     // other
//     //

//     public String toString() {
// 	return userObject.toString();
//     }
	 
//     public void add( MindMapNode child ) {
// 	insert(child,0);
//     }

//     public boolean isRoot() {
// 	return (parent==null);
//     }
	
//     //
//     //  Interface TreeNode
//     //

//     public Enumeration children() { 
// 	return children.elements();
//     }
	
//     public boolean getAllowsChildren() {
// 	return ALLOWSCHILDREN;
//     }
	
//     public TreeNode getChildAt( int childIndex ) {
// 	return children.elementAt( childIndex );
//     }

//     public int getChildCount() {
// 	return children.size();
//     }

//     public int getIndex( TreeNode node ) {
// 	return children.indexOf( (NodeModel)node ); //uses equals()
//     } 

//     public TreeNode getParent() {
// 	return parent;
//     }

//     public boolean isLeaf() {
// 	return getChildCount() == 0;
//     }

//     //
//     //  Interface MutableTreeNode
//     //
    
//     //do all remove methods have to work recursively to make the 
//     //Garbage Collection work (Nodes in removed Sub-Trees reference each other)?
    
//     public void insert( MutableTreeNode child, int index) {
//       	children.add( index, (NodeModel)child );
//     	child.setParent( this );
//     }
    
//     public void remove( int index ) {
// 	children.remove( index );
//     }
    
//     public void remove( MutableTreeNode node ) {
//     	children.remove( (MindMapNode)node );
//     }

//     public void removeFromParent() {
//     	parent.remove( this );
//     }

//     public void setParent( MutableTreeNode newParent ) {
//     	parent = newParent;
// 	getEdge().setSource( (MindMapNode)newParent );
//     }

//     public void setUserObject( Object object ) {
// 	userObject = object;
//     }

//     ////////////////
//     // Private methods. Internal Implementation
//     //////////////

//     /** Recursive Method for getPath() */
//     public void addToPathVector( Vector pathVector ) {
// 	pathVector.add( 0, this ); //Add myself to beginning of Vector
// 	if ( parent != null ) {
// 	    ( (NodeModel)parent ).addToPathVector( pathVector );
// 	}
//     }








