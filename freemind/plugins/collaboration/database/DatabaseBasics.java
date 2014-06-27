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

package plugins.collaboration.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import freemind.common.NumberProperty;
import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapTitleContributor;
import freemind.main.Resources;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.MapModule;

public abstract class DatabaseBasics extends MindMapNodeHookAdapter implements
		MapTitleContributor {

	public final static String SLAVE_HOOK_NAME = "plugins/collaboration/database/database_slave_plugin";
	public final static String SLAVE_STARTER_NAME = "plugins/collaboration/database/database_slave_starter_plugin";
	protected static final String ROW_PK = "PK";
	protected static final String ROW_ACTION = "do_action";
	protected static final String TABLE_XML_ACTIONS = "XmlActions";
	protected static final String TABLE_USERS = "Users";
	protected static final String ROW_UNDOACTION = "undo_action";
	protected static final String ROW_MAP = "map";
	protected static final String ROW_USER = "user";
	protected static final Integer ROLE_MASTER = Integer.valueOf(0);
	protected static final Integer ROLE_SLAVE = Integer.valueOf(1);
	private static final String PORT_PROPERTY = "plugins.collaboration.database.port";
	private static final String DATABASE_BASICS_CLASS = "plugins.collaboration.database.DatabaseBasics";

	protected static final String PASSWORD = DATABASE_BASICS_CLASS
			+ ".password";
	protected static final String PASSWORD_DESCRIPTION = DATABASE_BASICS_CLASS
			+ ".password.description";

	protected static final String PASSWORD_VERIFICATION = DATABASE_BASICS_CLASS
			+ ".password_verification";
	protected static final String PASSWORD_VERIFICATION_DESCRIPTION = DATABASE_BASICS_CLASS
			+ ".password_verification_description";

	protected static final String HOST = DATABASE_BASICS_CLASS + ".host";
	protected static final String HOST_DESCRIPTION = DATABASE_BASICS_CLASS
			+ ".host.description";

	protected static final String PORT = DATABASE_BASICS_CLASS + ".port";
	protected static final String PORT_DESCRIPTION = DATABASE_BASICS_CLASS
			+ ".port.description";

	protected static final String TITLE = DATABASE_BASICS_CLASS + ".title";

	protected static java.util.logging.Logger logger = null;
	protected UpdateThread mUpdateThread = null;

	public interface ResultHandler {
		void processResults(ResultSet rs);
	}

	public DatabaseBasics() {
		super();
	}

	/**
	 * @return ROLE_MASTER OR ROLE_SLAVE
	 */
	public abstract Integer getRole();

	public void startupMapHook() {
		super.startupMapHook();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		getMindMapController().getController()
				.registerMapTitleContributor(this);
	}

	public void shutdownMapHook() {
		Controller controller = getMindMapController().getController();
		controller.deregisterMapTitleContributor(this);
		controller.setTitle();
		super.shutdownMapHook();
	}

	protected static void togglePermanentHook(MindMapController controller) {
		MindMapNode rootNode = controller.getRootNode();
		List selecteds = Arrays.asList(new MindMapNode[] { rootNode });
		controller.addHook(rootNode, selecteds, SLAVE_HOOK_NAME, null);
	}

	protected void setPortProperty(final NumberProperty portProperty) {
		getMindMapController().getFrame().setProperty(PORT_PROPERTY,
				portProperty.getValue());
	}

	protected NumberProperty getPortProperty() {
		final NumberProperty portProperty = new NumberProperty(
				PORT_DESCRIPTION, PORT, 1024, 32767, 1);
		// fill values:
		portProperty.setValue(""
				+ getMindMapController().getFrame().getIntProperty(
						PORT_PROPERTY, 9001));
		return portProperty;
	}

	public void setUpdateThread(UpdateThread pUpdateThread) {
		mUpdateThread = pUpdateThread;
	}

	public UpdateThread getUpdateThread() {
		return mUpdateThread;
	}

	public String getMapTitle(String pOldTitle, MapModule pMapModule,
			MindMap pModel) {
		String title = pOldTitle;
		if (pMapModule.getModeController() != getMindMapController()) {
			return pOldTitle;
		}
		String userString = "";
		if (mUpdateThread != null) {
			try {
				boolean first = true;
				Vector users = mUpdateThread.getUsers();
				for (Iterator it = users.iterator(); it.hasNext();) {
					String user = (String) it.next();
					if (first)
						first = false;
					else
						userString += ", ";
					userString += user;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				freemind.main.Resources.getInstance().logException(e);

			}
		}
		return pOldTitle
				+ Resources.getInstance().format(
						TITLE,
						new Object[] { this.getRole(), this.getHost(),
								this.getPort(), userString });
	}

	public String getPort() {
		return mUpdateThread.getPort();
	}

	public String getHost() {
		return mUpdateThread.getHost();
	}

}
