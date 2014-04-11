/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.modes.mindmapmode.actions.xml.actors;

import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.actors.PasteActor.NodeCoordinate;

/**
 * @author foltin
 * @date 09.05.2012
 */
public class UndoPasteActor extends XmlActorAdapter {



	/**
	 * @param pMapFeedback
	 */
	public UndoPasteActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.mindmapmode.actions.xml.ActorXml#act(freemind.controller
	 * .actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction pAction) {
		if (pAction instanceof UndoPasteNodeAction) {
			UndoPasteNodeAction undoAction = (UndoPasteNodeAction) pAction;
			MindMapNode selectedNode = getNodeFromID(undoAction.getNode());
			int amount = undoAction.getNodeAmount();
			while(amount > 0) {
				NodeCoordinate coordinate = new NodeCoordinate(selectedNode,
						undoAction.getAsSibling(), undoAction.getIsLeft());
				MindMapNode targetNode = coordinate.getNode();
				getXmlActorFactory().getDeleteChildActor().deleteWithoutUndo(targetNode);
				amount--;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.mindmapmode.actions.xml.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return UndoPasteNodeAction.class;
	}

}
