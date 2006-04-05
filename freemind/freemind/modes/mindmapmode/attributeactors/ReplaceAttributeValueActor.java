/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.ReplaceAttributeValueElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class ReplaceAttributeValueActor extends AbstractActorXml {

    public ReplaceAttributeValueActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(String name, String oldValue, String newValue){
        ReplaceAttributeValueElementaryAction action = new ReplaceAttributeValueElementaryAction();
        action.setName(name);
        action.setOldValue(oldValue);
        action.setNewValue(newValue);
        return action;
    }
    
    public ActionPair createActionPair(String name, String oldValue, String newValue){
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
