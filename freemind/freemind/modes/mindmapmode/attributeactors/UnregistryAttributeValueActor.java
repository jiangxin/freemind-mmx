/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.UnregistryAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class UnregistryAttributeValueActor extends AbstractActorXml {

    public UnregistryAttributeValueActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(String name, String value){
        UnregistryAttributeValueElementaryAction action = new UnregistryAttributeValueElementaryAction();
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(String name, String value){
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
