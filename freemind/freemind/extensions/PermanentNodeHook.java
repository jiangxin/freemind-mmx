/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface PermanentNodeHook extends NodeHook {
	
	void onReceiveFocusHook();
	void onMouseOverHook();
	/** 
	 * If the node I belong to is changed, I get this notification.
	 * */
	void onUpdateNodeHook();

	void onAddChild(MindMapNode newChildNode);

	void onRemoveChild(MindMapNode oldChildNode);
	
	/**
	 * If any of my children is updated, I get this notification.
	 */
	void onUpdateChildrenHook(MindMapNode updatedNode);

	/**
	 * @param hookElement
	 */
	void save(XMLElement hookElement);
	/**
	 * @param child
	 */
	void loadFrom(XMLElement child);
	/**
	 * 
	 */
	void onLooseFocusHook();
	

}
