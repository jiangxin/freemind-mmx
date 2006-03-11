/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.SetAttributeRestrictedElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeRestrictedActor extends AbstractActorXml {

    public SetAttributeRestrictedActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(int index, boolean value){
        SetAttributeRestrictedElementaryAction action = new SetAttributeRestrictedElementaryAction();
        action.setIndex(index);
        action.setIsRestricted(value);
        return action;
    }
    
    public ActionPair createActionPair(int index, boolean value){
        final boolean previousValue ;
        if(index == AttributeRegistry.GLOBAL){
            previousValue = getAttributeRegistry().isRestricted();
        }
        else{
            previousValue = getAttributeRegistry().getElement(index).isRestricted();
        }
        ActionPair actionPair = new ActionPair(
                createAction(index, value), 
                createAction(index, previousValue));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeRestrictedElementaryAction){
            SetAttributeRestrictedElementaryAction setAttributeRestrictedAction = (SetAttributeRestrictedElementaryAction)action;
            act(setAttributeRestrictedAction.getIndex(), setAttributeRestrictedAction.getIsRestricted());                    
        }

    }
    
    private void act(int index, boolean value) {
        if(index == AttributeRegistry.GLOBAL){
            getAttributeRegistry().setRestricted(value);            
        }
        else{
            getAttributeRegistry().getElement(index).setRestriction(value);
        }
    }

    public Class getDoActionClass() {
        return SetAttributeRestrictedElementaryAction.class;
    }

}
