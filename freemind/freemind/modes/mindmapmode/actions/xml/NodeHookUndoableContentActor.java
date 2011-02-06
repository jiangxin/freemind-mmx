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
 * Created on 26.07.2004
 */
/* $Id: NodeHookUndoableContentActor.java,v 1.1.2.1 2007/06/05 20:53:31 dpolivaev Exp $ */
package freemind.modes.mindmapmode.actions.xml;

import java.util.Iterator;
import java.util.logging.Logger;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.HookContentNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.StatefulNodeHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;


public class NodeHookUndoableContentActor implements ActorXml {
	MindMapController controller;
	public MindMapController getController() {
		return controller;
	}
	
	private static Logger logger;
	public NodeHookUndoableContentActor(MindMapController controller) {
	    //URGENT: hookName must be translated!!
		this.controller = controller;
		if(logger == null)
			logger = controller.getFrame().getLogger(this.getClass().getName());
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}


	public void performAction(MindMapNode focussed, String hookName, String attributeName, String content) {
		String oldContent = findHook(focussed, hookName).getContent(attributeName);
		if(oldContent.equals(content)){
			return;
		}
		XmlAction doAction = createHookContentNodeAction(focussed, hookName, attributeName, content);
		XmlAction undoAction = createHookContentNodeAction(focussed, hookName, attributeName, oldContent);
		
		getController().getActionFactory().startTransaction(this.getClass().getName());
		getController().getActionFactory().executeAction(new ActionPair(doAction, undoAction));
		getController().getActionFactory().endTransaction(this.getClass().getName());
	}

	public void performAction(MindMapNode focussed, String hookName, String content) {
		performAction(focussed, hookName, null, content);
	}


	private void invoke(MindMapNode node, String hookName, String attributeName, String content) {
		logger.finest("invoke(selecteds) called.");
		findHook(node, hookName).setContent(attributeName, content);
	} 

	private StatefulNodeHook findHook(MindMapNode node, String hookName) {
		for (Iterator j = node.getActivatedHooks().iterator(); j
		.hasNext();) {
			final PermanentNodeHook permhook = (PermanentNodeHook) j.next();
			if(permhook instanceof StatefulNodeHook && permhook.getName().equals(hookName)) {
				final StatefulNodeHook hook = (StatefulNodeHook) permhook;
				return hook;
			}
		}
		throw new RuntimeException("hook not found");
	} 

	public HookContentNodeAction createHookContentNodeAction(MindMapNode focussed, String hookName, String attributeName, String content) {
	    HookContentNodeAction hookContentNodeAction = new HookContentNodeAction();
        hookContentNodeAction.setNode(focussed.getObjectId(getController()));
        hookContentNodeAction.setHookName(hookName);
        hookContentNodeAction.setAttributeName(attributeName);
        hookContentNodeAction.setContent(content);
        return hookContentNodeAction;
	}

	public void act(XmlAction action) {
        if (action instanceof HookContentNodeAction) {
            HookContentNodeAction hookContentNodeAction = (HookContentNodeAction) action;
            MindMapNode selected = getController().getNodeFromID(hookContentNodeAction.getNode());
            invoke(selected, hookContentNodeAction.getHookName(), hookContentNodeAction.getAttributeName(), hookContentNodeAction.getContent());
        }
    }

    public Class getDoActionClass() {
        return HookContentNodeAction.class;
    }
}
