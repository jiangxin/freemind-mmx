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
 * Created on 05.05.2004
 */
/*$Id: NewChildAction.java,v 1.1.2.1 2004-05-06 05:08:26 christianfoltin Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.MindMapLinkRegistry.ID_Registered;


public class NewChildAction extends AbstractAction implements ActorXml {
    private final ControllerAdapter modeController;
    public NewChildAction(ControllerAdapter modeController) {
        super(modeController.getText("new_child"));
        this.modeController = modeController;
		this.modeController.getActionFactory().registerActor(this, getDoActionClass());
    }

    public void actionPerformed(ActionEvent e) {
       this.modeController.addNew(modeController.getView().getSelected(), modeController.NEW_CHILD, null);
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
		System.out.println("NewNodeAction");
		NewNodeAction addNodeAction = (NewNodeAction) action;
		NodeAdapter parent = this.modeController.getNodeFromID(addNodeAction.getNode());
		int index = addNodeAction.getIndex();
		MindMapNode newNode = modeController.newNode();
		String newId = addNodeAction.getNewId();
		ID_Registered reg = modeController.getModel().getLinkRegistry().registerLinkTarget(newNode,newId);
		if(!reg.getID().equals(newId)) {
			throw new IllegalArgumentException("Designated id '"+newId+"' was not given to the node. It received '"+reg.getID()+"'.");
		}
		if(addNodeAction.getPosition()!= null) {
			newNode.setLeft(addNodeAction.getPosition().equals("left"));
		}
		modeController.getModel().insertNodeInto(newNode, parent, index);
		modeController.getFrame().repaint(); 
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return NewNodeAction.class;
    }
}