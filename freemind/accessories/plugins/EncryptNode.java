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
/*$Id: EncryptNode.java,v 1.1.2.1 2004-12-19 09:00:32 christianfoltin Exp $*/

/*
 * Created on 14.12.2004
 *
 */
package accessories.plugins;

import javax.swing.JFrame;

import accessories.plugins.dialogs.EnterPasswordDialog;
import freemind.extensions.NodeHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.EncryptedMindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapController.NewNodeCreator;

/**
 * @author foltin
 *  
 */
public class EncryptNode extends NodeHookAdapter {

    /**
     *  
     */
    public EncryptNode() {
        super();
    }

    public void invoke(MindMapNode node) {
        super.invoke(node);
        String foldingType = getResourceString("action");
        if (foldingType.equals("encrypt")) {
            encrypt(node);
        } else {
            decrypt(node);
        }
    }

    /**
     * @param node
     */
    private void encrypt(MindMapNode node) {
        // get password:
        final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
                (JFrame) getController().getFrame(), getController(), true);
        pwdDialog.setModal(true);
        pwdDialog.show();
        if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
            return;
        }
        MindMapController mindmapcontroller = (MindMapController) getController();
        mindmapcontroller.setNewNodeCreator(new NewNodeCreator() {

            public MindMapNode createNode(Object userObject) {
                EncryptedMindMapNode encryptedMindMapNode = new EncryptedMindMapNode(
                        userObject, getController().getFrame());
                encryptedMindMapNode.setPassword(pwdDialog.getPassword());
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
     * @param node
     */
    private void decrypt(MindMapNode node) {
        if (node instanceof EncryptedMindMapNode) {
            EncryptedMindMapNode encNode = (EncryptedMindMapNode) node;
            if (encNode.isVisible()) {
                getController().setFolded(node, true);
                encNode.encrypt();
            } else {
                doPasswordCheckAndDecryptNode(encNode);
                getController().setFolded(node, true);
                getController().setFolded(node, false);
            }
            getController().nodeChanged(node);
        }
    }

    /**
     * @param encNode
     */
    private void doPasswordCheckAndDecryptNode(EncryptedMindMapNode encNode) {
        // get password:
        final EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
                (JFrame) getController().getFrame(), getController(), false);
        pwdDialog.setModal(true);
        pwdDialog.show();
        if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
            return;
        }
        encNode.decrypt(pwdDialog.getPassword());
    }

}