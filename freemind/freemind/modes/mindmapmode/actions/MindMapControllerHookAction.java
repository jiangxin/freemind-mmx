/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008 Christian Foltin and others.
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
 * Created on 10.01.2008
 */
/*$Id: MindMapControllerHookAction.java,v 1.1.2.1 2008/01/13 20:55:35 christianfoltin Exp $*/

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import freemind.modes.mindmapmode.MindMapController;

public class MindMapControllerHookAction extends AbstractAction implements
		HookAction {
	String mHookName;
	MindMapController mindMapController;

	public MindMapControllerHookAction(String hookName,
			MindMapController mindMapController) {
		super(hookName);
		this.mHookName = hookName;
		this.mindMapController = mindMapController;
	}

	public void actionPerformed(ActionEvent arg0) {
		mindMapController.createModeControllerHook(mHookName);
	}

	public String getHookName() {
		return mHookName;
	}

}