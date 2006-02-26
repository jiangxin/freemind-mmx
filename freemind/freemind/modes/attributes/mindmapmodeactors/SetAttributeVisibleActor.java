/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeVisibleElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;

public class SetAttributeVisibleActor extends AbstractActorXml {

    public SetAttributeVisibleActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(int index, boolean value) throws JAXBException{
        SetAttributeVisibleElementaryAction action = getActionXmlFactory().createSetAttributeVisibleElementaryAction();
        action.setIndex(index);
        action.setIsVisible(value);
        return action;
    }
    
    public ActionPair createActionPair(int index, boolean value) throws JAXBException{
        final boolean previousValue = getAttributeRegistry().getElement(index).isVisible();
        ActionPair actionPair = new ActionPair(
                createAction(index, value), 
                createAction(index, previousValue));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeVisibleElementaryAction){
            SetAttributeVisibleElementaryAction setAttributeVisibleAction = (SetAttributeVisibleElementaryAction)action;
            act(setAttributeVisibleAction.getIndex(), setAttributeVisibleAction.isIsVisible());                    
        }

    }

    private void act(int index, boolean value) {
       getAttributeRegistry().getElement(index).setVisibility(value);
    }

    public Class getDoActionClass() {
        return SetAttributeVisibleElementaryAction.class;
    }

}
