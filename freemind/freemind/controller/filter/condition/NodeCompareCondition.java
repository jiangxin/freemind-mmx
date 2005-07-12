/*
 * Created on 17.05.2005
 *
 */
package freemind.controller.filter.condition;

import freemind.modes.MindMapNode;


class NodeCompareCondition extends CompareConditionAdapter{
    
    private String conditionValue;
    private int comparationResult;
    private boolean succeed;
     NodeCompareCondition(
            String description,
            String value,
            boolean ignoreCase,
            int comparationResult,
            boolean succeed) {
        super(description, value, ignoreCase);   
        this.comparationResult = comparationResult;
        this.succeed = succeed;
    }
    
    public boolean checkNode(MindMapNode node) {
        try{
            return succeed == (compareTo(node.getText()) == comparationResult);
        }
        catch(NumberFormatException  fne)
        {
            return false;
        }
    }
}