/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 23.04.2014
 */
public class ChangeNoteTextActor extends XmlActorAdapter {


	public ChangeNoteTextActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof EditNoteToNodeAction) {
			EditNoteToNodeAction noteTextAction = (EditNoteToNodeAction) action;
			MindMapNode node = getNodeFromID(noteTextAction
					.getNode());
			String newText = noteTextAction.getText();
			String oldText = node.getNoteText();
			if (!Tools.safeEquals(newText, oldText)) {
				node.setNoteText(newText);
				getExMapFeedback().nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return EditNoteToNodeAction.class;
	}

	public EditNoteToNodeAction createEditNoteToNodeAction(MindMapNode node,
			String text) {
		EditNoteToNodeAction nodeAction = new EditNoteToNodeAction();
		nodeAction.setNode(getNodeID(node));
		if (text != null
				&& (HtmlTools.htmlToPlain(text).length() != 0 || text
						.indexOf("<img") >= 0)) {
			nodeAction.setText(text);
		} else {
			nodeAction.setText(null);
		}
		return nodeAction;
	}

	public void setNoteText(MindMapNode node, String text) {
		String oldNoteText = node.getNoteText();
		if (Tools.safeEquals(text, oldNoteText)) {
			// they are equal.
			return;
		}
		logger.fine("Old Note Text:'" + oldNoteText + ", new:'" + text + "'.");
		logger.fine(Tools.compareText(oldNoteText, text));
		EditNoteToNodeAction doAction = createEditNoteToNodeAction(node, text);
		EditNoteToNodeAction undoAction = createEditNoteToNodeAction(node,
				oldNoteText);
		execute(new ActionPair(doAction, undoAction));
	}



}
