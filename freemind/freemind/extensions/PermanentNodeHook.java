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

	/** Is called if the addedChildNode is inserted as a direct child of the node,
	 * this hook is attached to.
	 * The cases in which this method is called contain new nodes, paste, move, etc.
	 * 
	 * Ah, don't call propagate in this method, as paste introduces nodes with the 
	 * hook and you'll have them twice, ...
	 * @see onNewChild
	 * @param addedChildNode
	 */
	void onAddChild(MindMapNode addedChildNode);

	/** Is only called, if a new nodes is inserted as a child.
	 * Remark: In this case onAddChild is called too and moreover *before* this method.
	 * @see onAddChild.
	 * @param newChildNode
	 */
	void onNewChild(MindMapNode newChildNode);
	
    /** This method is called, if a child is added to me or to any of my children.
     *  (See onUpdateChildrenHook)
     * @param addedChild
     */
    void onAddChildren(MindMapNode addedChild);

	void onRemoveChild(MindMapNode oldChildNode);
    /** This method is called, if a child is removed to me or to any of my children.
     *  (See onUpdateChildrenHook)
     * @param oldChildNode
     * @param oldDad TODO
     */
	void onRemoveChildren(MindMapNode oldChildNode, MindMapNode oldDad);
	
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
