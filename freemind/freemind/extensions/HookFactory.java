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
/*$Id: HookFactory.java,v 1.1.4.9.2.1 2006/07/25 20:28:20 christianfoltin Exp $*/
package freemind.extensions;

import java.util.List;
import java.util.Vector;

import freemind.controller.actions.generated.instance.Plugin;
import freemind.modes.MindMapNode;

public interface HookFactory {

	public static class RegistrationContainer {
		public Class hookRegistrationClass;

		public boolean isPluginBase;

		public Plugin correspondingPlugin;

		public RegistrationContainer() {
		}
	}

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public abstract Vector getPossibleNodeHooks();

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public abstract Vector getPossibleModeControllerHooks();

	public abstract ModeControllerHook createModeControllerHook(String hookName);

	/**
	 * Do not call this method directly. Call ModeController.createNodeHook
	 * instead.
	 */
	public abstract NodeHook createNodeHook(String hookName);

	/**
	 * @return null if not present, the hook otherwise.
	 */
	public abstract PermanentNodeHook getHookInNode(MindMapNode node,
			String hookName);

	/**
	 * @return returns a list of menu position strings for the
	 *         StructuredMenuHolder.
	 */
	public abstract List getHookMenuPositions(String hookName);

	/**
	 */
	public abstract HookInstanciationMethod getInstanciationMethod(
			String hookName);

	/**
	 * Each Plugin can have a list of HookRegistrations that are called after
	 * the corresponding mode is enabled. (Like singletons.) One of these can
	 * operate as the pluginBase that is accessible to every normal
	 * plugin_action via the getPluginBaseClass method.
	 * 
	 * @return A list of RegistrationContainer elements. The field
	 *         hookRegistrationClass of RegistrationContainer is a class that is
	 *         (probably) of HookRegistration type. You have to register every
	 *         registration via the registerRegistrationContainer method when
	 *         instanciated (this is typically done in the ModeController).
	 */
	public abstract List getRegistrations();

	/**
	 * See getRegistrations. The registration makes sense for the factory, as
	 * the factory observes every object creation. <br>
	 * Moreover, the factory can tell other hooks it creates, who is its base
	 * plugin.
	 * 
	 */
	public abstract void registerRegistrationContainer(
			HookFactory.RegistrationContainer container,
			HookRegistration instanciatedRegistrationObject);

	public abstract void deregisterAllRegistrationContainer();

	/**
	 * A plugin base class is a common registration class of multiple plugins.
	 * It is useful to embrace several related plugins (example: EncryptedNote
	 * -> Registration).
	 * 
	 * @return the base class if declared and successfully instanciated or NULL.
	 */
	public abstract Object getPluginBaseClass(String hookName);

}
