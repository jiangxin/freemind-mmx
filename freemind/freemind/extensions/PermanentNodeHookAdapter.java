/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.util.Iterator;
import java.util.List;

import freemind.main.XMLElement;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PermanentNodeHookAdapter
	extends NodeHookAdapter
	implements PermanentNodeHook {

		// Logging: 
		// private static java.util.logging.Logger logger;

	/**
	 * @param node
	 * @param map
	 * @param controller
	 */
	public PermanentNodeHookAdapter() {
		super();
//		if(logger == null)
//			logger = ((ControllerAdapter)getController()).getFrame().getLogger(this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode, java.util.List)
	 */
	public void invoke(MindMapNode focussed, List selecteds) {
		logger.info("invoke(selecteds) called.");
		HookFactory hookFactory = getController().getFrame().getHookFactory();
		for (Iterator it = selecteds.iterator();it.hasNext();) {
    	   PermanentNodeHook hook = (PermanentNodeHook) hookFactory.createNodeHook(getName());
    	   hook.setController(getController());
    	   hook.setMap(getMap());
		   MindMapNode selected = (MindMapNode)it.next();
		   selected.addHook(hook);
		   // call invoke.
		   selected.invokeHook(hook);
		}								
	}



	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		logger.info("shutdownMapHook");
		setNode(null);
		setMap(null);
		super.shutdownMapHook();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onMouseOverHook()
	 */
	public void onMouseOverHook() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		logger.info("onUpdateNodeHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateChildrenHook()
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		logger.info("onUpdateChildrenHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateAnyNodeHook()
	 */
	public void onUpdateAnyNodeHook(MindMapNode updatedNode) {
		// TODO Auto-generated method stub

	}
	
	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode newChildNode) {
		logger.info("onAddChild");
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		xml.setAttribute("name", getName());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
	}


}
