/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/*
 * Created on 29.01.2006
 * Created by Dimitri Polivaev
 */
package freemind.modes.mindmapmode.actions.xml;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.mindmapmode.MindMapController;

public abstract class AbstractActorXml implements ActorXml {
	private MindMapController mindMapModeController;

	protected AbstractActorXml(MindMapController mindMapModeController) {
		this.mindMapModeController = mindMapModeController;
		mindMapModeController.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	protected NodeAdapter getNode(String nodeID) {
		return mindMapModeController.getNodeFromID(nodeID);
	}

	protected String getNodeID(MindMapNode node) {
		return mindMapModeController.getNodeID(node);
	}

	protected AttributeController getAttributeController() {
		return mindMapModeController.getAttributeController();
	}

	protected AttributeRegistry getAttributeRegistry() {
		return mindMapModeController.getMap().getRegistry().getAttributes();
	}

	protected CompoundAction createCompoundAction() {
		return new CompoundAction();
	}

}
