/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 25.02.2006
 */
/*$Id: StylePatternFrame.java,v 1.1.2.4 2006-02-27 18:49:01 christianfoltin Exp $*/
package freemind.modes.mindmapmode.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import freemind.common.BooleanProperty;
import freemind.common.ColorProperty;
import freemind.common.ComboProperty;
import freemind.common.FontProperty;
import freemind.common.PropertyControl;
import freemind.common.SeparatorProperty;
import freemind.common.StringProperty;
import freemind.common.PropertyControl.TextTranslator;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.StylePattern;

/**
 * @author foltin
 * 
 */
public class StylePatternFrame extends JPanel implements TextTranslator {

	private static final String[] EDGE_STYLES = new String[] {
			EdgeAdapter.EDGESTYLE_LINEAR, EdgeAdapter.EDGESTYLE_BEZIER,
			EdgeAdapter.EDGESTYLE_SHARP_LINEAR,
			EdgeAdapter.EDGESTYLE_SHARP_BEZIER };

	private static final String[] EDGE_WIDTHS = new String[] {
			"EdgeWidth_parent", "EdgeWidth_thin", "EdgeWidth_1", "EdgeWidth_2",
			"EdgeWidth_4", "EdgeWidth_8" };

	private static final String FALSE_VALUE = "false";

	private static final String TRUE_VALUE = "true";

	private static final String NODE_BACKGROUND_COLOR = "nodebackgroundcolor";

	private static final String SET_NODE_BACKGROUND_COLOR = "setnodebackgroundcolor";

	private static final String NODE_COLOR = "nodecolor";

	private static final String SET_NODE_COLOR = "setnodecolor";

	private static final String SET_NODE_STYLE = "setnodestyle";

	private static final String NODE_STYLE = "nodestyle";

	private static final String NODE_FONT = "nodefont";

	private static final String SET_NODE_FONT = "setnodefont";

	private static final String SET_NODE_TEXT = "setnodetext";

	private static final String NODE_TEXT = "nodetext";

	private static final String SET_EDGE_WIDTH = "setedgewidth";

	private static final String EDGE_WIDTH = "edgewidth";

	private static final String SET_EDGE_STYLE = "setedgestyle";

	private static final String EDGE_STYLE = "edgestyle";

	private static final String SET_EDGE_COLOR = "setedgecolor";

	private static final String EDGE_COLOR = "edgecolor";

	private final TextTranslator mTranslator;

	private Vector mControls;

	private BooleanProperty mSetNodeColor;

	private ColorProperty mNodeColor;

	private BooleanProperty mSetNodeBackgroundColor;

	private ColorProperty mNodeBackgoundColor;

	private BooleanProperty mSetNodeStyle;

	private ComboProperty mNodeStyle;

	private FontProperty mNodeFont;

	private BooleanProperty mSetNodeFont;

	private BooleanProperty mSetNodeText;

	private StringProperty mNodeText;

	private BooleanProperty mSetEdgeWidth;

	private ComboProperty mEdgeWidth;

	private BooleanProperty mSetEdgeStyle;

	private ComboProperty mEdgeStyle;

	private BooleanProperty mSetEdgeColor;

	private ColorProperty mEdgeColor;

	/**
	 * @throws HeadlessException
	 */
	public StylePatternFrame(TextTranslator pTranslator)
			throws HeadlessException {
		super();
		mTranslator = pTranslator;
	}

	/**
	 * Creates all controls and adds them to the frame.
	 */
	public void init() {
		CardLayout cardLayout = new CardLayout();
		JPanel rightStack = new JPanel(cardLayout);
		String form = "right:max(40dlu;p), 4dlu, 10dlu, 7dlu,right:max(40dlu;p), 4dlu, 80dlu, 7dlu";
		FormLayout rightLayout = new FormLayout(form, "");
		DefaultFormBuilder rightBuilder = new DefaultFormBuilder(rightLayout);
		rightBuilder.setDefaultDialogBorder();
		mControls = getControls();
		for (Iterator i = mControls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
			control.layout(rightBuilder, this);
		}
		// add the last one, too
		rightStack.add(rightBuilder.getPanel(), "testTab");
		add(rightStack, BorderLayout.CENTER);
	}

