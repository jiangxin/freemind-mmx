/*
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;

import freemind.modes.MindMapNode;


class NodeContainsCondition extends NodeCondition{
        private String value;
        NodeContainsCondition(
                String description,
                String value) {
            super(description);   
            this.value = value; 
        }
        
        public boolean checkNode(MindMapNode node) {
            return node.getText().indexOf(value) > -1;
        }
}