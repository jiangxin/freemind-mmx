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
/*$Id: BrowseToolBar.java,v 1.6.18.1 2006-01-12 23:10:12 christianfoltin Exp $*/

package freemind.modes.browsemode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

public class BrowseToolBar extends JToolBar {

	public static final String BROWSE_URL_STORAGE_KEY = "browse_url_storage";

	private static class UrlField extends JComboBox {
		private ActionListener actionListener = null;

		private boolean sendExternalEvents = true;

		private final BrowseController mBrowseController;

		public UrlField(BrowseController browseController) {
			this.mBrowseController = browseController;
			setEditable(true);

			addUrl("", false);
			String storedUrls = mBrowseController.getFrame().getProperty(
					BROWSE_URL_STORAGE_KEY);
			if (storedUrls != null) {
				String[] array = storedUrls.split("\t");
				for (int i = 0; i < array.length; i++) {
					String string = array[i];
					addUrl(string, false);
				}
			}
			setSelectedIndex(0);
			super.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addUrl(getText(), false);
					// notification only if a new string is entered.
					if (sendExternalEvents && actionListener != null) {
						actionListener.actionPerformed(arg0);
					}
				}
			});
		}

		public void addActionListener(ActionListener arg0) {
			this.actionListener = arg0;
		}

		private boolean addUrl(String selectedItem, boolean calledFromSetText) {
			// search:
			for (int i = 0; i < getModel().getSize(); i++) {
				String element = (String) getModel().getElementAt(i);
				if (element.equals(selectedItem)) {
					if (calledFromSetText) {
						setSelectedIndex(i);
					}
					return false;
				}
			}
			addItem(selectedItem);
			setSelectedIndex(getModel().getSize() - 1);
			if (calledFromSetText) {
				StringBuffer resultBuffer = new StringBuffer();
				for (int i = 0; i < getModel().getSize(); i++) {
					String element = (String) getModel().getElementAt(i);
					resultBuffer.append(element);
					resultBuffer.append("\t");
				}
				mBrowseController.getFrame().setProperty(
						BROWSE_URL_STORAGE_KEY, resultBuffer.toString());
			}
			return true;
		};

		public String getText() {
			return getSelectedItem().toString();
		}

		public void setText(String text) {
			sendExternalEvents = false;
			addUrl(text, true);
			sendExternalEvents = true;
		}
	}
    private BrowseController c;
    private UrlField urlfield = null;

    public BrowseToolBar(BrowseController controller) {
	
	this.c=controller;
	urlfield = new UrlField(controller);
        this.setRollover(true);

	urlfield.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if("".equals(urlfield.getText()))
				return;
		    try {
                c.load(new URL(urlfield.getText()));
            } catch (Exception e1) {
                e1.printStackTrace();
                //FIXME: Give a good error message.
                c.getController().errorMessage(e1);
            }		    
		}
	    });


	add(new JLabel("URL:"));
	add(urlfield);
    }

    void setURLField (String text) {
	urlfield.setText(text);
    }
}
