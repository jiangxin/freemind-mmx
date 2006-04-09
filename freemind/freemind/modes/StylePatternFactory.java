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
/*$Id: StylePatternFactory.java,v 1.1.2.3.2.1 2006-04-09 13:34:38 dpolivaev Exp $*/

package freemind.modes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import freemind.common.XmlBindingTools;
import freemind.common.PropertyControl.TextTranslator;
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

/**
 * This class constructs patterns from files or from nodes and saves them back.
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

	/**
	 * @param writer
	 *            the result is written to, and it is closed afterwards
	 * @param listOfPatterns
	 *            List of Pattern elements.
	 * @throws Exception
	 */
	public static void savePatterns(Writer writer, List listOfPatterns)
			throws Exception {
		Patterns patterns = new Patterns();
		for (Iterator iter = listOfPatterns.iterator(); iter.hasNext();) {
			Pattern pattern = (Pattern) iter.next();
			patterns.addChoice(pattern);
		}
		String marshalledResult = XmlBindingTools.getInstance().marshall(
				patterns);
		writer.write(marshalledResult);
		writer.close();
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
		nodeFontBold.setValue(node.isBold() ? TRUE_VALUE : FALSE_VALUE);
		pattern.setPatternNodeFontBold(nodeFontBold);
		PatternNodeFontItalic nodeFontItalic = new PatternNodeFontItalic();
		nodeFontItalic.setValue(node.isItalic() ? TRUE_VALUE : FALSE_VALUE);
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

	public static String toString(Pattern pPattern, TextTranslator translator) {
		String result = "";
		if (pPattern.getPatternNodeColor() != null) {
			result = addSeparatorIfNecessary(result);
			if (pPattern.getPatternNodeColor().getValue() == null) {
				result += "-" + translator.getText("PatternToString.color");
			} else {
				result += "+" + translator.getText("PatternToString.color");
			}
		}
		if (pPattern.getPatternNodeBackgroundColor() != null) {
			result = addSeparatorIfNecessary(result);
			if (pPattern.getPatternNodeBackgroundColor().getValue() == null) {
				result += "-"
						+ translator.getText("PatternToString.backgroundColor");
			} else {
				result += "+"
						+ translator.getText("PatternToString.backgroundColor");
			}
		}
		if (pPattern.getPatternNodeFontSize() != null) {
			result = addSeparatorIfNecessary(result);
			if (pPattern.getPatternNodeFontSize().getValue() == null) {
				result += "-"
						+ translator.getText("PatternToString.NodeFontSize");
			} else {
				result += "+"
						+ translator.getText("PatternToString.NodeFontSize") + " "
						+ pPattern.getPatternNodeFontSize().getValue();
			}
		}
		// TODO: Add rest here.
		result += " ...";
		return result;
	}

	private static String addSeparatorIfNecessary(String result) {
		if (result.length() > 0) {
			result += ", ";
		}
		return result;
	}

	private static final String PATTERN_DUMMY = "<pattern name='dummy'/>";

	public static Pattern getPatternFromString(String pattern) {
		String patternString = pattern;
		if (patternString == null) {
			patternString = PATTERN_DUMMY;
		}
		Pattern pat = (Pattern) XmlBindingTools.getInstance().unMarshall(
				patternString);
		return pat;
	}

    private static final String PATTERNS_DUMMY = "<patterns/>";
	public static Patterns getPatternsFromString(String patterns) {
	    String patternsString = patterns;
	    if (patternsString == null) {
	        patternsString = PATTERNS_DUMMY;
	    }
	    Patterns pat = (Patterns) XmlBindingTools.getInstance().unMarshall(
	            patternsString);
	    return pat;
	}

}
