/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.util.Iterator;

import freemind.extensions.NodeHookAdapter;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 */
public class UnfoldAll extends NodeHookAdapter {

	/**
	 * 
	 */
	public UnfoldAll() {
		super();
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if(node.hasChildren() || node.isFolded()) {
			getMap().setFolded(node,false);
			nodeChanged(node);
		}
		for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			invoke((MindMapNode) i.next());
		}
	}

}
