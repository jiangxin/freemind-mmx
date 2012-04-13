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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.actions.generated.instance.LogFileViewerConfigurationStorage;
import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.Mode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

public class LogFileViewer extends MindMapHookAdapter implements
		MapModuleChangeObserver {

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = LogFileViewer.class
			.getName();

	private MindMapController mMyMindMapController;

	private JDialog mLogFileViewer;

	private CloseAction mCloseAction;

	private JTextArea mTextArea;

	private final class CloseAction extends AbstractAction {

		public CloseAction() {
			super(getResourceString("LogFileViewer_close"));
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookAdapter#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		mMyMindMapController = super.getMindMapController();
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
				false /* unmodal */);
		mLogFileViewer.setTitle(getResourceString("LogFileViewer_title")+pathname);
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
		mLogFileViewer.setSize(400, 400);
		mLogFileViewer.setLayout(new BorderLayout());
		mTextArea = new JTextArea(logFileContents);
		mTextArea.setEditable(false);
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

}
