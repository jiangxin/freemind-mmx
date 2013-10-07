/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2005   Christian Foltin.
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
 * Created on 06.05.2005
 */

package freemind.preferences.layout;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import freemind.common.BooleanProperty;
import freemind.common.ColorProperty;
import freemind.common.ComboProperty;
import freemind.common.DontShowNotificationProperty;
import freemind.common.NextLineProperty;
import freemind.common.NumberProperty;
import freemind.common.PasswordProperty;
import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.common.RemindValueProperty;
import freemind.common.SeparatorProperty;
import freemind.common.StringProperty;
import freemind.common.TextTranslator;
import freemind.common.XmlBindingTools;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.OptionPanelWindowConfigurationStorage;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.FreeMind;
import freemind.main.FreeMindCommon;
import freemind.main.Tools;
import freemind.modes.IconInformation;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.preferences.FreemindPropertyContributor;

/**
 * @author foltin
 * 
 */
public class OptionPanel implements TextTranslator {
	// TODO: Cancel and windowClose => Are you sure, or save.
	// FIXME: key dialog
	// FIXME: Translate me and html

	private static final String TOOLTIP_EXT = ".tooltip";

	private static final Color MARKED_BUTTON_COLOR = Color.BLUE;

	private Vector controls;

	private final JDialog frame;

	private HashMap tabButtonMap = new HashMap();
	private HashMap tabActionMap = new HashMap();
	private String selectedPanel = null;

	private static JColorChooser colorChooser;

	private final OptionPanelFeedback feedback;

	private static FreeMind fmMain;

	private static final String PREFERENCE_STORAGE_PROPERTY = "OptionPanel_Window_Properties";
	private static final String DEFAULT_LAYOUT_FORMAT = "right:max(40dlu;p), 4dlu, 120dlu, 7dlu";

	/**
	 * @throws IOException
	 * 
	 */
	public OptionPanel(FreeMind fm, JDialog frame, OptionPanelFeedback feedback) {
		super();
		if (fmMain == null) {
			fmMain = fm;
		}
		this.frame = frame;
		this.feedback = feedback;
		// Retrieve window size and column positions.
		WindowConfigurationStorage storage = XmlBindingTools.getInstance()
				.decorateDialog(fm.getController(), frame,
						PREFERENCE_STORAGE_PROPERTY);
		if (storage != null
				&& storage instanceof OptionPanelWindowConfigurationStorage) {
			OptionPanelWindowConfigurationStorage oWindowSettings = (OptionPanelWindowConfigurationStorage) storage;
			selectedPanel = oWindowSettings.getPanel();
		}
	}

	public interface OptionPanelFeedback {
		void writeProperties(Properties props);
	}

	public static Vector changeListeners = new Vector();

	/**
	 */
	public void setProperties() {
		for (Iterator i = controls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
			if (control instanceof PropertyBean) {
				PropertyBean bean = (PropertyBean) control;
				// System.out.println("grep -n -e \""+bean.getLabel()+"\" -r * |
				// grep -e \"\\.(java|xml):\"");
				final String label = bean.getLabel();
				String value = fmMain.getAdjustableProperty(label);
				// System.out.println("Setting property " + bean.getLabel()
				// + " to " + value);
				bean.setValue(value);
			}
		}
	}

	private Properties getOptionProperties() {
		Properties p = new Properties();
		for (Iterator i = controls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
			if (control instanceof PropertyBean) {
				PropertyBean bean = (PropertyBean) control;
				final String value = bean.getValue();
				if (value != null)
					p.setProperty(bean.getLabel(), value);
			}
		}
		return p;
	}

