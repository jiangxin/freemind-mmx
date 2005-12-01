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

    final public static Color SELECTED_BACKGROUND = new Color(207, 247, 202);
    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus){
        if(value == null) 
            return new JLabel("--");
        if(! (value instanceof Condition)) 
            return new JLabel(value.toString());

        Condition cond = (Condition) value;
        JComponent component = cond.getListCellRendererComponent();
        component.setOpaque(true);
        if (isSelected  || cellHasFocus){
            component.setBackground(SELECTED_BACKGROUND);            
        }
        else{
            component.setBackground(Color.WHITE);            
        }
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;       
    }

}
