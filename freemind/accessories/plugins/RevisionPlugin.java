/*
 * Created on 5.06.2004
 *
 */
package accessories.plugins;

import java.awt.Color;

import freemind.controller.actions.ActionHandler;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.generated.instance.EditNodeActionType;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;

/**
 * @author foltin
 *
 */
public class RevisionPlugin extends PermanentNodeHookAdapter implements ActionHandler {

	static boolean alreadyUsed = false;

	private Color color;

    /**
	 * 
	 */
	public RevisionPlugin() {
		super();
	}

//	/* (non-Javadoc)
//	 * @see freemind.extensions.PermanentNodeHook#loadFrom(freemind.main.XMLElement)
//	 */
//	public void loadFrom(XMLElement child) {
//		super.loadFrom(child);
//		XMLElement paramChild = (XMLElement) child.getChildren().get(0);
//		if(paramChild != null) {
//			Object obj = paramChild.getAttribute("color", null);
//			String str = (String) obj;
//			color = Tools.xmlToColor(str);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see freemind.extensions.PermanentNodeHook#save(freemind.main.XMLElement)
//	 */
//	public void save(XMLElement xml) {
//		super.save(xml);
//		XMLElement child = new XMLElement();
//		child.setName("Parameters");
//		child.setAttribute("color", Tools.colorToXml(color));
//		xml.addChild(child);
//	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		if(alreadyUsed == false ){
			color = Color.YELLOW;
			// new register: 
			getController().getActionFactory().registerHandler(this);
			alreadyUsed = true;
		}
	}

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActionHandler#executeAction(freemind.controller.actions.ActionPair)
     */
    public void executeAction(ActionPair pair) {
    	XmlAction action = pair.getDoAction();
    	if(action instanceof EditNodeActionType) {
    		// there is an edit action.
			EditNodeActionType editAction = (EditNodeActionType) action;
			NodeAdapter node = getController().getNodeFromID(editAction.getNode());
			node.setBackgroundColor(color);
			nodeChanged(node);
    	}
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActionHandler#startTransaction(java.lang.String)
     */
    public void startTransaction(String name) {
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActionHandler#endTransaction(java.lang.String)
     */
    public void endTransaction(String name) {
    }


}
