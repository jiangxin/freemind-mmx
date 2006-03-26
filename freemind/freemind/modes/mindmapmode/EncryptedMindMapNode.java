/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2001 Joerg Mueller <joergmueller@bigfoot.com> See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/* $Id: EncryptedMindMapNode.java,v 1.1.2.11 2006-01-12 23:10:13 christianfoltin Exp $ */

package freemind.modes.mindmapmode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.ImageIcon;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.main.Tools.TripleDesEncrypter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class EncryptedMindMapNode extends MindMapNodeModel {

    private boolean isVisible = true;

    /**
     * is only set to false by the load mechanism. 
     * If the node is generated or it is decrypted once, this is always true.
     */
    private boolean isDecrypted = true;

    /**
     * password have to be stored in a StringBuffer as Strings cannot be deleted
     * or overwritten.
     */
    private StringBuffer password = null;

    private String encryptedContent;

    private static ImageIcon encryptedIcon;

    private static ImageIcon decryptedIcon;

    private boolean isShuttingDown=false;

    /**
     * @param userObject
     * @param frame
     */
    public EncryptedMindMapNode(Object userObject, FreeMindMain frame) {
        super(userObject, frame);
        if (encryptedIcon == null) {
            encryptedIcon = MindIcon.factory("encrypted").getIcon(frame);
        }
        if (decryptedIcon == null) {
            decryptedIcon = MindIcon.factory("decrypted").getIcon(frame);
        }
        updateIcon();
    }

    /**
     * @param givenPassword
     * @return true, if the password was correct.
     */
    public boolean  decrypt(StringBuffer givenPassword) {
        if (!checkPassword(givenPassword)) {
            return false;
        }
        if (!isDecrypted) {
            String childXml = decryptXml(encryptedContent, password);
            String[] childs = childXml.split(ModeController.NODESEPARATOR);
            // and now? paste it:
            for (int i = childs.length-1; i >=0; i--) {
                String string = childs[i];
                // if the encrypted node is empty, we skip the insert.
                if(string.length() == 0)
                	 continue;
                //FIXME: This code smells:
                ((MindMapController) getFrame().getController()
                        .getModeController()).paste.pasteXMLWithoutRedisplay(
                        string, this, false);

            }
            isDecrypted = true;
        }
        setVisible(true);
        setFolded(false);
        return true;
    }

    /**
     * @param givenPassword
     */
    public boolean checkPassword(StringBuffer givenPassword) {

        if (password != null) {
            if (!equals(givenPassword, password)) {
                logger.warning("Wrong password supplied (cached!=given).");
                return false;
            }
            return true;
        }
        // new password:
        String decryptedNode = decryptXml(encryptedContent, givenPassword);
        // FIXME: Better test needed.
        if (decryptedNode == null || (!decryptedNode.startsWith("<node ") && decryptedNode.length() != 0)) {
            logger.warning("Wrong password supplied (stored!=given).");
            return false;
        }
        this.password = givenPassword;
        return true;
    }

    /**
     * @param givenPassword
     * @param password2
     * @return
     */
    private boolean equals(StringBuffer givenPassword, StringBuffer password2) {
        if (givenPassword.length() != password.length())
            return false;
        for (int i = 0; i < password2.length(); i++) {
            char c1 = password2.charAt(i);
            char c2 = givenPassword.charAt(i);
            if (c1 != c2)
                return false;
        }
        return true;
    }

    public void encrypt() {
        // FIXME: Sync.
        setFolded(true);
        setVisible(false);
    }

    public int getChildCount() {
        if (isVisible()) {
            return super.getChildCount();
        }
        return 0;
    }

    public ListIterator childrenFolded() {
        if (isVisible()) {
            return super.childrenFolded();
        }
        return new Vector().listIterator();
    }

    public ListIterator childrenUnfolded() {
        if (isVisible() || isShuttingDown) {
            return super.childrenUnfolded();
        }
        return new Vector().listIterator();
    }

    public boolean hasChildren() {
        if (isVisible()) {
            return super.hasChildren();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.modes.MindMapNode#getIcons()
     */
    public void updateIcon() {
        setStateIcon("encryptedNode", (isVisible()) ? decryptedIcon : encryptedIcon);
    }

	public void setPassword(StringBuffer password) {
		this.password = password;
	}

    /**
     *  
     */

    public boolean isFolded() {
        if (isVisible()) {
            return super.isFolded();
        }
        return true;
    }

    /**
     *  
     */

    public void setFolded(boolean folded) {
        if (isVisible()) {
            super.setFolded(folded);
        } else {
            super.setFolded(true);
        }
    }

    /**
     *  
     */

    public void setAdditionalInfo(String info) {
        encryptedContent = info;
        setVisible(false);
        isDecrypted = false;
    }

    public String getAdditionalInfo() {
        return encryptedContent;
    }

    /**
     *  
     */

    public XMLElement save(Writer writer, MindMapLinkRegistry registry)
            throws IOException {
        if (isDecrypted) {
            generateEncryptedContent(registry);
        }
        boolean oldIsVisible = isVisible();
        setVisible(false);
        XMLElement ret = null;
        try {
            ret = super.save(writer, registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setVisible(oldIsVisible);
        return ret;
    }

    /**
     * @param registry
     * @throws IOException
     */
    private void generateEncryptedContent(MindMapLinkRegistry registry) throws IOException {
        StringWriter sWriter = new StringWriter();
        for (Iterator i = super.childrenUnfolded(); i.hasNext();) {
            MindMapNode child = (MindMapNode) i.next();
            child.save(sWriter, registry);
            if (i.hasNext()) {
                sWriter.write(ModeController.NODESEPARATOR);
            }
        }
        StringBuffer childXml = sWriter.getBuffer();
        encryptedContent = encryptXml(childXml);
    }

    /**
     * @param childXml
     * @return
     */
    private String encryptXml(StringBuffer childXml) {
        try {
            // Create encrypter/decrypter class
            //FIXME: Use char[] instead of toString.
            TripleDesEncrypter encrypter = new TripleDesEncrypter(password);

            // Encrypt
            String encrypted = encrypter.encrypt(childXml.toString());
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Can't encrypt the node.");
    }

    /**
     * @param encryptedString
     * @return null if the password is wrong.
     */
    private String decryptXml(String encryptedString, StringBuffer pwd) {
        TripleDesEncrypter encrypter = new TripleDesEncrypter(pwd);

        //        // Decrypt
        String decrypted = encrypter.decrypt(encryptedString);

        return decrypted;
    }

    /**isShuttingDown is used to fold an encrypted node properly. 
     * If it is encrypted, it has no children. Thus, the formely existing children can't be removed.
     * Thus, this flag postpones the childlessness of a node until it tree structure is updated.
     * @param isShuttingDown The isShuttingDown to set.
     */
    public void setShuttingDown(boolean isShuttingDown) {
        this.isShuttingDown = isShuttingDown;
    }
 
    /**
     * @param isVisible The isVisible to set.
     */
    private void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        updateIcon();
    }

    /**
     * @return Returns the isVisible.
     */
    public boolean isVisible() {
        return isVisible;
    }
}