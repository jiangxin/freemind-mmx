/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.List;

import freemind.modes.MindIcon;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.modes.StylePattern;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MindMapActions {
	public void nodeStructureChanged(MindMapNode node);
	//  All these methods do redisplay, because they are offered to controller for use.
	public void edit(KeyEvent e, boolean addNew, boolean editLong);
	/** The following modes are present: 
	 *     public final int NEW_CHILD_WITHOUT_FOCUS = 1;  // old model of insertion
	 *     public final int NEW_CHILD = 2;
	 *     public final int NEW_SIBLING_BEHIND = 3;
	 *     public final int NEW_SIBLING_BEFORE = 4;
	 * @see freemind.modes.ControllerAdapter
	 * */
	public void setNodeText(MindMapNode selected, String newText);
	public MindMapNode addNew(final MindMapNode target, final int newNodeMode, final KeyEvent e);
	/** Another variant of addNew. If the index of the new node as a child of parent is known,
	 * this method is easier to use. Moreover, it does not automatically start an editor.
	 * @param newNodeIsLeft here, normally parent.isLeft() or null is used. 
	 * @return returns the new node. */
	MindMapNode addNewNode(MindMapNode parent, int index, freemind.main.Tools.BooleanHolder newNodeIsLeft);
	public void deleteNode(MindMapNode selectedNode);
	public Transferable cut();
	/**
	 * moves selected and selecteds (if they are child of the same parent and adjacent)
	 * in the direction specified (up = -1, down = 1). 
	 * */
	void moveNodes(MindMapNode selected, List selecteds, int direction);
	
	void setFolded(MindMapNode node, boolean folded);
	void toggleFolded();

	public void setBold(MindMapNode node, boolean bolded);
	public void setItalic(MindMapNode node, boolean isItalic);
	public void setNodeColor(MindMapNode node, Color color);
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
//	/** Source holds the MindMapArrowLinkModel and points to the id placed in target.*/
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
//	public void setUnderlined(MindMapNode node);
//	public void setNormalFont(MindMapNode node);
//	public void increaseFontSize(MindMapNode node, int increment);
//	public void splitNode(
//		MindMapNode node,
//		int caretPosition,
//		String newText);
//	//URGENT: This method needs refactoring. At least, it is at the wrong place in the model!!!!
//	public void joinNodes();
//	/*
//	 *
//	 */
	public void paste(Transferable t, MindMapNode parent);
	/** @param isLeft determines, whether or not the node is placed on the left or right. **/
	public void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft);
	public void paste(MindMapNode node, MindMapNode parent);
	
	//public void addHook(MindMapNode focussed, List selecteds, String hookName);
}