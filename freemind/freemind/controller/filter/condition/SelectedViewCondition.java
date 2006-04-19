/*
 * Created on 18.04.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;

import freemind.main.Resources;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;

public class SelectedViewCondition implements Condition {

    private static String description;
    private static JComponent renderer;
    private static Condition condition;
    public SelectedViewCondition() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String toString(){
        if(description == null){
            description = Resources.getInstance().getResourceString("filter_selected_node_view");
        }
        return  description;
    }
    public boolean checkNode(MindMapNode node) {
        final NodeView viewer = node.getViewer();
        return viewer != null && viewer.isSelected();
    }
    public JComponent getListCellRendererComponent() {
        if(renderer == null){
            renderer = ConditionFactory.createCellRendererComponent(description);
        }
        return renderer;
    }

    public static Condition CreateCondition(){
        if(condition == null){
            condition = new SelectedViewCondition();
        }
        return condition;
    }
}
