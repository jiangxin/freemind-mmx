/*
 * Created on 29.02.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.util.Properties;

import freemind.modes.ControllerAdapter;
import freemind.modes.MindMap;
import freemind.modes.ModeController;

/**
 * @author foltin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HookAdapter implements MindMapHook {

	private String name;
	private Properties properties;
	private ModeController controller;

	// Logging: 
	protected java.util.logging.Logger logger;

	/**
	 * @param map
	 * @param controller
	 */
	public HookAdapter() {
	}
	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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


	/**
	 * @param controller
	 */
	public void setController(ModeController controller) {
		this.controller = controller;
		if(logger == null)
			logger = ((ControllerAdapter)getController()).getFrame().getLogger(this.getClass().getName());
	}
	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#getResourceString(java.lang.String)
	 */
	public String getResourceString(String property) {
		return properties.getProperty(property);
	}

}
