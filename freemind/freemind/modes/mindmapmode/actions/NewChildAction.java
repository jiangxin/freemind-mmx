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
 * Created on 05.05.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.NodeView;

public class NewChildAction extends MindmapAction  {
	private final MindMapController c;
	private static Logger logger = null;

	public NewChildAction(MindMapController modeController) {
		super("new_child", "images/idea.png", modeController);
		this.c = modeController;
		if (logger == null) {
			logger = c.getFrame().getLogger(NewChildAction.class.getName());
		}
	}
	
	MindMapController getModeController() {
		return c;
	}

	public void actionPerformed(ActionEvent e) {
		this.c.addNew(c.getSelected(), MindMapController.NEW_CHILD, null);
	}

	public MindMapNode addNew(final MindMapNode target, int newNodeMode,
			final KeyEvent e) {
		final MindMapNode targetNode = target;
		MindMapNode newNode = null;

		switch (newNodeMode) {
		case MindMapController.NEW_SIBLING_BEFORE:
		case MindMapController.NEW_SIBLING_BEHIND: {
			if (!targetNode.isRoot()) {
				MindMapNode parent = targetNode.getParentNode();
				int childPosition = parent.getChildPosition(targetNode);
				if (newNodeMode == MindMapController.NEW_SIBLING_BEHIND) {
					childPosition++;
				}
				newNode = getModeController().addNewNode(parent, childPosition, targetNode.isLeft());
				final NodeView nodeView = getModeController().getNodeView(newNode);
				getModeController().select(nodeView);
				getModeController().edit.edit(nodeView, getModeController().getNodeView(target), e, true, false,
						false);
				break;
			} else {
				// fc, 21.8.07: we don't do anything here and get a new child
				// instead.
				newNodeMode = MindMapController.NEW_CHILD;
				// @fallthrough
			}
		}

		case MindMapController.NEW_CHILD:
		case MindMapController.NEW_CHILD_WITHOUT_FOCUS: {
			final boolean parentFolded = targetNode.isFolded();
			if (parentFolded) {
				getModeController().setFolded(targetNode, false);
			}
			int position = c.getProperty("placenewbranches")
					.equals("last") ? targetNode.getChildCount() : 0;
			newNode = addNewNode(targetNode, position);
			final NodeView nodeView = getModeController().getNodeView(newNode);
			if (newNodeMode == MindMapController.NEW_CHILD) {
				getModeController().select(nodeView);
			}
			getModeController().edit.edit(nodeView, getModeController().getNodeView(target), e, true, parentFolded,
					false);
			break;
		}
		}
		return newNode;
	}

	protected MindMapNode addNewNode(MindMapNode parent, int index) {
		return getModeController().addNewNode(parent, index, parent.isNewChildLeft());
	}


}