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
 * Created on 29.02.2004
 *
 */
package freemind.extensions;

import java.net.URL;
import java.util.Properties;

import freemind.modes.ModeController;

/**
 * Implments MindMapHook as an Adapter class. Implementation is straight
 * forward.
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
	 * Stores the plugin base class as declared by the
	 * plugin_registration/isBaseClass attribute.
	 */
	private PluginBaseClassSearcher baseClass;

	/**
	 */
	public HookAdapter() {
		if (logger == null)
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		baseClass = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#getName()
	 */
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#startupMapHook(java.lang.String)
	 */
	public void startupMapHook() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.NodeHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		controller = null;
	}

	/**
	 */
	protected ModeController getController() {
		return controller;
	}

	/**
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 */
	public void setController(ModeController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#getResourceString(java.lang.String)
	 */
	public String getResourceString(String property) {
		String result = properties.getProperty(property);
		if (result == null) {
			result = getController().getText(property);
		}
		if (result == null) {
			logger.warning("The following property was not found:" + property);
		}
		return result;
	}

	public URL getResource(String resourceName) {
		return this.getClass().getClassLoader().getResource(resourceName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#getPluginBaseClass()
	 */
	public Object getPluginBaseClass() {
		return baseClass.getPluginBaseObject();
	}

	public void setPluginBaseClass(PluginBaseClassSearcher baseClass) {
		this.baseClass = baseClass;
	}

	/**
	 * After tree node change, the focus must be obtained as it is invalid.
	 */
	protected void obtainFocusForSelected() {
		// Focus fix
		getController().getController().obtainFocusForSelected();
	}


}
