/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.attributes.mindmapmodeactors;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractActorXml;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.UnregistryAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.controller.filter.util.SortedMapVector;
import freemind.modes.ModeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeRegistryElement;
import freemind.modes.attributes.NodeAttributeTableModel;

public class UnregistryAttributeActor extends AbstractActorXml {

    public UnregistryAttributeActor(ModeController modeController) {
        super(modeController);
    }
    
    public XmlAction createAction(String name) throws JAXBException{
        UnregistryAttributeElementaryAction action = getActionXmlFactory().createUnregistryAttributeElementaryAction();
        action.setName(name);
        return action;
    }
    
    public ActionPair createActionPair(String name) throws JAXBException{
        ActionPair actionPair = new ActionPair(
                createAction(name), 
                createUndoAction(name)
                );        
        return actionPair;
    }
    
    private XmlAction createUndoAction(String name) throws JAXBException {
        final CompoundAction compoundAction = createCompoundAction();
        final SortedComboBoxModel values = getAttributeRegistry().getElement(name).getValues();
        String firstValue = values.getElementAt(0).toString();
        final XmlAction firstAction = ((MindMapModeAttributeController)getAttributeController()).registryAttributeActor.createAction(name, firstValue);        
        compoundAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(firstAction);
        for(int i = 1; i < values.getSize(); i++){
            String value = values.getElementAt(i).toString();
            final XmlAction nextAction = ((MindMapModeAttributeController)getAttributeController()).registryAttributeActor.createAction(name, value);
            compoundAction.getCompoundActionOrSelectNodeActionOrCutNodeAction().add(nextAction);            
        }
        return compoundAction;
    }

    public void act(XmlAction action) {
        if(action instanceof UnregistryAttributeElementaryAction){
            UnregistryAttributeElementaryAction unregistryAttributeElementaryAction = (UnregistryAttributeElementaryAction)action;
            act(unregistryAttributeElementaryAction.getName());                    
        }

    }

    private void act(String name) {
        final AttributeRegistry registry = getAttributeRegistry();
        registry.unregistry(name);
    }

    public Class getDoActionClass() {
        return UnregistryAttributeElementaryAction.class;
    }

}
