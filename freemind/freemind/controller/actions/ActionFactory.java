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
 * Created on 24.04.2004
 */
/*$Id: ActionFactory.java,v 1.1.2.2 2004-05-09 22:31:14 christianfoltin Exp $*/

package freemind.controller.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBException;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.BoldNodeActionType;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 *
 */
public class ActionFactory {
	public static final String JAXB_CONTEXT =
		"freemind.controller.actions.generated.instance";

	private Controller controller;
	private ObjectFactory factory;
	/** This set denotes all handler of the action to be called for each action. */
	private Set registeredHandler;
	/** HashMap of Action class -> actor instance. */
	private HashMap registeredActors;

	/**
	 * 
	 */
	public ActionFactory(Controller c) {
		super();
		this.controller = c;
		factory = new ObjectFactory();
		registeredHandler = new HashSet();
		registeredActors = new HashMap();
	}

	public void registerHandler(ActionHandler newHandler) {
		registeredHandler.add(newHandler);
	}

	public void deregisterHandler(ActionHandler newHandler) {
		registeredHandler.remove(newHandler);
	}

	public void startTransaction(String name) {
		for (Iterator i = registeredHandler.iterator(); i.hasNext();) {
			ActionHandler handler = (ActionHandler) i.next();
			handler.startTransaction(name);
		}
	}


	public void endTransaction(String name) {
		for (Iterator i = registeredHandler.iterator(); i.hasNext();) {
			ActionHandler handler = (ActionHandler) i.next();
			handler.endTransaction(name);
		}
	}

	/**
	 * @param doAction
	 * @param undoAction
	 */
	public void executeAction(ActionPair pair) {
		for (Iterator i = registeredHandler.iterator(); i.hasNext();) {
			ActionHandler handler = (ActionHandler) i.next();
			handler.executeAction(pair);
		}
	}

	/**
	 * @return
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * @param actor
	 * @param action
	 */
	public void registerActor(ActorXml actor, Class action) {
		registeredActors.put(action, actor);		
	}

	public ActorXml getActor(XmlAction action) {
		for (Iterator i = registeredActors.keySet().iterator(); i.hasNext();) {
			Class actorClass = (Class) i.next();
			if(actorClass.isInstance(action)) {
				return (ActorXml) registeredActors.get(actorClass);
			}
		}
//		Class actionClass = action.getClass();
//		if(registeredActors.containsKey(actionClass)) {
//			return (ActorXml) registeredActors.get(actionClass);
//		}
		throw new IllegalArgumentException("No actor present for xmlaction" + action.getClass());
	}
}
