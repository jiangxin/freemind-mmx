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
/*$Id: LinkRegistryAdapter.java,v 1.4 2003-11-19 20:36:29 christianfoltin Exp $*/

package freemind.modes;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import java.util.HashMap; 
import java.util.Random;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;



/** Interface for the registry, which manages the ids of nodes and the existing links in a map.
    Thus, this interface is bound to a map model, because other maps have a different registry.*/
public class LinkRegistryAdapter implements MindMapLinkRegistry {
    ////////////////////////////////////////////////////////////////////////////////////////
    ////   State Model                                                                 /////
    ////////////////////////////////////////////////////////////////////////////////////////
    /** State parent interface.*/
    public class ID_BasicStateAdapter implements ID_BasicState{
        protected String ID;
        /** Returns null for many states.*/
        public String getID() { return ID; };
        public String toString() { return "ID_BasicState, ID="+((ID==null)?"null":ID); };
    };
    /** This state interface expresses the state that a node is blank (i.e. without an id, normal state).*/
    public class ID_BlankAdapter extends ID_BasicStateAdapter implements ID_Blank  {};
    /** This state interface expresses the state that a node has an ID, but is abstract.*/
    protected abstract class ID_UsedStateAdapter  extends ID_BasicStateAdapter implements ID_UsedState {
        protected MindMapNode target;
        public MindMapNode getTarget() { return this.target; };
        public ID_UsedStateAdapter(MindMapNode target, String ID) {
            this.target = target;
            this.ID = ID;
        };
        /** For cloning.*/
        protected ID_UsedStateAdapter() {
        };
        protected void clone(ID_UsedStateAdapter state) {
            this.target = state.target;
            this.ID = state.ID;
        }
    };
    /** This state interface expresses the state that a node has an ID.*/
    protected class ID_RegisteredAdapter extends ID_UsedStateAdapter implements ID_Registered  {
        public ID_RegisteredAdapter(MindMapNode target, String ID) {
            super(target, ID);
        };

//         public ID_RegisteredAdapter(ID_PendingAdapter adapter) {
//             clone(adapter);
//         };
    };
//     /** This state interface expresses the state that a node was recently cutted and waits to be inserted at another place.
//         After inserting the states changes to ID_Registered.
//     */
//     public class ID_PendingAdapter  extends ID_UsedStateAdapter implements ID_Pending {
//     };

    ////////////////////////////////////////////////////////////////////////////////////////
    ////   Attributes                                                                  /////
    ////////////////////////////////////////////////////////////////////////////////////////

    protected HashMap /* MindMapNode = Target -> ID_BasicState. */ TargetToID;
    protected HashMap /* id -> vector of links whose TargetToID.get(target) == id.*/  IDToLinks;
    /** The map the registry belongs to.*/
//     protected MindMap map;

    // Logging:
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("freemind.modes.LinkRegistryAdapter");

    ////////////////////////////////////////////////////////////////////////////////////////
    ////   Methods                                                                     /////
    ////////////////////////////////////////////////////////////////////////////////////////
    public LinkRegistryAdapter(/*MindMap map*/) {
//         this.map = map;
        TargetToID = new HashMap();
        IDToLinks = new HashMap();
        logger.setLevel(java.util.logging.Level.WARNING);
         logger.info("New Registry");
    };

    protected String generateUniqueID(String proposedID) {
        Random ran = new Random();
        String myProposedID = new String((proposedID != null)?proposedID:"");
        String returnValue;
        do {
            if(!myProposedID.equals("")) {
                // there is a proposal:
                returnValue = myProposedID;
                // this string is tried only once:
                myProposedID="";
            } else {
                returnValue = Integer.toString(ran.nextInt(2000000000));
            }
        } while (IDToLinks.containsKey(returnValue));
        return returnValue;
    };

    

    /** The main method. Registeres a node with a new (or an existing) node-id. If the state of the id is pending,
     then it is set to registered again.
    */
    public ID_Registered registerLinkTarget(MindMapNode target) {
        return registerLinkTarget(target, null);
    }

    public ID_Registered registerLinkTarget(MindMapNode target, String proposedID) {
        // id already exists?
        if(TargetToID.containsKey(target)) {
            ID_BasicState state = (ID_BasicState) TargetToID.get(target);
            if(state instanceof ID_Registered) 
                return (ID_Registered) state;
            // blank state.
            // is equal to no state.
        }
        // generate new id:
        String newID = generateUniqueID(proposedID);
        ID_Registered state = new ID_RegisteredAdapter(target, newID);
        TargetToID.put(target,state);
         logger.info("Register target node:"+target+", with ID="+newID);
        return state;
    };
        
    public ID_BasicState getState(MindMapNode node) {
        if(TargetToID.containsKey(node))
            return (ID_BasicState) TargetToID.get(node);
        return new ID_BlankAdapter();
    };

