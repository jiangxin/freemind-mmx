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
 * Created on 06.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import freemind.main.Resources;
import freemind.modes.MindIcon;

;

/**
 * @author dimitri 06.05.2005
 */
public class ConditionRenderer implements ListCellRenderer {

	final public static Color SELECTED_BACKGROUND = new Color(207, 247, 202);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
	 * .JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null)
			return new JLabel(Resources.getInstance().getResourceString(
					"filter_no_filtering"));
		JComponent component;
		if (value instanceof MindIcon) {
			component = new JLabel(((MindIcon) value).getIcon());
		} else if (value instanceof Condition) {
			Condition cond = (Condition) value;
			component = cond.getListCellRendererComponent();
		} else {
			component = new JLabel(value.toString());
		}
		component.setOpaque(true);
		if (isSelected) {
			component.setBackground(SELECTED_BACKGROUND);
		} else {
			component.setBackground(Color.WHITE);
		}
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		return component;
	}

}
