/*
 * Created on 01.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NodeHookActionProxy extends NodeHookAdapter {

	private NodeHook subject;
	private AbstractAction action;

	protected class NodeHookAction extends AbstractAction {
		NodeHookAction(String text) {
			super(text);
		}

		public void actionPerformed(ActionEvent arg0) {
			NodeView view = getController().getFrame().getController().getView().getSelected();
			setNode(view.getModel());
			System.out.println("actionPerformed for "+getNode());
			invoke();
		}

	}

	/**
	 * @param node
	 * @param map
	 * @param controller
	 */
	public NodeHookActionProxy(
		MindMapNode node,
		MindMap map,
		ModeController controller) {
		super(node, map, controller);
	}

	NodeHook createHook() {
		if(subject == null){
			subject =
				getController().getFrame().getHookFactory().createNodeHook(
					getProperties().getProperty("propFile"),
					getNode(),getMap(), getController());
		}
		return subject;
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#nodeMenuHook(javax.swing.JMenu)
	 */
	public void nodeMenuHook(JMenu nodeMenu) {
		if(action == null)
			this.action = new NodeHookAction(getProperties().getProperty("script"));
		nodeMenu.add(action);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke()
	 */
	public void invoke() {
		createHook().invoke();
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
