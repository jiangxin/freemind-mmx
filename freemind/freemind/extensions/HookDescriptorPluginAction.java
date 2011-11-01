/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 22.07.2004
 */
/*$Id: HookDescriptorPluginAction.java,v 1.1.2.2 2008/01/13 20:55:34 christianfoltin Exp $*/
package freemind.extensions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginAction;
import freemind.controller.actions.generated.instance.PluginMenu;
import freemind.controller.actions.generated.instance.PluginMode;
import freemind.controller.actions.generated.instance.PluginProperty;
import freemind.main.FreeMindMain;

/**
 * This is an information class that holds all outer properties of a hook, i.e.
 * all contents of the XML description file.
 * 
 * Don't use this class for anything except for the implementation of a
 * HookFactory.
 * 
 * @author foltin
 * 
 */
public class HookDescriptorPluginAction extends HookDescriptorBase {
	private Properties properties;
	public Vector menuPositions;
	private Vector modes;
	private PluginAction pluginAction;

	public HookDescriptorPluginAction(FreeMindMain frame, String xmlPluginFile,
			Plugin pluginBase, PluginAction pluginAction) {
		super(pluginBase, frame, xmlPluginFile);
		this.pluginAction = pluginAction;
		if (pluginAction.getName() == null) {
			pluginAction.setName(pluginAction.getLabel());
		}
		menuPositions = new Vector();
		properties = new Properties();
		modes = new Vector();
		for (Iterator i = pluginAction.getListChoiceList().iterator(); i
				.hasNext();) {
			Object obj = i.next();
			if (obj instanceof PluginMenu) {
				PluginMenu menu = (PluginMenu) obj;
				menuPositions.add(menu.getLocation());
			}
			if (obj instanceof PluginProperty) {
				PluginProperty property = (PluginProperty) obj;
				properties.put(property.getName(), property.getValue());
			}
			if (obj instanceof PluginMode) {
				PluginMode mode = (PluginMode) obj;
				modes.add(mode.getClassName());
			}
		}
	}

	public String toString() {
		return "[HookDescriptor props=" + properties + ", menu positions="
				+ menuPositions + "]";
	}

	public HookInstanciationMethod getInstanciationMethod() {
		if (pluginAction.getInstanciation() != null) {
			HashMap allInstMethods = HookInstanciationMethod
					.getAllInstanciationMethods();
			for (Iterator i = allInstMethods.keySet().iterator(); i.hasNext();) {
				String name = (String) i.next();
				if (pluginAction.getInstanciation().equalsIgnoreCase(name)) {
					return (HookInstanciationMethod) allInstMethods.get(name);
				}
			}
		}
		// this is an error case?
		return HookInstanciationMethod.Other;
	}

	public Vector getModes() {
		return modes;
	}

	public String getBaseClass() {
		return pluginAction.getBase();
	}

	public String getName() {
		return getFromResourceIfNecessary(pluginAction.getName());
	}

	public String getClassName() {
		return pluginAction.getClassName();
	}

	public String getDocumentation() {
		return getFromResourceIfNecessary(pluginAction.getDocumentation());
	}

	public String getIconPath() {
		return pluginAction.getIconPath();
	}

	public String getKeyStroke() {
		return getFromPropertiesIfNecessary(pluginAction.getKeyStroke());
	}

	/**
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @return whether or not the plugin can be on/off and this should be
	 *         displayed in the menus.
	 */
	public boolean isSelectable() {
		return pluginAction.getIsSelectable();
	}

}
