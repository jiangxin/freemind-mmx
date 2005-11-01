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
/*$Id: MindMapNode.java,v 1.15.18.10.2.4.2.5 2005-11-01 13:42:20 dpolivaev Exp $*/

package freemind.modes;



import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;


import freemind.controller.filter.FilterInfo;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.main.XMLElement;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.view.mindmapview.NodeView;

public interface MindMapNode extends MutableTreeNode {

	public static final String STYLE_BUBBLE = "bubble";
	public static final String STYLE_FORK = "fork";
	public static final String STYLE_COMBINED = "combined";
	public static final String STYLE_AS_PARENT = "as_parent";
	
	static final int AUTO = -1;

	String getText();
    void setText(String text);
    
	/**
	 * @return returns the unique id of the node. It is generated using the LinkRegistry.
	 */
	String getObjectId(ModeController controller);
	
    /** @return returns a ListIterator of all children of the node if the node is unfolded. 
     * EMPTY_LIST_ITERATOR otherwise. 
     * */
    ListIterator childrenFolded();

    /** @return returns a ListIterator of all (and not only the unfolded ones!!) children of the node. 
     * */
    ListIterator childrenUnfolded();

    boolean hasChildren();
    
    public FilterInfo getFilterInfo();

	/** @return -1 if the argument childNode is not a child. */
    int getChildPosition(MindMapNode childNode);

    MindMapNode getPreferredChild();
    void setPreferredChild(MindMapNode node);
    
    int getNodeLevel();

    String getLink();
    /** returns a short textual description of the text contained in the node. 
     *  Html is filtered out. */
    String getShortText(ModeController controller);

    MindMapEdge getEdge();

    Color getColor();

    String getStyle();
    /** currently the style may be one of MindMapNode.STYLE_BUBBLE or MindMapNode.STYLE_FORK.*/
    void setStyle(String style);

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
    
    /** Root is on the right side.
     * @return
     */
    boolean isOneLeftSideOfRoot();
    
    void setLeft(boolean isLeft);

    void setFolded(boolean folded);

    void setFont(Font font);    
     void setShiftY(int y);
     int getShiftY();
     int calcShiftY();

 	void setVGap(int i);
	int getVGap();
	int calcVGap();
	void setHGap(int i);
	int getHGap();
    void setLink(String link);

    void setFontSize(int fontSize);

    void setColor(Color color);

    // fc, 06.10.2003:
    /** Is a vector of MindIcon s*/
    List getIcons();

    void   addIcon(MindIcon icon);

    /* @return returns the new amount of icons.*/
    int   removeLastIcon();
    // end, fc, 24.9.2003

    // clouds, fc, 08.11.2003:
    MindMapCloud getCloud();
    void setCloud( MindMapCloud cloud );
    // end clouds.
        
    //fc, 24.2.2004: background color:
    Color getBackgroundColor(           );
    void  setBackgroundColor(Color color);

    //hooks, fc 28.2.2004:
    List getHooks();
    Collection getActivatedHooks();
    
	/** Adds the hook to the list of hooks to my node.
	 *  Does not invoke the hook!
	 * @param hook
	 * @return returns the input parameter hook
	 */
	PermanentNodeHook addHook(PermanentNodeHook hook);
	void invokeHook(NodeHook hook);
	/** Removes the hook from the activated hooks, calls shutdown method of the hook and removes the
	 * hook from allHook belonging to the node afterwards. */
    void removeHook(PermanentNodeHook hook); 
	//end hooks
	
	//tooltips,fc 29.2.2004
	void setToolTip(String key, String tip);
	java.util.Map getToolTip();
	
	//additional info, fc, 15.12.2004
	
	/** This method can be used to store non-visual additions to a node. 
	 * Currently, it is used for encrypted nodes to store the encrypted content.
	 * @param info
	 */
	void setAdditionalInfo(String info);
	public String getAdditionalInfo();
        
    MindMapNode shallowCopy();
    public XMLElement save(Writer writer, MindMapLinkRegistry registry) throws IOException;
    
    // fc, 10.2.2005:
    /** State icons are icons that are not saved. They indicate that 
     *  this node is special.
     * @return
     */
    Map getStateIcons();

    /**
     * @param key
     * @param icon use null to remove the state icon. Then it is not 
     * required, that the key already exists.
     */
    void   setStateIcon(String key, ImageIcon icon);    
    
    //fc, 11.4.2005:
    HistoryInformation getHistoryInformation();
    
    void setHistoryInformation(HistoryInformation historyInformation);
    /**
     * @return
     */
    boolean isVisible();
    /**
     * @return
     */
    boolean hasOneVisibleChild();
    /**
     * @return
     */
    MindMap getMap();
    
    NodeAttributeTableModel getAttributes();
    
    public void addNodeViewEventListener(NodeViewEventListener l);

    public void removeNodeViewEventListener(NodeViewEventListener l);

}
