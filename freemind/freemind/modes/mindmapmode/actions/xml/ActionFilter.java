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
 * Created on 25.08.2004
 */

package freemind.modes.mindmapmode.actions.xml;

/**
 * @author foltin
 * 
 *         Filter serve for the intersection of commands to be executed.
 * 
 *         The most useful scenario for these classes is the intersection of the
 *         command flow, eg. for collaboration or for storage of the map
 *         creation procedure ("map's story").
 * 
 */
public interface ActionFilter {
	/**
	 * Each filter receives the action pair and its result is taken as the new
	 * action pair.
	 */
	ActionPair filterAction(ActionPair pair);

	/**
	 * @author foltin This is a marker interface. Final Action Filter are always
	 *         called last and *should* not alter the action pair.
	 */
	public interface FinalActionFilter extends ActionFilter {

	}
	/**
	 * @author foltin This is a marker interface. FirstActionFilter are always
	 *         called first and *should* not alter the action pair.
	 */
	public interface FirstActionFilter extends ActionFilter {
		
	}
}
