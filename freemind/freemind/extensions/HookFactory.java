/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: HookFactory.java,v 1.1.2.5 2004-04-08 18:54:55 christianfoltin Exp $*/
package freemind.extensions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import freemind.main.FreeMindMain;

/**
 * @author christianfoltin
 *
 * @file HookFactory.java 
 * @package freemind.modes
 * */
public class HookFactory {
	private class HookDescriptor {
		private Properties properties;
		private String script;
		public String fileName;
		HookDescriptor(String fileName, String script, Properties props) {
			this.fileName = fileName;
			this.script = script;
			this.properties = props;
		}
		public String toString() {
			return "[HookDescriptor fileName="
				+ fileName
				+ ", script="
				+ script
				+ ", props="
				+ properties
				+ "]";
		}
	}

	private static final String pluginPrefix = "accessories.plugins.";
	private FreeMindMain frame;
	private HashMap pluginCategories;
	// Logging: 
	private java.util.logging.Logger logger;

	private HashMap pluginInfo;

	/**
	 * 
	 */
	public HookFactory(FreeMindMain frame) {
		this.frame = frame;
		logger = frame.getLogger(this.getClass().getName());
	}

	public Vector getPossibleNodeHooks(Class mode) {
		actualizePlugins();
		return searchFor(NodeHook.class, mode);
		//return new String[] {"BlueNodeHook"};
	}

	public Vector getPossibleModeControllerHooks(Class mode) {
		actualizePlugins();
		return searchFor(ModeControllerHook.class, mode);
		//return new String[] {"BlueNodeHook"};
	}

