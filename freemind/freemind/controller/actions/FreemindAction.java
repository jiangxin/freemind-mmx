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
 * Created on 19.07.2004
 */
/*$Id: FreemindAction.java,v 1.1.2.1 2004-07-19 05:50:36 christianfoltin Exp $*/

package freemind.controller.actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import freemind.modes.ModeController;

/**
 * @author foltin
 *
 */
public abstract class FreemindAction extends AbstractAction {

    private final Icon actionItem;
    private static Icon enabledIcon;
    private static final String ENABLED_ICON_PATH = "images/icons/button_ok.png";
    private final ModeController modeController;

    /**
     * @param title
     * @param icon
     */
    public FreemindAction(String title, Icon icon,  ModeController modeController) {
        super(title, icon);
        this.actionItem = icon;
        this.modeController = modeController;
        if(enabledIcon == null){
            enabledIcon = new ImageIcon(modeController.getFrame().getResource(ENABLED_ICON_PATH));
        }
        
    }

    protected void setSelected(JMenuItem menuItem, boolean state) {
		if(state) {
		    menuItem.setIcon(enabledIcon);
		} else {
		    menuItem.setIcon(actionItem);
		}
    }
    
}
