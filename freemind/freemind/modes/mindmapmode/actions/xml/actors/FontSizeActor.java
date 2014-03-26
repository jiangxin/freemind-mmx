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

import freemind.controller.actions.generated.instance.FontSizeNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 26.03.2014
 */
public class FontSizeActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public FontSizeActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public Class getDoActionClass() {
		return FontSizeNodeAction.class;
	}

	/**
     */
	public void setFontSize(MindMapNode node, String fontSizeValue) {
		if (Tools.safeEquals(fontSizeValue, node.getFontSize())) {
			return;
		}
		execute(getActionPair(node, fontSizeValue));

	}

	public ActionPair getActionPair(MindMapNode node, String fontSizeValue) {
		FontSizeNodeAction fontSizeAction = createFontSizeNodeAction(node,
				fontSizeValue);
		FontSizeNodeAction undoFontSizeAction = createFontSizeNodeAction(node,
				node.getFontSize());
		return new ActionPair(fontSizeAction, undoFontSizeAction);
	}

	private FontSizeNodeAction createFontSizeNodeAction(MindMapNode node,
			String fontSizeValue) {
		FontSizeNodeAction fontSizeAction = new FontSizeNodeAction();
		fontSizeAction.setNode(getNodeID(node));
		fontSizeAction.setSize(fontSizeValue);
		return fontSizeAction;

	}

	/**
     *
     */

	public void act(XmlAction action) {
		if (action instanceof FontSizeNodeAction) {
			FontSizeNodeAction fontSizeAction = (FontSizeNodeAction) action;
			MindMapNode node = getNodeFromID(fontSizeAction.getNode());
			try {
				int size = Integer.valueOf(fontSizeAction.getSize()).intValue();
				if (!node.getFontSize().equals(fontSizeAction.getSize())) {
					node.setFontSize(size);
					getExMapFeedback().nodeChanged(node);
				}
			} catch (NumberFormatException e) {
				return;
			}
		}
	}

	
}
