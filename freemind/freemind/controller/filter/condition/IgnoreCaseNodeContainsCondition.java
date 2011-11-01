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
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;

import freemind.controller.Controller;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

class IgnoreCaseNodeContainsCondition extends NodeCondition {

	static final String VALUE = "value";
	static final String NAME = "ignore_case_node_contains_condition";
	private String value;

	IgnoreCaseNodeContainsCondition(String value) {
		super();
		this.value = value.toLowerCase();
	}

	public boolean checkNode(Controller c, MindMapNode node) {
		return node.getText().toLowerCase().indexOf(value) > -1;
	}

	public void save(XMLElement element) {
		XMLElement child = new XMLElement();
		child.setName(NAME);
		super.saveAttributes(child);
		child.setAttribute(VALUE, value);
		element.addChild(child);
	}

	static Condition load(XMLElement element) {
		return new IgnoreCaseNodeContainsCondition(
				element.getStringAttribute(VALUE));
	}

	protected String createDesctiption() {
		final String nodeCondition = Resources.getInstance().getResourceString(
				ConditionFactory.FILTER_NODE);
		final String simpleCondition = Resources.getInstance()
				.getResourceString(ConditionFactory.FILTER_CONTAINS);
		return ConditionFactory.createDescription(nodeCondition,
				simpleCondition, value, true);
	}
}