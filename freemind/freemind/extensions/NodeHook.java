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
/*$Id: NodeHook.java,v 1.1.2.1 2004-03-04 20:26:19 christianfoltin Exp $*/

package freemind.extensions;

import javax.swing.JMenu;

import freemind.modes.MindMapNode;

public interface NodeHook extends MindMapHook {
	
	void nodeMenuHook(JMenu nodeMenu);

	/* hooks */

	/**
	 * Is called after creation:
	 */
	void invoke();
	void onReceiveFocusHook();
	void onMouseOverHook();
	void onUpdateNodeHook();
	void onAddChild(MindMapNode newChildNode);
	/**
	 * If any of my children is updated, I get this notification.
	 */
	void onUpdateChildrenHook(MindMapNode updatedNode);

	/**
	 * If any node in the map is updated, I get this notification.
	 * To receive it, you have to subscribe yourself using any method to be written. 
	 */
	void onUpdateAnyNodeHook(MindMapNode updatedNode);
	
}

