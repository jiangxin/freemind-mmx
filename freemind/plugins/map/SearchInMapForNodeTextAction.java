/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package plugins.map;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.common.TextTranslator;
import freemind.main.FreeMind;
import freemind.main.HtmlTools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 1.4.2012
 */
public class SearchInMapForNodeTextAction extends MindMapNodeHookAdapter {

	/**
	 * 
	 */
	private static final String REALLY_SEARCH_FOR_NODE_TEXT_IN_WEB = "really_search_for_node_text_in_web";
	static final String NODE_CONTEXT_PLUGIN_NAME = "plugins/map/MapDialog_SearchInMapForNodeTextAction.properties";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		// generate string to search for
		StringBuffer sb = new StringBuffer();

		List selecteds = getMindMapController().getSelecteds();
		for (Iterator it = selecteds.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			// Convert to plain text
			final String plainText = HtmlTools.htmlToPlain(node.getText());
			if(sb.length()!=0) {
				sb.append(" ");
			}
			sb.append(plainText);
		}
		final String searchText = sb.toString();
		// First of all: ask user:
		int showResult = new OptionalDontShowMeAgainDialog(
				getMindMapController().getFrame().getJFrame(),
				getMindMapController().getSelectedView(),
				REALLY_SEARCH_FOR_NODE_TEXT_IN_WEB,
				"confirmation",
				new TextTranslator() {

					public String getText(String pKey) {
						String text = getMindMapController()
								.getText(pKey);
						if (pKey.equals(REALLY_SEARCH_FOR_NODE_TEXT_IN_WEB)) {
							Object[] messageArguments = { searchText };
							MessageFormat formatter = new MessageFormat(text);
							text = formatter.format(messageArguments);
						}
						return text;
					}
				},
				new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
						getMindMapController().getController(),
						FreeMind.RESOURCES_SEARCH_FOR_NODE_TEXT_WITHOUT_QUESTION),
				OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
				.show().getResult();
		if (showResult != JOptionPane.OK_OPTION) {
			return;
		}

		// is the map open? Ask base class.
		Registration registration = getRegistration();
		if (registration != null) {
			// is the map open?
			MapDialog mapDialog = registration.getMapDialog();
			if (mapDialog == null) {
				// if not, open it!
				getMindMapController().createModeControllerHook(
						MapDialog.MAP_HOOK_NAME);
			}
			mapDialog = registration.getMapDialog();
			if (mapDialog != null) {
				mapDialog.setSingleSearch();
				mapDialog.search(sb.toString(), true);
			} else {
				logger.warning("Can't find dialog to connect to!");
			}
		} else {
			logger.warning("Can't find registration base class!");

		}
	}

	public Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

}
