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
 * Created on 20.09.2004
 */
/*$Id: RedoAction.java,v 1.1.2.1 2004-09-20 21:20:47 christianfoltin Exp $*/

package freemind.modes.actions;

import javax.swing.ImageIcon;

import freemind.controller.actions.ActionPair;
import freemind.modes.ControllerAdapter;


public class RedoAction extends UndoAction {
	private ControllerAdapter controller;

    public RedoAction(ControllerAdapter controller) {
		super(controller, controller.getText("redo"), new ImageIcon(controller.getResource("images/redo.png")), controller);
        this.controller = controller;
	}	


    /**
     * @param pair
     */
    protected void informUndoPartner(ActionPair pair) {
		this.controller.undo.add(pair.reverse());
		this.controller.undo.setEnabled(true);
    }
	
}