/*
 * Created on 05.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freemind.controller.filter.condition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import freemind.controller.Controller;
import freemind.controller.filter.FilterController;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

/**
 * @author d
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
		return false;
	}

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public JComponent getListCellRendererComponent() {
        JComponent component = new JPanel();
        component.setBackground(Color.WHITE);
        component.add(new JLabel(Controller.getInstance().getResourceString("filter_icon")));
        component.add(new JLabel(Controller.getInstance().getResourceString("filter_contains")));
        component.add(Controller.getInstance().getFilterController().getMindIconRenderer().getComponent(getIconName()));
        return component;
    }

    private String getIconName() {
        return iconName;
    }
}
