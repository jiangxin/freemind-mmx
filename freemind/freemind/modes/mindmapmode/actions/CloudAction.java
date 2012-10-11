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
 * Created on 06.10.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.AddCloudXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapCloudModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * 
 */
public class CloudAction extends NodeGeneralAction implements NodeActorXml,
		MenuItemSelectedListener {

	public CloudAction(MindMapController controller) {
		super(controller, "cloud", "images/Cloud24.gif");
		addActor(this);
	}

	public Class getDoActionClass() {
		return AddCloudXmlAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		ActionPair pair = getActionPair(selected, selected.getCloud() == null);
		return pair;
	}

	public void setCloud(MindMapNode node, boolean enable) {
		modeController.doTransaction(
				(String) getValue(NAME), getActionPair(node, enable));

	}

	private ActionPair getActionPair(MindMapNode selected, boolean enable) {
		AddCloudXmlAction cloudAction = createAddCloudXmlAction(selected,
				enable, null);
		AddCloudXmlAction undocloudAction = null;
		if (selected.getCloud() != null) {
			undocloudAction = createAddCloudXmlAction(selected, true, selected
					.getCloud().getColor());
		} else {
			undocloudAction = createAddCloudXmlAction(selected, false, null);

		}
		return new ActionPair(cloudAction, undocloudAction);
	}

	private AddCloudXmlAction createAddCloudXmlAction(MindMapNode selected,
			boolean enable, Color color) {
		AddCloudXmlAction nodecloudAction = new AddCloudXmlAction();
		nodecloudAction.setNode(getNodeID(selected));
		nodecloudAction.setEnabled(enable);
		nodecloudAction.setColor(Tools.colorToXml(color));
		return nodecloudAction;
	}

	public void act(XmlAction action) {
		if (action instanceof AddCloudXmlAction) {
			AddCloudXmlAction nodecloudAction = (AddCloudXmlAction) action;
			MindMapNode node = getNodeFromID(nodecloudAction.getNode());
			if ((node.getCloud() == null) == nodecloudAction.getEnabled()) {
				if (nodecloudAction.getEnabled()) {
					if (node.isRoot()) {
						return;
					}
					node.setCloud(new MindMapCloudModel(node,
							getMindMapController().getFrame()));
					if (nodecloudAction.getColor() != null) {
						Color color = Tools.xmlToColor(nodecloudAction
								.getColor());
						((MindMapCloudModel) node.getCloud()).setColor(color);
					}
				} else {
					node.setCloud(null);
				}
				modeController.nodeChanged(node);
			}
		}
	}

	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		return modeController.getSelected().getCloud() != null;
	}
}