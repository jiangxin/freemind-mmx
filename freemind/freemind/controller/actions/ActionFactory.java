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
/*$Id: ActionFactory.java,v 1.1.2.6 2004-09-27 19:49:52 christianfoltin Exp $*/

package freemind.controller.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import freemind.common.JaxbTools;
import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;

/**
 * @author foltin
 *
 */
public class ActionFactory {

	private Controller controller;
	/** This Vector denotes all handler of the action to be called for each action. */
	private Vector registeredHandler;
	/** This set denotes all filters for XmlActions.*/
	private Set registeredFilters;
	/** HashMap of Action class -> actor instance. */
	private HashMap registeredActors;

	/**
	 * 
	 */
	public ActionFactory(Controller c) {
		super();
		this.controller = c;
		registeredHandler = new Vector();
		registeredFilters = new HashSet();
		registeredActors = new HashMap();
	}

	/** The handler is put in front. Thus it is called before others are called.
	 * @param newHandler
	 */
	public void registerHandler(ActionHandler newHandler) {
	    // if it is present, put it in front:
		if (!registeredHandler.contains(newHandler)) {
		    registeredHandler.remove(newHandler);
        }
        registeredHandler.add(0, newHandler);
	}

	public void deregisterHandler(ActionHandler newHandler) {
		registeredHandler.remove(newHandler);
	}

	public void registerFilter(ActionFilter newFilter) {
		registeredFilters.add(newFilter);
	}

	public void deregisterFilter(ActionFilter newFilter) {
		registeredFilters.remove(newFilter);
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
		ActionPair filteredPair = pair;
		// first filter:
		for (Iterator i = registeredFilters.iterator(); i.hasNext();) {
			ActionFilter filter = (ActionFilter) i.next();
			filteredPair = filter.filterAction(filteredPair);
		}
		for (Iterator i = registeredHandler.iterator(); i.hasNext();) {
			ActionHandler handler = (ActionHandler) i.next();
			// the executer must not disturb the whole picture if they throw something:
			try {
                handler.executeAction(filteredPair);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
