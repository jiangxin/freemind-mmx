/*
 * Created on 18.04.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.controller.filter.condition;

import javax.swing.JComponent;
import javax.swing.JLabel;

import freemind.main.Resources;
import freemind.modes.MindMapNode;

public class NoFilteringCondition implements Condition {

    private static String description;
    private static JComponent renderer;
    private static NoFilteringCondition condition;
    private NoFilteringCondition() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String toString(){
        if(description == null){
            description = Resources.getInstance().getResourceString("filter_no_filtering");
        }
        return  description;
    }
    public boolean checkNode(MindMapNode node) {
        return true;
    }
    public JComponent getListCellRendererComponent() {
        if(renderer == null){
            renderer = new JLabel(description);
        }
        return renderer;
    }
    
    public static Condition createCondition(){
        if(condition == null){
            condition = new NoFilteringCondition();
        }
        return condition;
    }

}
