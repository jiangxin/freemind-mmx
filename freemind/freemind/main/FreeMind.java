/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
/*$Id: FreeMind.java,v 1.32.14.28.2.147 2011/01/09 21:03:13 christianfoltin Exp $*/

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager;
import freemind.controller.MenuBar;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

public class FreeMind extends JFrame implements FreeMindMain {

	private static final String SPLIT_PANE_POSITION = "split_pane_position";

	private static final String SPLIT_PANE_LAST_POSITION = "split_pane_last_position";

	public static final String RESOURCE_LOOKANDFEEL = "lookandfeel";

	public static final String RESOURCES_SELECTION_METHOD = "selection_method";

	public static final String RESOURCES_NODE_STYLE = "standardnodestyle";

	public static final String RESOURCES_ROOT_NODE_STYLE = "standardrootnodestyle";

	public static final String RESOURCES_NODE_TEXT_COLOR = "standardnodetextcolor";

	public static final String RESOURCES_SELECTED_NODE_COLOR = "standardselectednodecolor";

	public static final String RESOURCES_SELECTED_NODE_RECTANGLE_COLOR = "standardselectednoderectanglecolor";

	public static final String RESOURCE_DRAW_RECTANGLE_FOR_SELECTION = "standarddrawrectangleforselection";	

	public static final String RESOURCES_EDGE_COLOR = "standardedgecolor";

	public static final String RESOURCES_EDGE_STYLE = "standardedgestyle";

	public static final String RESOURCES_CLOUD_COLOR = "standardcloudcolor";

	public static final String RESOURCES_LINK_COLOR = "standardlinkcolor";

	public static final String RESOURCES_BACKGROUND_COLOR = "standardbackgroundcolor";

	public static final String RESOURCE_PRINT_ON_WHITE_BACKGROUND = "printonwhitebackground";

	public static final String RESOURCES_WHEEL_VELOCITY = "wheel_velocity";

	public static final String RESOURCES_USE_TABBED_PANE = "use_tabbed_pane";
	
	public static final String RESOURCES_USE_SPLIT_PANE = "use_split_pane";

    public static final String RESOURCES_DELETE_NODES_WITHOUT_QUESTION = "delete_nodes_without_question";

	private Logger logger = null;
	
	protected static final VersionInformation VERSION = new VersionInformation("0.9.0");
	
	public static final String XML_VERSION = "0.9.0";

	public static final String RESOURCES_REMIND_USE_RICH_TEXT_IN_NEW_LONG_NODES = "remind_use_rich_text_in_new_long_nodes";

	public static final String RESOURCES_EXECUTE_SCRIPTS_WITHOUT_ASKING = "resources_execute_scripts_without_asking";

	public static final String RESOURCES_EXECUTE_SCRIPTS_WITHOUT_FILE_RESTRICTION = "resources_execute_scripts_without_file_restriction";
	
	public static final String RESOURCES_EXECUTE_SCRIPTS_WITHOUT_NETWORK_RESTRICTION = "resources_execute_scripts_without_network_restriction";

	public static final String RESOURCES_EXECUTE_SCRIPTS_WITHOUT_EXEC_RESTRICTION = "resources_execute_scripts_without_exec_restriction";

	public static final String RESOURCES_SCRIPT_USER_KEY_NAME_FOR_SIGNING = "resources_script_user_key_name_for_signing";

	public static final String RESOURCES_CONVERT_TO_CURRENT_VERSION = "resources_convert_to_current_version";

	public static final String RESOURCES_CUT_NODES_WITHOUT_QUESTION = "resources_cut_nodes_without_question";

	public static final String RESOURCES_DON_T_SHOW_NOTE_ICONS = "resources_don_t_show_note_icons";

	public static final String RESOURCES_REMOVE_NOTES_WITHOUT_QUESTION = "resources_remove_notes_without_question";

	public static final String RESOURCES_SAVE_FOLDING_STATE = "resources_save_folding_state";

	public static final String RESOURCES_SIGNED_SCRIPT_ARE_TRUSTED = "resources_signed_script_are_trusted";

	public static final String RESOURCES_USE_DEFAULT_FONT_FOR_NOTES_TOO = "resources_use_default_font_for_notes_too";

	public static final String RESOURCES_USE_MARGIN_TOP_ZERO_FOR_NOTES = "resources_use_margin_top_zero_for_notes";

	// public static final String defaultPropsURL = "freemind.properties";
	// public static Properties defaultProps;
	public static Properties props;

