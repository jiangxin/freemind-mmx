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
/*$Id: FileMapModel.java,v 1.7.18.3.6.3 2006-01-22 12:24:38 dpolivaev Exp $*/

package freemind.modes.filemode;

import freemind.main.FreeMindMain;
import freemind.modes.LinkRegistryAdapter;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.MapAdapter;
import freemind.modes.ModeController;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class FileMapModel extends MapAdapter {
    
    private LinkRegistryAdapter linkRegistry;

    //
    // Constructors
    //

    public FileMapModel(FreeMindMain frame, ModeController modeController) {
        this(new File(File.separator), frame, modeController);
    }
    
    public FileMapModel( File root , FreeMindMain frame, ModeController modeController) {
        super(frame, modeController);
        setRoot(new FileNodeModel(root,getFrame(), this));
 		linkRegistry = new LinkRegistryAdapter();
    }

    //
    // Other methods
    //
    public MindMapLinkRegistry getLinkRegistry() {
        return linkRegistry;
    }

    //
    // Other methods
    //
    public boolean save(File file) {
    	return true;
    }
    
    /**
     *
     */

    public void destroy() {
        /* fc, 8.8.2004: don't call super.destroy as this method tries to remove the hooks recursively.
         * This must fail. */
        //super.destroy();
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
// 	File file = ((FileNodeModel)node).getFile();
// 	File newFile = new File(file.getParentFile(), newText);
// 	file.renameTo(newFile);
// 	System.out.println(file);
// 	FileNodeModel parent = (FileNodeModel)node.getParent();
// 	//	removeNodeFromParent(node);
	
// 	insertNodeInto(new FileNodeModel(newFile),parent,0);


// 	nodeChanged(node);
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
		// nothing. 
		//FIXME: Implement me if you need me.
		throw new RuntimeException("Unimplemented method called.");
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
