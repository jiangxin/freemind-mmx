/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.DeleteAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;

public class RemoveAttributeActor extends AbstractActorXml {

    public RemoveAttributeActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row) throws JAXBException{
        DeleteAttributeElementaryAction action = getActionXmlFactory().createDeleteAttributeElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row) throws JAXBException{
        String name = model.getAttribute(row).getName();
        String value = model.getAttribute(row).getValue();
        ActionPair actionPair = new ActionPair(
                createAction(model, row), 
                ((MindMapModeAttributeController)getAttributeController()).insertAttributeActor.createAction(model, row, name, value)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof DeleteAttributeElementaryAction){
            DeleteAttributeElementaryAction AttributeAction = (DeleteAttributeElementaryAction)action;
            act(getNode(AttributeAction.getNode()).getAttributes(), 
                    AttributeAction.getRow());                    
        }

    }

    private void act(NodeAttributeTableModel model, int row) {
        model.getAttributes().remove(row);
        model.disableStateIcon();
        model.fireTableRowsDeleted(row, row);
    }

    public Class getDoActionClass() {
        return DeleteAttributeElementaryAction.class;
    }

}
