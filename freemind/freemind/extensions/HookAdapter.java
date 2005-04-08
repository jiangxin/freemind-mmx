/*
 * Created on 29.02.2004
 *
 */
package freemind.extensions;

import java.net.URL;
import java.util.Properties;

import freemind.modes.ControllerAdapter;
import freemind.modes.MindMap;
import freemind.modes.ModeController;

/** Implments MindMapHook as an Adapter class.
 *  Implementation is straight forward.
 * 
 * @author foltin
 *
 */
public class HookAdapter implements MindMapHook {

	private String name;
	private Properties properties;
	private ModeController controller;

	// Logging: 
	protected java.util.logging.Logger logger;
	/**
	 * Stores the plugin base class as declared by the plugin_registration/isBaseClass
	 * attribute.
	 */
	private Object baseClass;

	/**
	 */
	public HookAdapter() {
		baseClass=null;
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
		String result = properties.getProperty(property);
		if(result == null) {
			result = getController().getText(property);
		}
		if(result == null) {
			logger.warning("The following property was not found:"+property);
		}
		return result;
	}
	
	public URL getResource(String resourceName) {
	    return this.getClass().getClassLoader().getResource(resourceName);
	}
	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#getPluginBaseClass()
	 */
	public Object getPluginBaseClass() {
		return baseClass;
	}
	/* (non-Javadoc)
	 * @see freemind.extensions.MindMapHook#setPluginBaseClass(java.lang.Object)
	 */
	public void setPluginBaseClass(Object baseClass) {
		this.baseClass = baseClass;
	}

}
