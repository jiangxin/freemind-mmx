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


/*
 * Created on 14.12.2004
 *
 */
package accessories.plugins;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import freemind.controller.MenuItemEnabledListener;
import freemind.extensions.HookRegistration;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.common.dialogs.EnterPasswordDialog;
import freemind.modes.mindmapmode.EncryptedMindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.NewNodeCreator;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class EncryptNode extends MindMapNodeHookAdapter {
	/**
	 * Enables the encrypt/decrypt menu item only if the map/node is encrypted.
	 * 
	 * @author foltin
	 * 
	 */
	public static class Registration implements HookRegistration,
			MenuItemEnabledListener {

		private final ModeController controller;
		private final MindMap mMap;
		private final java.util.logging.Logger logger;
		private boolean enabled = false;

		public Registration(ModeController controller, MindMap map) {
			this.controller = controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			enabled = true;
		}

		public void deRegister() {
			enabled = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing
		 * .JMenuItem, javax.swing.Action)
		 */
		public boolean isEnabled(JMenuItem item, Action action) {
			String hookName = ((NodeHookAction) action).getHookName();
			// the following function does not work without a running valid
			// controller, so we comment it out.
			// if(hookName.equals("accessories/plugins/NewEncryptedMap.properties"))
			// {
			// return true;
			// }
			if (!enabled)
				return false;
			boolean isEncryptedNode = false;
			boolean isOpened = false;
			if (controller.getSelected() != null
					&& controller.getSelected() instanceof EncryptedMindMapNode) {
				isEncryptedNode = true;
				EncryptedMindMapNode enode = (EncryptedMindMapNode) controller
						.getSelected();
				isOpened = enode.isAccessible();
			}
			if (hookName.equals("accessories/plugins/EnterPassword.properties")) {
				return isEncryptedNode;
			} else {
				/*
				 * you can insert an encrypted node, if the current selected
				 * node is not encrypted, or if it is opened.
				 */
				return (!isEncryptedNode || isOpened);
			}
		}
	}

	/**
     *
     */
	public EncryptNode() {
		super();
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		String actionType = getResourceString("action");
		if (actionType.equals("encrypt")) {
			encrypt(node);
			getController().nodeRefresh(node);
			return;
		} else if (actionType.equals("toggleCryptState")) {
			toggleCryptState(node);
			getController().nodeRefresh(node);
			return;
		} else if (actionType.equals("encrypted_map")) {
			// new map
			newEncryptedMap();
			return;
		} else {
			throw new IllegalArgumentException("Unknown action type:"
					+ actionType);
		}
	}

	/**
     *
     */
	private void newEncryptedMap() {
		final StringBuffer password = getUsersPassword();
		if (password == null) {
			return;
		}
		ModeController newModeController = getMindMapController().getMode()
				.createModeController();
		EncryptedMindMapNode encryptedMindMapNode = new EncryptedMindMapNode(
				getMindMapController().getText(
						"accessories/plugins/EncryptNode.properties_select_me"),
				getMindMapController().getFrame(), null);
		encryptedMindMapNode.setPassword(password);
		MapAdapter newModel = new MindMapMapModel(encryptedMindMapNode,
				getMindMapController().getFrame(), newModeController);
		MindMapController mindmapcontroller = getMindMapController();
		encryptedMindMapNode.setMap(newModel);
		mindmapcontroller.newMap(newModel);
	}

	/**
     */
	private void encrypt(MindMapNode node) {
		final StringBuffer password = getUsersPassword();
		if (password == null) {
			return;
		}
		MindMapController mindmapcontroller = (MindMapController) getMindMapController();
		// FIXME: not multithreading safe
		mindmapcontroller.setNewNodeCreator(new NewNodeCreator() {

			public MindMapNode createNode(Object userObject, MindMap map) {
				EncryptedMindMapNode encryptedMindMapNode = new EncryptedMindMapNode(
						userObject, getMindMapController().getFrame(), map);
				encryptedMindMapNode.setPassword(password);
				return encryptedMindMapNode;
			}
		});
		try {
			MindMapNode newNode = getMindMapController().addNewNode(node, 0,
					node.isLeft());
		} catch (Exception e) {
		}
		// normal value:
		mindmapcontroller.setNewNodeCreator(null);
	}

	/**
     */
	private StringBuffer getUsersPassword() {
		// get password:
		final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
				(JFrame) getMindMapController().getFrame(),
				getMindMapController(), true);
		pwdDialog.setModal(true);
		pwdDialog.show();
		if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
			return null;
		}
		final StringBuffer password = pwdDialog.getPassword();
		return password;
	}

	/**
     */
	private void toggleCryptState(MindMapNode node) {
		final MindMapController mindMapController = getMindMapController();
		if (node instanceof EncryptedMindMapNode) {
			EncryptedMindMapNode encNode = (EncryptedMindMapNode) node;
			if (encNode.isAccessible()) {
				// to remove all children views:
				encNode.encrypt();
				encNode.setShuttingDown(true);
			} else {
				doPasswordCheckAndDecryptNode(encNode);
			}
			mindMapController.nodeStructureChanged(encNode);
			final MapView mapView = mindMapController.getView();
			mapView.selectAsTheOnlyOneSelected(mapView.getNodeView(encNode));
			encNode.setShuttingDown(false);
		} else {
			// box:
			JOptionPane
					.showMessageDialog(
							mindMapController.getFrame().getContentPane(),
							mindMapController
									.getText("accessories/plugins/EncryptNode.properties_insert_encrypted_node_first"),
							"Freemind", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
     */
	private void doPasswordCheckAndDecryptNode(EncryptedMindMapNode encNode) {
		while (true) {
			// get password:
			final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
					(JFrame) getMindMapController().getFrame(),
					getMindMapController(), false);
			pwdDialog.setModal(true);
			pwdDialog.setVisible(true);
			if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
				return;
			}
			if (!encNode.decrypt(pwdDialog.getPassword())) {
				// box:
				JOptionPane
						.showMessageDialog(
								getMindMapController().getFrame()
										.getContentPane(),
								getMindMapController()
										.getText(
												"accessories/plugins/EncryptNode.properties_wrong_password"),
								"Freemind", JOptionPane.ERROR_MESSAGE);
			} else {
				return; // correct password.
			}
		}
	}

}
