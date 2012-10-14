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


package freemind.modes.mindmapmode.actions.xml;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.Icon;

import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.FreemindAction;

/**
 * @author foltin
 * 
 */
public abstract class AbstractXmlAction extends FreemindAction {

	private ActorXml actor;

	private MindMapController controller;

	protected AbstractXmlAction(String name, Icon icon,
			MindMapController controller) {
		super(name, icon, controller);
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	final public void actionPerformed(ActionEvent arg0) {
		xmlActionPerformed(arg0);
	}

	protected String getShortDescription() {
		return (String) getValue(Action.SHORT_DESCRIPTION);
	}

	/**
	 */
	protected abstract void xmlActionPerformed(ActionEvent arg0);

	/**
	 */
	public MindMapController getMindMapController() {
		return controller;
	}

	public void addActor(ActorXml actor) {
		this.actor = actor;
		if (actor != null) {
			// registration:
			getMindMapController().getActionFactory().registerActor(actor,
					actor.getDoActionClass());
		}
	}

	/**
	 */
	public ActorXml getActor() {
		return actor;
	}

}
