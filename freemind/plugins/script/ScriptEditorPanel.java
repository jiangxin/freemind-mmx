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
/*$Id: ScriptEditorPanel.java,v 1.1.2.6 2007-01-30 21:09:49 christianfoltin Exp $*/
package plugins.script;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Element;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;

import freemind.controller.BlindIcon;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import groovy.lang.GroovyRuntimeException;

/**
 * @author foltin 
 * TODO:
 * <ul><li> 
 * </li><li>new script/delete script buttons 
 * </li><li>rename script button
 * </li><li>undo feature? 
 * </li><li>show line/column numbers in status bar
 * </li><li>"Are you sure to cancel..."
 * </li></ul>
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

	private final class RunAction extends AbstractAction {
		private RunAction(String pArg0) {
			super(pArg0);
		}

		public void actionPerformed(ActionEvent arg0) {
			storeCurrent();
			if (!mScriptList.isSelectionEmpty()) {
				mScriptResultField.setText("");
				String resultString = "";
				try {
					resultString = "\n"
							+ mFrame
									.getResourceString("plugins/ScriptEditor/window.Result")
							+ mScriptModel.executeScript(mScriptList
									.getSelectedIndex(), new PrintStream(
									new OutputStream() {

										public void write(int pByte)
												throws IOException {
											mScriptResultField
													.append(new String(
															new byte[] { (byte) pByte }));
										}
									}));
				} catch (GroovyRuntimeException e) {
					// freemind.main.Resources.getInstance().logExecption(e);
					// ByteArrayOutputStream byteArrayOutputStream = new
					// ByteArrayOutputStream();
					// PrintStream writer = new
					// PrintStream(byteArrayOutputStream);
					// e.printStackTrace(writer);
					// resultString = byteArrayOutputStream.toString();
					resultString = e.getMessage();
					logger.info("message: " + resultString);
					ModuleNode module = e.getModule();
					ASTNode node = e.getNode();
					int lineNumber = -1;
					if (module != null) {
						lineNumber = module.getLineNumber();
					} else if (node != null) {
						lineNumber = node.getLineNumber();
					} else {
						lineNumber = findLineNumberInString(resultString, lineNumber);
					}
					logger.info("Line number: " + lineNumber);
					if (lineNumber > 0
							&& lineNumber <= mScriptTextField.getLineCount()) {
						Element element3 = mScriptTextField.getDocument()
								.getDefaultRootElement();
						Element element4 = element3.getElement(lineNumber-1);
						if (element4 != null) {
							mScriptTextField.select(((int) element4
									.getStartOffset()), element4.getEndOffset());
						}						
					}
				} catch(Exception e){
					resultString = e.getMessage();
				}
				mScriptResultField.append(resultString);
			}
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

	public static class ScriptHolder {
		String mScript;

		String mScriptName;

		/**
		 * @param pScriptName
		 *            script name (starting with "script")
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

		String executeScript(int pIndex, PrintStream outStream);

		void storeDialogPositions(ScriptEditorPanel pPanel,
				ScriptEditorWindowConfigurationStorage pStorage,
				String pWindow_preference_storage_property);

		ScriptEditorWindowConfigurationStorage decorateDialog(
				ScriptEditorPanel pPanel,
				String pWindow_preference_storage_property);
        
        void endDialog(boolean pIsCanceled);
	}

	public ScriptEditorPanel(ScriptModel pScriptModel, FreeMindMain pFrame) {
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
				System.out.println("List selection:" + pEvent);
				if (pEvent.getValueIsAdjusting())
					return;
				select(mScriptList.getSelectedIndex());
			}
		});
		// add(mScriptList, BorderLayout.WEST);
		mScriptTextField = new JTextArea();
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
		updateFields();
		mScriptTextField.repaint();
		// menu:
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu(pFrame
				.getResourceString("plugins/ScriptEditor.menu_actions"));
		AbstractAction runAction = new RunAction(pFrame
				.getResourceString("plugins/ScriptEditor.run"));
		AbstractAction exitAction = new ExitAction(pFrame
				.getResourceString("plugins/ScriptEditor.exit"));

		AbstractAction[] actionList = new AbstractAction[] { runAction, exitAction };
		for (int i = 0; i < actionList.length; i++) {
			AbstractAction action = actionList[i];
			JMenuItem item = menu.add(action);
			item.setIcon(new BlindIcon(StructuredMenuHolder.ICON_SIZE));
		}
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		// Retrieve window size and column positions.
		ScriptEditorWindowConfigurationStorage storage = mScriptModel
				.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			mCentralUpperPanel.setDividerLocation(storage.getLeftRatio());
			mCentralPanel.setDividerLocation(storage.getTopRatio());
		}

	}

	private void updateFields() {
		mListModel.clear();
		for (int i = 0; i < mScriptModel.getAmountOfScripts(); ++i) {
			ScriptHolder script = mScriptModel.getScript(i);
			mListModel.addElement(script.getScriptName());
		}
	}

	private void select(int pIndex) {
		storeCurrent();
		// set new script
		mScriptTextField.setText(mScriptModel.getScript(pIndex).getScript());
		// set last one:
		mLastSelected = new Integer(pIndex);
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
	 * @param pIsCanceled TODO
	 * 
	 */
	private void disposeDialog(boolean pIsCanceled) {
		// store current script:
		if (!mScriptList.isSelectionEmpty()) {
			select(mScriptList.getSelectedIndex());
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

	public static int findLineNumberInString(String resultString, int lineNumber) {
		Pattern pattern = Pattern.compile(".*@ line ([0-9]+).*", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(resultString);
		if ( matcher.matches() ) {
			lineNumber = Integer.parseInt(matcher.group(1));
		}
		return lineNumber;
	}

}
