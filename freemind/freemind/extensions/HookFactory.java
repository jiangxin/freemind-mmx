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
/*$Id: HookFactory.java,v 1.1.2.8 2004-07-01 20:13:39 christianfoltin Exp $*/
package freemind.extensions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import freemind.common.JaxbTools;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginAction;
import freemind.controller.actions.generated.instance.PluginMenu;
import freemind.controller.actions.generated.instance.PluginMode;
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
		public Vector menuPositions;
		HookDescriptor(String fileName, String script, Properties props, String menuPositionString) {
			this.fileName = fileName;
			this.script = script;
			this.properties = props;
			menuPositions = new Vector();
			StringTokenizer to = new StringTokenizer(menuPositionString, ",");
			while(to.hasMoreTokens()) {
				String token = to.nextToken();
				menuPositions.add(token.trim());
			}
		}
		public String toString() {
			return "[HookDescriptor fileName="
				+ fileName
				+ ", script="
				+ script
				+ ", props="
				+ properties
				+ ", menu positions="
				+ menuPositions
				+ "]";
		}
	}

	private static final String pluginPrefix = "accessories.plugins.";
	private FreeMindMain frame;
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

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public Vector getPossibleNodeHooks(Class mode) {
		return searchFor(NodeHook.class, mode);
	}

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public Vector getPossibleModeControllerHooks(Class mode) {
		return searchFor(ModeControllerHook.class, mode);
	}

	/**
	 * @param class1
	 * @param mode
	 * @return a string vector with representatives for plugins.
	 */
	private Vector searchFor(Class baseClass, Class mode) {
		actualizePlugins();
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
					String menuPos = def.getProperty("menus");
					if(menuPos == null) {
						menuPos = "";
					}
					getXmlDescription(def);
					pluginInfo.put(
						propFile,
						new HookDescriptor(file, script, def, menuPos));
				}
			}
		}
	}

	/**
     * @param def
     */
    private void getXmlDescription(Properties def) {
		try {
			ObjectFactory factory = JaxbTools.getInstance().getObjectFactory();
            Plugin plugin = factory.createPlugin();
            PluginAction action = factory.createPluginAction();
			action.setBase(def.getProperty("base"));
			action.setClassName("accessories.plugins." + 
				def.getProperty("script").replaceAll("\\.class$", "")); 
			action.setDocumentation(def.getProperty("documentation"));
			action.setIconPath(def.getProperty("icon"));
			action.setName(def.getProperty("name"));
			action.setKeyStroke(def.getProperty("keystroke"));

			PluginMode mode = factory.createPluginMode();
			mode.setClassName("freemind.modes.mindmapmode");
			action.getPluginModeOrPluginMenu().add(mode);

			StringTokenizer to = new StringTokenizer(def.getProperty("menus"), ",");
			while(to.hasMoreTokens()) {
				String token = to.nextToken();
				PluginMenu menu = factory.createPluginMenu();
				menu.setLocation(token.trim());
				action.getPluginModeOrPluginMenu().add(menu);
			}
            plugin.getPluginAction().add(action);
			//marshal to StringBuffer:
//			StringWriter writer = new StringWriter();
			FileWriter fwriter = new FileWriter(def.getProperty("script").replaceAll("\\.class$", "")+".xml");
			Marshaller m = JaxbTools.getInstance().createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			m.marshal(plugin, fwriter);
//			String result = writer.toString();
//			logger.info(result) ;           
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public ModeControllerHook createModeControllerHook(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
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

	public NodeHook createNodeHook(
		String hookName) {
        logger.finest("CreateNodeHook: " + hookName);
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(hookName==null || descriptor==null)
			throw new IllegalArgumentException("Unknown hook name "+hookName);
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

    /**
     * @return
     */
    public List getHookMenuPositions(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		return descriptor.menuPositions;
//    	Vector ret = new Vector();
//    	ret.add("menu_bar/file/export/"+descriptor.script);
//        return ret;
    }

}
