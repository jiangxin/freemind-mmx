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
 * Created on 25.08.2004
 */

package accessories.plugins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FormatNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionHandler;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * This plugin formats new nodes using the formats given to former nodes.
 * 
 * @author foltin
 */
public class FormatNewNodes implements ActionHandler, ActionFilter,
		HookRegistration {

	private MindMapController controller;

	private MindMap mMap;

	private Logger logger;

	private HashMap formatActions;

	public FormatNewNodes(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		mMap = map;
		logger = controller.getFrame().getLogger(this.getClass().getName());
		this.formatActions = new HashMap();
	}

	public void register() {
		controller.getActionFactory().registerHandler(this);
		controller.getActionFactory().registerFilter(this);

	}

	public void deRegister() {
		controller.getActionFactory().deregisterHandler(this);
		controller.getActionFactory().deregisterFilter(this);
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
		} else if (doAction instanceof FormatNodeAction) {
			formatActions.put(doAction.getClass().getName(), doAction);
		}

	}

	public void startTransaction(String name) {
	}

	public void endTransaction(String name) {
	}

	public ActionPair filterAction(ActionPair pair) {
		if (pair.getDoAction() instanceof NewNodeAction) {
			NewNodeAction newNodeAction = (NewNodeAction) pair.getDoAction();
			// add to a compound the newNodeAction and the other formats we
			// have:
			CompoundAction compound = new CompoundAction();
			compound.addChoice(newNodeAction);
			for (Iterator i = formatActions.values().iterator(); i.hasNext();) {
				NodeAction formatAction = (NodeAction) i.next();
				// deep copy:
				FormatNodeAction copiedFormatAction = (FormatNodeAction) Tools
						.deepCopy(formatAction);
				copiedFormatAction.setNode(newNodeAction.getNewId());
				compound.addChoice(copiedFormatAction);
			}
			ActionPair newPair = new ActionPair(compound, pair.getUndoAction());
			return newPair;
		}
		return pair;
	}

}
