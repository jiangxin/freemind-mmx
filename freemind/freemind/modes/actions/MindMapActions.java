/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.Transferable;

import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MindMapActions {
	//  All these methods do redisplay, because they are offered to controller for use.
//	public abstract void setNodeColor(MindMapNodeModel node, Color color);
//	public abstract void blendNodeColor(MindMapNodeModel node);
//	public abstract void setNodeFont(MindMapNodeModel node, Font font);
//	public abstract void setEdgeColor(MindMapNodeModel node, Color color);
//	public abstract void setEdgeWidth(MindMapNodeModel node, int width);
//	public abstract void setNodeStyle(MindMapNodeModel node, String style);
//	public abstract void setEdgeStyle(MindMapNodeModel node, String style);
//	public abstract void setBold(MindMapNodeModel node);
//	public abstract void setCloud(MindMapNodeModel node);
//	public abstract void setCloudColor(MindMapNodeModel node, Color color);
//	public abstract void setCloudWidth(MindMapNodeModel node, int width);
//	public abstract void setCloudStyle(MindMapNodeModel node, String style);
//	public abstract void addIcon(MindMapNodeModel node, MindIcon icon);
//	public abstract int removeLastIcon(MindMapNodeModel node);
//	/** Source holds the MindMapArrowLinkModel and points to the id placed in target.*/
//	public abstract void addLink(
//		MindMapNodeModel source,
//		MindMapNodeModel target);
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
//	public abstract void setItalic(MindMapNodeModel node);
//	public abstract void setUnderlined(MindMapNodeModel node);
//	public abstract void setNormalFont(MindMapNodeModel node);
//	public abstract void setFontFamily(
//		MindMapNodeModel node,
//		String fontFamily);
//	public abstract void setFontSize(MindMapNodeModel node, int fontSize);
//	public abstract void increaseFontSize(MindMapNodeModel node, int increment);
//	public abstract void splitNode(
//		MindMapNode node,
//		int caretPosition,
//		String newText);
//	//URGENT: This method needs refactoring. At least, it is at the wrong place in the model!!!!
//	public abstract void joinNodes();
//	/*
//	 *
//	 */
//	public abstract void paste(
//		Transferable t,
//		MindMapNode target,
//		boolean asSibling,
//		boolean isLeft);
}