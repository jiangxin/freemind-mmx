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
 * Created on 21.08.2004
 */
/*$Id: NodeUpAction.java,v 1.1.4.1 2004-10-17 23:00:10 dpolivaev Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.FoldAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.NodeListMemberType;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;


public class NodeUpAction extends AbstractAction implements ActorXml{
    private final ControllerAdapter modeController;
    private static Logger logger;
    public NodeUpAction(ControllerAdapter modeController) {
        super(modeController.getText("node_up"));
        this.modeController = modeController;
        modeController.getActionFactory().registerActor(this, getDoActionClass());
        if(logger == null) {
            logger = modeController.getFrame().getLogger(this.getClass().getName());
        }
    }
    public void actionPerformed(ActionEvent e) {
        moveNodes(modeController.getSelected(), modeController.getSelecteds(), -1);
    }
    /**
     * @param selected
     * @param selecteds
     * @param i
     */
    public void moveNodes(MindMapNode selected, List selecteds, int direction) {
        MoveNodesAction doAction = createMoveNodesAction(selected, selecteds, direction);
        MoveNodesAction undoAction = createMoveNodesAction(selected, selecteds, -direction);
        modeController.getActionFactory().startTransaction((String) getValue(NAME));
		modeController.getActionFactory().executeAction(new ActionPair(doAction, undoAction));
        modeController.getActionFactory().endTransaction((String) getValue(NAME));
    }
    public void _moveNodes(MindMapNode selected, List selecteds, int direction) {
        Comparator comparator =  (direction==-1)?null:new Comparator(){

            public int compare(Object o1, Object o2) {
                int i1 = ((Integer) o1).intValue();
                int i2 = ((Integer) o2).intValue();
                return i2 - i1;
            }
        };
        if(!selected.isRoot()) {
            MindMapNode parent = selected.getParentNode();
            // multiple move:
            Vector sortedChildren = getSortedSiblings(parent);
            TreeSet range = new TreeSet(comparator);
            for (Iterator i = selecteds.iterator(); i.hasNext();) {
                MindMapNode node = (MindMapNode) i.next();
                if(node.getParent() != parent) {
                    logger.warning("Not all selected nodes have the same parent.");
                    return;
                }
                range.add(new Integer(sortedChildren.indexOf(node)));
            }
            // test range for adjacent nodes:
            Integer last = (Integer) range.iterator().next();
            for (Iterator i = range.iterator(); i.hasNext();) {
                Integer newInt = (Integer) i.next();
                if(Math.abs(newInt.intValue() - last.intValue()) > 1) {
                    logger.warning("Not adjacent nodes. Skipped. ");
                    return;
                }
                last = newInt;
            }
            for (Iterator i = range.iterator(); i.hasNext();) {
                Integer position = (Integer) i.next();
                // from above:
                MindMapNode node = (MindMapNode) sortedChildren.get(position.intValue());
                moveNodeTo(node, parent, direction);
            }
            modeController.getView().selectAsTheOnlyOneSelected(
                  selected.getViewer());
            modeController.getView().scrollNodeToVisible(
                  selected.getViewer());
            for (Iterator i = range.iterator(); i.hasNext();) {
                Integer position = (Integer) i.next();
                // from above:
                MindMapNode node = (MindMapNode) sortedChildren.get(position.intValue());
                modeController.getView().makeTheSelected(node.getViewer());
            }
            modeController.getController().obtainFocusForSelected(); // focus fix
        }
    }
    /**
     * The direction is used if side left and right are present. then the next suitable place on the same side#
     is searched. if there is no such place, then the side is changed.
     @return returns the new index.
     */
    public int moveNodeTo(MindMapNode newChild, MindMapNode parent, int direction){
        MapAdapter model = modeController.getModel();
        int index = model.getIndexOfChild(parent, newChild);
        int newIndex = index;
        if(newChild.isLeft() != null) {
            int maxIndex = parent.getChildCount(); 
            Vector sortedNodesIndices = getSortedSiblings(parent);
            int newPositionInVector = sortedNodesIndices.indexOf(newChild) + direction;
            if(newPositionInVector < 0) {
                newPositionInVector = maxIndex-1;
            }
            if(newPositionInVector  >= maxIndex) {
                newPositionInVector = 0;
            }
            MindMapNode destinationNode =(MindMapNode) sortedNodesIndices.get(newPositionInVector); 
            newIndex = model.getIndexOfChild(parent, destinationNode);
            newChild.setLeft(destinationNode.isLeft().getValue());
            model.removeNodeFromParent(newChild);
            model.insertNodeInto(newChild,parent,newIndex);
            modeController.nodeStructureChanged(parent);
        }
        return newIndex;
    }

    /** Sorts nodes by their left/right status. The left are first.
     * @param node
     * @return
     */
    private Vector getSortedSiblings(MindMapNode node) {
        Vector nodes = new Vector();
        for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
            nodes.add(i.next());
        }
        Collections.sort(nodes, new Comparator(){

            public int compare(Object o1, Object o2) {
                if (o1 instanceof MindMapNode) {
                    MindMapNode n1 = (MindMapNode) o1;
                    if (o2 instanceof MindMapNode) {
                        MindMapNode n2 = (MindMapNode) o2;
                        if(n1.isLeft() != null && n2.isLeft()!= null) {
                            // left is less than right
                            int b1 = (n1.isLeft().getValue())?0:1;
                            int b2 = (n2.isLeft().getValue())?0:1;
                            return b1 - b2;
                        }
                    }
                }
                throw new IllegalArgumentException("Elements in LeftRightComparator are not comparable.");
            }
        });
        //logger.finest("Sorted nodes "+ nodes);
        return nodes;
    }
    public void act(XmlAction action) {
        if (action instanceof MoveNodesAction) {
            MoveNodesAction moveAction = (MoveNodesAction) action;
            MindMapNode selected = modeController.getNodeFromID(moveAction.getNode());
            Vector selecteds = new Vector();
            for (Iterator i = moveAction.getNodeListMember().iterator(); i.hasNext();) {
                NodeListMemberType node = (NodeListMemberType) i.next();
                selecteds.add(modeController.getNodeFromID(node.getNode()));
            }
            _moveNodes(selected, selecteds, moveAction.getDirection());
        }
    }
    public Class getDoActionClass() {
        return MoveNodesAction.class;
    }
    private MoveNodesAction createMoveNodesAction(MindMapNode selected, List selecteds, int direction) {
        try {
            MoveNodesAction moveAction = modeController.getActionXmlFactory().createMoveNodesAction();
            moveAction.setDirection(direction);
            moveAction.setNode(selected.getObjectId(modeController));
            // selectedNodes list 
            for (Iterator i = selecteds.iterator(); i.hasNext();) {
                MindMapNode node = (MindMapNode) i.next();
                NodeListMember nodeListMember = modeController.getActionXmlFactory().createNodeListMember();
                nodeListMember.setNode(node.getObjectId(modeController));
                moveAction.getNodeListMember().add(nodeListMember);
            } 
            return moveAction;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
        
    }
}