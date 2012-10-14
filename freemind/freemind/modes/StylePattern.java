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


package freemind.modes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import freemind.main.Tools;
import freemind.main.XMLElement;

// Daniel: this seems like a description of what pattern should do rather
// than of that what it actually does.

/**
 * THIS CLASS IS NO LONGER USED!
 * 
 * This class represents a StylePattern than can be applied to a node or a whole
 * branch. The properties of the nodes are replaced with the properties saved in
 * the pattern. If a property "text" is given, this pattern is automatically
 * applied to all nodes that contain the String saved in "text".
 */
public class StylePattern {
	private String name;
	/**
	 * NOT USED: The idea of recursive is redundant. You have a possibility to
	 * select all nodes in a branch easily.
	 */
	private boolean recursive;

	private String text;

	private Color nodeColor;
	private Color nodeBackgroundColor;
	private String nodeStyle;

	private String nodeFontFamily = null;
	private Integer nodeFontSize = null;
	private Boolean nodeFontBold = null;
	private Boolean nodeFontItalic = null;

	private MindIcon nodeIcon;

	private Color edgeColor;
	private String edgeStyle;
	private Integer edgeWidth;

	/** Inhertitable patterns, fc, 3.12.2003. */
	private StylePattern mChildrenStylePattern;

	/**
	 * Empty constructor
	 */
	public StylePattern() {
	}

	public StylePattern(XMLElement elm, List justConstructedPatterns) {
		loadPattern(elm, justConstructedPatterns);
	}

	/**
	 * Constructs a style pattern from a node:
	 */
	public StylePattern(MindMapNode node) {
		nodeColor = node.getColor();
		nodeBackgroundColor = node.getBackgroundColor();
		nodeStyle = node.getStyle();

		nodeFontBold = new Boolean(node.isBold());
		nodeFontItalic = new Boolean(node.isItalic());
		nodeFontSize = node.getFontSize() == null ? null : Integer.valueOf(node
				.getFontSize());
		nodeFontFamily = node.getFontFamilyName();

		nodeIcon = null;
		// appliesToNodeIcon = node.getIcons().size()>0;
		// nodeIcon = (MindIcon)
		// (node.getIcons().size()==0?null:node.getIcons().get(0));

		edgeColor = node.getEdge().getColor();
		edgeStyle = node.getEdge().getStyle();
		edgeWidth = new Integer(node.getEdge().getWidth());

	}

	public String toString() {
		return "node: " + nodeColor + ", " + nodeBackgroundColor + ", "
				+ nodeStyle + ", " + nodeFontFamily + ", " + nodeFontSize
				+ ", " + nodeIcon + ", " + text + ", " + "\nedge: " + edgeColor
				+ ", " + edgeStyle + ", " + edgeWidth;
	}

	public boolean getAppliesToEdge() {
		return edgeColor != null || edgeStyle != null || edgeWidth != null;
	}

	public boolean getAppliesToNode() {
		return nodeBackgroundColor != null || nodeColor != null
				|| nodeStyle != null;
	}

	public boolean getAppliesToNodeFont() {
		return nodeFontBold != null || nodeFontFamily != null
				|| nodeFontItalic != null || nodeFontSize != null;
	}

	public boolean getAppliesToNodeIcon() {
		return nodeIcon != null;
	}

	public boolean getAppliesToChildren() {
		return mChildrenStylePattern != null;
	}

	/**
	 * Get the value of name.
	 * 
	 * @return Value of name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name.
	 * 
	 * @param v
	 *            Value to assign to name.
	 */
	public void setName(String v) {
		this.name = v;
	}

	/**
	 * Determine if the properies of this pattern, of course except the "text"
	 * attribute, apply to all the child nodes of this node.
	 * 
	 * @return Value of recursive.
	 */
	public boolean getRecursive() {
		return recursive;
	}

