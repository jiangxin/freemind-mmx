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
 * Created on 02.05.2004
 */
/*$Id: AbstractXmlAction.java,v 1.1.4.1.8.1 2005-05-31 20:24:06 dpolivaev Exp $*/

package freemind.controller.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.xml.bind.JAXBException;

import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public abstract class AbstractXmlAction extends FreemindAction {

	private ActorXml actor;

	private ModeController controller;


	protected AbstractXmlAction(String name, Icon icon, ModeController controller) {
		super(name, icon, controller);
		this.controller = controller;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	final public void actionPerformed(ActionEvent arg0) {
		getActionFactory().startTransaction((String) getValue(Action.SHORT_DESCRIPTION));
		try {
			xmlActionPerformed(arg0);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getActionFactory().endTransaction((String) getValue(Action.SHORT_DESCRIPTION));
	}

	/**
	 * @param arg0
	 */
	protected abstract void xmlActionPerformed(ActionEvent arg0)  throws JAXBException;

	/**
	 * 
	 */
	private ActionFactory getActionFactory() {
		return getModeController().getActionFactory();
	}

	/**
	 * @return
	 */
	public ModeController getModeController() {
		return controller;
	}

	public void addActor(ActorXml actor) {
		this.actor = actor;
		if (actor != null) {
			// registration:
			getActionFactory().registerActor(actor, actor.getDoActionClass());
		}			
	}

	/**
	 * @return
	 */
	public ActorXml getActor() {
		return actor;
	}

}
