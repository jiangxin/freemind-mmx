/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.SetAttributeFontSizeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeFontSizeActor extends AbstractActorXml {

    public SetAttributeFontSizeActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(int size){
        SetAttributeFontSizeElementaryAction action = new SetAttributeFontSizeElementaryAction();
        action.setSize(size);
        return action;
    }
    
    public ActionPair createActionPair(int size){
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
