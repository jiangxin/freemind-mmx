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
/* $Id: JumpLastEditLocation.java,v 1.1.4.3.2.3 2007/08/12 08:03:07 dpolivaev Exp $ */
package accessories.plugins;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FoldAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionHandler;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * This plugin stores the location of last edit taken place in order to jump to
 * it on keystroke.
 * 
 * @author foltin
 */
public class JumpLastEditLocation extends MindMapNodeHookAdapter {


	public JumpLastEditLocation(){
		
	}
	
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		JumpLastEditLocationRegistration base = (JumpLastEditLocationRegistration) getPluginBaseClass();
		
		String lastEditLocation = base.getLastEditLocation();
		if(lastEditLocation == null) {
			return;
		}
		try {
			MindMapNode node = getMindMapController().getNodeFromID(
					lastEditLocation);
			this.logger.info("Selecting " + node + " as last edit location.");
			Vector nodes = new Vector();
			nodes.add(node);
			getMindMapController().selectMultipleNodes(node, nodes);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}
	
	
	public static class JumpLastEditLocationRegistration implements HookRegistration, ActionHandler {

		private MindMapController controller;

		private MindMap mMap;

		private Logger logger;

		private String mLastEditLocation = null;

		public String getLastEditLocation() {
			return mLastEditLocation;
		}


		public JumpLastEditLocationRegistration(ModeController controller, MindMap map) {
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
			} else if ((doAction instanceof NodeAction) && ! (doAction instanceof FoldAction)) {
				mLastEditLocation = ((NodeAction) doAction).getNode();
				logger.info("Last edit location: " + mLastEditLocation);
			}
	
		}

		public void startTransaction(String name) {
		}
	
		public void endTransaction(String name) {
		}
	}
}
