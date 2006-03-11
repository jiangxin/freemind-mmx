/*
 * Created on 29.01.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.modes.mindmapmode.actions.xml;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.mindmapmode.MindMapController;

public abstract class AbstractActorXml implements ActorXml {
    private MindMapController mindMapModeController;
    protected AbstractActorXml(MindMapController mindMapModeController){
        this.mindMapModeController = mindMapModeController;
        mindMapModeController.getActionFactory().registerActor(this, getDoActionClass());
    }

    protected NodeAdapter getNode(String nodeID){
        return mindMapModeController.getNodeFromID(nodeID);
    }
    
    protected String getNodeID(MindMapNode node){        
        return mindMapModeController.getNodeID(node);
    }
    
    protected AttributeController getAttributeController(){
        return mindMapModeController.getAttributeController();
    }
    
    protected AttributeRegistry getAttributeRegistry(){
        return mindMapModeController.getMap().getRegistry().getAttributes();
    }
    
    protected CompoundAction createCompoundAction() {
        return new CompoundAction();
    }
    
}
