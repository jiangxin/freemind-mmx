/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.InsertAttributeElementaryAction;
import freemind.controller.actions.generated.instance.SetAttributeNameElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ModeController;
import freemind.modes.attributes.Attribute;
import freemind.modes.attributes.NodeAttributeTableModel;

public class InsertAttributeActor extends AbstractActorXml {

    public InsertAttributeActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(
            NodeAttributeTableModel model, int row, String name, String value) throws JAXBException{
        InsertAttributeElementaryAction action = getActionXmlFactory().createInsertAttributeElementaryAction();
        action.setNode(getNodeID(model.getNode()));
        action.setRow(row);
        action.setName(name);
        action.setValue(value);
        return action;
    }
    
    public ActionPair createActionPair(
            NodeAttributeTableModel model, int row, String name, String value) throws JAXBException{
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
