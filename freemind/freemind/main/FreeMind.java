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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import java.util.logging.SimpleFormatter;

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

import com.inet.jortho.SpellChecker;

import freemind.controller.Controller;
import freemind.controller.LastStateStorageManagement;
import freemind.controller.MenuBar;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;
import freemind.main.FreeMindStarter.ProxyAuthenticator;
import freemind.modes.ModeController;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

public class FreeMind extends JFrame implements FreeMindMain {

	public static final String J_SPLIT_PANE_SPLIT_TYPE = "JSplitPane.SPLIT_TYPE";

	public static final String VERTICAL_SPLIT_BELOW = "vertical_split_below";
	
	public static final String HORIZONTAL_SPLIT_RIGHT = "horizontal_split_right";
	
	public static final String LOG_FILE_NAME = "log";

	private static final String PORT_FILE = "portFile";

	private static final String FREE_MIND_PROGRESS_LOAD_MAPS = "FreeMind.progress.loadMaps";

	private static final String FREE_MIND_PROGRESS_LOAD_MAPS_NAME = "FreeMind.progress.loadNamedMaps";

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

	public static final String RESOURCES_RELOAD_FILES_WITHOUT_QUESTION = "reload_files_without_question";

	private Logger logger = null;

	protected static final VersionInformation VERSION = new VersionInformation("1.0.1");

	public static final String XML_VERSION = "1.0.1";

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

	public static final String RESOURCES_DON_T_SHOW_CLONE_ICONS = "resources_don_t_show_clone_icons";

	public static final String RESOURCES_DON_T_OPEN_PORT = "resources_don_t_open_port";

	public static final String KEYSTROKE_MOVE_MAP_LEFT = "keystroke_MoveMapLeft";

	public static final String KEYSTROKE_MOVE_MAP_RIGHT = "keystroke_MoveMapRight";

	public static final String KEYSTROKE_PREVIOUS_MAP = "keystroke_previousMap";

	public static final String KEYSTROKE_NEXT_MAP = "keystroke_nextMap";

	public static final String RESOURCES_SEARCH_IN_NOTES_TOO = "resources_search_in_notes_too";

	public static final String RESOURCES_DON_T_SHOW_NOTE_TOOLTIPS = "resources_don_t_show_note_tooltips";

	public static final String RESOURCES_SEARCH_FOR_NODE_TEXT_WITHOUT_QUESTION = "resources_search_for_node_text_without_question";
	
	public static final String RESOURCES_COMPLETE_CLONING = "complete_cloning";

	public static final String RESOURCES_CLONE_TYPE_COMPLETE_CLONE = "COMPLETE_CLONE";

	public static final String TOOLTIP_DISPLAY_TIME = "tooltip_display_time";

	public static final String PROXY_PORT = "proxy.port";

	public static final String PROXY_HOST = "proxy.host";

	public static final String PROXY_PASSWORD = "proxy.password";

	public static final String PROXY_USER = "proxy.user";

	public static final String PROXY_IS_AUTHENTICATED = "proxy.is_authenticated";

	public static final String PROXY_USE_SETTINGS = "proxy.use_settings";

	public static final String RESOURCES_DISPLAY_FOLDING_BUTTONS = "resources_display_folding_buttons";


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
	private static boolean mFileHandlerError = false;

	private JScrollPane mScrollPane = null;

	private JSplitPane mSplitPane;

	private JComponent mContentComponent = null;

	private JTabbedPane mTabbedPane = null;

	private ImageIcon mWindowIcon;

	private boolean mStartupDone = false;

	private List mStartupDoneListeners = new Vector();

	private EditServer mEditServer = null;

	private Vector mLoggerList = new Vector();

	private static LogFileLogHandler sLogFileHandler;

