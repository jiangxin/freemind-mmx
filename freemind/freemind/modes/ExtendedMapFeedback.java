/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/

package freemind.modes;

import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * MapFeedback extended by the xml based node change management.
 * 
 * @author foltin
 * @date 16.03.2014
 */
public interface ExtendedMapFeedback extends MapFeedback {
	/**
	 * @return the action factory that contains the actors definitions.
	 */
	ActionFactory getActionFactory();

	boolean doTransaction(String pName, ActionPair pPair);

	
	/**
	 * Given a node identifier, this method returns the corresponding node.
	 * 
	 * @throws IllegalArgumentException
	 *             if the id is unknown.
	 */
	NodeAdapter getNodeFromID(String nodeID);

	/**
	 * Calling this method the map-unique identifier of the node is returned
	 * (and created before, if not present)
	 */
	String getNodeID(MindMapNode selected);

	
}
