/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 28.12.2008
 */
/* $Id: DatabaseBasics.java,v 1.1.2.3 2009-01-14 21:18:36 christianfoltin Exp $ */
package plugins.collaboration.database;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import freemind.common.NumberProperty;
import freemind.common.PropertyControl;
import freemind.common.StringProperty;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;

public class DatabaseBasics extends MindMapHookAdapter implements ActionFilter,
		Runnable {

	abstract class ResultHandler {
		abstract void processResults(ResultSet rs);
	}

	protected static final String ROW_PK = "PK";
	protected static final String ROW_ACTION = "do_action";
	protected static final String TABLE_XML_ACTIONS = "XmlActions";
	protected static final String ROW_UNDOACTION = "undo_action";
	protected static final String ROW_MAP = "map";
	private static final String PORT_PROPERTY = "plugins.collaboration.database.port";
	protected static java.util.logging.Logger logger = null;
	protected Connection mConnection = null;
	protected BigInteger mPrimaryKeyMutex = BigInteger.valueOf(0);
	protected long mPrimaryKey = 1l;
	protected MindMapController mController;
	protected Thread mUpdateThread;
	protected boolean mFilterEnabled = true;

	public DatabaseBasics() {
		super();
	}

	public void startupMapHook() {
		super.startupMapHook();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}

	protected void insertIntoActionTable(String expression) throws SQLException {
		synchronized (mPrimaryKeyMutex) {
			update(expression);
			mPrimaryKey++;
		}
	}

	public synchronized void query(String expression, ResultHandler pHandler)
			throws SQLException {

		Statement st = null;
		ResultSet rs = null;

		st = mConnection.createStatement();
		rs = st.executeQuery(expression);

		pHandler.processResults(rs);
		st.close();
	}

	public synchronized boolean update(String expression) throws SQLException {
		logger.info("Executing " + expression);
		Statement st = null;
		st = mConnection.createStatement(); // statements
		int i = st.executeUpdate(expression); // run the query
		if (i == -1) {
			logger.severe("db error : " + expression);
		}
		st.close();
		return i != -1;
	}

	public ActionPair filterAction(ActionPair pPair) {
		if (pPair == null || !mFilterEnabled)
			return pPair;
		String doAction = mController.marshall(pPair.getDoAction());
		String undoAction = mController.marshall(pPair.getUndoAction());
		String expression = "INSERT INTO " + TABLE_XML_ACTIONS + "(" + ROW_PK
				+ "," + ROW_ACTION + "," + ROW_UNDOACTION + ") VALUES("
				+ mPrimaryKey + ", '" + escapeQuotations(doAction) + "', '"
				+ escapeQuotations(undoAction) + "')";
		try {
			insertIntoActionTable(expression);
		} catch (SQLException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		return pPair;
	}

	protected String escapeQuotations(String pInput) {
		return pInput.replaceAll("'", "''");
	}

	public void run() {
		while (true) {
			try {
				logger.info("Looking for updates...");
				synchronized (mPrimaryKeyMutex) {
					String query = "SELECT * FROM " + TABLE_XML_ACTIONS
							+ " WHERE " + ROW_PK + " >= " + mPrimaryKey;
					logger.info("Looking for updates... Query");
					query(query, new ResultHandler() {
						void processResults(ResultSet pRs) {
							try {
								while (pRs.next()) {
									long nextPk = pRs.getLong(ROW_PK);
									mPrimaryKey = nextPk + 1;
									String doAction = pRs.getString(ROW_ACTION);
									String undoAction = pRs
											.getString(ROW_UNDOACTION);
									String map = pRs.getString(ROW_MAP);
									logger
											.info("Got the following from database: "
													+ nextPk
													+ ", "
													+ doAction
													+ ", "
													+ undoAction
													+ ", "
													+ map);
									if (doAction != null && undoAction != null) {
										XmlAction xmlDoAction = mController
												.unMarshall(doAction);
										XmlAction xmlUndoAction = mController
												.unMarshall(undoAction);
										ActionPair pair = new ActionPair(
												xmlDoAction, xmlUndoAction);
										executeTransaction(pair);
									} else if (map != null) {
										logger.info("Restoring the map...");
										MindMapController newModeController = (MindMapController) mController
												.getMode()
												.createModeController();
										MapAdapter newModel = new MindMapMapModel(
												mController.getFrame(),
												newModeController);
										HashMap IDToTarget = new HashMap();
										StringReader reader = new StringReader(
												map);
										MindMapNodeModel rootNode = (MindMapNodeModel) newModeController
												.createNodeTreeFromXml(reader,
														IDToTarget);
										reader.close();
										newModel.setRoot(rootNode);
										rootNode.setMap(newModel);
										mController.newMap(newModel);
										newModeController
												.invokeHooksRecursively(
														(NodeAdapter) rootNode,
														newModel);
										// deregister from old controller:
										mController.getActionFactory()
												.deregisterFilter(
														DatabaseBasics.this);
										mController = newModeController;
										// register as listener, as I am a
										// slave.
										mController.getActionFactory()
												.registerFilter(
														DatabaseBasics.this);
									}
								}
							} catch (Exception e) {
								freemind.main.Resources.getInstance()
										.logException(e);
							}
						}

						private void executeTransaction(final ActionPair pair)
								throws InterruptedException,
								InvocationTargetException {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									mFilterEnabled = false;
									try {
										mController.getActionFactory()
												.startTransaction("update");
										mController.getActionFactory()
												.executeAction(pair);
										mController.getActionFactory()
												.endTransaction("update");
									} finally {
										mFilterEnabled = true;
									}
								}
							});
						}
					});
				}
				logger.info("Looking for updates... Done.");
				Thread.sleep(1000);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}

	}

	protected void setPortProperty(final NumberProperty portProperty) {
		mController.getFrame().setProperty(PORT_PROPERTY,
				portProperty.getValue());
	}

	protected NumberProperty getPortProperty() {
		final NumberProperty portProperty = new NumberProperty(
				"The port to open", "Port", 1024, 32767, 1);
		// fill values:
		portProperty.setValue(""
				+ mController.getFrame().getIntProperty(PORT_PROPERTY, 9001));
		return portProperty;
	}

	public static abstract class FormDialogValidator {
		/**
		 * @return true, if ok should be enabled.
		 */
		public abstract boolean isValid();
	}
	public static class FormDialog extends JDialog {
		private final MindMapController mController2;
		private boolean mSuccess = false;

		public boolean isSuccess() {
			return mSuccess;
		}

		public FormDialog(MindMapController pController) {
			super(pController.getFrame().getJFrame());
			mController2 = pController;
		}

		public void setUp(Vector controls) {
			setUp(controls, new FormDialogValidator(){

				public boolean isValid() {
					return true;
				}});
		}
		
		public void setUp(Vector controls, FormDialogValidator pValidator) {
			setModal(true);
			getContentPane().setLayout(new BorderLayout());
			FormLayout formLayout = new FormLayout(
					"right:max(40dlu;p), 4dlu, 80dlu, 7dlu", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
			builder.setDefaultDialogBorder();
			for (Iterator it = controls.iterator(); it.hasNext();) {
				PropertyControl prop = (PropertyControl) it.next();
				prop.layout(builder, mController2);
				
			}
			getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
			JButton cancelButton = new JButton(getText("Cancel"));
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}

			});
			JButton okButton = new JButton(getText("OK"));
			okButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					mSuccess = true;
					closeWindow();
				}

			});
			getRootPane().setDefaultButton(okButton);
			getContentPane().add(
					ButtonBarFactory.buildOKCancelBar(cancelButton, okButton),
					BorderLayout.SOUTH);
			setTitle("Enter Password Dialog");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					closeWindow();
				}
			});
			Action action = new AbstractAction() {

				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}
			};
			Action actionSuccess = new AbstractAction() {
				
				public void actionPerformed(ActionEvent arg0) {
					mSuccess = true;
					closeWindow();
				}
			};
			Tools.addEscapeActionToDialog(this, action);
		    Tools.addKeyActionToDialog(this, actionSuccess, "RETURN", "ok_dialog");

			pack();
			setVisible(true);

		}

		private void closeWindow() {
			setVisible(false);
		}

		String getText(String text) {
			return text;
		}

	}

}