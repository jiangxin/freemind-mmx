/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2009  Christian Foltin and others.
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
 * Created on 28.01.2009
 */
/*$Id: DatabaseRegistration.java,v 1.1.2.1 2009-02-04 19:31:21 christianfoltin Exp $*/

package plugins.collaboration.database;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MenuItemSelectedListener;
import freemind.extensions.HookRegistration;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;

public class DatabaseRegistration implements HookRegistration, MenuItemSelectedListener, MenuItemEnabledListener {

	
    private final MindMapController mController;
    private final java.util.logging.Logger logger;
	private MindMap mMap;

	public DatabaseRegistration(ModeController controller, MindMap map) {
		this.mController = (MindMapController) controller;
		mMap = map;
		logger = controller.getFrame().getLogger(this.getClass().getName());
	}
	
	public void register() {
		logger.fine("Registration of database registration.");
	}
	
	public void deRegister() {
		logger.fine("Deregistration of database registration.");
	}
	
   	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
   		logger.info(this+" is asked for " + pAction + ".");
   		if (pAction instanceof NodeHookAction) {
			NodeHookAction action = (NodeHookAction) pAction;
			if(action.getHookName().equals(DatabaseConnector.SLAVE_STARTER_NAME)){
				return isSlave();
			}
		}
   		return isMaster();
	}

	private boolean isMaster() {
		Collection activatedHooks = mController.getRootNode().getActivatedHooks();
   		for (Iterator it = activatedHooks.iterator(); it
   		.hasNext();) {
   			PermanentNodeHook hook = (PermanentNodeHook) it.next();
   			if (hook instanceof DatabaseStarter) {
   				return true;
   			}
   		}
   		return false;
	}

	private boolean isSlave() {
		Collection activatedHooks = mController.getRootNode().getActivatedHooks();
		for (Iterator it = activatedHooks.iterator(); it
				.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) it.next();
			if (hook instanceof DatabaseConnectionHook) {
				return true;
			}
		}
		return false;
	}

   	/** 
   	 * When one option is enabled, the other is impossible.
   	 * */
	public boolean isEnabled(JMenuItem pItem, Action pAction) {
		logger.info(this+" is asked for " + pAction + ".");
		if (pAction instanceof NodeHookAction) {
			NodeHookAction action = (NodeHookAction) pAction;
			if(action.getHookName().equals(DatabaseConnector.SLAVE_STARTER_NAME)){
				return !isMaster();
			}
		}
		return !isSlave();
	}
}
