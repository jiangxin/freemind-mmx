/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: MindMapHook.java,v 1.1.2.1 2004-03-04 20:26:19 christianfoltin Exp $*/
package freemind.extensions;

import java.util.Properties;

/**
 * @author foltin
 *
 */
public interface MindMapHook {
	String getName();
	
	/**
	 * @return it should return its persistent state or null if not applicable.
	 */
	String getPersistentState();

	/**
	 * @return it should return its persistent state or null if not applicable.
	 */
	void setPersistentState(String persistentState);

	/**
	 * @param properties the properties of the property file belonging to the hook are passed.
	 */
	void setProperties(Properties properties);

	/* Hooks */

	/**
	 * This method is also called, if the hook is created in the map.
	 * @param persistentStateString every hook can be equipped by a persistent state,
	 * which is here given. It is stored in the xml of FM.
	 */
	void startupMapHook();

	/**
	 * This method is also called, if the node, this hook belongs to, is removed from the map.
	 */
	void shutdownMapHook();

}
