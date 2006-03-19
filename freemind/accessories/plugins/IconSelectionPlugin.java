/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.common.dialogs.IconSelectionPopupDialog;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.IconAction;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

/**
 * @author adapted to the plugin mechanism by ganzer
 *
 */
public class IconSelectionPlugin extends MindMapNodeHookAdapter {

	private MindIcon icon;

	/**
	 * @param node
	 * @param map
	 * @param controller
	 */
	public IconSelectionPlugin() {
		super();
	}
	
	public void invoke(MindMapNode rootNode) {
		// we dont need node. 
		MindMapNode focussed = getController().getSelected();
		Vector actions = new Vector();
		Vector items = new Vector();
		Vector itemdescriptions = new Vector();
		MindMapController controller = getMindMapController();
		Vector iconActions = controller.iconActions;
		for (Enumeration e = iconActions.elements(); e.hasMoreElements();) {
			IconAction action =
				((IconAction) e.nextElement());
			addActionToActionVector(actions, items, itemdescriptions, action);
		}
		// And add the remove action, too:
		addActionToActionVector(actions, items, itemdescriptions, controller.removeLastIconAction);
		addActionToActionVector(actions, items, itemdescriptions, controller.removeAllIconsAction);

		FreeMind frame = (FreeMind) getController().getFrame();
		IconSelectionPopupDialog selectionDialog =
			new IconSelectionPopupDialog(
				frame.getJFrame(),
				items,
				itemdescriptions,
				frame);

		NodeView node = focussed.getViewer();
		controller.getView().scrollNodeToVisible(node, 0);
		Tools.moveDialogToPosition(frame, selectionDialog, node.getLocationOnScreen());
		selectionDialog.setModal(true);
		selectionDialog.show();
		// process result:
		int result = selectionDialog.getResult();
        if (result >= 0) {
			Action action = (Action) actions.get(result);
			action.actionPerformed(null);
		}
	}

	/**
     * @param items
     * @param itemdescriptions
     * @param itemdescriptions2
     * @param action
     */
    private void addActionToActionVector(Vector actions, Vector items, Vector itemdescriptions, Action action) {
        actions.add(action);
        items.add(action.getValue(Action.SMALL_ICON));
        itemdescriptions.add(action.getValue(Action.SHORT_DESCRIPTION));
    }

//	/* (non-Javadoc)
//	 * @see freemind.extensions.NodeHook#invoke()
//	 */
//	public void invoke(MindMapNode node) {
//		setNode(node);
//		node.addIcon(icon);
//		nodeChanged(node);
//	}


}
