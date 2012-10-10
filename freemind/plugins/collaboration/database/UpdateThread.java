/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2009 Christian Foltin and others.
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
 * Created on 03.02.2009
 */
/*$Id: UpdateThread.java,v 1.1.2.1 2009/02/04 19:31:21 christianfoltin Exp $*/

package plugins.collaboration.database;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import plugins.collaboration.database.DatabaseBasics.ResultHandler;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionFilter.FinalActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class UpdateThread extends Thread implements ResultHandler,
		FinalActionFilter {
	private static final String QUERY_GET_USERS = "SELECT * FROM "
			+ DatabaseBasics.TABLE_USERS;
	private static final String QUERY = "SELECT * FROM "
			+ DatabaseBasics.TABLE_XML_ACTIONS + " WHERE "
			+ DatabaseBasics.ROW_PK + " >= ?";
	private boolean mShouldTerminate = false;
	private boolean mIsTerminated = false;
	protected Connection mConnection = null;
	protected BigInteger mPrimaryKeyMutex = BigInteger.valueOf(0);
	protected long mPrimaryKey = 1l;
	protected MindMapController mController;
	protected boolean mFilterEnabled = true;
	private static java.util.logging.Logger logger = null;
	private PreparedStatement mPrepareStatement;
	private PreparedStatement mPrepareStatementUsers = null;
	protected String mPort;

	public String getPort() {
		return mPort;
	}

	public void setPort(String pPort) {
		mPort = pPort;
	}

	public String getHost() {
		return mHost;
	}

	public void setHost(String pHost) {
		mHost = pHost;
	}

	protected String mHost;

	public UpdateThread(Connection pConnection, MindMapController pController)
			throws SQLException {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mConnection = pConnection;
		mController = pController;
	}

	public void run() {
		try {
			mPrepareStatement = mConnection.prepareStatement(QUERY);
		} catch (SQLException e1) {
			freemind.main.Resources.getInstance().logException(e1);
			return;
		}
		int counter = 1;
		while (!mShouldTerminate) {
			try {
				logger.fine("Looking for updates...");
				synchronized (mPrimaryKeyMutex) {
					mPrepareStatement.setLong(1, mPrimaryKey);
					logger.fine("Looking for updates... Query");
					query(mPrepareStatement, this);
				}
				logger.fine("Looking for updates... Done.");
				Thread.sleep(1000);
				counter--;
				if (counter <= 0) {
					counter = 10;
					mController.getController().setTitle();
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		mIsTerminated = true;
	}

	public void commitSuicide() {
		mShouldTerminate = true;
		int timeout = 100;
		logger.info("Shutting down update thread.");
		while (!mIsTerminated && timeout-- > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		if (timeout == 0) {
			logger.warning("Can't stop update thread!");
		} else {
			logger.info("Shutting down update thread complete.");
		}
	}

	public void shutdown(boolean pWithShutdown) {
		try {
			if (pWithShutdown) {
				Statement st = mConnection.createStatement();
				st.execute("SHUTDOWN");
			}
			mConnection.close();
			mConnection = null;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public void processResults(ResultSet rs) {
		try {
			while (rs.next()) {
				long nextPk = rs.getLong(DatabaseBasics.ROW_PK);
				mPrimaryKey = nextPk + 1;
				String doAction = rs.getString(DatabaseBasics.ROW_ACTION);
				String undoAction = rs.getString(DatabaseBasics.ROW_UNDOACTION);
				String map = rs.getString(DatabaseBasics.ROW_MAP);
				logger.info("Got the following from database: " + nextPk + ", "
						+ doAction + ", " + undoAction + ", " + map);
				if (doAction != null && undoAction != null) {
					XmlAction xmlDoAction = mController.unMarshall(doAction);
					XmlAction xmlUndoAction = mController
							.unMarshall(undoAction);
					ActionPair pair = new ActionPair(xmlDoAction, xmlUndoAction);
					executeTransaction(pair);
				} else if (map != null) {
					createNewMap(map);
				} else {
					logger.info("Shutting down was signalled.");
					rs.close();
					// session has ended.
					DatabaseBasics.togglePermanentHook(mController);
					// and end.
					return;
				}
			}
			rs.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	private void createNewMap(String map) throws IOException {
		{
			// deregister from old controller:
			deregisterFilter();
			logger.info("Restoring the map...");
			MindMapController newModeController = (MindMapController) mController
					.getMode().createModeController();
			MapAdapter newModel = new MindMapMapModel(mController.getFrame(),
					newModeController);
			HashMap IDToTarget = new HashMap();
			StringReader reader = new StringReader(map);
			MindMapNodeModel rootNode = (MindMapNodeModel) newModeController
					.createNodeTreeFromXml(reader, IDToTarget);
			reader.close();
			newModel.setRoot(rootNode);
			rootNode.setMap(newModel);
			mController.newMap(newModel);
			newModeController.invokeHooksRecursively((NodeAdapter) rootNode,
					newModel);
			mController = newModeController;
			// add new hook
			DatabaseBasics.togglePermanentHook(mController);
			// tell him about this thread.
			Collection activatedHooks = mController.getRootNode()
					.getActivatedHooks();
			for (Iterator it = activatedHooks.iterator(); it.hasNext();) {
				PermanentNodeHook hook = (PermanentNodeHook) it.next();
				if (hook instanceof DatabaseConnectionHook) {
					DatabaseConnectionHook connHook = (DatabaseConnectionHook) hook;
					connHook.setUpdateThread(this);
					break;
				}
			}
			/* register as listener, as I am a slave. */
			registerFilter();
		}
	}

	private void executeTransaction(final ActionPair pair)
			throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mFilterEnabled = false;
				try {
					mController.doTransaction("update", pair);
				} finally {
					mFilterEnabled = true;
				}
			}
		});
	}

	protected void insertIntoActionTable(String expression) throws SQLException {
		synchronized (mPrimaryKeyMutex) {
			update(expression);
			mPrimaryKey++;
		}
	}

	public synchronized void query(PreparedStatement preparedStatement,
			ResultHandler pHandler) throws SQLException {

		Statement st = null;
		ResultSet rs = null;

		boolean execute = preparedStatement.execute();
		if (execute) {
			rs = preparedStatement.getResultSet();
			pHandler.processResults(rs);
		}
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
		String expression = "INSERT INTO " + DatabaseBasics.TABLE_XML_ACTIONS
				+ "(" + DatabaseBasics.ROW_PK + "," + DatabaseBasics.ROW_ACTION
				+ "," + DatabaseBasics.ROW_UNDOACTION + ") VALUES("
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

	protected void deregisterFilter() {
		logger.info("Deregistering filter");
		mController.getActionFactory().deregisterFilter(this);
	}

	protected void registerFilter() {
		logger.info("Registering filter");
		mController.getActionFactory().registerFilter(this);
	}

	public void setupTables(String password) throws Exception {
		// create tables
		logger.info("Create tables...");
		createTables(password);
		insertUser();
		// register as listener:
		registerFilter();
		// send first action:
		logger.info("Store map in database...");
		StringWriter writer = new StringWriter();
		mController.getMap().getXml(writer);
		String expression = "INSERT INTO " + DatabaseBasics.TABLE_XML_ACTIONS
				+ "(" + DatabaseBasics.ROW_PK + "," + DatabaseBasics.ROW_MAP
				+ ") VALUES(" + mPrimaryKey + ", '"
				+ escapeQuotations(writer.toString()) + "')";
		insertIntoActionTable(expression);
	}

	void insertUser() throws SQLException {
		update("INSERT INTO " + DatabaseBasics.TABLE_USERS + "("
				+ DatabaseBasics.ROW_USER + ") VALUES('"
				+ escapeQuotations(getUserName()) + "')");
	}

	public void removeUser() throws SQLException {
		update("DELETE FROM " + DatabaseBasics.TABLE_USERS + " WHERE "
				+ DatabaseBasics.ROW_USER + " = '"
				+ escapeQuotations(getUserName()) + "'");

	}

	private String getUserName() {
		// Get host name
		String hostname = Tools.getHostName();

		return System.getProperty("user.name") + "@" + hostname;
	}

	protected void createTables(String pPassword) throws SQLException {
		update("ALTER USER sa SET PASSWORD \"" + pPassword + "\"");
		dropTable(DatabaseBasics.TABLE_XML_ACTIONS);
		dropTable(DatabaseBasics.TABLE_USERS);
		update("CREATE TABLE " + DatabaseBasics.TABLE_XML_ACTIONS + " ("
				+ DatabaseBasics.ROW_PK + " IDENTITY, "
				+ DatabaseBasics.ROW_ACTION + " VARCHAR, "
				+ DatabaseBasics.ROW_UNDOACTION + " VARCHAR, "
				+ DatabaseBasics.ROW_MAP + " VARCHAR)");
		update("CREATE TABLE " + DatabaseBasics.TABLE_USERS + " ("
				+ DatabaseBasics.ROW_USER + " VARCHAR)");
	}

	private boolean dropTable(String tableName) throws SQLException {
		return update("DROP TABLE " + tableName + " IF EXISTS");
	}

	public void signalEndOfSession() {
		// signal end of session:
		String expression = "INSERT INTO " + DatabaseBasics.TABLE_XML_ACTIONS
				+ "(" + DatabaseBasics.ROW_PK + ") VALUES(" + mPrimaryKey + ")";
		try {
			insertIntoActionTable(expression);
			// and wait until the others should have shut down.
			Thread.sleep(2000);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public Vector getUsers() throws SQLException {
		if (mPrepareStatementUsers == null) {
			mPrepareStatementUsers = mConnection
					.prepareStatement(QUERY_GET_USERS);
		}
		Vector result = new Vector();

		boolean execute = mPrepareStatementUsers.execute();
		if (!execute) {
			return result;
		}
		ResultSet rs = mPrepareStatementUsers.getResultSet();
		try {
			while (rs.next()) {
				result.add(rs.getString(DatabaseBasics.ROW_USER));
			}
			rs.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		return result;
	}

}