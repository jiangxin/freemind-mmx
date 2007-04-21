/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/
/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;

import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.MindMapActions.MouseWheelEventHandler;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 *
 */
public class UnfoldAll extends MindMapNodeHookAdapter  {

    public static class Registration implements HookRegistration, MouseWheelEventHandler {

        private final MindMapController controller;
        private final MindMap mMap;
        private final java.util.logging.Logger logger;
        private UnfoldAll hookInstance;

        public Registration(ModeController controller, MindMap map) {
            this.controller = (MindMapController) controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
            // fc, 12.8.2004: this is a bad hack, but when time lacks...
            hookInstance = new UnfoldAll();
            hookInstance.setController(controller);
            hookInstance.setMap(mMap);
        }
        
        public void register() {
            controller.registerMouseWheelEventHandler(this);
        }

        public void deRegister() {
            controller.deRegisterMouseWheelEventHandler(this);
        }

        public boolean handleMouseWheelEvent(MouseWheelEvent e) {
            if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
	            logger.info("handleMouseWheelEvent entered.");
                MindMapNode rootNode = mMap.getRootNode();
                if(e.getWheelRotation() > 0) {
                    hookInstance.unfoldOneStage(rootNode);
                } else {
                    // this is to avoid having selected nodes getting folded.
                    controller.select(controller.getView().getRoot());
                    hookInstance.foldOneStage(rootNode);
                }
                return true;
            }
            return false;
        }
    }
    
	/**
	 * 
	 */
	public UnfoldAll() {
		super();
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		boolean foldState = Tools.xmlToBoolean(getResourceString("foldingState"));
		String foldingType = getResourceString("foldingType");
		if(foldingType.equals("All")) {
			if(foldState) {
				foldAll(node);			
			} else {
				unfoldAll(node);
			} 
		} else {
			if(foldState) {
				foldOneStage(node);
			} else {
				unfoldOneStage(node);
			} 
		}
	}
		
	/**
     */
    protected void unfoldOneStage(MindMapNode node) {
        int minDepth = getMinDepth(node);
        if(minDepth < Integer.MAX_VALUE) 
        	minDepth++;
        unfoldStageN(node, minDepth);
    }

    /**
     */
    protected void foldOneStage(MindMapNode node) {
        foldStageN(node, getMaxDepth(node)-1);
    }

    /**
	 */
	protected void foldAll(MindMapNode node) {
		for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			foldAll((MindMapNode) i.next());
		}
		setFolded(node, true);
	}

	public void unfoldAll(MindMapNode node) {
		setFolded(node, false);
		for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			unfoldAll((MindMapNode) i.next());
		}
	}

	/**
	 * Unfolds every node that has only children which themselves have children.
	 * As this function is a bit difficult to describe and perhaps not so
	 * useful, it is currently not introduced into the menus.
	 * 
	 * @param node
	 *            node to start from.
	 */
	public void foldLastBranches(MindMapNode node) {
		boolean nodeHasChildWhichIsLeave = false;
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			if (child.getChildCount() == 0) {
				nodeHasChildWhichIsLeave = true;
			}
		}
		setFolded(node, nodeHasChildWhichIsLeave);
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			foldLastBranches((MindMapNode) i.next());
		}
	}
	
	protected void setFolded(MindMapNode node, boolean state) {
		if(node.hasChildren() && (node.isFolded()!=state)) {
		    getMindMapController().setFolded(node, state);
		}
	}

	public void unfoldStageN(MindMapNode node, int stage) {
		int k = depth(node);
		if(k < stage) {
			setFolded(node, false);
			for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
				unfoldStageN((MindMapNode) i.next(), stage);
			}			
		} else {
			foldAll(node);
		}
	}

	public void foldStageN(MindMapNode node, int stage) {
		int k = depth(node);
		if(k < stage) {
			setFolded(node, false);
			for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
				foldStageN((MindMapNode) i.next(), stage);
			}			
		} else {
			foldAll(node);
		}
	}

	public int getMinDepth(MindMapNode node) {
		if(node.isFolded())
			return depth(node);
		if(!node.hasChildren())
			return Integer.MAX_VALUE;
		int k = Integer.MAX_VALUE;
		for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			int l = getMinDepth((MindMapNode) i.next());
			if(l < k)
				k=l;
		}
		return k;
	}

	/**
	 */
	protected int getMaxDepth(MindMapNode node) {
		if(node.isFolded() || !node.hasChildren())
			return depth(node);
		int k = 0;
		for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			int l = getMaxDepth((MindMapNode) i.next());
			if(l > k)
				k=l;
		}
		return k;
	}



	protected int depth(MindMapNode node){
		if(node.isRoot())
			return 0;
		return depth((MindMapNode) node.getParent()) + 1;
	}

}
