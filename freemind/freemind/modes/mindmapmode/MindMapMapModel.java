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
/*$Id: MindMapMapModel.java,v 1.8 2000-12-05 17:32:56 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.modes.MapAdapter;
import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
//XML Specification (Interfaces)
import  org.w3c.dom.Document;
import org.w3c.dom.Element;
// //XML Parser, actually apache xerces
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

public class MindMapMapModel extends MapAdapter {

    //
    // Constructors
    //

    public MindMapMapModel() {
	setRoot(new MindMapNodeModel("new Mindmap"));
    }
    
    public MindMapMapModel( MindMapNodeModel root ) {
	setRoot(root);
    }

    //
    // Methods for editing of the Nodes
    //

    public void setNodeColor(MindMapNodeModel node, Color color) {
	node.setColor(color);
	nodeChanged(node);
    }

    public void setBranchFont(MindMapNodeModel node, Font f) {
	setNodeFont(node,f);
	for(int i=0;i<node.getChildCount();i++) {
	    setBranchFont((MindMapNodeModel)node.getChildAt(i),f);
	}
	nodeChanged(node);
    }


    public void setBranchColor(MindMapNodeModel node, Color color) {
	setNodeColor(node,color);
	for(int i=0;i<node.getChildCount();i++) {
	    setBranchColor((MindMapNodeModel)node.getChildAt(i),color);
	}
	nodeChanged(node);
    }

    public void setBranchBold(MindMapNodeModel node) {
	Font f = node.getFont();
	if(!f.isBold()) {
	    // make bold
	    node.setFont(f.deriveFont(f.getStyle()+Font.BOLD));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchBold((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }

    public void setBranchNonBold(MindMapNodeModel node) {
	Font f = node.getFont();
	if(f.isBold()) {
	    // make normal
	    node.setFont(f.deriveFont(f.getStyle()-Font.BOLD));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchNonBold((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }

    public void setBranchToggleBold(MindMapNodeModel node) {
	Font f = node.getFont();
	if(f.isBold()) {
	    // make normal
	    node.setFont(f.deriveFont(f.getStyle()-Font.BOLD));
	} else {
	    node.setFont(f.deriveFont(f.getStyle()+Font.BOLD));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchToggleBold((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }


    public void setBranchItalic(MindMapNodeModel node) {
	Font f = node.getFont();
	if(!f.isItalic()) {
	    // make italic
	    node.setFont(f.deriveFont(f.getStyle()+Font.ITALIC));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchItalic((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }

    public void setBranchNonItalic(MindMapNodeModel node) {
	Font f = node.getFont();
	if(f.isItalic()) {
	    // make normal
	    node.setFont(f.deriveFont(f.getStyle()-Font.ITALIC));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchNonItalic((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }

    public void setBranchToggleItalic(MindMapNodeModel node) {
	Font f = node.getFont();
	if(f.isItalic()) {
	    // make normal
	    node.setFont(f.deriveFont(f.getStyle()-Font.ITALIC));
	} else {
	    node.setFont(f.deriveFont(f.getStyle()+Font.ITALIC));
	}

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchToggleItalic((MindMapNodeModel)node.getChildAt(i));
	}
	nodeChanged(node);
    }




    public void setNodeFont(MindMapNodeModel node, Font f) {
	node.setFont(f);
//  	node.setFontSize(f.getSize());
//  	node.setFont(f.getFontName());
	// FIXME: the implementation should be changed to only use java.awt.Font
	// node.setStyle(f.getStyle());
	nodeChanged(node);
    }

    public void setEdgeColor(MindMapNodeModel node, Color color) {
	((MindMapEdgeModel)node.getEdge()).setColor(color);
	nodeChanged(node);
    }

    public void setNodeStyle(MindMapNodeModel node, String style) {
	node.setStyle(style);
	nodeStructureChanged(node);
    }

    public void setEdgeStyle(MindMapNodeModel node, String style) {
	MindMapEdgeModel edge = (MindMapEdgeModel)node.getEdge();
	edge.setStyle(style);
	nodeStructureChanged(node);
    }

    public void setBold(MindMapNodeModel node) {
	if (node.isBold()) {
	    node.setBold(false);
	} else {
	    node.setBold(true);
	}
	nodeChanged(node);
    }

    public void setItalic(MindMapNodeModel node) {
	if (node.isItalic()) {
	    node.setItalic(false);
	} else {
	    node.setItalic(true);
	}
	nodeChanged(node);
    }

    public void setUnderlined(MindMapNodeModel node) {
	if (node.isUnderlined()) {
	    node.setUnderlined(false);
	} else {
	    node.setUnderlined(true);
	}
	nodeChanged(node);
    }

    public void setNormalFont(MindMapNodeModel node) {
	node.setItalic(false);
	node.setBold(false);
	node.setUnderlined(false);
	nodeChanged(node);
    }

    public void setFontSize(MindMapNodeModel node, int fontSize) {
	// ** change the font size
	node.setFont(node.getFont().deriveFont((float)fontSize));
	nodeStructureChanged(node);
    }

    public void setFont(MindMapNodeModel node, String font) {
	node.setFont(new Font(font,node.getFont().getStyle(),node.getFont().getSize()));
	nodeStructureChanged(node);
    }

    public void setBranchFontSize(MindMapNodeModel node, int fontSize) {
	// ** change the font size
	node.setFont(node.getFont().deriveFont((float)fontSize));

	for(int i=0;i<node.getChildCount();i++) {
	    setBranchFontSize((MindMapNodeModel)node.getChildAt(i),fontSize);
	}

	nodeStructureChanged(node);
    }

    //
    // Other methods
    //
    public String toString() {
	if (getFile() == null) {
	    return null;
	} else {
	    return getFile().getName();
	}
    }

    public void save(File file) {
	try {
	    setFile(file);
	    setSaved(true);
	    Document doc = new DocumentImpl();
	    Element map = doc.createElement("map");
	    doc.appendChild(map);
	    ( (MindMapNodeModel)getRoot() ).save(doc,map);
	    String encoding = FreeMind.userProps.getProperty("mindmap_encoding");
	    
	    OutputFormat format = new OutputFormat(doc, encoding, false);//Serialize Document
            StringWriter  stringOut = new StringWriter();        //Writer will be a String
            XMLSerializer    serial = new XMLSerializer( stringOut, format );
            serial.asDOMSerializer();                            // As a DOM Serializer

            serial.serialize( doc.getDocumentElement() );

	    //Generating output Stream
	    BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

            fileout.write( stringOut.toString() ); //Spit out DOM as a String

	    fileout.close();

	} catch(Exception e) {
	    System.out.println("Error in MindMapMapModel.saveXML(): ");
	    e.printStackTrace();
	}
    }
    
    public void load(File file) throws FileNotFoundException {
	setFile(file);
	setSaved(true);
	MindMapNodeModel root = loadTree(file);
	if (root != null) {
	    setRoot(root);
	} else {
	    throw new FileNotFoundException();
	}
    }

    MindMapNodeModel loadTree(File file) {
	MindMapNodeModel root = null;
	try {
	    //Generating Parser
            DOMParser parser = new DOMParser();
	    try {
		//		parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace",false);
		parser.parse(file.getPath());
	    } catch(Exception e) {
		return null;
	    }
	    Document doc = parser.getDocument();

	    //Throw away old map
	    Element map = doc.getDocumentElement();
	    Element rootElement = (Element)map.getChildNodes().item(0);
	    root = new MindMapNodeModel();
	    root.load(rootElement);
	} catch(Exception e) {
	    return null;
	}
	return root;
    }
}
