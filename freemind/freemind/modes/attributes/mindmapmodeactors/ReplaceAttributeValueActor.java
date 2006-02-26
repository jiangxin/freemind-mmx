/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.ReplaceAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.UnregistryAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;

public class ReplaceAttributeValueActor extends AbstractActorXml {

    public ReplaceAttributeValueActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(String name, String oldValue, String newValue) throws JAXBException{
        ReplaceAttributeValueElementaryAction action = getActionXmlFactory().createReplaceAttributeValueElementaryAction();
        action.setName(name);
        action.setOldValue(oldValue);
        action.setNewValue(newValue);
        return action;
    }
    
    public ActionPair createActionPair(String name, String oldValue, String newValue) throws JAXBException{
        ActionPair actionPair = new ActionPair(
                createAction(name, oldValue, newValue), 
                createAction(name, newValue, oldValue)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof ReplaceAttributeValueElementaryAction){
            ReplaceAttributeValueElementaryAction replaceAttributeValueAction = (ReplaceAttributeValueElementaryAction)action;
            act(replaceAttributeValueAction.getName(),
                    replaceAttributeValueAction.getOldValue(),
                    replaceAttributeValueAction.getNewValue());                    
        }

    }

    private void act(String name, String oldValue, String newValue) {
        getAttributeRegistry().getElement(name).replaceValue(oldValue, newValue);
    }

    public Class getDoActionClass() {
        return ReplaceAttributeValueElementaryAction.class;
    }

}
