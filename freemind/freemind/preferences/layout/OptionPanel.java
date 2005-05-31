/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
/*$Id: OptionPanel.java,v 1.1.4.2 2005-05-31 20:24:06 dpolivaev Exp $*/
package freemind.preferences.layout;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import freemind.controller.Controller;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;

/**
 * @author foltin
 *  
 */
public class OptionPanel {

	//TODO: mark first button blue.
	//TODO: Cancel and windowClose => Are you sure, or save.
    //FIXME: key dialog
    //FIXME: Translate me and html 
	private static final Color MARKED_BUTTON_COLOR = Color.BLUE;

	private Vector controls;

	private final JDialog frame;

	private HashMap tabButtonMap = new HashMap();

	private static JColorChooser colorChooser;

	private final OptionPanelFeedback feedback;

	private static FreeMindMain fmMain;

	/**
	 * @param frame
	 * @param feedback
	 * @throws IOException
	 *  
	 */
	public OptionPanel(FreeMindMain fm, JDialog frame,
			OptionPanelFeedback feedback) {
		super();
		if (fmMain == null) {
			fmMain = fm;
		}
		this.frame = frame;
		this.feedback = feedback;
	}

	public interface OptionPanelFeedback {
		void writeProperties(Properties props);
	}

	public static Vector changeListeners = new Vector();

	private static Properties getProperties() {
		checkConnectionToFreeMindMain();
		return fmMain.getProperties();
	}

	/**
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		for (Iterator i = controls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
			if (control instanceof PropertyBean) {
				PropertyBean bean = (PropertyBean) control;
				//				System.out.println("grep -n -e \""+bean.getLabel()+"\" -r * |
				// grep -e \"\\.(java|xml):\"");
				String value = properties.getProperty(bean.getLabel());
				//				System.out.println("Setting property " + bean.getLabel()
				//						+ " to " + value);
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
				p.setProperty(bean.getLabel(), bean.getValue());
			}
		}
		return p;
	}

	public void buildPanel() {

		FormLayout leftLayout = new FormLayout("80dlu", "");
		DefaultFormBuilder leftBuilder = new DefaultFormBuilder(leftLayout);

		CardLayout cardLayout = new CardLayout();
		JPanel rightStack = new JPanel(cardLayout);

		FormLayout rightLayout = null; // add rows dynamically
		DefaultFormBuilder rightBuilder = null;
		String lastTabName = null;

		controls = getControls();
		for (Iterator i = controls.iterator(); i.hasNext();) {
			PropertyControl control = (PropertyControl) i.next();
//			System.out.println("layouting : " + control.getLabel());

			if (control instanceof NewTabProperty) {
				NewTabProperty newTab = (NewTabProperty) control;
				if (rightBuilder != null) {
					// terminate old panel:
					rightStack.add(rightBuilder.getPanel(), lastTabName);
				}
				rightLayout = new FormLayout(
						"right:max(40dlu;p), 4dlu, 60dlu, 7dlu, " // 1st major
								// column
								+ "right:max(40dlu;p), 4dlu, 60dlu" // 2nd major
								// column
						, "");
				rightBuilder = new DefaultFormBuilder(rightLayout);
				rightBuilder.setDefaultDialogBorder();
				lastTabName = newTab.getLabel();
				// add a button to the left side:
				JButton tabButton = new JButton(getText(lastTabName));
				ChangeTabAction changeTabAction = new ChangeTabAction(
						cardLayout, rightStack, lastTabName);
				tabButton.addActionListener(changeTabAction);
				registerTabButton(tabButton, lastTabName);
				leftBuilder.append(tabButton);
			} else {
				control.layout(rightBuilder);
			}
		}
		// add the last one, too
		rightStack.add(rightBuilder.getPanel(), lastTabName);
		JSplitPane centralPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftBuilder.getPanel(), new JScrollPane(rightStack));
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
		frame.getContentPane().add(
				ButtonBarFactory.buildOKCancelBar(cancelButton, okButton),
				BorderLayout.SOUTH);
	}

	private static String lastKey = "";

	/**
	 * @param string
	 * @return
	 */
	private static String getText(String string) {
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
	 * @param tabButton
	 * @param lastTabName
	 */
	private void registerTabButton(JButton tabButton, String name) {
		tabButtonMap.put(name, tabButton);

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
		}
	}

