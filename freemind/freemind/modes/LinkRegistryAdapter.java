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
/*$Id: LinkRegistryAdapter.java,v 1.2 2003-11-16 22:15:15 christianfoltin Exp $*/

package freemind.modes;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import java.util.HashMap; 
import java.util.Random;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;



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
    protected HashMap /* id -> vector of sources */  IDToSources;
    /** The map the registry belongs to.*/
//     protected MindMap map;
    ////////////////////////////////////////////////////////////////////////////////////////
    ////   Methods                                                                     /////
    ////////////////////////////////////////////////////////////////////////////////////////
    public LinkRegistryAdapter(/*MindMap map*/) {
//         this.map = map;
        TargetToID = new HashMap();
        IDToSources = new HashMap();
//         System.out.println("New Registry");
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
        } while (IDToSources.containsKey(returnValue));
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
//             if(state instanceof ID_Pending) {
//                 // the node is pending. set to registered again:
//                 ID_RegisteredAdapter reg = new ID_RegisteredAdapter((ID_PendingAdapter) state);
//                 TargetToID.put(target,reg);
//                 return reg;
//             }
            if(state instanceof ID_Registered) 
                return (ID_Registered) state;
            // blank state.
            // is equal to no state.
        }
        // generate new id:
        String newID = generateUniqueID(proposedID);
        ID_Registered state = new ID_RegisteredAdapter(target, newID);
        TargetToID.put(target,state);
        target.setLabel(state.getID());
//         System.out.println("Register target node:"+target+", with ID="+newID);
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
        
    private Vector getAssignedLinksVector(ID_Registered state) {
        String id = state.getID();
        // look, if target is already present:
        Vector vec;
        if(IDToSources.containsKey(id) ) {
            vec = (Vector) IDToSources.get(id);
        } else { 
            vec = new Vector();
            IDToSources.put(id,vec);
        }
//         System.out.println("getAssignedLinksVector "+vec);
        return vec;
    }

    /** Method to keep track of the sources associated to a target node. This method also sets the new id to the target. 
        Moreover, it is not required that the target node is already registered. This will be done on the fly.*/
    public void registerLink(MindMapNode source, MindMapNode target) {
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        for(int i = 0 ; i < vec.size(); ++i) {
            if(vec.get(i) == source)
                return;
        }
        vec.add(source);
//         System.out.println("Register link from source node:"+source+" to target " + target);

    };

    /** Exception if there are still targets registered.*/
    public void deregisterLinkTarget(MindMapNode target)
        throws java.lang.IllegalArgumentException
    {
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        if(vec.size() != 0)
            throw new java.lang.IllegalArgumentException("Cannot remove a link target, if there are sources pointing to.");
//         System.out.println("Register target node:"+target);
        target.setLabel(null);
        TargetToID.remove(target);
    }
                
        
//     /** Sets all nodes beginning from source with its children to ID_Pending for later paste action.*/
//     public ID_Pending cutLinkTarget(MindMapNode target) { return null;};

    public void deregisterLink(MindMapNode source, MindMapNode target){
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        for(int i = 0 ; i < vec.size(); ++i) {
//             System.out.println("Test for equal node:"+source+" to vector(i) " + vec.get(i));
            if(vec.get(i) == source)
                {
                    vec.removeElementAt(i);
//                     System.out.println("Deregister source node:"+source+" to target " + target);
                }
        }
    };

    /** Returns a Vector of Nodes that point to the given target node.*/
    public Vector /* of MindMapNode s */ getAllSources(MindMapNode target) { 
        ID_Registered state = registerLinkTarget(target);
        Vector vec = getAssignedLinksVector(state);
        return vec;
    };


}
