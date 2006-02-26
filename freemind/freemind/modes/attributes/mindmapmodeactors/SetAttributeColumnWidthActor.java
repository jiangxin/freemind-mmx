/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.SetAttributeColumnWidthElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;

public class SetAttributeColumnWidthActor extends AbstractActorXml {

    public SetAttributeColumnWidthActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int col, int width) throws JAXBException{
        SetAttributeColumnWidthElementaryAction action = getActionXmlFactory().createSetAttributeColumnWidthElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setColumn(col);
        action.setWidth(width);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int col, int width) throws JAXBException{
        final int previousWidth = model.getColumnWidth(col);
        ActionPair actionPair = new ActionPair(
                createAction(model, col, width), 
                createAction(model, col, previousWidth));        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof SetAttributeColumnWidthElementaryAction){
            SetAttributeColumnWidthElementaryAction setAttributeColumnWidthAction = (SetAttributeColumnWidthElementaryAction)action;
            act(getNode(setAttributeColumnWidthAction.getNode()).getAttributes(), 
                    setAttributeColumnWidthAction.getColumn(),
                    setAttributeColumnWidthAction.getWidth());                    
        }

    }

    private void act(NodeAttributeTableModel model, int col, int width) {
       model.getLayout().setColumnWidth(col, width);
    }

    public Class getDoActionClass() {
        return SetAttributeColumnWidthElementaryAction.class;
    }

}
