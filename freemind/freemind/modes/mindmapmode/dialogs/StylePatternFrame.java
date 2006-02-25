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
/*$Id: StylePatternFrame.java,v 1.1.2.1 2006-02-25 23:10:58 christianfoltin Exp $*/
package freemind.modes.mindmapmode.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.common.PropertyControl.TextTranslator;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.StylePattern;

/**
 * @author foltin
 * 
 */
public class StylePatternFrame extends JPanel implements TextTranslator {

	private static final String NODE_BACKGROUND_COLOR = "nodebackgroundcolor";

	private static final String SET_NODE_BACKGROUND_COLOR = "setnodebackgroundcolor";

	private static final String NODE_COLOR = "nodecolor";

	private static final String SET_NODE_COLOR = "setnodecolor";

	private static final String SET_NODE_STYLE = "setnodestyle";

	private static final String NODE_STYLE = "nodestyle";

	private static final String NODE_FONT_NAME = "nodefontname";

	private static final String SET_NODE_FONT_NAME = "setnodefontname";

	private final TextTranslator mTranslator;

	private Vector mControls;

	private BooleanProperty mSetNodeColor;

	private ColorProperty mNodeColor;

	private BooleanProperty mSetNodeBackgroundColor;

	private ColorProperty mNodeBackgoundColor;

	private BooleanProperty mSetNodeStyle;

	private ComboProperty mNodeStyle;

	private FontProperty mNodeFontName;

	private BooleanProperty mSetNodeFontName;

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
		String form = "right:max(40dlu;p), 4dlu, 80dlu, 7dlu";
		FormLayout rightLayout = new FormLayout(form + "," + form, "");
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
		mSetNodeStyle = new BooleanProperty(SET_NODE_STYLE + ".tooltip",
				SET_NODE_STYLE);
		controls.add(mSetNodeStyle);
		mNodeStyle = new ComboProperty(NODE_STYLE + ".tooltip", NODE_STYLE,
				MindMapNode.NODE_STYLES, this);
		controls.add(mNodeStyle);
		mSetNodeFontName = new BooleanProperty(SET_NODE_FONT_NAME + ".tooltip",
				SET_NODE_FONT_NAME);
		controls.add(mSetNodeFontName);
		mNodeFontName = new FontProperty(NODE_FONT_NAME+".tooltip", NODE_FONT_NAME, "", this);
		controls.add(mNodeFontName);
		return controls;
	}

	public String getText(String pKey) {
		return mTranslator.getText("PatternDialog." + pKey);
	}

	public void setPattern(StylePattern pattern) {
		mSetNodeColor.setValue((pattern.getNodeColor() == null) ? "false"
				: "true");
		mNodeColor.setValue(Tools.colorToXml(pattern.getNodeColor()));
		mSetNodeBackgroundColor
				.setValue((pattern.getNodeBackgroundColor() == null) ? "false"
						: "true");
		mNodeBackgoundColor.setValue(Tools.colorToXml(pattern
				.getNodeBackgroundColor()));
		mSetNodeStyle.setValue((pattern.getNodeStyle() == null) ? "false"
				: "true");
		mNodeStyle
				.setValue((pattern.getNodeStyle() == null) ? MindMapNode.STYLE_AS_PARENT
						: pattern.getNodeStyle());
	}

	public StylePattern getResultPattern() {
		StylePattern pattern = new StylePattern();
		if (mSetNodeColor.getValue() == "true") {
			pattern.setNodeColor(Tools.xmlToColor(mNodeColor.getValue()));
		} else {
			pattern.setNodeColor(null);
		}
		if (mSetNodeBackgroundColor.getValue() == "true") {
			pattern.setNodeBackgroundColor(Tools.xmlToColor(mNodeBackgoundColor
					.getValue()));
		} else {
			pattern.setNodeBackgroundColor(null);
		}
		if (mSetNodeStyle.getValue() == "true") {
			pattern.setNodeStyle(mNodeStyle.getValue());
		} else {
			pattern.setNodeStyle(null);
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
						return pKey;
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
