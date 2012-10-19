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
 * Created on 23.09.2004
 */

package freemind.modes.mindmapmode.actions.xml;

import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.RedoAction;
import freemind.modes.mindmapmode.actions.UndoAction;

public class UndoActionHandler {
	private MindMapController controller;
	private final UndoAction undo;
	private final RedoAction redo;

	/**
     */
	public UndoActionHandler(MindMapController adapter, UndoAction undo,
			RedoAction redo) {
		this.controller = adapter;
		this.undo = undo;
		this.redo = redo;
	}

	public void executeAction(ActionPair pair) {
		if (!controller.isUndoAction()) {
			redo.clear();
			undo.add(pair);
			// undo.print();
			undo.setEnabled(true);
			redo.setEnabled(false);
		}
	}

	public void startTransaction(String name) {
	}

	public void endTransaction(String name) {
	}
}
