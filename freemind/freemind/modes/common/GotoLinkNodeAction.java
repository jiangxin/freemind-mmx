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
 * Created on 16.10.2004
 */


package freemind.modes.common;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/** Follow a graphical link (AKA connector) action. */
public class GotoLinkNodeAction extends AbstractAction {
	MindMapNode source;

	private final ModeController controller;

	public GotoLinkNodeAction(ModeController controller, MindMapNode source) {
		super(controller.getText("goto_link_node_action"), new ImageIcon(
				controller.getResource("images/Link.png")));
		this.controller = controller;
		// only display a reasonable part of the string. the rest is available
		// via the short description (tooltip).
		this.source = source;
		// source is for the controllerAdapter == null,
		if (source != null) {
			String adaptedText = source.getShortText(controller);
			putValue(Action.NAME, controller.getText("follow_graphical_link")
					+ adaptedText);
			putValue(Action.SHORT_DESCRIPTION, source.toString());
		}
	}

	public void actionPerformed(ActionEvent e) {
		controller.centerNode(source);
	}

}