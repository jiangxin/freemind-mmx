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
/*$Id: MindMapNode.java,v 1.15.12.2 2004-03-11 06:28:41 christianfoltin Exp $*/

package freemind.modes;

import freemind.extensions.*;
import freemind.view.mindmapview.NodeView;
// clouds, fc, 08.11.2003:
// end clouds.
// links, fc, 08.11.2003:
// end links.
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Vector;

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

    boolean isUnderlined();

    Font getFont();
    
	String getFontSize();
    
	String getFontFamilyName();
    
    NodeView getViewer();

    void setViewer( NodeView viewer );

    String toString();
	 
    TreePath getPath();
    
    boolean isDescendantOf(MindMapNode node);
    
    boolean isRoot();

    boolean isFolded();

    freemind.main.Tools.BooleanHolder isLeft();
    
    void setLeft(boolean isLeft);

    void setFolded(boolean folded);

    void setFont(Font font);

    void setLink(String link);

    void setFontSize(int fontSize);

    void setColor(Color color);

    // fc, 06.10.2003:
    Vector/*of MindIcon s*/ getIcons();

    void   addIcon(MindIcon icon);

    /* @return returns the new amount of icons.*/
    int   removeLastIcon();
    // end, fc, 24.9.2003

    // clouds, fc, 08.11.2003:
    MindMapCloud getCloud();
    // end clouds.
        
    //fc, 24.2.2004: background color:
    Color getBackgroundColor(           );
    void  setBackgroundColor(Color color);

    //hooks, fc 28.2.2004:
    List getHooks();
    Collection getActivatedHooks();
	PermanentNodeHook addHook(PermanentNodeHook hook);
	void invokeHook(NodeHook hook);
    void removeHook(PermanentNodeHook hook); 
	//end hooks
	
	//tooltips,fc 29.2.2004
	void setToolTip(String tip);
	String getToolTip();
        
    MindMapNode shallowCopy();
}