	/**
	 * Set the value of recursive.
	 * 
	 * @param v
	 *            Value to assign to recursive.
	 */
	public void setRecursive(boolean v) {
		this.recursive = v;
	}

	/**
	 * Get the value of text.
	 * 
	 * @return Value of text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the value of text.
	 * 
	 * @param v
	 *            Value to assign to text.
	 */
	public void setText(String v) {
		this.text = v;
	}

	/**
	 * Get the value of nodeColor.
	 * 
	 * @return Value of nodeColor.
	 */
	public Color getNodeColor() {
		return nodeColor;
	}

	/**
	 * Set the value of nodeColor.
	 * 
	 * @param v
	 *            Value to assign to nodeColor.
	 */
	public void setNodeColor(Color v) {
		this.nodeColor = v;
	}

	public Color getNodeBackgroundColor() {
		return nodeBackgroundColor;
	}

	public void setNodeBackgroundColor(Color nodeBackgroundColor) {
		this.nodeBackgroundColor = nodeBackgroundColor;
	}

	/**
	 * Get the value of nodeStyle.
	 * 
	 * @return Value of nodeStyle.
	 */
	public String getNodeStyle() {
		return nodeStyle;
	}

	/**
	 * Set the value of nodeStyle.
	 * 
	 * @param nodeStyle
	 *            Value to assign to nodeStyle.
	 */
	public void setNodeStyle(String nodeStyle) {
		this.nodeStyle = nodeStyle;
	}

	/**
	 * @return Returns the nodeFontFamily.
	 */
	public String getNodeFontFamily() {
		return nodeFontFamily;
	}

	/**
	 * @param nodeFontFamily
	 *            The nodeFontFamily to set.
	 */
	public void setNodeFontFamily(String nodeFontFamily) {
		this.nodeFontFamily = nodeFontFamily;
	}

	/**
	 * @return Returns the nodeFontSize.
	 */
	public Integer getNodeFontSize() {
		return nodeFontSize;
	}

	/**
	 * @param nodeFontSize
	 *            The nodeFontSize to set.
	 */
	public void setNodeFontSize(Integer nodeFontSize) {
		this.nodeFontSize = nodeFontSize;
	}

	/**
	 * Get the value of icon.
	 * 
	 * @return Value of icon.
	 */
	public MindIcon getNodeIcon() {
		return nodeIcon;
	}

	/**
	 * Set the value of icon.
	 * 
	 * @param nodeIcon
	 *            Value to assign to icon.
	 */
	public void setNodeIcon(MindIcon nodeIcon) {
		this.nodeIcon = nodeIcon;
	}

	/**
	 * Get the value of edgeColor.
	 * 
	 * @return Value of edgeColor.
	 */
	public Color getEdgeColor() {
		return edgeColor;
	}

