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
/*$Id: LinkRegistryAdapter.java,v 1.10.18.3.12.6 2008/12/09 21:09:43 christianfoltin Exp $*/

package freemind.modes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;

/**
 * Interface for the registry, which manages the ids of nodes and the existing
 * links in a map. Thus, this interface is bound to a map model, because other
 * maps have a different registry.
 */
public class LinkRegistryAdapter implements MindMapLinkRegistry {
	/**
	 * All elements put into this sort of vectors are put into the
	 * SourceToLinks, too. This structure is kept synchronous to the IDToLinks
	 * structure, but reversed.
	 * 
	 * @author foltin
	 * @date 23.01.2012
	 */
	private class SynchronousVector extends Vector {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Vector#add(java.lang.Object)
		 */
		public synchronized boolean add(Object pE) {
			boolean add = super.add(pE);
			if (pE instanceof MindMapLink) {
				MindMapLink link = (MindMapLink) pE;
				MindMapNode source = link.getSource();
				if (!SourceToLinks.containsKey(source)) {
					SourceToLinks.put(source, new Vector());
				}
				((Vector) SourceToLinks.get(source)).add(pE);
			}
			return add;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Vector#removeElementAt(int)
		 */
		public synchronized void removeElementAt(int pIndex) {
			MindMapLink link = (MindMapLink) get(pIndex);
			MindMapNode source = link.getSource();
			Vector vector = (Vector) SourceToLinks.get(source);
			if (vector != null) {
				vector.remove(link);
				if (vector.isEmpty()) {
					SourceToLinks.remove(source);
				}
			}
			super.removeElementAt(pIndex);
		}

	}

	/** source -> vector of links with same source */
	protected HashMap SourceToLinks = new HashMap();

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Attributes /////
	// //////////////////////////////////////////////////////////////////////////////////////

	/** MindMapNode = Target -> ID. */
	protected HashMap TargetToID;
	/** MindMapNode-> ID. */
	protected HashMap IDToTarget;
	/** id -> vector of links whose TargetToID.get(target) == id. */
	protected HashMap IDToLinks;
	/** id -> link */
	protected HashMap IDToLink;
	/** id */
	protected HashSet mLocallyLinkedIDs;

	// bug fix from Dimitri.
	protected static Random ran = new Random();

	protected static java.util.logging.Logger logger = null;

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Methods /////
	// //////////////////////////////////////////////////////////////////////////////////////
	public LinkRegistryAdapter(/* MindMap map */) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		TargetToID = new HashMap();
		IDToTarget = new HashMap();
		IDToLinks = new HashMap();
		IDToLink = new HashMap();
		mLocallyLinkedIDs = new HashSet();
	};

	public String generateUniqueID(String proposedID) {
		return generateID(proposedID, IDToLinks, "ID_");
	}

	public String generateUniqueLinkID(String proposedID) {
		return generateID(proposedID, IDToLink, "Arrow_ID_");
	};

	private String generateID(String proposedID, HashMap hashMap, String prefix) {
		String myProposedID = new String((proposedID != null) ? proposedID : "");
		String returnValue;
		do {
			if (!myProposedID.isEmpty()) {
				// there is a proposal:
				returnValue = myProposedID;
				// this string is tried only once:
				myProposedID = "";
			} else {
				/*
				 * The prefix is to enable the id to be an ID in the sense of
				 * XML/DTD.
				 */
				returnValue = prefix
						+ Integer.toString(ran.nextInt(2000000000));
			}
		} while (hashMap.containsKey(returnValue));
		return returnValue;
	};

	public void registerLinkTarget(MindMapNode pTarget) {
		_registerLinkTarget(pTarget);

	}

	public String registerLinkTarget(MindMapNode pTarget, String pProposedID) {
		return _registerLinkTarget(pTarget, pProposedID);
	}

	/**
	 * The main method. Registeres a node with a new (or an existing) node-id.
	 */
	public String _registerLinkTarget(MindMapNode target) {
		return _registerLinkTarget(target, null);
	}

	public String _registerLinkTarget(MindMapNode target, String proposedID) {
		// id already exists?
		if (TargetToID.containsKey(target)) {
			String id = (String) TargetToID.get(target);
			if (id != null)
				return id;
			// blank state.
			// is equal to no state.
		}
		// generate new id:
		String newId = generateUniqueID(proposedID);
		TargetToID.put(target, newId);
		IDToTarget.put(newId, target);

		// logger.fine("Register target node:"+target+", with ID="+newID);
		/*
		 * This is to allocate the link target in
		 * the IDToLinks map!.
		 */
		getAssignedLinksVector(newId);
		return newId;
	};

	/**
	 * @param node
	 * @return null, if not registered.
	 */
	public String getState(MindMapNode node) {
		if (TargetToID.containsKey(node))
			return (String) TargetToID.get(node);
		return null;
	};

	public MindMapNode getTargetForID(String ID) {
		final Object target = IDToTarget.get(ID);
		return (MindMapNode) target;
	}

	/** @return a Vector of {@link MindMapLink}s */
	private Vector getAssignedLinksVector(
			String newId) {
		String id = newId;
		// look, if target is already present:
		Vector vec;
		if (IDToLinks.containsKey(id)) {
			vec = (Vector) IDToLinks.get(id);
		} else {
			vec = new SynchronousVector();
			IDToLinks.put(id, vec);
		}

		// Dimitry : logger is a performance killer here
		// //logger.fine("getAssignedLinksVector "+vec);
		return vec;
	}

