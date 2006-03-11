/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.SetAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeValueActor extends AbstractActorXml {

    public SetAttributeValueActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String value){
        SetAttributeValueElementaryAction action = new SetAttributeValueElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String value){
        final String previousValue = model.getAttribute(row).getValue();
        ActionPair actionPair = new ActionPair(
                createAction(model, row, value), 
                createAction(model, row, previousValue));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeValueElementaryAction){
            SetAttributeValueElementaryAction setAttributeValueAction = (SetAttributeValueElementaryAction)action;
            act(getNode(setAttributeValueAction.getNode()).getAttributes(), 
                    setAttributeValueAction.getRow(),
                    setAttributeValueAction.getValue());                    
        }
    }

    private void act(NodeAttributeTableModel model, int row, String value) {
        model.getAttribute(row).setValue(value);        
        model.fireTableCellUpdated(row, 1);        
    }

    public Class getDoActionClass() {
        return SetAttributeValueElementaryAction.class;
    }
}
