/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
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
 * Created on 08.10.2004
 */
/*
 * $Id: ColorArrowLinkAction.java,v 1.16.10.1 08.10.2004 22:45:36
 * christianfoltin Exp $
 */

package freemind.modes.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.xml.bind.JAXBException;

import freemind.controller.Controller;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.FreemindAction;
import freemind.controller.actions.generated.instance.ArrowLinkColorXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.LineAdapter;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;

public class ColorArrowLinkAction extends FreemindAction implements ActorXml{

    MindMapArrowLinkModel arrowLink;

    private final ModeController controller;

    public ColorArrowLinkAction(ModeController controller, 
            MindMapArrowLinkModel arrowLink) {
        super("arrow_link_color", "images/Colors24.gif", controller);
        this.controller = controller;
        this.arrowLink = arrowLink;
        addActor(this);
    }

    public void actionPerformed(ActionEvent e) {
        Color selectedColor = arrowLink.getColor();
        Color color = Controller.showCommonJColorChooserDialog(controller
                .getView().getSelected(), (String) this.getValue(Action.NAME),
                selectedColor);
        if (color == null)
            return;
        setArrowLinkColor(arrowLink, color);
    }

    public void setArrowLinkColor(MindMapLink arrowLink, Color color) {
        controller.getActionFactory().startTransaction(
                (String) getValue(NAME));
        controller.getActionFactory().executeAction(
                getActionPair(arrowLink, color));
        controller.getActionFactory().endTransaction(
                (String) getValue(NAME));
    }

    /**
     * @param arrowLink
     * @param color
     * @return
     */
    private ActionPair getActionPair(MindMapLink arrowLink, Color color) {
        return new ActionPair(createArrowLinkColorXmlAction(arrowLink, color), 
                		createArrowLinkColorXmlAction(arrowLink, arrowLink.getColor()));
    }

    public void act(XmlAction action) {
        if (action instanceof ArrowLinkColorXmlAction) {
            ArrowLinkColorXmlAction colorAction = (ArrowLinkColorXmlAction) action;
            MindMapLink link = getLinkRegistry().getLinkForID(colorAction.getId());
	        ((LineAdapter) link).setColor(Tools.xmlToColor(colorAction.getColor()));
	        controller.nodeChanged(link.getSource());
        }
    }

    public Class getDoActionClass() {
        return ArrowLinkColorXmlAction.class;
    }

    
    private ArrowLinkColorXmlAction createArrowLinkColorXmlAction(MindMapLink arrowLink, Color color) {
        try {
            ArrowLinkColorXmlAction action = controller.getActionXmlFactory().createArrowLinkColorXmlAction();
            action.setColor(Tools.colorToXml(color));
            action.setId(arrowLink.getUniqueID());
            return action;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @return
     */
    private MindMapLinkRegistry getLinkRegistry() {
        return controller.getMap().getLinkRegistry();
    }

}