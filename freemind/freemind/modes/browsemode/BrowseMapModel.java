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
/*$Id: BrowseMapModel.java,v 1.3 2001-04-19 16:20:38 ponder Exp $*/

package freemind.modes.browsemode;

import freemind.main.FreeMindMain;
import freemind.modes.MapAdapter;
import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import freemind.modes.MapAdapter;
import java.awt.Color; 
import freemind.main.XMLElement;
//XML Specification (Interfaces)
//import  org.w3c.dom.Document;
//import org.w3c.dom.Element;
// //XML Parser, actually apache xerces
//import org.apache.xerces.parsers.DOMParser;
//import org.apache.xerces.dom.DocumentImpl;
//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;

public class BrowseMapModel extends MapAdapter {

    private URL url;

    //
    // Constructors
    //

    public BrowseMapModel(FreeMindMain frame) {
	super(frame);
	setRoot(new BrowseNodeModel(getFrame().getResources().getString("new_mindmap"), getFrame()));
    }
    
    public BrowseMapModel( BrowseNodeModel root, FreeMindMain frame ) {
	super(frame);
	setRoot(root);
    }

    //
    // Other methods
    //
    public String toString() {
	if (getURL() == null) {
	    return null;
	} else {
	    return getURL().toString();
	}
    }

    public File getFile() {
	return null;
    }

    protected void setFile() {
    }

    
    /**
       * Get the value of url.
       * @return Value of url.
       */
    public URL getURL() {return url;}
    
    /**
       * Set the value of url.
       * @param v  Value to assign to url.
       */
    public void setURL(URL  v) {this.url = v;}
    

    public void save(File file) {
    }
    
    public void load(File file) throws FileNotFoundException {
	throw new FileNotFoundException();
    }

    public void load(URL url) throws Exception {
	setURL(url);
	BrowseNodeModel root = loadTree(url);
	if (root != null) {
	    setRoot(root);
	} else {
	    //	    System.err.println("Err:"+root.toString());
	    throw new Exception();
	}
    }

    BrowseNodeModel loadTree(URL url) {
	BrowseNodeModel root = null;

	/*
	try {
	    //Generating Parser
	              DOMParser parser = new DOMParser();
	    //	    try {
		//		parser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace",false);
		parser.parse(file.getPath());
		//	    } catch(Exception e) {
		//		return null;
		//	    }
	    Document doc = parser.getDocument();

	    //Throw away old map
	    Element map = doc.getDocumentElement();
	    Element rootElement = (Element)map.getChildNodes().item(0);
	    root = new BrowseNodeModel();
	    root.load(rootElement);
	} catch(Exception e) {
	    return null;
	    }*/



	//NanoXML Code
	XMLElement parser = new XMLElement();
	try {
	    parser.parseFromReader(new InputStreamReader( url.openStream() ));
	} catch (Exception ex) {
	    System.err.println("Help! Error while parsing!");
	    return null;
	}

	//	XMLElement map = 

	XMLElement rootElement = (XMLElement)parser.getChildren().firstElement();
	root = new BrowseNodeModel(getFrame());
	root.load(rootElement);

	return root;
    }
}
