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
 * Created on 29.09.2004
 */
/*$Id: RemoveLastIconAction.java,v 1.1.4.1.10.1 2005-07-12 15:41:16 dpolivaev Exp $*/

package freemind.modes.actions;

import java.util.List;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.AddIconAction;
import freemind.controller.actions.generated.instance.RemoveLastIconXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public class RemoveLastIconAction extends NodeGeneralAction implements NodeActorXml{

    private IconAction iconAction;
    /**
     * @param title
     * @param icon
     * @param modeController
     */
    public RemoveLastIconAction(ModeController modeController) {
        super(modeController, "remove_last_icon", "images/remove.png");
        addActor(this);
    }

    public ActionPair apply(MapAdapter model, MindMapNode selected) throws JAXBException {
        List icons = selected.getIcons();
		if(icons.size()==0) 
            return null;
        AddIconAction undoAction = iconAction.createAddIconAction(selected, (MindIcon) icons.get(icons.size()-1));
        return new ActionPair(createRemoveLastIconXmlAction(selected), undoAction);
    }

    public Class getDoActionClass() {
        return RemoveLastIconXmlAction.class;
    }

    public RemoveLastIconXmlAction createRemoveLastIconXmlAction(MindMapNode node) throws JAXBException {
        RemoveLastIconXmlAction action = modeController.getActionXmlFactory().createRemoveLastIconXmlAction();
        action.setNode(node.getObjectId(modeController));
        return action;
    }

    
    public int removeLastIcon(MindMapNode node) {
        try {
            modeController.getActionFactory().startTransaction(
                    (String) getValue(NAME));
            modeController.getActionFactory().executeAction(
                    apply(modeController.getMap(), node));
            modeController.getActionFactory().endTransaction(
                    (String) getValue(NAME));
            return node.getIcons().size();
        } catch (JAXBException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
    *
    */

   public void act(XmlAction action) {
        if (action instanceof freemind.controller.actions.generated.instance.RemoveLastIconXmlAction) {
            freemind.controller.actions.generated.instance.RemoveLastIconXmlAction removeAction = (freemind.controller.actions.generated.instance.RemoveLastIconXmlAction) action;
            MindMapNode node = modeController.getNodeFromID(removeAction
                    .getNode());
            node.removeLastIcon();
            modeController.nodeChanged(node);
        }
    }


    /**
     * @param addIconAction The addIconAction to set.
     */
    public void setIconAction(IconAction iconAction) {
        this.iconAction = iconAction;
    }
}
