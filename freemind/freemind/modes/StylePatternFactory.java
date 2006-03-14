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
/*$Id: StylePatternFactory.java,v 1.1.2.1 2006-03-14 21:56:27 christianfoltin Exp $*/

package freemind.modes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternEdgeColor;
import freemind.controller.actions.generated.instance.PatternEdgeStyle;
import freemind.controller.actions.generated.instance.PatternEdgeWidth;
import freemind.controller.actions.generated.instance.PatternIcon;
import freemind.controller.actions.generated.instance.PatternNodeBackgroundColor;
import freemind.controller.actions.generated.instance.PatternNodeColor;
import freemind.controller.actions.generated.instance.PatternNodeFontBold;
import freemind.controller.actions.generated.instance.PatternNodeFontItalic;
import freemind.controller.actions.generated.instance.PatternNodeFontName;
import freemind.controller.actions.generated.instance.PatternNodeFontSize;
import freemind.controller.actions.generated.instance.PatternNodeStyle;
import freemind.controller.actions.generated.instance.Patterns;
import freemind.main.Tools;

// Daniel: this seems like a description of what pattern should do rather
// than of that what it actually does.

/**
 * This class represents a StylePattern than can be applied to a node or a whole
 * branch. The properties of the nodes are replaced with the properties saved in
 * the pattern.
 */
public class StylePatternFactory {
    public static final String FALSE_VALUE = "false";
    public static final String TRUE_VALUE = "true";

    public static List loadPatterns(File file) throws Exception {
        return loadPatterns(new BufferedReader(new FileReader(file)));
    }

    /**
     * @param reader
     * @return a List of Pattern elements.
     * @throws Exception
     */
    public static List loadPatterns(Reader reader) throws Exception {
        Patterns patterns = (Patterns) XmlBindingTools.getInstance()
                .unMarshall(reader);
        return patterns.getListChoiceList();
    }

    public static Pattern createPatternFromNode(MindMapNode node) {
        Pattern pattern = new Pattern();

        if (node.getColor() != null) {
            PatternNodeColor subPattern = new PatternNodeColor();
            subPattern.setValue(Tools.colorToXml(node.getColor()));
            pattern.setPatternNodeColor(subPattern);
        }
        if (node.getBackgroundColor() != null) {
            PatternNodeBackgroundColor subPattern = new PatternNodeBackgroundColor();
            subPattern.setValue(Tools.colorToXml(node.getBackgroundColor()));
            pattern.setPatternNodeBackgroundColor(subPattern);
        }
        if (node.getStyle() != null) {
            PatternNodeStyle subPattern = new PatternNodeStyle();
            subPattern.setValue(node.getStyle());
            pattern.setPatternNodeStyle(subPattern);
        }

        PatternNodeFontBold nodeFontBold = new PatternNodeFontBold();
        nodeFontBold.setValue(node.isBold()?TRUE_VALUE:FALSE_VALUE);
        pattern.setPatternNodeFontBold(nodeFontBold);
        PatternNodeFontItalic nodeFontItalic = new PatternNodeFontItalic();
        nodeFontItalic.setValue(node.isItalic()?TRUE_VALUE:FALSE_VALUE);
        pattern.setPatternNodeFontItalic(nodeFontItalic);
        if (node.getFontSize() != null) {
            PatternNodeFontSize nodeFontSize = new PatternNodeFontSize();
            nodeFontSize.setValue(node.getFontSize());
            pattern.setPatternNodeFontSize(nodeFontSize);
        }
        if (node.getFontFamilyName() != null) {
            PatternNodeFontName subPattern = new PatternNodeFontName();
            subPattern.setValue(node.getFontFamilyName());
            pattern.setPatternNodeFontName(subPattern);
        }

        if (node.getIcons().size() == 1) {
            PatternIcon iconPattern = new PatternIcon();
            iconPattern.setValue(((MindIcon) node.getIcons().get(0)).getName());
        }
        if (node.getEdge().getColor() != null) {
            PatternEdgeColor subPattern = new PatternEdgeColor();
            subPattern.setValue(Tools.colorToXml(node.getEdge().getColor()));
            pattern.setPatternEdgeColor(subPattern);
        }
        if (node.getEdge().getStyle() != null) {
            PatternEdgeStyle subPattern = new PatternEdgeStyle();
            subPattern.setValue(node.getEdge().getStyle());
            pattern.setPatternEdgeStyle(subPattern);
        }
        if (node.getEdge().getWidth() != EdgeAdapter.DEFAULT_WIDTH) {
            PatternEdgeWidth subPattern = new PatternEdgeWidth();
            subPattern.setValue("" + node.getEdge().getWidth());
            pattern.setPatternEdgeWidth(subPattern);
        }

        return pattern;
    }

}
