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
/*$Id: MindMapNode.java,v 1.11 2003-11-03 10:49:17 sviles Exp $*/

package freemind.modes;

import freemind.view.mindmapview.NodeView;
import java.util.ListIterator;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public interface MindMapNode extends MutableTreeNode {
   
    public static final String STYLE_BUBBLE = "bubble";
    public static final String STYLE_FORK = "fork";
	
    ListIterator childrenFolded();

    ListIterator childrenUnfolded();

    boolean hasChildren();

    int getChildPosition(MindMapNode childNode);

    MindMapNode getPreferredChild();
    void setPreferredChild(MindMapNode node);
    
    int getNodeLevel();

    String getLink();

    MindMapEdge getEdge();

    Color getColor();

    String getStyle();

    MindMapNode getParentNode();

    boolean isBold();

    boolean isItalic();

    boolean isLong();
    
    boolean isUnderlined();

    Font getFont();
    
    NodeView getViewer();

    void setViewer( NodeView viewer );

    String toString();
	 
    TreePath getPath();
    
    boolean isDescendantOf(MindMapNode node);
    
    boolean isRoot();

    boolean isFolded();

    void setFolded(boolean folded);

    void setFont(Font font);

    void setLink(String link);

    void setFontSize(int fontSize);

    void setColor(Color color);

// (PN)
//    /**
//     * Returns the number of childer, whether the node is folded or not. The
//     * method getChildCount() returns 0, if the node is folded.
//     */
//    int getChildCount();

    MindMapNode shallowCopy();
}
