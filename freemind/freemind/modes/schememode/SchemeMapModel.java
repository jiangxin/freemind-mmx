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
/*$Id: SchemeMapModel.java,v 1.9 2003-11-03 10:39:53 sviles Exp $*/

package freemind.modes.schememode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.MapAdapter;
import java.io.File;
import java.io.Reader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.StreamTokenizer;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class SchemeMapModel extends MapAdapter {
    
    //
    // Constructors
    //

    public SchemeMapModel(FreeMindMain frame) {
	super(frame);
	setRoot(new SchemeNodeModel(getFrame()));
    }
    
    //
    // Other methods
    //
    public void save(File file) {
	try {
	    setFile(file);
	    setSaved(true);

	    //Generating output Stream
	    BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

	    fileout.write( getCode() );

	    fileout.close();

	} catch(Exception e) {
	    System.err.println("Error in SchemeMapModel.save: ");
	    e.printStackTrace();
	}
    }
    
    public void load(File file) throws FileNotFoundException {
	setFile(file);
	setSaved(true);

	setRoot(new SchemeNodeModel(getFrame()));
	
	try {
	    loadMathStyle(new InputStreamReader(new FileInputStream(file)));
	} catch (IOException ex) {
	}
    }

    public void loadMathStyle(Reader re) throws IOException{
	StreamTokenizer tok = new StreamTokenizer(re);
	tok.resetSyntax();
	tok.whitespaceChars(0, 32);
	tok.wordChars(33, 255);
	tok.ordinaryChars('(',')');

	//commentChar('/');
	tok.commentChar(';');//59 is ';'
	//	tok.quoteChar('"');
	//	quoteChar('\'');	//	tok.eolIsSignificant(true);

	SchemeNodeModel node = (SchemeNodeModel)getRoot();
	while (tok.nextToken() != tok.TT_EOF) {
	    if (tok.ttype == 40) {    //"("
		//		System.out.println("Token starts with (");
		SchemeNodeModel newNode = new SchemeNodeModel(getFrame());
		insertNodeInto(newNode, node);
		node = newNode;
	    } else if (tok.ttype == 41) {    //")"
		//		System.out.println("Token starts with )");
		if (node.getParent() != null) {//this should not be necessary, if this happens, the code is wrong
		    node = (SchemeNodeModel)node.getParent();
		}
	    } else if (tok.ttype == tok.TT_WORD) {
		String token = tok.sval.trim();

		if (node.toString().equals(" ") && node.getChildCount() == 0) {
		    node.setUserObject(token);
		} else {
		    SchemeNodeModel newNode = new SchemeNodeModel(getFrame());
		    insertNodeInto(newNode, node);
		    newNode.setUserObject(token);
		}
	    }/* else if (tok.ttype == tok.TT_NUMBER) {
		String token = Double.toString(tok.nval);

		if (node.toString().equals("")) {
		    node.setUserObject(token);
		} else {
		    SchemeNodeModel newNode = new SchemeNodeModel(getFrame());
		    insertNodeInto(newNode,node,node.getChildCount());
		    newNode.setUserObject(token);
		}
		}*/
	}
    }

    /**
     * This method returns the scheme code that is represented by
     * this map as a plain string.
     */
    public String getCode() {
	return ((SchemeNodeModel)getRoot()).getCodeMathStyle();
    }
    
    //    public boolean isSaved() {
    //	return true;
    //    }

    public String toString() {
	if (getFile() == null) {
	    return null;
	} else {
	    return getFile().getName();
	}
    }
}