	private static Properties defProps;

	private MenuBar menuBar;

	private JLabel status;

	private Map filetypes; // Hopefully obsolete. Used to store applications

	// used to open different file types

	private File autoPropertiesFile;

	private File patternsFile;

	Controller controller;// the one and only controller

	private FreeMindCommon mFreeMindCommon;

	private static FileHandler mFileHandler;

	private JScrollPane mScrollPane = null;

	private JSplitPane mSplitPane;

	private JComponent mContentComponent = null;

	private JTabbedPane mTabbedPane = null;
	private boolean mTabbedPaneSelectionUpdate = true;

	private ImageIcon mWindowIcon;

	private Vector mTabbedPaneMapModules;

	public FreeMind(Properties pDefaultPreferences, Properties pUserPreferences, File pAutoPropertiesFile) {
		super("FreeMind");
		// Focus searcher
//		FocusSearch search = new FocusSearch();
//		search.start();
		System.setSecurityManager(new FreeMindSecurityManager());
		defProps = pDefaultPreferences;
		props = pUserPreferences;
		autoPropertiesFile = pAutoPropertiesFile;
		if (logger == null) {
			logger = getLogger(FreeMind.class.getName());
			StringBuffer info = new StringBuffer();
			info.append("freemind_version = ");
			info.append(VERSION);
			info.append("; freemind_xml_version = ");
			info.append(XML_VERSION);
			try {
				String propsLoc = "version.properties";
				URL versionUrl = this.getClass().getClassLoader().getResource(propsLoc);
				Properties buildNumberPros = new Properties();
				InputStream stream = versionUrl.openStream();
				buildNumberPros.load(stream);
				info.append("\nBuild: "
						+ buildNumberPros.getProperty("build.number")+"\n");
				stream.close();
			} catch(Exception e){
				info.append("Problems reading build number file: " +e);
			}
			info.append("\njava_version = ");
			info.append(System.getProperty("java.version"));
			info.append("; os_name = ");
			info.append(System.getProperty("os.name"));
			info.append("; os_version = ");
			info.append(System.getProperty("os.version"));
			logger.info(info.toString());
		}
		mFreeMindCommon = new FreeMindCommon(this);
		Resources.createInstance(this);
	}

	
	void init(FeedBack feedback) {
		/* This is only for apple but does not harm for the others. */
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        patternsFile = new File (getFreemindDirectory(),getDefaultProperty("patternsfile"));

		feedback.increase("FreeMind.progress.updateLookAndFeel");

		updateLookAndFeel();
		feedback.increase("FreeMind.progress.createController");

		setIconImage(mWindowIcon.getImage());
		// Layout everything
		getContentPane().setLayout(new BorderLayout());

		controller = new Controller(this);
		feedback.increase("FreeMind.progress.settingPreferences");
		// add a listener for the controller, resource bundle:
		Controller.addPropertyChangeListener(new FreemindPropertyListener() {

			public void propertyChanged(String propertyName, String newValue,
					String oldValue) {
				if (propertyName.equals(FreeMindCommon.RESOURCE_LANGUAGE)) {
					// re-read resources:
					mFreeMindCommon.clearLanguageResources();
					getResources();
				}
			}
		});
		// fc, disabled with purpose (see java look and feel styleguides).
		// http://java.sun.com/products/jlf/ed2/book/index.html
		// // add a listener for the controller, look and feel:
		// Controller.addPropertyChangeListener(new FreemindPropertyListener() {
		//
		// public void propertyChanged(String propertyName, String newValue,
		// String oldValue) {
		// if (propertyName.equals(RESOURCE_LOOKANDFEEL)) {
		// updateLookAndFeel();
		// }
		// }
		// });

		controller.optionAntialiasAction
				.changeAntialias(getProperty(FreeMindCommon.RESOURCE_ANTIALIAS));


		feedback.increase("FreeMind.progress.propageteLookAndFeel");
		SwingUtilities.updateComponentTreeUI(this); // Propagate LookAndFeel to

		feedback.increase("FreeMind.progress.buildScreen");
		setScreenBounds();
		
		// JComponents

		feedback.increase("FreeMind.progress.createInitialMode");
		controller.createNewMode(getProperty("initial_mode"));

	}// Constructor

