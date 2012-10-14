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
import freemind.controller.actions.generated.instance.CloudColorXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.LineAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class CloudColorAction extends FreemindAction implements ActorXml,
		MenuItemEnabledListener {
	private final MindMapController controller;

	public CloudColorAction(MindMapController controller) {
		super("cloud_color", "images/Colors24.gif", controller);
		this.controller = controller;
		controller.getActionFactory().registerActor(this, getDoActionClass());
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
			setCloudColor(selected, color);
		}
	}

	public void setCloudColor(MindMapNode node, Color color) {
		CloudColorXmlAction doAction = createCloudColorXmlAction(node, color);
		CloudColorXmlAction undoAction = createCloudColorXmlAction(node,
				(node.getCloud() == null) ? null : node.getCloud().getColor());
		controller.doTransaction(this.getClass().getName(),
				new ActionPair(doAction, undoAction));
	}

	public CloudColorXmlAction createCloudColorXmlAction(MindMapNode node,
			Color color) {
		CloudColorXmlAction nodeAction = new CloudColorXmlAction();
		nodeAction.setNode(node.getObjectId(controller));
		nodeAction.setColor(Tools.colorToXml(color));
		return nodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof CloudColorXmlAction) {
			CloudColorXmlAction nodeColorAction = (CloudColorXmlAction) action;
			Color color = Tools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = controller.getNodeFromID(nodeColorAction
					.getNode());
			// this is not necessary, as this action is not enabled if there is
			// no cloud.
			if (node.getCloud() == null) {
				controller.setCloud(node, true);
			}
			Color selectedColor = null;
			if (node.getCloud() != null) {
				selectedColor = node.getCloud().getColor();
			}
			if (!Tools.safeEquals(color, selectedColor)) {
				((LineAdapter) node.getCloud()).setColor(color); // null
				controller.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return CloudColorXmlAction.class;
	}

	/**
     *
     */

	public boolean isEnabled(JMenuItem item, Action action) {
		return (controller != null) && (controller.getSelected() != null)
				&& (controller.getSelected().getCloud() != null);
	}

}