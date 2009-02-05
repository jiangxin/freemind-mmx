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
/* $Id: DatabaseStarter.java,v 1.1.2.4 2009-02-05 22:12:37 christianfoltin Exp $ */

package plugins.collaboration.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

import freemind.common.NumberProperty;
import freemind.common.StringProperty;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class DatabaseStarter extends DatabaseBasics implements PermanentNodeHook {

	private File mTempDbFile;

	/**
     *
     */

	public void startupMapHook() {
		super.startupMapHook();
		MindMapController controller = getMindMapController();
		final StringProperty passwordProperty = new StringProperty(
				"The password needed to connect", "Password");
		final StringProperty passwordProperty2 = new StringProperty(
				"Enter the password twice to make sure that it is correct.",
				"Password again");
		// StringProperty bindProperty = new StringProperty(
		// "IP address of the local machine, or 0.0.0.0 if ", "Host");
		final NumberProperty portProperty = getPortProperty();
		Vector controls = new Vector();
		controls.add(passwordProperty);
		controls.add(passwordProperty2);
		// controls.add(bindProperty);
		controls.add(portProperty);
		FormDialog dialog = new FormDialog(controller);
		dialog.setUp(controls, new FormDialogValidator() {
			public boolean isValid() {
				logger.info("Output valid?");
				return Tools.safeEquals(passwordProperty.getValue(),
						passwordProperty2.getValue());
			}
		});
		if (!dialog.isSuccess())
			return;
		String password = passwordProperty.getValue();
		setPortProperty(portProperty);
		// start server:
		logger.info("Start server...");
		try {
			mTempDbFile = File.createTempFile(
					"collaboration_database", ".hsqldb", new File(controller
							.getFrame().getFreemindDirectory()));
//			mTempDbFile.deleteOnExit();
			logger.info("Start server in file " + mTempDbFile);
			Thread server = new Thread(new Runnable() {

				public void run() {
					org.hsqldb.Server
							.main(new String[] { "-database.0",
									"file:" + mTempDbFile, "-dbname.0", "xdb",
									"-port", portProperty.getValue(),
									"-no_system_exit", "true" });
				}
			});
			server.start();
			Thread.sleep(1000);
			logger.info("Connect...");
			Class.forName("org.hsqldb.jdbcDriver");
			String url = "jdbc:hsqldb:hsql://localhost:"
					+ portProperty.getValue() + "/xdb";
			logger.info("Connecting to " + url);
			Connection connection = DriverManager.getConnection(url, "sa", "");
			mUpdateThread = new UpdateThread(connection, controller);
			mUpdateThread.setupTables(password);
			logger.info("Starting update thread...");
			mUpdateThread.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			// TODO: Need a better message here.
			controller.getController().errorMessage(e.getLocalizedMessage());
			if (mUpdateThread != null) {
				mUpdateThread.shutdown(true);
			}
			return;
		}
	}

	

	public void loadFrom(XMLElement pChild) {
		// this plugin should not be saved.
	}

	public void save(XMLElement pXml) {
		// this plugin should not be saved.
		// nothing to do.
	}

	public void shutdownMapHook() {
		if (mUpdateThread != null) {
			mUpdateThread.deregisterFilter();
			mUpdateThread.signalEndOfSession();
			mUpdateThread.commitSuicide();
			mUpdateThread.shutdown(true);
			// remove temporary files:
			logger.info("Remove temporary database files.");
			mTempDbFile.delete();
			new File(mTempDbFile.getAbsoluteFile()+".script").delete();
			new File(mTempDbFile.getAbsoluteFile()+".properties").delete();
		}
		super.shutdownMapHook();
	}

	public void onAddChild(MindMapNode pAddedChildNode) {
	}

	public void onAddChildren(MindMapNode pAddedChild) {
	}

	public void onDeselectHook(NodeView pNodeView) {
	}

	public void onNewChild(MindMapNode pNewChildNode) {
	}

	public void onRemoveChild(MindMapNode pOldChildNode) {
	}

	public void onRemoveChildren(MindMapNode pOldChildNode, MindMapNode pOldDad) {
	}

	public void onSelectHook(NodeView pNodeView) {
	}

	public void onUpdateChildrenHook(MindMapNode pUpdatedNode) {
	}

	public void onUpdateNodeHook() {
	}

	public void onViewCreatedHook(NodeView pNodeView) {
	}

	public void onViewRemovedHook(NodeView pNodeView) {
	}

}