	/**
	 * 
	 */
	private void updateLookAndFeel() {
		// set Look&Feel
		try {
			String lookAndFeel = props.getProperty(RESOURCE_LOOKANDFEEL);
			if (lookAndFeel.equals("windows")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} else if (lookAndFeel.equals("motif")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} else if (lookAndFeel.equals("mac")) {
				// Only available on macOS
				UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
			} else if (lookAndFeel.equals("metal")) {
				UIManager
						.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			} else if (lookAndFeel.equals("gtk")) {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} else if (lookAndFeel.equals("nothing")) {
			} else if (lookAndFeel.indexOf('.') != -1) { // string contains a
				// dot
				UIManager.setLookAndFeel(lookAndFeel);
				// we assume class name
			} else {
				// default.
				logger.info("Default (System) Look & Feel: "
						+ UIManager.getSystemLookAndFeelClassName());
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
		} catch (Exception ex) {
			System.err.println("Unable to set Look & Feel.");
		}
		mFreeMindCommon.loadUIProperties(defProps);
	}

	public boolean isApplet() {
		return false;
	}

	public File getPatternsFile() {
		return patternsFile;
	}

	public Container getViewport() {
		return mScrollPane.getViewport();
	}

	public VersionInformation getFreemindVersion() {
		return VERSION;
	}

	// maintain this methods to keep the last state/size of the window (PN)
	public int getWinHeight() {
		return getHeight();
	}

	public int getWinWidth() {
		return getWidth();
	}
	
	public int getWinX() {
		return getX();
	}

	public int getWinY() {
		return getY();
	}

	public int getWinState() {
		return getExtendedState();
	}

	public URL getResource(String name) {
		return this.getClass().getClassLoader().getResource(name);
	}

	public String getProperty(String key) {
		return props.getProperty(key);
	}

	public int getIntProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	public Properties getProperties() {
		return props;
	}

	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public String  getDefaultProperty(String key) {
		return defProps.getProperty(key);
	}

	public void setDefaultProperty(String key, String value) {
		defProps.setProperty(key, value);
	}

	public String getFreemindDirectory() {
		return System.getProperty("user.home") + File.separator
				+ getProperty("properties_folder");
	}

	public void saveProperties() {
		try {
			OutputStream out = new FileOutputStream(autoPropertiesFile);
			final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, "8859_1");
			outputStreamWriter.write("#FreeMind ");
			outputStreamWriter.write(VERSION.toString());
			outputStreamWriter.write('\n');
			outputStreamWriter.flush();
			// auto.store(out,null);//to save as few props as possible.
			props.store(out, null);
			out.close();
		} catch (Exception ex) {
		}
		getController().getFilterController().saveConditions();
	}

	public MapView getView() {
		return controller.getView();
	}

	public Controller getController() {
		return controller;
	}

	public void setView(MapView view) {
		mScrollPane.setViewportView(view);
	}

	public MenuBar getFreeMindMenuBar() {
		return menuBar;
	}

	public void out(String msg) {
    	// TODO: Automatically remove old messages after a certain time.
		if (status != null) {
			status.setText(msg);
			// logger.info(msg);
		}
	}

	public void err(String msg) {
		if (status != null) {
			status.setText(msg);
		}
		// logger.info(msg);
	}

