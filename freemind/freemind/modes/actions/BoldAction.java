/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.BoldNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;


public class BoldAction extends NodeGeneralAction implements NodeActorXml {
	private final MindMapController modeController;

	/**
	 * @param textID
	 * @param iconPath
	 * @param actor
	 */
	public BoldAction(MindMapController modeController) {
		super(modeController, "bold", "images/Bold24.gif");
		this.modeController = modeController;
		addActor(this);			
	}

	public void act(XmlAction action) {
		System.out.println("BoldActor");
		BoldNodeAction boldact = (BoldNodeAction) action;
		NodeAdapter node = getNodeFromID(boldact.getNode());
		if(node.isBold() != boldact.isBold()) {
			node.setBold(boldact.isBold());
			this.modeController.nodeChanged(node);
		}
	}


	public Class getDoActionClass() {
		return BoldNodeAction.class;
	}

	public ActionPair apply(MapAdapter model, MindMapNodeModel selected) throws JAXBException {

		boolean bold = selected.isBold();
		return getActionPair(selected, bold);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean bold)
		throws JAXBException {
		BoldNodeAction boldAction = toggleBold(selected, !bold);
		BoldNodeAction undoBoldAction = toggleBold(selected, bold);
		return new ActionPair(boldAction, undoBoldAction);
	}

	private BoldNodeAction toggleBold(MindMapNode selected, boolean bold)
		throws JAXBException {
		BoldNodeAction boldAction = getActionXmlFactory().createBoldNodeAction();
		boldAction.setNode(getNodeID(selected));
		boldAction.setBold(bold);
		return boldAction;
	}

	public void setBold(MindMapNode node, boolean  bold) {
		try {
			execute(getActionPair(node, bold));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}


}