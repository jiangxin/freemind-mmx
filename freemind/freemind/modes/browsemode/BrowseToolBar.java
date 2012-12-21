/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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


package freemind.modes.browsemode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JToolBar;

import freemind.modes.ControllerAdapter;
import freemind.modes.common.dialogs.PersistentEditableComboBox;

public class BrowseToolBar extends JToolBar {

	public static final String BROWSE_URL_STORAGE_KEY = "browse_url_storage";

	private ControllerAdapter c;
	private PersistentEditableComboBox urlfield = null;

	public BrowseToolBar(ControllerAdapter controller) {

		this.c = controller;
		this.setRollover(true);
		this.add(controller.getController().showFilterToolbarAction);
		urlfield = new PersistentEditableComboBox(controller,
				BROWSE_URL_STORAGE_KEY);

		urlfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String urlText = urlfield.getText();
				if ("".equals(urlText)
						|| e.getActionCommand().equals("comboBoxEdited"))
					return;
				try {
					c.load(new URL(urlText));
				} catch (Exception e1) {
					freemind.main.Resources.getInstance().logException(e1);
					// FIXME: Give a good error message.
					c.getController().errorMessage(e1);
				}
			}
		});

		add(new JLabel("URL:"));
		add(urlfield);
	}

	void setURLField(String text) {
		urlfield.setText(text);
	}
}
