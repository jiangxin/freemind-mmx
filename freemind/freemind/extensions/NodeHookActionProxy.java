/*
 * Created on 01.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import javax.swing.AbstractAction;

import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NodeHookActionProxy extends PermanentNodeHookAdapter {

	private PermanentNodeHook subject;
	private AbstractAction action;

	/**
	 * @param node
	 * @param map
	 * @param controller
	 */
	public NodeHookActionProxy() {
		super();
	}

	PermanentNodeHook createHook() {
		if(subject == null){
			subject = (PermanentNodeHook)
				getController().getFrame().getHookFactory().createNodeHook(
					getProperties().getProperty("propFile"));
			subject.setController(getController());
			subject.setMap(getMap());
		}
		return subject;
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke()
	 */
	public void invoke(MindMapNode node) {
		createHook().invoke(node);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode newChildNode) {
		createHook().onAddChild(newChildNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onMouseOverHook()
	 */
	public void onMouseOverHook() {
		createHook().onMouseOverHook();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		createHook().onReceiveFocusHook();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onUpdateAnyNodeHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateAnyNodeHook(MindMapNode updatedNode) {
		createHook().onUpdateAnyNodeHook(updatedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		createHook().onUpdateChildrenHook(updatedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		createHook().onUpdateNodeHook();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		createHook().shutdownMapHook();
		subject=null;
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#getName()
	 */
	public String getName() {
		return createHook().getName();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		createHook().startupMapHook();
	}

}
