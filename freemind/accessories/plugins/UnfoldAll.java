/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;

import freemind.extensions.HookRegistration;
import freemind.extensions.NodeHookAdapter;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.MouseWheelEventHandler;

/**
 * @author foltin
 *
 */
public class UnfoldAll extends NodeHookAdapter  {

    public static class Registration implements HookRegistration, MouseWheelEventHandler {

        private final ModeController controller;
        private final MindMap mMap;
        private final java.util.logging.Logger logger;
        private UnfoldAll hookInstance;

        public Registration(ModeController controller, MindMap map) {
            this.controller = controller;
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
                MindMapNode rootNode = (MindMapNode) mMap.getRoot();
                if(e.getWheelRotation() > 0) {
                    hookInstance.unfoldOneStage(rootNode);
                } else {
                    // this is to avoid having selected nodes getting folded.
                    controller.select(rootNode);
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
     * @param node
     */
    protected void unfoldOneStage(MindMapNode node) {
        int minDepth = getMinDepth(node);
        if(minDepth < Integer.MAX_VALUE) 
        	minDepth++;
        unfoldStageN(node, minDepth);
    }

    /**
     * @param node
     */
    protected void foldOneStage(MindMapNode node) {
        foldStageN(node, getMaxDepth(node)-1);
    }

    /**
	 * @param node
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

	protected void setFolded(MindMapNode node, boolean state) {
		if(node.hasChildren() && (node.isFolded()!=state)) {
		    getController().setFolded(node, state);
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
	 * @param node
	 * @return
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
