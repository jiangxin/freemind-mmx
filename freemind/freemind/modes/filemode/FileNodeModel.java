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

package freemind.modes.filemode;

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
import java.io.File;


/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public class FileNodeModel extends NodeAdapter {
    private File file;
	
    //
    //  Constructors
    //

    public FileNodeModel( File file ) {
	setEdge(new FileEdgeModel(this));
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
    public Enumeration children() {
	if (!isRoot()) {
	    if (isFolded() || isLeaf()) {
		return new Vector().elements();//Empty Enumeration
	    }
	}
	if (children != null) {
	    return children.elements(); 
	}
	try {
	    String[] files = file.list();
	    if(files != null) {
		children = new Vector();

		String path = file.getPath();
		for(int i = 0; i < files.length; i++) {
		    File childFile = new File(path, files[i]);
		    if (!childFile.isHidden()) {
			insert(new FileNodeModel(childFile),0);
		    }
		}
	    }
	} catch (SecurityException se) {}
	return children.elements(); 
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
