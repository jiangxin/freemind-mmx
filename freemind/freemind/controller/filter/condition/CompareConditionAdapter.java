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

import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;

abstract class CompareConditionAdapter extends NodeCondition {

	static final String IGNORE_CASE = "ignore_case";
	static final String VALUE = "value";
	private String conditionValue;
	private boolean ignoreCase;

	CompareConditionAdapter(String value, boolean ignoreCase) {
		super();
		this.conditionValue = value;
		this.ignoreCase = ignoreCase;
	}

	protected int compareTo(String nodeValue) throws NumberFormatException {
		try {
			int i2 = Integer.parseInt(conditionValue);
			int i1 = Integer.parseInt(nodeValue);
			return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
		} catch (NumberFormatException fne) {
		}
		;
		double d2;
		try {
			d2 = Double.parseDouble(conditionValue);
		} catch (NumberFormatException fne) {
			return ignoreCase ? nodeValue.compareToIgnoreCase(conditionValue)
					: nodeValue.compareTo(conditionValue);
		}
		;
		double d1 = Double.parseDouble(nodeValue);
		return Double.compare(d1, d2);
	}

	public void saveAttributes(XMLElement child) {
		super.saveAttributes(child);
		child.setAttribute(VALUE, conditionValue);
		child.setAttribute(IGNORE_CASE, Tools.BooleanToXml(ignoreCase));
	}

	public String createDescription(String attribute, int comparationResult,
			boolean succeed) {
		String simpleCondition;
		switch (comparationResult) {
		case -1:
			simpleCondition = succeed ? ConditionFactory.FILTER_LT
					: ConditionFactory.FILTER_GE;
			break;
		case 0:
			simpleCondition = Resources.getInstance().getResourceString(
					succeed ? ConditionFactory.FILTER_IS_EQUAL_TO
							: ConditionFactory.FILTER_IS_NOT_EQUAL_TO);
			break;
		case 1:
			simpleCondition = succeed ? ConditionFactory.FILTER_GT
					: ConditionFactory.FILTER_LE;
			break;
		default:
			throw new IllegalArgumentException();
		}
		return ConditionFactory.createDescription(attribute, simpleCondition,
				conditionValue, ignoreCase);
	}

}