	private Vector getControls() {
		Vector controls = new Vector();
		controls.add(new SeparatorProperty("NodeColors"));
		mSetNodeColor = new BooleanProperty(SET_NODE_COLOR + ".tooltip",
				SET_NODE_COLOR);
		controls.add(mSetNodeColor);
		mNodeColor = new ColorProperty(NODE_COLOR + ".tooltip", NODE_COLOR,
				"#ffffff", this);
		controls.add(mNodeColor);
		mSetNodeBackgroundColor = new BooleanProperty(SET_NODE_BACKGROUND_COLOR
				+ ".tooltip", SET_NODE_BACKGROUND_COLOR);
		controls.add(mSetNodeBackgroundColor);
		mNodeBackgoundColor = new ColorProperty(NODE_BACKGROUND_COLOR
				+ ".tooltip", NODE_BACKGROUND_COLOR, "#000000", this);
		controls.add(mNodeBackgoundColor);
		controls.add(new SeparatorProperty("NodeStyles"));
		mSetNodeStyle = new BooleanProperty(SET_NODE_STYLE + ".tooltip",
				SET_NODE_STYLE);
		controls.add(mSetNodeStyle);
		mNodeStyle = new ComboProperty(NODE_STYLE + ".tooltip", NODE_STYLE,
				MindMapNode.NODE_STYLES, this);
		controls.add(mNodeStyle);
		mSetNodeFont = new BooleanProperty(SET_NODE_FONT + ".tooltip",
				SET_NODE_FONT);
		controls.add(mSetNodeFont);
		mNodeFont = new FontProperty(NODE_FONT + ".tooltip", NODE_FONT, this);
		controls.add(mNodeFont);
		/* **** */
		mSetNodeText = new BooleanProperty(SET_NODE_TEXT + ".tooltip",
				SET_NODE_TEXT);
		controls.add(mSetNodeText);
		mNodeText = new StringProperty(NODE_TEXT + ".tooltip", NODE_TEXT);
		controls.add(mNodeText);
		/* **** */
		controls.add(new SeparatorProperty("EdgeControls"));
		mSetEdgeWidth = new BooleanProperty(SET_EDGE_WIDTH + ".tooltip",
				SET_EDGE_WIDTH);
		controls.add(mSetEdgeWidth);
		mEdgeWidth = new ComboProperty(EDGE_WIDTH + ".tooltip", EDGE_WIDTH,
				EDGE_WIDTHS, this);
		controls.add(mEdgeWidth);
		/* **** */
		mSetEdgeStyle = new BooleanProperty(SET_EDGE_STYLE + ".tooltip",
				SET_EDGE_STYLE);
		controls.add(mSetEdgeStyle);
		mEdgeStyle = new ComboProperty(EDGE_STYLE + ".tooltip", EDGE_STYLE,
				EDGE_STYLES, this);
		controls.add(mEdgeStyle);
		/* **** */
		mSetEdgeColor = new BooleanProperty(SET_EDGE_COLOR + ".tooltip",
				SET_EDGE_COLOR);
		controls.add(mSetEdgeColor);
		mEdgeColor = new ColorProperty(EDGE_COLOR + ".tooltip", EDGE_COLOR,
				"#000000", this);
		controls.add(mEdgeColor);
		return controls;
	}

	public String getText(String pKey) {
		return mTranslator.getText("PatternDialog." + pKey);
	}

	public void setPattern(StylePattern pattern) {
		mSetNodeColor.setValue((pattern.getNodeColor() == null) ? FALSE_VALUE
				: TRUE_VALUE);
		mNodeColor.setValue(Tools.colorToXml(pattern.getNodeColor()));
		mSetNodeBackgroundColor
				.setValue((pattern.getNodeBackgroundColor() == null) ? FALSE_VALUE
						: TRUE_VALUE);
		mNodeBackgoundColor.setValue(Tools.colorToXml(pattern
				.getNodeBackgroundColor()));
		mSetNodeStyle.setValue((pattern.getNodeStyle() == null) ? FALSE_VALUE
				: TRUE_VALUE);
		mNodeStyle
				.setValue((pattern.getNodeStyle() == null) ? MindMapNode.STYLE_AS_PARENT
						: pattern.getNodeStyle());
		mSetNodeFont
				.setValue((pattern.getAppliesToNodeFont() == false) ? FALSE_VALUE
						: TRUE_VALUE);
		if (pattern.getAppliesToNodeFont()) {
			mNodeFont.setFontValue(new Font(pattern.getNodeFontFamily(),
					getFontStyleFromPattern(pattern), (pattern
							.getNodeFontSize() != null) ? pattern
							.getNodeFontSize().intValue() : 12));
		} else {
			mNodeFont.setFontValue(null);
		}
		mSetNodeText.setValue((pattern.getText() == null) ? FALSE_VALUE
				: TRUE_VALUE);
		mNodeText
				.setValue((pattern.getText() == null) ? "" : pattern.getText());
		mSetEdgeColor.setValue((pattern.getEdgeColor() == null) ? FALSE_VALUE
				: TRUE_VALUE);
		mEdgeColor.setValue(Tools.colorToXml(pattern.getEdgeColor()));
		mSetEdgeWidth.setValue((!pattern.getAppliesToEdge()) ? FALSE_VALUE
				: TRUE_VALUE);
		String transformEdgeWidth = transformEdgeWidth(pattern.getEdgeWidth());
		mEdgeWidth.setValue((transformEdgeWidth != null) ? transformEdgeWidth
				: EDGE_STYLES[0]);
		mSetEdgeStyle.setValue((pattern.getEdgeStyle() == null) ? FALSE_VALUE
				: TRUE_VALUE);
		mEdgeStyle.setValue((pattern.getEdgeStyle() == null) ? EDGE_STYLES[0]
				: pattern.getEdgeStyle());
	}

