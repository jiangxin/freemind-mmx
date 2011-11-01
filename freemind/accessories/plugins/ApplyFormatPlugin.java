/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
import freemind.modes.MindMapNode;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author adapted to the plugin mechanism by ganzer
 * 
 */
public class ApplyFormatPlugin extends MindMapNodeHookAdapter {

	/**
	 */
	public ApplyFormatPlugin() {
		super();
	}

	public void invoke(MindMapNode rootNode) {
		// we dont need node.
		MindMapNode focussed = getController().getSelected();
		List selected = getController().getSelecteds();
		Pattern nodePattern = StylePatternFactory.createPatternFromSelected(
				focussed, selected);
		ChooseFormatPopupDialog formatDialog = new ChooseFormatPopupDialog(
				getController().getFrame().getJFrame(), getMindMapController(),
				"accessories/plugins/ApplyFormatPlugin.dialog.title",
				nodePattern);
		formatDialog.setModal(true);
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
