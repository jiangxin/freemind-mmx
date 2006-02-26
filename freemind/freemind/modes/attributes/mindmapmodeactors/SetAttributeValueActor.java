/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;

public class SetAttributeValueActor extends AbstractActorXml {

    public SetAttributeValueActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String value) throws JAXBException{
        SetAttributeValueElementaryAction action = getActionXmlFactory().createSetAttributeValueElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String value) throws JAXBException{
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
