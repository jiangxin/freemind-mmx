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
/*$Id: FontFamilyAction.java,v 1.1.2.3 2004-10-05 22:23:58 christianfoltin Exp $*/

package freemind.modes.actions;

import java.awt.Font;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.FontNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;

/**
 * @author foltin
 *
 */
public class FontFamilyAction extends NodeGeneralAction implements NodeActorXml {
    /** This action is used for all fonts, which have to be set first.*/
    private String actionFont;
      /**
     * @param modeController
     * @param textID
     * @param iconPath
     * @param actor
     */
    public FontFamilyAction(ControllerAdapter modeController) {
        super(modeController, "font_family", null, (NodeActorXml) null);
        addActor(this);
        // default value:
        actionFont = modeController.getFrame().getProperty("defaultfont");
    }

    public void actionPerformed(String font) {
        this.actionFont = font;
        super.actionPerformed(null);
    }
    
    public ActionPair apply(MapAdapter model, MindMapNode selected) throws JAXBException {
        return getActionPair(selected, actionFont);
    }

    public Class getDoActionClass() {
        return FontNodeAction.class;
    }

    /**
     * @param node
     * @param fontValue
     */
    public void setFontFamily(MindMapNode node, String fontFamilyValue) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, fontFamilyValue));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

	private ActionPair getActionPair(MindMapNode node, String fontFamilyValue)
	throws JAXBException {
	    FontNodeAction fontFamilyAction = createFontNodeAction(node, fontFamilyValue);
	    FontNodeAction undoFontFamilyAction = createFontNodeAction(node, node.getFontFamilyName());
	    return new ActionPair(fontFamilyAction, undoFontFamilyAction);
	}

	private FontNodeAction createFontNodeAction(MindMapNode node, String fontValue) throws JAXBException {
        FontNodeAction fontFamilyAction = getActionXmlFactory().createFontNodeAction();
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
            if(!Tools.safeEquals(node.getFontFamilyName(),fontFamily)) {
                ((NodeAdapter) node).estabilishOwnFont();
                node.setFont(modeController.getController().getFontThroughMap
                        (new Font(fontFamily,node.getFont().getStyle(),node.getFont().getSize())));
                modeController.nodeChanged(node);
            }
        }
    }
}
