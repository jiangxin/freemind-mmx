/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.util.Iterator;

import freemind.extensions.NodeHookAdapter;
import freemind.main.Tools;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 */
public class UnfoldAll extends NodeHookAdapter  {

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
		getController().getView().selectAsTheOnlyOneSelected(node.getViewer());
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
			if(node.getViewer() != null) {
				getMap().setFolded(node, state);
			} else {
				node.setFolded(state);
			}
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
