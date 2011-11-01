/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 31.12.2005
 */
/*$Id: HookFactoryAdapter.java,v 1.1.2.1.2.2 2006/07/25 20:28:20 christianfoltin Exp $*/
package freemind.extensions;

import java.util.HashMap;
import java.util.Iterator;

import freemind.modes.MindMapNode;

/**
 * @author foltin
 * 
 */
public abstract class HookFactoryAdapter implements HookFactory {

	/** Contains PluginType -> Object (baseClass) relations. */
	protected HashMap allRegistrationInstances;

	/**
	 * 
	 */
	protected HookFactoryAdapter() {
		super();
	}

	/**
	 * @return null if not present, the hook otherwise.
	 */
	public PermanentNodeHook getHookInNode(MindMapNode node, String hookName) {
		// search for already instanciated hooks of this type:
		for (Iterator i = node.getActivatedHooks().iterator(); i.hasNext();) {
			PermanentNodeHook otherHook = (PermanentNodeHook) i.next();
			if (otherHook.getName().equals(hookName)) {
				// there is already one instance.
				return otherHook;
			}
		}
		return null;
	}

	/**
	 * See getRegistrations. The registration makes sense for the factory, as
	 * the factory observes every object creation. <br>
	 * Moreover, the factory can tell other hooks it creates, who is its base
	 * plugin.
	 * 
	 */
	public void registerRegistrationContainer(
			HookFactory.RegistrationContainer container,
			HookRegistration instanciatedRegistrationObject) {
		// registration only for pluginBases.
		if (container.isPluginBase) {
			allRegistrationInstances.put(
					container.correspondingPlugin.getLabel(),
					instanciatedRegistrationObject);
		}
	}

	public void deregisterAllRegistrationContainer() {
		allRegistrationInstances.clear();
	}

}
