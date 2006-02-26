/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeFontSizeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;

public class SetAttributeFontSizeActor extends AbstractActorXml {

    public SetAttributeFontSizeActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(int size) throws JAXBException{
        SetAttributeFontSizeElementaryAction action = getActionXmlFactory().createSetAttributeFontSizeElementaryAction();
        action.setSize(size);
        return action;
    }
    
    public ActionPair createActionPair(int size) throws JAXBException{
        final int previousSize = getAttributeRegistry().getFontSize();
        ActionPair actionPair = new ActionPair(
                createAction(size), 
                createAction(previousSize));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeFontSizeElementaryAction){
            SetAttributeFontSizeElementaryAction setAttributeFontSizeAction = (SetAttributeFontSizeElementaryAction)action;
            act(setAttributeFontSizeAction.getSize());                    
        }

    }

    private void act(int size) {
       getAttributeRegistry().setFontSize(size);
    }

    public Class getDoActionClass() {
        return SetAttributeFontSizeElementaryAction.class;
    }

}
