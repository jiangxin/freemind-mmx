/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 12.10.2004
 */
/*
 * $Id: SetLinkByTextFieldAction.java,v 1.16.10.1 12.10.2004 22:18:45
 * christianfoltin Exp $
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import freemind.modes.mindmapmode.MindMapController;

public class SetLinkByTextFieldAction extends MindmapAction {
	private final MindMapController controller;

	public SetLinkByTextFieldAction(MindMapController controller) {
		super("set_link_by_textfield", controller);
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent e) {
		String inputValue = JOptionPane.showInputDialog(controller.getView()
				.getSelected(), controller.getText("edit_link_manually"),
				controller.getSelected().getLink());
		if (inputValue != null) {
			if (inputValue.equals("")) {
				inputValue = null; // In case of no entry unset link
			}
			controller.setLink(controller.getSelected(), inputValue);
		}
	}


}