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
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.common.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import freemind.modes.ModeController;

// //////////
// Actions
// /////////

public class NewMapAction extends AbstractAction {
	private final ModeController modeController;

	public NewMapAction(ModeController modeController) {
		super(modeController.getText("new"), new ImageIcon(
				modeController.getResource("images/filenew.png")));
		this.modeController = modeController;
		// Workaround to get the images loaded in jar file.
		// they have to be added to jar manually with full path from root
		// I really don't like this, but it's a bug of java
	}

	public void actionPerformed(ActionEvent e) {
		modeController.newMap();
	}
}