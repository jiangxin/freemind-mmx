/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package accessories.plugins;

import javax.swing.Action;
import javax.swing.JMenuItem;

import accessories.plugins.ClonePasteAction.Registration;
import freemind.controller.MenuItemEnabledListener;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 11.10.2013
 */
public class DeactivateCloneAction extends MindMapNodeHookAdapter implements MenuItemEnabledListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		ClonePlugin hook = ClonePlugin.getHook(pNode);
		if (hook != null) {
			// has it been removed due to the deregister in the meantime?
			hook = ClonePlugin.getHook(pNode);
			if (hook != null) {
				hook.removeHook();
			}
		}

	}

	/* (non-Javadoc)
	 * @see freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem, javax.swing.Action)
	 */
	public boolean isEnabled(JMenuItem pItem, Action pAction) {
		return getRegistration().isEnabled(pItem, pAction); 
	}
	
	protected Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

}