	public FreeMind(Properties pDefaultPreferences,
			Properties pUserPreferences, File pAutoPropertiesFile) {
		super("FreeMind");
		// Focus searcher
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
				URL versionUrl = this.getClass().getClassLoader()
						.getResource(propsLoc);
				Properties buildNumberPros = new Properties();
				InputStream stream = versionUrl.openStream();
				buildNumberPros.load(stream);
				info.append("\nBuild: "
						+ buildNumberPros.getProperty("build.number") + "\n");
				stream.close();
			} catch (Exception e) {
				info.append("Problems reading build number file: " + e);
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
		patternsFile = new File(getFreemindDirectory(),
				getDefaultProperty("patternsfile"));

		feedback.increase("FreeMind.progress.updateLookAndFeel", null);

		updateLookAndFeel();
		feedback.increase("FreeMind.progress.createController", null);

		setIconImage(mWindowIcon.getImage());
		// Layout everything
		getContentPane().setLayout(new BorderLayout());

		controller = new Controller(this);
		controller.init();
		feedback.increase("FreeMind.progress.settingPreferences", null);
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

		setupSpellChecking();
		setupProxy();
		feedback.increase("FreeMind.progress.propageteLookAndFeel", null);
		SwingUtilities.updateComponentTreeUI(this); // Propagate LookAndFeel to

		feedback.increase("FreeMind.progress.buildScreen", null);
		setScreenBounds();

		// JComponents

		feedback.increase("FreeMind.progress.createInitialMode", null);
		controller.createNewMode(getProperty("initial_mode"));
//		EventQueue eventQueue = Toolkit.getDefaultToolkit()
//				.getSystemEventQueue();
//		eventQueue.push(new MyEventQueue());
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

	public String getDefaultProperty(String key) {
		return defProps.getProperty(key);
	}

	public void setDefaultProperty(String key, String value) {
		defProps.setProperty(key, value);
	}

	public String getFreemindDirectory() {
		return System.getProperty("user.home") + File.separator
				+ getProperty("properties_folder");
	}

	public void saveProperties(boolean pIsShutdown) {
		try {
			OutputStream out = new FileOutputStream(autoPropertiesFile);
			final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					out, "8859_1");
			outputStreamWriter.write("#FreeMind ");
			outputStreamWriter.write(VERSION.toString());
			outputStreamWriter.write('\n');
			outputStreamWriter.flush();
			//to save as few props as possible.
			Properties toBeStored = Tools.copyChangedProperties(props, defProps);
			toBeStored.store(out, null);
			out.close();
		} catch (Exception ex) {
			Resources.getInstance().logException(ex);
		}
		getController().getFilterController().saveConditions();
		if (pIsShutdown && mEditServer != null) {
			mEditServer.stopServer();
		}
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
		String urlString = url.toString();

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
				Object[] messageArguments = { urlString };
				MessageFormat formatter = new MessageFormat(
						getProperty(propertyString));
				browser_command = formatter.format(messageArguments);

				if (url.getProtocol().equals("file")) {
					final File file = Tools.urlToFile(url);
					if (!Tools.isBelowJava6()) {
						Class desktopClass = Class.forName("java.awt.Desktop");
						Method getDesktopMethod = desktopClass.getMethod(
								"getDesktop", new Class[] {});
						Object desktopObject = getDesktopMethod.invoke(null,
								new Object[] {});
						Method openMethod = desktopObject.getClass().getMethod(
								"open", new Class[] { File.class });
						logger.info("Opening file " + file);
						openMethod.invoke(desktopObject, new Object[] { file });
						return;
					}
					// command = "rundll32 url.dll,FileProtocolHandler "+
					// Tools.urlGetFile(url);
					// bug fix by Dan:
					command = "cmd /C rundll32 url.dll,FileProtocolHandler "
							+ urlString;
					// see
					// http://rsb.info.nih.gov/ij/developer/source/ij/plugin/BrowserLauncher.java.html
					if (System.getProperty("os.name")
							.startsWith("Windows 2000"))
						command = "cmd /C rundll32 shell32.dll,ShellExec_RunDLL "
								+ urlString;
				} else if (urlString.startsWith("mailto:")) {
					command = "cmd /C rundll32 url.dll,FileProtocolHandler "
							+ urlString;
				} else {
					command = browser_command;
				}
				logger.info("Starting browser with " + command);
				// Runtime.getRuntime().exec(command);
				execWindows(command);
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
				Object[] messageArguments = { correctedUrl, urlString };
				if ("file".equals(url.getProtocol())) {
					// Bug in the apple's open function. For files, a pure
					// filename must be given.
					final File file = Tools.urlToFile(url);
					String[] command = {
							getProperty("default_browser_command_mac_open"),
							"file:" + file.getAbsolutePath() };
					logger.info("Starting command: "
							+ Arrays.deepToString(command));
					Runtime.getRuntime().exec(command, null, null);
				} else {
					MessageFormat formatter = new MessageFormat(
							getProperty("default_browser_command_mac"));
					browser_command = formatter.format(messageArguments);
					logger.info("Starting command: " + browser_command);
					Runtime.getRuntime().exec(browser_command);
				}
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
				Object[] messageArguments = { correctedUrl, urlString };
				MessageFormat formatter = new MessageFormat(
						getProperty("default_browser_command_other_os"));
				browser_command = formatter.format(messageArguments);
				logger.info("Starting command: " + browser_command);
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

	/**
	 * @param cmd
	 *            precondition: the command can be split by spaces and the last
	 *            argument is the only one that contains unicode chars.
	 *            Moreover, we are under Windows. THIS METHOD DOESN'T SEEM TO
	 *            WORK for UNICODE ARGUMENTS.
	 * @throws IOException
	 */
	private void execWindows(String pCommand) throws IOException {
		// taken and adapted from
		// http://stackoverflow.com/questions/1876507/java-runtime-exec-on-windows-fails-with-unicode-in-arguments
		StringTokenizer st = new StringTokenizer(pCommand, " ");
		String[] cmd = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			cmd[i++] = st.nextToken();
		}
		Map newEnv = new HashMap();
		newEnv.putAll(System.getenv());
		// exchange last argument by environment
		String envName = "JENV_1";
		newEnv.put(envName, cmd[cmd.length - 1]);
		cmd[cmd.length - 1] = "%" + envName + "%";

		logger.info("Starting command array "
				+ Arrays.toString(cmd)
				+ ", and env for "
				+ envName
				+ " = "
				+ HtmlTools.unicodeToHTMLUnicodeEntity(
						(String) newEnv.get(envName), true));
		ProcessBuilder pb = new ProcessBuilder(cmd);
		Map env = pb.environment();
		env.putAll(newEnv);
		final Process p = pb.start();
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

	public String getResourceString(String key, String pDefault) {
		return mFreeMindCommon.getResourceString(key, pDefault);
	}

	public Logger getLogger(String forClass) {
		Logger loggerForClass = java.util.logging.Logger.getLogger(forClass);
		mLoggerList.add(loggerForClass);
		if (mFileHandler == null && !mFileHandlerError) {
			// initialize handlers using an old System.err:
			final Logger parentLogger = loggerForClass.getParent();
			final Handler[] handlers = parentLogger.getHandlers();
			for (int i = 0; i < handlers.length; i++) {
				final Handler handler = handlers[i];
				if (handler instanceof ConsoleHandler) {
					parentLogger.removeHandler(handler);
				}
			}

			try {
				mFileHandler = new FileHandler(getFreemindDirectory()
						+ File.separator + LOG_FILE_NAME, 1400000, 5, false);
				mFileHandler.setFormatter(new StdFormatter());
				mFileHandler.setLevel(Level.INFO);
				parentLogger.addHandler(mFileHandler);

				final ConsoleHandler stdConsoleHandler = new ConsoleHandler();
				stdConsoleHandler.setFormatter(new StdFormatter());
				stdConsoleHandler.setLevel(Level.WARNING);
				parentLogger.addHandler(stdConsoleHandler);

				sLogFileHandler = new LogFileLogHandler();
				sLogFileHandler.setFormatter(new SimpleFormatter());
				sLogFileHandler.setLevel(Level.INFO);

				LoggingOutputStream los;
				Logger logger = Logger.getLogger(StdFormatter.STDOUT.getName());
				los = new LoggingOutputStream(logger, StdFormatter.STDOUT);
				System.setOut(new PrintStream(los, true));

				logger = Logger.getLogger(StdFormatter.STDERR.getName());
				los = new LoggingOutputStream(logger, StdFormatter.STDERR);
				System.setErr(new PrintStream(los, true));

			} catch (Exception e) {
				System.err.println("Error creating logging File Handler");
				e.printStackTrace();
				mFileHandlerError = true;
				// to avoid infinite recursion.
				// freemind.main.Resources.getInstance().logExecption(e);
			}
	        if (false) {
				// Obtain a reference to the logger
				Logger focusLog = Logger.getLogger("java.awt.focus.Component");
				// The logger should log all messages
				focusLog.setLevel(Level.ALL);
				// Create a new handler
				ConsoleHandler handler = new ConsoleHandler();
				// The handler must handle all messages
				handler.setLevel(Level.ALL);
				// Add the handler to the logger
				focusLog.addHandler(handler);
			}
		}
		if (sLogFileHandler != null) {
			loggerForClass.addHandler(sLogFileHandler);
		}
		return loggerForClass;
	}

	public static void main(final String[] args,
			Properties pDefaultPreferences, Properties pUserPreferences,
			File pAutoPropertiesFile) {
		final FreeMind frame = new FreeMind(pDefaultPreferences,
				pUserPreferences, pAutoPropertiesFile);
		IFreeMindSplash splash = null;
		frame.checkForAnotherInstance(args);
		frame.initServer();
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

				public void increase(String messageId,
						Object[] pMessageParameters) {
					progress(getActualValue() + 1, messageId,
							pMessageParameters);
				}

				public void progress(int act, String messageId,
						Object[] pMessageParameters) {
					frame.logger.info("Beginnig task:" + messageId);
				}

				public void setMaximumValue(int max) {
				}
			};
			frame.mWindowIcon = new ImageIcon(
					frame.getResource("images/FreeMindWindowIcon.png"));
		}
		feedBack.setMaximumValue(10 + frame.getMaximumNumberOfMapsToLoad(args));
		frame.init(feedBack);

