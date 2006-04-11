/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.RegistryAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class RegistryAttributeActor extends AbstractActorXml {

    public RegistryAttributeActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(String name){
        RegistryAttributeElementaryAction action = new RegistryAttributeElementaryAction();
        action.setName(name);
        return action;
    }
    
    public ActionPair createActionPair(String name){
        ActionPair actionPair = new ActionPair(
                createAction(name), 
                ((MindMapModeAttributeController)getAttributeController()).unregistryAttributeActor.createAction(name)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof RegistryAttributeElementaryAction){
            RegistryAttributeElementaryAction registryAttributeAction = (RegistryAttributeElementaryAction)action;
            act(registryAttributeAction.getName());                    
        }

    }

    private void act(String name) {
        AttributeRegistry registry = getAttributeRegistry();
        AttributeRegistryElement attributeRegistryElement = new AttributeRegistryElement(registry, name);
        int index = registry.getElements().add(name, attributeRegistryElement);
        registry.getTableModel().fireTableRowsInserted(index, index);
    }

    public Class getDoActionClass() {
        return RegistryAttributeElementaryAction.class;
    }

}
