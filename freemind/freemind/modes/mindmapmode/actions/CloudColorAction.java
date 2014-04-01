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
 * Created on 19.09.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.Controller;
import freemind.controller.MenuItemEnabledListener;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class CloudColorAction extends MindmapAction implements MenuItemEnabledListener {
	private final MindMapController controller;

	public CloudColorAction(MindMapController controller) {
		super("cloud_color", "images/Colors24.gif", controller);
		this.controller = controller;
	}

	public void actionPerformed(ActionEvent e) {
		Color selectedColor = null;
		if (controller.getSelected().getCloud() != null) {
			selectedColor = controller.getSelected().getCloud().getColor();
		}
		Color color = Controller.showCommonJColorChooserDialog(controller
				.getView().getSelected(), controller
				.getText("choose_cloud_color"), selectedColor);
		if (color == null) {
			return;
		}
		for (ListIterator it = controller.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			controller.setCloudColor(selected, color);
		}
	}

	/**
     *
     */

	public boolean isEnabled(JMenuItem item, Action action) {
		return super.isEnabled(item, action) && (controller != null) && (controller.getSelected() != null)
				&& (controller.getSelected().getCloud() != null);
	}

}