	/**
	 * Set the value of edgeColor.
	 * 
	 * @param edgeColor
	 *            Value to assign to edgeColor.
	 */
	public void setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
	}

	/**
	 * Get the value of edgeStyle.
	 * 
	 * @return Value of edgeStyle.
	 */
	public String getEdgeStyle() {
		return edgeStyle;
	}

	/**
	 * Set the value of edgeStyle.
	 * 
	 * @param edgeStyle
	 *            Value to assign to edgeStyle.
	 */
	public void setEdgeStyle(String edgeStyle) {
		this.edgeStyle = edgeStyle;
	}

	/**
	 * Get the value of edgeWidth.
	 * 
	 * @return Value of edgeWidth.
	 */
	public Integer getEdgeWidth() {
		return edgeWidth;
	}

	/**
	 * Set the value of edgeWidth.
	 * 
	 * @param edgeWidth
	 *            Value to assign to edgeWidth.
	 */
	public void setEdgeWidth(Integer edgeWidth) {
		this.edgeWidth = edgeWidth;
	}

	/**
	 * Get the value of ChildrenStylePattern.
	 * 
	 * @return Value of ChildrenStylePattern.
	 */
	public StylePattern getChildrenStylePattern() {
		return mChildrenStylePattern;
	}

	/**
	 * Set the value of ChildrenStylePattern.
	 * 
	 * @param pChildrenStylePattern
	 *            Value to assign to ChildrenStylePattern.
	 */
	public void setChildrenStylePattern(StylePattern pChildrenStylePattern) {
		this.mChildrenStylePattern = pChildrenStylePattern;
	}

	public static List loadPatterns(File file) throws Exception {
		return loadPatterns(new BufferedReader(new FileReader(file)));
	}

	public static List loadPatterns(Reader reader) throws Exception {
		List list = new LinkedList();
		XMLElement parser = new XMLElement();
		parser.parseFromReader(reader);
		for (Enumeration e = parser.enumerateChildren(); e.hasMoreElements();) {
			list.add(new StylePattern((XMLElement) e.nextElement(), list));
		}
		return list;
	}

	protected void loadPattern(XMLElement pattern, List justConstructedPatterns) {
		// PATTERN
		if (pattern.getStringAttribute("name") != null) {
			setName(pattern.getStringAttribute("name"));
		}
		if (Tools.safeEquals(pattern.getStringAttribute("recursive"), "true")) {
			setRecursive(true);
		}

		for (Iterator i = pattern.getChildren().iterator(); i.hasNext();) {
			// this has to be improved!
			// NODE
			XMLElement child = (XMLElement) i.next();
			if (child.getName().equals("node")) {
				if (child.getStringAttribute("color") != null
						&& child.getStringAttribute("color").length() == 7) {
					setNodeColor(Tools.xmlToColor(child
							.getStringAttribute("color")));
				}
				if (child.getStringAttribute("background_color") != null
						&& child.getStringAttribute("background_color")
								.length() == 7) {
					setNodeBackgroundColor(Tools.xmlToColor(child
							.getStringAttribute("background_color")));
				}
				if (child.getStringAttribute("style") != null) {
					setNodeStyle(child.getStringAttribute("style"));
				}
				if (child.getStringAttribute("icon") != null) {
					setNodeIcon(child.getStringAttribute("icon").equals("none") ? null
							: MindIcon
									.factory(child.getStringAttribute("icon")));
				}
				setText(child.getStringAttribute("text"));

				for (Iterator j = child.getChildren().iterator(); j.hasNext();) {
					XMLElement nodeChild = (XMLElement) j.next();
					// FONT
					if (nodeChild.getName().equals("font")) {

						if (nodeChild.getStringAttribute("name") != null) {
							setNodeFontFamily(nodeChild
									.getStringAttribute("name"));
						}
						if (Tools.safeEquals(
								nodeChild.getStringAttribute("bold"), "true")) {
							setNodeFontBold(Boolean.TRUE);
						}
						if (Tools.safeEquals(
								nodeChild.getStringAttribute("italic"), "true")) {
							setNodeFontItalic(Boolean.TRUE);
						}
						// if (font.getProperty("underline")!=null &&
						// nodeChild.getProperty("underline").equals("true"))
						// setUnderlined(true);
						if (nodeChild.getStringAttribute("size") != null) {
							setNodeFontSize(Integer.valueOf(nodeChild
									.getStringAttribute("size")));
						}

					}
				}
			}

			// EDGE
			if (child.getName().equals("edge")) {
				if (child.getStringAttribute("style") != null) {
					setEdgeStyle(child.getStringAttribute("style"));
				}
				if (child.getStringAttribute("color") != null) {
					setEdgeColor(Tools.xmlToColor(child
							.getStringAttribute("color")));
				}
				if (child.getStringAttribute("width") != null) {
					if (child.getStringAttribute("width").equals("thin")) {
						setEdgeWidth(new Integer(
								freemind.modes.EdgeAdapter.WIDTH_THIN));
					} else {
						setEdgeWidth(new Integer(Integer.parseInt(child
								.getStringAttribute("width"))));
					}
				}
			}

			// CHILD
			if (child.getName().equals("child")) {
				if (child.getStringAttribute("pattern") != null) {
					// find name in list of justConstructedPatterns:
					String searchName = child.getStringAttribute("pattern");
					boolean anythingFound = false;
					for (ListIterator e = justConstructedPatterns
							.listIterator(); e.hasNext();) {
						StylePattern patternFound = (StylePattern) e.next();
						if (patternFound.getName().equals(searchName)) {
							setChildrenStylePattern(patternFound);
							anythingFound = true;
							break;
						}
					}
					// perhaps our own pattern?
					if (getName().equals(searchName)) {
						setChildrenStylePattern(this);
						anythingFound = true;
					}
					if (anythingFound == false)
						System.err.println("Cannot find the children "
								+ searchName + " to the pattern " + getName());
				}
			}
		}
	}

	/**
	 * @return Returns the nodeFontBold.
	 */
	public Boolean getNodeFontBold() {
		return nodeFontBold;
	}

	/**
	 * @param nodeFontBold
	 *            The nodeFontBold to set.
	 */
	public void setNodeFontBold(Boolean nodeFontBold) {
		this.nodeFontBold = nodeFontBold;
	}

	/**
	 * @return Returns the nodeFontItalic.
	 */
	public Boolean getNodeFontItalic() {
		return nodeFontItalic;
	}

	/**
	 * @param nodeFontItalic
	 *            The nodeFontItalic to set.
	 */
	public void setNodeFontItalic(Boolean nodeFontItalic) {
		this.nodeFontItalic = nodeFontItalic;
	}

}

