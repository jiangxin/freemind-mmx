/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */

package accessories.plugins;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.MenuItemSelectedListener;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.LogFileViewerConfigurationStorage;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.LogFileLogHandler;
import freemind.main.LogFileLogHandler.LogReceiver;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionHandler;
import freemind.modes.mindmapmode.actions.xml.PrintActionHandler;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

public class LogFileViewer extends MindMapHookAdapter implements
		MapModuleChangeObserver, LogReceiver {

	public static class Registration implements HookRegistration {
		/**
		 * Maps MindMapController --> PrintActionHandler Here, a static map is
		 * used, as the HookRegistration are registered each time a map is
		 * changed. Thus, a normal member isn't possible here.
		 */
		private static HashMap mPrintActionHandler = new HashMap();

		private final MindMapController modeController;

		public Registration(ModeController controller, MindMap map) {
			modeController = (MindMapController) controller;
		}

		public void register() {
		}

		public void deRegister() {
		}

		public void togglePrintAction() {
			if (!mPrintActionHandler.containsKey(modeController)) {
				PrintActionHandler printActionHandler = new freemind.modes.mindmapmode.actions.xml.PrintActionHandler(
						modeController);
				modeController.getActionFactory().registerHandler(
						printActionHandler);
				mPrintActionHandler.put(modeController, printActionHandler);
			} else {
				modeController.getActionFactory()
						.deregisterHandler(
								(ActionHandler) mPrintActionHandler
										.get(modeController));
			}

		}

		public boolean isPrintActionActive() {
			return mPrintActionHandler.containsKey(modeController);
		}
	}

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = LogFileViewer.class
			.getName();

	private MindMapController mMyMindMapController;

	private JDialog mLogFileViewer;

	private CloseAction mCloseAction;

	private JTextArea mTextArea;

	protected static java.util.logging.Logger logger = null;

	private JMenuBar mMenuBar;
	
	private UpdateTextAreaThread mUpdateTextAreaThread;

	private SimpleFormatter mSimpleFormatter;

	private final class CloseAction extends AbstractAction {

		public CloseAction() {
			super(getResourceString("LogFileViewer_close"));
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog();
		}
	}

	private final class PrintOperationAction extends AbstractAction implements
			MenuItemSelectedListener {

		public PrintOperationAction() {
			super(getResourceString("LogFileViewer.PrintOperationAction"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent pE) {
			getRegistration().togglePrintAction();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.MenuItemSelectedListener#isSelected(javax.swing
		 * .JMenuItem, javax.swing.Action)
		 */
		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return getRegistration().isPrintActionActive();
		}

	}

	private final class SetLogLevelAction extends AbstractAction implements
			MenuItemSelectedListener {

		private final Level mLevel;

		public SetLogLevelAction(Level pLevel) {
			super(getResourceString("LogFileViewer.SetLogLevelAction_"
					+ pLevel.getName()));
			mLevel = pLevel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent pE) {
			getBaseHandler().setLevel(mLevel);
			List loggerList = getMindMapController().getFrame().getLoggerList();
			for (Iterator it = loggerList.iterator(); it.hasNext();) {
				Logger otherLogger = (Logger) it.next();
				otherLogger.setLevel(mLevel);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.MenuItemSelectedListener#isSelected(javax.swing
		 * .JMenuItem, javax.swing.Action)
		 */
		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return getBaseHandler().getLevel().equals(mLevel);
		}

	}

	public Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookAdapter#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mMyMindMapController = super.getMindMapController();
		mSimpleFormatter = new SimpleFormatter();
		// retrieve content
		final String pathname = getMindMapController().getFrame()
				.getFreemindDirectory()
				+ File.separator
				+ FreeMind.LOG_FILE_NAME + ".0";
		String logFileContents = Tools.getFile(new File(pathname));
		// done.
		getMindMapController().getController().getMapModuleManager()
				.addListener(this);
		mLogFileViewer = new JDialog(getController().getFrame().getJFrame(),
				false);
		mLogFileViewer.setTitle(getResourceString("LogFileViewer_title")
				+ pathname);
		mLogFileViewer
				.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mLogFileViewer.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog();
			}
		});
		mCloseAction = new CloseAction();
		// the action title is changed by the following method, thus we create
		// another close action.
		Tools.addEscapeActionToDialog(mLogFileViewer, new CloseAction());

		/** Menu **/
		StructuredMenuHolder menuHolder = new StructuredMenuHolder();
		mMenuBar = new JMenuBar();
		JMenu mainItem = new JMenu(
				getResourceString("MapControllerPopupDialog.Actions"));
		menuHolder.addMenu(mainItem, "main/actions/.");
		Action printOperationAction = new PrintOperationAction();
		addAccelerator(menuHolder.addAction(printOperationAction,
				"main/actions/printOperationAction"),
				"keystroke_accessories/plugins/LogFileViewer_printOperationAction");
		JMenu loggerItem = new JMenu(
				getResourceString("MapControllerPopupDialog.LogLevels"));
		menuHolder.addMenu(loggerItem, "main/loglevel/.");
		Level[] levels = new Level[] {Level.FINEST, Level.FINER, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF};
		for (int i = 0; i < levels.length; i++) {
			Level level = levels[i];
			menuHolder.addAction(new SetLogLevelAction(level),
					"main/loglevel/setLogLevel_"+level.getName());
			
		}
		menuHolder.updateMenus(mMenuBar, "main/");
		mLogFileViewer.setJMenuBar(mMenuBar);
		mLogFileViewer.setSize(400, 400);
		mLogFileViewer.setLayout(new BorderLayout());
		mTextArea = new JTextArea(logFileContents);
		mTextArea.setEditable(false);
		mTextArea.getCaret().setVisible(true);
		// scroll at the end
		mTextArea.setCaretPosition(logFileContents.length());
		mLogFileViewer.add(new JScrollPane(mTextArea), BorderLayout.CENTER);
		// restore preferences:
		// Retrieve window size and column positions.
		LogFileViewerConfigurationStorage storage = (LogFileViewerConfigurationStorage) getMindMapController()
				.decorateDialog(mLogFileViewer,
						WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			// retrieve_additional_data_here
		}
		mLogFileViewer.setVisible(true);
		mUpdateTextAreaThread = new UpdateTextAreaThread();
		mUpdateTextAreaThread.start();
		LogFileLogHandler baseHandler = getBaseHandler();
		if (baseHandler != null) {
			baseHandler.setLogReceiver(this);
		}
	}

	
	
	protected Logger getBaseLogger() {
		return logger.getParent();
	}
	
	/**
	 * @TODO: This is a bit dirty here, better would be to ask the resources class
	 * for the static logger, but this would result in too much new interfaces.
	 */
	protected LogFileLogHandler getBaseHandler() {
		for (int i = 0; i < logger.getHandlers().length; i++) {
			Handler handler = logger.getHandlers()[i];
			if (handler instanceof LogFileLogHandler) {
				LogFileLogHandler logHandler = (LogFileLogHandler) handler;
				return logHandler;
			}
		}
		return null;
	}

	/**
	 * Overwritten, as this dialog is not modal, but after the plugin has
	 * terminated, the dialog is still present and needs the controller to store
	 * its values.
	 * */
	public MindMapController getMindMapController() {
		return mMyMindMapController;
	}

	/**
	 * 
	 */
	public void disposeDialog() {
		mUpdateTextAreaThread.commitSuicide();
		mUpdateTextAreaThread = null;
		LogFileLogHandler baseHandler = getBaseHandler();
		if (baseHandler != null) {
			baseHandler.setLogReceiver(null);
		}
		// store window positions:
		LogFileViewerConfigurationStorage storage = new LogFileViewerConfigurationStorage();
		// put_additional_data_here
		getMindMapController().storeDialogPositions(mLogFileViewer, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);

		getMindMapController().getController().getMapModuleManager()
				.removeListener(this);
		mLogFileViewer.setVisible(false);
		mLogFileViewer.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * isMapModuleChangeAllowed(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public boolean isMapModuleChangeAllowed(MapModule pOldMapModule,
			Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * beforeMapModuleChange(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void beforeMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.MapModuleManager.MapModuleChangeObserver#afterMapClose
	 * (freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
		disposeDialog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * afterMapModuleChange(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
		disposeDialog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * numberOfOpenMapInformation(int, int)
	 */
	public void numberOfOpenMapInformation(int pNumber, int pIndex) {
	}

	public CloseAction getCloseAction() {
		return mCloseAction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * accessories.plugins.LogFileLogHandler.LogReceiver#receiveLog(java.util
	 * .logging.LogRecord)
	 */
	public void receiveLog(final LogRecord record) {
		String msg = mSimpleFormatter.format(record);
		mUpdateTextAreaThread.addToInbox(msg);
	}

	private class UpdateTextAreaThread extends Thread {
		Vector mInbox = new Vector();
		private boolean mCommitSuicide = false;
		private boolean mSuicided = false;

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while(!mCommitSuicide) {
				final Vector queue = new Vector();
				synchronized (mInbox) {
					if(!mInbox.isEmpty()) {
						queue.addAll(mInbox);
						mInbox.clear();
					}
				}
				if(!queue.isEmpty()) {
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								StringBuffer buffer = new StringBuffer();
								for (Iterator it = queue.iterator(); it
										.hasNext();) {
									String msg = (String) it.next();
									buffer.append(msg);
//									buffer.append('\n');
								}
								String msg = buffer.toString();
								// is cursor at the end?
								final int length = mTextArea.getDocument().getLength();
								boolean atEnd = mTextArea.getCaretPosition() == length;
								mTextArea.getDocument().insertString(length, msg, null);
								if (atEnd) {
									// if at end, scroll again to the end
									mTextArea.setCaretPosition(mTextArea.getDocument()
											.getLength());
								}
							} catch (Exception ex) {
								// We don't want to log anything here...
							}
							
						}
					});
				} 
				sleepALittle();
			}
			mSuicided = true;
		}

		/**
		 * 
		 */
		public void commitSuicide() {
			mCommitSuicide  = true;
			int timeout = 100;
			while(timeout-->0) {
				if(mSuicided)
					break;
				sleepALittle();
			}
		}

		protected void sleepALittle() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
				
			}
		}
		
		public void addToInbox(String msg) {
			synchronized (mInbox) {
				mInbox.add(msg);
			}
		}
		
	}
	
}
