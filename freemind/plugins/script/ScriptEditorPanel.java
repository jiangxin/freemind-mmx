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
/*$Id: ScriptEditorPanel.java,v 1.1.2.2 2007-01-17 23:12:05 dpolivaev Exp $*/
package plugins.script;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import freemind.controller.BlindIcon;
import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.view.mindmapview.MapView;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author foltin
 * 
 */
public class ScriptEditorPanel extends JDialog {
	private final FreeMindMain mFrame;

	private final ScriptModel mScriptModel;

	private JList mScriptList;

	private JTextArea mScriptTextField;

	private DefaultListModel mListModel;

	private Integer mLastSelected = null;

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
		
		String executeScript(int pIndex);
	}

	public ScriptEditorPanel(ScriptModel pScriptModel, FreeMindMain pFrame) {
		super(pFrame.getJFrame(), true /* modal */);
		mScriptModel = pScriptModel;
		mFrame = pFrame;
		// build the panel:
		this.setTitle(pFrame
				.getResourceString("plugins/ScriptEditor/window.title"));
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog();
			}
		});
		Tools.addEscapeActionToDialog(this, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				disposeDialog();
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
		// mScriptTextField.setSize(new Dimension(500,50));
		// add(mScriptTextField, BorderLayout.CENTER);
		JSplitPane centralPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				mScriptList, new JScrollPane(mScriptTextField));
		centralPanel.setContinuousLayout(true);
		contentPane.add(centralPanel);
		updateFields();
		mScriptTextField.repaint();
		// menu:
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu(pFrame
				.getResourceString("plugins/ScriptEditor.menu_actions"));
		AbstractAction runAction = new AbstractAction(pFrame
				.getResourceString("plugins/ScriptEditor.run")) {
			public void actionPerformed(ActionEvent arg0) {
				// do something
				storeCurrent();
				if (!mScriptList.isSelectionEmpty()) {
					mScriptModel.executeScript(mScriptList.getSelectedIndex());
				}				
			}
		};
		;
		AbstractAction[] actionList = new AbstractAction[] { runAction };
		for (int i = 0; i < actionList.length; i++) {
			AbstractAction action = actionList[i];
			JMenuItem item = menu.add(action);
			item.setIcon(new BlindIcon(StructuredMenuHolder.ICON_SIZE));
		}
		menuBar.add(menu);
		this.setJMenuBar(menuBar);

	}

	private void updateFields() {
		mScriptList.removeAll();
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
	 * 
	 */
	private void disposeDialog() {
		// store current script:
		if (!mScriptList.isSelectionEmpty()) {
			select(mScriptList.getSelectedIndex());
		}
		// store window positions:

		// TimeWindowConfigurationStorage storage = new
		// TimeWindowConfigurationStorage();
		// for(int i = 0; i< timeTable.getColumnCount(); i++) {
		// TimeWindowColumnSetting setting = new TimeWindowColumnSetting();
		// setting.setColumnWidth(timeTable.getColumnModel().getColumn(i).getWidth());
		// setting.setColumnSorting(sorter.getSortingStatus(i));
		// storage.addTimeWindowColumnSetting(setting);
		// }
		// getMindMapController().storeDialogPositions(dialog, storage,
		// WINDOW_PREFERENCE_STORAGE_PROPERTY);
		this.setVisible(false);
		this.dispose();
	}

	public static void main(String[] args) {
		ScriptEditorPanel scriptEditor = new ScriptEditorPanel(
				new ScriptModel() {
					String[] scripts = {"for(i=0;i<6;++i){print i;}",
							"for(i=0;i<5;++i){print i;}"};

					public int getAmountOfScripts() {
						// TODO Auto-generated method stub
						return 2;
					}

					public ScriptHolder getScript(int pIndex) {
							return new ScriptHolder("script"+pIndex, scripts[pIndex]);
					}

					public void setScript(int pIndex, ScriptHolder pScript) {
						scripts[pIndex] = pScript.getScript();
					}

					public String executeScript(int pIndex) {
						Binding binding = new Binding();
						binding.setVariable("c", null);
						binding.setVariable("node", null);
						GroovyShell shell = new GroovyShell(binding);

						String script = getScript(pIndex).getScript();
						Object value = shell.evaluate(script);
						return value.toString();
					}
				}, new FreeMindMain() {

					public void err(String pMsg) {
						// TODO Auto-generated method stub

					}

					public String getAdjustableProperty(String pLabel) {
						// TODO Auto-generated method stub
						return null;
					}

					public Container getContentPane() {
						// TODO Auto-generated method stub
						return null;
					}

					public Controller getController() {
						// TODO Auto-generated method stub
						return null;
					}

					public ClassLoader getFreeMindClassLoader() {
						// TODO Auto-generated method stub
						return null;
					}

					public MenuBar getFreeMindMenuBar() {
						// TODO Auto-generated method stub
						return null;
					}

					public String getFreemindBaseDir() {
						// TODO Auto-generated method stub
						return null;
					}

					public String getFreemindDirectory() {
						// TODO Auto-generated method stub
						return null;
					}

					public String getFreemindVersion() {
						// TODO Auto-generated method stub
						return null;
					}

					public int getIntProperty(String pKey, int pDefaultValue) {
						// TODO Auto-generated method stub
						return 0;
					}

					public JFrame getJFrame() {
						// TODO Auto-generated method stub
						return null;
					}

					public JLayeredPane getLayeredPane() {
						// TODO Auto-generated method stub
						return null;
					}

					public Logger getLogger(String pForClass) {
						// TODO Auto-generated method stub
						return null;
					}

					public File getPatternsFile() {
						// TODO Auto-generated method stub
						return null;
					}

					public Properties getProperties() {
						// TODO Auto-generated method stub
						return null;
					}

					public String getProperty(String pKey) {
						// TODO Auto-generated method stub
						return null;
					}

					public URL getResource(String pName) {
						// TODO Auto-generated method stub
						return null;
					}

					public String getResourceString(String pKey) {
						// TODO Auto-generated method stub
						return pKey;
					}

					public String getResourceString(String pKey,
							String pDefaultResource) {
						// TODO Auto-generated method stub
						return null;
					}

					public ResourceBundle getResources() {
						// TODO Auto-generated method stub
						return null;
					}

					public JPanel getSouthPanel() {
						// TODO Auto-generated method stub
						return null;
					}

					public JSplitPane getSplitPane() {
						// TODO Auto-generated method stub
						return null;
					}

					public MapView getView() {
						// TODO Auto-generated method stub
						return null;
					}

					public Container getViewport() {
						// TODO Auto-generated method stub
						return null;
					}

					public int getWinHeight() {
						// TODO Auto-generated method stub
						return 0;
					}

					public int getWinState() {
						// TODO Auto-generated method stub
						return 0;
					}

					public int getWinWidth() {
						// TODO Auto-generated method stub
						return 0;
					}

					public boolean isApplet() {
						// TODO Auto-generated method stub
						return false;
					}

					public void openDocument(URL pLocation) throws Exception {
						// TODO Auto-generated method stub

					}

					public void out(String pMsg) {
						// TODO Auto-generated method stub

					}

					public void repaint() {
						// TODO Auto-generated method stub

					}

					public void saveProperties() {
						// TODO Auto-generated method stub

					}

					public void setProperty(String pKey, String pValue) {
						// TODO Auto-generated method stub

					}

					public void setTitle(String pTitle) {
						// TODO Auto-generated method stub

					}

					public void setView(MapView pView) {
						// TODO Auto-generated method stub

					}

					public void setWaitingCursor(boolean pWaiting) {
						// TODO Auto-generated method stub

					}

                    public void setDefaultProperty(String key, String value) {
                        // TODO Auto-generated method stub

                    }
				});
		scriptEditor.pack();
		scriptEditor.setVisible(true);
	}
}
