/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.xml.bind.JAXBException;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.NodeActorXml;
import freemind.controller.actions.generated.instance.BoldNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;


public class BoldAction extends NodeGeneralAction implements NodeActorXml, MenuItemEnabledListener{
	/**
	 * @param textID
	 * @param iconPath
	 * @param actor
	 */
	public BoldAction(ControllerAdapter modeController) {
		super(modeController, "bold", "images/Bold16.gif");
		addActor(this);			
	}

	public void act(XmlAction action) {
		if (action instanceof BoldNodeAction) {
            BoldNodeAction boldact = (BoldNodeAction) action;
            NodeAdapter node = getNodeFromID(boldact.getNode());
            if (node.isBold() != boldact.isBold()) {
                node.setBold(boldact.isBold());
                modeController.nodeChanged(node);
            }
        }
	}


	public Class getDoActionClass() {
		return BoldNodeAction.class;
	}

	public ActionPair apply(MapAdapter model, MindMapNode selected) throws JAXBException {
		// every node is set to the inverse of the focussed node.
		boolean bold = modeController.getSelected().isBold();
		return getActionPair(selected, !bold);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean bold)
		throws JAXBException {
		BoldNodeAction boldAction = toggleBold(selected, bold);
		BoldNodeAction undoBoldAction = toggleBold(selected, selected.isBold());
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

    public boolean isEnabled(JMenuItem item, Action action) {
		setSelected(item, modeController.getSelected().isBold());
        return true;
    }


}