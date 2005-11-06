/*
 * Created on 08.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import freemind.main.Resources;
import freemind.modes.MindMapNode;

/**
 * @author dimitri
 * 08.05.2005
 */
public class ConditionNotSatisfiedDecorator implements Condition {

    private Condition originalCondition;
    /**
     *
     */
    public ConditionNotSatisfiedDecorator(Condition originalCondition) {
        super();
        this.originalCondition = originalCondition;
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#checkNode(freemind.modes.MindMapNode)
     */
    public boolean checkNode(MindMapNode node) {
        return ! originalCondition.checkNode(node);
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#getListCellRendererComponent()
     */
    public JComponent getListCellRendererComponent() {
        JCondition component = new JCondition();   
        String text = Resources.getInstance().getResourceString("filter_not") + ' ';
        component.add(new JLabel(text));
        component.add(originalCondition.getListCellRendererComponent());
        return component;
    }

}
