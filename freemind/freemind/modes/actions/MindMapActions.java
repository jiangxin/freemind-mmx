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
 * Created on 05.05.2004
 */
/*$Id: MindMapActions.java,v 1.1.4.14.6.1 2005-07-12 15:41:16 dpolivaev Exp $*/
package freemind.modes.actions;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;

import freemind.modes.MindIcon;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.modes.StylePattern;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.view.mindmapview.NodeView;

/** This is the central method interface of actions that can be undertaken on
 *  nodes. Whenever you want to change the mindmap choose one of these actions
 *  as they do proper redisplay, inform others about the actions, the actions are
 *  all undoable etc.etc.
 *  
 * All these methods do redisplay, because they are offered from the  ModeController for use.
 * 
 * @author foltin
 * @see freemind.modes.ModeController
 * */
public interface MindMapActions {
    /** Call this method, if you changed anything at a node. This method makes the map dirty.
     * @param node
     */
    public void nodeChanged(MindMapNode node);
    /** This is nodeChanged without making the map dirty.
     * @param node
     */
    public void nodeRefresh(MindMapNode node);
	public void nodeStructureChanged(MindMapNode node);
	/** The following modes are present: 
	 *     public final int NEW_CHILD_WITHOUT_FOCUS = 1;  // old model of insertion
	 *     public final int NEW_CHILD = 2;
	 *     public final int NEW_SIBLING_BEHIND = 3;
	 *     public final int NEW_SIBLING_BEFORE = 4;
	 * @see freemind.modes.ControllerAdapter
	 * */
	public void edit(KeyEvent e, boolean addNew, boolean editLong);
	public void setNodeText(MindMapNode selected, String newText);
	public MindMapNode addNew(final MindMapNode target, final int newNodeMode, final KeyEvent e);
	/** Another variant of addNew. If the index of the new node as a child of parent is known,
	 * this method is easier to use. Moreover, it does not automatically start an editor.
	 * @param newNodeIsLeft here, normally parent.isLeft() or null is used. 
	 * @return returns the new node. */
	MindMapNode addNewNode(MindMapNode parent, int index, freemind.main.Tools.BooleanHolder newNodeIsLeft);
	public void deleteNode(MindMapNode selectedNode);
	public Transferable cut();
	public Transferable cut(List nodeList);
	/**
	 * moves selected and selecteds (if they are child of the same parent and adjacent)
	 * in the direction specified (up = -1, down = 1). 
	 * */
	void moveNodes(MindMapNode selected, List selecteds, int direction);
	
	/**
	 * @param node
	 * @param folded
	 */
	void setFolded(MindMapNode node, boolean folded);
	/**
	 * Switches the folding state of all selected nodes. In fact, 
	 * it determines one action (fold or unfold) and applies this action to every
	 * selected node.
	 */
	void toggleFolded();
	
	/** Unfolds a node if necessary.
	 * @param node
	 */
	void displayNode(MindMapNode node);
	/** Node is displayed and selected as the only one selected. It is moved to the center of the
	 *  screen.
	 * @param node
	 */
	void centerNode(MindMapNode node);
	String getLinkShortText(MindMapNode node);

	public void setBold(MindMapNode node, boolean bolded);
	public void setItalic(MindMapNode node, boolean isItalic);
	public void setNodeColor(MindMapNode node, Color color);
	public void setNodeBackgroundColor(MindMapNode node, Color color);
	
	public void blendNodeColor(MindMapNode node);
	public void setFontFamily(
		MindMapNode node,
		String fontFamily);
	public void setFontSize(MindMapNode node, String fontSizeValue);
	/** This method is nice, but how to get a MindIcon ? */
	public void addIcon(MindMapNode node, MindIcon icon);
	public int removeLastIcon(MindMapNode node);
	public void removeAllIcons(MindMapNode node);
	/** @param patternName is one of the names. They can be received using 
	 * the patterns list of ApplyPatternActions from the ControllerAdapter. Each action
	 * has a getPattern() method and the pattern has a getName() method ... */
	public void applyPattern(MindMapNode node, String patternName);
	public void applyPattern(MindMapNode node, StylePattern pattern);
	public void setNodeStyle(MindMapNode node, String style);
	public void setEdgeColor(MindMapNode node, Color color);
	/** The widths range from -1 (for equal to parent) to 0 (thin), 1, 2, 4, 8. */
	public void setEdgeWidth(MindMapNode node, int width);
	public void setEdgeStyle(MindMapNode node, String style);
	public void setCloud(MindMapNode node, boolean enable);
	public void setCloudColor(MindMapNode node, Color color);
//	public void setCloudWidth(MindMapNode node, int width);
//	public void setCloudStyle(MindMapNode node, String style);
	/** Source holds the MindMapArrowLinkModel and points to the id placed in target.*/
	public void addLink(
		MindMapNode source,
		MindMapNode target);
	public void removeReference(MindMapLink arrowLink);
	public void changeArrowsOfArrowLink(
	    MindMapArrowLinkModel arrowLink,
		boolean hasStartArrow,
		boolean hasEndArrow);
	public void setArrowLinkColor(
		MindMapLink arrowLink,
		Color color);
	public void setArrowLinkEndPoints(MindMapArrowLink link, Point startPoint, Point endPoint);
	/** Adds a textual hyperlink to a node (e.g. http:/freemind.sourceforge.net)
	 * @param node
	 * @param link
	 */
	public void setLink(MindMapNode node, String link);
//	public void setUnderlined(MindMapNode node);
//	public void setNormalFont(MindMapNode node);
	public void increaseFontSize(MindMapNode node, int increment);
	public void splitNode(MindMapNode node, int caretPosition, String newText);
	public void joinNodes(MindMapNode selectedNode, List selectedNodes);

	public void paste(Transferable t, MindMapNode parent);
	/** @param isLeft determines, whether or not the node is placed on the left or right. */
	public void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft);
	public void paste(MindMapNode node, MindMapNode parent);
	
	public void addHook(MindMapNode focussed, List selecteds, String hookName);
	public MindMapNode getRootNode();
	
	/**
	 * @param node
	 * @param key key value patterns is used to ensure, that more than one tooltip can be displayed.
	 * @param value null if you want to delete this tooltip.
	 */
	public void setToolTip(MindMapNode node, String key, String value);
	
    /** Single selection: the node is the only one selected after calling this method.
     * @param node
     */
    public void select( NodeView node) ;

    /**Single selection: the node is the only one selected after calling this method.
     * @param selected
     */
    public void select( MindMapNode selected);

    /** Multiple selection.
     * @param focussed will be focussed afterwards
     * @param selecteds are all nodes that are selected (the focussed has not to be contained).
     */
    public void selectMultipleNodes(MindMapNode focussed, Collection selecteds) ;

    /** The branch that starts from selected is added to the selection.
     * 
     * On extend = false clear up the previous selection.
     * if extend is false, the past selection will be empty.
     * if yes, the selection will extended with this node and its children
     */
    public void selectBranch( MindMapNode selected, boolean extend);

	
	/** Moves the node to a new position.
	 * @param node
	 * @param vGap
	 * @param hGap
	 * @param shiftY
	 */
	public void moveNodePosition(MindMapNode node, int vGap, int hGap,
            int shiftY);
    
    /** Load the given map (as String) instead of the currently opened map.
     * @param xmlMapContents
     */
    void load(String xmlMapContents);
    
}