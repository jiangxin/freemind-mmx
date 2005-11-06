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
public class DisjunctConditions implements Condition {

    private Object[] conditions;
    /**
     *
     */
    public DisjunctConditions(Object[] conditions) {
        this.conditions = conditions;
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#checkNode(freemind.modes.MindMapNode)
     */
    public boolean checkNode(MindMapNode node) {
        int i;
        for(i=0; i<conditions.length; i++){
            Condition cond = (Condition)conditions[i];
            if (cond.checkNode(node)) return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#getListCellRendererComponent()
     */
    public JComponent getListCellRendererComponent() {
        JCondition component = new JCondition();   
        component.add(new JLabel("("));
        Condition cond = (Condition)conditions[0];
        component.add(cond.getListCellRendererComponent());
        int i;
        for(i=1; i<conditions.length; i++){
            String text = ' ' + Resources.getInstance().getResourceString("filter_or") + ' ';
            component.add(new JLabel(text));
            cond = (Condition)conditions[i];
            component.add(cond.getListCellRendererComponent());        }
        component.add(new JLabel(")"));
        return component;
    }
}
