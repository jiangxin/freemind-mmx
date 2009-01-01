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
/* $Id: DatabaseConnector.java,v 1.1.2.1 2009-01-01 21:33:48 christianfoltin Exp $ */

package plugins.collaboration.database;

import java.sql.DriverManager;

/**
 * @author foltin
 * 
 */
public class DatabaseConnector extends DatabaseBasics  {

	/**
     *
     */

	public void startupMapHook() {
		super.startupMapHook();
		try {
			logger.info("Connect...");
			Class.forName("org.hsqldb.jdbcDriver");
			mConnection = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
			mController = getMindMapController();
			logger.info("Starting update thread...");
			mUpdateThread = new Thread(this);
			mUpdateThread.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return;
		}
	}


}
