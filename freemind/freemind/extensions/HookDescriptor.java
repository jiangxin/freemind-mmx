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
/*$Id: HookDescriptor.java,v 1.1.2.5 2004-11-15 07:00:52 christianfoltin Exp $*/
package freemind.extensions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginActionType;
import freemind.controller.actions.generated.instance.PluginMenuType;
import freemind.controller.actions.generated.instance.PluginModeType;
import freemind.controller.actions.generated.instance.PluginPropertyType;
import freemind.main.FreeMindMain;


class HookDescriptor {
	private Properties properties;
	public Vector menuPositions;
	private Vector modes;
	private PluginActionType pluginAction;
    private final Plugin pluginBase;
	private final FreeMindMain frame;
	public HookDescriptor(FreeMindMain frame, PluginActionType pluginAction, Plugin pluginBase) {
		this.frame = frame;
		this.pluginAction = pluginAction;
        this.pluginBase = pluginBase;
		if (pluginAction.getName() == null) {	
			pluginAction.setName(pluginAction.getLabel());
		}
		menuPositions = new Vector();
		for (Iterator i = pluginAction.getPluginMenu().iterator(); i.hasNext();) {
			PluginMenuType menu = (PluginMenuType) i.next();
			menuPositions.add(menu.getLocation());
		}
		properties = new Properties();
		for (Iterator i = pluginAction.getPluginProperty().iterator(); i.hasNext();) {
			PluginPropertyType property = (PluginPropertyType) i.next();
			properties.put(property.getName(), property.getValue());
		}
		modes = new Vector();
		for (Iterator i = pluginAction.getPluginMode().iterator(); i.hasNext();) {
			PluginModeType mode = (PluginModeType) i.next();
			modes.add(mode.getClassName());
		}
	}
	public String toString() {
		return "[HookDescriptor props="
			+ properties
			+ ", menu positions="
			+ menuPositions
			+ "]";
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
	/**
	 * @param name
	 * @return
	 */
	private String getFromResourceIfNecessary(String string) {
		if(string==null) {
			return string;
		}
		if(string.startsWith("%")) {
			return frame.getController().getResourceString(string.substring(1));
		}
		return string;
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
		return pluginAction.getKeyStroke();
	}
	public Plugin getPluginBase(){
	    return pluginBase;
	}
	/**
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}
}