	/**
	 * Open url in WWW browser. This method hides some differences between
	 * operating systems.
	 */
	public void openDocument(URL url) throws Exception {
		// build string for default browser:
		String correctedUrl = new String(url.toExternalForm());
		if (url.getProtocol().equals("file")) {
			correctedUrl = correctedUrl.replace('\\', '/').replaceAll(" ",
					"%20");
			// ^ This is more of a heuristic than a "logical" code
			// and due to a java bug:
			// http://forum.java.sun.com/thread.jsp?forum=31&thread=363990
		}
		// Originally, this method determined external application, with which
		// the document
		// should be opened. Which application should open which document type
		// was
		// configured in FreeMind properties file. As a result, FreeMind tried
		// to solve the
		// problem (of determining application for a file type), which should
		// better be
		// solved somewhere else. Indeed, on Windows, this problem is perfectly
		// solved by
		// Explorer. On KDE, this problem is solved by Konqueror default
		// browser. In
		// general, most WWW browsers have to solve this problem.

		// As a result, the only thing we do here, is to open URL in WWW
		// browser.

		String osName = System.getProperty("os.name");
		if (osName.substring(0, 3).equals("Win")) {
			String propertyString = new String(
					"default_browser_command_windows");
			if (osName.indexOf("9") != -1 || osName.indexOf("Me") != -1) {
				propertyString += "_9x";
			} else {
				propertyString += "_nt";
			}

			String browser_command = new String();
			String command = new String();
			// Here we introduce " around the parameter of explorer
			// command. This is not because of possible spaces in this
			// parameter - it is because of "=" character, which causes
			// problems. My understanding of MSDOS is not so good, but at
			// least I can say, that "=" is used in general for the purpose
			// of variable assignment.
			// String[] call = { browser_command, "\""+url.toString()+"\"" };
			try {
				// This is working fine on Windows 2000 and NT as well
				// Below is a piece of code showing how to run executables
				// directly
				// without asking. However, we don't want to do that. Explorer
				// will run
				// executable, but ask before it actually runs it.
				//
				// Imagine you download a package of maps containing also nasty
				// executable. Let's say there is a map "index.mm". This map
				// contains a
				// link to that nasty executable, but the name of the link
				// appearing to the
				// user does not indicate at all that clicking the link leads to
				// execution
				// of a programm. This executable is located on your local
				// computer, so
				// asking before executing remote executable does not solve the
				// problem. You click the link and there you are running evil
				// executable.

				// build string for default browser:
				// ask for property about browser: fc, 26.11.2003.
				Object[] messageArguments = { url.toString() };
				MessageFormat formatter = new MessageFormat(
						getProperty(propertyString));
				browser_command = formatter.format(messageArguments);

				if (url.getProtocol().equals("file")) {
//					command = "rundll32 url.dll,FileProtocolHandler "+ Tools.urlGetFile(url);
					// bug fix by Dan:
					command = "rundll32 url.dll,FileProtocolHandler "+ url.toString();
					// see http://rsb.info.nih.gov/ij/developer/source/ij/plugin/BrowserLauncher.java.html
					if (System.getProperty("os.name").startsWith("Windows 2000"))
						command = "rundll32 shell32.dll,ShellExec_RunDLL " + url.toString();
				} else if (url.toString().startsWith("mailto:")) {
					command = "rundll32 url.dll,FileProtocolHandler "
							+ url.toString();
				} else {
					command = browser_command;
				}
				// logger.info("Starting browser with "+command);
				Runtime.getRuntime().exec(command);
			} catch (IOException x) {
				controller
						.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""
								+ command
								+ "\".\n\nYou may look at the user or default property called '"
								+ propertyString + "'.");
				System.err.println("Caught: " + x);
			}
		} else if (osName.startsWith("Mac OS")) {

			// logger.info("Opening URL "+urlString);
			String browser_command = new String();
			try {
				// ask for property about browser: fc, 26.11.2003.
				Object[] messageArguments = { correctedUrl, url.toString() };
				MessageFormat formatter = new MessageFormat(
						getProperty("default_browser_command_mac"));
				browser_command = formatter.format(messageArguments);
				Runtime.getRuntime().exec(browser_command);
			} catch (IOException ex2) {
				controller
						.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""
								+ browser_command
								+ "\".\n\nYou may look at the user or default property called 'default_browser_command_mac'.");
				System.err.println("Caught: " + ex2);
			}
		} else {
			// There is no '"' character around url.toString (compare to Windows
			// code
			// above). Putting '"' around does not work on Linux - instead, the
			// '"'
			// becomes part of URL, which is malformed, as a result.

			String browser_command = new String();
			try {
				// ask for property about browser: fc, 26.11.2003.
				Object[] messageArguments = { correctedUrl, url.toString() };
				MessageFormat formatter = new MessageFormat(
						getProperty("default_browser_command_other_os"));
				browser_command = formatter.format(messageArguments);
				Runtime.getRuntime().exec(browser_command);
			} catch (IOException ex2) {
				controller
						.errorMessage("Could not invoke browser.\n\nFreemind excecuted the following statement on a command line:\n\""
								+ browser_command
								+ "\".\n\nYou may look at the user or default property called 'default_browser_command_other_os'.");
				System.err.println("Caught: " + ex2);
			}
		}
	}

	private String transpose(String input, char findChar, String replaceString) {
		String res = new String();
		for (int i = 0; i < input.length(); ++i) {
			char d = input.charAt(i);
			if (d == findChar)
				res += replaceString;
			else
				res += d;
		}
		return res;
	}

	public void setWaitingCursor(boolean waiting) {
		if (waiting) {
			getRootPane().getGlassPane().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			getRootPane().getGlassPane().setVisible(true);
		} else {
			getRootPane().getGlassPane().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getRootPane().getGlassPane().setVisible(false);
		}
	}

	private String getProgramForFile(String type) {
		if (filetypes == null) {
			filetypes = new HashMap();
			String raw = getProperty("filetypes");
			if (raw == null || raw.equals("")) {
				return "";
			}
			StringTokenizer tokens = new StringTokenizer(raw, ",");
			while (tokens.hasMoreTokens()) {
				StringTokenizer pair = new StringTokenizer(tokens.nextToken(),
						":");
				String key = pair.nextToken().trim().toLowerCase();
				String value = pair.nextToken().trim();
				filetypes.put(key, value);
			}
		}
		return (String) filetypes.get(type.trim().toLowerCase());
	}

	/** Returns the ResourceBundle with the current language */
	public ResourceBundle getResources() {
		return mFreeMindCommon.getResources();
	}

	public String getResourceString(String resource) {
		return mFreeMindCommon.getResourceString(resource);
	}

	public String getResourceString(String key, String resource) {
		return mFreeMindCommon.getResourceString(key, resource);
	}

	public Logger getLogger(String forClass) {
		Logger loggerForClass = java.util.logging.Logger.getLogger(forClass);
		if (mFileHandler == null) {
			// initialize handlers using an old System.err:
			final Logger parentLogger = loggerForClass.getParent();
			final Handler[] handlers = parentLogger.getHandlers();
			for(int i = 0; i < handlers.length; i++){
				final Handler handler = handlers[i];
				if(handler instanceof ConsoleHandler){
					parentLogger.removeHandler(handler);
				}
			}

			try {
				mFileHandler = new FileHandler(getFreemindDirectory()
						+ File.separator + "log", 1400000, 5, false);
				mFileHandler.setFormatter(new StdFormatter());
				mFileHandler.setLevel(Level.INFO);
				parentLogger.addHandler(mFileHandler);
				
				final ConsoleHandler stdConsoleHandler = new ConsoleHandler();
				stdConsoleHandler.setFormatter(new StdFormatter());
				stdConsoleHandler.setLevel(Level.WARNING);
				parentLogger.addHandler(stdConsoleHandler);

				LoggingOutputStream los;
				Logger logger = Logger.getLogger(StdFormatter.STDOUT.getName());
				los = new LoggingOutputStream(logger, StdFormatter.STDOUT);
				System.setOut(new PrintStream(los, true));

				logger = Logger.getLogger(StdFormatter.STDERR.getName());
				los= new LoggingOutputStream(logger, StdFormatter.STDERR);
				System.setErr(new PrintStream(los, true));

			} catch (Exception e) {
				System.err.println("Error creating logging File Handler");
				e.printStackTrace();
				// to avoid infinite recursion.
				// freemind.main.Resources.getInstance().logExecption(e);
			}
		}
		return loggerForClass;
	}

	public static void main(final String[] args, Properties pDefaultPreferences, Properties pUserPreferences, File pAutoPropertiesFile) {
		final FreeMind frame = new FreeMind(pDefaultPreferences, pUserPreferences, pAutoPropertiesFile);
		IFreeMindSplash splash = null;
		final FeedBack feedBack;
		// change here, if you don't like the splash
		if (true) {
			splash = new FreeMindSplashModern(frame);
			splash.setVisible(true);
			feedBack = splash.getFeedBack();
			frame.mWindowIcon = splash.getWindowIcon();
		} else {
			feedBack = new FeedBack() {
				int value = 0;

				public int getActualValue() {
					return value;
				}

				public void increase(String messageId) {
					progress(getActualValue() + 1, messageId);
				}

				public void progress(int act, String messageId) {
					frame.logger.info("Beginnig task:" + messageId);
				}

				public void setMaximumValue(int max) {
				}
			};
			frame.mWindowIcon = new ImageIcon(frame
					.getResource("images/FreeMindWindowIcon.png"));
		}
		feedBack.setMaximumValue(9);
		frame.init(feedBack);

		feedBack.increase("FreeMind.progress.startCreateController");
		final ModeController ctrl = frame.createModeController(args);
		
		feedBack.increase("FreeMind.progress.loadMaps");
		// This could be improved.
		frame.loadMaps(args, ctrl);
		
		Tools.waitForEventQueue();
		feedBack.increase("FreeMind.progress.endStartup");
		// focus fix after startup.
		frame.addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent e) {
			}

			public void windowGainedFocus(WindowEvent e) {
				NodeView selectedView = ctrl.getSelectedView();
				if (selectedView!=null) {
					selectedView.requestFocus();
					MindMapNode selected = ctrl.getSelected();
					if (selected!=null) {
						ctrl.centerNode(selected);
					}
				}
				frame.removeWindowFocusListener(this);
			}
		});
		frame.setVisible(true);
		if (splash != null) {
			splash.setVisible(false);
		}
	}


	private void setScreenBounds() {
		// Create the MenuBar
		menuBar = new MenuBar(controller);
		setJMenuBar(menuBar);

		// Create the scroll pane
		mScrollPane =  new MapView.ScrollPane();
		if (Resources.getInstance().getBoolProperty("no_scrollbar")) {
			mScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			mScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		} else {
			mScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			mScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		}
		status = new JLabel("!");
		status.setPreferredSize(status.getPreferredSize());
		status.setText("");
		mContentComponent = mScrollPane;

		boolean shouldUseTabbedPane = Resources.getInstance().getBoolProperty(RESOURCES_USE_TABBED_PANE);


		if (shouldUseTabbedPane) {
			// tabbed panes eat control up. This is corrected here.
			InputMap map;
			map = (InputMap) UIManager.get("TabbedPane.ancestorInputMap");
			KeyStroke keyStrokeCtrlUp = KeyStroke.getKeyStroke( KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK);
			map.remove(keyStrokeCtrlUp);
			mTabbedPane = new JTabbedPane();
			mTabbedPane.setFocusable(false);
			mTabbedPaneMapModules = new Vector();
			mTabbedPane.addChangeListener(new ChangeListener() {

				public synchronized void stateChanged(ChangeEvent pE) {
					tabSelectionChanged();
				}


			});
			controller.getMapModuleManager().addListener(
					new MapModuleChangeObserver() {

						public void afterMapModuleChange(
								MapModule pOldMapModule, Mode pOldMode,
								MapModule pNewMapModule, Mode pNewMode) {
							int selectedIndex = mTabbedPane.getSelectedIndex();
							if (pNewMapModule == null) {
								return;
							}
							// search, if already present:
							for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
								if (mTabbedPaneMapModules.get(i) ==
										pNewMapModule) {
									if (selectedIndex != i) {
										mTabbedPane.setSelectedIndex(i);
									}
									return;
								}
							}
							// create new tab:
							mTabbedPaneMapModules.add(pNewMapModule);
							mTabbedPane.addTab(pNewMapModule.toString(),
									new JPanel());
							mTabbedPane.setSelectedIndex(mTabbedPane
									.getTabCount() - 1);
						}

						public void beforeMapModuleChange(
								MapModule pOldMapModule, Mode pOldMode,
								MapModule pNewMapModule, Mode pNewMode) {
						}

						public boolean isMapModuleChangeAllowed(
								MapModule pOldMapModule, Mode pOldMode,
								MapModule pNewMapModule, Mode pNewMode) {
							return true;
						}

						public void numberOfOpenMapInformation(int pNumber) {
						}

						public void afterMapClose(MapModule pOldMapModule,
								Mode pOldMode) {
							for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
								if (mTabbedPaneMapModules.get(i) ==	pOldMapModule) {
									logger.fine("Remove tab:" + i + " with title:" + mTabbedPane.getTitleAt(i));
									mTabbedPaneSelectionUpdate = false;
									mTabbedPane.removeTabAt(i);
									mTabbedPaneMapModules.remove(i);
									mTabbedPaneSelectionUpdate = true;
									tabSelectionChanged();
									return;
								}
							}							
						}
					});
			controller.registerMapTitleChangeListener(new MapModuleManager.MapTitleChangeListener(){

				public void setMapTitle(String pNewMapTitle,
						MapModule pMapModule, MindMap pModel) {
					for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
						if (mTabbedPaneMapModules.get(i) ==	pMapModule) {
							mTabbedPane.setTitleAt(i, pNewMapTitle + ((pModel.isSaved())?"":"*"));
						}
					}
				}
			});
			getContentPane().add(mTabbedPane, BorderLayout.CENTER);
		} else {
			// don't use tabbed panes.
			getContentPane().add(mContentComponent, BorderLayout.CENTER);
		}
		getContentPane().add(status, BorderLayout.SOUTH);

		// Disable the default close button, instead use windowListener
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.quit
						.actionPerformed(new ActionEvent(this, 0, "quit"));
			}

			/* fc, 14.3.2008: Completely removed, as it
			 * damaged the focus if for example the note
			 * window was active.
			 */ 
