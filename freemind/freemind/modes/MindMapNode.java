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
/*$Id: MindMapNode.java,v 1.6 2001-03-26 20:14:01 ponder Exp $*/

package freemind.modes;

import freemind.view.mindmapview.NodeView;
import java.util.ListIterator;
import java.net.URL;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public interface MindMapNode extends MutableTreeNode {
	
    ListIterator childrenFolded();

    ListIterator childrenUnfolded();

    MindMapEdge getEdge();

    Color getColor();

    String getStyle();

    boolean isBold();

    boolean isItalic();
    
    boolean isUnderlined();

    Font getFont();
    
//      int getFontSize();

//      String getFont();

    NodeView getViewer();

    void setViewer( NodeView viewer );

    String toString();
	 
    TreePath getPath();
    
    boolean isRoot();

    boolean isFolded();

    void setFolded(boolean folded);
}
