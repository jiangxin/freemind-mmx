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
public class AttributeNotExistsCondition extends NodeCondition {
private String attribute;
    /**
     * @param description
     * @param value
     * @param ignoreCase
     * @param comparationResult
     * @param succeed
     */
    public AttributeNotExistsCondition(String description, String attribute) {
        super(description);
        this.attribute = attribute;
    }
    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#checkNode(freemind.modes.MindMapNode)
     */
    public boolean checkNode(MindMapNode node) {
        AttributeTableModel attributes = node.getAttributes();
        for(int i = 0; i < attributes.getRowCount(); i++){
            if (attributes.getValueAt(i, 0).equals(attribute))
                return false;
        }
        return true;
    }
}
