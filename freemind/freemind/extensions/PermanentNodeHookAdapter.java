/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.util.HashMap;
import java.util.Iterator;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

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

	/**
	 * @param child the child node the hook should be propagated to.
	 * @return returns the new hook or null if there is already such a hook.
	 */
	protected PermanentNodeHook propagate(MindMapNode child) {
		PermanentNodeHook hook = (PermanentNodeHook) getController().createNodeHook(getName(), child, getMap());
		// invocation:
		child.invokeHook(hook);
		return hook;
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		logger.finest("shutdownMapHook");
		setNode(null);
		setMap(null);
		super.shutdownMapHook();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onMouseOverHook()
	 */
	public void onMouseOverHook() {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		logger.finest("onUpdateNodeHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateChildrenHook()
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		logger.finest("onUpdateChildrenHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode newChildNode) {
		logger.finest("onAddChild");
	}

    public void onNewChild(MindMapNode newChildNode) {
		logger.finest("onNewChild");
    }
	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onRemoveChild(freemind.modes.MindMapNode)
	 */
	public void onRemoveChild(MindMapNode oldChildNode) {
		logger.finest("onRemoveChild");
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		String saveName = getName();
		//saveName=saveName.replace(File.separatorChar, '/');
		xml.setAttribute("name", saveName);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		logger.finest("onReceiveFocusHook");

	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onLooseFocusHook()
	 */
	public void onLooseFocusHook() {
		logger.finest("onLooseFocusHook");
	}

    /* (non-Javadoc)
     * @see freemind.extensions.PermanentNodeHook#onAddChildren(freemind.modes.MindMapNode)
     */
    public void onAddChildren(MindMapNode addedChild) {
        logger.finest("onAddChildren");
    }

	public static final String PARAMETERS = "Parameters";

	/**
	 * @param child
	 */
	protected HashMap loadNameValuePairs(XMLElement xml) {
		HashMap result = new HashMap();
		XMLElement child = (XMLElement) xml.getChildren().get(0);
		if (child != null && PARAMETERS.equals(child.getName())) {
			for (Iterator i = child.enumerateAttributeNames(); i.hasNext();) {
				String name = (String) i.next();
				result.put(name, child.getStringAttribute(name));
			}
		}
		return result;
	}

	/**
	 * @param nameValuePairs
	 * @param xml
	 */
	protected void saveNameValuePairs(HashMap nameValuePairs, XMLElement xml) {
		XMLElement child = new XMLElement();
		child.setName(PARAMETERS);
		for (Iterator i = nameValuePairs.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			Object value = nameValuePairs.get(key);
			child.setAttribute(key, value);
		}
		xml.addChild(child);
	
	}

    public void onRemoveChildren(MindMapNode oldChildNode, MindMapNode oldDad) {
		logger.finest("onRemoveChildren");
    }



}
