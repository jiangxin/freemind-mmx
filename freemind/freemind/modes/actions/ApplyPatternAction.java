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

package freemind.modes.actions;

import java.util.ListIterator;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.StylePattern;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class ApplyPatternAction extends NodeGeneralAction implements
        SingleNodeOperation {
    private StylePattern mpattern;

    public ApplyPatternAction(ModeController controller, StylePattern pattern) {
        super(controller, null /* no text */, null /* = no icon */);
        setName(pattern.getName());
        this.mpattern = pattern;
        setSingleNodeOperation(this);
    }

    public void apply(MindMapMapModel map, MindMapNodeModel node) {
        applyPattern(node, mpattern);
    }

    public void applyPattern(MindMapNode node, StylePattern pattern) {
        if (pattern.getAppliesToNode()) {
            if (pattern.getText() != null) {
                getModeController().setNodeText(node, pattern.getText());
            }
            getModeController().setNodeColor(node, pattern.getNodeColor());
            getModeController().setNodeBackgroundColor(node, pattern.getNodeBackgroundColor());
            //FIXME: fc, 3.1.2004: setting the style to "null" causes strange behaviour. 
            // see https://sourceforge.net/tracker/?func=detail&atid=107118&aid=1094623&group_id=7118
            if (pattern.getNodeStyle() != null) {
                getModeController().setNodeStyle(node, pattern.getNodeStyle());
            }
            if (pattern.getAppliesToNodeIcon()) {
                if (pattern.getNodeIcon() == null) {
                    while (getModeController().removeLastIcon(node) > 0) {
                    }
                } else {
                    getModeController().addIcon(node, pattern.getNodeIcon());
                }
            } // fc, 28.9.2003
            if (pattern.getAppliesToNodeFont()) {
                String nodeFontFamily = pattern.getNodeFontFamily();
                if (nodeFontFamily == null) {
                    nodeFontFamily = getModeController().getController().getDefaultFontFamilyName();
                }
                getModeController().setFontFamily(node,
                        nodeFontFamily);
                Integer nodeFontSize = pattern.getNodeFontSize();
                if (nodeFontSize == null) {
                    nodeFontSize = new Integer(getModeController().getController().getDefaultFontSize());
                }
                getModeController().setFontSize(node,
                        String.valueOf(nodeFontSize));
                if (pattern.getNodeFontItalic() != null) {
                    getModeController().setItalic(node,
                            pattern.getNodeFontItalic().booleanValue());
                } else {
                    getModeController().setItalic(node,false);
                    
                }
                if (pattern.getNodeFontBold() != null) {
                    getModeController().setBold(node,
                            pattern.getNodeFontBold().booleanValue());
                } else {
                    getModeController().setBold(node,false);
                }
            }
        }

        if (pattern.getAppliesToEdge()) {
            getModeController().setEdgeColor(node, pattern.getEdgeColor());
            getModeController().setEdgeStyle(node, pattern.getEdgeStyle());
            getModeController().setEdgeWidth(node, pattern.getEdgeWidth());
        }

        if (pattern.getAppliesToChildren()) {
            for (ListIterator i = node.childrenUnfolded(); i.hasNext();) {
                NodeAdapter child = (NodeAdapter) i.next();
                applyPattern(child, pattern.getChildrenStylePattern());
            }
        }
    }

    /**
     * @return Returns the pattern.
     */
    public StylePattern getPattern() {
        return mpattern;
    }
}