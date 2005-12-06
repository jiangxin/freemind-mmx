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
/*$Id: HierarchicalIcons.java,v 1.1.2.2.6.2 2005-12-06 19:47:29 dpolivaev Exp $*/

package accessories.plugins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.extensions.UndoEventReceiver;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MultipleImage;

/** */
public class HierarchicalIcons extends PermanentNodeHookAdapter implements UndoEventReceiver {

    private HashMap /* of MindMapNode to a TreeSet */ nodeIconSets = new HashMap();
    
  
    public void shutdownMapHook()
    {
        // remove all icons:
        MindMapNode root = getController().getRootNode();
        removeIcons(root);
        super.shutdownMapHook();
    }
    /**
     * @param root
     */
    private void removeIcons(MindMapNode node)
    {
        node.setStateIcon(getName(),null);
        getController().nodeRefresh(node);
        for (Iterator i = node.childrenUnfolded(); i.hasNext();)
        {
            MindMapNode child = (MindMapNode) i.next();
            removeIcons(child);
        }
    }
    /**
     *  
     */
    public HierarchicalIcons() {
        super();

    }


    private void setStyle(MindMapNode node) {
        // precondition: all children are contained in nodeIconSets
        
        // gather all icons of my children and of me here:
        TreeSet iconSet = new TreeSet();
        for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
            MindMapNode child = (MindMapNode) i.next();
            addAccumulatedIconsToTreeSet(child, iconSet,
                    (TreeSet) nodeIconSets.get(child));
        }
        // remove my icons from the treeset:
        for (Iterator i = node.getIcons().iterator(); i.hasNext();)
        {
            MindIcon icon = (MindIcon) i.next();
            iconSet.remove(icon.getName());
        }
        boolean dirty = true;
        // look for a change:
        if(nodeIconSets.containsKey(node)) {
            TreeSet storedIconSet = (TreeSet) nodeIconSets.get(node);
            if(storedIconSet.equals(iconSet)) {
                dirty = false;
            }
        }
        nodeIconSets.put(node, iconSet);
        
        if (dirty) {
            if (iconSet.size() > 0) {
                // create multiple image:
                MultipleImage image = new MultipleImage(0.75);
                for (Iterator i = iconSet.iterator(); i.hasNext();) {
                    String iconName = (String) i.next();
//                    logger.info("Adding icon "+iconName + " to node "+ node.toString());
                    MindIcon icon = MindIcon.factory(iconName);
                    image.addImage(icon.getIcon());
                }
                node.setStateIcon(getName(), image);
            } else {
                node.setStateIcon(getName(),null);
            }
            getController().nodeRefresh(node);
        }
        
    }

    /**
     * @param child
     * @param iconSet
     * @param object
     */
    private void addAccumulatedIconsToTreeSet(MindMapNode child, TreeSet iconSet, TreeSet childsTreeSet) {
        for (Iterator i = child.getIcons().iterator(); i.hasNext();) {
            MindIcon icon = (MindIcon) i.next();
            iconSet.add(icon.getName());
        }
        if(childsTreeSet == null)
            return;
        for (Iterator i = childsTreeSet.iterator(); i.hasNext();) {
            String iconName = (String) i.next();
            iconSet.add(iconName);
        }
    }



    /*
     * (non-Javadoc)
     * 
     * @see freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode)
     */
    public void onAddChildren(MindMapNode newChildNode) {
        logger.finest("onAddChildren " + newChildNode);
        super.onAddChild(newChildNode);
        setStyleRecursive(newChildNode);
    }
    public void onRemoveChildren(MindMapNode removedChild, MindMapNode oldDad) {
        logger.finest("onRemoveChildren " + removedChild);
        super.onRemoveChildren(removedChild, oldDad);
        setStyleRecursive(oldDad);
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
     */
    public void onUpdateChildrenHook(MindMapNode updatedNode) {
        super.onUpdateChildrenHook(updatedNode);
        setStyleRecursive(updatedNode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
     */
    public void onUpdateNodeHook() {
        super.onUpdateNodeHook();
        setStyle(getNode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
     */
    public void invoke(MindMapNode node) {
        super.invoke(node);
        gatherLeavesAndSetStyle(node);
        gatherLeavesAndSetParentsStyle(node);
    }

    /**
     * @param node
     */
    private void gatherLeavesAndSetStyle(MindMapNode node) {
        if(node.getChildCount() == 0) {
	        // call setStyle for all leaves:
            setStyle(node);
            return;
        }
        for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
            MindMapNode child = (MindMapNode) i.next();
            gatherLeavesAndSetStyle(child);
        }
    }

    /**
     * @param node
     */
    private void gatherLeavesAndSetParentsStyle(MindMapNode node) {
        if(node.getChildCount() == 0) {
	        // call setStyleRecursive for all parents:
            if (node.getParentNode() != null) {
                setStyleRecursive(node.getParentNode());
            }
            return;
        }
        for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
            MindMapNode child = (MindMapNode) i.next();
            gatherLeavesAndSetParentsStyle(child);
        }
    }


    /**
     * @param node
     */
    private void setStyleRecursive(MindMapNode node) {
//        logger.finest("setStyle " + node);
        setStyle(node);
        // recurse:
        if (node.getParentNode() != null) {
            setStyleRecursive(node.getParentNode());
        }
    }

}

