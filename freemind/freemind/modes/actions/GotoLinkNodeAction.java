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
 * Created on 16.10.2004
 */
/*$Id: GotoLinkNodeAction.java,v 1.1.4.1 2004-10-17 23:00:10 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import freemind.controller.actions.FreemindAction;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;


public class GotoLinkNodeAction extends FreemindAction {
    MindMapNode source;
    private final ModeController controller;
    public GotoLinkNodeAction(ModeController controller, MindMapNode source) {
        super(null, "images/Link.png", controller);
        this.controller = controller;
        // only display a reasonable part of the string. the rest is available via the short description (tooltip).
        this.source = source;
        // source is for the controllerAdapter == null,
        if (source!=null) {
            String adaptedText = source.getShortText(controller);
            putValue(Action.NAME, controller.getText("follow_link")
                    + adaptedText);
            putValue(Action.SHORT_DESCRIPTION, source.toString());
        }
    }

    public void actionPerformed(ActionEvent e) {
        controller.displayNode(source);
    }
    
    public String getShortTextForLink(MindMapNode node) {
        String adaptedText = node.getLink();
        if(adaptedText== null) 
            return null;
        if ( adaptedText.startsWith("#")) {
            try {
                MindMapNode dest = controller.getNodeFromID(adaptedText.substring(1));
                return dest.getShortText(controller);
            } catch(Exception e) {
                return controller.getText("link_not_available_any_more");
            }
        }
        return adaptedText;
    }
}