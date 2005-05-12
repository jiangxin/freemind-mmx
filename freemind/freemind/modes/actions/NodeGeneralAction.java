/*
 * Created on 05.05.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBException;

import freemind.controller.Controller;
import freemind.controller.actions.AbstractXmlAction;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.FreemindAction;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.preferences.FreemindPropertyListener;

public class NodeGeneralAction extends AbstractXmlAction {
    protected final ModeController modeController;

    private freemind.controller.actions.NodeActorXml actor;

    SingleNodeOperation singleNodeOperation;

    protected static Logger logger;

    /**
     * @param modeController
     * @param textID
     *           null if you cannot provide a title that is present in the
     *           resources. Use the setName method to set your not translateble
     *           title after that.
     * @param iconPath
     *           give a resource name for the icon.
     */
    protected NodeGeneralAction(ModeController modeController,
            final String textID, String iconPath) {
        super(null, iconPath != null ? new ImageIcon(modeController
                .getController().getResource(iconPath)) : null, modeController);
        this.modeController = modeController;
        if (textID != null) {
            setName(modeController.getText(textID));
        }

        this.singleNodeOperation = null;
        this.actor = null;
        if (logger == null) {
            logger = modeController.getFrame().getLogger(
                    this.getClass().getName());
        }
    }

    protected void setName(String name) {
        if (name != null) {
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, name);
        }

    }

    public NodeGeneralAction(ModeController modeController, String textID,
            String iconPath, SingleNodeOperation singleNodeOperation) {
        this(modeController, textID, iconPath);
        this.singleNodeOperation = singleNodeOperation;
    }

    public NodeGeneralAction(ModeController modeController, String textID,
            String iconPath, freemind.controller.actions.NodeActorXml actor) {
        this(modeController, textID, iconPath);
        addActor(actor);
    }

    public void addActor(NodeActorXml actor) {
        this.actor = actor;
        if (actor != null) {
            super.addActor(actor);
        }
    }

    /**
     * @param singleNodeOperation
     *           The singleNodeOperation to set.
     */
    public void setSingleNodeOperation(SingleNodeOperation singleNodeOperation) {
        this.singleNodeOperation = singleNodeOperation;
    }

    public void xmlActionPerformed(ActionEvent e) {
        if (singleNodeOperation != null) {
            for (ListIterator it = modeController.getSelecteds().listIterator(); it
                    .hasNext();) {
                MindMapNodeModel selected = (MindMapNodeModel) it.next();
                singleNodeOperation.apply((MindMapMapModel) this.modeController
                        .getMap(), selected);
            }
        } else {
            // xml action:
            try {
                // Do-action
                CompoundAction doAction = modeController.getActionXmlFactory()
                        .createCompoundAction();
                // Undo-action
                CompoundAction undo = modeController.getActionXmlFactory()
                        .createCompoundAction();
                // sort selectedNodes list by depth, in order to guarantee that
                // sons are deleted first:
                for (ListIterator it = modeController.getSelecteds()
                        .listIterator(); it.hasNext();) {
                    MindMapNodeModel selected = (MindMapNodeModel) it.next();
                    ActionPair pair = actor.apply(this.modeController.getMap(),
                            selected);
                    if (pair != null) {
                        doAction
                                .getCompoundActionOrSelectNodeActionOrCutNodeAction()
                                .add(pair.getDoAction());
                        undo
                                .getCompoundActionOrSelectNodeActionOrCutNodeAction()
                                .add(0, pair.getUndoAction());
                    }
                }
                modeController.getActionFactory().startTransaction(
                        (String) getValue(NAME));
                modeController.getActionFactory().executeAction(
                        new ActionPair(doAction, undo));
                modeController.getActionFactory().endTransaction(
                        (String) getValue(NAME));
            } catch (JAXBException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }

    protected void execute(ActionPair pair) {
        modeController.getActionFactory().executeAction(pair);
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.controller.actions.FreeMindAction#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
    }

    /**
     * @param string
     * @return
     */
    protected NodeAdapter getNodeFromID(String string) {
        return modeController.getNodeFromID(string);
    }

    /**
     * @param selected
     * @return
     */
    protected String getNodeID(MindMapNode selected) {
        // TODO Auto-generated method stub
        return modeController.getNodeID(selected);
    }

    /**
     *  
     */
    protected ObjectFactory getActionXmlFactory() {
        // TODO Auto-generated method stub
        return modeController.getActionXmlFactory();
    }

}