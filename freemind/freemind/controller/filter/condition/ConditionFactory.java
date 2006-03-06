/*
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import freemind.controller.filter.util.TranslatedString;
import freemind.main.Resources;

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
            TranslatedString attribute,
            TranslatedString simpleCondition,
            String value,
            boolean ignoreCase){
        return createDescription(attribute.toString(), simpleCondition, value, ignoreCase);
    }

        String createDescription(
                String attribute,
                TranslatedString simpleCondition,
                String value,
                boolean ignoreCase){
        boolean considerValue = ! simpleCondition.equals("filter_exist") && ! simpleCondition.equals("filter_does_not_exist");
        String description = attribute + " "
        + simpleCondition.toString() + " "
        + (considerValue ?  "\"" + value + "\"": "")
        + (considerValue && ignoreCase ? ", " + Resources.getInstance().getResourceString("filter_ignore_case") : "");
        return description;
    }

    public Condition createAttributeCondition(
            String attribute,
            TranslatedString simpleCondition,
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
            TranslatedString attribute,
            TranslatedString simpleCondition,
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

    public  TranslatedString[] getNodeConditionNames() {
            return new TranslatedString[] {
    //                new TranslatedString("filter_exist"),
    //                new TranslatedString("filter_does_not_exist"),
                    new TranslatedString("filter_contains"),
                    new TranslatedString("filter_is_equal_to"),
                    new TranslatedString("filter_is_not_equal_to"),
                    TranslatedString.literal(">"),
                    TranslatedString.literal(">="),
                    TranslatedString.literal("<="),
                    TranslatedString.literal("<"),
            };
        }

    public Object[] getIconConditionNames() {
        return new TranslatedString[] {
                new TranslatedString("filter_contains"),
        };
    }
    public  TranslatedString[] getAttributeConditionNames() {
        return new TranslatedString[] {
                new TranslatedString("filter_exist"),
                new TranslatedString("filter_does_not_exist"),
//                new TranslatedString("filter_contains"),
                new TranslatedString("filter_is_equal_to"),
                new TranslatedString("filter_is_not_equal_to"),
                TranslatedString.literal(">"),
                TranslatedString.literal(">="),
                TranslatedString.literal("<="),
                TranslatedString.literal("<"),
        };
    }

    protected Condition createNodeCondition(
            String description,
            TranslatedString simpleCondition,
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
        label.setBackground(Color.PINK);
        component.add(label);
        return component;
    }

}
