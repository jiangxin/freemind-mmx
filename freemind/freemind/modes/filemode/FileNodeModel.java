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
/*$Id: FileNodeModel.java,v 1.6 2001-03-13 15:50:05 ponder Exp $*/

package freemind.modes.filemode;

import freemind.main.FreeMindMain;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.io.File;
import java.awt.Color;


/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class FileNodeModel extends NodeAdapter {
    private File file;
    private Color color;
	
    //
    //  Constructors
    //

    public FileNodeModel( File file, FreeMindMain frame ) {
	super(frame);
	setEdge(new FileEdgeModel(this,getFrame()));
	this.file = file;
	setFolded(true);
    }

    //Overwritten get Methods
    public String getStyle() {
	if(file.isFile()) {
	    return "bubble";
	} else {
	    return "fork";
	}
    }

    File getFile() {
	return file;
    }

    /**
     * This could be a nice feat. Improve it!
     */
    public Color getColor() {
	if (color == null) {

	    //float hue = (float)getFile().length() / 100000;
	    // float hue = 6.3F;
	   //  if (hue > 1) {
// 		hue = 1;
// 	    }
	    //	    color = Color.getHSBColor(hue,0.5F, 0.5F);
// 	    int red = (int)(1 / (getFile().length()+1) * 255);
// 	    color = new Color(red,0,0);
	    color = Color.blue;
	}
	return color;
    }

//     void setFile(File file) {
// 	this.file = file;
//     }

    public String toString() {
	String name = file.getName();
	if (name.equals("")) {
	    name = "Root";
	}
	return name;
    }

    /**
     * 
     */
    public ListIterator childrenFolded() {
	if (!isRoot()) {
	    if (isFolded() || isLeaf()) {
		return null;//Empty Enumeration
	    }
	}
	if (children != null) {
	    return children.listIterator(); 
	}
	try {
	    String[] files = file.list();
	    if(files != null) {
		children = new LinkedList();

		String path = file.getPath();
		for(int i = 0; i < files.length; i++) {
		    File childFile = new File(path, files[i]);
		    if (!childFile.isHidden()) {
			insert(new FileNodeModel(childFile,getFrame()),0);
		    }
		}
	    }
	} catch (SecurityException se) {}
	return children.listIterator(); 
    }

    public boolean isLeaf() {
	return file.isFile();
    }

}

// /* A FileNode is a derivative of the File class - though we delegate to 
//  * the File object rather than subclassing it. It is used to maintain a 
//  * cache of a directory's children and therefore avoid repeated access 
//  * to the underlying file system during rendering. 
//  */
// class FileNode { 
//     File     file; 
//     Object[] children; 

//     public FileNode(File file) { 
// 	this.file = file; 
//     }

//     // Used to sort the file names.
//     static private MergeSort  fileMS = new MergeSort() {
// 	public int compareElementsAt(int a, int b) {
// 	    return ((String)toSort[a]).compareTo((String)toSort[b]);
// 	}
//     };

//     /**
//      * Returns the the string to be used to display this leaf in the JTree.
//      */
//     public String toString() { 
// 	return file.getName();
//     }

//     public File getFile() {
// 	return file; 
//     }
// }