	/** If there are still targets registered, they are removed, too. */
	public void deregisterLinkTarget(MindMapNode target)
			throws java.lang.IllegalArgumentException {
		String id = getState(target);
		if (id != null) {
			Vector vec = getAssignedLinksVector(id);
			for (int i = vec.size() - 1; i >= 0; --i) {
				deregisterLink((MindMapLink) vec.get(i));
			}
			// logger.fine("Deregister target node:"+target);
			TargetToID.remove(target);
			IDToTarget.remove(id);
			IDToLinks.remove(id);
		}
	}

	/**
	 * Method to keep track of the sources associated to a target node. This
	 * method also sets the new id to the target. Moreover, it is not required
	 * that the target node is already registered. This will be done on the fly.
	 */
	public void registerLink(MindMapLink link)
			throws java.lang.IllegalArgumentException {
		if ((link.getSource() == null) || (link.getTarget() == null)
				|| (link.getDestinationLabel() == null))
			throw new java.lang.IllegalArgumentException(
					"Illegal link specification." + link);
		MindMapNode source = link.getSource();
		MindMapNode target = link.getTarget();
		logger.info("Register link (" + link + ") from source node:" + source
				+ " to target " + target);
		String id = _registerLinkTarget(target);
		Vector vec = getAssignedLinksVector(id);
		// already present?
		for (int i = 0; i < vec.size(); ++i) {
			if (vec.get(i) == link)
				return;
		}
		vec.add(link);
		String uniqueID = link.getUniqueID();
		if (uniqueID == null) {
			((LinkAdapter) link).setUniqueID(generateUniqueLinkID(uniqueID));
			uniqueID = link.getUniqueID();
		}
		if (IDToLink.containsKey(uniqueID)) {
			if (IDToLink.get(uniqueID) != link) {
				logger.warning("link with duplicated unique id found:" + link);
				// new id:
				((LinkAdapter) link)
						.setUniqueID(generateUniqueLinkID(uniqueID));
			}
		}
		IDToLink.put(uniqueID, link);
	};

	public void deregisterLink(MindMapLink link) {
		MindMapNode source = link.getSource();
		MindMapNode target = link.getTarget();
		String id = _registerLinkTarget(target);
		Vector vec = getAssignedLinksVector(id);
		for (int i = vec.size() - 1; i >= 0; --i) {
			// logger.fine("Test for equal node:"+source+" to vector(i) " +
			// vec.get(i));
			if (vec.get(i) == link) {
				vec.removeElementAt(i);
				// logger.fine("Deregister link  ("+link+") from source node:"+source+" to target "
				// + target);
			}
		}
		IDToLink.remove(link.getUniqueID());
	};

	public MindMapLink getLinkForID(String ID) {
		if (IDToLink.containsKey(ID)) {
			return (MindMapLink) IDToLink.get(ID);
		}
		return null;
	}

	/** @return Returns a Vector of {@link MindMapNode}s that point to the given target node. */
	public Vector /* of MindMapNode s */getAllSources(MindMapNode target) {
		Vector returnValue;
		returnValue = new Vector();
		String id = getState(target);
		if (id != null) {
			Vector vec = getAssignedLinksVector(id);
			for (int i = 0; i < vec.size(); ++i) {
				returnValue.add(((MindMapLink) vec.get(i)).getSource());
			}
		}
		return returnValue;
	};

	public Vector /* of MindMapLink s */getAllLinks(MindMapNode node) {
		Vector returnValue = new Vector();
		returnValue.addAll(getAllLinksIntoMe(node));
		returnValue.addAll(getAllLinksFromMe(node));
		// Dimitry : logger is a performance killer here
		// //logger.fine("All links  ("+returnValue+") from  node:"+node);
		return returnValue;
	};

	/** @return returns all links to this node as {@link MindMapLink} vector. */
	public Vector getAllLinksIntoMe(MindMapNode target) {
		Vector returnValue = new Vector();
		String id = getState(target);
		if (id != null) {
			Vector vec = getAssignedLinksVector(id);
			/* "clone" */
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	/** @return returns all links from this node as {@link MindMapLink} vector. */
	public Vector getAllLinksFromMe(MindMapNode source) {
		Vector returnValue = new Vector();
		Collection vec = (Collection) SourceToLinks.get(source);
		if (vec != null) {
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	public String getLabel(MindMapNode target) {
		return getState(target);
	}

	public void cutNode(MindMapNode target) {
		// logger.entering("LinkRegistryAdapter", "cutNode", target);
		String id = getState(target);
		if (id != null) {
			// there is a registered target id.
			// deregister all links to me:
			Vector links = getAllLinksIntoMe(target);
			for (int i = links.size() - 1; i >= 0; --i) {
				MindMapLink link = (MindMapLink) links.get(i);
				deregisterLink(link);
			}
		}
		// deregister all links from me:
		Vector links = getAllLinksFromMe(target);
		for (int i = links.size() - 1; i >= 0; --i) {
			MindMapLink link = (MindMapLink) links.get(i);
			deregisterLink(link);
		}
		// and process my sons:
		for (ListIterator e = target.childrenUnfolded(); e.hasNext();) {
			MindMapNode child = (MindMapNode) e.next();
			cutNode(child);
		}
		// logger.exiting("LinkRegistryAdapter", "cutNode", target);
	}

	public void registerLocalHyperlinkId(String pTargetId) {
		mLocallyLinkedIDs.add(pTargetId);
	}

	public boolean isTargetOfLocalHyperlinks(String pTargetId) {
		return mLocallyLinkedIDs.contains(pTargetId);
	}

}
