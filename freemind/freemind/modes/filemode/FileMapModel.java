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

import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import freemind.modes.MapAdapter;
import java.io.File;

public class FileMapModel extends MapAdapter {
    
    int i = 0;
    //
    // Constructors
    //

    public FileMapModel() {
	setRoot(new FileNodeModel(new File(File.separator)));
    }
    
    public FileMapModel( File root ) {
 	setRoot(new FileNodeModel(root));
    }

    //
    // Other methods
    //
    public void save(File file) {
    }
    
    public void load(File file) {
    }
    
    public boolean isSaved() {
	return true;
    }

    public String toString() {
	return "File: "+getRoot().toString();
    }

    public void changeNode(MindMapNode node, String newText) {
	System.out.println(i++);
	if (i>10) System.exit(i);
	
	File file = ((FileNodeModel)node).getFile();
	File newFile = new File(file.getParentFile(), newText);
	file.renameTo(newFile);
	System.out.println(file);
	FileNodeModel parent = (FileNodeModel)node.getParent();
	System.out.println("A");
	//	removeNodeFromParent(node);
	System.out.println("B");
	
	insertNodeInto(new FileNodeModel(newFile),parent,0);


	nodeChanged(node);
	System.out.println("end");
	
    }
}


// public class FileSystemModel extends AbstractTreeTableModel 
//                              implements TreeTableModel {

//     // The the returned file length for directories. 
//     public static final Integer ZERO = new Integer(0); 

//     //
//     // Some convenience methods. 
//     //

//     protected File getFile(Object node) {
// 	FileNode fileNode = ((FileNode)node); 
// 	return fileNode.getFile();       
//     }

//     protected Object[] getChildren(Object node) {
// 	FileNode fileNode = ((FileNode)node); 
// 	return fileNode.getChildren(); 
//     }

//     //
//     // The TreeModel interface
//     //

//     public int getChildCount(Object node) { 
// 	Object[] children = getChildren(node); 
// 	return (children == null) ? 0 : children.length;
//     }

//     public Object getChild(Object node, int i) { 
// 	return getChildren(node)[i]; 
//     }
// }
