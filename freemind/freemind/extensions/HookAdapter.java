/*
 * Created on 29.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.util.Properties;

import freemind.modes.MindMap;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HookAdapter implements MindMapHook {

	private Properties properties;
	private ModeController controller;
	/**
	 * @param map
	 * @param controller
	 */
	public HookAdapter(ModeController controller) {
		this.controller = controller;
	}
	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#getName()
	 */
	public String getName() {
		return this.getClass().getName();
	}
	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#startupMapHook(java.lang.String)
	 */
	public void startupMapHook() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		controller = null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#getPersistentState()
	 */
	public String getPersistentState() {
		return null;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#setPersistentState(java.lang.String)
	 */
	public void setPersistentState(String persistentState) {
	}

	/**
	 * @return
	 */
	protected ModeController getController() {
		return controller;
	}

	/**
	 * @return
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}


}