	private interface PropertyControl {

		String getDescription();

		String getLabel();

		void layout(DefaultFormBuilder builder);
	}

	private interface PropertyBean {
		/** The key of the property. */
		String getLabel();

		void setValue(String value);

		String getValue();

	}

	private static class SeparatorProperty implements PropertyControl {

		private String label;

		public SeparatorProperty(String label) {
			super();
			this.label = label;
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return label;
		}

		public void layout(DefaultFormBuilder builder) {
			builder.appendSeparator(getText("separator."+getLabel()));
		}

	}

	private static class NewTabProperty implements PropertyControl {

		private String label;

		public NewTabProperty(String label) {
			super();
			this.label = label;
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return label;
		}

		public void layout(DefaultFormBuilder builder) {

		}

	}

	private static class NextLineProperty implements PropertyControl {

		public NextLineProperty() {
			super();
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return null;
		}

		public void layout(DefaultFormBuilder builder) {
			builder.nextLine();
		}

	}

	private static class StringProperty extends JTextField implements
			PropertyControl, PropertyBean {
		String description;

		String label;

		/**
		 * @param description
		 * @param label
		 */
		public StringProperty(String description, String label) {
			super();
			this.description = description;
			this.label = label;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			setText(value);
		}

		public String getValue() {
			return getText();
		}

		public void layout(DefaultFormBuilder builder) {
			JLabel label = builder
					.append(OptionPanel.getText(getLabel()), this);
			label.setToolTipText(OptionPanel.getText(getDescription()));
		}

	}

	private static class ColorProperty extends JButton implements
			PropertyControl, PropertyBean, ActionListener {
		String description;

		String label;

		Color color;

		/**
		 * @param description
		 * @param label
		 */
		public ColorProperty(String description, String label) {
			super();
			this.description = description;
			this.label = label;
			addActionListener(this);
			color = Color.BLACK;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			setColorValue(Tools.xmlToColor(value));
		}

		public String getValue() {
			return Tools.colorToXml(getColorValue());
		}

		public void layout(DefaultFormBuilder builder) {
			JLabel label = builder
					.append(OptionPanel.getText(getLabel()), this);
			label.setToolTipText(OptionPanel.getText(getDescription()));
		}

		public void actionPerformed(ActionEvent arg0) {
			Color result = Controller.showCommonJColorChooserDialog(
					getRootPane(), getLabel(), getColorValue());
			if (result != null) {
				setColorValue(result);
			}
		}

		/**
		 * @param result
		 */
		private void setColorValue(Color result) {
			color = result;
			setBackground(result);
			setText(Tools.colorToXml(result));
		}

		/**
		 * @return
		 */
		private Color getColorValue() {
			return color;
		}

	}

