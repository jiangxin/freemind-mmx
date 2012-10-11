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

package freemind.modes.browsemode;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import freemind.extensions.HookFactoryAdapter;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHookSubstituteUnknown;
import freemind.modes.common.plugins.MapNodePositionHolderBase;
import freemind.modes.common.plugins.ReminderHookBase;

/**
 * @author foltin
 * 
 */
public class BrowseHookFactory extends HookFactoryAdapter {

	/**
	 *
	 */
	public BrowseHookFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookFactory#getPossibleNodeHooks()
	 */
	public Vector getPossibleNodeHooks() {
		return new Vector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookFactory#getPossibleModeControllerHooks()
	 */
	public Vector getPossibleModeControllerHooks() {
		return new Vector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.HookFactory#createModeControllerHook(java.lang.String
	 * )
	 */
	public ModeControllerHook createModeControllerHook(String hookName) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookFactory#createNodeHook(java.lang.String)
	 */
	public NodeHook createNodeHook(String hookName) {
		// System.out.println("create node hook:"+hookName);
		NodeHook hook;
		if (hookName.equals(ReminderHookBase.PLUGIN_LABEL)) {
			hook = new BrowseReminderHook();
		} else if (hookName.equals(MapNodePositionHolderBase.NODE_MAP_HOOK_NAME)) {
			hook = new MapNodePositionHolderBase();
		} else {
			hook = new PermanentNodeHookSubstituteUnknown(hookName);
		}
		// decorate hook.
		hook.setProperties(new Properties());
		hook.setName(hookName);
		hook.setPluginBaseClass(null);
		return hook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.HookFactory#getHookMenuPositions(java.lang.String)
	 */
	public List getHookMenuPositions(String hookName) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.HookFactory#getInstanciationMethod(java.lang.String)
	 */
	public HookInstanciationMethod getInstanciationMethod(String hookName) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookFactory#getRegistrations()
	 */
	public List getRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookFactory#getPluginBaseClass(java.lang.String)
	 */
	public Object getPluginBaseClass(String hookName) {
		// TODO Auto-generated method stub
		return null;
	}

}
