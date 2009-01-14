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
/* $Id: DatabaseStarter.java,v 1.1.2.2 2009-01-14 21:18:36 christianfoltin Exp $ */

package plugins.collaboration.database;

import java.io.File;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JOptionPane;

import plugins.collaboration.database.DatabaseBasics.FormDialog;
import freemind.common.NumberProperty;
import freemind.common.StringProperty;
import freemind.main.Tools;

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
		mController = getMindMapController();
		final StringProperty passwordProperty = new StringProperty(
				"The password needed to connect", "Password");
		final StringProperty passwordProperty2 = new StringProperty(
				"Enter the password twice to make sure that it is correct.", "Password again");
//		StringProperty bindProperty = new StringProperty(
//				"IP address of the local machine, or 0.0.0.0 if ", "Host");
		final NumberProperty portProperty = getPortProperty();
		Vector controls = new Vector();
		controls.add(passwordProperty);
		controls.add(passwordProperty2);
//		controls.add(bindProperty);
		controls.add(portProperty);
		FormDialog dialog = new FormDialog(mController);
		dialog.setUp(controls, new FormDialogValidator(){

			public boolean isValid() {
				return Tools.safeEquals(passwordProperty.getValue(), passwordProperty2.getValue());
			}});
		if(!dialog.isSuccess())
			return;
		String password = passwordProperty.getValue();
		setPortProperty(portProperty);
		// start server:
		logger.info("Start server...");
		try {
			final File tempDbFile = File.createTempFile("collaboration_database", ".hsqldb", new File(mController.getFrame().getFreemindDirectory()));
			tempDbFile.deleteOnExit();
			logger.info("Start server in directory " + tempDbFile);
			Thread server = new Thread(new Runnable() {
	
				public void run() {
					org.hsqldb.Server.main(new String[] { "-database.0",
							"file:"+tempDbFile, "-dbname.0", "xdb", "-no_system_exit", "-port", portProperty.getValue()});
				}
			});
			server.start();
			Thread.sleep(1000);
			logger.info("Connect...");
			Class.forName("org.hsqldb.jdbcDriver");
			mConnection = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
			// create tables
			logger.info("Create tables...");
			createTables(password);
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

	protected void createTables(String pPassword) throws SQLException {
		update("ALTER USER sa SET PASSWORD \"" + pPassword + "\"");
		update("DROP TABLE " + TABLE_XML_ACTIONS + " IF EXISTS");
		update("CREATE TABLE " + TABLE_XML_ACTIONS + " (" + ROW_PK
				+ " IDENTITY, " + ROW_ACTION + " VARCHAR, " + ROW_UNDOACTION
				+ " VARCHAR, " + ROW_MAP + " VARCHAR)");
	}

	public void shutdown() {
		try {
			if (mConnection != null) {
				Statement st = mConnection.createStatement();
				st.execute("SHUTDOWN");
				mConnection.close();
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}


}
