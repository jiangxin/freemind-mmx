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
/*$Id: MindMapLinkRegistry.java,v 1.6.18.1.16.4 2008/12/09 21:09:43 christianfoltin Exp $*/

package freemind.modes;

import java.util.Vector;

/**
 * Interface for the registry, which manages the ids of nodes and the existing
 * links in a map. Thus, this interface is bound to a map model, because other
 * maps have a different registry.
 */
public interface MindMapLinkRegistry {
	public void registerLinkTarget(MindMapNode target);

	/**
	 * The second variant of the main method. The difference is that here an ID
	 * is proposed, but has not to be taken, though.
	 */
	public String registerLinkTarget(MindMapNode target, String proposedID);

	public void deregisterLinkTarget(MindMapNode target)
			throws java.lang.IllegalArgumentException;

	public String getLabel(MindMapNode target);

	/**
	 * Reverses the getLabel method: searches for a node with the id given as
	 * the argument.
	 */
	public MindMapNode getTargetForID(String ID);

	/**
	 * This can be used, if the id has to be known, before a node can be labled.
	 */
	public String generateUniqueID(String proposedID);

	public void registerLink(MindMapLink link);

	public void deregisterLink(MindMapLink link);

	/**
	 * Reverses the getUniqueID method: searches for a link with the id given as
	 * the argument.
	 */
	public MindMapLink getLinkForID(String ID);

	/**
	 * This can be used, if the id has to be known, before a link can be labled.
	 */
	public String generateUniqueLinkID(String proposedID);

	/** Removes links to all nodes beginning from target with its children. */
	public void cutNode(MindMapNode target);

	// fc, 9.8.: apparently not used.
	// /** Returns a Vector of Nodes that point to the given node.*/
	// public Vector /* of MindMapNode s */ getAllSources(MindMapNode target);
	/** @return returns all links from or to this node. */
	public Vector /* of MindMapLink s */getAllLinks(MindMapNode node);

	/** @return returns all links to this node. */
	public Vector /* of MindMapLink s */getAllLinksIntoMe(MindMapNode target);

	/** @return returns all links from this node. */
	public Vector /* of MindMapLink s */getAllLinksFromMe(MindMapNode source);

	public void registerLocalHyperlinkId(String pTargetId);

	public boolean isTargetOfLocalHyperlinks(String pTargetId);

}
