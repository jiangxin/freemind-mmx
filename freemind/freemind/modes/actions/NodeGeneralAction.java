/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.AbstractXmlAction;
import freemind.controller.actions.ActionFactory;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;


public class NodeGeneralAction extends AbstractXmlAction {
	private final MindMapController modeController;
	private freemind.controller.actions.NodeActorXml actor;
	SingleNodeOperation singleNodeOperation;
	public NodeGeneralAction(MindMapController modeController, String textID, String iconPath) {
		super(
		modeController.getText(textID),
			iconPath != null ? new ImageIcon(modeController.getResource(iconPath)) : null,
		modeController);
		this.modeController = modeController;
		putValue(Action.SHORT_DESCRIPTION, modeController.getText(textID));
		this.singleNodeOperation = null;
		this.actor = null;
	}
	public NodeGeneralAction(
		MindMapController modeController, String textID,
		String iconPath,
		SingleNodeOperation singleNodeOperation) {
		this(modeController, textID, iconPath);
		this.singleNodeOperation = singleNodeOperation;
	}
	public NodeGeneralAction(
		MindMapController modeController, String textID,
		String iconPath,
		freemind.controller.actions.NodeActorXml actor) {
		this(modeController, textID, iconPath);
		addActor(actor);
	}
	public void addActor(NodeActorXml actor) {
		this.actor = actor;
		if (actor != null) {
			// registration:
			modeController.getActionFactory().registerActor(actor, actor.getDoActionClass());
		}			
	}
	public void xmlActionPerformed(ActionEvent e) {
		if(singleNodeOperation != null) {
			for (ListIterator it = modeController.getSelecteds().listIterator();
				it.hasNext();
				) {
				MindMapNodeModel selected = (MindMapNodeModel) it.next();
				singleNodeOperation.apply((MindMapMapModel) this.modeController.getModel(), selected);
			}
		} else {
			// xml action:
			for (ListIterator it = modeController.getSelecteds().listIterator();
				it.hasNext();
				) {
				MindMapNodeModel selected = (MindMapNodeModel) it.next();
				try {
					ActionPair pair = actor.apply(this.modeController.getModel(), selected);
					modeController.getActionFactory().executeAction(pair);
				} catch (JAXBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	protected void execute(ActionPair pair) {
		modeController.getActionFactory().executeAction(pair);
	}


	/* (non-Javadoc)
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