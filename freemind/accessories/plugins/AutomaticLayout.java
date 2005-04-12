/*
 * Created on 16.03.2004
 *
 */
package accessories.plugins;

import java.awt.Color;
import java.util.Iterator;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.Tools;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 */
public class AutomaticLayout extends PermanentNodeHookAdapter {

	private Color[] colors = new Color[]{ new Color(0x000000), new Color(0x0033FF), new Color(0x00b439), 
		new Color(0x990000), new Color(0x111111) };

	private String[] fontSize = new String[]{ "20", "18", "16", "14", "12"};


	/**
	 * 
	 */
	public AutomaticLayout() {
		super();
	}

	private void setStyle(MindMapNode node) {
	    logger.finest("updating node id="+node.getObjectId(getController())+" and text:"+node);
		int depth = depth(node);
		logger.finest("COLOR, depth="+(depth));
		int myIndex = colors.length - 1;
		if (depth < colors.length)
		    myIndex = depth;
		Color mycolor = colors[myIndex];
		if (!Tools.safeEquals(mycolor, node.getColor())) {
			getController().setNodeColor(node, mycolor);
//			nodeChanged(node);
		}
		String myFontSize = fontSize[myIndex];
		if (((node.getFontSize() != null) && (!node.getFontSize().equals(myFontSize)) ) 
		        || node.getFontSize() == null ) {
		    getController().setFontSize(node, myFontSize);
//		    nodeChanged(node);
		}
	}

	private int depth(MindMapNode node){
		if(node.isRoot())
			return 0;
		return depth((MindMapNode) node.getParent()) + 1;
	}



	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChildren(MindMapNode newChildNode) {
	    logger.finest("onAddChildren "+ newChildNode);
		super.onAddChild(newChildNode);
		setStyleRecursive(newChildNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setStyleRecursive(updatedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setStyle(getNode());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		setStyleRecursive(node);
	}

    /**
     * @param node
     */
    private void setStyleRecursive(MindMapNode node) {
	    logger.finest("setStyle "+ node);
        setStyle(node);
		// recurse:
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			invoke(child);
		}
    }

}
