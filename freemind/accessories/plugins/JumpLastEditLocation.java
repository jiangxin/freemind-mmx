/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 11.11.2010
 */

package accessories.plugins;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FoldAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.actions.xml.ActionHandler;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * This plugin stores the location of last edit taken place in order to jump to
 * it on keystroke.
 * 
 * @author foltin
 */
public class JumpLastEditLocation extends MindMapNodeHookAdapter {

	public JumpLastEditLocation() {

	}

	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		try {
			JumpLastEditLocationRegistration base = (JumpLastEditLocationRegistration) getPluginBaseClass();
			MindMapNode node = base.getLastEditLocation(pNode);
			if (node == null) {
				return;
			}
			this.logger.fine("Selecting " + node + " as last edit location.");
			getMindMapController().select(node,
					Tools.getVectorWithSingleElement(node));
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public static class JumpLastEditLocationRegistration implements
			HookRegistration, ActionHandler, MenuItemEnabledListener {

		private static final String PLUGIN_NAME = "accessories/plugins/JumpLastEditLocation.properties";

		private MindMapController controller;

		private MindMap mMap;

		private Logger logger;

		private Vector mLastEditLocations = new Vector();

		public MindMapNode getLastEditLocation(MindMapNode pCurrentNode) {
			int size = mLastEditLocations.size();
			if (size == 0) {
				return null;
			}
			// search for the current node inside the vector:
			String id = controller.getNodeID(pCurrentNode);
			int index = mLastEditLocations.lastIndexOf(id);
			do {
				if(index < 0) {
					// current node not present, we start with the last position:
					index = size -1;
				} else {
					index = index - 1;
					if (index < 0) {
						index = 0;
					}
				}
				id = (String) mLastEditLocations.elementAt(index);
				try {
					pCurrentNode = controller.getNodeFromID(id);
					return pCurrentNode;
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			} while(index > 0);
			return null;
		}

		public JumpLastEditLocationRegistration(ModeController controller,
				MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			controller.getActionFactory().registerHandler(this);
		}

		public void deRegister() {
			controller.getActionFactory().deregisterHandler(this);
		}

		public void executeAction(XmlAction action) {
			// detect format changes:
			detectFormatChanges(action);
		}

		/**
		 */
		private void detectFormatChanges(XmlAction doAction) {
			if (doAction instanceof CompoundAction) {
				CompoundAction compAction = (CompoundAction) doAction;
				for (Iterator i = compAction.getListChoiceList().iterator(); i
						.hasNext();) {
					XmlAction childAction = (XmlAction) i.next();
					detectFormatChanges(childAction);
				}
			} else if ((doAction instanceof NodeAction)
					&& !(doAction instanceof FoldAction)) {
				// remove myself:
				if (doAction instanceof HookNodeAction) {
					HookNodeAction hookAction = (HookNodeAction) doAction;
					if (Tools.safeEquals(hookAction.getHookName(), PLUGIN_NAME)) {
						return;
					}
				}
				String lastLocation = ((NodeAction) doAction).getNode();
				if (doAction instanceof NewNodeAction) {
					NewNodeAction newNodeAction = (NewNodeAction) doAction;
					lastLocation = newNodeAction.getNewId();
				}
				// prevent double entries
				if (mLastEditLocations.size() > 0
						&& Tools.safeEquals(lastLocation,
								mLastEditLocations.lastElement())) {
					return;
				}
				mLastEditLocations.add(lastLocation);
				if (mLastEditLocations.size() > 10) {
					mLastEditLocations.remove(0);
				}
				try {
					logger.fine("New last edit location: " + lastLocation
							+ " from " + controller.marshall(doAction));
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
					logger.warning("Not able to marshall the action "
							+ doAction.getClass() + " as " + doAction);
				}
			}

		}

		public void startTransaction(String name) {
		}

		public void endTransaction(String name) {
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			String hookName = ((NodeHookAction) pAction).getHookName();
			if (PLUGIN_NAME.equals(hookName)) {
				// back is only enabled if there are already some nodes to go
				// back ;-)
				return !mLastEditLocations.isEmpty();
			}
			return true;
		}
	}
}
