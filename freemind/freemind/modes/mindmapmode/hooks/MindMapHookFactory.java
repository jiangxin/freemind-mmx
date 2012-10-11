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

package freemind.modes.mindmapmode.hooks;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.jibx.runtime.IUnmarshallingContext;

import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginAction;
import freemind.controller.actions.generated.instance.PluginClasspath;
import freemind.controller.actions.generated.instance.PluginMode;
import freemind.controller.actions.generated.instance.PluginRegistration;
import freemind.extensions.HookDescriptorPluginAction;
import freemind.extensions.HookDescriptorRegistration;
import freemind.extensions.HookFactoryAdapter;
import freemind.extensions.HookInstanciationMethod;
import freemind.extensions.ImportWizard;
import freemind.extensions.MindMapHook;
import freemind.extensions.MindMapHook.PluginBaseClassSearcher;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.main.FreeMindMain;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author christianfoltin
 *
 * @file HookFactory.java
 * @package freemind.modes
 */
/**
 * @author foltin
 * 
 */
public class MindMapHookFactory extends HookFactoryAdapter {
	/**
	 * Match xml files in the accessories/plugin directory and not in its
	 * subdirectories.
	 */
	private final static String pluginPrefixRegEx = ".*(accessories(/|\\\\)|)plugins(/|\\\\)[^/\\\\]*";

	private FreeMindMain frame;

	// Logging:
	private java.util.logging.Logger logger;

	private static HashMap pluginInfo = null;

	private static Vector allPlugins = null;

	private static ImportWizard importWizard = null;

	/** Contains PluginRegistrationType -> PluginType relations. */
	protected static HashSet allRegistrations;

	/**
	 *
	 */
	public MindMapHookFactory(FreeMindMain frame) {
		this.frame = frame;
		logger = frame.getLogger(this.getClass().getName());
		allRegistrationInstances = new HashMap();
	}

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public Vector getPossibleNodeHooks() {
		return searchFor(NodeHook.class, MindMapController.class);
	}

	/**
	 * @return a string vector with representatives for plugins.
	 */
	public Vector getPossibleModeControllerHooks() {
		return searchFor(ModeControllerHook.class, MindMapController.class);
	}

