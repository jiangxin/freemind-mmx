/*
 * Created on 16.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AutomaticLayout extends PermanentNodeHookAdapter {

	private Color[] colors = new Color[]{ new Color(0x000000), new Color(0x0033FF), new Color(0x00b439), 
		new Color(0x990000), new Color(0x111111) };

	private String[] fontSize = new String[]{ "36", "24", "18", "14", "12"};


	/**
	 * 
	 */
	public AutomaticLayout() {
		super();
	}

	private void setStyle(MindMapNode node) {
		int depth = depth(node);
		logger.finest("COLOR, depth="+(depth));
		int myIndex = colors.length - 1;
		if (depth < colors.length)
		    myIndex = depth;
		Color mycolor = colors[myIndex];
		Color nodeColor = node.getColor();
		if (((nodeColor != null) && (nodeColor.getRGB() != mycolor.getRGB()))
				|| nodeColor == null) {
			node.setColor(mycolor);
			nodeChanged(node);
		}
		String myFontSize = fontSize[myIndex];
		if (((node.getFontSize() != null) && (!node.getFontSize().equals(myFontSize)) ) 
		        || node.getFontSize() == null ) {
		    node.setFontSize(Integer.valueOf(myFontSize).intValue());
		    nodeChanged(node);
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
		for (Iterator i = node.childrenFolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			invoke(child);
		}
    }

}
