/*
 * Created on 12.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import freemind.modes.MindMapNode;
import freemind.modes.attributes.AttributeTableModel;

/**
 * @author Dimitri Polivaev
 * 12.07.2005
 */
public class AttributeCompareCondition extends CompareConditionAdapter {
private String attribute;
private int comparationResult;
private boolean succeed;
    /**
     * @param description
     * @param value
     * @param ignoreCase
     * @param comparationResult
     * @param succeed
     */
    public AttributeCompareCondition(String description, String attribute, String value,
            boolean ignoreCase, int comparationResult, boolean succeed) {
        super(description, value, ignoreCase);
        this.attribute = attribute;
        this.comparationResult = comparationResult;
        this.succeed = succeed;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#checkNode(freemind.modes.MindMapNode)
     */
    public boolean checkNode(MindMapNode node) {
        AttributeTableModel attributes = node.getAttributes();
        for(int i = 0; i < attributes.getRowCount(); i++){
            try{
                if (attributes.getValueAt(i, 0).equals(attribute) 
                        && succeed == (compareTo(attributes.getValueAt(i, 1).toString()) == comparationResult))
                    return true;
            }
            catch(NumberFormatException  fne)
            {
            }
        }
        return false;
    }
}
