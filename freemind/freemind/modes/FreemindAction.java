/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.modes;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;

/**
 * Common class for all actions that are disabled, when no map is open.
 * 
 * @author foltin
 * @date 22.10.2013
 */
public abstract class FreemindAction extends AbstractAction implements MenuItemEnabledListener{

	private final ControllerAdapter pControllerAdapter;
	protected static java.util.logging.Logger logger = null;
	/**
	 * @param title
	 *            is a fixed title (no translation is done via resources)
	 */
	public FreemindAction(String title, Icon icon,
			ControllerAdapter controllerAdapter) {
		super(title, icon);
		this.pControllerAdapter = controllerAdapter;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}

	}

	/**
	 * @param title
	 *            Title is a resource.
	 */
	public FreemindAction(String title, 
			ControllerAdapter controllerAdapter) {
		this(title, (String) null, controllerAdapter);
	}
	
	/**
	 * @param title
	 *            Title is a resource.
	 * @param iconPath
	 *            is a path to an icon.
	 */
	public FreemindAction(String title, String iconPath,
			final ControllerAdapter controllerAdapter) {
		this(controllerAdapter.getText(title), (iconPath == null) ? null
				: new ImageIcon(controllerAdapter.getResource(iconPath)),
				controllerAdapter);
	}

	public ControllerAdapter getControllerAdapter() {
		return pControllerAdapter;
	}
	
	/* (non-Javadoc)
	 * @see freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem, javax.swing.Action)
	 */
	public boolean isEnabled(JMenuItem pItem, Action pAction) {
		boolean result = pControllerAdapter != null && pControllerAdapter.getMap() != null;
		logger.finest("isEnabled " + pAction.getValue(AbstractAction.NAME) + "=" + result + " from " + pControllerAdapter);
		return result;
	}

}