	private static class KeyProperty extends JButton implements
			PropertyControl, PropertyBean {
		String description;

		String label;

		/**
		 * @param description
		 * @param label
		 */
		public KeyProperty(final JDialog frame, String description, String label) {
			super();
			this.description = description;
			this.label = label;
			addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					//FIXME: Determine bindings.
					Vector allKeybindings = new Vector();
					GrabKeyDialog dialog = new GrabKeyDialog(fmMain, frame,
							new GrabKeyDialog.KeyBinding(getLabel(),
									getLabel(), getValue(), false),
							allKeybindings, null);
					if (dialog.isOK()) {
						setValue(dialog.getShortcut());
					}
				}
			});
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			setText(value);
		}

		public String getValue() {
			return getText();
		}

		public void layout(DefaultFormBuilder builder) {
			JLabel label = builder
					.append(OptionPanel.getText(getLabel()), this);
			label.setToolTipText(OptionPanel.getText(getDescription()));
		}

	}

	private static class BooleanProperty extends JCheckBox implements
			PropertyControl, PropertyBean {
		String description;

		String label;

		/**
		 * @param description
		 * @param label
		 */
		public BooleanProperty(String description, String label) {
			super();
			this.description = description;
			this.label = label;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			if (value == null
					|| !(value.toLowerCase().equals("true") || value
							.toLowerCase().equals("false"))) {
				throw new IllegalArgumentException("Cannot set a boolean to "
						+ value);
			}
			setSelected(value.toLowerCase().equals("true"));
		}

		public String getValue() {
			return isSelected() ? "true" : "false";
		}

		public void layout(DefaultFormBuilder builder) {
			JLabel label = builder
					.append(OptionPanel.getText(getLabel()), this);
			label.setToolTipText(OptionPanel.getText(getDescription()));
		}

	}

	private static class ComboProperty extends JComboBox implements
			PropertyControl, PropertyBean {
		String description;

		String label;

		private Vector possibleValues;

		/**
		 * @param description
		 * @param label
		 */
		public ComboProperty(String description, String label,
				String[] possibles) {
			super();
			this.description = description;
			this.label = label;
			fillPossibleValues(possibles);
			Vector possibleTranslations = new Vector();
			for (Iterator i = possibleValues.iterator(); i.hasNext();) {
				String key = (String) i.next();
				possibleTranslations.add(OptionPanel.getText(key));
			}
			setModel(new DefaultComboBoxModel(possibleTranslations));
		}

        public ComboProperty(String description, String label,
				String[] possibles, Vector possibleTranslations) {
			this.description = description;
			this.label = label;
			fillPossibleValues(possibles);
			setModel(new DefaultComboBoxModel(possibleTranslations));
		}
		/**
         * @param possibles
         */
        private void fillPossibleValues(String[] possibles) {
            this.possibleValues = new Vector();
			possibleValues.addAll(Arrays.asList(possibles));
        }

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			if (possibleValues.contains(value)) {
				super.setSelectedIndex(possibleValues.indexOf(value));
			} else {
				throw new IllegalArgumentException("Unknown value:" + value);
			}
		}

		public String getValue() {
			return (String) possibleValues.get(super.getSelectedIndex());
		}

		public void layout(DefaultFormBuilder builder) {
			JLabel label = builder
					.append(OptionPanel.getText(getLabel()), this);
			label.setToolTipText(OptionPanel.getText(getDescription()));
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
		//TODO: Search class path for translations.
		controls.add(new ComboProperty(

		"language.tooltip", FreeMind.RESOURCE_LANGUAGE, new String[] { "automatic", "de", "dk",
				"en", "es", "fr", "hu", "it", "ja", "kr", "nl", "pl", "pt_BR",
				"pt_PT", "ru", "sl", "zh", "zh_CN" })); //  automatic

		//INTERNAL PROPERTY.
		//		controls
		//				.add(new StringProperty(
		//						"The Modes which Freemind will load on startup, full Class names,
		// comma, identifier, separated by a comma.",
		//						"modes_since_0_8_0")); //
		// freemind.modes.browsemode.BrowseMode,Browse,freemind.modes.mindmapmode.MindMapMode,MindMap,freemind.modes.filemode.FileMode,File
		//
		//		controls.add(new StringProperty(
		//				"The initial mode that is loaded on startup", "initial_mode")); //
		// MindMap
		//
		//		controls
		//				.add(new StringProperty(
		//						"This is the place where the users properties file is located. It is
		// ignored by the applet (set Parameters in the html file instead). You
		// can write '~' to indicate the users home directory. Of course this
		// works only in the default 'freemind.properties', which is included in
		// the jar file, not for the users freemind.props out of the jar file.",
		//						"properties_folder")); // .freemind

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("files"));
		controls.add(new StringProperty(null, "last_opened_list_length")); //  25
		controls
				.add(new BooleanProperty(

				"experimental_file_locking_on.tooltip",
						"experimental_file_locking_on")); //  false
		controls.add(new NextLineProperty());
		controls.add(new StringProperty(null, "userproperties")); //  user.properties

		//FIXME: Remove autoproperties from Freemind.
		//		controls.add(new StringProperty(null, "autoproperties")); //
		// auto.properties

		controls.add(new StringProperty(null, "patternsfile")); //  patterns.xml
		//FIXME: Which one? controls.add(new StringProperty(
		//				"The URL of the documentation mindmap (.mm)", "docmapurl")); //
		// ./doc/freemind.mm

		controls.add(new StringProperty(null, "docmapurl_since_version_0_7_0")); //  ./doc/freemind.mm
		//
		//		 The Browse Mode
		//

		controls.add(new StringProperty(

		"browsemode_initial_map.tooltip", "browsemode_initial_map")); //  ./doc/freemind.mm
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("automatic_save"));
		controls.add(new StringProperty(

		"time_for_automatic_save.tooltip", "time_for_automatic_save")); // 60000
		//

		controls.add(new BooleanProperty(

		"delete_automatic_saves_at_exit.tooltip",
				"delete_automatic_saves_at_exit")); // true

		controls.add(new StringProperty(

		"number_of_different_files_for_automatic_save.tooltip",
				"number_of_different_files_for_automatic_save")); // 10

		controls.add(new StringProperty(

		"path_to_automatic_saves.tooltip", "path_to_automatic_saves")); // freemind_home

		/***********************************************************************
		 * Defaults
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Defaults"));
		controls.add(new SeparatorProperty("default_styles"));
		controls.add(new ComboProperty(

		"standardnodestyle.tooltip", "standardnodestyle", new String[] {
				"fork", "bubble", "as_parent", "combined" })); //  as_parent

		controls.add(new ComboProperty(

		"standardrootnodestyle.tooltip", "standardrootnodestyle", new String[] {
				"fork", "bubble", "combined" })); //  fork

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("default_colors"));
		controls.add(new ColorProperty(

		"standardnodecolor.tooltip", "standardnodecolor")); //  #000000

		controls.add(new ColorProperty(

		"standardselectednodecolor.tooltip", "standardselectednodecolor")); //  #D2D2D2

		controls.add(new ColorProperty(

		"standardedgecolor.tooltip", "standardedgecolor")); //  #808080

		controls.add(new ColorProperty(

		"standardlinkcolor.tooltip", "standardlinkcolor")); //  #b0b0b0

		controls.add(new ColorProperty(

		"standardbackgroundcolor.tooltip", "standardbackgroundcolor")); //  #ffffff

		controls.add(new ColorProperty(

		"standardcloudcolor.tooltip", "standardcloudcolor")); //  #f0f0f0

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("default_fonts"));
		controls.add(new StringProperty(

		"defaultfont.tooltip", "defaultfont")); //  SansSerif

		controls.add(new StringProperty(null, "defaultfontstyle")); //  0

		controls.add(new StringProperty(null, "defaultfontsize")); //  12

		controls.add(new StringProperty("max_node_width.tooltip",
				"max_node_width")); //  600

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("other_defaults"));
		controls.add(new ComboProperty(

		"standardedgestyle.tooltip", "standardedgestyle", new String[] {
				"bezier", "linear" })); //  bezier

		controls.add(new ComboProperty(

		"standardcloudestyle.tooltip", "standardcloudestyle",
				new String[] { "bezier" })); //  bezier

		controls.add(new ComboProperty(

		"standardlinkestyle.tooltip", "standardlinkestyle",
				new String[] { "bezier" })); //  bezier

		/***********************************************************************
		 * Appearance
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Appearance"));
		controls.add(new SeparatorProperty("look_and_feel"));
		LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		String[] lafNames = new String[lafInfo.length + 5];
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
            lafNames[i + 5] = className;
			translatedLafNames.add(info.getName());
		}
		controls.add(new ComboProperty("lookandfeel.tooltip", FreeMind.RESOURCE_LOOKANDFEEL,
				lafNames, translatedLafNames)); //  default
		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("anti_alias"));
		controls.add(new BooleanProperty(
		"antialiasEdges.tooltip", FreeMind.RESOURCE_ANTIALIASEDGES)); //  true

		controls.add(new BooleanProperty(null, FreeMind.RESOURCE_ANTIALIASALL)); //  false

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("initial_map_size"));
		controls.add(new StringProperty("mapxsize.tooltip", "mapxsize")); //  1000

		controls.add(new StringProperty(null, "mapysize")); //  3200

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("hyperlink_types"));
		controls.add(new ComboProperty("links.tooltip", "links", new String[] {
				"relative", "absolute" })); //  relative

		/* ***************************************************************** */
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("edit_long_node_window"));
		controls.add(new StringProperty("el__buttons_position.tooltip",
				"el__buttons_position")); //  above

		controls
				.add(new BooleanProperty(null, "el__position_window_below_node")); //  true

		controls.add(new StringProperty(null, "el__min_default_window_height")); //  150

		controls.add(new StringProperty(null, "el__max_default_window_height")); //  500

		controls.add(new StringProperty(null, "el__min_default_window_width")); //  600

		controls.add(new StringProperty(null, "el__max_default_window_width")); //  600

		controls
				.add(new BooleanProperty(null, "el__enter_confirms_by_default")); //  true

		/***********************************************************************
		 * Keystrokes
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("Keystrokes"));
		//		 
		//		 These are the accelerators for the menu items. Valid modifiers are:
		//		 shift | control | alt | meta | button1 | button2 | button3
		//		 Valid keys should be all that are defined in java.awt.event.KeyEvent
		//		 (without the "VK_" prefix), but I found this buggy. All normal char's
		// should work.

		//		 The ideas employed in choice of keyboard shortcuts are:
		//		   If there is a standard for a feature, use it
		//		   Use control modifier whereever possible

		//		Commands for the program
		controls.add(new SeparatorProperty("commands_for_the_program"));
		controls.add(new KeyProperty(frame, null, "keystroke_newMap")); //  control
		// N

		controls.add(new KeyProperty(frame, null, "keystroke_open")); //  control
		// O

		controls.add(new KeyProperty(frame, null, "keystroke_save")); //  control
		// S

		controls.add(new KeyProperty(frame, null, "keystroke_saveAs")); //  control
		// shift
		// S

		controls.add(new KeyProperty(frame, null, "keystroke_print")); //  control
		// P

		controls.add(new KeyProperty(frame, null, "keystroke_close")); //  control
		// W

		controls.add(new KeyProperty(frame, null, "keystroke_quit")); //  control
		// Q

		controls.add(new KeyProperty(frame, null, "keystroke_export_to_html")); //  control
		// E

		controls.add(new KeyProperty(frame, null,
				"keystroke_export_branch_to_html")); //  control
		// H

		controls.add(new KeyProperty(frame, null,
				"keystroke_open_first_in_history")); //  control
		// shift
		// W

		controls.add(new KeyProperty(frame, null, "keystroke_previousMap")); //  control
		// LEFT

		controls.add(new KeyProperty(frame, null, "keystroke_nextMap")); //  control
		// RIGHT

		controls.add(new KeyProperty(frame, null, "keystroke_mode_MindMap")); //  alt
		// 1

		controls.add(new KeyProperty(frame, null, "keystroke_mode_Browse")); //  alt
		// 2

		controls.add(new KeyProperty(frame, null, "keystroke_mode_File")); //  alt
		// 3

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_italic")); //  control
		// I

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_boldface")); //  control
		// B

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_toggle_underlined")); //  control
		// U

		controls
				.add(new KeyProperty(frame, null, "keystroke_node_toggle_cloud")); //  control
		// shift
		// B

		controls.add(new KeyProperty(frame, null, "keystroke_undo")); //  control
		// Z

		controls.add(new KeyProperty(frame, null, "keystroke_redo")); //  control
		// Y

		controls.add(new KeyProperty(frame, null, "keystroke_delete_child")); //  DELETE

		controls.add(new KeyProperty(frame, null, "keystroke_select_all")); //  control
		// A

		controls.add(new KeyProperty(frame, null, "keystroke_select_branch")); //  control
		// shift A

		controls.add(new KeyProperty(frame, null, "keystroke_zoom_out")); //  alt
		// UP

		controls.add(new KeyProperty(frame, null, "keystroke_zoom_in")); //  alt
		// DOWN

		//		Node editing commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_editing_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_cut")); //  control
		// X

		controls.add(new KeyProperty(frame, null, "keystroke_copy")); //  control
		// C

		controls.add(new KeyProperty(frame, null, "keystroke_copy_single")); //  control
		// shift C

		controls.add(new KeyProperty(frame, null, "keystroke_paste")); //  control
		// V

		controls.add(new KeyProperty(frame, null, "keystroke_remove")); //  none

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_arrow_link_action")); // control
		// L

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_local_link_action")); // alt

		// L

		//		 Unline with control X, the node you remove with action remove cannot
		// be
		//		 pasted again. Therefore, we do not provide any quick shortcut. We
		// suggest
		//		 that you use cut instead of remove.

		//		Node navigation commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_navigation_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_moveToRoot")); //  ESCAPE

		controls.add(new KeyProperty(frame, null, "keystroke_move_up")); //  E

		controls.add(new KeyProperty(frame, null, "keystroke_move_down")); //  D

		controls.add(new KeyProperty(frame, null, "keystroke_move_left")); //  S

		controls.add(new KeyProperty(frame, null, "keystroke_move_right")); //  F

		controls.add(new KeyProperty(frame, null, "keystroke_follow_link")); //  control

		// ENTER

		//		New node commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("new_node_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_add")); //  ENTER

		controls.add(new KeyProperty(frame, null, "keystroke_add_child")); //  INSERT

		controls.add(new KeyProperty(frame, null, "keystroke_add_child_mac")); //  TAB

		controls.add(new KeyProperty(frame, null,
				"keystroke_add_sibling_before")); //  shift

		// ENTER

		//		Node editing commands
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("node_editing_commands"));
		controls.add(new KeyProperty(frame, null, "keystroke_edit")); //  F2

		controls.add(new KeyProperty(frame, null, "keystroke_edit_long_node")); //  alt
		// ENTER

		controls.add(new KeyProperty(frame, null, "keystroke_join_nodes")); //  control
		// J

		controls.add(new KeyProperty(frame, null, "keystroke_toggle_folded")); //  SPACE

		controls.add(new KeyProperty(frame, null,
				"keystroke_toggle_children_folded")); //  control
		// SPACE

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_link_by_filechooser")); //  control
		// shift
		// K

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_link_by_textfield")); //  control
		// K

		controls.add(new KeyProperty(frame, null,
				"keystroke_set_image_by_filechooser")); //  alt
		// K

		controls.add(new KeyProperty(frame, null, "keystroke_node_up")); //  control
		// UP

		controls.add(new KeyProperty(frame, null, "keystroke_node_down")); //  control
		// DOWN

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_increase_font_size")); //  control
		// PLUS

		controls.add(new KeyProperty(frame, null,
				"keystroke_node_decrease_font_size")); //  control
		// MINUS

//		controls.add(new KeyProperty(frame, null,
//				"keystroke_branch_increase_font_size")); //  control
//		// shift
//		// PLUS
//
//		controls.add(new KeyProperty(frame, null,
//				"keystroke_branch_decrease_font_size")); //  control
//		// shift
//		// MINUS
//
		controls.add(new KeyProperty(frame, null, "keystroke_export_branch")); //  alt
		// A
		//

		controls.add(new KeyProperty(frame, null, "keystroke_node_color")); //  alt
		// F

		controls
				.add(new KeyProperty(frame, null, "keystroke_node_color_blend")); //  alt
		// B

		controls.add(new KeyProperty(frame, null, "keystroke_edge_color")); //  alt
		// E

		controls.add(new KeyProperty(frame, null, "keystroke_find")); //  ctrl F

		controls.add(new KeyProperty(frame, null, "keystroke_find_next")); //  ctrl
		// G

		//		 Apply patterns

		//		 There is no limiting number of the pattern, you can have as many
		// keystrokes for patterns as you want. The reason I do not follow to
		// F10 and further in this default is that F10 has special function on
		// Windows.

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("patterns"));
		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_1")); //  F1

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_2")); //  control
		// shift
		// N

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_3")); //  F3

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_4")); //  F4

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_5")); //  F5

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_6")); //  F6

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_7")); //  F7

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_8")); //  F8

		controls.add(new KeyProperty(frame, null, "keystroke_apply_pattern_9")); //  F9

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_10")); //  control
		// F1

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_11")); //  control
		// F2

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_12")); //  control
		// F3

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_13")); //  control

		// F4

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_14")); //  control
		// F5

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_15")); //  control
		// F6

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_16")); //  control
		// F7

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_17")); //  control
		// F8

		controls
				.add(new KeyProperty(frame, null, "keystroke_apply_pattern_18")); //  control
		// F9

		/***********************************************************************
		 * Misc ****************************************************************
		 */
		controls.add(new NewTabProperty("Behaviour"));
		controls.add(new SeparatorProperty("behaviour"));
		controls.add(new ComboProperty(

		"placenewbranches.tooltip", "placenewbranches", new String[] { "first",
				"last" })); //  last
		controls.add(new BooleanProperty("draganddrop.tooltip", "draganddrop")); //  true

		controls.add(new BooleanProperty(

		"disable_cursor_move_paper.tooltip", "disable_cursor_move_paper")); //  false

		controls.add(new BooleanProperty(

		"enable_leaves_folding.tooltip", "enable_leaves_folding")); //  false
		controls.add(new StringProperty(

		"foldingsymbolwidth.tooltip", "foldingsymbolwidth")); //  6

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("key_typing"));
		controls.add(new BooleanProperty(

		"disable_key_type.tooltip", "disable_key_type")); //  false

		controls.add(new BooleanProperty(

		"key_type_adds_new.tooltip", "key_type_adds_new")); //  false

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("selection_method"));
		controls.add(new ComboProperty(

		"selection_method.tooltip", "selection_method", new String[] {
				"selection_method_direct", "selection_method_delayed",
				"selection_method_by_click" })); //  selection_method_direct

		controls.add(new StringProperty(

		"time_for_delayed_selection.tooltip", "time_for_delayed_selection")); // 500

		/***********************************************************************
		 * Browser/external apps
		 * ****************************************************************
		 */
		controls.add(new NewTabProperty("HTML"));
		controls.add(new SeparatorProperty("browser"));
		//
		//		 The default browser setting
		//
		//		 For Windows (the \"\" signs are necessary due to links, that have "="
		// in their URL).
		//		 default_browser_command_windows_nt = explorer "{0}"
		//
		//		 The next setting works for the default browser, but eventually starts
		// programs without questions, so be careful!
		//
		//		 default_browser_command_windows_nt = rundll32
		// url.dll,FileProtocolHandler {0}

		controls.add(new StringProperty(

		"default_browser_command_windows_nt.tooltip",
				"default_browser_command_windows_nt")); //  cmd.exe
		// /c
		// start
		// ""
		// "{0}"

		controls.add(new StringProperty(

		"default_browser_command_windows_9x.tooltip",
				"default_browser_command_windows_9x")); //  command.com
		// /c
		// start
		// "{0}"
		//		 Dimitri proposed:
		//		 default_browser_command_windows_9x = explorer "{0}"
		//
		//		 Here the default browser for other operating systems goes:
		//

		controls.add(new StringProperty(
				"default_browser_command_other_os.tooltip",
				"default_browser_command_other_os")); //  mozilla {0}
		//

		controls.add(new StringProperty("default_browser_command_mac.tooltip",
				"default_browser_command_mac")); //  open -a
		// /Applications/Safari.app {0}

		//
		controls
				.add(new ComboProperty(null, "html_export_folding",
						new String[] { "html_export_no_folding",
								"html_export_fold_currently_folded",
								"html_export_fold_all",
								"html_export_based_on_headings" })); //  html_export_fold_currently_folded

		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("html_export"));
		controls.add(new BooleanProperty(

		"export_icons_in_html.tooltip", "export_icons_in_html")); //  false

		return controls;
	}

	public void closeWindow() {
		frame.hide();
		frame.dispose();
	}
}