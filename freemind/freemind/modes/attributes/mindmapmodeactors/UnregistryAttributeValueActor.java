/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.RegistryAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.SetAttributeNameElementaryAction;
import freemind.controller.actions.generated.instance.UnregistryAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;

public class UnregistryAttributeValueActor extends AbstractActorXml {

    public UnregistryAttributeValueActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(String name, String value) throws JAXBException{
        UnregistryAttributeValueElementaryAction action = getActionXmlFactory().createUnregistryAttributeValueElementaryAction();
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(String name, String value) throws JAXBException{
        ActionPair actionPair = new ActionPair(
                createAction(name, value), 
                ((MindMapModeAttributeController)getAttributeController()).registryAttributeValueActor.createAction(name, value)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof UnregistryAttributeValueElementaryAction){
            UnregistryAttributeValueElementaryAction unrregistryAttributeValueAction = (UnregistryAttributeValueElementaryAction)action;
            act(unrregistryAttributeValueAction.getName(),
                    unrregistryAttributeValueAction.getValue());                    
        }

    }

    private void act(String name, String value) {
        getAttributeRegistry().getElement(name).removeValue(value);
    }

    public Class getDoActionClass() {
        return UnregistryAttributeValueElementaryAction.class;
    }

}
