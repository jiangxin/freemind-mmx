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
 * Created on 21.09.2004
 */
/*$Id: UndoEventReceiver.java,v 1.1.4.2 2005/02/13 22:39:56 christianfoltin Exp $*/

package freemind.extensions;

/**
 * This is a marker interface for hooks. If a PermanentNodeHook implements this
 * interface (which is easy), it receives onUpdateNodeHook,
 * onUpdateChildrenHook, even when the action issuing this update is caused by an
 * undo action.
 * 
 * Normally, on undo, no event are generated.
 * 
 * The onAddChild, onAddChildren events are not implemented yet. They are
 * generated in any case.
 * 
 * @author foltin
 * 
 */
public interface UndoEventReceiver {

}
