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
/*$Id: HookFactory.java,v 1.1.2.13 2004-08-08 13:03:48 christianfoltin Exp $*/
package freemind.extensions;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.xml.bind.Unmarshaller;

import freemind.common.JaxbTools;
import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginActionType;
import freemind.controller.actions.generated.instance.PluginMenuType;
import freemind.controller.actions.generated.instance.PluginModeType;
import freemind.controller.actions.generated.instance.PluginPropertyType;
import freemind.main.FreeMindMain;
import freemind.modes.MindMapNode;

/**
 * @author christianfoltin
 *
 * @file HookFactory.java 
 * @package freemind.modes
 * */
public class HookFactory {
	private static final String pluginPrefix = "accessories.plugins.";
	private FreeMindMain frame;
	// Logging: 
	private java.util.logging.Logger logger;

	private HashMap pluginInfo;
	private Vector allPlugins;

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
		for (Iterator i = allPlugins.iterator(); i.hasNext();) {
			String label = (String) i.next();
			HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(label);
			//Properties prop = descriptor.properties;
			try {
				logger.finest("Loading: "+label);
				if (baseClass.isAssignableFrom(Class.forName(descriptor
						.getBaseClass()))) {
					// the plugin inherits from the baseClass, we carry on to
					// look for the mode
					for (Iterator j = descriptor.getModes().iterator(); j.hasNext();) {
						String pmode = (String) j.next();
						if (pmode.equals(mode.getPackage().getName())) {
								// add the class:
								returnValue.add(label);
						}
						
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
		allPlugins = new Vector();
		for (Iterator i = ImportWizard.CLASS_LIST.iterator(); i.hasNext();) {
			String xmlPluginFile = (String) i.next();
			if (xmlPluginFile.startsWith(pluginPrefix)) {
				// make file name:
				xmlPluginFile =
					xmlPluginFile.replace('.', File.separatorChar)
						+ ImportWizard.lookFor;
				// this is one of our plugins:
				URL pluginURL = ClassLoader.getSystemResource(xmlPluginFile);
				// unmarshal xml:
				Plugin plugin = null;
				try {
					InputStream in = pluginURL.openStream();
					Unmarshaller unmarshaller = JaxbTools.getInstance()
							.createUnmarshaller();
					logger.finest("Reading: "+xmlPluginFile);
					unmarshaller.setValidating(true);
					plugin = (Plugin) unmarshaller.unmarshal(in);
//					for (Iterator j = plugin.getPluginAction().iterator(); j.hasNext();) {
//						PluginActionType action = (PluginActionType) j.next();
//						String label = action.getLabel();
//						label = label.replaceFirst(".class$", "").replaceFirst(".properties$","").replace('.', '/')+".properties";
//						action.setLabel(label);
//					}					
//					Marshaller marshaller = JaxbTools.getInstance().createMarshaller();
//					FileWriter filew = new FileWriter(new File(xmlPluginFile));
//					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
//					marshaller.marshal(plugin, filew);
				} catch (Exception e) {
					// error case
					logger.severe(e.getLocalizedMessage());
					e.printStackTrace();
					continue;
				}
				// plugin is loaded.
				for (Iterator j = plugin.getPluginAction().iterator(); j.hasNext();) {
					PluginActionType action = (PluginActionType) j.next();
					pluginInfo.put(action.getLabel(), new HookDescriptor(action));
					allPlugins.add(action.getLabel());
				}
			}
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
			Class hookClass = Class.forName(descriptor.getClassName());
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
				"Error occurred loading hook: " + descriptor.getClassName());
			return null;
		}
	}

	/** 
	 * Do not call this method directly. Call ModeController.createNodeHook instead.
	 * */
	public NodeHook createNodeHook(
		String hookName) {
        logger.finest("CreateNodeHook: " + hookName);
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(hookName==null || descriptor==null)
			throw new IllegalArgumentException("Unknown hook name "+hookName);
		return (NodeHook) createJavaHook(hookName, descriptor);
	}

	/**
	 * @param node
	 * @param hookName
	 * @return null if not present, the hook otherwise.
	 */
	public PermanentNodeHook getHookInNode(MindMapNode node, String hookName) {
		// search for already instanciated hooks of this type:
		for (Iterator i = node.getActivatedHooks().iterator(); i.hasNext();) {
			PermanentNodeHook otherHook = (PermanentNodeHook) i.next();
			if(otherHook.getName().equals(hookName)) {
				// there is already one instance. 
				return otherHook;
			}
		}
		return null;
	}
	
	private void decorateHook(
		String hookName,
		HookDescriptor descriptor,
		MindMapHook hook) {
		hook.setProperties(descriptor.getProperties());
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
		return descriptor.getProperties().getProperty(prop);	
	}

	/**
	 * @param action
	 */
	public void decorateAction(String hookName, AbstractAction action) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		if(descriptor == null){
			throw new IllegalArgumentException("The hook "+hookName + " is not defined.");
		}
		String name = descriptor.getName();
		if(name != null){
			action.putValue(AbstractAction.NAME, name);		
		} else {
			action.putValue(AbstractAction.NAME, descriptor.getClassName());		
		}
		String docu = descriptor.getDocumentation();
		if(docu != null)
			action.putValue(AbstractAction.SHORT_DESCRIPTION, docu);
		String icon = descriptor.getIconPath();
		if(icon != null) {
			ImageIcon imageIcon = new ImageIcon(frame.getResource(icon)); 
			action.putValue(AbstractAction.SMALL_ICON, imageIcon);
		}
		String key = descriptor.getKeyStroke();
		if(key != null)
			action.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key));

	}

    /**
     * @return returns a list of menu position strings for the StructuredMenuHolder.
     */
    public List getHookMenuPositions(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		return descriptor.menuPositions;
    }

    /** This method takes into account, that there are on off hooks that need 
     *  a special treatment for their menues like adjusting the checkbox.
     * @param hookAction
     * @return returns a new JMenuItem for the given hookAction.
     */
    public JMenuItem getHookMenuItem(String hookName, AbstractAction hookAction) {
    		return new JMenuItem(hookAction);
    }
    
	/**
	 * @param permHook
	 * @return
	 */
	public HookInstanciationMethod getInstanciationMethod(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		return descriptor.getInstanciationMethod();
	}

    /**
     * @param desc
     * @return
     */
    public boolean isAutomaticallyInvoked(String hookName) {
		HookDescriptor descriptor = (HookDescriptor) pluginInfo.get(hookName);
		return descriptor.isAutomaticallyInvoked();
    }

}