	/**
	 * @return a string vector with representatives for plugins.
	 */
	private Vector searchFor(Class baseClass, Class mode) {
		actualizePlugins();
		Vector returnValue = new Vector();
		String modeName = mode.getPackage().getName();
		for (Iterator i = allPlugins.iterator(); i.hasNext();) {
			String label = (String) i.next();
			HookDescriptorPluginAction descriptor = getHookDescriptor(label);
			// Properties prop = descriptor.properties;
			try {
				logger.finest("Loading: " + label);
				if (baseClass.isAssignableFrom(Class.forName(descriptor
						.getBaseClass()))) {
					// the plugin inherits from the baseClass, we carry on to
					// look for the mode
					for (Iterator j = descriptor.getModes().iterator(); j
							.hasNext();) {
						String pmode = (String) j.next();
						if (pmode.equals(modeName)) {
							// add the class:
							returnValue.add(label);
						}

					}
				}
			} catch (ClassNotFoundException e) {
				logger.severe("Class not found.");
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		return returnValue;
	}

	/**
	 *
	 */
	private void actualizePlugins() {
		if (importWizard == null) {
			importWizard = new ImportWizard(frame);
			importWizard.CLASS_LIST.clear();
			importWizard.buildClassList();
			pluginInfo = new HashMap();
			allPlugins = new Vector();
			allRegistrations = new HashSet();
			// the unmarshaller:
			IUnmarshallingContext unmarshaller = XmlBindingTools.getInstance()
					.createUnmarshaller();
			// the loop
			for (Iterator i = importWizard.CLASS_LIST.iterator(); i.hasNext();) {
				String xmlPluginFile = (String) i.next();
				if (xmlPluginFile.matches(pluginPrefixRegEx)) {
					// make file name:
					/*
					 * Here, this is not the File.separatorChar!!!
					 */
					xmlPluginFile = xmlPluginFile.replace('\\', '/')
							+ importWizard.lookFor;
					// this is one of our plugins:
					URL pluginURL = frame.getFreeMindClassLoader().getResource(
							xmlPluginFile);
					// unmarshal xml:
					Plugin plugin = null;
					try {
						logger.finest("Reading: " + xmlPluginFile + " from "
								+ pluginURL);
						InputStream in = pluginURL.openStream();
						plugin = (Plugin) unmarshaller.unmarshalDocument(in,
								null);
					} catch (Exception e) {
						// error case
						freemind.main.Resources.getInstance().logException(e);
						continue;
					}
					// plugin is loaded.
					for (Iterator j = plugin.getListChoiceList().iterator(); j
							.hasNext();) {
						Object obj = j.next();
						if (obj instanceof PluginAction) {
							PluginAction action = (PluginAction) obj;
							pluginInfo.put(action.getLabel(),
									new HookDescriptorPluginAction(frame,
											xmlPluginFile, plugin, action));
							allPlugins.add(action.getLabel());

						} else if (obj instanceof PluginRegistration) {
							PluginRegistration registration = (PluginRegistration) obj;
							allRegistrations
									.add(new HookDescriptorRegistration(frame,
											xmlPluginFile, plugin, registration));
							// logger.info("Added registration " +
							// registration.getClassName() +
							// " to allRegistrations=" + allRegistrations);
						}
					}
				}
			}
		}
	}

	public ModeControllerHook createModeControllerHook(String hookName) {
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		return (ModeControllerHook) createJavaHook(hookName, descriptor);
	}

	private MindMapHook createJavaHook(String hookName,
			HookDescriptorPluginAction descriptor) {
		try {
			// constructed.
			ClassLoader loader = descriptor.getPluginClassLoader();
			Class hookClass = Class.forName(descriptor.getClassName(), true,
					loader);
			MindMapHook hook = (MindMapHook) hookClass.newInstance();
			decorateHook(hookName, descriptor, hook);
			return hook;
		} catch (Throwable e) {
			String path = "";
			for (Iterator it = descriptor.getPluginClasspath().iterator(); it.hasNext();) {
				PluginClasspath plPath = (PluginClasspath) it.next();
				path += plPath.getJar() + ";";
			}
			freemind.main.Resources.getInstance().logException(
					e,
					"Error occurred loading hook: " + descriptor.getClassName()
							+ "\nClasspath: " + path + "\nException:");
			return null;
		}
	}

	/**
	 * Do not call this method directly. Call ModeController.createNodeHook
	 * instead.
	 */
	public NodeHook createNodeHook(String hookName) {
		logger.finest("CreateNodeHook: " + hookName);
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		return (NodeHook) createJavaHook(hookName, descriptor);
	}

	private void decorateHook(String hookName,
			final HookDescriptorPluginAction descriptor, MindMapHook hook) {
		hook.setProperties(descriptor.getProperties());
		hook.setName(hookName);
		PluginBaseClassSearcher pluginBaseClassSearcher = new PluginBaseClassSearcher() {

			public Object getPluginBaseObject() {
				return getPluginBaseClass(descriptor);
			}
		};
		hook.setPluginBaseClass(pluginBaseClassSearcher);
	}

	/**
	 */
	public void decorateAction(String hookName, AbstractAction action) {
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		String name = descriptor.getName();
		if (name != null) {
			action.putValue(AbstractAction.NAME, name);
		} else {
			action.putValue(AbstractAction.NAME, descriptor.getClassName());
		}
		String docu = descriptor.getDocumentation();
		if (docu != null) {
			action.putValue(AbstractAction.SHORT_DESCRIPTION, docu);
			action.putValue(AbstractAction.LONG_DESCRIPTION, docu);
		}
		String icon = descriptor.getIconPath();
		if (icon != null) {
			ImageIcon imageIcon = new ImageIcon(descriptor
					.getPluginClassLoader().getResource(icon));
			action.putValue(AbstractAction.SMALL_ICON, imageIcon);
		}
		String key = descriptor.getKeyStroke();
		if (key != null)
			action.putValue(AbstractAction.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(key));

	}

	/**
	 * @return returns a list of menu position strings for the
	 *         StructuredMenuHolder.
	 */
	public List getHookMenuPositions(String hookName) {
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		return descriptor.menuPositions;
	}

	/**
	 */
	public HookInstanciationMethod getInstanciationMethod(String hookName) {
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		return descriptor.getInstanciationMethod();
	}

	/**
	 * Each Plugin can have a list of HookRegistrations that are called after
	 * the corresponding mode is enabled. (Like singletons.) One of these can
	 * operate as the pluginBase that is accessible to every normal
	 * plugin_action via the getPluginBaseClass method.
	 * 
	 * @return A list of RegistrationContainer elements. The field
	 *         hookRegistrationClass of RegistrationContainer is a class that is
	 *         (probably) of HookRegistration type. You have to register every
	 *         registration via the registerRegistrationContainer method when
	 *         instanciated (this is typically done in the ModeController).
	 */
	public List getRegistrations() {
		Class mode = MindMapController.class;
		actualizePlugins();
		Vector returnValue = new Vector();
		for (Iterator i = allRegistrations.iterator(); i.hasNext();) {
			HookDescriptorRegistration descriptor = (HookDescriptorRegistration) i
					.next();
			// PluginRegistration registration =
			// descriptor.getPluginRegistration();
			boolean modeFound = false;
			for (Iterator j = (descriptor.getListPluginModeList()).iterator(); j
					.hasNext();) {
				PluginMode possibleMode = (PluginMode) j.next();
				if (mode.getPackage().getName()
						.equals(possibleMode.getClassName())) {
					modeFound = true;
				}
			}
			if (!modeFound)
				continue;
			try {
				Plugin plugin = descriptor.getPluginBase();
				ClassLoader loader = descriptor.getPluginClassLoader();
				Class hookRegistrationClass = Class.forName(
						descriptor.getClassName(), true, loader);
				RegistrationContainer container = new RegistrationContainer();
				container.hookRegistrationClass = hookRegistrationClass;
				container.correspondingPlugin = plugin;
				container.isPluginBase = descriptor.getIsPluginBase();
				returnValue.add(container);
			} catch (ClassNotFoundException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		return returnValue;
	}

	/**
	 * A plugin base class is a common registration class of multiple plugins.
	 * It is useful to embrace several related plugins (example: EncryptedNote
	 * -> Registration).
	 * 
	 * @return the base class if declared and successfully instanciated or NULL.
	 */
	public Object getPluginBaseClass(String hookName) {
		logger.finest("getPluginBaseClass: " + hookName);
		HookDescriptorPluginAction descriptor = getHookDescriptor(hookName);
		return getPluginBaseClass(descriptor);
	}

	/**
	 */
	private Object getPluginBaseClass(HookDescriptorPluginAction descriptor) {
		// test if registration is present:
		Object baseClass = null;
		String label = descriptor.getPluginBase().getLabel();
		if (allRegistrationInstances.containsKey(label)) {
			baseClass = allRegistrationInstances.get(label);
		}
		return baseClass;
	}

	/**
	 */
	private HookDescriptorPluginAction getHookDescriptor(String hookName) {
		HookDescriptorPluginAction descriptor = (HookDescriptorPluginAction) pluginInfo
				.get(hookName);
		if (hookName == null || descriptor == null)
			throw new IllegalArgumentException("Unknown hook name " + hookName);
		return descriptor;
	}

	public JMenuItem getMenuItem(String pHookName, AbstractAction pHookAction) {
		HookDescriptorPluginAction descriptor = getHookDescriptor(pHookName);
		if (descriptor.isSelectable()) {
			return new JCheckBoxMenuItem(pHookAction);
		} else {
			return new JMenuItem(pHookAction);
		}
	}

}
