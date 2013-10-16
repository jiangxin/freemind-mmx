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

package freemind.modes.mindmapmode.dialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import freemind.common.BooleanProperty;
import freemind.common.ColorProperty;
import freemind.common.ComboProperty;
import freemind.common.FontProperty;
import freemind.common.IconProperty;
import freemind.common.NextLineProperty;
import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.common.ScriptEditorProperty;
import freemind.common.SeparatorProperty;
import freemind.common.StringProperty;
import freemind.common.TextTranslator;
import freemind.common.ThreeCheckBoxProperty;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternChild;
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
import freemind.controller.actions.generated.instance.PatternNodeText;
import freemind.controller.actions.generated.instance.PatternPropertyBase;
import freemind.controller.actions.generated.instance.PatternScript;
import freemind.main.FreeMind;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.ApplyPatternAction;
import freemind.modes.mindmapmode.actions.IconAction;

/**
 * @author foltin
 * 
 */
public class StylePatternFrame extends JPanel implements TextTranslator,
		PropertyChangeListener {
	public static final class StylePatternFrameType {
		private StylePatternFrameType() {

		}

		public static StylePatternFrameType WITHOUT_NAME_AND_CHILDS = new StylePatternFrameType();

		public static StylePatternFrameType WITH_NAME_AND_CHILDS = new StylePatternFrameType();
	}

	private static final String[] EDGE_STYLES = new String[] {
			EdgeAdapter.EDGESTYLE_LINEAR, EdgeAdapter.EDGESTYLE_BEZIER,
			EdgeAdapter.EDGESTYLE_SHARP_LINEAR,
			EdgeAdapter.EDGESTYLE_SHARP_BEZIER };

	private static final String[] EDGE_WIDTHS = new String[] {
			"EdgeWidth_parent", "EdgeWidth_thin", "EdgeWidth_1", "EdgeWidth_2",
			"EdgeWidth_4", "EdgeWidth_8" };

	private static final String NODE_BACKGROUND_COLOR = "nodebackgroundcolor";

	private static final String SET_RESOURCE = "set_property_text";

	private static final String SET_NODE_BACKGROUND_COLOR = SET_RESOURCE;

	private static final String NODE_COLOR = "nodecolor";

	private static final String SET_NODE_COLOR = SET_RESOURCE;

	private static final String SET_NODE_STYLE = SET_RESOURCE;

	private static final String NODE_STYLE = "nodestyle";

	private static final String NODE_FONT_NAME = "nodefontname";

	private static final String SET_NODE_FONT_NAME = SET_RESOURCE;

	private static final String NODE_FONT_SIZE = "nodefontsize";

	private static final String SET_NODE_FONT_SIZE = SET_RESOURCE;

	private static final String NODE_FONT_BOLD = "nodefontbold";

	private static final String SET_NODE_FONT_BOLD = SET_RESOURCE;

	private static final String NODE_FONT_ITALIC = "nodefontitalic";

	private static final String SET_NODE_FONT_ITALIC = SET_RESOURCE;

	private static final String SET_NODE_TEXT = SET_RESOURCE;

	private static final String NODE_TEXT = "nodetext";

	private static final String SET_EDGE_WIDTH = SET_RESOURCE;

	private static final String EDGE_WIDTH = "edgewidth";

	private static final String SET_EDGE_STYLE = SET_RESOURCE;

	private static final String EDGE_STYLE = "edgestyle";

	private static final String SET_EDGE_COLOR = SET_RESOURCE;

	private static final String EDGE_COLOR = "edgecolor";

	private static final String CLEAR_ALL_SETTERS = "clear_all_setters";

	private static final String SET_ICON = SET_RESOURCE;

	private static final String ICON = "icon";

	private static final String NODE_NAME = "patternname";

	private static final String SET_CHILD_PATTERN = SET_RESOURCE;

	private static final String CHILD_PATTERN = "childpattern";

	private static final String SET_SCRIPT = "setscript";

	private static final String SCRIPT = "script";

	private final TextTranslator mTranslator;

	private Vector mControls;

	private ThreeCheckBoxProperty mSetNodeColor;

	private ColorProperty mNodeColor;

	private ThreeCheckBoxProperty mSetNodeBackgroundColor;

	private ColorProperty mNodeBackgroundColor;

	private ThreeCheckBoxProperty mSetNodeStyle;

	private ComboProperty mNodeStyle;

	private ThreeCheckBoxProperty mSetNodeFontName;

	private FontProperty mNodeFontName;

	private ThreeCheckBoxProperty mSetNodeFontBold;

	private BooleanProperty mNodeFontBold;

	private ThreeCheckBoxProperty mSetNodeFontItalic;

	private BooleanProperty mNodeFontItalic;

	private ThreeCheckBoxProperty mSetNodeFontSize;

	private ComboProperty mNodeFontSize;

	private ThreeCheckBoxProperty mSetNodeText;

	private StringProperty mNodeText;

	private ThreeCheckBoxProperty mSetEdgeWidth;

	private ComboProperty mEdgeWidth;

	private ThreeCheckBoxProperty mSetEdgeStyle;

	private ComboProperty mEdgeStyle;

	private ThreeCheckBoxProperty mSetEdgeColor;

	private ColorProperty mEdgeColor;

	private ThreeCheckBoxProperty mSetIcon;

	private IconProperty mIcon;

	private ThreeCheckBoxProperty mSetChildPattern;

	private ComboProperty mChildPattern;

	private ThreeCheckBoxProperty mSetScriptPattern;

	private ScriptEditorProperty mScriptPattern;

	private StringProperty mName;

	private Vector mIconInformationVector;

	/**
	 * Denotes pairs property -> ThreeCheckBoxProperty such that the boolean
	 * property can be set, when the format property is changed.
	 */
	private HashMap mPropertyChangePropagation = new HashMap();

	private ThreeCheckBoxProperty mClearSetters;

	private final MindMapController mMindMapController;

	private final StylePatternFrameType mType;

	/**
	 * @throws HeadlessException
	 */
	public StylePatternFrame(TextTranslator pTranslator,
			MindMapController pMindMapController, StylePatternFrameType pType)
			throws HeadlessException {
		super();
		mTranslator = pTranslator;
		mMindMapController = pMindMapController;
		mType = pType;
	}

	/**
	 * Creates all controls and adds them to the frame.
	 */
	public void init() {
		CardLayout cardLayout = new CardLayout();
		JPanel rightStack = new JPanel(cardLayout);
		String form = "right:max(40dlu;p), 4dlu, 20dlu, 7dlu,right:max(40dlu;p), 4dlu, 80dlu, 7dlu";
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

	public void addListeners() {
		// add listeners:
		for (Iterator iter = mControls.iterator(); iter.hasNext();) {
			PropertyControl control = (PropertyControl) iter.next();
			if (control instanceof PropertyBean) {
				PropertyBean bean = (PropertyBean) control;
				bean.addPropertyChangeListener(this);
			}
		}
		mClearSetters.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent pEvt) {
				for (Iterator iter = mPropertyChangePropagation.keySet()
						.iterator(); iter.hasNext();) {
					ThreeCheckBoxProperty booleanProp = (ThreeCheckBoxProperty) iter
							.next();
					booleanProp.setValue(mClearSetters.getValue());
				}
			}
		});
	}

	private String[] sizes = new String[] { "2", "4", "6", "8", "10", "12",
			"14", "16", "18", "20", "22", "24", "30", "36", "48", "72" };

	private List mPatternList;

	private Vector getControls() {
		Vector controls = new Vector();
		controls.add(new SeparatorProperty("General"));
		mClearSetters = new ThreeCheckBoxProperty(CLEAR_ALL_SETTERS
				+ ".tooltip", CLEAR_ALL_SETTERS);
		mClearSetters.setValue(ThreeCheckBoxProperty.TRUE_VALUE);
		controls.add(mClearSetters);
		if (StylePatternFrameType.WITH_NAME_AND_CHILDS.equals(mType)) {
			mName = new StringProperty(NODE_NAME + ".tooltip", NODE_NAME);
			controls.add(mName);
			// child pattern
			mSetChildPattern = new ThreeCheckBoxProperty(SET_CHILD_PATTERN
					+ ".tooltip", SET_CHILD_PATTERN);
			controls.add(mSetChildPattern);
			Vector childNames = new Vector();
			mChildPattern = new ComboProperty(CHILD_PATTERN + ".tooltip",
					CHILD_PATTERN, childNames, childNames);
			controls.add(mChildPattern);
		}
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("NodeColors"));
		mSetNodeColor = new ThreeCheckBoxProperty(SET_NODE_COLOR + ".tooltip",
				SET_NODE_COLOR);
		controls.add(mSetNodeColor);
		FreeMind fmMain = (FreeMind) mMindMapController.getFrame();
		mNodeColor = new ColorProperty(NODE_COLOR + ".tooltip", NODE_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_NODE_TEXT_COLOR),
				this);
		controls.add(mNodeColor);
		mSetNodeBackgroundColor = new ThreeCheckBoxProperty(
				SET_NODE_BACKGROUND_COLOR + ".tooltip",
				SET_NODE_BACKGROUND_COLOR);
		controls.add(mSetNodeBackgroundColor);
		mNodeBackgroundColor = new ColorProperty(NODE_BACKGROUND_COLOR
				+ ".tooltip", NODE_BACKGROUND_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_BACKGROUND_COLOR),
				this);
		controls.add(mNodeBackgroundColor);
		controls.add(new SeparatorProperty("NodeStyles"));
		mSetNodeStyle = new ThreeCheckBoxProperty(SET_NODE_STYLE + ".tooltip",
				SET_NODE_STYLE);
		controls.add(mSetNodeStyle);
		mNodeStyle = new ComboProperty(NODE_STYLE + ".tooltip", NODE_STYLE,
				MindMapNode.NODE_STYLES, this);
		controls.add(mNodeStyle);
		mIconInformationVector = new Vector();
		MindMapController controller = mMindMapController;
		Vector iconActions = controller.iconActions;
		for (Enumeration e = iconActions.elements(); e.hasMoreElements();) {
			IconAction action = ((IconAction) e.nextElement());
			MindIcon info = action.getMindIcon();
			mIconInformationVector.add(info);
		}
		mSetIcon = new ThreeCheckBoxProperty(SET_ICON + ".tooltip", SET_ICON);
		controls.add(mSetIcon);
		mIcon = new IconProperty(ICON + ".tooltip", ICON,
				mMindMapController.getFrame(), mIconInformationVector);
		controls.add(mIcon);
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("NodeFont"));
		mSetNodeFontName = new ThreeCheckBoxProperty(SET_NODE_FONT_NAME
				+ ".tooltip", SET_NODE_FONT_NAME);
		controls.add(mSetNodeFontName);
		mNodeFontName = new FontProperty(NODE_FONT_NAME + ".tooltip",
				NODE_FONT_NAME, this);
		controls.add(mNodeFontName);
		mSetNodeFontSize = new ThreeCheckBoxProperty(SET_NODE_FONT_SIZE
				+ ".tooltip", SET_NODE_FONT_SIZE);
		controls.add(mSetNodeFontSize);
		Vector sizesVector = new Vector();
		for (int i = 0; i < sizes.length; i++) {
			sizesVector.add(sizes[i]);
		}
		mNodeFontSize = new IntegerComboProperty(NODE_FONT_SIZE + ".tooltip",
				NODE_FONT_SIZE, sizes, sizesVector);
		controls.add(mNodeFontSize);
		mSetNodeFontBold = new ThreeCheckBoxProperty(SET_NODE_FONT_BOLD
				+ ".tooltip", SET_NODE_FONT_BOLD);
		controls.add(mSetNodeFontBold);
		mNodeFontBold = new BooleanProperty(NODE_FONT_BOLD + ".tooltip",
				NODE_FONT_BOLD);
		controls.add(mNodeFontBold);
		mSetNodeFontItalic = new ThreeCheckBoxProperty(SET_NODE_FONT_ITALIC
				+ ".tooltip", SET_NODE_FONT_ITALIC);
		controls.add(mSetNodeFontItalic);
		mNodeFontItalic = new BooleanProperty(NODE_FONT_ITALIC + ".tooltip",
				NODE_FONT_ITALIC);
		controls.add(mNodeFontItalic);
		/* **** */
		mSetNodeText = new ThreeCheckBoxProperty(SET_NODE_TEXT + ".tooltip",
				SET_NODE_TEXT);
		controls.add(mSetNodeText);
		mNodeText = new StringProperty(NODE_TEXT + ".tooltip", NODE_TEXT);
		controls.add(mNodeText);
		/* **** */
		controls.add(new SeparatorProperty("EdgeControls"));
		mSetEdgeWidth = new ThreeCheckBoxProperty(SET_EDGE_WIDTH + ".tooltip",
				SET_EDGE_WIDTH);
		controls.add(mSetEdgeWidth);
		mEdgeWidth = new ComboProperty(EDGE_WIDTH + ".tooltip", EDGE_WIDTH,
				EDGE_WIDTHS, this);
		controls.add(mEdgeWidth);
		/* **** */
		mSetEdgeStyle = new ThreeCheckBoxProperty(SET_EDGE_STYLE + ".tooltip",
				SET_EDGE_STYLE);
		controls.add(mSetEdgeStyle);
		mEdgeStyle = new ComboProperty(EDGE_STYLE + ".tooltip", EDGE_STYLE,
				EDGE_STYLES, this);
		controls.add(mEdgeStyle);
		/* **** */
		mSetEdgeColor = new ThreeCheckBoxProperty(SET_EDGE_COLOR + ".tooltip",
				SET_EDGE_COLOR);
		controls.add(mSetEdgeColor);
		mEdgeColor = new ColorProperty(EDGE_COLOR + ".tooltip", EDGE_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_EDGE_COLOR), this);
		controls.add(mEdgeColor);
		/* **** */
		controls.add(new SeparatorProperty("ScriptingControl"));
		mSetScriptPattern = new ThreeCheckBoxProperty(SET_SCRIPT + ".tooltip",
				SET_SCRIPT);
		controls.add(mSetScriptPattern);
		mScriptPattern = new ScriptEditorProperty(SCRIPT + ".tooltip", SCRIPT,
				mMindMapController);
		controls.add(mScriptPattern);
		// fill map;
		mPropertyChangePropagation.put(mSetNodeColor, mNodeColor);
		mPropertyChangePropagation.put(mSetNodeBackgroundColor,
				mNodeBackgroundColor);
		mPropertyChangePropagation.put(mSetNodeStyle, mNodeStyle);
		mPropertyChangePropagation.put(mSetNodeFontName, mNodeFontName);
		mPropertyChangePropagation.put(mSetNodeFontSize, mNodeFontSize);
		mPropertyChangePropagation.put(mSetNodeFontBold, mNodeFontBold);
		mPropertyChangePropagation.put(mSetNodeFontItalic, mNodeFontItalic);
		mPropertyChangePropagation.put(mSetNodeText, mNodeText);
		mPropertyChangePropagation.put(mSetEdgeColor, mEdgeColor);
		mPropertyChangePropagation.put(mSetEdgeStyle, mEdgeStyle);
		mPropertyChangePropagation.put(mSetEdgeWidth, mEdgeWidth);
		mPropertyChangePropagation.put(mSetIcon, mIcon);
		mPropertyChangePropagation.put(mSetScriptPattern, mScriptPattern);
		if (StylePatternFrameType.WITH_NAME_AND_CHILDS.equals(mType)) {
			// child pattern
			mPropertyChangePropagation.put(mSetChildPattern, mChildPattern);
		}
		return controls;
	}

	private Vector getPatternNames() {
		Vector childNames = new Vector();
		for (Iterator iter = mPatternList.iterator(); iter.hasNext();) {
			Pattern pattern = (Pattern) iter.next();
			childNames.add(pattern.getName());
		}
		return childNames;
	}

	public String getText(String pKey) {
		return mTranslator.getText("PatternDialog." + pKey);
	}

	public void setPattern(Pattern pattern) {
		FreeMind fmMain = (FreeMind) mMindMapController.getFrame();
		setPatternControls(pattern.getPatternNodeColor(), mSetNodeColor,
				mNodeColor,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_NODE_TEXT_COLOR));
		setPatternControls(pattern.getPatternNodeBackgroundColor(),
				mSetNodeBackgroundColor, mNodeBackgroundColor,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_BACKGROUND_COLOR));
		setPatternControls(pattern.getPatternNodeStyle(), mSetNodeStyle,
				mNodeStyle, MindMapNode.STYLE_AS_PARENT);
		setPatternControls(pattern.getPatternNodeText(), mSetNodeText,
				mNodeText, "");
		setPatternControls(pattern.getPatternEdgeColor(), mSetEdgeColor,
				mEdgeColor,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_EDGE_COLOR));
		setPatternControls(pattern.getPatternEdgeStyle(), mSetEdgeStyle,
				mEdgeStyle, EDGE_STYLES[0]);
		setPatternControls(pattern.getPatternEdgeWidth(), mSetEdgeWidth,
				mEdgeWidth, EDGE_WIDTHS[0], new EdgeWidthTransformer());
		setPatternControls(pattern.getPatternNodeFontName(), mSetNodeFontName,
				mNodeFontName, mMindMapController.getController()
						.getDefaultFontFamilyName());
		setPatternControls(pattern.getPatternNodeFontSize(), mSetNodeFontSize,
				mNodeFontSize, sizes[0]);
		setPatternControls(pattern.getPatternNodeFontBold(), mSetNodeFontBold,
				mNodeFontBold, BooleanProperty.TRUE_VALUE);
		setPatternControls(pattern.getPatternNodeFontItalic(),
				mSetNodeFontItalic, mNodeFontItalic, BooleanProperty.TRUE_VALUE);
		MindIcon firstInfo = (MindIcon) mIconInformationVector.get(0);
		setPatternControls(pattern.getPatternIcon(), mSetIcon, mIcon,
				firstInfo.getName());
		setPatternControls(pattern.getPatternScript(), mSetScriptPattern,
				mScriptPattern, "");
		if (StylePatternFrameType.WITH_NAME_AND_CHILDS.equals(mType)) {
			mName.setValue(pattern.getName());
			setPatternControls(
					pattern.getPatternChild(),
					mSetChildPattern,
					mChildPattern,
					(mPatternList.size() > 0) ? ((Pattern) mPatternList.get(0))
							.getName() : null);

		}
		for (Iterator iter = mPropertyChangePropagation.keySet().iterator(); iter
				.hasNext();) {
			ThreeCheckBoxProperty prop = (ThreeCheckBoxProperty) iter.next();
			propertyChange(new PropertyChangeEvent(prop, prop.getLabel(), null,
					prop.getValue()));
		}

	}

	private interface ValueTransformator {
		String transform(String value);
	}

	private final class IdentityTransformer implements ValueTransformator {
		public String transform(String value) {
			return value;
		}
	}

	private final class EdgeWidthTransformer implements ValueTransformator {
		public String transform(String value) {
			return transformEdgeWidth(value);
		}
	}

	private final class EdgeWidthBackTransformer implements ValueTransformator {
		public String transform(String value) {
			return transformStringToWidth(value);
		}
	}

	private void setPatternControls(PatternPropertyBase patternProperty,
			PropertyBean threeCheckBoxProperty, PropertyBean property,
			String defaultValue) {
		setPatternControls(patternProperty, threeCheckBoxProperty, property,
				defaultValue, new IdentityTransformer());
	}

	/**
	 */
	private void setPatternControls(PatternPropertyBase patternProperty,
			PropertyBean threeCheckBoxProperty, PropertyBean property,
			String defaultValue, ValueTransformator transformer) {
		if (patternProperty == null) {
			// value is not set:
			property.setValue(defaultValue);
			threeCheckBoxProperty
					.setValue(ThreeCheckBoxProperty.DON_T_TOUCH_VALUE);
			return;
		}
		if (patternProperty.getValue() == null) {
			// remove prop:
			property.setValue(defaultValue);
			threeCheckBoxProperty.setValue(ThreeCheckBoxProperty.FALSE_VALUE);
			return;
		}
		property.setValue(transformer.transform(patternProperty.getValue()));
		threeCheckBoxProperty.setValue(ThreeCheckBoxProperty.TRUE_VALUE);
	}

	private String transformEdgeWidth(String pEdgeWidth) {
		if (pEdgeWidth == null)
			return null;
		int edgeWidth = ApplyPatternAction.edgeWidthStringToInt(pEdgeWidth);
		HashMap transformator = getEdgeWidthTransformation();
		for (Iterator iter = transformator.keySet().iterator(); iter.hasNext();) {
			String widthString = (String) iter.next();
			Integer width = (Integer) transformator.get(widthString);
			if (edgeWidth == width.intValue()) {
				return widthString;
			}
		}
		// not found:
		return null;
	}

	private String transformStringToWidth(String value) {
		HashMap transformator = getEdgeWidthTransformation();
		int intWidth = ((Integer) transformator.get(value)).intValue();
		return ApplyPatternAction.edgeWidthIntToString(intWidth);
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

	public Pattern getResultPattern() {
		Pattern pattern = new Pattern();
		return getResultPattern(pattern);
	}

	public Pattern getResultPattern(Pattern pattern) {
		pattern.setPatternNodeColor((PatternNodeColor) getPatternResult(
				new PatternNodeColor(), mSetNodeColor, mNodeColor));
		pattern.setPatternNodeBackgroundColor((PatternNodeBackgroundColor) getPatternResult(
				new PatternNodeBackgroundColor(), mSetNodeBackgroundColor,
				mNodeBackgroundColor));
		pattern.setPatternNodeStyle((PatternNodeStyle) getPatternResult(
				new PatternNodeStyle(), mSetNodeStyle, mNodeStyle));
		pattern.setPatternNodeText((PatternNodeText) getPatternResult(
				new PatternNodeText(), mSetNodeText, mNodeText));
		/* edges */
		pattern.setPatternEdgeColor((PatternEdgeColor) getPatternResult(
				new PatternEdgeColor(), mSetEdgeColor, mEdgeColor));
		pattern.setPatternEdgeStyle((PatternEdgeStyle) getPatternResult(
				new PatternEdgeStyle(), mSetEdgeStyle, mEdgeStyle));
		pattern.setPatternEdgeWidth((PatternEdgeWidth) getPatternResult(
				new PatternEdgeWidth(), mSetEdgeWidth, mEdgeWidth,
				new EdgeWidthBackTransformer()));
		/* font */
		pattern.setPatternNodeFontName((PatternNodeFontName) getPatternResult(
				new PatternNodeFontName(), mSetNodeFontName, mNodeFontName));
		pattern.setPatternNodeFontSize((PatternNodeFontSize) getPatternResult(
				new PatternNodeFontSize(), mSetNodeFontSize, mNodeFontSize));
		pattern.setPatternNodeFontBold((PatternNodeFontBold) getPatternResult(
				new PatternNodeFontBold(), mSetNodeFontBold, mNodeFontBold));
		pattern.setPatternNodeFontItalic((PatternNodeFontItalic) getPatternResult(
				new PatternNodeFontItalic(), mSetNodeFontItalic,
				mNodeFontItalic));
		pattern.setPatternIcon((PatternIcon) getPatternResult(
				new PatternIcon(), mSetIcon, mIcon));
		pattern.setPatternScript((PatternScript) getPatternResult(
				new PatternScript(), mSetScriptPattern, mScriptPattern));
		if (StylePatternFrameType.WITH_NAME_AND_CHILDS.equals(mType)) {
			pattern.setName(mName.getValue());
			pattern.setPatternChild((PatternChild) getPatternResult(
					new PatternChild(), mSetChildPattern, mChildPattern));
		}
		return pattern;
	}

	private PatternPropertyBase getPatternResult(
			PatternPropertyBase baseProperty,
			ThreeCheckBoxProperty threeCheckBoxProperty, PropertyBean property) {
		ValueTransformator transformer = new IdentityTransformer();
		return getPatternResult(baseProperty, threeCheckBoxProperty, property,
				transformer);
	}

	/**
	 */
	private PatternPropertyBase getPatternResult(
			PatternPropertyBase baseProperty,
			ThreeCheckBoxProperty threeCheckBoxProperty, PropertyBean property,
			ValueTransformator transformer) {
		String checkboxResult = threeCheckBoxProperty.getValue();
		if (checkboxResult == null) {
			return null;
		}
		if (checkboxResult.equals(ThreeCheckBoxProperty.DON_T_TOUCH_VALUE)) {
			return null;
		}
		if (checkboxResult.equals(ThreeCheckBoxProperty.FALSE_VALUE)) {
			// remove property:
			return baseProperty;
		}
		baseProperty.setValue(transformer.transform(property.getValue()));
		return baseProperty;
	}

	/**
	 * Used to enable/disable the attribute controls, if the check boxes are
	 * changed.
	 */
	public void propertyChange(PropertyChangeEvent pEvt) {
		// System.out.println("Propagation of "+ pEvt.getPropertyName()
		// + " with value " + pEvt.getNewValue() + " and source " +
		// pEvt.getSource());
		if (mPropertyChangePropagation.containsKey(pEvt.getSource())) {
			ThreeCheckBoxProperty booleanProp = (ThreeCheckBoxProperty) pEvt
					.getSource();
			// enable only when set:
			PropertyControl bean = (PropertyControl) mPropertyChangePropagation
					.get(booleanProp);
			bean.setEnabled(ThreeCheckBoxProperty.TRUE_VALUE.equals(booleanProp
					.getValue()));
			return;
		}
	}

	/**
	 * For the child pattern box, the list is set here.
	 * 
	 */
	public void setPatternList(List patternList) {
		this.mPatternList = patternList;
		Vector childNames = getPatternNames();
		mChildPattern.updateComboBoxEntries(childNames, childNames);
	}

}
