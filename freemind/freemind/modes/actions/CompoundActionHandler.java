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
 * Created on 09.05.2004
 */
/*$Id: CompoundActionHandler.java,v 1.1.4.1 2004-10-17 23:00:08 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CompoundActionType;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;

/**
 * @author foltin
 *
 */
public class CompoundActionHandler extends AbstractAction implements ActorXml {

    private ControllerAdapter c;
    public CompoundActionHandler(ControllerAdapter c) {
        this.c = c;
        this.c.getActionFactory().registerActor(this, getDoActionClass());
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
		CompoundAction compound = (CompoundAction) action;
        for (Iterator i =
            compound
                .getCompoundActionOrSelectNodeActionOrCutNodeAction()
                .iterator();
            i.hasNext();
            ) {
            XmlAction ac = (XmlAction) i.next();
			ActorXml actor = c.getActionFactory().getActor(ac);
			actor.act(ac);

        }
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return CompoundAction.class;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {

    }

}
