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
/*$Id: MindMap.java,v 1.14.14.6.2.1.2.3 2005-09-17 19:02:07 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import freemind.controller.filter.Filter;
import freemind.main.XMLParseException;

public interface MindMap extends TreeModel {
        
//    void changeNode(MindMapNode node, String newText);
    //nodeChanged has moved to the modeController. (fc, 2.5.2004)
	void nodeChanged(TreeNode node);
	
	void nodeRefresh(TreeNode node);

    Transferable cut(MindMapNode node);

    Transferable copy(MindMapNode node);

    // ^ Is copy with node really needed? It seems to me, that no.
    Transferable copy(); 
    Transferable copySingle();
    /**
     * @param selectedNodes
     * @param inPlainText typically this is null. AN alternative is node.toString(); if there is only one node.
     * @return
     */
    public Transferable copy(List selectedNodes, String inPlainText);
    String getAsPlainText(List mindMapNodes);
    String getAsRTF(List mindMapNodes);

	void insertNodeInto(
			MindMapNode newChild,
			MindMapNode parent,
			int index);

//    void paste(Transferable t, MindMapNode parent);
//    /** @param isLeft determines, whether or not the node is placed on the left or right. **/
//    void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft);
//
    //    void paste(MindMapNode node, MindMapNode parent);


 
    
    /**
     * Returns the file name of the map edited or null if not possible.
     */
    File getFile();

    /**
     * Return URL of the map (whether as local file or a web location)
     */
    URL getURL() throws MalformedURLException;
    
    /** writes the content of the map to a writer.
	 * @param fileout
	 * @throws IOException
	 */
	void getXml(Writer fileout) throws IOException;

    /**
     * Returns a string that may be given to the modes restore()
     * to get this map again. The Mode must take care that
     * two different maps don't give the same restoreable
     * key.
     */
    String getRestoreable();

    Object[] getPathToRoot( TreeNode node );

    Color getBackgroundColor();
    
    void setBackgroundColor(Color color);


    /** @return returns the link registry associated with this mode, or null, if no registry is present.*/
    MindMapLinkRegistry getLinkRegistry();

   
    /**
     * Destroy everything you have created upon opening.  
     */
    void destroy();

    boolean isReadOnly();
    /**
     * @return
     */
    MapRegistry getRegistry();
    /**
     * @param file
     * @throws XMLParseException
     * @throws IOException
     * @throws FileNotFoundException
     */
    void load(File file) throws FileNotFoundException, IOException, XMLParseException;
    /**
     * @return
     */
    Filter getFilter();
    /**
     * @param inactiveFilter
     */
    void setFilter(Filter inactiveFilter);

// (PN)
//    void close();
}
