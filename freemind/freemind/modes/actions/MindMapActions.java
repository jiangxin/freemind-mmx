/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;

import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;

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
	public void addNew(final NodeView target, final int newNodeMode, final KeyEvent e);
	public void deleteNode(MindMapNode selectedNode);
	public Transferable cut();

//	public abstract void setNodeColor(MindMapNode node, Color color);
//	public abstract void blendNodeColor(MindMapNode node);
//	public abstract void setNodeFont(MindMapNode node, Font font);
//	public abstract void setEdgeColor(MindMapNode node, Color color);
//	public abstract void setEdgeWidth(MindMapNode node, int width);
//	public abstract void setNodeStyle(MindMapNode node, String style);
//	public abstract void setEdgeStyle(MindMapNode node, String style);
	public abstract void setBold(MindMapNode node, boolean bolded);
//	public abstract void setCloud(MindMapNode node);
//	public abstract void setCloudColor(MindMapNode node, Color color);
//	public abstract void setCloudWidth(MindMapNode node, int width);
//	public abstract void setCloudStyle(MindMapNode node, String style);
//	public abstract void addIcon(MindMapNode node, MindIcon icon);
//	public abstract int removeLastIcon(MindMapNode node);
//	/** Source holds the MindMapArrowLinkModel and points to the id placed in target.*/
//	public abstract void addLink(
//		MindMapNode source,
//		MindMapNode target);
//	public abstract void removeReference(
//		MindMapNode source,
//		MindMapArrowLinkModel arrowLink);
//	public abstract void changeArrowsOfArrowLink(
//		MindMapNode source,
//		MindMapArrowLinkModel arrowLink,
//		boolean hasStartArrow,
//		boolean hasEndArrow);
//	public abstract void setArrowLinkColor(
//		MindMapNode source,
//		MindMapArrowLinkModel arrowLink,
//		Color color);
//	public abstract void setItalic(MindMapNode node);
//	public abstract void setUnderlined(MindMapNode node);
//	public abstract void setNormalFont(MindMapNode node);
//	public abstract void setFontFamily(
//		MindMapNode node,
//		String fontFamily);
//	public abstract void setFontSize(MindMapNode node, int fontSize);
//	public abstract void increaseFontSize(MindMapNode node, int increment);
//	public abstract void splitNode(
//		MindMapNode node,
//		int caretPosition,
//		String newText);
//	//URGENT: This method needs refactoring. At least, it is at the wrong place in the model!!!!
//	public abstract void joinNodes();
//	/*
//	 *
//	 */
	public void paste(Transferable t, MindMapNode parent);
	/** @param isLeft determines, whether or not the node is placed on the left or right. **/
	public void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft);
	public void paste(MindMapNode node, MindMapNode parent);
}