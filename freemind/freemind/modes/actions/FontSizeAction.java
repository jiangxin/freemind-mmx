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
/*$Id: FontSizeAction.java,v 1.1.2.1 2004-08-27 21:44:20 christianfoltin Exp $*/

package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import sun.java2d.loops.FontInfo;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.BoldNodeAction;
import freemind.controller.actions.generated.instance.FontSizeNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 *
 */
public class FontSizeAction extends NodeGeneralAction implements NodeActorXml {

    /** This action is used for all sizes, which have to be set first.*/
    private String actionSize;
      /**
     * @param modeController
     * @param textID
     * @param iconPath
     * @param actor
     */
    public FontSizeAction(ControllerAdapter modeController) {
        super(modeController, "font_size", null, (NodeActorXml) null);
        addActor(this);
        // default value:
        actionSize = modeController.getFrame().getProperty("defaultfontsize");
    }

    public void actionPerformed(String size) {
        this.actionSize = size;
        super.actionPerformed(null);
    }
    
    public ActionPair apply(MapAdapter model, MindMapNodeModel selected) throws JAXBException {
        return getActionPair(selected, actionSize);
    }

    public Class getDoActionClass() {
        return FontSizeNodeAction.class;
    }

    /**
     * @param node
     * @param fontSizeValue
     */
    public void setFontSize(MindMapNode node, String fontSizeValue) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    getActionPair(node, fontSizeValue));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        
    }

	private ActionPair getActionPair(MindMapNode node, String fontSizeValue)
	throws JAXBException {
	    FontSizeNodeAction fontSizeAction = createFontSizeNodeAction(node, fontSizeValue);
	    FontSizeNodeAction undoFontSizeAction = createFontSizeNodeAction(node, node.getFontSize());
	    return new ActionPair(fontSizeAction, undoFontSizeAction);
	}

	private FontSizeNodeAction createFontSizeNodeAction(MindMapNode node, String fontSizeValue) throws JAXBException {
        FontSizeNodeAction fontSizeAction = getActionXmlFactory().createFontSizeNodeAction();
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
	            if(!node.getFontSize().equals(fontSizeAction.getSize())) {
	                node.setFontSize(size);
	                modeController.nodeChanged(node);
	            }
            } catch (NumberFormatException e) {
                return;
            }
        }
    }
}
