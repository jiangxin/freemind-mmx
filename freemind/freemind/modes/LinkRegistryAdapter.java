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
/*$Id: LinkRegistryAdapter.java,v 1.1 2003-11-13 06:36:59 christianfoltin Exp $*/

package freemind.modes;

import freemind.modes.MindMapNode;
import java.util.HashMap; 
import java.util.Random;
import java.util.Vector;


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
        protected MindMapNode source;
        public MindMapNode getSource() { return this.source; };
        public ID_UsedStateAdapter(MindMapNode source, String ID) {
            this.source = source;
            this.ID = ID;
        };
        /** For cloning.*/
        protected ID_UsedStateAdapter() {
        };
        protected void clone(ID_UsedStateAdapter state) {
            this.source = state.source;
            this.ID = state.ID;
        }
    };
    /** This state interface expresses the state that a node has an ID.*/
    protected class ID_RegisteredAdapter extends ID_UsedStateAdapter implements ID_Registered  {
        public ID_RegisteredAdapter(MindMapNode source, String ID) {
            super(source, ID);
        };

        public ID_RegisteredAdapter(ID_PendingAdapter adapter) {
            clone(adapter);
        };
    };
    /** This state interface expresses the state that a node was recently cutted and waits to be inserted at another place.
        After inserting the states changes to ID_Registered.
    */
    public class ID_PendingAdapter  extends ID_UsedStateAdapter implements ID_Pending {
    };

    ////////////////////////////////////////////////////////////////////////////////////////
    ////   Attributes                                                                  /////
    ////////////////////////////////////////////////////////////////////////////////////////

    protected HashMap /* MindMapNode -> ID_BasicState. */ NodeToID;
    protected HashMap /* id -> vector of targets */  IDToTargets;
    /** The map the registry belongs to.*/
//     protected MindMap map;
    ////////////////////////////////////////////////////////////////////////////////////////
    ////   Methods                                                                     /////
    ////////////////////////////////////////////////////////////////////////////////////////
    public LinkRegistryAdapter(/*MindMap map*/) {
//         this.map = map;
        NodeToID = new HashMap();
        IDToTargets = new HashMap();
    };

    protected String generateUniqueID() {
        Random ran = new Random();
        String returnValue;
        do {
            returnValue = Integer.toString(ran.nextInt(2000000000));
        } while (IDToTargets.containsKey(returnValue));
        return returnValue;
    };

    
    /** The main method. Registeres a node with a new (or an existing) node-id. If the state of the id is pending,
     then it is set to registered again.
    */
    public ID_Registered registerLinkSource(MindMapNode source) {
        // id already exists?
        if(NodeToID.containsKey(source)) {
            ID_BasicState state = (ID_BasicState) NodeToID.get(source);
            if(state instanceof ID_Pending) {
                // the node is pending. set to registered again:
                ID_RegisteredAdapter reg = new ID_RegisteredAdapter((ID_PendingAdapter) state);
                NodeToID.put(source,reg);
                return reg;
            }
            if(state instanceof ID_Registered) 
                return (ID_Registered) state;
            // blank state.
            // is equal to no state.
        }
        // generate new id:
        String newID = generateUniqueID();
        ID_Registered state = new ID_RegisteredAdapter(source, newID);
        NodeToID.put(source,state);
        return state;
    };
        
    public ID_BasicState getState(MindMapNode node) {
        if(NodeToID.containsKey(node))
            return (ID_BasicState) NodeToID.get(node);
        return new ID_BlankAdapter();
    };

    protected String getIDString(MindMapNode node) { 
        if(NodeToID.containsKey(node)) {
            ID_BasicState state =  (ID_BasicState) NodeToID.get(node);
            return state.getID();
        }
        return null;
    }
        
    private Vector getAssignedTargetVector(ID_Registered state) {
        String id = state.getID();
        // look, if target is already present:
        Vector vec;
        if(IDToTargets.containsKey(id)) {
            vec = (Vector) IDToTargets.get(id);
        } else { 
            vec = new Vector();
            IDToTargets.put(id,vec);
        }
        return vec;
    }

    /** Method to keep track of the targets associated to a source node. This method also sets the new id to the target. 
        Moreover, it is not required that the source node is already registered. This will be done on the fly.*/
    public void registerLinkTarget(MindMapNode source, MindMapNode target) {
        ID_Registered state = registerLinkSource(source);
        Vector vec = getAssignedTargetVector(state);
        for(int i = 0 ; i < vec.size(); ++i) {
            if(vec.get(i) == target)
                return;
        }
        vec.add(target);
    };

    /** Exception if there are still targets registered.*/
    public void deregisterLinkSource(MindMapNode source)
        throws java.lang.IllegalArgumentException
    {
        ID_Registered state = registerLinkSource(source);
        Vector vec = getAssignedTargetVector(state);
        if(vec.size() != 0)
            throw new java.lang.IllegalArgumentException("Cannot remove a link source, if there are targets pointing to.");
        NodeToID.remove(source);
    }
                
        
    /** Sets all nodes beginning from source with its children to ID_Pending for later paste action.*/
    public ID_Pending cutLinkSource(MindMapNode source) { return null;};
    public void deregisterLinkTarget(MindMapNode source, MindMapNode target){
        ID_Registered state = registerLinkSource(source);
        Vector vec = getAssignedTargetVector(state);
        for(int i = 0 ; i < vec.size(); ++i) {
            if(vec.get(i) == target)
                vec.removeElementAt(i);
        }
    };

    /** Returns a Vector of Nodes that point to the given node.*/
    public Vector /* of MindMapNode s */ getAllTargets(MindMapNode source) { 
        ID_Registered state = registerLinkSource(source);
        Vector vec = getAssignedTargetVector(state);
        return vec;
    };


}
