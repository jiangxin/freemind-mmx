/*
 * Created on 06.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import freemind.modes.MindIcon;
import freemind.main.Resources;;


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
            return new JLabel(Resources.getInstance().getResourceString("attribute_no_filtering"));
        JComponent component;
        if(value instanceof MindIcon){
            component = ((MindIcon)value).getRendererComponent();
        }
        else if(value instanceof Condition){ 
            Condition cond = (Condition) value;            
            component = cond.getListCellRendererComponent();
        }
        else{
            component = new JLabel(value.toString());
        }
        component.setOpaque(true);
        if (isSelected){
            component.setBackground(SELECTED_BACKGROUND);            
        }
        else{
            component.setBackground(Color.WHITE);            
        }
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;       
    }

}
