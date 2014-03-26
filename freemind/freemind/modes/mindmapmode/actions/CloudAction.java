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
 * Created on 06.10.2004
 */


package freemind.modes.mindmapmode.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.AddCloudXmlAction;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * 
 */
public class CloudAction extends NodeGeneralAction implements MenuItemSelectedListener {

	public CloudAction(MindMapController controller) {
		super(controller, "cloud", "images/Cloud24.gif");
		setDoActionClass(getDoActionClass());
	}

	public Class getDoActionClass() {
		return AddCloudXmlAction.class;
	}
	
	public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
		return modeController.getSelected().getCloud() != null;
	}
}