	public void buildPanel() {

		FormLayout leftLayout = new FormLayout("80dlu", "");
		DefaultFormBuilder leftBuilder = new DefaultFormBuilder(leftLayout);

		CardLayout cardLayout = new VariableSizeCardLayout();
		JPanel rightStack = new JPanel(cardLayout);

		FormLayout rightLayout = null; // add rows dynamically
		DefaultFormBuilder rightBuilder = null;
		String lastTabName = null;

		controls = getControls();
		for (Iterator i = controls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
			// System.out.println("layouting : " + control.getLabel());

			if (control instanceof NewTabProperty) {
				NewTabProperty newTab = (NewTabProperty) control;
				if (rightBuilder != null) {
					// terminate old panel:
					rightStack.add(rightBuilder.getPanel(), lastTabName);
				}
				rightLayout = new FormLayout(newTab.getDescription(), "");
				rightBuilder = new DefaultFormBuilder(rightLayout);
				rightBuilder.setDefaultDialogBorder();
				lastTabName = newTab.getLabel();
				// add a button to the left side:
				JButton tabButton = new JButton(getText(lastTabName));
				ChangeTabAction changeTabAction = new ChangeTabAction(
						cardLayout, rightStack, lastTabName);
				tabButton.addActionListener(changeTabAction);
				registerTabButton(tabButton, lastTabName, changeTabAction);
				leftBuilder.append(tabButton);
			} else {
				control.layout(rightBuilder, this);
			}
		}
		// add the last one, too
		rightStack.add(rightBuilder.getPanel(), lastTabName);
		// select one panel:
		if (selectedPanel != null && tabActionMap.containsKey(selectedPanel)) {
			((ChangeTabAction) tabActionMap.get(selectedPanel))
					.actionPerformed(null);
		}
		JScrollPane rightScrollPane = new JScrollPane(rightStack);
		rightScrollPane.getVerticalScrollBar().setUnitIncrement(100);
		JSplitPane centralPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftBuilder.getPanel(), rightScrollPane);
		frame.getContentPane().add(centralPanel, BorderLayout.CENTER);
		JButton cancelButton = new JButton(getText("Cancel"));
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				closeWindow();
			}
		});
		JButton okButton = new JButton(getText("OK"));
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				feedback.writeProperties(getOptionProperties());
				closeWindow();
			}

		});
		frame.getRootPane().setDefaultButton(okButton);
		frame.getContentPane().add(
				ButtonBarFactory.buildOKCancelBar(cancelButton, okButton),
				BorderLayout.SOUTH);
	}

	private static String lastKey = "";

	/**
	 */
	public String getText(String string) {
		if (string == null)
			return null;
		checkConnectionToFreeMindMain();
		return fmMain.getResourceString("OptionPanel." + string);
	}

	private static void checkConnectionToFreeMindMain() {
		if (fmMain == null) {
			throw new IllegalArgumentException("FreeMindMain not set yet.");
		}
	}

	/**
	 */
	private void registerTabButton(JButton tabButton, String name,
			ChangeTabAction changeTabAction) {
		tabButtonMap.put(name, tabButton);
		tabActionMap.put(name, changeTabAction);
		// if no default panel was given, we use the first.
		if (selectedPanel == null) {
			selectedPanel = name;
		}
	}

	private JButton getTabButton(String name) {
		return (JButton) tabButtonMap.get(name);
	}

	private Collection getAllButtons() {
		return tabButtonMap.values();
	}

	private final class ChangeTabAction implements ActionListener {
		private CardLayout cardLayout;

		private JPanel centralPanel;

		private String tabName;

		private ChangeTabAction(CardLayout cardLayout, JPanel centralPanel,
				String tabName) {
			super();
			this.cardLayout = cardLayout;
			this.centralPanel = centralPanel;
			this.tabName = tabName;
		}

		public void actionPerformed(ActionEvent arg0) {
			cardLayout.show(centralPanel, tabName);
			// design: mark selected button with a color
			Collection c = getAllButtons();
			for (Iterator i = c.iterator(); i.hasNext();) {
				JButton button = (JButton) i.next();
				button.setForeground(null);
			}
			getTabButton(tabName).setForeground(MARKED_BUTTON_COLOR);
			selectedPanel = tabName;
		}
	}

	public static class NewTabProperty implements PropertyControl {

		private String label;
		private String layoutFormat;

		public NewTabProperty(String label) {
			this(label, DEFAULT_LAYOUT_FORMAT);
		}

		public NewTabProperty(String label, String layoutFormat) {
			super();
			this.label = label;
			this.layoutFormat = layoutFormat;
		}

		public String getDescription() {
			return layoutFormat;
		}

		public String getLabel() {
			return label;
		}

		public void layout(DefaultFormBuilder builder,
				TextTranslator pTranslator) {

		}

		public void setEnabled(boolean pEnabled) {

		}

	}

	private static class KeyProperty extends PropertyBean implements
			PropertyControl {
		private int modifierMask = 0;
		String description;

		String label;

		JButton mButton = new JButton();
		private String labelText;
		private ImageIcon icon;

		private static RowSpec rowSpec;

		/**
		 */
		public KeyProperty(final JDialog frame, String description, String label) {
			super();
			this.description = description;
			this.label = label;
			mButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					// FIXME: Determine bindings.
					Vector allKeybindings = new Vector();
					GrabKeyDialog dialog = new GrabKeyDialog(fmMain, frame,
							new GrabKeyDialog.KeyBinding(getLabel(),
									getLabel(), getValue(), false),
							allKeybindings, null, modifierMask);
					if (dialog.isOK()) {
						setValue(dialog.getShortcut());
						firePropertyChangeEvent();
					}
				}
			});
		}

		public void disableModifiers() {
			modifierMask = KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			mButton.setText(value);
			mButton.setToolTipText(mButton.getText());
		}

		public String getValue() {
			return mButton.getText();
		}

		public void layout(DefaultFormBuilder builder,
				TextTranslator pTranslator) {
			if (labelText == null)
				labelText = pTranslator.getText(getLabel());

			JLabel label = new JLabel(labelText, icon, JLabel.RIGHT);
			label.setToolTipText(pTranslator.getText(getDescription()));
			if (rowSpec == null) {
				rowSpec = new RowSpec("fill:20dlu");
			}
			if (3 < builder.getColumn()) {
				builder.appendRelatedComponentsGapRow();
				builder.appendRow(rowSpec);
				builder.nextLine(2);
			} else {
				builder.nextColumn(2);
			}
			builder.add(label);
			builder.nextColumn(2);
			builder.add(mButton);
		}

		public void setEnabled(boolean pEnabled) {
			mButton.setEnabled(pEnabled);
		}

		public void setLabelText(String labelText) {
			this.labelText = labelText;
		}

		public void setImageIcon(ImageIcon icon) {
			this.icon = icon;

		}
	}

	//
	private Vector getControls() {
		Vector controls = new Vector();
		/***********************************************************************
		 * Language
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Environment"));
		controls.add(new SeparatorProperty("language"));
		// TODO: Search class path for translations.
		controls.add(new ComboProperty(

		/**
		 * For the codes see
		 * http://www.loc.gov/standards/iso639-2/php/English_list.php
		 */
		"language.tooltip", FreeMindCommon.RESOURCE_LANGUAGE,
				new String[] { "automatic", "ar", "bg", "cs", "de", "dk", "en",
						"el", "es", "et", "eu", "fr", "gl", "hr", "hu", "id",
						"it", "ja", "ko", "lt", "nl", "nn", "nb", "pl",
						"pt_BR", "pt_PT", "ro", "ru", "sk", "se", "sl", "sr",
						"tr", "uk_UA", "vi", "zh_TW", "zh_CN" },
				new TextTranslator() {

					public String getText(String pKey) {
						// decorator, that removes "TranslateMe" comments.
						return Tools.removeTranslateComment(OptionPanel.this
								.getText(pKey));
					}
				})); // automatic

		controls.add(new BooleanProperty(FreeMindCommon.CHECK_SPELLING
				+ TOOLTIP_EXT, FreeMindCommon.CHECK_SPELLING)); // true

		// INTERNAL PROPERTY.
		// controls
		// .add(new StringProperty(
		// "The Modes which Freemind will load on startup, full Class names,
		// comma, identifier, separated by a comma.",
		// "modes_since_0_8_0")); //
		// freemind.modes.browsemode.BrowseMode,Browse,freemind.modes.mindmapmode.MindMapMode,MindMap,freemind.modes.filemode.FileMode,File
		//
		// controls.add(new StringProperty(
		// "The initial mode that is loaded on startup", "initial_mode")); //
		// MindMap
		//
		// controls
		// .add(new StringProperty(
		// "This is the place where the users properties file is located. It is
		// ignored by the applet (set Parameters in the html file instead). You
		// can write '~' to indicate the users home directory. Of course this
		// works only in the default 'freemind.properties', which is included in
		// the jar file, not for the users freemind.props out of the jar file.",
		// "properties_folder")); // .freemind

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("proxy"));
		controls.add(new BooleanProperty(FreeMind.PROXY_USE_SETTINGS
				+ TOOLTIP_EXT, FreeMind.PROXY_USE_SETTINGS));
		controls.add(new StringProperty(FreeMind.PROXY_HOST + TOOLTIP_EXT,
				FreeMind.PROXY_HOST));
		controls.add(new NumberProperty(FreeMind.PROXY_PORT + TOOLTIP_EXT,
				FreeMind.PROXY_PORT, 1, 65535, 1));
		controls.add(new BooleanProperty(FreeMind.PROXY_IS_AUTHENTICATED
				+ TOOLTIP_EXT, FreeMind.PROXY_IS_AUTHENTICATED));
		controls.add(new StringProperty(FreeMind.PROXY_USER + TOOLTIP_EXT,
				FreeMind.PROXY_USER));
		controls.add(new PasswordProperty(
				FreeMind.PROXY_PASSWORD + TOOLTIP_EXT, FreeMind.PROXY_PASSWORD));

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("files"));
		controls.add(new NumberProperty(null, "last_opened_list_length", 0,
				200, 1)); // 25
		controls.add(new BooleanProperty(FreeMindCommon.LOAD_LAST_MAP
				+ TOOLTIP_EXT, FreeMindCommon.LOAD_LAST_MAP)); // true
		controls.add(new BooleanProperty(FreeMind.RESOURCES_DON_T_OPEN_PORT
				+ TOOLTIP_EXT, FreeMind.RESOURCES_DON_T_OPEN_PORT));
		controls.add(new BooleanProperty(
				FreeMindCommon.LOAD_LAST_MAPS_AND_LAYOUT + TOOLTIP_EXT,
				FreeMindCommon.LOAD_LAST_MAPS_AND_LAYOUT)); // true
		controls.add(new BooleanProperty(

		"experimental_file_locking_on.tooltip", "experimental_file_locking_on")); // false
		controls.add(new NextLineProperty());
		controls.add(new StringProperty(null, "userproperties")); // user.properties

		// FIXME: Remove autoproperties from Freemind.
		// controls.add(new StringProperty(null, "autoproperties")); //
		// auto.properties

		controls.add(new StringProperty(null, "patternsfile")); // patterns.xml
		// FIXME: Which one? controls.add(new StringProperty(
		// "The URL of the documentation mindmap (.mm)", "docmapurl")); //
		// ./doc/freemind.mm

		// replaced by browsemode_initial_map?? (See Controller,
		// DocumentationAction).
		// controls.add(new StringProperty(null,
		// "docmapurl_since_version_0_7_0")); // ./doc/freemind.mm
		//
		// The Browse Mode
		//

		controls.add(new StringProperty("browsemode_initial_map.tooltip",
				"browsemode_initial_map")); // ./doc/freemind.mm
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("automatic_save"));
		controls.add(new StringProperty("time_for_automatic_save.tooltip",
				"time_for_automatic_save")); // 60000
		//

		controls.add(new BooleanProperty(
				"delete_automatic_saves_at_exit.tooltip",
				"delete_automatic_saves_at_exit")); // true

		controls.add(new StringProperty(
				"number_of_different_files_for_automatic_save.tooltip",
				"number_of_different_files_for_automatic_save")); // 10

		controls.add(new StringProperty("path_to_automatic_saves.tooltip",
				"path_to_automatic_saves")); // freemind_home

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("save"));

		controls.add(new BooleanProperty(
				"resources_save_folding_state.tooltip",
				FreeMind.RESOURCES_SAVE_FOLDING_STATE)); // true
		controls.add(new BooleanProperty(
				"save_only_intrisically_needed_ids.tooltip",
				FreeMindCommon.SAVE_ONLY_INTRISICALLY_NEEDED_IDS)); // false
		/***********************************************************************
		 * Defaults
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Defaults"));
		controls.add(new SeparatorProperty("default_styles"));
		controls.add(new ComboProperty("standardnodestyle.tooltip",
				FreeMind.RESOURCES_NODE_STYLE, MindMapNode.NODE_STYLES, this)); // as_parent

		controls.add(new ComboProperty("standardrootnodestyle.tooltip",
				FreeMind.RESOURCES_ROOT_NODE_STYLE, new String[] {
						MindMapNode.STYLE_FORK, MindMapNode.STYLE_BUBBLE,
						MindMapNode.STYLE_COMBINED }, this)); // fork

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("default_colors"));
		controls.add(new ColorProperty("standardnodetextcolor.tooltip",
				FreeMind.RESOURCES_NODE_TEXT_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_NODE_TEXT_COLOR),
				this)); // #000000

		controls.add(new ColorProperty("standardedgecolor.tooltip",
				FreeMind.RESOURCES_EDGE_COLOR, fmMain
						.getDefaultProperty(FreeMind.RESOURCES_EDGE_COLOR),
				this)); // #808080

		controls.add(new ColorProperty("standardlinkcolor.tooltip",
				FreeMind.RESOURCES_LINK_COLOR, fmMain
						.getDefaultProperty(FreeMind.RESOURCES_LINK_COLOR),
				this)); // #b0b0b0

		controls.add(new ColorProperty("standardbackgroundcolor.tooltip",
				FreeMind.RESOURCES_BACKGROUND_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_BACKGROUND_COLOR),
				this)); // #ffffff

		controls.add(new BooleanProperty(
				FreeMind.RESOURCE_PRINT_ON_WHITE_BACKGROUND + TOOLTIP_EXT,
				FreeMind.RESOURCE_PRINT_ON_WHITE_BACKGROUND)); // true

		controls.add(new ColorProperty("standardcloudcolor.tooltip",
				FreeMind.RESOURCES_CLOUD_COLOR, fmMain
						.getDefaultProperty(FreeMind.RESOURCES_CLOUD_COLOR),
				this)); // #f0f0f0

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("default_fonts"));
		controls.add(new StringProperty(

		"defaultfont.tooltip", "defaultfont")); // SansSerif

		controls.add(new StringProperty(null, "defaultfontstyle")); // 0

		controls.add(new NumberProperty("defaultfontsize.tooltip",
				"defaultfontsize", 1,
				96 /* taken from openoffice as maximum. */, 1)); // 12
		// controls.add(new StringProperty(null, "defaultfontsize")); // 12

		controls.add(new NumberProperty("max_node_width.tooltip",
				"max_node_width", 1, Integer.MAX_VALUE, 1)); // 600

		controls.add(new NumberProperty("max_tooltip_width.tooltip",
				"max_tooltip_width", 1, Integer.MAX_VALUE, 1)); // 600

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("other_defaults"));
		controls.add(new ComboProperty("standardedgestyle.tooltip",
				FreeMind.RESOURCES_EDGE_STYLE, new String[] { "bezier",
						"linear" }, this)); // bezier

		// controls.add(new ComboProperty(
		//
		// "standardcloudestyle.tooltip", "standardcloudestyle",
		// new String[] { "bezier" })); // bezier
		//
		// controls.add(new ComboProperty(
		//
		// "standardlinkestyle.tooltip", "standardlinkestyle",
		// new String[] { "bezier" })); // bezier

		/***********************************************************************
		 * Appearance
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Appearance"));
		controls.add(new SeparatorProperty("look_and_feel"));
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		int reservedCount = 6;
		String[] lafNames = new String[lafInfo.length + reservedCount];
		Vector translatedLafNames = new Vector();
		lafNames[0] = "default";
		translatedLafNames.add(getText("default"));
		lafNames[1] = "metal";
		translatedLafNames.add(getText("metal"));
		lafNames[2] = "windows";
		translatedLafNames.add(getText("windows"));
		lafNames[3] = "motif";
		translatedLafNames.add(getText("motif"));
		lafNames[4] = "gtk";
		translatedLafNames.add(getText("gtk"));
		lafNames[5] = "nothing";
		translatedLafNames.add(getText("nothing"));
		for (int i = 0; i < lafInfo.length; i++) {
			LookAndFeelInfo info = lafInfo[i];
			String className = info.getClassName();
			lafNames[i + reservedCount] = className;
			translatedLafNames.add(info.getName());
		}
		controls.add(new ComboProperty("lookandfeel.tooltip",
				FreeMind.RESOURCE_LOOKANDFEEL, lafNames, translatedLafNames)); // default

		controls.add(new BooleanProperty("use_tabbed_pane.tooltip",
				FreeMind.RESOURCES_USE_TABBED_PANE)); // true
		controls.add(new ComboProperty(FreeMind.J_SPLIT_PANE_SPLIT_TYPE
				+ TOOLTIP_EXT, FreeMind.J_SPLIT_PANE_SPLIT_TYPE,
				new String[] { FreeMind.VERTICAL_SPLIT_BELOW,
						FreeMind.HORIZONTAL_SPLIT_RIGHT }, this));

		controls.add(new NumberProperty(
				StructuredMenuHolder.AMOUNT_OF_VISIBLE_MENU_ITEMS + TOOLTIP_EXT,
				StructuredMenuHolder.AMOUNT_OF_VISIBLE_MENU_ITEMS, 10,
				Integer.MAX_VALUE, 1));

		controls.add(new BooleanProperty(FreeMind.RESOURCES_DISPLAY_FOLDING_BUTTONS+TOOLTIP_EXT,
				FreeMind.RESOURCES_DISPLAY_FOLDING_BUTTONS)); // true
		// controls.add(new BooleanProperty(
		//
		// "use_split_pane.tooltip", FreeMind.RESOURCES_USE_SPLIT_PANE)); //
		// true

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("selection_colors"));
		controls.add(new BooleanProperty(
				FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION + TOOLTIP_EXT,
				FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION)); // false
		controls.add(new ColorProperty(
				"standardselectednoderectanglecolor.tooltip",
				FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_SELECTED_NODE_RECTANGLE_COLOR),
				this)); // #000000

		controls.add(new ColorProperty(
				"standardselectednodecolor.tooltip",
				FreeMind.RESOURCES_SELECTED_NODE_COLOR,
				fmMain.getDefaultProperty(FreeMind.RESOURCES_SELECTED_NODE_COLOR),
				this)); // #D2D2D2

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		final String RESOURCE_ROOT_NODE = "root_node_appearance";
		final String RESOURCE_USE_COMMON_OUT_POINT_FOR_ROOT_NODE = "use_common_out_point_for_root_node";
		controls.add(new SeparatorProperty(RESOURCE_ROOT_NODE));
		controls.add(new BooleanProperty(
				RESOURCE_USE_COMMON_OUT_POINT_FOR_ROOT_NODE + TOOLTIP_EXT,
				RESOURCE_USE_COMMON_OUT_POINT_FOR_ROOT_NODE)); // false
		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("anti_alias"));
		controls.add(new ComboProperty("antialias.tooltip",
				FreeMindCommon.RESOURCE_ANTIALIAS, new String[] {
						"antialias_edges", "antialias_all", "antialias_none" },
				this)); // true

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("initial_map_size"));
		controls.add(new StringProperty("mapxsize.tooltip", "mapxsize")); // 1000

		controls.add(new StringProperty(null, "mapysize")); // 3200

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("hyperlink_types"));
		controls.add(new ComboProperty("links.tooltip", "links", new String[] {
				"relative", "absolute" }, this)); // relative

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("edit_long_node_window"));
		controls.add(new StringProperty("el__buttons_position.tooltip",
				"el__buttons_position")); // above

		controls.add(new BooleanProperty(null, "el__position_window_below_node")); // true

		controls.add(new StringProperty(null, "el__min_default_window_height")); // 150

		controls.add(new StringProperty(null, "el__max_default_window_height")); // 500

		controls.add(new StringProperty(null, "el__min_default_window_width")); // 600

		controls.add(new StringProperty(null, "el__max_default_window_width")); // 600

		controls.add(new BooleanProperty(null, "el__enter_confirms_by_default")); // true

		controls.add(new BooleanProperty(null, "el__show_icon_for_attributes")); // true

		controls.add(new SeparatorProperty("note_properties"));
		controls.add(new BooleanProperty(null,
				FreeMind.RESOURCES_DON_T_SHOW_NOTE_ICONS));
		controls.add(new BooleanProperty(null,
				FreeMind.RESOURCES_DON_T_SHOW_NOTE_TOOLTIPS));
		controls.add(new NumberProperty(FreeMind.TOOLTIP_DISPLAY_TIME
				+ ".tooltip", FreeMind.TOOLTIP_DISPLAY_TIME, 0,
				Integer.MAX_VALUE, 1)); // 4000
		controls.add(new SeparatorProperty("icon_properties"));
		controls.add(new StringProperty("icon_order_description",
				MindIcon.PROPERTY_STRING_ICONS_LIST));
		/***********************************************************************
		 * Keystrokes
		 * ****************************************************************
		 */
		String form = "right:max(40dlu;p), 4dlu, 80dlu, 7dlu";
		controls.add(new NewTabProperty("Keystrokes", form + "," + form)); // ", right:max(40dlu;p), 4dlu, 60dlu"));
		//
		// These are the accelerators for the menu items. Valid modifiers are:
		// shift | control | alt | meta | button1 | button2 | button3
		// Valid keys should be all that are defined in java.awt.event.KeyEvent
		// (without the "VK_" prefix), but I found this buggy. All normal char's
		// should work.

		// The ideas employed in choice of keyboard shortcuts are:
		// If there is a standard for a feature, use it
		// Use control modifier whereever possible

		// Commands for the program
		controls.add(new SeparatorProperty("commands_for_the_program"));
		controls.add(new KeyProperty(frame, null, "keystroke_newMap")); // control
		// N

		controls.add(new KeyProperty(frame, null, "keystroke_open")); // control
		// O

		controls.add(new KeyProperty(frame, null, "keystroke_save")); // control
		// S

		controls.add(new KeyProperty(frame, null, "keystroke_saveAs")); // control
		// shift
		// S

		controls.add(new KeyProperty(frame, null, "keystroke_print")); // control
		// P

		controls.add(new KeyProperty(frame, null, "keystroke_close")); // control
		// W

		controls.add(new KeyProperty(frame, null, "keystroke_quit")); // control
		// Q

		controls.add(new KeyProperty(frame, null, "keystroke_option_dialog")); // control
																				// COMMA

		controls.add(new KeyProperty(frame, null, "keystroke_export_to_html")); // control
		// E

		controls.add(new KeyProperty(frame, null,
				"keystroke_export_branch_to_html")); // control
		// H

		controls.add(new KeyProperty(frame, null,
				"keystroke_open_first_in_history")); // control
		// shift
		// W

		controls.add(new KeyProperty(frame, null,
				FreeMind.KEYSTROKE_PREVIOUS_MAP)); // control
		// LEFT

		controls.add(new KeyProperty(frame, null, FreeMind.KEYSTROKE_NEXT_MAP)); // control
		// RIGHT

		controls.add(new KeyProperty(frame, null,
				FreeMind.KEYSTROKE_MOVE_MAP_LEFT));

		controls.add(new KeyProperty(frame, null,
				FreeMind.KEYSTROKE_MOVE_MAP_RIGHT));

		controls.add(new KeyProperty(frame, null, "keystroke_mode_MindMap")); // alt
		// 1

		controls.add(new KeyProperty(frame, null, "keystroke_mode_Browse")); // alt
		// 2

		controls.add(new KeyProperty(frame, null, "keystroke_mode_File")); // alt
		// 3

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_italic")); // control
		// I

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_boldface")); // control
		// B

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_underlined")); // control
		// U

		controls.add(new KeyProperty(frame, null, "keystroke_node_toggle_cloud")); // control
		// shift
		// B

		controls.add(new KeyProperty(frame, null, "keystroke_undo")); // control
		// Z

		controls.add(new KeyProperty(frame, null, "keystroke_redo")); // control
		// Y

		controls.add(new KeyProperty(frame, null, "keystroke_delete_child")); // DELETE

		controls.add(new KeyProperty(frame, null, "keystroke_select_all")); // control
		// A

		controls.add(new KeyProperty(frame, null, "keystroke_select_branch")); // control
		// shift A

		controls.add(new KeyProperty(frame, null, "keystroke_zoom_out")); // alt
		// UP

		controls.add(new KeyProperty(frame, null, "keystroke_zoom_in")); // alt
		// DOWN

		// Node editing commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_editing_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_cut")); // control
		// X

		controls.add(new KeyProperty(frame, null, "keystroke_copy")); // control
		// C

		controls.add(new KeyProperty(frame, null, "keystroke_copy_single")); // control
		// shift C

		controls.add(new KeyProperty(frame, null, "keystroke_paste")); // control
		// V

		controls.add(new KeyProperty(frame, null, "keystroke_pasteAsPlainText"));

		controls.add(new KeyProperty(frame, null, "keystroke_remove")); // none

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_arrow_link_action")); // control
		// L

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_local_link_action")); // alt

		// L

		// Unline with control X, the node you remove with action remove cannot
		// be
		// pasted again. Therefore, we do not provide any quick shortcut. We
		// suggest
		// that you use cut instead of remove.

		// Node navigation commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_navigation_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_moveToRoot")); // ESCAPE

		controls.add(new KeyProperty(frame, null, "keystroke_move_up")); // E

		controls.add(new KeyProperty(frame, null, "keystroke_move_down")); // D

		controls.add(new KeyProperty(frame, null, "keystroke_move_left")); // S

		controls.add(new KeyProperty(frame, null, "keystroke_move_right")); // F

		controls.add(new KeyProperty(frame, null, "keystroke_follow_link")); // control

		// ENTER

		// New node commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("new_node_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_add")); // ENTER

		controls.add(new KeyProperty(frame, null, "keystroke_add_child")); // INSERT

		controls.add(new KeyProperty(frame, null, "keystroke_add_child_mac")); // TAB

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_sibling_before")); // shift

		// ENTER

		// Node editing commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_editing_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_edit")); // F2

		controls.add(new KeyProperty(frame, null, "keystroke_edit_long_node")); // alt
		// ENTER

		controls.add(new KeyProperty(frame, null, "keystroke_join_nodes")); // control
		// J

		controls.add(new KeyProperty(frame, null, "keystroke_toggle_folded")); // SPACE

		controls.add(new KeyProperty(frame, null,
				"keystroke_toggle_children_folded")); // control
		// SPACE

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_link_by_filechooser")); // control
		// shift
		// K

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_link_by_textfield")); // control
		// K

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_image_by_filechooser")); // alt
		// K

		controls.add(new KeyProperty(frame, null, "keystroke_node_up")); // control
		// UP

		controls.add(new KeyProperty(frame, null, "keystroke_node_down")); // control
		// DOWN

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_increase_font_size")); // control
		// PLUS

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_decrease_font_size")); // control
		// MINUS

		// controls.add(new KeyProperty(frame, null,
		// "keystroke_branch_increase_font_size")); // control
		// // shift
		// // PLUS
		//
		// controls.add(new KeyProperty(frame, null,
		// "keystroke_branch_decrease_font_size")); // control
		// // shift
		// // MINUS
		//
		controls.add(new KeyProperty(frame, null, "keystroke_export_branch")); // alt
		// A
		//

		controls.add(new KeyProperty(frame, null, "keystroke_node_color")); // alt
		// F

		controls.add(new KeyProperty(frame, null, "keystroke_node_color_blend")); // alt
		// B

		controls.add(new KeyProperty(frame, null, "keystroke_edge_color")); // alt
		// E

		controls.add(new KeyProperty(frame, null, "keystroke_find")); // ctrl F

		controls.add(new KeyProperty(frame, null, "keystroke_find_next")); // ctrl
		// G

		// Apply patterns

		// There is no limiting number of the pattern, you can have as many
		// keystrokes for patterns as you want. The reason I do not follow to
		// F10 and further in this default is that F10 has special function on
		// Windows.

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("patterns"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/ManagePatterns_manage_patterns_dialog")); // control
																							// shift
																							// F1
		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_1")); // F1

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_2")); // control
		// shift
		// N

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_3")); // F3

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_4")); // F4

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_5")); // F5

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_6")); // F6

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_7")); // F7

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_8")); // F8

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_9")); // F9

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_10")); // control
		// F1

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_11")); // control
		// F2

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_12")); // control
		// F3

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_13")); // control

		// F4

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_14")); // control
		// F5

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_15")); // control
		// F6

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_16")); // control
		// F7

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_17")); // control
		// F8

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_18")); // control
		// F9

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("others"));

		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/ChangeNodeLevelAction_left.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/ChangeNodeLevelAction_right.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/FormatCopy.properties.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/FormatPaste.properties.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/IconSelectionPlugin.properties.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/NewParentNode.properties_key"));
		// controls.add(new KeyProperty(frame, null,
		// "keystroke_accessories/plugins/NodeNote.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/NodeNote_jumpto.keystroke.alt_N"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/NodeNote_hide_show.keystroke.control_shift_less"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/RemoveNote.properties.properties_key"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/UnfoldAll.keystroke.alt_PAGE_UP"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/UnfoldAll.keystroke.alt_PAGE_DOWN"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/UnfoldAll.keystroke.alt_HOME"));
		controls.add(new KeyProperty(frame, null,
				"keystroke_accessories/plugins/UnfoldAll.keystroke.alt_END"));

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("attributes"));
		controls.add(new KeyProperty(frame, null, "keystroke_edit_attributes")); // control
		controls.add(new KeyProperty(frame, null,
				"keystroke_show_all_attributes")); // control
		controls.add(new KeyProperty(frame, null,
				"keystroke_show_selected_attributes")); // control
		controls.add(new KeyProperty(frame, null,
				"keystroke_hide_all_attributes")); // control
		controls.add(new KeyProperty(frame, null,
				"keystroke_show_attribute_manager")); // control
		controls.add(new KeyProperty(frame, null, "keystroke_assign_attributes")); // control
		controls.add(new KeyProperty(frame, null,
				"keystroke_plugins/ScriptingEngine.keystroke.evaluate"));

		final ModeController modeController = fmMain.getController()
				.getModeController();
		if (modeController instanceof MindMapController) {
			MindMapController controller = (MindMapController) modeController;
			Vector iconActions = controller.iconActions;
			Vector actions = new Vector();
			actions.addAll(iconActions);
			actions.add(controller.removeLastIconAction);
			actions.add(controller.removeAllIconsAction);
			controls.add(new NextLineProperty());
			controls.add(new SeparatorProperty("icons"));
			final Iterator iterator = actions.iterator();
			while (iterator.hasNext()) {
				IconInformation info = (IconInformation) iterator.next();
				final KeyProperty keyProperty = new KeyProperty(frame, null,
						info.getKeystrokeResourceName());
				keyProperty.setLabelText(info.getDescription());
				keyProperty.setImageIcon(info.getIcon());
				keyProperty.disableModifiers();
				controls.add(keyProperty);
			}

		}

		/***********************************************************************
		 * Misc ****************************************************************
		 */
		controls.add(new NewTabProperty("Behaviour"));
		controls.add(new SeparatorProperty("behaviour"));
		controls.add(new BooleanProperty("enable_node_movement.tooltip",
				"enable_node_movement"));
		controls.add(new BooleanProperty(FreeMind.RESOURCES_SEARCH_IN_NOTES_TOO
				+ TOOLTIP_EXT, FreeMind.RESOURCES_SEARCH_IN_NOTES_TOO));

		controls.add(new ComboProperty("placenewbranches.tooltip",
				"placenewbranches", new String[] { "first", "last" }, this)); // last
		controls.add(new BooleanProperty("draganddrop.tooltip", "draganddrop")); // true

		controls.add(new BooleanProperty("unfold_on_paste.tooltip",
				"unfold_on_paste")); // true

		controls.add(new BooleanProperty("disable_cursor_move_paper.tooltip",
				"disable_cursor_move_paper")); // false

		controls.add(new BooleanProperty("enable_leaves_folding.tooltip",
				"enable_leaves_folding")); // false

		controls.add(new StringProperty("foldingsymbolwidth.tooltip",
				"foldingsymbolwidth")); // 6

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("key_typing"));
		controls.add(new BooleanProperty("disable_key_type.tooltip",
				"disable_key_type")); // false

		controls.add(new BooleanProperty("key_type_adds_new.tooltip",
				"key_type_adds_new")); // false

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("resources_notifications"));
		controls.add(new RemindValueProperty(
				"remind_type_of_new_nodes.tooltip",
				FreeMind.RESOURCES_REMIND_USE_RICH_TEXT_IN_NEW_LONG_NODES,
				modeController));
		controls.add(new DontShowNotificationProperty(
				"resources_convert_to_current_version.tooltip",
				FreeMind.RESOURCES_CONVERT_TO_CURRENT_VERSION));
		controls.add(new RemindValueProperty(
				"reload_files_without_question.tooltip",
				FreeMind.RESOURCES_RELOAD_FILES_WITHOUT_QUESTION,
				modeController));
		controls.add(new DontShowNotificationProperty(
				"delete_nodes_without_question.tooltip",
				FreeMind.RESOURCES_DELETE_NODES_WITHOUT_QUESTION));
		controls.add(new DontShowNotificationProperty(
				"cut_nodes_without_question.tooltip",
				FreeMind.RESOURCES_CUT_NODES_WITHOUT_QUESTION));
		controls.add(new DontShowNotificationProperty(
				"remove_notes_without_question.tooltip",
				FreeMind.RESOURCES_REMOVE_NOTES_WITHOUT_QUESTION));
		controls.add(new RemindValueProperty(
				FreeMind.RESOURCES_COMPLETE_CLONING + ".tooltip",
				FreeMind.RESOURCES_COMPLETE_CLONING, modeController));
		controls.add(new DontShowNotificationProperty(
				"execute_scripts_without_asking.tooltip",
				FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING));

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty(FreeMind.RESOURCES_SELECTION_METHOD));
		controls.add(new ComboProperty("selection_method.tooltip",
				FreeMind.RESOURCES_SELECTION_METHOD, new String[] {
						"selection_method_direct", "selection_method_delayed",
						"selection_method_by_click" }, this)); // selection_method_direct

		controls.add(new NumberProperty("time_for_delayed_selection.tooltip",
				"time_for_delayed_selection", 1, Integer.MAX_VALUE, 1)); // 500

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("mouse_wheel"));
		controls.add(new NumberProperty("wheel_velocity.tooltip",
				FreeMind.RESOURCES_WHEEL_VELOCITY, 1, 250, 1));
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("undo"));
		controls.add(new NumberProperty("undo_levels.tooltip", "undo_levels",
				2, 1000, 1));

		/***********************************************************************
		 * Browser/external apps
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("HTML"));
		controls.add(new SeparatorProperty("browser"));
		//
		// The default browser setting
		//
		// For Windows (the \"\" signs are necessary due to links, that have "="
		// in their URL).
		// default_browser_command_windows_nt = explorer "{0}"
		//
		// The next setting works for the default browser, but eventually starts
		// programs without questions, so be careful!
		//
		// default_browser_command_windows_nt = rundll32
		// url.dll,FileProtocolHandler {0}

		controls.add(new StringProperty(
				"default_browser_command_windows_nt.tooltip",
				"default_browser_command_windows_nt")); // cmd.exe
		// /c
		// start
		// ""
		// "{0}"

		controls.add(new StringProperty(
				"default_browser_command_windows_9x.tooltip",
				"default_browser_command_windows_9x")); // command.com
		// /c
		// start
		// "{0}"
		// Dimitri proposed:
		// default_browser_command_windows_9x = explorer "{0}"
		//
		// Here the default browser for other operating systems goes:
		//

		controls.add(new StringProperty(
				"default_browser_command_other_os.tooltip",
				"default_browser_command_other_os")); // mozilla {0}
		//

		controls.add(new StringProperty("default_browser_command_mac.tooltip",
				"default_browser_command_mac")); // open -a
		// /Applications/Safari.app {0}

		controls.add(new SeparatorProperty("html_export"));
		//
		controls.add(new ComboProperty(
				null,
				"html_export_folding",
				new String[] { "html_export_no_folding",
						"html_export_fold_currently_folded",
						"html_export_fold_all", "html_export_based_on_headings" },
				this)); // html_export_fold_currently_folded

		controls.add(new NextLineProperty());
		controls.add(new BooleanProperty("export_icons_in_html.tooltip",
				"export_icons_in_html")); // false

		for (Iterator iter = sContributors.iterator(); iter.hasNext();) {
			FreemindPropertyContributor contributor = (FreemindPropertyContributor) iter
					.next();
			controls.addAll(contributor.getControls(this));
		}
		return controls;
	}

	public void closeWindow() {
		OptionPanelWindowConfigurationStorage storage = new OptionPanelWindowConfigurationStorage();
		storage.setPanel(selectedPanel);
		XmlBindingTools.getInstance().storeDialogPositions(
				fmMain.getController(), frame, storage,
				PREFERENCE_STORAGE_PROPERTY);
		frame.setVisible(false);
		frame.dispose();
	}

	private static Set sContributors = new HashSet();

	public static void addContributor(FreemindPropertyContributor contributor) {
		sContributors.add(contributor);
	}

	public static void removeContributor(FreemindPropertyContributor contributor) {
		sContributors.remove(contributor);
	}

}
