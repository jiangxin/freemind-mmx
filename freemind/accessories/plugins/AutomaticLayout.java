/*
 * Created on 16.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.Color;
import java.awt.Dimension;

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


	/**
	 * 
	 */
	public AutomaticLayout() {
		super();
	}

	private void setColor(MindMapNode node) {
		int depth = depth(node);
		logger.info("COLOR, depth="+(depth));
		Color mycolor = colors[colors.length-1]; 
		if(depth < colors.length)
			mycolor = colors[depth];
		Color nodeColor = node.getColor();
		if(((nodeColor!=null) && (nodeColor.getRGB() != mycolor.getRGB()))|| nodeColor == null ) {
			node.setColor(mycolor);
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
	public void onAddChild(MindMapNode newChildNode) {
		super.onAddChild(newChildNode);
		setColor(newChildNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setColor(updatedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setColor(getNode());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		setColor(getNode());
	}

}
