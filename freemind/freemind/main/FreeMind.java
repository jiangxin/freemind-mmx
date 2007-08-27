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
/*$Id: FreeMind.java,v 1.32.14.28.2.73 2007-08-27 17:55:24 dpolivaev Exp $*/

package freemind.main;

import java.awt.BorderLayout;
import java.awt.Container;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
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
import freemind.controller.MenuBar;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.preferences.FreemindPropertyListener;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

public class FreeMind extends JFrame implements FreeMindMain {

	private static class SouthPanel extends JPanel {
		public SouthPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		}
		
		protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
			return super.processKeyBinding(ks, e, condition, pressed) || e.getKeyChar() == KeyEvent.VK_SPACE;			
		}
	}

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

    public static final String RESOURCES_DELETE_NODES_WITHOUT_QUESTION = "delete_nodes_without_question";

	private static Logger logger = null;

	private static final String DEFAULT_LANGUAGE = "en";

	public static final String VERSION = "0.9.0 Beta 13";

	public static final String XML_VERSION = "0.9.0_Beta_8";

	public static final String RESOURCES_REMIND_USE_RICH_TEXT_IN_NEW_LONG_NODES = "remind_use_rich_text_in_new_long_nodes";


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

	private JPanel southPanel;

	private JScrollPane scrollPane = new JScrollPane();

	private JSplitPane mSplitPane;

	private JTabbedPane mTabbedPane;
	private boolean mTabbedPaneSelectionUpdate = true;

	private ImageIcon mWindowIcon;

	public FreeMind() {
		super("FreeMind");
		// read default properties from jar:
		readDefaultProperties();
		createUserDirectory();
		if (logger == null) {
			logger = getLogger(FreeMind.class.getName());
		}
		mFreeMindCommon = new FreeMindCommon(this);
		Resources.createInstance(this);
	}

	
	void init(FeedBack feedback) {
		/* This is only for apple but does not harm for the others. */
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// Automagical properties, generated by freemind itself
		Properties auto = new Properties(props);
		// load user properties

		try {

			// Dimitri: feedback.increase must not be called before auto
			// properties with language setting has been loaded
			InputStream in = null;
			File userPropertiesFolder = new File(getFreemindDirectory());
			autoPropertiesFile = new File(userPropertiesFolder, props
					.getProperty("autoproperties"));
			patternsFile = new File(userPropertiesFolder, props
					.getProperty("patternsfile"));

			try {
				in = new FileInputStream(autoPropertiesFile);
				auto.load(in);
			} catch (Exception ex) {
				// System.err.println("No auto properties loaded.");
			} finally {
				in.close();
			}

		} catch (Exception ex) {
			// even close() fails. what now?
		}
		props = auto;// priority of files might be changed again
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


		feedback.increase("FreeMind.progress.buildScreen");
		setScreenBounds();
		
		feedback.increase("FreeMind.progress.propageteLookAndFeel");
		SwingUtilities.updateComponentTreeUI(this); // Propagate LookAndFeel to
		// JComponents

		feedback.increase("FreeMind.progress.createInitialMode");
		controller.createNewMode(getProperty("initial_mode"));

	}// Constructor

	private void readDefaultProperties() {
		// Default Properties from .jar file
		String propsLoc = "freemind.properties";
		URL defaultPropsURL = ClassLoader.getSystemResource(propsLoc);
		props = new Properties();
		try {
			InputStream in = null;
			in = defaultPropsURL.openStream();
			props.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err
					.println("Panic! Error while loading default properties.");
		}
		defProps = props;
	}

	private void createUserDirectory() {
		File userPropertiesFolder = new File(getFreemindDirectory());
		try {
			// create user directory:
			if (!userPropertiesFolder.exists()) {
				userPropertiesFolder.mkdir();
			}
		} catch (Exception e) {
			// exception is logged to console as we don't have a logger
			e.printStackTrace();
			System.err
					.println("Cannot create folder for user properties and logging: '"
							+ userPropertiesFolder.getAbsolutePath() + "'");

		}
	}

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
		return scrollPane.getViewport();
	}

	public String getFreemindVersion() {
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
		return ClassLoader.getSystemResource(name);
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
		// TODO: Move the split pane property to a listener.
		setProperty(SPLIT_PANE_POSITION, "" + mSplitPane.getDividerLocation());
		setProperty(SPLIT_PANE_LAST_POSITION, ""
				+ mSplitPane.getLastDividerLocation());
		try {
			OutputStream out = new FileOutputStream(autoPropertiesFile);
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
		scrollPane.setViewportView(view);
	}

	public MenuBar getFreeMindMenuBar() {
		return menuBar;
	}

	public void out(String msg) {
		status.setText(msg);
		// logger.info(msg);
	}

	public void err(String msg) {
		status.setText(msg);
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
					command = "rundll32 url.dll,FileProtocolHandler "
							+ Tools.urlGetFile(url);
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

	public java.util.logging.Logger getLogger(String forClass) {
		Logger logger2 = java.util.logging.Logger.getLogger(forClass);
		if (mFileHandler == null) {
			try {
				mFileHandler = new FileHandler(getFreemindDirectory()
						+ File.separator + "log", 1400000, 5, false);
				mFileHandler.setFormatter(new SimpleFormatter());
			} catch (Exception e) {
				System.err.println("Error creating logging File Handler");
				e.printStackTrace();
				// to avoid infinite recursion.
				// freemind.main.Resources.getInstance().logExecption(e);
			}
		}
		if (mFileHandler != null)
			logger2.addHandler(mFileHandler);
		return logger2;
	}

	public static void main(final String[] args) {
		final FreeMind frame = new FreeMind();
		IFreeMindSplash splash = null;
		final FeedBack feedBack;
		// change here, if you don't like the splash
		if (true) {
			// fc: don't change the following line as it is changed by the
			// build.xml script.
			splash = new FreeMindSplash(frame);
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
					logger.info("Beginnig task:" + messageId);
				}

				public void setMaximumValue(int max) {
				}
			};
			frame.mWindowIcon = new ImageIcon(frame
					.getResource("images/FreeMindWindowIcon.png"));
		}
		feedBack.setMaximumValue(9);
		frame.init(feedBack);
		try {
			// wait until AWT thread starts
			if (!EventQueue.isDispatchThread()) {
				EventQueue.invokeAndWait(new Runnable() {
					public void run() {
					};
				});
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}

		
		final IFreeMindSplash splash2 = splash;
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				// work around because of the Note edit event on start:
				// fc: but only, if a map is open
				if (frame.getController().getMapModule() != null) {
					frame.getController().getMap().setSaved(true);
				}				
				feedBack
				.increase("FreeMind.progress.startCreateController");
				ModeController ctrl = frame.createModeController(args);
				feedBack.increase("FreeMind.progress.loadMaps");
				// This could be improved.
				frame.loadMaps(args, ctrl);

				feedBack.increase("FreeMind.progress.endStartup");
				
				if (splash2 != null) {
					splash2.setVisible(false);
				}
				frame.setVisible(true);
			}
		});
	}

	private void setScreenBounds() {
		// Create the MenuBar
		menuBar = new MenuBar(controller);
		setJMenuBar(menuBar);

		// Create the scroll pane

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
		if (Tools.safeEquals(getProperty("no_scrollbar"), "true")) {
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		} else {
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		}
		southPanel = new SouthPanel();
		status = new JLabel();
		// southPanel.add( status, BorderLayout.SOUTH );

		mSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane,
				southPanel);
		mSplitPane.setContinuousLayout(true);
		mSplitPane.setOneTouchExpandable(true);
		// split panes eat F8 and F6. This is corrected here.
		InputMap map = (InputMap) UIManager.get("SplitPane.ancestorInputMap");

		KeyStroke keyStrokeF6 = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
		KeyStroke keyStrokeF8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0);

		map.remove(keyStrokeF6);
		map.remove(keyStrokeF8);

		boolean shouldUseTabbedPane = "true".equals(controller.getFrame()
				.getProperty(RESOURCES_USE_TABBED_PANE));

		if (shouldUseTabbedPane) {
			// tabbed panes eat control up. This is corrected here.
			map = (InputMap) UIManager.get("TabbedPane.ancestorInputMap");
			KeyStroke keyStrokeCtrlUp = KeyStroke.getKeyStroke( KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK);
			map.remove(keyStrokeCtrlUp);
			mTabbedPane = new JTabbedPane();
//			mTabbedPane.setFocusable(false);
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
							// detect rename:
							if (pOldMapModule == pNewMapModule
									&& selectedIndex >= 0) {
								// map was renamed.
								mTabbedPane.setTitleAt(selectedIndex,
										pNewMapModule.toString());
							}
							// search, if already present:
							for (int i = 0; i < mTabbedPane.getTabCount(); ++i) {
								if (mTabbedPane.getTitleAt(i).equals(
										pNewMapModule.toString())) {
									if (selectedIndex != i) {
										mTabbedPane.setSelectedIndex(i);
									}
									return;
								}
							}
							// create new tab:
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
							for (int i = 0; i < mTabbedPane.getTabCount(); ++i) {
								if (mTabbedPane.getTitleAt(i).equals(
										pOldMapModule.toString())) {
									logger.fine("Remove tab:" + i + " with title:" + mTabbedPane.getTitleAt(i));
									mTabbedPaneSelectionUpdate = false;
									mTabbedPane.removeTabAt(i);
									mTabbedPaneSelectionUpdate = true;
									tabSelectionChanged();
									return;
								}
							}							
						}
					});
			getContentPane().add(mTabbedPane, BorderLayout.CENTER);
		} else {
			// don't use tabbed panes.
			getContentPane().add(mSplitPane, BorderLayout.CENTER);
		}
		getContentPane().add(status, BorderLayout.SOUTH);

		// Disable the default close button, instead use windowListener
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.quit
						.actionPerformed(new ActionEvent(this, 0, "quit"));
			}

			public void windowActivated(WindowEvent e) {
				// This doesn't work the first time, it's called too early to
				// get Focus
				if ((getView() != null) && (getView().getSelected() != null)) {
					getView().getSelected().requestFocus();
				}
			}
		});

		if (Tools.safeEquals(getProperty("toolbarVisible"), "false")) {
			controller.setToolbarVisible(false);
		}

		if (Tools.safeEquals(getProperty("leftToolbarVisible"), "false")) {
			controller.setLeftToolbarVisible(false);
		}

		// set the default state (normal/maximized) (PN)
		// (note: this must be done later when partucular
		// initalizations of the windows are ready,
		// perhaps after setVisible is it enough... :-?
		int win_state = Integer.parseInt(FreeMind.props
				.getProperty("appwindow_state", "0"));
		win_state = ((win_state & ICONIFIED) != 0) ? NORMAL
				: win_state;
		setExtendedState(win_state);
		// set divider position:
		int splitPanePosition = getIntProperty(
				SPLIT_PANE_POSITION,  -1);
		int lastSplitPanePosition = getIntProperty(
				SPLIT_PANE_LAST_POSITION, -1);
		mSplitPane.setResizeWeight(0.8);
		if(splitPanePosition != -1 && lastSplitPanePosition != -1){
			mSplitPane.setDividerLocation(splitPanePosition);
			mSplitPane.setLastDividerLocation(lastSplitPanePosition);
		}
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

	private void loadMaps(final String[] args, ModeController ctrl) {
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
					ctrl.load(new File(fileArgument).toURL());
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
				} catch (Exception e) {
					freemind.main.Resources.getInstance()
					.logException(e);
					out("An error occured on opening the file: "
							+ restoreable + ".");
				}
			}
		}
	}
	/**
	 */
	public JPanel getSouthPanel() {
		return southPanel;
	}

	public JSplitPane getSplitPane() {
		return mSplitPane;
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
		String selectedTitle = mTabbedPane
				.getTitleAt(selectedIndex);
		logger.fine("Selected index of tab is now: " + selectedIndex + " with title:"+selectedTitle);
		if (!selectedTitle.equals(controller.getMapModule()
				.toString())) {
			// we have to change the active map actively:
			controller.getMapModuleManager().changeToMapModule(
					selectedTitle);
		}
		mTabbedPane.setComponentAt(selectedIndex, mSplitPane);
	}

}
