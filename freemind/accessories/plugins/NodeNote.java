/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Christian Foltin and others
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

package accessories.plugins;

import java.awt.KeyboardFocusManager;

import javax.swing.JSplitPane;

import freemind.main.FreeMind;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * 
 */
public class NodeNote extends MindMapNodeHookAdapter {

	public final static String NODE_NOTE_PLUGIN = "accessories/plugins/NodeNote.properties";

	public final static String EMPTY_EDITOR_STRING = "<html>\n  <head>\n\n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

	public final static String EMPTY_EDITOR_STRING_ALTERNATIVE = "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";
	public final static String EMPTY_EDITOR_STRING_ALTERNATIVE2 = "<html>\n  <head>\n    \n    \n  </head>\n  <body>\n    <p>\n      \n    </p>\n  </body>\n</html>\n";

	public void startupMapHook() {
		super.startupMapHook();
		String foldingType = getResourceString("command");
		// get registration:
		NodeNoteRegistration registration = (NodeNoteRegistration) this
				.getPluginBaseClass();
		JSplitPane splitPane = null;
		if (foldingType.equals("jump")) {
			// jump to the notes:
			splitPane = getSplitPaneToScreen(registration);
			int oldSize = splitPane.getDividerLocation();
			NodeNoteRegistration.sPositionToRecover = new Integer(oldSize);
			// int maximumDividerLocation =
			// splitPane.getMaximumDividerLocation();
			// if (maximumDividerLocation < oldSize) {
			// openSplitPane(splitPane, maximumDividerLocation);
			// } else {
			// NodeNoteRegistration.sPositionToRecover = null;
			// }
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.clearGlobalFocusOwner();
			requestFocusForNotePanel();
		} else {
			// show hidden window:
			if (registration.getSplitPane() == null) {
				// the window is currently hidden. show it:
				splitPane = getSplitPaneToScreen(registration);
				// openSplitPane(splitPane, maximumDividerLocation);
				requestFocusForNotePanel();
			} else {
				// it is shown, hide it:
				// int newLoc = splitPane.getHeight() -
				// splitPane.getDividerSize();
				// int currentLoc = splitPane.getDividerLocation();
				// splitPane.setDividerLocation(newLoc);
				// splitPane.setLastDividerLocation(currentLoc);
				registration.hideNotesPanel();
				getMindMapController().getFrame().setProperty(
						FreeMind.RESOURCES_USE_SPLIT_PANE, "false");
			}

		}
	}

	protected void requestFocusForNotePanel() {
		NodeNoteRegistration.getHtmlEditorPanel()
				.getMostRecentFocusOwner().requestFocus();
	}

	private JSplitPane getSplitPaneToScreen(NodeNoteRegistration registration) {
		JSplitPane splitPane;
		splitPane = registration.getSplitPane();
		if (splitPane == null) {
			// no split panes are used.
			// jump to the notes:
			registration.showNotesPanel();
			splitPane = registration.getSplitPane();
			getMindMapController().getFrame().setProperty(
					FreeMind.RESOURCES_USE_SPLIT_PANE, "true");
		}
		return splitPane;
	}

	private void openSplitPane(JSplitPane splitPane, int maximumDividerLocation) {
		int newLoc = splitPane.getLastDividerLocation();
		int currentLoc = splitPane.getDividerLocation();
		if (newLoc > maximumDividerLocation) {
			newLoc = maximumDividerLocation;
		}
		splitPane.setDividerLocation(newLoc);
		splitPane.setLastDividerLocation(currentLoc);
	}

}
