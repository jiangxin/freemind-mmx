/*
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.util.Iterator;
import java.util.List;

import accessories.plugins.dialogs.ChooseFormatPopupDialog;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.main.FreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author adapted to the plugin mechanism by ganzer
 *
 */
public class ApplyFormatPlugin extends MindMapNodeHookAdapter {

	/**
	 * @param node
	 * @param map
	 * @param controller
	 */
	public ApplyFormatPlugin() {
		super();
	}
	
	public void invoke(MindMapNode rootNode) {
		// we dont need node. 
		MindMapNode focussed = getController().getSelected();
		List selected = getController().getSelecteds();
		FreeMind frame = (FreeMind) getController().getFrame();
		ChooseFormatPopupDialog formatDialog =
			new ChooseFormatPopupDialog(
				frame, getMindMapController(), focussed);
		formatDialog.setModal(true);
        formatDialog.pack();
        formatDialog.setVisible(true);
		// process result:
        if (formatDialog.getResult() == ChooseFormatPopupDialog.OK) {
        		Pattern pattern = formatDialog.getPattern();
        		for (Iterator iter = selected.iterator(); iter.hasNext();) {
					MindMapNode node = (MindMapNode) iter.next();
					getMindMapController().applyPattern(node, pattern);
				}
		}
	}


}
