/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;

import freemind.extensions.HookFactory;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreationModificationPlugin extends PermanentNodeHookAdapter {

	private static final String CREATED = "CREATED";
    private static final String MODIFIED = "MODIFIED";
    private long created;
	private long modified;

	/**
	 * 
	 */
	public CreationModificationPlugin() {
		super();
		created = System.currentTimeMillis();
		modified = System.currentTimeMillis();
	}

	private void setStyle() {
		Object[] messageArguments = {
			(new Date(created)),
			(new Date(modified))
		};
		MessageFormat formatter = new MessageFormat(getResourceString("tooltip_format"));
		String message = formatter.format(messageArguments);
		setToolTip(getName(),message);
		logger.finest(this+"Tooltiop for "+getNode()+" with parent " + getNode().getParentNode()+" is "+message);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		XMLElement paramChild = (XMLElement) child.getChildren().get(0);

	    HashMap hash = loadNameValuePairs(child);
        created = toLong((String) hash.get(CREATED));
	    modified = toLong((String) hash.get(MODIFIED));
	}

	/**
     * @param createdString
     * @return
     */
    private long toLong(String createdString) {
        try {
            return Long.valueOf(createdString).longValue();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    /* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onNewChild(MindMapNode child) {
		super.onNewChild(child);
		propagate(child);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		HashMap hash = new HashMap();
		hash.put(CREATED, toString(created));
		hash.put(MODIFIED, toString(modified));
        saveNameValuePairs(hash, xml);
//		XMLElement child = new XMLElement();
//		child.setName("Parameters");
//		child.setAttribute("CREATED", new Long(created));
//		child.setAttribute("MODIFIED", new Long(modified));
//		xml.addChild(child);
	}

	/**
     * @param modified2
     * @return
     */
    private String toString(long longValue) {
        return new Long(longValue).toString();
    }

    /* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		setStyle();
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		modified = System.currentTimeMillis();
		setStyle();
	}
	public void shutdownMapHook() {
		setToolTip(getName(), null);
		nodeChanged(getNode());
		super.shutdownMapHook();
	}

}
