/*
 * Created on 10.03.2004
 *
 */
package accessories.plugins;

import java.text.MessageFormat;
import java.util.Iterator;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *  
 */
public class CreationModificationPlugin extends PermanentNodeHookAdapter {

	private static final String CREATED = "CREATED";

	private static final String MODIFIED = "MODIFIED";

	private long created;

	private long modified;

	private String tooltipFormat;

	/**
	 *  
	 */
	public CreationModificationPlugin() {
		super();
	}

	private void setStyle(MindMapNode node) {
		Object[] messageArguments = {
				node.getHistoryInformation().getCreatedAt(),
				node.getHistoryInformation().getLastModifiedAt() };
		if (tooltipFormat == null) {
			tooltipFormat = getResourceString("tooltip_format");
		}
		MessageFormat formatter = new MessageFormat(tooltipFormat);
		String message = formatter.format(messageArguments);
		setToolTip(node, getName(), message);
		logger.finest(this + "Tooltiop for " + node + " with parent "
				+ node.getParentNode() + " is " + message);
	}

//	/** Only for compability with old files ( <= 0.8.0 RC2) */
//	public void loadFrom(XMLElement child) {
//		super.loadFrom(child);
//		HashMap hash = loadNameValuePairs(child);
//		long created_ = toLong((String) hash.get(CREATED));
//		long modified_ = toLong((String) hash.get(MODIFIED));
//		getNode().getHistoryInformation().setCreatedAt(new Date(created_));
//		getNode().getHistoryInformation()
//				.setLastModifiedAt(new Date(modified_));
//	}

	/**
	 * @param createdString
	 * @return
	 */
	private static long toLong(String createdString) {
		try {
			return Long.valueOf(createdString).longValue();
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}

	public void shutdownMapHook() {
		removeToolTipRecursively(getNode());
		super.shutdownMapHook();
	}

	/**
	 *  
	 */
	private void removeToolTipRecursively(MindMapNode node) {
		setToolTip(node, getName(), null);
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			removeToolTipRecursively(child);
		}
	}

	private long getCreated() {
		return getNode().getHistoryInformation().getCreatedAt().getTime();
	}

	private long getModified() {
		return getNode().getHistoryInformation().getLastModifiedAt().getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setStyleRecursive(updatedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setStyle(getNode());
	}

	/*
	 * (non-Javadoc)
	 * 
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
		logger.finest("setStyle " + node);
		setStyle(node);
		// recurse:
		for (Iterator i = node.childrenFolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			setStyleRecursive(child);
		}
	}

}