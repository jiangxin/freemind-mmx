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
 * Created on 27.08.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.Font;

import freemind.controller.actions.generated.instance.FontNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * 
 */
public class FontFamilyAction extends NodeGeneralAction implements NodeActorXml {
	/** This action is used for all fonts, which have to be set first. */
	private String actionFont;

	/**
     */
	public FontFamilyAction(MindMapController modeController) {
		super(modeController, "font_family", null, (NodeActorXml) null);
		addActor(this);
		// default value:
		actionFont = modeController.getFrame().getProperty("defaultfont");
	}

	public void actionPerformed(String font) {
		this.actionFont = font;
		super.actionPerformed(null);
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		return getActionPair(selected, actionFont);
	}

	public Class getDoActionClass() {
		return FontNodeAction.class;
	}

	/**
     */
	public void setFontFamily(MindMapNode node, String fontFamilyValue) {
		modeController.doTransaction(
				(String) getValue(NAME), getActionPair(node, fontFamilyValue));
	}

	private ActionPair getActionPair(MindMapNode node, String fontFamilyValue) {
		FontNodeAction fontFamilyAction = createFontNodeAction(node,
				fontFamilyValue);
		FontNodeAction undoFontFamilyAction = createFontNodeAction(node,
				node.getFontFamilyName());
		return new ActionPair(fontFamilyAction, undoFontFamilyAction);
	}

	private FontNodeAction createFontNodeAction(MindMapNode node,
			String fontValue) {
		FontNodeAction fontFamilyAction = new FontNodeAction();
		fontFamilyAction.setNode(getNodeID(node));
		fontFamilyAction.setFont(fontValue);
		return fontFamilyAction;

	}

	/**
     *
     */

	public void act(XmlAction action) {
		if (action instanceof FontNodeAction) {
			FontNodeAction fontFamilyAction = (FontNodeAction) action;
			MindMapNode node = getNodeFromID(fontFamilyAction.getNode());
			String fontFamily = fontFamilyAction.getFont();
			if (!Tools.safeEquals(node.getFontFamilyName(), fontFamily)) {
				((NodeAdapter) node).establishOwnFont();
				node.setFont(modeController.getController().getFontThroughMap(
						new Font(fontFamily, node.getFont().getStyle(), node
								.getFont().getSize())));
				modeController.nodeChanged(node);
			}
		}
	}
}
