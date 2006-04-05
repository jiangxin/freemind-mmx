/*
 * Created on 05.05.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.main.Resources;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

public class IconContainedCondition implements Condition {
	private String iconName;
	public IconContainedCondition(String iconName){
		this.iconName = iconName;
	}

	public boolean checkNode(MindMapNode node) {
		List icons = node.getIcons();
		for (ListIterator i=icons.listIterator(); i.hasNext(); ) {
			MindIcon nextIcon = (MindIcon) i.next() ;
			if (iconName.equals(nextIcon.getName())) return true;
		}
		Set stateIcons = node.getStateIcons().keySet();
		for(Iterator stateIcon = stateIcons.iterator(); stateIcon.hasNext();){
			String nextIcon = (String) stateIcon.next() ;
			if (iconName.equals(nextIcon)) return true;		    
		}
		return false;
	}

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public JComponent getListCellRendererComponent() {
        JCondition component = new JCondition(); 
        String text = Resources.getInstance().getResourceString("filter_icon")
        + ' ' 
        + Resources.getInstance().getResourceString("filter_contains")
        + ' ';
        component.add(new JLabel(text));
        component.add(MindIcon.factory(getIconName()).getRendererComponent());
        return component;
    }

    private String getIconName() {
        return iconName;
    }
}
