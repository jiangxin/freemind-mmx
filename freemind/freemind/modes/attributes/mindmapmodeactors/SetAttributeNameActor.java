/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.SetAttributeNameElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeNameActor extends AbstractActorXml {

    public SetAttributeNameActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String name){
        SetAttributeNameElementaryAction action = new SetAttributeNameElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setName(name);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String name){
        final String previousName = model.getAttribute(row).getName();
        ActionPair actionPair = new ActionPair(
                createAction(model, row, name), 
                createAction(model, row, previousName));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeNameElementaryAction){
            SetAttributeNameElementaryAction setAttributeNameAction = (SetAttributeNameElementaryAction)action;
            act(getNode(setAttributeNameAction.getNode()).getAttributes(), 
                    setAttributeNameAction.getRow(),
                    setAttributeNameAction.getName());                    
        }

    }

    private void act(NodeAttributeTableModel model, int row, String name) {
        model.getAttribute(row).setName(name);        
        model.fireTableCellUpdated(row, 0);        
    }

    public Class getDoActionClass() {
        return SetAttributeNameElementaryAction.class;
    }

}
