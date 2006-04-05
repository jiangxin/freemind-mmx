/*
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;

import freemind.modes.MindMapNode;


class IgnoreCaseNodeContainsCondition extends NodeCondition{
    
    private String value;
    IgnoreCaseNodeContainsCondition(
            String description,
            String value) {
        super(description);   
        this.value = value.toLowerCase(); 
    }
    
    public boolean checkNode(MindMapNode node) {
        return node.getText().toLowerCase().indexOf(value) > -1;
    }
}