/*
 * Is saving necessary? public void savePattern(File file) { try { //CODE FOR
 * NANOXML XMLElement pattern = new XMLElement(); pattern.setTagName("pattern");
 * XMLElement node = new XMLElement(); node.setTagName("node");
 * pattern.addChild(node); XMLElement edge = new XMLElement();
 * edge.setTagName("edge"); pattern.addChild(edge);
 * 
 * 
 * pattern.addChild(((MindMapNodeModel)getRoot()).save());
 * 
 * XMLElement node = new XMLElement(); node.setTagName("node");
 * 
 * node.addProperty("text",this.toString());
 * 
 * // ((MindMapEdgeModel)getEdge()).save(doc,node);
 * 
 * XMLElement edge = ((MindMapEdgeModel)getEdge()).save(); if (edge != null) {
 * node.addChild(edge); }
 * 
 * if (isFolded()) { node.addProperty("folded","true"); }
 * 
 * if (color != null) { node.addProperty("color", Tools.colorToXml(getColor()));
 * }
 * 
 * if (style != null) { node.addProperty("style", getStyle()); }
 * 
 * //link if (getLink() != null) { node.addProperty("link", getLink()); }
 * 
 * //font if (font!=null || font.getSize()!=0 || isBold() || isItalic() ||
 * isUnderlined() ) { XMLElement fontElement = new XMLElement();
 * fontElement.setTagName("font");
 * 
 * if (font != null) { fontElement.addProperty("name",getFont().getFontName());
 * } if (font.getSize() != 0) {
 * fontElement.addProperty("size",Integer.toString(getFont().getSize())); } if
 * (isBold()) { fontElement.addProperty("bold","true"); } if (isItalic()) {
 * fontElement.addProperty("italic","true"); } if (isUnderlined()) {
 * fontElement.addProperty("underline","true"); } node.addChild(fontElement); }
 * 
 * 
 * 
 * //Generating output Stream BufferedWriter fileout = new BufferedWriter( new
 * OutputStreamWriter( new FileOutputStream(file) ) ); pattern.write(fileout);
 * 
 * fileout.close();
 * 
 * } catch(Exception e) {
 * System.err.println("Error in MindMapMapModel.saveXML(): ");
 * freemind.main.Resources.getInstance().logExecption(e); } }
 */

