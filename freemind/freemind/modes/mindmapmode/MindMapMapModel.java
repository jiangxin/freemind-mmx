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
/*$Id: MindMapMapModel.java,v 1.6 2000-11-02 17:20:11 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.modes.MapAdapter;
import java.awt.Color;
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
	node.setFontSize(fontSize);
	nodeStructureChanged(node);
    }

    public void setFont(MindMapNodeModel node, String font) {
	node.setFont(font);
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
