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
/*$Id: MindMapNodeModel.java,v 1.10 2003-11-03 10:15:46 sviles Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.Tools;
import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.NodeAdapter;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;

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

    private String saveHTML_escapeUnicodeAndSpecialCharacters(String text) {
       int len = text.length();
       StringBuffer result = new StringBuffer(len);
       int intValue;
       char myChar;
       for (int i = 0; i < len; ++i) {
          myChar = text.charAt(i);
          intValue = (int) text.charAt(i);
          if (intValue > 128) {
             result.append("&#").append(intValue).append(';'); }
          else {
             switch (myChar) {
             case '&':
                result.append("&amp;");
                break;
             case '<':
                result.append("&lt;");
                break;
             case '>':
                result.append("&gt;");
                break;                
             default:
                result.append(myChar); }}}
       return result.toString(); };

    public int saveHTML(BufferedWriter fileout,String parentID,int lastChildNumber,
                        boolean isRoot) throws IOException {
       // return lastChildNumber 
       // Not very beautiful solution, but working at least and logical too.

        boolean createFolding = isFolded() && !isRoot;

        fileout.write("<li>");

        String localParentID = parentID;
	if (createFolding) {
           // lastChildNumber = new Integer lastChildNumber.intValue() + 1; Change value of an integer
           ++lastChildNumber;
     
           localParentID = parentID+"_"+lastChildNumber;
           fileout.write
              ("<span id=\"show"+localParentID+"\" class=\"foldclosed\" onClick=\"show_folder('"+localParentID+
               "')\" style=\"POSITION: absolute\">+</span> "+
               "<span id=\"hide"+localParentID+"\" class=\"foldopened\" onClick=\"hide_folder('"+localParentID+
               "')\">-</Span>");

           fileout.newLine(); }

	if (getLink() != null) {
           String link = getLink();
           if (link.endsWith(".mm")) {
              link += ".html";
           }
           fileout.write("<a href=\""+link+"\">"); }

        String fontStyle="";
	
	if (color != null) {
           fontStyle+="color: "+Tools.colorToXml(getColor())+";"; }

        if (font!=null && font.getSize()!=0) {
           fontStyle+="font-size: "+getFont().getSize()+"px;"; }

        if (font != null) {
           String fontFamily = getFont().getFontName();
           if (fontFamily.endsWith(" Kursiv")) {
              fontFamily = fontFamily.substring(0,fontFamily.length()-7); }
           if (fontFamily.endsWith(" Fett")) {
              fontFamily = fontFamily.substring(0,fontFamily.length()-5); }
           fontStyle+="font-family: "+fontFamily+", sans-serif; "; }

        if (isItalic()) {
           fontStyle+="font-style: italic; "; }

        if (isBold()) {
           fontStyle+="font-weight: bold; "; }

        // ------------------------

        if (!fontStyle.equals("")) {
           fileout.write("<font style=\""+fontStyle+"\">"); }

        if (this.toString().matches(" *")) {
           fileout.write("&nbsp;"); }
        else {
           fileout.write(saveHTML_escapeUnicodeAndSpecialCharacters(toString())); }

        //XMLElement auxiliaryXMLElement = new XMLElement();
        //   auxiliaryXMLElement.setContent(this.toString());
        //  auxiliaryXMLElement.write(fileout); }


        if (fontStyle != "") {
           fileout.write("</font>"); }

	if (getLink() != null) {
           fileout.write("</a>"); }

        fileout.newLine();


	if (createFolding) {
           fileout.write("<ul id=\"fold"+localParentID+
                         "\" style=\"POSITION: relative; VISIBILITY: visible;\">");
           int localLastChildNumber = 0;
           for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
              MindMapNodeModel child = (MindMapNodeModel)e.next();            
              localLastChildNumber =
                 child.saveHTML(fileout,localParentID,localLastChildNumber,/*isRoot=*/false); }}
        else {
           fileout.write("<ul>"); 
           for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
              MindMapNodeModel child = (MindMapNodeModel)e.next();            
              lastChildNumber =
                 child.saveHTML(fileout,parentID,lastChildNumber,/*isRoot=*/false); }}

        fileout.newLine();

        fileout.write("</ul>");
        fileout.newLine();
        fileout.write("</li>");
        fileout.newLine();

        return lastChildNumber;
    }
    public void saveTXT(BufferedWriter fileout,int depth) throws IOException {
        for (int i=0; i < depth; ++i) {
           fileout.write("    "); }
        if (this.toString().matches(" *")) {
           fileout.write("o"); }
        else {
           if (getLink() != null) {
              String link = getLink();
              if (!link.equals(this.toString())) {
                 fileout.write(this.toString()+" "); }              
              fileout.write("<"+link+">"); }
           else {
              fileout.write(this.toString()); }}


        fileout.write("\n");
        //fileout.write(System.getProperty("line.separator"));
        //fileout.newLine();

        // ^ One would rather expect here one of the above commands
        // commented out. However, it does not work as expected on
        // Windows. My unchecked hypothesis is, that the String Java stores
        // in Clipboard carries information that it actually is \n
        // separated string. The current coding works fine with pasting on
        // Windows (and I expect, that on Unix too, because \n is a Unix
        // separator). This method is actually used only for pasting
        // purposes, it is never used for writing to file. As a result, the
        // writing to file is not tested.
        
        // Another hypothesis is, that something goes astray when creating
        // StringWriter.

        for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
           ((MindMapNodeModel)e.next()).saveTXT(fileout,depth + 1); }
    }
    public void collectColors(HashSet colors) {
       if (color != null) {
          colors.add(getColor()); }
       for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
          ((MindMapNodeModel)e.next()).collectColors(colors); }}

    private String saveRFT_escapeUnicodeAndSpecialCharacters(String text) {
       int len = text.length();
       StringBuffer result = new StringBuffer(len);
       int intValue;
       char myChar;
       for (int i = 0; i < len; ++i) {
          myChar = text.charAt(i);
          intValue = (int) text.charAt(i);
          if (intValue > 128) {
             result.append("\\u").append(intValue).append("?"); }
          else {
             switch (myChar) {
             case '\\':
                result.append("\\\\");
                break;
             case '{':
                result.append("\\{");
                break;
             case '}':
                result.append("\\}");
                break;                
             default:
                result.append(myChar); }}}
       return result.toString(); }

    public void saveRTF(BufferedWriter fileout, int depth, HashMap colorTable) throws IOException {
        String pre="{"+"\\li"+depth*400;
        String fontsize="";
	if (color != null) {
           pre += "\\cf"+((Integer)colorTable.get(getColor())).intValue(); }

        if (isItalic()) {
           pre += "\\i "; }
        if (isBold()) {
           pre += "\\b "; }
        if (font!=null && font.getSize()!=0) {
           fontsize="\\fs"+Math.round(1.5*getFont().getSize());
           pre += fontsize; }

        pre += "{}"; // make sure setting of properties is separated from the text itself

        fileout.write("\\li"+depth*400+"{}");
        if (this.toString().matches(" *")) {
           fileout.write("o"); }
        else {
           String text = saveRFT_escapeUnicodeAndSpecialCharacters(this.toString());
           if (getLink() != null) {
              String link = saveRFT_escapeUnicodeAndSpecialCharacters(getLink());
              if (link.equals(this.toString())) {
                 fileout.write(pre+"<{\\ul\\cf1 "+link+"}>"+"}"); }
              else {
                 fileout.write("{"+fontsize+pre+text+"} ");
                 fileout.write("<{\\ul\\cf1 "+link+"}}>"); }}
           else {
              fileout.write(pre+text+"}"); }}
        
        fileout.write("\\par");
        fileout.newLine();

        for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
           ((MindMapNodeModel)e.next()).saveRTF(fileout,depth + 1,colorTable); }
    }

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
	    node.addProperty("style", super.getStyle());
	    //  ^ Here cannot be just getStyle() without super. This is because
	    //  getStyle's style depends on folded / unfolded. For example, when
	    //  real style is fork and node is folded, getStyle returns
	    //  "Bubble", which is not what we want to save.
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
	    //This has to be improved!
            XMLElement childElement = (XMLElement)childNodes.elementAt(i);
            String tagName = childElement.getTagName();
	    //node
	    if ( tagName.equals("node")) {
		MindMapNodeModel child = new MindMapNodeModel(getFrame());
		insert(child,getChildCount());
		child.load( childElement ); //recursive
                continue;
	    }

	    //edge
	    if ( tagName.equals("edge")) {
		setEdge(new MindMapEdgeModel(this,getFrame()) );
		((MindMapEdgeModel)getEdge()).load(childElement);
                continue;
	    }
	    //font
	    if ( tagName.equals("font")) {
		String name = childElement.getProperty("name"); 
		int style=0;
		int size=0;

		//if (!Tools.isValidFont(name)) {
                // name = "Sans Serif";
                //}
                //     ^ This is absolutely unnecessary and it extremely slows down loading

		if (childElement.getProperty("bold")!=null && childElement.getProperty("bold").equals("true")) {
                   style+=Font.BOLD; }
		if (childElement.getProperty("italic")!=null && childElement.getProperty("italic").equals("true")) {
                   style+=Font.ITALIC; }
		if (childElement.getProperty("underline")!=null && childElement.getProperty("underline").equals("true")) {
                   setUnderlined(true); }

		if (childElement.getProperty("size")!=null) {
		    size = Integer.parseInt(childElement.getProperty("size"));
		    // getFont().setSize(Integer.parseInt(childElement.getProperty("size")));
		}

		setFont(new Font(name, style, size));

	    }
	}

	if (node.getProperty("folded")!=null && node.getProperty("folded").equals("true")) {
            setFolded(true);
	}
    // ^ When this condition was at the beginning of the function, children of folded nodes were
    // inserted into map in reverse order.

    }
}





