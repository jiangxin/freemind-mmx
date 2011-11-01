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
 * Created on 15.05.2005
 *
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;

import freemind.main.XMLElement;

/**
 * @author dimitri 15.05.2005
 */
public abstract class NodeCondition implements Condition {
	private JComponent renderer;
	private String description;

	protected NodeCondition() {
	}

	public JComponent getListCellRendererComponent() {
		if (renderer == null) {
			renderer = ConditionFactory.createCellRendererComponent(toString());
		}
		return renderer;
	}

	public String toString() {
		if (description == null) {
			description = createDesctiption();
		}
		return description;
	}

	abstract protected String createDesctiption();

	public void saveAttributes(XMLElement child) {
	}

}
