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
 * Created on 08.08.2004
 */
/*$Id: UnfoldAllForMouseWheel.java,v 1.1.2.1 2004-08-08 13:03:47 christianfoltin Exp $*/

package accessories.plugins;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;

import freemind.extensions.PermanentNodeHook;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController.MouseWheelEventHandler;

/**
 * @author foltin
 *
 */
public class UnfoldAllForMouseWheel extends UnfoldAll implements MouseWheelEventHandler, PermanentNodeHook{

    /**
     *
     */

    public void invoke(MindMapNode node) {
    }
    /**
     * 
     */
    public UnfoldAllForMouseWheel() {
        super();
        // TODO Auto-generated constructor stub
    }


    /**
    *
    */

   public void startupMapHook() {
       super.startupMapHook();
       getController().registerMouseWheelEventHandler(this);
   }

    /**
     *
     */

    public void shutdownMapHook() {
        getController().deRegisterMouseWheelEventHandler(this);
        super.shutdownMapHook();
    }
    public boolean handleMouseWheelEvent(MouseWheelEvent e) {
        logger.info("handleMouseWheelEvent entered.");
        if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
            if(e.getWheelRotation() > 0) {
                unfoldOneStage((MindMapNode) getMap().getRoot());
            } else {
                foldOneStage((MindMapNode) getMap().getRoot());
            }
            return true;
        }
        return false;
    }


    public void onReceiveFocusHook() {
    }


    public void onMouseOverHook() {
    }


    public void onUpdateNodeHook() {
    }


    public void onAddChild(MindMapNode newChildNode) {
    }


    public void onAddChildren(MindMapNode addedChild) {
    }


    public void onRemoveChild(MindMapNode oldChildNode) {
    }


    public void onUpdateChildrenHook(MindMapNode updatedNode) {
    }


    public void save(XMLElement hookElement) {
    }


    public void loadFrom(XMLElement child) {
    }


    public void onLooseFocusHook() {
    }

}
