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
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.controller.Controller;
import freemind.main.Resources;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

public class IconContainedCondition implements Condition {
	private String iconName;
	public IconContainedCondition(String iconName){
		this.iconName = iconName;
	}

	public boolean checkNode(Controller c, MindMapNode node) {
		List icons = node.getIcons();
		for (ListIterator i=icons.listIterator(); i.hasNext(); ) {
			MindIcon nextIcon = (MindIcon) i.next() ;
			if (iconName.equals(nextIcon.getName())) return true;
		}
		Set stateIcons = node.getStateIcons().keySet();
		for(Iterator stateIcon = stateIcons.iterator(); stateIcon.hasNext();){
			String nextIcon = (String) stateIcon.next() ;
			if (iconName.equals(nextIcon)) return true;		    
		}
		return false;
	}

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public JComponent getListCellRendererComponent() {
        JCondition component = new JCondition(); 
        String text = Resources.getInstance().getResourceString("filter_icon")
        + ' ' 
        + Resources.getInstance().getResourceString("filter_contains")
        + ' ';
        component.add(new JLabel(text));
        component.add(MindIcon.factory(getIconName()).getRendererComponent());
        return component;
    }

    private String getIconName() {
        return iconName;
    }
}
