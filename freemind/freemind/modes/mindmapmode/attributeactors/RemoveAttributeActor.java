/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.DeleteAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class RemoveAttributeActor extends AbstractActorXml {

    public RemoveAttributeActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row){
        DeleteAttributeElementaryAction action = new DeleteAttributeElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row){
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
