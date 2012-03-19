/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 * Created on 10.01.2007
 */
/*$Id: ScriptEditorPanel.java,v 1.1.2.18 2008/07/05 20:40:10 christianfoltin Exp $*/
package plugins.script;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import plugins.script.ScriptingEngine.ErrorHandler;
import freemind.controller.BlindIcon;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;

/**
 * @author foltin TODO:
 *         <ul>
 *         <li></li>
 *         <li>new script/delete script buttons</li>
 *         <li>rename script button</li>
 *         <li>undo feature?</li>
 *         <li>show line/column numbers in status bar</li>
 *         </ul>
 */
public class ScriptEditorPanel extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3221975191441136520L;

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = "plugins.script.ScriptEditorPanel/window_positions";

	private final FreeMindMain mFrame;

	private final ScriptModel mScriptModel;

	private JList mScriptList;

	private JTextArea mScriptTextField;

	private DefaultListModel mListModel;

	private Integer mLastSelected = null;

	private JTextArea mScriptResultField;

	private JSplitPane mCentralUpperPanel;

	private JSplitPane mCentralPanel;

	private Logger logger;

	private JLabel mStatus;

	private AbstractAction mRunAction;

	private SignAction mSignAction;

	private final class ResultFieldStream extends OutputStream {
		public void write(int pByte) throws IOException {
			mScriptResultField.append(new String(new byte[] { (byte) pByte }));
		}

		public void write(byte[] pB) throws IOException {
			mScriptResultField.append(new String(pB));
		}
	}

	private final class RunAction extends AbstractAction {
		private RunAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			storeCurrent();
			if (!mScriptList.isSelectionEmpty()) {
				mScriptResultField.setText("");
				mScriptModel.executeScript(mScriptList.getSelectedIndex(),
						getPrintStream(), getErrorHandler());
			}
		}
	}

	private final class SignAction extends AbstractAction {
		private SignAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			storeCurrent();
			if (!mScriptList.isSelectionEmpty()) {
				int selectedIndex = mScriptList.getSelectedIndex();
				ScriptHolder script = mScriptModel.getScript(selectedIndex);
				String signedScript = new SignedScriptHandler().signScript(
						script.mScript, Resources.getInstance(), mFrame);
				script.setScript(signedScript);
				mScriptModel.setScript(selectedIndex, script);
				mScriptTextField.setText(signedScript);
			}
		}
	}

	private final class CancelAction extends AbstractAction {
		private CancelAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog(true);
		}
	}

	private final class ExitAction extends AbstractAction {
		private ExitAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			storeCurrent();
			disposeDialog(false);
		}
	}

	private final class NewScriptAction extends AbstractAction {
		private NewScriptAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			storeCurrent();
			mLastSelected = null;
			int scriptIndex = mScriptModel.addNewScript();
			updateFields();
			select(scriptIndex);
		}
	}

	public static class ScriptHolder {
		String mScript;

		String mScriptName;

		/**
		 * @param pScriptName
		 *            script name (starting with "script"
		 *            (ScriptingEngine.SCRIPT_PREFIX))
		 * @param pScript
		 *            script content
		 */
		public ScriptHolder(String pScriptName, String pScript) {
			super();
			mScript = pScript;
			mScriptName = pScriptName;
		}

		public String getScript() {
			return mScript;
		}

		public String getScriptName() {
			return mScriptName;
		}

		public ScriptHolder setScript(String pScript) {
			mScript = pScript;
			return this;
		}

		public ScriptHolder setScriptName(String pScriptName) {
			mScriptName = pScriptName;
			return this;
		}
	}

	public interface ScriptModel {
		int getAmountOfScripts();

		/**
		 * @param pIndex
		 *            zero-based
		 * @return a script
		 */
		ScriptHolder getScript(int pIndex);

		void setScript(int pIndex, ScriptHolder pScript);

		boolean executeScript(int pIndex, PrintStream outStream,
				ErrorHandler pErrorHandler);

		void storeDialogPositions(ScriptEditorPanel pPanel,
				ScriptEditorWindowConfigurationStorage pStorage,
				String pWindow_preference_storage_property);

		ScriptEditorWindowConfigurationStorage decorateDialog(
				ScriptEditorPanel pPanel,
				String pWindow_preference_storage_property);

		void endDialog(boolean pIsCanceled);

		boolean isDirty();

		/**
		 * 
		 * @return the index of the new script.
		 */
		int addNewScript();
	}

	public ScriptEditorPanel(ScriptModel pScriptModel, FreeMindMain pFrame,
			boolean pHasNewScriptFunctionality) {
		super(pFrame.getJFrame(), true /* modal */);
		logger = pFrame.getLogger(this.getClass().getName());
		mScriptModel = pScriptModel;
		mFrame = pFrame;
		// build the panel:
		this.setTitle(pFrame
				.getResourceString("plugins/ScriptEditor/window.title"));
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog(true);
			}
		});
		Tools.addEscapeActionToDialog(this, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				disposeDialog(true);
			}
		});

		Container contentPane = this.getContentPane();

		contentPane.setLayout(new BorderLayout());
		mListModel = new DefaultListModel();
		mScriptList = new JList(mListModel);
		mScriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mScriptList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent pEvent) {
				if (pEvent.getValueIsAdjusting())
					return;
				// System.out.println("List selection:" + pEvent);
				select(mScriptList.getSelectedIndex());
			}
		});
		// add(mScriptList, BorderLayout.WEST);
		mScriptTextField = new JTextArea();
		mScriptTextField.setFont(new Font("Monospaced", Font.PLAIN, 12));
		mScriptTextField.setEnabled(false);
		mScriptTextField.setTabSize(2);
		mCentralUpperPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				mScriptList, new JScrollPane(mScriptTextField));
		mCentralUpperPanel.setContinuousLayout(true);
		mScriptResultField = new JTextArea();
		mScriptResultField.setEditable(false);
		mScriptResultField.setWrapStyleWord(true);
		mCentralPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				mCentralUpperPanel, new JScrollPane(mScriptResultField));
		mCentralPanel.setDividerLocation(0.8);
		mCentralPanel.setContinuousLayout(true);
		contentPane.add(mCentralPanel, BorderLayout.CENTER);
		mStatus = new JLabel();
		contentPane.add(mStatus, BorderLayout.SOUTH);
		mScriptTextField.addCaretListener(new CaretListener() {

			public void caretUpdate(CaretEvent arg0) {
				int caretPosition = mScriptTextField.getCaretPosition();
				try {
					int lineOfOffset = mScriptTextField
							.getLineOfOffset(caretPosition);
					mStatus.setText("Line: "
							+ (lineOfOffset + 1)
							+ ", Column: "
							+ (caretPosition
									- mScriptTextField
											.getLineStartOffset(lineOfOffset) + 1));
				} catch (BadLocationException e) {
					freemind.main.Resources.getInstance().logException(e);
				}

			}
		});
		updateFields();
		mScriptTextField.repaint();
		// menu:
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu();
		Tools.setLabelAndMnemonic(menu,
				pFrame.getResourceString("plugins/ScriptEditor.menu_actions"));
		if (pHasNewScriptFunctionality) {
			addAction(
					menu,
					new NewScriptAction(
							pFrame.getResourceString("plugins/ScriptEditor.new_script")));
		}
		mRunAction = new RunAction(
				pFrame.getResourceString("plugins/ScriptEditor.run"));
		mRunAction.setEnabled(false);
		addAction(menu, mRunAction);
		mSignAction = new SignAction(
				pFrame.getResourceString("plugins/ScriptEditor.sign"));
		mSignAction.setEnabled(false);
		addAction(menu, mSignAction);
		AbstractAction cancelAction = new CancelAction(
				pFrame.getResourceString("plugins/ScriptEditor.cancel"));
		addAction(menu, cancelAction);
		AbstractAction exitAction = new ExitAction(
				pFrame.getResourceString("plugins/ScriptEditor.exit"));
		addAction(menu, exitAction);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		// Retrieve window size and column positions.
		ScriptEditorWindowConfigurationStorage storage = mScriptModel
				.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			mCentralUpperPanel.setDividerLocation(storage.getLeftRatio());
			mCentralPanel.setDividerLocation(storage.getTopRatio());
		} else {
			// bug fix: for new users, this is set to some reasonable values.
			mCentralUpperPanel.setDividerLocation(100);
			mCentralPanel.setDividerLocation(240);
		}

	}

	private void addAction(JMenu menu, AbstractAction action) {
		JMenuItem item = menu.add(action);
		Tools.setLabelAndMnemonic(item,
				(String) action.getValue(AbstractAction.NAME));
		item.setIcon(new BlindIcon(StructuredMenuHolder.ICON_SIZE));
	}

	private void updateFields() {
		mListModel.clear();
		for (int i = 0; i < mScriptModel.getAmountOfScripts(); ++i) {
			ScriptHolder script = mScriptModel.getScript(i);
			mListModel.addElement(script.getScriptName());
		}
	}

	private void select(int pIndex) {
		mScriptTextField.setEnabled(pIndex >= 0);
		mRunAction.setEnabled(pIndex >= 0);
		mSignAction.setEnabled(pIndex >= 0);
		if (pIndex < 0) {
			mScriptTextField.setText("");
			return;
		}
		storeCurrent();
		// set new script
		mScriptTextField.setText(mScriptModel.getScript(pIndex).getScript());
		// set last one:
		mLastSelected = new Integer(pIndex);
		if (pIndex >= 0 && mScriptList.getSelectedIndex() != pIndex) {
			mScriptList.setSelectedIndex(pIndex);
		}
	}

	private void storeCurrent() {
		if (mLastSelected != null) {
			// store old value:
			int oldIndex = mLastSelected.intValue();
			mScriptModel.setScript(oldIndex, mScriptModel.getScript(oldIndex)
					.setScript(mScriptTextField.getText()));
		}
	}

	/**
	 * @param pIsCanceled
	 *            TODO
	 * 
	 */
	private void disposeDialog(boolean pIsCanceled) {
		// store current script:
		if (!mScriptList.isSelectionEmpty()) {
			select(mScriptList.getSelectedIndex());
		}
		if (pIsCanceled && mScriptModel.isDirty()) {
			// ask if really cancel:
			int action = JOptionPane.showConfirmDialog(this, mFrame
					.getResourceString("ScriptEditorPanel.changed_save"),
					"FreeMind", JOptionPane.YES_NO_CANCEL_OPTION);
			if (action == JOptionPane.CANCEL_OPTION)
				return;
			if(action == JOptionPane.YES_OPTION) {
				pIsCanceled = false;
			}
		}
		// store window positions:
		ScriptEditorWindowConfigurationStorage storage = new ScriptEditorWindowConfigurationStorage();
		storage.setLeftRatio(mCentralUpperPanel.getDividerLocation());
		storage.setTopRatio(mCentralPanel.getDividerLocation());
		mScriptModel.storeDialogPositions(this, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);
		this.setVisible(false);
		this.dispose();
		mScriptModel.endDialog(pIsCanceled);
	}

	PrintStream getPrintStream() {
		return new PrintStream(new ResultFieldStream());
	}

	ErrorHandler getErrorHandler() {
		return new ErrorHandler() {
			public void gotoLine(int pLineNumber) {
				logger.info("Line number: " + pLineNumber);
				if (pLineNumber > 0
						&& pLineNumber <= mScriptTextField.getLineCount()) {
					Element element3 = mScriptTextField.getDocument()
							.getDefaultRootElement();
					Element element4 = element3.getElement(pLineNumber - 1);
					if (element4 != null) {
						mScriptTextField.select(
								((int) element4.getStartOffset()),
								element4.getEndOffset());
					}
				}
			}
		};
	}

}
