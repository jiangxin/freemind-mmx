/*
 * FreeMind - a program for creating and viewing mind maps
 * Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 26.01.2006.
 */

package freemind.modes.mindmapmode.actions;

import freemind.main.HtmlTools;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

public class UsePlainTextAction extends NodeGeneralAction {

	public UsePlainTextAction(final MindMapController modeController) {
		super(modeController, "use_plain_text", null,
				new SingleNodeOperation() {

					public void apply(MindMapMapModel map,
							MindMapNodeModel selected) {
						// modeController.getController().setProperty(
						// "use_rich_text_in_new_long_nodes", "false");
						String nodeText = selected.getText();
						if (HtmlTools.isHtmlNode(nodeText)) {
							modeController.setNodeText(selected,
									HtmlTools.htmlToPlain(nodeText));
						}
					}
				});
	}

	// public boolean isEnabled() {
	// if (1 == 0) {
	// // Dan: The following code probably needs that the node context menu
	// // gets somehow refreshed
	// // whenever it pops. But I do not know how to ensure that.
	// for (ListIterator it = controller.getView().getSelecteds()
	// .listIterator(); it.hasNext();) {
	// NodeView selected = (NodeView) it.next();
	// String nodeText = selected.getModel().toString();
	// if (nodeText.startsWith("<html>")) {
	// return true;
	// }
	// }
	// return false;
	// }
	// return true;
	// }

}