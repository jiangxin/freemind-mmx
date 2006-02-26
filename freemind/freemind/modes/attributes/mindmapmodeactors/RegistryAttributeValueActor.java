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
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.NodeAttributeTableModel;

public class RegistryAttributeValueActor extends AbstractActorXml {

    public RegistryAttributeValueActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(String name, String value) throws JAXBException{
        RegistryAttributeValueElementaryAction action = getActionXmlFactory().createRegistryAttributeValueElementaryAction();
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(String name, String value) throws JAXBException{
        ActionPair actionPair = new ActionPair(
                createAction(name, value), 
                ((MindMapModeAttributeController)getAttributeController()).unregistryAttributeValueActor.createAction(name, value)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof RegistryAttributeValueElementaryAction){
            RegistryAttributeValueElementaryAction registryAttributeValueAction = (RegistryAttributeValueElementaryAction)action;
            act(registryAttributeValueAction.getName(),
                registryAttributeValueAction.getValue());                    
        }

    }

    private void act(String name, String value) {
        AttributeRegistryElement elem = getAttributeRegistry().getElement(name);
        elem.addValue(value);
    }

    public Class getDoActionClass() {
        return RegistryAttributeValueElementaryAction.class;
    }

}
