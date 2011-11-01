/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 05.10.2004
 */
/*
 * $Id: ApplyPatternAction.java,v 1.16.10.1 05.10.2004 11:32:42 christianfoltin
 * Exp $
 */

package freemind.modes.mindmapmode.actions;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternEdgeWidth;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.MindMapControllerPlugin;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class ApplyPatternAction extends NodeGeneralAction implements
		SingleNodeOperation {
	public interface ExternalPatternAction extends MindMapControllerPlugin {
		public void act(MindMapNode node, Pattern pattern);
	}

	private Pattern mpattern;

	public ApplyPatternAction(MindMapController controller, Pattern pattern) {
		super(controller, null /* no text */, null /* = no icon */);
		setName(pattern.getName());
		this.mpattern = pattern;
		setSingleNodeOperation(this);
	}

	public void apply(MindMapMapModel map, MindMapNodeModel node) {
		applyPattern(node, mpattern);
	}

	public void applyPattern(MindMapNode node, Pattern pattern) {
		if (pattern.getPatternNodeText() != null) {
			if (pattern.getPatternNodeText().getValue() != null) {
				getMindMapController().setNodeText(node,
						pattern.getPatternNodeText().getValue());
			} else {
				// clear text:
				getMindMapController().setNodeText(node, "");
			}
		}
		if (pattern.getPatternNodeColor() != null) {
			getMindMapController().setNodeColor(node,
					Tools.xmlToColor(pattern.getPatternNodeColor().getValue()));
		}
		if (pattern.getPatternNodeBackgroundColor() != null) {
			getMindMapController().setNodeBackgroundColor(
					node,
					Tools.xmlToColor(pattern.getPatternNodeBackgroundColor()
							.getValue()));
		}
		// Perhaps already fixed?:
		// FIXME: fc, 3.1.2004: setting the style to "null" causes strange
		// behaviour.
		// see
		// https://sourceforge.net/tracker/?func=detail&atid=107118&aid=1094623&group_id=7118
		if (pattern.getPatternNodeStyle() != null) {
			getMindMapController().setNodeStyle(node,
					pattern.getPatternNodeStyle().getValue());
		}
		if (pattern.getPatternIcon() != null) {
			String iconName = pattern.getPatternIcon().getValue();
			if (iconName == null) {
				while (getMindMapController().removeLastIcon(node) > 0) {
				}
			} else {
				// check if icon is already present:
				List icons = node.getIcons();
				boolean found = false;
				for (Iterator iterator = icons.iterator(); iterator.hasNext();) {
					MindIcon icon = (MindIcon) iterator.next();
					if (icon.getName() != null
							&& icon.getName().equals(iconName)) {
						found = true;
						break;
					}
				}
				if (!found) {
					getMindMapController().addIcon(node,
							MindIcon.factory(iconName));
				}
			}
		} // fc, 28.9.2003
		if (pattern.getPatternNodeFontName() != null) {
			String nodeFontFamily = pattern.getPatternNodeFontName().getValue();
			if (nodeFontFamily == null) {
				nodeFontFamily = getMindMapController().getController()
						.getDefaultFontFamilyName();
			}
			getMindMapController().setFontFamily(node, nodeFontFamily);
		}
		if (pattern.getPatternNodeFontSize() != null) {
			String nodeFontSize = pattern.getPatternNodeFontSize().getValue();
			if (nodeFontSize == null) {
				nodeFontSize = ""
						+ getMindMapController().getController()
								.getDefaultFontSize();
			}
			getMindMapController().setFontSize(node,
					String.valueOf(nodeFontSize));
		}
		if (pattern.getPatternNodeFontItalic() != null) {
			getMindMapController()
					.setItalic(
							node,
							"true".equals(pattern.getPatternNodeFontItalic()
									.getValue()));
		}
		if (pattern.getPatternNodeFontBold() != null) {
			getMindMapController().setBold(node,
					"true".equals(pattern.getPatternNodeFontBold().getValue()));
		}

		if (pattern.getPatternEdgeColor() != null) {
			getMindMapController().setEdgeColor(node,
					Tools.xmlToColor(pattern.getPatternEdgeColor().getValue()));
		}
		if (pattern.getPatternEdgeStyle() != null) {
			getMindMapController().setEdgeStyle(node,
					pattern.getPatternEdgeStyle().getValue());
		}
		PatternEdgeWidth patternEdgeWidth = pattern.getPatternEdgeWidth();
		if (patternEdgeWidth != null) {
			if (patternEdgeWidth.getValue() != null) {
				getMindMapController().setEdgeWidth(node,
						edgeWidthStringToInt(patternEdgeWidth.getValue()));
			} else {
				getMindMapController().setEdgeWidth(node,
						EdgeAdapter.DEFAULT_WIDTH);
			}
		}

		if (pattern.getPatternChild() != null
				&& pattern.getPatternChild().getValue() != null) {
			// find children among all patterns:
			String searchedPatternName = pattern.getPatternChild().getValue();
			ApplyPatternAction[] patterns = getMindMapController().patterns;
			for (int i = 0; i < patterns.length; i++) {
				ApplyPatternAction action = patterns[i];
				if (action.getPattern().getName().equals(searchedPatternName)) {
					for (ListIterator j = node.childrenUnfolded(); j.hasNext();) {
						NodeAdapter child = (NodeAdapter) j.next();
						applyPattern(child, action.getPattern());
					}
					break;
				}
			}
		}
		for (Iterator i = getMindMapController().getPlugins().iterator(); i
				.hasNext();) {
			MindMapControllerPlugin action = (MindMapControllerPlugin) i.next();
			if (action instanceof ExternalPatternAction) {
				ExternalPatternAction externalAction = (ExternalPatternAction) action;
				externalAction.act(node, pattern);
			}
		}
	}

	/**
     */
	public static int edgeWidthStringToInt(String value) {
		if (value == null) {
			return EdgeAdapter.DEFAULT_WIDTH;
		}
		if (value.equals(EdgeAdapter.EDGE_WIDTH_THIN_STRING)) {
			return EdgeAdapter.WIDTH_THIN;
		}
		return Integer.valueOf(value).intValue();
	}

	/**
     */
	public static String edgeWidthIntToString(int value) {
		if (value == EdgeAdapter.DEFAULT_WIDTH) {
			return null;
		}
		if (value == EdgeAdapter.WIDTH_THIN) {
			return EdgeAdapter.EDGE_WIDTH_THIN_STRING;
		}
		return Integer.toString(value);
	}

	/**
	 * @return Returns the pattern.
	 */
	public Pattern getPattern() {
		return mpattern;
	}

}
