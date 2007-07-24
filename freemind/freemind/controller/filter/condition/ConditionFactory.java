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

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.common.NamedObject;
import freemind.main.Resources;
import freemind.main.XMLElement;

/**
 * @author dimitri
 * 17.05.2005
 */
public class ConditionFactory {

    /**
     *
     */
    public ConditionFactory() {
    }

    String createDescription(
            NamedObject attribute,
            NamedObject simpleCondition,
            String value,
            boolean ignoreCase){
        return createDescription(attribute.toString(), simpleCondition, value, ignoreCase);
    }

        String createDescription(
                String attribute,
                NamedObject simpleCondition,
                String value,
                boolean ignoreCase){
        boolean considerValue = ! simpleCondition.equals("filter_exist") && ! simpleCondition.equals("filter_does_not_exist");
        String description = attribute + " "
        + simpleCondition.toString() + " "
        + (considerValue ?  "\"" + value + "\"": "")
        + (considerValue && ignoreCase ? ", " + Resources.getInstance().getResourceString("filter_ignore_case") : "");
        return description;
    }

    public Condition loadCondition(XMLElement element){
    	if (element.getName().equalsIgnoreCase(NodeContainsCondition.NAME))
			return NodeContainsCondition.load(element);
    	if (element.getName().equalsIgnoreCase(IgnoreCaseNodeContainsCondition.NAME))
			return IgnoreCaseNodeContainsCondition.load(element);
    	if (element.getName().equalsIgnoreCase(NodeCompareCondition.NAME))
			return NodeCompareCondition.load(element);
       	if (element.getName().equalsIgnoreCase(AttributeCompareCondition.NAME))
			return AttributeCompareCondition.load(element);
       	if (element.getName().equalsIgnoreCase(AttributeExistsCondition.NAME))
			return AttributeExistsCondition.load(element);
       	if (element.getName().equalsIgnoreCase(AttributeNotExistsCondition.NAME))
			return AttributeNotExistsCondition.load(element);
       	if (element.getName().equalsIgnoreCase(IconContainedCondition.NAME))
			return IconContainedCondition.load(element);
       	if (element.getName().equalsIgnoreCase(ConditionNotSatisfiedDecorator.NAME)){
       		return ConditionNotSatisfiedDecorator.load(element);
       	}
      	if (element.getName().equalsIgnoreCase(ConjunctConditions.NAME)){
       		return ConjunctConditions.load(element);
       	}
      	if (element.getName().equalsIgnoreCase(DisjunctConditions.NAME)){
       		return DisjunctConditions.load(element);
       	}
    	return null;
    }

	public Condition createAttributeCondition(
            String attribute,
            NamedObject simpleCondition,
            String value,
            boolean ignoreCase)
    {
    	String description   = createDescription(attribute, simpleCondition, value, ignoreCase);
        if(simpleCondition.equals("filter_exist"))
            return new AttributeExistsCondition(description, attribute);
        if(simpleCondition.equals("filter_does_not_exist"))
            return new AttributeNotExistsCondition(description, attribute);
        if(ignoreCase){
            if(simpleCondition.equals("filter_is_equal_to"))
                return new AttributeCompareCondition(description, attribute,value, true, 0, true);
            if(simpleCondition.equals("filter_is_not_equal_to"))
                return new AttributeCompareCondition(description, attribute,value,  true, 0, false);
            if(simpleCondition.equals(">"))
                return  new AttributeCompareCondition(description, attribute,value,  true, 1, true);
            if(simpleCondition.equals(">="))
                return new AttributeCompareCondition(description, attribute,value,  true, -1, false);
            if(simpleCondition.equals("<"))
                return new AttributeCompareCondition(description, attribute,value,  true, -1, true);
            if(simpleCondition.equals("<="))
                return new AttributeCompareCondition(description, attribute,value,  true, 1, false);
        }
        else{
            if(simpleCondition.equals("filter_is_equal_to"))
                return new AttributeCompareCondition(description, attribute,value, false, 0, true);
            if(simpleCondition.equals("filter_is_not_equal_to"))
                return new AttributeCompareCondition(description, attribute,value, false, 0, false);
            if(simpleCondition.equals(">"))
                return  new AttributeCompareCondition(description, attribute,value, false, 1, true);
            if(simpleCondition.equals(">="))
                return new AttributeCompareCondition(description, attribute,value, false, -1, false);
            if(simpleCondition.equals("<"))
                return new AttributeCompareCondition(description, attribute,value, false, -1, true);
            if(simpleCondition.equals("<="))
                return new AttributeCompareCondition(description, attribute,value, false, 1, false);
        }
        return null;
    }
    public Condition createCondition(
            NamedObject attribute,
            NamedObject simpleCondition,
            String value,
            boolean ignoreCase)
    {
        if (attribute.equals("filter_icon")
            && simpleCondition.equals("filter_contains")    )
            return new IconContainedCondition(value);
        if (attribute.equals("filter_node")){
            	String description   = createDescription(attribute, simpleCondition, value, ignoreCase);
                return createNodeCondition(description, simpleCondition, value, ignoreCase);
        }
        return null;
    }

