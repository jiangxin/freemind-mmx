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
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

class NodeCompareCondition extends CompareConditionAdapter {

	static final String COMPARATION_RESULT = "comparation_result";
	static final String VALUE = "value";
	static final String NAME = "node_compare_condition";
	static final String SUCCEED = "succeed";
	private int comparationResult;
	private boolean succeed;

	NodeCompareCondition(String value, boolean ignoreCase,
			int comparationResult, boolean succeed) {
		super(value, ignoreCase);
		this.comparationResult = comparationResult;
		this.succeed = succeed;
	}

	public boolean checkNode(Controller c, MindMapNode node) {
		try {
			return succeed == (compareTo(node.getText()) == comparationResult);
		} catch (NumberFormatException fne) {
			return false;
		}
	}

	public void save(XMLElement element) {
		XMLElement child = new XMLElement();
		child.setName(NAME);
		super.saveAttributes(child);
		child.setIntAttribute(COMPARATION_RESULT, comparationResult);
		child.setAttribute(SUCCEED, Tools.BooleanToXml(succeed));
		element.addChild(child);
	}

	static Condition load(XMLElement element) {
		return new NodeCompareCondition(element.getStringAttribute(VALUE),
				Tools.xmlToBoolean(element
						.getStringAttribute(NodeCompareCondition.IGNORE_CASE)),
				element.getIntAttribute(COMPARATION_RESULT),
				Tools.xmlToBoolean(element.getStringAttribute(SUCCEED)));
	}

	protected String createDesctiption() {
		final String nodeCondition = Resources.getInstance().getResourceString(
				ConditionFactory.FILTER_NODE);
		return super.createDescription(nodeCondition, comparationResult,
				succeed);
	}
}