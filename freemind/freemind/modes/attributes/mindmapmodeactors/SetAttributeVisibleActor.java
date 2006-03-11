/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.SetAttributeVisibleElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeVisibleActor extends AbstractActorXml {

    public SetAttributeVisibleActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(int index, boolean value){
        SetAttributeVisibleElementaryAction action = new SetAttributeVisibleElementaryAction();
        action.setIndex(index);
        action.setIsVisible(value);
        return action;
    }
    
    public ActionPair createActionPair(int index, boolean value){
        final boolean previousValue = getAttributeRegistry().getElement(index).isVisible();
        ActionPair actionPair = new ActionPair(
                createAction(index, value), 
                createAction(index, previousValue));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeVisibleElementaryAction){
            SetAttributeVisibleElementaryAction setAttributeVisibleAction = (SetAttributeVisibleElementaryAction)action;
            act(setAttributeVisibleAction.getIndex(), setAttributeVisibleAction.getIsVisible());                    
        }

    }

    private void act(int index, boolean value) {
       getAttributeRegistry().getElement(index).setVisibility(value);
    }

    public Class getDoActionClass() {
        return SetAttributeVisibleElementaryAction.class;
    }

}
