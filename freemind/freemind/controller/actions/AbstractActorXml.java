/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.controller.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;

public abstract class AbstractActorXml implements ActorXml {
    private ModeController modeController;
    protected AbstractActorXml(ModeController modeController){
        this.modeController = modeController;
        modeController.getActionFactory().registerActor(this, getDoActionClass());
    }

    protected ObjectFactory getActionXmlFactory() {
        return this.modeController.getActionXmlFactory();
    }
    
    protected NodeAdapter getNode(String nodeID){
        return modeController.getNodeFromID(nodeID);
    }
    
    protected String getNodeID(MindMapNode node){        
        return modeController.getNodeID(node);
    }
    
    protected AttributeController getAttributeController(){
        return modeController.getAttributeController();
    }
    
    protected AttributeRegistry getAttributeRegistry(){
        return modeController.getMap().getRegistry().getAttributes();
    }
    
    protected CompoundAction createCompoundAction() throws JAXBException{
        return getActionXmlFactory().createCompoundAction();
    }
    
}
