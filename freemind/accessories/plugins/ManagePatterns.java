/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: ManagePatterns.java,v 1.1.2.4.2.1 2008/01/30 20:44:47 christianfoltin Exp $*/

package accessories.plugins;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import accessories.plugins.dialogs.ChooseFormatPopupDialog;
import accessories.plugins.dialogs.ManagePatternsPopupDialog;
import freemind.main.FreeMind;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;

/** */
public class ManagePatterns extends MindMapHookAdapter {

	/**
	 * 
	 */
	public ManagePatterns() {
		super();

	}

	public void startupMapHook() {
		super.startupMapHook();
		// start dialog:
		FreeMind frame = (FreeMind) getController().getFrame();
		ManagePatternsPopupDialog formatDialog = new ManagePatternsPopupDialog(
				frame.getJFrame(), getMindMapController());
		// formatDialog.pack();
		formatDialog.setModal(true);
		formatDialog.setVisible(true);
		// process result:
		if (formatDialog.getResult() == ChooseFormatPopupDialog.OK) {
			try {
				// Save patterns in private pattern list:
				File patternFile = getController().getFrame().getPatternsFile();
				StylePatternFactory.savePatterns(new FileWriter(patternFile),
						formatDialog.getPatternList());
				getMindMapController().loadPatterns(
						getMindMapController().getPatternReader());
				// TODO: seems to be a bad hack:
				getMindMapController().getFrame().getFreeMindMenuBar()
						.updateMenus(getMindMapController());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
			}
		}
	}
}
