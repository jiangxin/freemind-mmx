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
 * Created on 22.07.2004
 */
/*$Id: HookInstanciationMethod.java,v 1.1.4.1.16.3 2007/06/05 20:53:30 dpolivaev Exp $*/
package freemind.extensions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class HookInstanciationMethod {
	private static interface DestinationNodesGetter {
		Collection getDestinationNodes(ModeController controller,
				MindMapNode focussed, List selecteds);

		MindMapNode getCenterNode(ModeController controller,
				MindMapNode focussed, List selecteds);
	}

	private static class DefaultDestinationNodesGetter implements
			DestinationNodesGetter {
		public Collection getDestinationNodes(ModeController controller,
				MindMapNode focussed, List selecteds) {
			return selecteds;
		}

		public MindMapNode getCenterNode(ModeController controller,
				MindMapNode focussed, List selecteds) {
			return focussed;
		}
	}

	private static class RootDestinationNodesGetter implements
			DestinationNodesGetter {
		public Collection getDestinationNodes(ModeController controller,
				MindMapNode focussed, List selecteds) {
			Vector returnValue = new Vector();
			returnValue.add(controller.getMap().getRoot());
			return returnValue;
		}

		public MindMapNode getCenterNode(ModeController controller,
				MindMapNode focussed, List selecteds) {
			return controller.getMap().getRootNode();
		}
	}

	private static class AllDestinationNodesGetter implements
			DestinationNodesGetter {
		private void addChilds(MindMapNode node, Collection allNodeCollection) {
			allNodeCollection.add(node);
			for (Iterator i = node.childrenFolded(); i.hasNext();) {
				MindMapNode child = (MindMapNode) i.next();
				addChilds(child, allNodeCollection);
			}
		}

		public Collection getDestinationNodes(ModeController controller,
				MindMapNode focussed, List selecteds) {
			Vector returnValue = new Vector();
			addChilds(controller.getMap().getRootNode(), returnValue);
			return returnValue;
		}

		public MindMapNode getCenterNode(ModeController controller,
				MindMapNode focussed, List selecteds) {
			return focussed;
		}

	}

	private boolean isSingleton;
	private DestinationNodesGetter getter;
	private final boolean isPermanent;
	private final boolean isUndoable;

	public boolean isSingleton() {
		return isSingleton;
	}

	/**
	 * @return Returns the isPermanent.
	 */
	public boolean isPermanent() {
		return isPermanent;
	}

	private HookInstanciationMethod(boolean isPermanent, boolean isSingleton,
			DestinationNodesGetter getter, boolean isUndoable) {
		this.isPermanent = isPermanent;
		this.isSingleton = isSingleton;
		this.getter = getter;
		this.isUndoable = isUndoable;
	}

	static final public HookInstanciationMethod Once = new HookInstanciationMethod(
			true, true, new DefaultDestinationNodesGetter(), true);
	/** The hook should only be added/removed to the root node. */
	static final public HookInstanciationMethod OnceForRoot = new HookInstanciationMethod(
			true, true, new RootDestinationNodesGetter(), true);
	/** Each (or none) node should have the hook. */
	static final public HookInstanciationMethod OnceForAllNodes = new HookInstanciationMethod(
			true, true, new AllDestinationNodesGetter(), true);
	/**
	 * This is for MindMapHooks in general. Here, no undo- or redoaction are
	 * performed, the undo information is given by the actions the hook
	 * performs.
	 */
	static final public HookInstanciationMethod Other = new HookInstanciationMethod(
			false, false, new DefaultDestinationNodesGetter(), false);
	/**
	 * This is for MindMapHooks that wish to be applied to root, whereevery they
	 * are called from. Here, no undo- or redoaction are performed, the undo
	 * information is given by the actions the hook performs.
	 */
	static final public HookInstanciationMethod ApplyToRoot = new HookInstanciationMethod(
			false, false, new RootDestinationNodesGetter(), false);

	static final public HashMap getAllInstanciationMethods() {
		HashMap res = new HashMap();
		res.put("Once", Once);
		res.put("OnceForRoot", OnceForRoot);
		res.put("OnceForAllNodes", OnceForAllNodes);
		res.put("Other", Other);
		res.put("ApplyToRoot", ApplyToRoot);
		return res;
	}

	/**
	 */
	public Collection getDestinationNodes(ModeController controller,
			MindMapNode focussed, List selecteds) {
		return getter.getDestinationNodes(controller, focussed, selecteds);
	}

	/**
	 */
	public boolean isAlreadyPresent(ModeController controller, String hookName,
			MindMapNode focussed) {
		for (Iterator i = focussed.getActivatedHooks().iterator(); i.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) i.next();
			if (hookName.equals(hook.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 */
	public MindMapNode getCenterNode(ModeController controller,
			MindMapNode focussed, List selecteds) {
		return getter.getCenterNode(controller, focussed, selecteds);
	}

	/**
     */
	public boolean isUndoable() {
		return isUndoable;
	}
}
