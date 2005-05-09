/*
 * Created on 08.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


import freemind.controller.Controller;
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
        return true;
    }

    /* (non-Javadoc)
     * @see freemind.controller.filter.condition.Condition#getListCellRendererComponent()
     */
    public JComponent getListCellRendererComponent() {
        JComponent component = new JPanel();
        component.setBackground(Color.WHITE);
        component.add(new JLabel("("));
        Condition cond = (Condition)conditions[0];
        component.add(cond.getListCellRendererComponent());
        int i;
        for(i=1; i<conditions.length; i++){
            component.add(new JLabel(Controller.getInstance().getResourceString("filter_or") + " "));
            cond = (Condition)conditions[i];
            component.add(cond.getListCellRendererComponent());        }
        component.add(new JLabel(")"));
        return component;
    }
}
