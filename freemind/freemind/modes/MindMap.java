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
/*$Id: MindMap.java,v 1.8 2003-11-03 10:15:45 sviles Exp $*/

package freemind.modes;

import java.io.File;
import java.net.URL;
import java.awt.Color;
import java.util.List;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
// Clipboard
import java.awt.datatransfer.Transferable;

public interface MindMap extends TreeModel {
        
    void changeNode(MindMapNode node, String newText);

    Transferable cut(MindMapNode node);

    Transferable copy(MindMapNode node);

    // ^ Is copy with node really needed? It seems to me, that no.
    Transferable cut();
    Transferable copy(); 
    String getAsPlainText(List mindMapNodes);
    String getAsRTF(List mindMapNodes);

    void paste(Transferable t, MindMapNode parent);
    void paste(Transferable t, MindMapNode target, boolean asSibling);

    //    void paste(MindMapNode node, MindMapNode parent);

    boolean find(MindMapNode node, String what, boolean caseSensitive);
    
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

    void setFolded(MindMapNode node, boolean folded);

    void close();
}
