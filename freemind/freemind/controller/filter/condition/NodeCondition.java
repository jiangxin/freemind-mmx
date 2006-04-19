/*
 * Created on 15.05.2005
 *
 */
package freemind.controller.filter.condition;


import javax.swing.JComponent;

/**
 * @author dimitri
 * 15.05.2005
 */
public abstract class NodeCondition implements Condition {

    private String description;
    private JComponent renderer;
    
    protected NodeCondition (String description) {
        super();
        this.description = description;
    }
    
    public JComponent getListCellRendererComponent() {
        if(renderer == null){
            renderer = ConditionFactory.createCellRendererComponent(description);
        }
        return renderer;
    }
    
    public String toString(){
        return description;
    }

}
