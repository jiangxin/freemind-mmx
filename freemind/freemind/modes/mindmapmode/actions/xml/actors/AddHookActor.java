/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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
*/

package freemind.modes.mindmapmode.actions.xml.actors;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.NodeChildParameter;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.DontSaveMarker;
import freemind.extensions.HookFactory;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.ViewAbstraction;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class AddHookActor extends XmlActorAdapter {

	protected static java.util.logging.Logger logger = null;
	/**
	 * @param pMapFeedback
	 */
	public AddHookActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}
	/**
	 */
	private HookFactory getHookFactory() {
		return getExMapFeedback().getHookFactory();
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
			execute(new ActionPair(doAction, undoAction));
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
				getExMapFeedback(), focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(
				getExMapFeedback(), focussed, selecteds);
		// test if hook already present
		if (instMethod.isAlreadyPresent(hookName, adaptedFocussedNode)) {
			// remove the hook:
			for (Iterator i = destinationNodes.iterator(); i.hasNext();) {
				MindMapNode currentDestinationNode = (MindMapNode) i.next();
				// find the hook in the current node, if present:
				for (Iterator j = currentDestinationNode.getActivatedHooks()
						.iterator(); j.hasNext();) {
					PermanentNodeHook hook = (PermanentNodeHook) j.next();
					if (hook.getName().equals(hookName)) {
						XMLElement child = new XMLElement();
						if(!(hook instanceof DontSaveMarker)) {
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
	public HookNodeAction createHookNodeAction(MindMapNode focussed,
			List selecteds, String hookName, Properties pHookProperties) {
		HookNodeAction hookNodeAction = new HookNodeAction();
		hookNodeAction.setNode(getNodeID(focussed));
		hookNodeAction.setHookName(hookName);
		// selectedNodes list
		for (Iterator i = selecteds.iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			NodeListMember nodeListMember = new NodeListMember();
			nodeListMember.setNode(getNodeID(node));
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
			MindMapNode selected = getNodeFromID(
					hookNodeAction.getNode());
			Vector selecteds = new Vector();
			for (Iterator i = hookNodeAction.getListNodeListMemberList()
					.iterator(); i.hasNext();) {
				NodeListMember node = (NodeListMember) i.next();
				selecteds.add(getNodeFromID(node.getNode()));
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
		execute(new ActionPair(undoAction, doAction));
	}

	private void invoke(MindMapNode focussed, List selecteds, String hookName,
			XMLElement pXmlParent) {
		logger.finest("invoke(selecteds) called.");
		HookInstanciationMethod instMethod = getInstanciationMethod(hookName);
		// get destination nodes
		Collection<MindMapNode> destinationNodes = instMethod.getDestinationNodes(
				getExMapFeedback(), focussed, selecteds);
		MindMapNode adaptedFocussedNode = instMethod.getCenterNode(
				getExMapFeedback(), focussed, selecteds);
		// test if hook already present
		if (instMethod.isAlreadyPresent(hookName, adaptedFocussedNode)) {
			// remove the hook:
			for (Iterator<MindMapNode> i = destinationNodes.iterator(); i.hasNext();) {
				MindMapNode currentDestinationNode = i.next();
				// find the hook ini the current node, if present:
				for (Iterator j = currentDestinationNode.getActivatedHooks()
						.iterator(); j.hasNext();) {
					PermanentNodeHook hook = (PermanentNodeHook) j.next();
					if (hook.getName().equals(hookName)) {
						currentDestinationNode.removeHook(hook);
						getExMapFeedback().nodeChanged(currentDestinationNode);
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
				NodeHook hook = getExMapFeedback().createNodeHook(hookName,
						currentDestinationNode);
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
						permHook.onFocusNode(getNodeView(currentDestinationNode));
					}
					// using this method, the map is dirty now. This is
					// important to
					// guarantee, that the hooks are saved.
					getExMapFeedback().nodeChanged(currentDestinationNode);
				}
			}
			finishInvocation(focussed, selecteds, adaptedFocussedNode,
					destinationNodes);
		}
	}

	/**
	 * @param pNode
	 * @return
	 */
	private NodeView getNodeView(MindMapNode pNode) {
		return getViewAbstraction().getNodeView(pNode);
	}

	protected ViewAbstraction getViewAbstraction() {
		ViewAbstraction viewAbstraction = getExMapFeedback().getViewAbstraction();
		if(viewAbstraction==null) {
			throw new IllegalArgumentException("View abstraction not available.");
		}
		return viewAbstraction;
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
		if (getViewAbstraction().getSelecteds().size() == 0) {
			// select all destination nodes:
			getExMapFeedback().select(focussed, selecteds);
		}
	}



}
