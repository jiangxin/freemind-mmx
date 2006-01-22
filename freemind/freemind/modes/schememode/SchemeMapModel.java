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
/*$Id: SchemeMapModel.java,v 1.11.18.3.2.1.2.2 2006-01-22 12:24:39 dpolivaev Exp $*/

package freemind.modes.schememode;

import freemind.main.FreeMindMain;

import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

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
import java.io.Writer;

public class SchemeMapModel extends MapAdapter {
    
    //
    // Constructors
    //

    public SchemeMapModel(FreeMindMain frame, ModeController modeController) {
	super(frame, modeController);
	setRoot(new SchemeNodeModel(getFrame(), this));
    }
    
    //
    // Other methods
    //
    public boolean save(File file) {
	try {
	    setFile(file);
	    setSaved(true);

	    //Generating output Stream
	    BufferedWriter fileout = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(file) ) );

	    fileout.write( getCode() );
	    fileout.close();
        return true;

	} catch(Exception e) {
	    System.err.println("Error in SchemeMapModel.save: ");
	    e.printStackTrace();
	    return false;
	}
    }
    
    public void load(File file) throws FileNotFoundException {
	setFile(file);
	setSaved(true);

	setRoot(new SchemeNodeModel(getFrame(), this));
	
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
	while (tok.nextToken() != StreamTokenizer.TT_EOF) {
	    if (tok.ttype == 40) {    //"("
		//		System.out.println("Token starts with (");
		SchemeNodeModel newNode = new SchemeNodeModel(getFrame(), this);
		insertNodeInto(newNode, node);
		node = newNode;
	    } else if (tok.ttype == 41) {    //")"
		//		System.out.println("Token starts with )");
		if (node.getParent() != null) {//this should not be necessary, if this happens, the code is wrong
		    node = (SchemeNodeModel)node.getParent();
		}
	    } else if (tok.ttype == StreamTokenizer.TT_WORD) {
		String token = tok.sval.trim();

		if (node.toString().equals(" ") && node.getChildCount() == 0) {
		    node.setUserObject(token);
		} else {
		    SchemeNodeModel newNode = new SchemeNodeModel(getFrame(), this);
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

    /* (non-Javadoc)
     * @see freemind.modes.MindMap#setLinkInclinationChanged()
     */
    public void setLinkInclinationChanged() {
    }
	/* (non-Javadoc)
	 * @see freemind.modes.MindMap#getXml(java.io.Writer)
	 */
	public void getXml(Writer fileout) throws IOException {
		fileout.write(getCode());
	}

}