    public  NamedObject[] getNodeConditionNames() {
            return new NamedObject[] {
    //                TranslatedString.create("filter_exist"),
    //                TranslatedString.create("filter_does_not_exist"),
                    Resources.getInstance().createTranslatedString("filter_contains"),
                    Resources.getInstance().createTranslatedString("filter_is_equal_to"),
                    Resources.getInstance().createTranslatedString("filter_is_not_equal_to"),
                    NamedObject.literal(">"),
                    NamedObject.literal(">="),
                    NamedObject.literal("<="),
                    NamedObject.literal("<"),
            };
        }

    public Object[] getIconConditionNames() {
        return new NamedObject[] {
                Resources.getInstance().createTranslatedString("filter_contains"),
        };
    }
    public  NamedObject[] getAttributeConditionNames() {
        return new NamedObject[] {
                Resources.getInstance().createTranslatedString("filter_exist"),
                Resources.getInstance().createTranslatedString("filter_does_not_exist"),
//                TranslatedString.create("filter_contains"),
                Resources.getInstance().createTranslatedString("filter_is_equal_to"),
                Resources.getInstance().createTranslatedString("filter_is_not_equal_to"),
                NamedObject.literal(">"),
                NamedObject.literal(">="),
                NamedObject.literal("<="),
                NamedObject.literal("<"),
        };
    }

    protected Condition createNodeCondition(
            String description,
            NamedObject simpleCondition,
            String value,
            boolean ignoreCase) {
        if(ignoreCase){
            if(simpleCondition.equals("filter_contains")){
                if (value.equals(""))
                    return null;
                return new IgnoreCaseNodeContainsCondition(description, value);
            }
            if(simpleCondition.equals("filter_is_equal_to"))
                return new NodeCompareCondition(description, value, true, 0, true);
            if(simpleCondition.equals("filter_is_not_equal_to"))
                return new NodeCompareCondition(description, value,  true, 0, false);
            if(simpleCondition.equals(">"))
                return  new NodeCompareCondition(description, value,  true, 1, true);
            if(simpleCondition.equals(">="))
                return new NodeCompareCondition(description, value,  true, -1, false);
            if(simpleCondition.equals("<"))
                return new NodeCompareCondition(description, value,  true, -1, true);
            if(simpleCondition.equals("<="))
                return new NodeCompareCondition(description, value,  true, 1, false);
        }
        else{
            if(simpleCondition.equals("filter_contains")){
                if (value.equals(""))
                    return null;
                return new NodeContainsCondition(description, value);
            }
            if(simpleCondition.equals("filter_is_equal_to"))
                return new NodeCompareCondition(description, value, false, 0, true);
            if(simpleCondition.equals("filter_is_not_equal_to"))
                return new NodeCompareCondition(description, value, false, 0, false);
            if(simpleCondition.equals(">"))
                return  new NodeCompareCondition(description, value, false, 1, true);
            if(simpleCondition.equals(">="))
                return new NodeCompareCondition(description, value, false, -1, false);
            if(simpleCondition.equals("<"))
                return new NodeCompareCondition(description, value, false, -1, true);
            if(simpleCondition.equals("<="))
                return new NodeCompareCondition(description, value, false, 1, false);
        }
        return null;
    }

    static public JComponent createCellRendererComponent(String description) {
        JCondition component = new JCondition();   
        JLabel label = new JLabel(description);
        component.add(label);
        return component;
    }

}
