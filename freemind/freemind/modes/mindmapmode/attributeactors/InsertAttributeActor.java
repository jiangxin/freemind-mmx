/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.InsertAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class InsertAttributeActor extends AbstractActorXml {

    public InsertAttributeActor(MindMapController mindMapModeController) {
        super(mindMapModeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String name, String value){
        InsertAttributeElementaryAction action = new InsertAttributeElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String name, String value){
        ActionPair actionPair = new ActionPair(
                createAction(model, row, name, value), 
                ((MindMapModeAttributeController)getAttributeController()).removeAttributeActor.createAction(model, row)
                );        
        return actionPair;
    }
    
    public void act(XmlAction action) {
        if(action instanceof InsertAttributeElementaryAction){
            InsertAttributeElementaryAction insertAttributeAction = (InsertAttributeElementaryAction)action;
            act(getNode(insertAttributeAction.getNode()).getAttributes(), 
                    insertAttributeAction.getRow(),
                    insertAttributeAction.getName(),
                    insertAttributeAction.getValue());                    
        }

    }

    private void act(NodeAttributeTableModel model, int row, String name, String value) {
        Attribute newAttribute = new Attribute(name, value);
        model.getAttributes().add(row, newAttribute);
        model.enableStateIcon();
        model.fireTableRowsInserted(row, row);
    }

    public Class getDoActionClass() {
        return InsertAttributeElementaryAction.class;
    }

}
