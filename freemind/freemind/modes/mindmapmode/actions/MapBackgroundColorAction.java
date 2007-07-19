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
 * Created on 19.09.2004
 */
/* $Id: MapBackgroundColorAction.java,v 1.1.2.1 2007-07-19 21:31:29 dpolivaev Exp $ */

package freemind.modes.mindmapmode.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ListIterator;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.MapBackgroundColorXmlAction;
import freemind.controller.actions.generated.instance.NodeColorFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class MapBackgroundColorAction extends FreemindAction implements ActorXml {
    private final MindMapController controller;

    public MapBackgroundColorAction(MindMapController controller) {
        super("background_color", (String)null, controller);
        this.controller = controller;
        controller.getActionFactory().registerActor(this, getDoActionClass());
    }

    public void actionPerformed(ActionEvent e) {
        Color color = Controller.showCommonJColorChooserDialog(controller
                .getView().getSelected(), controller.getText("choose_background_color"), 
                controller.getModel().getBackgroundColor());
        if (color == null) {
            return;
        }
        setMapColor(color);
    }
    
    public void setMapColor(Color color) {
    	MapBackgroundColorXmlAction doAction = createMapBackgroundColorAction(color);
    	MapBackgroundColorXmlAction undoAction = createMapBackgroundColorAction(controller.getMap().getBackgroundColor());
        controller.getActionFactory().startTransaction(this.getClass().getName());
        controller.getActionFactory().executeAction(new ActionPair(doAction, undoAction));
        controller.getActionFactory().endTransaction(this.getClass().getName());
    }

    public MapBackgroundColorXmlAction createMapBackgroundColorAction(Color color)  {
    	MapBackgroundColorXmlAction action = new MapBackgroundColorXmlAction();
    	action.setColor(Tools.colorToXml(color));
		return action;
    }
    
    public void act(XmlAction action) {
		if (action instanceof MapBackgroundColorXmlAction) {
			MapBackgroundColorXmlAction bgColorAction = (MapBackgroundColorXmlAction) action;
			Color color = Tools.xmlToColor(bgColorAction.getColor());
			Color oldColor = controller.getModel().getBackgroundColor() ;
			if (!Tools.safeEquals(color, oldColor)) {
				controller.setBackgroundColor(color); // null
            }
		}
   }

    public Class getDoActionClass() {
        return MapBackgroundColorXmlAction.class;
    }

}