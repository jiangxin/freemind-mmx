/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeRestrictedElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.AttributeRegistry;

public class SetAttributeRestrictedActor extends AbstractActorXml {

    public SetAttributeRestrictedActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(int index, boolean value) throws JAXBException{
        SetAttributeRestrictedElementaryAction action = getActionXmlFactory().createSetAttributeRestrictedElementaryAction();
        action.setIndex(index);
        action.setIsRestricted(value);
        return action;
    }
    
    public ActionPair createActionPair(int index, boolean value) throws JAXBException{
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
            act(setAttributeRestrictedAction.getIndex(), setAttributeRestrictedAction.isIsRestricted());                    
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
