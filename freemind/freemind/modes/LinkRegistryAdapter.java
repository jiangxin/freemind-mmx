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
	 * All elements put into this sort of vectors are put into the SourceToLinks, too.
	 * This structure is kept synchronous to the IDToLinks structure, but reversed.
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

	protected HashMap /* source -> vector of links with same source */SourceToLinks = new HashMap();

	/** State parent interface. */
	public interface ID_BasicState {
		/** Returns null for many states. */
		public String getID();

		public String toString();
	};

	/**
	 * This state interface expresses the state that a node is blank (i.e.
	 * without an id, normal state).
	 */
	public interface ID_Blank extends ID_BasicState {
	};

	/**
	 * This state interface expresses the state that a node has an ID, but is
	 * abstract.
	 */
	public interface ID_UsedState extends ID_BasicState {
		public MindMapNode getTarget();
	};

	/** This state interface expresses the state that a node has an ID. */
	public interface ID_Registered extends ID_UsedState {
	};

	// //////////////////////////////////////////////////////////////////////////////////////
	// // State Model /////
	// //////////////////////////////////////////////////////////////////////////////////////
	/** State parent interface. */
	public class ID_BasicStateAdapter implements ID_BasicState {
		protected String ID;

		/** Returns null for many states. */
		public String getID() {
			return ID;
		};

		public String toString() {
			return "ID_BasicState, ID=" + ((ID == null) ? "null" : ID);
		};
	};

	/**
	 * This state interface expresses the state that a node is blank (i.e.
	 * without an id, normal state).
	 */
	public class ID_BlankAdapter extends ID_BasicStateAdapter implements
			ID_Blank {
	};

	/**
	 * This state interface expresses the state that a node has an ID, but is
	 * abstract.
	 */
	protected abstract class ID_UsedStateAdapter extends ID_BasicStateAdapter
			implements ID_UsedState {
		protected MindMapNode target;

		public MindMapNode getTarget() {
			return this.target;
		};

		public ID_UsedStateAdapter(MindMapNode target, String ID) {
			this.target = target;
			this.ID = ID;
		};

		/** For cloning. */
		protected ID_UsedStateAdapter() {
		};

		protected void clone(ID_UsedStateAdapter state) {
			this.target = state.target;
			this.ID = state.ID;
		}
	};

	/** This state interface expresses the state that a node has an ID. */
	protected class ID_RegisteredAdapter extends ID_UsedStateAdapter implements
			ID_Registered {
		public ID_RegisteredAdapter(MindMapNode target, String ID) {
			super(target, ID);
		};

		// public ID_RegisteredAdapter(ID_PendingAdapter adapter) {
		// clone(adapter);
		// };
	};

	// /** This state interface expresses the state that a node was recently
	// cutted and waits to be inserted at another place.
	// After inserting the states changes to ID_Registered.
	// */
	// public class ID_PendingAdapter extends ID_UsedStateAdapter implements
	// ID_Pending {
	// };

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Attributes /////
	// //////////////////////////////////////////////////////////////////////////////////////

	protected HashMap /* MindMapNode = Target -> ID_BasicState. */TargetToID;
	protected HashMap /* MindMapNode = ID_BasicState -> ID. */IDToTarget;
	protected HashMap /* id -> vector of links whose TargetToID.get(target) == id. */IDToLinks;
	protected HashMap /* id -> link */IDToLink;
	protected HashSet /* id */mLocallyLinkedIDs;
	/** The map the registry belongs to. */
	// protected MindMap map;

	// Logging: for applets the logging must be anonymously. This will be
	// generalized later. fc, 22.12.2003.
	private static java.util.logging.Logger logger = java.util.logging.Logger
			.getAnonymousLogger(); // getLogger("freemind.modes.LinkRegistryAdapter");
	// bug fix from Dimitri.
	protected static Random ran = new Random();

	// //////////////////////////////////////////////////////////////////////////////////////
	// // Methods /////
	// //////////////////////////////////////////////////////////////////////////////////////
	public LinkRegistryAdapter(/* MindMap map */) {
		// this.map = map;
		TargetToID = new HashMap();
		IDToTarget = new HashMap();
		IDToLinks = new HashMap();
		IDToLink = new HashMap();
		mLocallyLinkedIDs = new HashSet();
		// logger.fine("New Registry");
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
		return _registerLinkTarget(pTarget, pProposedID).getID();
	}

	/**
	 * The main method. Registeres a node with a new (or an existing) node-id.
	 * If the state of the id is pending, then it is set to registered again.
	 */
	public ID_Registered _registerLinkTarget(MindMapNode target) {
		return _registerLinkTarget(target, null);
	}

	public ID_Registered _registerLinkTarget(MindMapNode target,
			String proposedID) {
		// id already exists?
		if (TargetToID.containsKey(target)) {
			ID_BasicState state = (ID_BasicState) TargetToID.get(target);
			if (state instanceof ID_Registered)
				return (ID_Registered) state;
			// blank state.
			// is equal to no state.
		}
		// generate new id:
		String newID = generateUniqueID(proposedID);
		ID_Registered state = new ID_RegisteredAdapter(target, newID);
		TargetToID.put(target, state);
		IDToTarget.put(newID, target);

		// logger.fine("Register target node:"+target+", with ID="+newID);
		getAssignedLinksVector(state);/*
									 * This is to allocate the link target in
									 * the IDToLinks map!.
									 */
		return state;
	};

	public ID_BasicState getState(MindMapNode node) {
		if (TargetToID.containsKey(node))
			return (ID_BasicState) TargetToID.get(node);
		return new ID_BlankAdapter();
	};

	public MindMapNode getTargetForID(String ID) {
		final Object target = IDToTarget.get(ID);
		return (MindMapNode) target;
	}

	private Vector /* of MindMapLink s */getAssignedLinksVector(
			ID_Registered state) {
		String id = state.getID();
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
		ID_BasicState state = getState(target);
		if (state instanceof ID_Registered) {
			Vector vec = getAssignedLinksVector((ID_Registered) state);
			for (int i = vec.size() - 1; i >= 0; --i) {
				deregisterLink((MindMapLink) vec.get(i));
			}
			// if(vec.size() != 0)
			// throw new
			// java.lang.IllegalArgumentException("Cannot remove a link target, if there are sources pointing to.");
			// logger.fine("Deregister target node:"+target);
			TargetToID.remove(target);
			IDToTarget.remove(state.getID());
			IDToLinks.remove(state.getID());
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
		ID_Registered state = _registerLinkTarget(target);
		Vector vec = getAssignedLinksVector(state);
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
		ID_Registered state = _registerLinkTarget(target);
		Vector vec = getAssignedLinksVector(state);
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

	/** Returns a Vector of Nodes that point to the given target node. */
	public Vector /* of MindMapNode s */getAllSources(MindMapNode target) {
		Vector returnValue;
		returnValue = new Vector();
		ID_BasicState state = getState(target);
		if (state instanceof ID_Registered) {
			Vector vec = getAssignedLinksVector((ID_Registered) state);
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

	/** @return returns all links to this node. */
	public Vector /* of MindMapLink s */getAllLinksIntoMe(MindMapNode target) {
		Vector returnValue = new Vector();
		ID_BasicState state = getState(target);
		if (getState(target) instanceof ID_Registered) {
			Vector vec = getAssignedLinksVector((ID_Registered) state);
			/* "clone" */
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	/** @return returns all links from this node. */
	public Vector /* of MindMapLink s */getAllLinksFromMe(MindMapNode source) {
		Vector returnValue = new Vector();
		Collection vec = (Collection) SourceToLinks.get(source);
		if (vec != null) {
			returnValue.addAll(vec);
		}
		return returnValue;
	}

	public String getLabel(MindMapNode target) {
		ID_BasicState state = getState(target);
		if (getState(target) instanceof ID_Registered) {
			return ((ID_Registered) state).getID();
		}
		return null;
	}

	public void cutNode(MindMapNode target) {
		// logger.entering("LinkRegistryAdapter", "cutNode", target);
		ID_BasicState state = getState(target);
		if (state instanceof ID_Registered) {
			// there is a registered target id.
			String id = getLabel(target);
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
