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
 * Created on 21.08.2004
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class NodeUpAction extends MindmapAction  {
	private final MindMapController modeController;
	private static Logger logger;

	public NodeUpAction(MindMapController modeController) {
		super("node_up", modeController);
		this.modeController = modeController;
		if (logger == null) {
			logger = modeController.getFrame().getLogger(
					this.getClass().getName());
		}
	}

	public void actionPerformed(ActionEvent e) {
		MindMapNode selected = modeController.getSelected();
		List selecteds = modeController.getSelecteds();
		modeController.moveNodes(selected, selecteds, -1);
		modeController.select(selected, selecteds);
	}

}