//			public void windowActivated(WindowEvent e) {
//				// This doesn't work the first time, it's called too early to
//				// get Focus
//				logger.info("windowActivated");
//				if ((getView() != null) && (getView().getSelected() != null)) {
//					getView().getSelected().requestFocus();
//				}
//			}
		});

		if (Tools.safeEquals(getProperty("toolbarVisible"), "false")) {
			controller.setToolbarVisible(false);
		}

		if (Tools.safeEquals(getProperty("leftToolbarVisible"), "false")) {
			controller.setLeftToolbarVisible(false);
		}

		// first define the final layout of the screen:
		setFocusTraversalKeysEnabled(false);
		pack();
		// and now, determine size, position and state.
		// set the default size (PN)
		int win_width = getIntProperty("appwindow_width", 0);
		int win_height =getIntProperty("appwindow_height", 0);
		int win_x  = getIntProperty("appwindow_x", 0);
		int win_y  = getIntProperty("appwindow_y", 0);
		win_width = (win_width > 0) ? win_width : 640;
		win_height = (win_height > 0) ? win_height : 440;
		final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		final Insets screenInsets = defaultToolkit.getScreenInsets(getGraphicsConfiguration());
		Dimension screenSize = defaultToolkit.getScreenSize();
		final int screenWidth = screenSize.width-screenInsets.left-screenInsets.right;
		win_width = Math.min(win_width, screenWidth);
		final int screenHeight = screenSize.height-screenInsets.top-screenInsets.bottom;
		win_height = Math.min(win_height, screenHeight);
		win_x = Math.max(screenInsets.left, win_x);
		win_x = Math.min(screenWidth+screenInsets.left-win_width, win_x);
		win_y = Math.max(screenInsets.top, win_y);
		win_y = Math.min(screenWidth+screenInsets.top-win_height, win_y);
		setBounds(win_x, win_y, win_width, win_height);
		// set the default state (normal/maximized) (PN)
		// (note: this must be done later when partucular
		// initalizations of the windows are ready,
		// perhaps after setVisible is it enough... :-?
		int win_state = Integer.parseInt(FreeMind.props
				.getProperty("appwindow_state", "0"));
		win_state = ((win_state & ICONIFIED) != 0) ? NORMAL
				: win_state;
		setExtendedState(win_state);
	}
	
	 private ModeController createModeController(final String[] args) {
		ModeController ctrl = controller
		.getModeController();
		// try to load mac module:
		try {
			Class macClass = Class
			.forName("accessories.plugins.MacChanges");
			// lazy programming. the mac class has exactly one
			// constructor
			// with a modeController.
			macClass.getConstructors()[0]
			                           .newInstance(new Object[] { this });
		} catch (Exception e1) {
			// freemind.main.Resources.getInstance().logExecption(e1);
		}
		return ctrl;
	}

	private void loadMaps(final String[] args, ModeController pModeController) {
		boolean fileLoaded = false;
		for (int i = 0; i < args.length; i++) {
			// JOptionPane.showMessageDialog(null,i+":"+args[i]);
			String fileArgument = args[i];
			if (fileArgument
					.toLowerCase()
					.endsWith(
							freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION)) {

				if (!Tools.isAbsolutePath(fileArgument)) {
					fileArgument = System
					.getProperty("user.dir")
					+ System
					.getProperty("file.separator")
					+ fileArgument;
				}
				// fin = ;
				try {
					pModeController.load(new File(fileArgument));
					fileLoaded = true;
					// logger.info("Attempting to load: " +
					// args[i]);
				} catch (Exception ex) {
					System.err.println("File " + fileArgument
							+ " not found error");
					// System.exit(1);
				}
			}
		}
		if (!fileLoaded) {
			String restoreable = getProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED);
			if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAP))
					&& restoreable != null
					&& restoreable.length() > 0) {
				try {
					controller.getLastOpenedList().open(
							restoreable);
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance()
					.logException(e);
					out("An error occured on opening the file: "
							+ restoreable + ".");
				}
			}
		}
		if (!fileLoaded
				&& Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_NEW_MAP))) {
			/* nothing loaded so far. Perhaps, we should display a new map...
			 * According to 
			 * Summary: On first start FreeMind should show new map to newbies
			 * https://sourceforge.net/tracker/?func=detail&atid=107118&aid=1752516&group_id=7118
			 */
			pModeController.newMap();
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.main.FreeMindMain#getJFrame()
	 */
	public JFrame getJFrame() {
		return this;
	}

	public ClassLoader getFreeMindClassLoader() {
		return mFreeMindCommon.getFreeMindClassLoader();
	}

	public String getFreemindBaseDir() {
		return mFreeMindCommon.getFreemindBaseDir();
	}

	public String getAdjustableProperty(String label) {
		return mFreeMindCommon.getAdjustableProperty(label);
	}


	private void tabSelectionChanged() {
		if(!mTabbedPaneSelectionUpdate)
			return;
		int selectedIndex = mTabbedPane.getSelectedIndex();
		// display nothing on the other tabs:
		for(int j = 0 ; j < mTabbedPane.getTabCount(); j++) {
			if(j != selectedIndex)
				mTabbedPane.setComponentAt(j, new JPanel());
		}
		if (selectedIndex < 0) {
			// nothing selected. probably, the last map was closed
			return;
		}
		MapModule module = (MapModule) mTabbedPaneMapModules.get(selectedIndex);
		logger.fine("Selected index of tab is now: " + selectedIndex + " with title:"+module.toString());
		if (module != controller.getMapModule()) {
			// we have to change the active map actively:
			controller.getMapModuleManager().changeToMapModule(module.toString());
		}
		// mScrollPane could be set invisible by JTabbedPane
		mScrollPane.setVisible(true);
		mTabbedPane.setComponentAt(selectedIndex, mContentComponent);
	}



	public JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent) {
		if(mSplitPane != null) {
			// already present:
			return mSplitPane;
		}
		removeContentComponent();
		mSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mScrollPane,
				pMindMapComponent);
		mSplitPane.setContinuousLayout(true);
		mSplitPane.setOneTouchExpandable(false);
		/* This means that the mind map area gets all the space that
		 * results from resizing the window.*/
		mSplitPane.setResizeWeight(1.0d);
		// split panes eat F8 and F6. This is corrected here.
		InputMap map = (InputMap) UIManager.get("SplitPane.ancestorInputMap");
		KeyStroke keyStrokeF6 = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
		KeyStroke keyStrokeF8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);
		map.remove(keyStrokeF6);
		map.remove(keyStrokeF8);
		mContentComponent = mSplitPane;
		setContentComponent();
		// set divider position:
		int splitPanePosition = getIntProperty(SPLIT_PANE_POSITION, -1);
		int lastSplitPanePosition = getIntProperty(
				SPLIT_PANE_LAST_POSITION, -1);
		if (splitPanePosition != -1 && lastSplitPanePosition != -1) {
			mSplitPane.setDividerLocation(splitPanePosition);
			mSplitPane.setLastDividerLocation(lastSplitPanePosition);
//			try {
//				throw new IllegalArgumentException("");
//			} catch (Exception e) {
//				freemind.main.Resources.getInstance().logException(e);
//				// TODO: handle exception
//			}
		}
		return mSplitPane;
	}



	public void removeSplitPane() {
		if (mSplitPane != null) {
			setProperty(SPLIT_PANE_POSITION, ""
					+ mSplitPane.getDividerLocation());
			setProperty(SPLIT_PANE_LAST_POSITION, ""
					+ mSplitPane.getLastDividerLocation());
			removeContentComponent();
			mContentComponent = mScrollPane;
			setContentComponent();
			mSplitPane = null;
		}
	}

	private void removeContentComponent(){
		if(mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex()>= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(),
						new JPanel());
			}
		} else {
			getContentPane().remove(mContentComponent);
			getRootPane().revalidate();
		}
		
	}
	
	private void setContentComponent() {
		if(mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex()>= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(), mContentComponent);
			}
		} else {
			getContentPane().add(mContentComponent, BorderLayout.CENTER);			
			getRootPane().revalidate();
		}
	}
	
	class FocusSearch extends Thread {
		Component lastFocussedC = null;
		FocusListener listener = new FocusListener() {

			public void focusGained(FocusEvent pE) {
				Tools.printStackTrace();
			}

			public void focusLost(FocusEvent pE) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		public void run() {
			super.run();
			while (true) {
				searchFocus(FreeMind.this);
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

		private void searchFocus(Component pComponent) {
//			pComponent.removeFocusListener(listener);
//			pComponent.addFocusListener(listener);
			if(pComponent.hasFocus() && pComponent != lastFocussedC) {
				logger.info("Fokus has " + pComponent);
				lastFocussedC = pComponent;
			}
			if (pComponent instanceof Container) {
				Container container = (Container) pComponent;
				for (int i = 0; i < container.getComponents().length; i++) {
					Component child = container.getComponents()[i];
					searchFocus(child);
				}
			}
		}
	}
	
}
