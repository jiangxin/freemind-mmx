/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.RegistryAttributeElementaryAction;
import freemind.controller.actions.generated.instance.SetAttributeNameElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.NodeAttributeTableModel;

public class RegistryAttributeActor extends AbstractActorXml {

    public RegistryAttributeActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(String name, String value) throws JAXBException{
        RegistryAttributeElementaryAction action = getActionXmlFactory().createRegistryAttributeElementaryAction();
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(String name, String value) throws JAXBException{
        ActionPair actionPair = new ActionPair(
                createAction(name, value), 
                ((MindMapModeAttributeController)getAttributeController()).unregistryAttributeActor.createAction(name)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof RegistryAttributeElementaryAction){
            RegistryAttributeElementaryAction registryAttributeAction = (RegistryAttributeElementaryAction)action;
            act(registryAttributeAction.getName(),
                registryAttributeAction.getValue());                    
        }

    }

    private void act(String name, String value) {
        AttributeRegistry registry = getAttributeRegistry();
        AttributeRegistryElement attributeRegistryElement = new AttributeRegistryElement(registry, name);
        if(value != null){
            attributeRegistryElement.addValue(value);
        }
        int index = registry.getElements().add(name, attributeRegistryElement);
        registry.getTableModel().fireTableRowsInserted(index, index);
    }

    public Class getDoActionClass() {
        return RegistryAttributeElementaryAction.class;
    }

}
