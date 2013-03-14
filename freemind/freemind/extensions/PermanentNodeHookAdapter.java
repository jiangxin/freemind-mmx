/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PermanentNodeHookAdapter extends NodeHookAdapter implements
		PermanentNodeHook {

	// Logging:
	// private static java.util.logging.Logger logger;

	/**
	 */
	public PermanentNodeHookAdapter() {
		super();
		// if(logger == null)
		// logger =
		// ((ControllerAdapter)getController()).getFrame().getLogger(this.getClass().getName());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		logger.finest("onUpdateNodeHook");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#onUpdateChildrenHook()
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		logger.finest("onUpdateChildrenHook");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode newChildNode) {
		logger.finest("onAddChild");
	}

	public void onNewChild(MindMapNode newChildNode) {
		logger.finest("onNewChild");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onRemoveChild(freemind.modes.
	 * MindMapNode)
	 */
	public void onRemoveChild(MindMapNode oldChildNode) {
		logger.finest("onRemoveChild");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		String saveName = getName();
		// saveName=saveName.replace(File.separatorChar, '/');
		xml.setAttribute("name", saveName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#onSelectHook()
	 */
	public void onFocusNode(NodeView nodeView) {
		logger.finest("onSelectHook");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onDeselectHook()
	 */
	public void onLostFocusNode(NodeView nodeView) {
		logger.finest("onDeselectHook");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onAddChildren(freemind.modes.
	 * MindMapNode)
	 */
	public void onAddChildren(MindMapNode addedChild) {
		logger.finest("onAddChildren");
	}

	public static final String PARAMETERS = "Parameters";

	/**
	 */
	protected HashMap loadNameValuePairs(XMLElement xml) {
		HashMap result = new HashMap();
		if(xml.getChildren().isEmpty()) {
			return result;
		}
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

	public void onViewCreatedHook(NodeView nodeView) {
	}

	public void onViewRemovedHook(NodeView nodeView) {
	}

	/**
     */
	protected void setToolTip(String key, String value) {
		setToolTip(getNode(), key, value);
	}

	protected void setToolTip(MindMapNode node, String key, String value) {
		getController().setToolTip(node, key, value);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#executeTransaction(freemind.modes.mindmapmode.actions.xml.ActionPair)
	 */
	protected void executeTransaction(final ActionPair pair)
			throws InterruptedException, InvocationTargetException {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#registerFilter()
	 */
	public void registerFilter() {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#deregisterFilter()
	 */
	public void deregisterFilter() {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#filterAction(freemind.modes.mindmapmode.actions.xml.ActionPair)
	 */
	public ActionPair filterAction(ActionPair pPair) {
		return null;
	}

	public void processUnfinishedLinks() {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#saveHtml(java.io.Writer)
	 */
	public void saveHtml(Writer pFileout) throws IOException {
	}

	
}
