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
/* $Id: NodeHookAction.java,v 1.1.2.2.2.2 2006-07-25 20:28:21 christianfoltin Exp $ */
package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookFactory;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;


public class NodeHookAction extends FreemindAction implements ActorXml, MenuItemEnabledListener {
	String _hookName;
	MindMapController controller;
	public MindMapController getController() {
		return controller;
	}
	private static Logger logger;
	public NodeHookAction(String hookName, MindMapController controller) {
	    //URGENT: hookName must be translated!!
		super(hookName, (ImageIcon) null, null);
		this._hookName = hookName;
		this.controller = controller;
		if(logger == null)
			logger = controller.getFrame().getLogger(this.getClass().getName());
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent arg0) {
		// check, which method of invocation:
		//
	    controller.getFrame().setWaitingCursor(true);
		invoke(controller.getSelected(), controller.getSelecteds());
	    controller.getFrame().setWaitingCursor(false);
	}



	public void addHook(MindMapNode focussed, List selecteds, String hookName) {
	    HookNodeAction doAction = createHookNodeAction(focussed, selecteds, hookName);

        XmlAction undoAction=null;
        // this is the non operation:
        undoAction = new CompoundAction();
	    if (getInstanciationMethod(hookName).isPermanent()) {
            // double application = remove.
            undoAction = createHookNodeAction(focussed,
                    selecteds, hookName);
        }
	    if (getInstanciationMethod(hookName).isUndoable()) {
	        getController().getActionFactory().startTransaction((String) getValue(NAME));
			getController().getActionFactory().executeAction(new ActionPair(doAction, undoAction));
	        getController().getActionFactory().endTransaction((String) getValue(NAME));
        } else {
            // direct invocation without undo and such stuff.
            invoke(focussed, selecteds, hookName);
        }
	}

	public void invoke(MindMapNode focussed, List selecteds) {
	    addHook(focussed, selecteds, _hookName);
	}