		feedBack.increase("FreeMind.progress.startCreateController", null);
		final ModeController ctrl = frame.createModeController(args);

		feedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS, null);

		frame.loadMaps(args, ctrl, feedBack);

		Tools.waitForEventQueue();
		feedBack.increase("FreeMind.progress.endStartup", null);
		// focus fix after startup.
		frame.addWindowFocusListener(new WindowFocusListener() {

			public void windowLostFocus(WindowEvent e) {
			}

			public void windowGainedFocus(WindowEvent e) {
				frame.getController().obtainFocusForSelected();
				frame.removeWindowFocusListener(this);
			}
		});
		frame.setVisible(true);
		if (splash != null) {
			splash.setVisible(false);
		}
		frame.fireStartupDone();
	}

	private void setupSpellChecking() {
		boolean checkSpelling =
//			Resources.getInstance().getBoolProperty(FreeMindCommon.CHECK_SPELLING);
			Tools.safeEquals("true", props.getProperty(FreeMindCommon.CHECK_SPELLING));
		if (checkSpelling) {
			try {
				// TODO filter languages in dictionaries.properties like this:
//				String[] languages = "en,de,es,fr,it,nl,pl,ru,ar".split(",");
//				for (int i = 0; i < languages.length; i++) {
//					System.out.println(new File("dictionary_" + languages[i] + ".ortho").exists());
//				}
				String decodedPath = Tools.getFreeMindBasePath();
				URL url = null;
				if (new File (decodedPath).exists()) {
					url = new URL("file", null, decodedPath);
				}
				SpellChecker.registerDictionaries(url, Locale.getDefault().getLanguage());
			} catch (MalformedURLException e) {
				freemind.main.Resources.getInstance().logException(e);
			} catch (UnsupportedEncodingException e) {
				freemind.main.Resources.getInstance().logException(e);
				
			}
		}
	}

	private void setupProxy() {
		// proxy settings
		if("true".equals(props.getProperty(PROXY_USE_SETTINGS))) {
			if ("true".equals(props.getProperty(PROXY_IS_AUTHENTICATED))) {
				Authenticator.setDefault(new ProxyAuthenticator(props
						.getProperty(PROXY_USER), Tools.decompress(props
						.getProperty(PROXY_PASSWORD))));
			}
			System.setProperty("http.proxyHost", props.getProperty(PROXY_HOST));
			System.setProperty("http.proxyPort", props.getProperty(PROXY_PORT));
		}
	}


	private class MyEventQueue extends EventQueue {
        public void postEvent(AWTEvent theEvent) {
            logger.info("Event Posted: " + theEvent);
            super.postEvent(theEvent);
        }
    }

	private void initServer() {
		String portFile = getPortFile();
		if (portFile == null) {
			return;
		}
		mEditServer = new EditServer(portFile, this);
		mEditServer.start();
	}

	private void checkForAnotherInstance(String[] pArgs) {
		String portFile = getPortFile();
		if (portFile == null) {
			return;
		}
		// {{{ Try connecting to another running FreeMind instance
		if (portFile != null && new File(portFile).exists()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(portFile));
				String check = in.readLine();
				if (!check.equals("b"))
					throw new Exception("Wrong port file format");

				int port = Integer.parseInt(in.readLine());
				int key = Integer.parseInt(in.readLine());

				Socket socket = new Socket(InetAddress.getByName("127.0.0.1"),
						port);
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				out.writeInt(key);

				String script;
				// Put url to open here
				script = Tools.arrayToUrls(pArgs);
				out.writeUTF(script);

				logger.info("Waiting for server");
				// block until its closed
				try {
					socket.getInputStream().read();
				} catch (Exception e) {
				}

				in.close();
				out.close();

				System.exit(0);
			} catch (Exception e) {
				// ok, this one seems to confuse newbies
				// endlessly, so log it as NOTICE, not
				// ERROR
				logger.info("An error occurred"
						+ " while connecting to the FreeMind server instance."
						+ " This probably means that"
						+ " FreeMind crashed and/or exited abnormally"
						+ " the last time it was run." + " If you don't"
						+ " know what this means, don't worry. Exception: "+e );
			}
		}

	}

	/**
	 * @return null, if no port should be opened.
	 */
	private String getPortFile() {
		if (mEditServer == null
				&& Resources.getInstance().getBoolProperty(
						RESOURCES_DON_T_OPEN_PORT)) {
			return null;
		}
		return getFreemindDirectory() + File.separator + getProperty(PORT_FILE);
	}

	private void fireStartupDone() {
		mStartupDone = true;
		for (Iterator it = mStartupDoneListeners.iterator(); it.hasNext();) {
			StartupDoneListener listener = (StartupDoneListener) it.next();
			listener.startupDone();
		}
	}

	private void setScreenBounds() {
		// Create the MenuBar
		menuBar = new MenuBar(controller);
		setJMenuBar(menuBar);

		// Create the scroll pane
		mScrollPane = new MapView.ScrollPane();
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

		boolean shouldUseTabbedPane = Resources.getInstance().getBoolProperty(
				RESOURCES_USE_TABBED_PANE);

		if (shouldUseTabbedPane) {
			// tabbed panes eat control up. This is corrected here.
			InputMap map;
			map = (InputMap) UIManager.get("TabbedPane.ancestorInputMap");
			KeyStroke keyStrokeCtrlUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,
					InputEvent.CTRL_DOWN_MASK);
			map.remove(keyStrokeCtrlUp);
			mTabbedPane = new JTabbedPane();
			mTabbedPane.setFocusable(false);
			controller.addTabbedPane(mTabbedPane);
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

			/*
			 * fc, 14.3.2008: Completely removed, as it damaged the focus if for
			 * example the note window was active.
			 */
			// public void windowActivated(WindowEvent e) {
			// // This doesn't work the first time, it's called too early to
			// // get Focus
			// logger.info("windowActivated");
			// if ((getView() != null) && (getView().getSelected() != null)) {
			// getView().getSelected().requestFocus();
			// }
			// }
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
		int win_height = getIntProperty("appwindow_height", 0);
		int win_x = getIntProperty("appwindow_x", 0);
		int win_y = getIntProperty("appwindow_y", 0);
		win_width = (win_width > 0) ? win_width : 640;
		win_height = (win_height > 0) ? win_height : 440;
		final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		final Insets screenInsets = defaultToolkit
				.getScreenInsets(getGraphicsConfiguration());
		Dimension screenSize = defaultToolkit.getScreenSize();
		final int screenWidth = screenSize.width - screenInsets.left
				- screenInsets.right;
		win_width = Math.min(win_width, screenWidth);
		final int screenHeight = screenSize.height - screenInsets.top
				- screenInsets.bottom;
		win_height = Math.min(win_height, screenHeight);
		win_x = Math.max(screenInsets.left, win_x);
		win_x = Math.min(screenWidth + screenInsets.left - win_width, win_x);
		win_y = Math.max(screenInsets.top, win_y);
		win_y = Math.min(screenWidth + screenInsets.top - win_height, win_y);
		setBounds(win_x, win_y, win_width, win_height);
		// set the default state (normal/maximized) (PN)
		// (note: this must be done later when partucular
		// initalizations of the windows are ready,
		// perhaps after setVisible is it enough... :-?
		int win_state = Integer.parseInt(FreeMind.props.getProperty(
				"appwindow_state", "0"));
		win_state = ((win_state & ICONIFIED) != 0) ? NORMAL : win_state;
		setExtendedState(win_state);
	}

	private ModeController createModeController(final String[] args) {
		ModeController ctrl = controller.getModeController();
		// try to load mac module:
		try {
			Class macClass = Class.forName("accessories.plugins.MacChanges");
			// lazy programming. the mac class has exactly one
			// constructor
			// with a modeController.
			macClass.getConstructors()[0].newInstance(new Object[] { this });
		} catch (Exception e1) {
			// freemind.main.Resources.getInstance().logExecption(e1);
		}
		return ctrl;
	}

	private int getMaximumNumberOfMapsToLoad(String[] args) {
		LastStateStorageManagement management = getLastStateStorageManagement();
		return Math.max( args.length + management.getLastOpenList().size(), 1 );
	}

	private void loadMaps(final String[] args, ModeController pModeController,
			FeedBack pFeedBack) {
		boolean fileLoaded = false;
		if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAPS_AND_LAYOUT))) {
			int index = 0;
			MapModule mapToFocus = null;
			LastStateStorageManagement management = getLastStateStorageManagement();
			for (Iterator it = management.getLastOpenList().iterator(); it
					.hasNext();) {
				MindmapLastStateStorage store = (MindmapLastStateStorage) it
						.next();
				String restorable = store.getRestorableName();
				pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
						new Object[] { restorable.replaceAll(".*/", "") });
				try {
					if (controller.getLastOpenedList().open(restorable)) {
						if (index == management.getLastFocussedTab()) {
							mapToFocus = controller.getMapModule();
						}
					}
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
				index++;
			}
			if (mapToFocus != null) {
				controller.getMapModuleManager().changeToMapModule(
						mapToFocus.getDisplayName());
			}
		}
		for (int i = 0; i < args.length; i++) {
			String fileArgument = args[i];
			pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
					new Object[] { fileArgument.replaceAll(".*/", "") });
			if (fileArgument.toLowerCase().endsWith(
					freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION)) {

				if (!Tools.isAbsolutePath(fileArgument)) {
					fileArgument = System.getProperty("user.dir")
							+ System.getProperty("file.separator")
							+ fileArgument;
				}
				try {
					pModeController.load(new File(fileArgument));
					fileLoaded = true;
					// logger.info("Attempting to load: " +
					// args[i]);
				} catch (Exception ex) {
					System.err.println("File " + fileArgument
							+ " not found error");
				}
			}
		}
		if (!fileLoaded) {
			fileLoaded = processLoadEventFromStartupPhase();
		}
		if (!fileLoaded) {
			String restoreable = getProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED);
			if (Tools
					.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAP))
					&& restoreable != null && restoreable.length() > 0) {
				pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS_NAME,
						new Object[] { restoreable.replaceAll(".*/", "") });
				try {
					controller.getLastOpenedList().open(restoreable);
					controller.getModeController().getView().moveToRoot();
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
					out("An error occured on opening the file: " + restoreable
							+ ".");
				}
			}
		}
		if (!fileLoaded
				&& Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_NEW_MAP))) {
			/*
			 * nothing loaded so far. Perhaps, we should display a new map...
			 * According to Summary: On first start FreeMind should show new map
			 * to newbies
			 * https://sourceforge.net/tracker/?func=detail&atid=107118
			 * &aid=1752516&group_id=7118
			 */
			pModeController.newMap();
			pFeedBack.increase(FREE_MIND_PROGRESS_LOAD_MAPS, null);
		}
	}

	private LastStateStorageManagement getLastStateStorageManagement() {
		String lastStateMapXml = getProperty(FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE);
		LastStateStorageManagement management = new LastStateStorageManagement(
				lastStateMapXml);
		return management;
	}

	/**
	 * Iterates over the load events from the startup phase
	 * <p>
	 * More than one file can be opened during startup. The filenames are stored
	 * in numbered properties, i.e.
	 * <ul>
	 * loadEventDuringStartup0=/Users/alex/Desktop/test1.mm
	 * loadEventDuringStartup1=/Users/alex/Desktop/test2.mm
	 * </ul>
	 * 
	 * @return true if at least one file has been loaded
	 */
	private boolean processLoadEventFromStartupPhase() {
		boolean atLeastOneFileHasBeenLoaded = false;
		int count = 0;
		while (true) {
			String propertyKey = FreeMindCommon.LOAD_EVENT_DURING_STARTUP
					+ count;
			if (getProperty(propertyKey) == null) {
				break;
			} else {
				if (processLoadEventFromStartupPhase(propertyKey))
					atLeastOneFileHasBeenLoaded = true;
				++count;
			}
		}
		return atLeastOneFileHasBeenLoaded;
	}

	private boolean processLoadEventFromStartupPhase(String propertyKey) {
		String filename = getProperty(propertyKey);
		try {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading " + filename);
			}
			controller.getModeController().load(
					Tools.fileToUrl(new File(filename)));
			// remove temporary property because we do not want to store in a
			// file and survive restart
			getProperties().remove(propertyKey);
			return true;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			out("An error occured on opening the file: " + filename + ".");
			return false;
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

	public JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent) {
		if (mSplitPane != null) {
			// already present:
			return mSplitPane;
		}
		removeContentComponent();
		int splitType = JSplitPane.VERTICAL_SPLIT;
		String splitProperty = getProperty(J_SPLIT_PANE_SPLIT_TYPE);
		if(Tools.safeEquals(splitProperty, HORIZONTAL_SPLIT_RIGHT)) {
			splitType = JSplitPane.HORIZONTAL_SPLIT;
		} else if(Tools.safeEquals(splitProperty, VERTICAL_SPLIT_BELOW)) {
			// default
		} else {
			logger.warning("Split type not known: " + splitProperty);
		}
		mSplitPane = new JSplitPane(splitType, mScrollPane, pMindMapComponent);
		mSplitPane.setContinuousLayout(true);
		mSplitPane.setOneTouchExpandable(false);
		/*
		 * This means that the mind map area gets all the space that results
		 * from resizing the window.
		 */
		mSplitPane.setResizeWeight(1.0d);
		// split panes eat F8 and F6. This is corrected here.
		Tools.correctJSplitPaneKeyMap();
		mContentComponent = mSplitPane;
		setContentComponent();
		// set divider position:
		int splitPanePosition = getIntProperty(SPLIT_PANE_POSITION, -1);
		int lastSplitPanePosition = getIntProperty(SPLIT_PANE_LAST_POSITION, -1);
		if (splitPanePosition != -1 && lastSplitPanePosition != -1) {
			mSplitPane.setDividerLocation(splitPanePosition);
			mSplitPane.setLastDividerLocation(lastSplitPanePosition);
		}
		return mSplitPane;
	}

	public void removeSplitPane() {
		if (mSplitPane != null) {
			setProperty(SPLIT_PANE_POSITION,
					"" + mSplitPane.getDividerLocation());
			setProperty(SPLIT_PANE_LAST_POSITION,
					"" + mSplitPane.getLastDividerLocation());
			removeContentComponent();
			mContentComponent = mScrollPane;
			setContentComponent();
			mSplitPane = null;
		}
	}

	private void removeContentComponent() {
		if (mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex() >= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(),
						new JPanel());
			}
		} else {
			getContentPane().remove(mContentComponent);
			getRootPane().revalidate();
		}

	}

	private void setContentComponent() {
		if (mTabbedPane != null) {
			if (mTabbedPane.getSelectedIndex() >= 0) {
				mTabbedPane.setComponentAt(mTabbedPane.getSelectedIndex(),
						mContentComponent);
			}
		} else {
			getContentPane().add(mContentComponent, BorderLayout.CENTER);
			getRootPane().revalidate();
		}
	}

	public JScrollPane getScrollPane() {
		return mScrollPane;
	}

	public JComponent getContentComponent() {
		return mContentComponent;
	}

	public void registerStartupDoneListener(
			StartupDoneListener pStartupDoneListener) {
		if (!mStartupDone)
			mStartupDoneListeners.add(pStartupDoneListener);
	}

	public List getLoggerList() {
		return Collections.unmodifiableList(mLoggerList);
	}

}