	/**
	 * @param class1
	 * @param mode
	 * @return
	 */
	private Vector searchFor(Class baseClass, Class mode) {
		Vector returnValue = new Vector();
		for (Iterator i = pluginInfo.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			HookDescriptor descriptor = (HookDescriptor) entry.getValue();
			String file = (String) entry.getKey();
			Properties prop = descriptor.properties;
			try {
				if (baseClass
					.isAssignableFrom(
						Class.forName(prop.getProperty("base")))) {
					// the plugin inherits from the baseClass, we carry on to look for the mode
					if (prop
						.getProperty("modes")
						.equals(mode.getPackage().getName())) {
						// add the class:
						returnValue.add(file);
					}
				}
			} catch (ClassNotFoundException e) {
				logger.severe("Class not found.");
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	/**
	 * 
	 */
	private void actualizePlugins() {
		ImportWizard.CLASS_LIST.clear();
		ImportWizard.buildClassList();
		pluginInfo = new HashMap();
		for (Iterator i = ImportWizard.CLASS_LIST.iterator(); i.hasNext();) {
			String propFile = (String) i.next();
			if (propFile.startsWith(pluginPrefix)) {
				// make file name:
				propFile =
					propFile.replace('.', File.separatorChar)
						+ ImportWizard.lookFor;
				// this is one of our plugins:
				URL pluginURL = ClassLoader.getSystemResource(propFile);

				//load properties
				Properties def = new Properties();
				try {
					InputStream in = pluginURL.openStream();
					def.load(in);
					in.close();
				} catch (IOException e) {
					// not correct?
					logger.severe(e.getMessage());
					e.printStackTrace();
					def.clear();
				}
				if (!def.isEmpty()) {
					// defs are valid:
					String file =
						propFile.substring(
							0,
							propFile.lastIndexOf(File.separator) + 1);
					String script = def.getProperty("script");
					file += script;
					// set values of propFile and file into the properties:
					def.setProperty("file", file);
					//URGENT: Rename propFile.
					def.setProperty("propFile", propFile);
					pluginInfo.put(
						propFile,
						new HookDescriptor(file, script, def));
				}
			}
		}
	}

	public ModeControllerHook createModeControllerHook(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if (descriptor.script.endsWith("py")) {
			return (ModeControllerHook) createJythonHook(hookName, descriptor);
		}
		return (ModeControllerHook) createJavaHook(hookName, descriptor);
	}

	private MindMapHook createJavaHook(
		String hookName,
		HookDescriptor descriptor) {
		try {
			logger.finest("Excecuting java class: " + descriptor.fileName);
			String className =
				descriptor.fileName.replace(File.separatorChar, '.');
			className = className.substring(0, className.length() - 6
			/*length of .class */
			);
			logger.finest("Excecuting java class: " + className);
			Class hookClass = Class.forName(className);
			Constructor hookConstructor =
				hookClass.getConstructor(new Class[] {
			});
			MindMapHook hook =
				(MindMapHook) hookConstructor.newInstance(new Object[] {
			});
			decorateHook(hookName, descriptor, hook);
			return hook;
		} catch (Exception e) {
			logger.severe(
				"Error occurred loading hook: " + descriptor.fileName);
			return null;
		}
	}

	private org.python.util.PythonInterpreter interp;

	private MindMapHook createJythonHook(
		String hookName,
		HookDescriptor descriptor) {
		try {
			// Lazy initialization:
			if(interp==null) {
				org.python.core.PySystemState.initialize();
				this.interp = new org.python.util.PythonInterpreter();
			}
			logger.finest("Excecuting jython script: " + descriptor.fileName);
			interp.execfile(
				ClassLoader.getSystemResourceAsStream(descriptor.fileName));
			MindMapHook hook =
				(MindMapHook) interp.get("instance", MindMapHook.class);
			decorateHook(hookName, descriptor, hook);
			return hook;
		} catch (Exception e) {
			logger.severe(
				"Error occurred loading hook: " + descriptor.fileName);
			return null;
		}
	}

	public NodeHook createNodeHook(
		String hookName) {
        logger.finest("CreateNodeHook: " + hookName);
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(hookName==null || descriptor==null)
			throw new IllegalArgumentException("Unknown hook name "+hookName);
		// proxy support
		if(descriptor.properties.getProperty("proxy")!=null) {
			logger.info("Creating proxy for "+hookName);
			NodeHook proxy = new NodeHookActionProxy();
			descriptor.properties.remove("proxy");
			decorateHook(hookName, descriptor, proxy);
			return proxy;
		}
		if (descriptor.script.endsWith("py")) {
			return (NodeHook) createJythonHook(hookName, descriptor);
		}
		return (NodeHook) createJavaHook(hookName, descriptor);
	}

	private void decorateHook(
		String hookName,
		HookDescriptor descriptor,
		MindMapHook hook) {
		hook.setProperties(descriptor.properties);
		hook.setName(hookName);
	}

	/**
	 * @return
	 */
	protected FreeMindMain getFrame() {
		return frame;
	}

	public String getProperty(String hookName, String prop){
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(descriptor == null){
			throw new IllegalArgumentException("The hook "+hookName + " is not defined.");
		}
		return descriptor.properties.getProperty(prop);	
	}

	/**
	 * @param action
	 */
	public void decorateAction(String hookName, AbstractAction action) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(descriptor == null){
			throw new IllegalArgumentException("The hook "+hookName + " is not defined.");
		}
		String name = descriptor.properties.getProperty("name");
		if(name != null){
			action.putValue(AbstractAction.NAME, name);		
		} else {
			action.putValue(AbstractAction.NAME, descriptor.script);		
		}
		String docu = descriptor.properties.getProperty("documentation");
		if(docu != null)
			action.putValue(AbstractAction.SHORT_DESCRIPTION, docu);
		String icon = descriptor.properties.getProperty("icon");
		if(icon != null) {
			ImageIcon imageIcon = new ImageIcon(frame.getResource(icon)); 
			action.putValue(AbstractAction.SMALL_ICON, imageIcon);
		}
		String key = descriptor.properties.getProperty("keystroke");
		if(key != null)
			action.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key));

	}

}
