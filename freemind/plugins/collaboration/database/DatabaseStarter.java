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
/* $Id: DatabaseStarter.java,v 1.1.2.1 2009-01-01 21:33:48 christianfoltin Exp $ */

package plugins.collaboration.database;

import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author foltin
 * 
 */
public class DatabaseStarter extends DatabaseBasics  {

	/**
     *
     */

	public void startupMapHook() {
		super.startupMapHook();
		// start server:
		logger.info("Start server...");
		Thread server = new Thread(new Runnable() {

			public void run() {
				org.hsqldb.Server.main(new String[] { "-database.0",
						"file:mydb", "-dbname.0", "xdb", "-no_system_exit"});
			}
		});
		server.start();
		try {
			Thread.sleep(1000);
			logger.info("Connect...");
			Class.forName("org.hsqldb.jdbcDriver");
			mConnection = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
			mController = getMindMapController();
			// create tables
			logger.info("Create tables...");
			createTables();
			// register as listener:
			mController.getActionFactory().registerFilter(this);
			// send first action:
			logger.info("Store map in database...");
			StringWriter writer = new StringWriter();
			mController.getMap().getXml(writer);
			String expression = "INSERT INTO " + TABLE_XML_ACTIONS + "("
					+ ROW_PK + "," + ROW_MAP + ") VALUES(" + mPrimaryKey
					+ ", '" + escapeQuotations(writer.toString()) + "')";
			insertIntoActionTable(expression);
			logger.info("Starting update thread...");
			mUpdateThread = new Thread(this);
			mUpdateThread.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			shutdown();
			return;
		}
	}

	protected void createTables() throws SQLException {
		update("DROP TABLE " + TABLE_XML_ACTIONS + " IF EXISTS");
		update("CREATE TABLE " + TABLE_XML_ACTIONS + " (" + ROW_PK
				+ " IDENTITY, " + ROW_ACTION + " VARCHAR, " + ROW_UNDOACTION
				+ " VARCHAR, " + ROW_MAP + " VARCHAR)");
	}

	public void shutdown() {
		try {
			Statement st = mConnection.createStatement();
			st.execute("SHUTDOWN");
			mConnection.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}


}