    protected String getIDString(MindMapNode node) { 
        if(TargetToID.containsKey(node)) {
            ID_BasicState state =  (ID_BasicState) TargetToID.get(node);
            return state.getID();
        }
        return null;
    }
        
    
    private Vector  /* of MindMapLink s */ getAssignedLinksVector(ID_Registered state) {
        String id = state.getID();
        // look, if target is already present:
        Vector vec;
        if(IDToLinks.containsKey(id) ) {
            vec = (Vector) IDToLinks.get(id);
        } else { 
            vec = new Vector();
            IDToLinks.put(id,vec);
        }
        logger.fine("getAssignedLinksVector "+vec);
        return vec;
    }

    /** Exception if there are still targets registered.*/
    public void deregisterLinkTarget(MindMapNode target)
        throws java.lang.IllegalArgumentException
    {
        ID_BasicState state = getState(target);
        if(state instanceof ID_Registered)
            {
                Vector vec = getAssignedLinksVector((ID_Registered) state);
                for(int i = 0 ; i < vec.size(); ++i) {
                    deregisterLink((MindMapLink) vec.get(i));
                }
                //         if(vec.size() != 0)
                //             throw new java.lang.IllegalArgumentException("Cannot remove a link target, if there are sources pointing to.");
                logger.info("Deregister target node:"+target);
                TargetToID.remove(target);
            }
    }
                
    /** Method to keep track of the sources associated to a target node. This method also sets the new id to the target. 
        Moreover, it is not required that the target node is already registered. This will be done on the fly.*/
    public void registerLink(MindMapLink link) 
        throws java.lang.IllegalArgumentException
    {
        if((link.getSource()==null)||(link.getTarget()==null)||(link.getDestinationLabel()==null))
            throw new java.lang.IllegalArgumentException("Illegal link specification."+link);
        MindMapNode source = link.getSource();
        MindMapNode target = link.getTarget();
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        // already present?
        for(int i = 0 ; i < vec.size(); ++i) {
            if(vec.get(i) == source)
                return;
        }
        vec.add(link);
        logger.info("Register link ("+link+") from source node:"+source+" to target " + target);

    };

        
    public void deregisterLink(MindMapLink link){
        MindMapNode source = link.getSource();
        MindMapNode target = link.getTarget();
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        for(int i = 0 ; i < vec.size(); ++i) {
            logger.info("Test for equal node:"+source+" to vector(i) " + vec.get(i));
            if(vec.get(i) == link)
                {
                    vec.removeElementAt(i);
                    logger.info("Deregister link  ("+link+") from source node:"+source+" to target " + target);
                }
        }
    };

    /** Returns a Vector of Nodes that point to the given target node.*/
    public Vector /* of MindMapNode s */ getAllSources(MindMapNode target) { 
        Vector returnValue;
        returnValue = new Vector();
        ID_BasicState state = getState(target);
        if(state instanceof ID_Registered)
            {
                Vector vec = getAssignedLinksVector((ID_Registered) state);
                for(int i = 0 ; i < vec.size(); ++i) {
                    returnValue.add( ((MindMapLink) vec.get(i)).getSource() );
                }
            }
        return returnValue;
    };

    public Vector /* of MindMapLink s */ getAllLinks(MindMapNode node) { 
        Vector returnValue = new Vector();
        returnValue.addAll(getAllLinksIntoMe( node ));
        returnValue.addAll(getAllLinksFromMe( node ));
        logger.fine("All links  ("+returnValue+") from  node:"+node);
        return returnValue;
    };

    /** @return returns all links to this node.*/
    public Vector /* of MindMapLink s */ getAllLinksIntoMe(MindMapNode target){
        Vector returnValue = new Vector();
        ID_BasicState state = getState(target);
        if(getState(target) instanceof ID_Registered)
            {
                Vector vec = getAssignedLinksVector((ID_Registered) state);
                /* clone */
                for(int i = 0 ; i < vec.size(); ++i) {
                    returnValue.add( vec.get(i) );
                }
            }
        return returnValue;
    }

    /** @return returns all links from this node.*/
    public Vector /* of MindMapLink s */ getAllLinksFromMe(MindMapNode source){
        Vector returnValue = new Vector();
        Collection values = IDToLinks.values();
        for(Iterator i = values.iterator(); i.hasNext();) {
            Vector linkVector = (Vector) i.next();
            for(int j = 0; j < linkVector.size(); ++j) {
                MindMapLink link = (MindMapLink) linkVector.get(j);
                if(link.getSource() == source)
                    returnValue.add(link);
            }
        }
        return returnValue;
    }

    public String getLabel(MindMapNode target) { 
        ID_BasicState state = getState(target);
        if(getState(target) instanceof ID_Registered)
            {
                return ((ID_Registered) state).getID();
            }
        return null;
    }

    public void        cutNode(MindMapNode target) {
        Vector links = getAllLinksFromMe(target);
        for(int i = 0; i  < links.size(); ++i) {
            deregisterLink((MindMapLink) links.get(i));
        }
        deregisterLinkTarget(target);
        for (ListIterator e = target.childrenUnfolded(); e.hasNext(); ) {
            MindMapNodeModel child = (MindMapNodeModel)e.next();            
            cutNode(child);
        }
    }


}
