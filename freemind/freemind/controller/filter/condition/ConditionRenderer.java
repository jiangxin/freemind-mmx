/*
 * Created on 06.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



/**
 * @author dimitri
 * 06.05.2005
 */
public class ConditionRenderer implements ListCellRenderer {

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus){
        if(value == null) return new JLabel("--");
        Condition cond = (Condition) value;
        JComponent component = cond.getListCellRendererComponent();
        if (isSelected  || cellHasFocus){
            component.setBackground(Color.BLUE);            
        }
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;       
    }

}
