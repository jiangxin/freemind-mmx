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
/*$Id: EncryptNode.java,v 1.1.2.5 2005-04-08 21:37:30 christianfoltin Exp $*/

/*
 * Created on 14.12.2004
 *
 */
package accessories.plugins;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import accessories.plugins.dialogs.EnterPasswordDialog;
import freemind.controller.MenuItemEnabledListener;
import freemind.extensions.HookRegistration;
import freemind.extensions.NodeHookAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.actions.NodeHookAction;
import freemind.modes.mindmapmode.EncryptedMindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapController.NewNodeCreator;

/**
 * @author foltin
 *  
 */
public class EncryptNode extends NodeHookAdapter {
    /** Enables the encrypt/decrypt menu item only if the map/node is encrypted.
     * @author foltin
     *
     */
    public static class Registration implements HookRegistration, MenuItemEnabledListener {

        private final ModeController controller;
        private final MindMap mMap;
        private final java.util.logging.Logger logger;

        public Registration(ModeController controller, MindMap map) {
            this.controller = controller;
            mMap = map;
            logger = controller.getFrame().getLogger(this.getClass().getName());
        }

		public void register() {
		}

		public void deRegister() {
		}

		/* (non-Javadoc)
		 * @see freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem, javax.swing.Action)
		 */
		public boolean isEnabled(JMenuItem item, Action action) {
			boolean isEncryptedNode = false;
			if(controller.getSelected() != null && controller.getSelected() instanceof EncryptedMindMapNode) {
				isEncryptedNode = true;
			}
			String hookName = ((NodeHookAction)action).getHookName();
			if (hookName.equals("accessories/plugins/EnterPassword.properties")) {
				return isEncryptedNode;
			} else {
				return true;
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
            return;
        } else if(actionType.equals("toggleCryptState")){
            toggleCryptState(node);
            return;
        } else if(actionType.equals("encrypted_map")){
            // new map
            newEncryptedMap();
            return;
        } else {
            throw new IllegalArgumentException("Unknown action type:"+actionType);
        }
    }

    /**
     * 
     */
    private void newEncryptedMap() {
        final StringBuffer password = getUsersPassword();
        if(password == null) {
            return;
        }
        EncryptedMindMapNode encryptedMindMapNode = new EncryptedMindMapNode(
                getController().getText("accessories/plugins/EncryptNode.properties_select_me"), 
                getController().getFrame());
        encryptedMindMapNode.setPassword(password);
        MapAdapter newModel = new MindMapMapModel(encryptedMindMapNode, getController().getFrame());
        MindMapController mindmapcontroller = (MindMapController) getController();
        mindmapcontroller.newMap(newModel);
    }

    /**
     * @param node
     */
    private void encrypt(MindMapNode node) {
        final StringBuffer password = getUsersPassword();
        if(password == null) {
            return;
        }
        MindMapController mindmapcontroller = (MindMapController) getController();
        mindmapcontroller.setNewNodeCreator(new NewNodeCreator() {

            public MindMapNode createNode(Object userObject) {
                EncryptedMindMapNode encryptedMindMapNode = new EncryptedMindMapNode(
                        userObject, getController().getFrame());
                encryptedMindMapNode.setPassword(password);
                return encryptedMindMapNode;
            }
        });
        try {
            MindMapNode newNode = getController().addNewNode(node, 0,
                    node.isLeft());
        } catch (Exception e) {
        }
        // normal value:
        mindmapcontroller.setNewNodeCreator(null);
    }

    /**
     * @return
     */
    private StringBuffer getUsersPassword() {
        // get password:
        final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
                (JFrame) getController().getFrame(), getController(), true);
        pwdDialog.setModal(true);
        pwdDialog.show();
        if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
            return null;
        }
        final StringBuffer password = pwdDialog.getPassword();
        return password;
    }

    /**
     * @param node
     */
    private void toggleCryptState(MindMapNode node) {
        if (node instanceof EncryptedMindMapNode) {
            EncryptedMindMapNode encNode = (EncryptedMindMapNode) node;
            if (encNode.isVisible()) {
                // to remove all children views:
                encNode.encrypt();
                encNode.setShuttingDown(true);
            } else {
                doPasswordCheckAndDecryptNode(encNode);
            }
            getController().nodeStructureChanged(encNode);
            getController().getView().selectAsTheOnlyOneSelected(encNode.getViewer());
            encNode.setShuttingDown(false);
        } else {
            // box:
            JOptionPane
                    .showMessageDialog(
                            getController().getFrame().getContentPane(),
                            getController()
                                    .getText(
                                            "accessories/plugins/EncryptNode.properties_insert_encrypted_node_first"),
                            "Freemind", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * @param encNode
     */
    private void doPasswordCheckAndDecryptNode(EncryptedMindMapNode encNode) {
        while (true) {
            // get password:
            final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
                    (JFrame) getController().getFrame(), getController(), false);
            pwdDialog.setModal(true);
            pwdDialog.show();
            if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
                return;
            }
            if (!encNode.decrypt(pwdDialog.getPassword())) {
                // box:
                JOptionPane
                        .showMessageDialog(
                                getController().getFrame().getContentPane(),
                                getController()
                                        .getText(
                                                "accessories/plugins/EncryptNode.properties_wrong_password"),
                                "Freemind", JOptionPane.ERROR_MESSAGE);
            } else {
                return; // correct password.
            }
        }
    }

}