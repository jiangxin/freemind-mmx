/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.text.MessageFormat;
import java.util.Date;

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
		setToolTip(formatter.format(messageArguments));

		nodeChanged(getNode());
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
	 */
	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		XMLElement paramChild = (XMLElement) child.getChildren().get(0);
		if(paramChild != null) {
			Object obj = paramChild.getAttribute("created", new Long(created));
			String str = (String) obj;
			created = Long.valueOf(str).longValue();
			modified = Long.valueOf((String) paramChild.getAttribute("modified", new Long(modified))).longValue();
		}
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode child) {
		super.onAddChild(child);
		PermanentNodeHook hook = (PermanentNodeHook) getController().createNodeHook(getName(), child, getMap());
		child.invokeHook(hook);
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		XMLElement child = new XMLElement();
		child.setName("Parameters");
		child.setAttribute("created", new Long(created));
		child.setAttribute("modified", new Long(modified));
		xml.addChild(child);
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

}