	private void invoke(MindMapNode focussed, List selecteds, String hookName) {
		logger.finest("invoke(selecteds) called.");
		HookInstanciationMethod instMethod = getInstanciationMethod(hookName);
		// get destination nodes
		Collection destinationNodes = instMethod.getDestinationNodes(controller, focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(controller, focussed, selecteds);
		// test if hook already present
		if(instMethod.isAlreadyPresent(controller, hookName, adaptedFocussedNode, destinationNodes)){
			// remove the hook:
			for (Iterator i = destinationNodes.iterator(); i.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) i.next();
				// find the hook ini the current node, if present:
				for (Iterator j = currentDestinationNode.getActivatedHooks().iterator(); j
						.hasNext();) {
					PermanentNodeHook hook = (PermanentNodeHook) j.next();
					if(hook.getName().equals(hookName)) {
						currentDestinationNode.removeHook(hook);
						controller.nodeChanged(currentDestinationNode);
						/* fc, 30.7.2004:
						 * we have to break. otherwise the collection is modified
						 * at two points (i.e., the collection is not valid anymore after removing
						 * one element).
						 * But this is no problem, as there exist only "once" plugins currently.
						 */
						break;
					}
				}
			}
		} else {
			// add the hook
			for (Iterator it = destinationNodes.iterator(); it.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) it.next();
				NodeHook hook = controller
						.createNodeHook(hookName, currentDestinationNode, controller.getMap());
				logger.finest("created hook "+hookName);
				// call invoke.
				currentDestinationNode.invokeHook(hook);
				if (hook instanceof PermanentNodeHook) {
					PermanentNodeHook permHook = (PermanentNodeHook) hook;
					logger.finest("This is a permanent hook "+ hookName);
					// the focussed receives the focus:
					if (currentDestinationNode == adaptedFocussedNode) {
						permHook.onReceiveFocusHook();
					}
					// using this method, the map is dirty now. This is important to
					// guarantee, that the hooks are saved.
					controller.nodeChanged(currentDestinationNode);
				}
			}
			finishInvocation(focussed, selecteds, adaptedFocussedNode, destinationNodes);
		}
	}


	/**
	 * @param focussed The real focussed node
	 * @param selecteds The list of selected nodes
	 * @param adaptedFocussedNode The calculated focussed node (if the hook specifies, that
	 * the hook should apply to root, then this is the root node).
	 * @param destinationNodes The calculated list of selected nodes (see last)
	 */
	private void finishInvocation(MindMapNode focussed, List selecteds,
			MindMapNode adaptedFocussedNode, Collection destinationNodes) {
			// select all destination nodes:
			// fc, 25.8.2004: The following code snippet should be moved to a more general place.
			if (focussed.getViewer() != null) {
				getController().getView().selectAsTheOnlyOneSelected(
						focussed.getViewer());
				getController().getView().scrollNodeToVisible(
						focussed.getViewer());
				for (Iterator i = selecteds.iterator(); i.hasNext();) {
					MindMapNode node = (MindMapNode) i.next();
					if(node.getViewer() != null) {
						getController().getView().makeTheSelected(node.getViewer());
					}
				}
			}
		}



	/**
	 */
	private HookInstanciationMethod getInstanciationMethod(String hookName) {
		HookFactory factory = getHookFactory();
		// determine instanciation method
		HookInstanciationMethod instMethod = factory.getInstanciationMethod(hookName);
		return instMethod;
	}

	/**
	 */
	private HookFactory getHookFactory() {
		HookFactory factory = controller.getHookFactory();
		return factory;
	}

	/* (non-Javadoc)
	 * @see freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem, javax.swing.Action)
	 */
	public boolean isEnabled(JMenuItem item, Action action) {
		// test if plugin has its own method:
		HookFactory factory = getHookFactory();
		Object baseClass = factory.getPluginBaseClass(_hookName);
		if(baseClass != null) {
			if (baseClass instanceof MenuItemEnabledListener) {
				MenuItemEnabledListener listener = (MenuItemEnabledListener) baseClass;
				return listener.isEnabled(item, this);
			}
		}
		MindMapNode focussed = controller.getSelected();
		List selecteds = controller.getSelecteds();
		HookInstanciationMethod instMethod = getInstanciationMethod(_hookName);
		// get destination nodes
		Collection destinationNodes = instMethod.getDestinationNodes(controller, focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(controller, focussed, selecteds);
		// test if hook already present
		boolean isActionSelected = instMethod.isAlreadyPresent(controller, _hookName, adaptedFocussedNode, destinationNodes);
		setSelected(item, isActionSelected);

		return true;
	}

	public HookNodeAction createHookNodeAction(MindMapNode focussed, List selecteds, String hookName) {
	    HookNodeAction hookNodeAction = new HookNodeAction();
        hookNodeAction.setNode(focussed.getObjectId(getController()));
        hookNodeAction.setHookName(hookName);
        // selectedNodes list
        for (Iterator i = selecteds.iterator(); i.hasNext();) {
            MindMapNode node = (MindMapNode) i.next();
            NodeListMember nodeListMember = new NodeListMember();
            nodeListMember.setNode(node.getObjectId(getController()));
            hookNodeAction.addNodeListMember(nodeListMember);
        }
        return hookNodeAction;
	}

	public void act(XmlAction action) {
        if (action instanceof HookNodeAction) {
            HookNodeAction hookNodeAction = (HookNodeAction) action;
            MindMapNode selected = getController().getNodeFromID(hookNodeAction.getNode());
            Vector selecteds = new Vector();
            for (Iterator i = hookNodeAction.getListNodeListMemberList().iterator(); i.hasNext();) {
                NodeListMember node = (NodeListMember) i.next();
                selecteds.add(getController().getNodeFromID(node.getNode()));
            }
            invoke(selected, selecteds, hookNodeAction.getHookName());
        }
    }

    public Class getDoActionClass() {
        return HookNodeAction.class;
    }

	/**
	 */
	public String getHookName() {
		return _hookName;
	}

}
