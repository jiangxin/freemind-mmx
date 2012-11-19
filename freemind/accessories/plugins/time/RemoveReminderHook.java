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
 * Created on 02.05.2005
 */

package accessories.plugins.time;

import java.util.Arrays;
import java.util.List;

import freemind.modes.MindMapNode;
import freemind.modes.common.plugins.ReminderHookBase;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * 
 */
public class RemoveReminderHook extends MindMapNodeHookAdapter {

	/**
	 *
	 */
	public RemoveReminderHook() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		ReminderHookBase hook = TimeManagementOrganizer.getHook(node);
		if (hook != null) {
			List selected = Arrays.asList(new MindMapNode[] { node });
			// adding the hook the second time, it is removed.
			getMindMapController().addHook(node, selected,
					TimeManagement.REMINDER_HOOK_NAME, null);
		}
	}
}
