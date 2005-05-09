/*
 * Created on 06.05.2005
 *
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import freemind.controller.Controller;
import freemind.modes.MindIcon;

/**
 * @author dimitri
 * 06.05.2005
 */
public class MindIconRenderer implements ListCellRenderer{
    private Controller c;
    private Map name2components = null;
    private ListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    public MindIconRenderer(Controller c){
        this.c = c;
    }
    Component getComponent(String name){
        if(name2components == null) name2components = new HashMap();
        JLabel component = (JLabel)name2components.get(name);
        if (component == null){
            Icon icon = MindIcon.factory(name).getIcon(c.getFrame());
            component = new JLabel(icon);
            name2components.put(name, component);            
        }        
        return component;
    }
    
    Component getComponent(MindIcon mi){
        return getComponent(mi.getName());
    }
    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if(value == null) return new JLabel("--");
        if (value instanceof MindIcon){
	        MindIcon mi = (MindIcon) value;
	        Component component = getComponent(mi);
	        if (isSelected  || cellHasFocus){
	            component.setBackground(Color.BLUE);            
	        }
	        return component;
        }
        return defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus );
    }
    
}
