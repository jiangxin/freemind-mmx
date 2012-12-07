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

package freemind.modes;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import freemind.modes.mindmapmode.MindMapController;

public class NodeDownAction extends AbstractAction {
	private final MindMapController modeController;

	public NodeDownAction(MindMapController adapter) {
		super(adapter.getText("node_down"));
		this.modeController = adapter;
	}

	public void actionPerformed(ActionEvent e) {
		MindMapNode selected = modeController.getSelected();
		List selecteds = modeController.getSelecteds();
		modeController.moveNodes(selected, selecteds, 1);
		modeController.select(selected, selecteds);

	}
}