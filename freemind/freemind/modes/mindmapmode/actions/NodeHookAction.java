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

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.NodeChildParameter;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookFactory;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class NodeHookAction extends FreemindAction implements HookAction,
		ActorXml, MenuItemEnabledListener, MenuItemSelectedListener {
	String _hookName;
	MindMapController mMindMapController;

	public MindMapController getController() {
		return mMindMapController;
	}

	private static Logger logger;

	public NodeHookAction(String hookName, MindMapController controller) {
		super(hookName, (ImageIcon) null, null);
		this._hookName = hookName;
		this.mMindMapController = controller;
		if (logger == null)
			logger = controller.getFrame().getLogger(this.getClass().getName());
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent arg0) {
		// check, which method of invocation:
		//
		mMindMapController.getFrame().setWaitingCursor(true);
		invoke(mMindMapController.getSelected(),
				mMindMapController.getSelecteds());
		mMindMapController.getFrame().setWaitingCursor(false);
	}

	public void addHook(MindMapNode focussed, List selecteds, String hookName, Properties pHookProperties) {
		HookNodeAction doAction = createHookNodeAction(focussed, selecteds,
				hookName, pHookProperties);

		XmlAction undoAction = null;
		// this is the non operation:
		undoAction = new CompoundAction();
		if (getInstanciationMethod(hookName).isPermanent()) {
			// double application = remove.
			undoAction = createHookNodeUndoAction(focussed, selecteds, hookName);
		}
		if (getInstanciationMethod(hookName).isUndoable()) {
			getController().doTransaction(
					(String) getValue(NAME),
					new ActionPair(doAction, undoAction));
		} else {
			// direct invocation without undo and such stuff.
			invoke(focussed, selecteds, hookName, null);
		}
	}

	private XmlAction createHookNodeUndoAction(MindMapNode focussed,
			List selecteds, String hookName) {
		CompoundAction undoAction = new CompoundAction();
		HookNodeAction hookNodeAction = createHookNodeAction(focussed,
				selecteds, hookName, null);
		undoAction.addChoice(hookNodeAction);
		HookInstanciationMethod instMethod = getInstanciationMethod(hookName);
		// get destination nodes
		Collection destinationNodes = instMethod.getDestinationNodes(
				mMindMapController, focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(
				mMindMapController, focussed, selecteds);
		// test if hook already present
		if (instMethod.isAlreadyPresent(mMindMapController, hookName,
				adaptedFocussedNode)) {
			// remove the hook:
			for (Iterator i = destinationNodes.iterator(); i.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) i.next();
				// find the hook in the current node, if present:
				for (Iterator j = currentDestinationNode.getActivatedHooks()
						.iterator(); j.hasNext();) {
					PermanentNodeHook hook = (PermanentNodeHook) j.next();
					if (hook.getName().equals(hookName)) {
						XMLElement child = new XMLElement();
						hook.save(child);
						if (child.countChildren() == 1) {
							XMLElement parameters = (XMLElement) child
									.getChildren().firstElement();
							if (Tools.safeEquals(parameters.getName(),
									PermanentNodeHookAdapter.PARAMETERS)) {
								// standard save mechanism
								for (Iterator it = parameters
										.enumerateAttributeNames(); it
										.hasNext();) {
									String name = (String) it.next();
									NodeChildParameter nodeHookChild = new NodeChildParameter();
									nodeHookChild.setKey(name);
									nodeHookChild.setValue(parameters
											.getStringAttribute(name));
									hookNodeAction
											.addNodeChildParameter(nodeHookChild);
								}

							} else {
								logger.warning("Unusual save mechanism, implement me.");
							}
						} else {
							logger.warning("Unusual save mechanism, implement me.");
						}
						/*
						 * fc, 30.7.2004: we have to break. otherwise the
						 * collection is modified at two points (i.e., the
						 * collection is not valid anymore after removing one
						 * element). But this is no problem, as there exist only
						 * "once" plugins currently.
						 */
						break;
					}
				}
			}
		}
		return undoAction;
	}

	public void invoke(MindMapNode focussed, List selecteds) {
		addHook(focussed, selecteds, _hookName, null);
	}

	private void invoke(MindMapNode focussed, List selecteds, String hookName,
			XMLElement pXmlParent) {
		logger.finest("invoke(selecteds) called.");
		HookInstanciationMethod instMethod = getInstanciationMethod(hookName);
		// get destination nodes
		Collection destinationNodes = instMethod.getDestinationNodes(
				mMindMapController, focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(
				mMindMapController, focussed, selecteds);
		// test if hook already present
		if (instMethod.isAlreadyPresent(mMindMapController, hookName,
				adaptedFocussedNode)) {
			// remove the hook:
			for (Iterator i = destinationNodes.iterator(); i.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) i.next();
				// find the hook ini the current node, if present:
				for (Iterator j = currentDestinationNode.getActivatedHooks()
						.iterator(); j.hasNext();) {
					PermanentNodeHook hook = (PermanentNodeHook) j.next();
					if (hook.getName().equals(hookName)) {
						currentDestinationNode.removeHook(hook);
						mMindMapController.nodeChanged(currentDestinationNode);
						/*
						 * fc, 30.7.2004: we have to break. otherwise the
						 * collection is modified at two points (i.e., the
						 * collection is not valid anymore after removing one
						 * element). But this is no problem, as there exist only
						 * "once" plugins currently.
						 */
						break;
					}
				}
			}
		} else {
			// add the hook
			for (Iterator it = destinationNodes.iterator(); it.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) it.next();
				NodeHook hook = mMindMapController.createNodeHook(hookName,
						currentDestinationNode, mMindMapController.getMap());
				logger.finest("created hook " + hookName);
				// set parameters, if present
				if (pXmlParent != null && hook instanceof PermanentNodeHook) {
					((PermanentNodeHook) hook).loadFrom(pXmlParent);
				}
				// call invoke.
				currentDestinationNode.invokeHook(hook);
				if (hook instanceof PermanentNodeHook) {
					PermanentNodeHook permHook = (PermanentNodeHook) hook;
					logger.finest("This is a permanent hook " + hookName);
					// the focused receives the focus:
					if (currentDestinationNode == adaptedFocussedNode) {
						permHook.onFocusNode(mMindMapController
								.getNodeView(currentDestinationNode));
					}
					// using this method, the map is dirty now. This is
					// important to
					// guarantee, that the hooks are saved.
					mMindMapController.nodeChanged(currentDestinationNode);
				}
			}
			finishInvocation(focussed, selecteds, adaptedFocussedNode,
					destinationNodes);
		}
	}

	/**
	 * @param focussed
	 *            The real focussed node
	 * @param selecteds
	 *            The list of selected nodes
	 * @param adaptedFocussedNode
	 *            The calculated focussed node (if the hook specifies, that the
	 *            hook should apply to root, then this is the root node).
	 * @param destinationNodes
	 *            The calculated list of selected nodes (see last)
	 */
	private void finishInvocation(MindMapNode focussed, List selecteds,
			MindMapNode adaptedFocussedNode, Collection destinationNodes) {
		// restore selection only, if nothing selected.
		if (getController().getView().getSelecteds().size() == 0) {
			// select all destination nodes:
			getController().select(focussed, selecteds);
		}
	}

	/**
	 */
	private HookInstanciationMethod getInstanciationMethod(String hookName) {
		HookFactory factory = getHookFactory();
		// determine instanciation method
		HookInstanciationMethod instMethod = factory
				.getInstanciationMethod(hookName);
		return instMethod;
	}

	/**
	 */
	private HookFactory getHookFactory() {
		HookFactory factory = mMindMapController.getHookFactory();
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem
	 * , javax.swing.Action)
	 */
	public boolean isEnabled(JMenuItem item, Action action) {
		if (mMindMapController.getView() == null) {
			return false;
		}
		HookFactory factory = getHookFactory();
		Object baseClass = factory.getPluginBaseClass(_hookName);
		if (baseClass != null) {
			if (baseClass instanceof MenuItemEnabledListener) {
				MenuItemEnabledListener listener = (MenuItemEnabledListener) baseClass;
				return listener.isEnabled(item, action);
			}
		}

		return true;
	}

	public HookNodeAction createHookNodeAction(MindMapNode focussed,
			List selecteds, String hookName, Properties pHookProperties) {
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
		if(pHookProperties != null) {
			for (Iterator it = pHookProperties.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				NodeChildParameter nodeChildParameter = new NodeChildParameter();
				nodeChildParameter.setKey((String) entry.getKey());
				nodeChildParameter.setValue((String) entry.getValue());
				hookNodeAction.addNodeChildParameter(nodeChildParameter);
			}
		}
		return hookNodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof HookNodeAction) {
			HookNodeAction hookNodeAction = (HookNodeAction) action;
			MindMapNode selected = getController().getNodeFromID(
					hookNodeAction.getNode());
			Vector selecteds = new Vector();
			for (Iterator i = hookNodeAction.getListNodeListMemberList()
					.iterator(); i.hasNext();) {
				NodeListMember node = (NodeListMember) i.next();
				selecteds.add(getController().getNodeFromID(node.getNode()));
			}
			// reconstruct child-xml:
			XMLElement xmlParent = new XMLElement();
			xmlParent.setName(hookNodeAction.getHookName());
			XMLElement child = new XMLElement();
			xmlParent.addChild(child);
			child.setName(PermanentNodeHookAdapter.PARAMETERS);
			for (Iterator it = hookNodeAction.getListNodeChildParameterList()
					.iterator(); it.hasNext();) {
				NodeChildParameter childParameter = (NodeChildParameter) it
						.next();
				child.setAttribute(childParameter.getKey(),
						childParameter.getValue());
			}
			invoke(selected, selecteds, hookNodeAction.getHookName(), xmlParent);
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

	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		// test if plugin has its own method:
		HookFactory factory = getHookFactory();
		Object baseClass = factory.getPluginBaseClass(_hookName);
		if (baseClass != null) {
			if (baseClass instanceof MenuItemSelectedListener) {
				MenuItemSelectedListener listener = (MenuItemSelectedListener) baseClass;
				return listener.isSelected(pCheckItem, pAction);
			}
		}
		MindMapNode focussed = mMindMapController.getSelected();
		List selecteds = mMindMapController.getSelecteds();
		HookInstanciationMethod instMethod = getInstanciationMethod(_hookName);
		// get destination nodes
		Collection destinationNodes = instMethod.getDestinationNodes(
				mMindMapController, focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(
				mMindMapController, focussed, selecteds);
		// test if hook already present
		return instMethod.isAlreadyPresent(mMindMapController, _hookName,
				adaptedFocussedNode);

	}

	public void removeHook(MindMapNode pFocussed, List pSelecteds,
			String pHookName) {
		HookNodeAction undoAction = createHookNodeAction(pFocussed, pSelecteds,
				pHookName, null);

		XmlAction doAction = null;
		// this is the non operation:
		doAction = new CompoundAction();
		if (getInstanciationMethod(pHookName).isPermanent()) {
			// double application = remove.
			doAction = createHookNodeUndoAction(pFocussed, pSelecteds,
					pHookName);
		}
		getController().doTransaction(
				(String) getValue(NAME), new ActionPair(undoAction, doAction));
	}

}
