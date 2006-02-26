/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeNameElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;

public class SetAttributeNameActor extends AbstractActorXml {

    public SetAttributeNameActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String name) throws JAXBException{
        SetAttributeNameElementaryAction action = getActionXmlFactory().createSetAttributeNameElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setName(name);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String name) throws JAXBException{
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
