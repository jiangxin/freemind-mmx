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

package freemind.modes;

import java.awt.Color;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import freemind.view.mindmapview.NodeView;
import java.net.URL;
//XML Definition (Interfaces)
import  org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public interface MindMapNode extends MutableTreeNode {
	
    MindMapEdge getEdge();

    Color getColor();

    String getStyle();

    boolean isBold();

    boolean isItalic();
    
    boolean isUnderlined();

    int getFontSize();
    
    String getFont();

    NodeView getViewer();

    void setViewer( NodeView viewer );

    String toString();
	 
    TreePath getPath();
    
    boolean isRoot();

    boolean isFolded();

    void setFolded(boolean folded);
}

	