	private String transformEdgeWidth(Integer edgeWidth) {
		if (edgeWidth == null)
			return null;
		HashMap transformator = getEdgeWidthTransformation();
		for (Iterator iter = transformator.keySet().iterator(); iter.hasNext();) {
			String widthString = (String) iter.next();
			Integer width = (Integer) transformator.get(widthString);
			if (edgeWidth.intValue() == width.intValue()) {
				return widthString;
			}
		}
		// not found:
		return null;
	}

	private Integer transformStringToWidth(String value) {
		HashMap transformator = getEdgeWidthTransformation();
		return (Integer) transformator.get(value);
	}

	private HashMap getEdgeWidthTransformation() {
		HashMap transformator = new HashMap();
		transformator
				.put(EDGE_WIDTHS[0], new Integer(EdgeAdapter.WIDTH_PARENT));
		transformator.put(EDGE_WIDTHS[1], new Integer(EdgeAdapter.WIDTH_THIN));
		transformator.put(EDGE_WIDTHS[2], new Integer(1));
		transformator.put(EDGE_WIDTHS[3], new Integer(2));
		transformator.put(EDGE_WIDTHS[4], new Integer(4));
		transformator.put(EDGE_WIDTHS[5], new Integer(8));
		return transformator;
	}

	private int getFontStyleFromPattern(StylePattern pattern) {
		return booleanToInt(pattern.getNodeFontBold()) * Font.BOLD
				+ booleanToInt(pattern.getNodeFontItalic()) * Font.ITALIC;
	}

	private int booleanToInt(Boolean booleanHolder) {
		if (booleanHolder == null)
			return 0;
		if (booleanHolder.booleanValue()) {
			return 1;
		}
		return 0;
	}

	public StylePattern getResultPattern() {
		StylePattern pattern = new StylePattern();
		if (mSetNodeColor.getValue() == TRUE_VALUE) {
			pattern.setNodeColor(Tools.xmlToColor(mNodeColor.getValue()));
		} else {
			pattern.setNodeColor(null);
		}
		if (mSetNodeBackgroundColor.getValue() == TRUE_VALUE) {
			pattern.setNodeBackgroundColor(Tools.xmlToColor(mNodeBackgoundColor
					.getValue()));
		} else {
			pattern.setNodeBackgroundColor(null);
		}
		if (mSetNodeStyle.getValue() == TRUE_VALUE) {
			pattern.setNodeStyle(mNodeStyle.getValue());
		} else {
			pattern.setNodeStyle(null);
		}
		if (mSetNodeFont.getValue() == TRUE_VALUE) {
			pattern.setNodeFontFamily(mNodeFont.getFontValue().getFamily());
			pattern.setNodeFontBold(new Boolean((mNodeFont.getFontValue()
					.getStyle() & Font.BOLD) != 0));
			pattern.setNodeFontItalic(new Boolean((mNodeFont.getFontValue()
					.getStyle() & Font.ITALIC) != 0));
			pattern.setNodeFontSize(new Integer(mNodeFont.getFontValue()
					.getSize()));
		} else {
			pattern.setNodeFontFamily(null);
			pattern.setNodeFontBold(null);
			pattern.setNodeFontItalic(null);
			pattern.setNodeFontSize(null);
		}
		if (mSetNodeText.getValue() == TRUE_VALUE) {
			pattern.setText(mNodeText.getValue());
		} else {
			pattern.setText(null);
		}
		if (mSetEdgeColor.getValue() == TRUE_VALUE) {
			pattern.setEdgeColor(Tools.xmlToColor(mEdgeColor.getValue()));
		} else {
			pattern.setEdgeColor(null);
		}
		if (mSetEdgeStyle.getValue() == TRUE_VALUE) {
			pattern.setEdgeStyle(mEdgeStyle.getValue());
		} else {
			pattern.setEdgeStyle(null);
		}
		if (mSetEdgeWidth.getValue() == TRUE_VALUE) {
			pattern.setEdgeWidth(transformStringToWidth(mEdgeWidth.getValue()));
		} else {
			pattern.setEdgeWidth(null);
		}
		return pattern;
	}

	/**
	 * Internal test.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		final StylePatternFrame stylePatternFrame = new StylePatternFrame(
				new TextTranslator() {

					public String getText(String pKey) {
						System.out.println(pKey + "="
								+ pKey.replaceAll("PatternDialog.", ""));
						return pKey.replaceAll("PatternDialog.", "");
					}
				});
		stylePatternFrame.init();
		StylePattern stylePattern = new StylePattern();
		stylePattern.setNodeColor(Color.RED);
		stylePattern.setNodeBackgroundColor(Color.GREEN);
		stylePatternFrame.setPattern(stylePattern);
		frame.getContentPane().add(stylePatternFrame);
		frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				StylePattern result = stylePatternFrame.getResultPattern();
				System.out.println(result);
				frame.hide();
				frame.dispose();
				System.exit(0);
			}

		});

		frame.pack();
		frame.show();
	}

}
