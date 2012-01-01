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

package freemind.modes.mindmapmode.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import freemind.main.HtmlTools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * @date 18.10.2011
 */
public class PasteAsPlainTextAction extends AbstractAction {

	private MindMapController mMindMapController;
	private static Logger logger;

	public PasteAsPlainTextAction(MindMapController pMindMapController) {
		super(pMindMapController.getText("paste_as_plain_text"), null);
		this.mMindMapController = pMindMapController;
		if (logger == null) {
			logger = mMindMapController.getFrame().getLogger(
					this.getClass().getName());
		}

		setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent pArg0) {
		Transferable clipboardContents = mMindMapController
				.getClipboardContents();
		// test for plain text support
		if (clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				String plainText = (String) clipboardContents
						.getTransferData(DataFlavor.stringFlavor);
				// sometimes these (for XML illegal) characters occur
				plainText = HtmlTools.makeValidXml(plainText);
				logger.info("Pasting string " + plainText);
				// paste.
				MindMapNode selected = mMindMapController.getSelected();
				MindMapNode newNode = mMindMapController.addNewNode(selected,
						selected.getChildCount(), selected.isLeft());
				mMindMapController.setNodeText(newNode, plainText);
			} catch (UnsupportedFlavorException e) {
				freemind.main.Resources.getInstance().logException(e);

			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);

			}
		} else {
			// not supported message.
			logger.warning("String flavor not supported for transferable "
					+ clipboardContents);
		}
	}

}
