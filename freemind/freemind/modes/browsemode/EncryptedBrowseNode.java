/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 10.01.2006
 */

package freemind.modes.browsemode;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.swing.ImageIcon;

import freemind.main.FreeMindMain;
import freemind.main.Tools.SingleDesEncrypter;
import freemind.main.XMLParseException;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.common.dialogs.EnterPasswordDialog;

/**
 * @author foltin
 * 
 */
public class EncryptedBrowseNode extends BrowseNodeModel {

	private static ImageIcon encryptedIcon;

	private static ImageIcon decryptedIcon;

	private String encryptedContent;

	private boolean isDecrypted = false;

	// Logging:
	static protected java.util.logging.Logger logger;

	private final ModeController mModeController;

	/**
	 */
	public EncryptedBrowseNode(FreeMindMain frame, ModeController modeController) {
		this(null, frame, modeController);
	}

	/**
	 */
	public EncryptedBrowseNode(Object userObject, FreeMindMain frame,
			ModeController modeController) {
		super(userObject, frame, modeController.getMap());
		this.mModeController = modeController;
		if (logger == null)
			logger = frame.getLogger(this.getClass().getName());
		if (encryptedIcon == null) {
			encryptedIcon = MindIcon.factory("encrypted").getIcon();
		}
		if (decryptedIcon == null) {
			decryptedIcon = MindIcon.factory("decrypted").getIcon();
		}
		updateIcon();
	}

	public void updateIcon() {
		setStateIcon("encryptedNode", (isDecrypted) ? decryptedIcon
				: encryptedIcon);
	}

	public void setFolded(boolean folded) {
		if (isDecrypted || folded) {
			super.setFolded(folded);
			return;
		}
		ControllerAdapter browseController = (ControllerAdapter) mModeController;
		// get password:
		final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(null,
				browseController, false);
		pwdDialog.setModal(true);
		pwdDialog.show();
		if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
			return;
		}
		SingleDesEncrypter encrypter = new SingleDesEncrypter(
				pwdDialog.getPassword());
		// Decrypt
		String decrypted = encrypter.decrypt(encryptedContent);
		if (decrypted == null)
			return;
		HashMap IDToTarget = new HashMap();
		String[] childs = decrypted.split(ModeController.NODESEPARATOR);
		// and now? paste it:
		for (int i = childs.length - 1; i >= 0; i--) {
			String string = childs[i];
			logger.finest("Decrypted '" + string + "'.");
			// if the encrypted node is empty, we skip the insert.
			if (string.length() == 0)
				continue;
			try {
				NodeAdapter node = (NodeAdapter) browseController
						.createNodeTreeFromXml(new StringReader(string),
								IDToTarget);
				// now, the import is finished. We can inform others about
				// the new nodes:
				browseController.insertNodeInto(node, this);
				MapAdapter model = browseController.getModel();
				browseController.invokeHooksRecursively(node, model);
				super.setFolded(folded);
				browseController.nodeChanged(this);
				browseController.nodeStructureChanged(this);
				isDecrypted = true;
				updateIcon();
			} catch (XMLParseException e) {
				freemind.main.Resources.getInstance().logException(e);
				return;
			} catch (IOException e) {
				freemind.main.Resources.getInstance().logException(e);
				return;
			}
		}
	}

	/**
	 *
	 */
	public void setAdditionalInfo(String info) {
		encryptedContent = info;
		isDecrypted = false;
	}

}
