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
/* $Id: EncryptedMindMapNode.java,v 1.1.2.1 2004-12-19 09:00:40 christianfoltin Exp $ */

package freemind.modes.mindmapmode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.actions.PasteAction;

public class EncryptedMindMapNode extends MindMapNodeModel {

    private boolean isVisible = true;

    private boolean isDecrypted = true;

    /**
     * password have to be stored in a StringBuffer as Strings cannot be deleted
     * or overwritten.
     */
    private StringBuffer password = null;

    private String encryptedContent;

    private boolean isNodeCurrentlySaved = false;

    private static MindIcon encryptedIcon;

    private static MindIcon decryptedIcon;

    /**
     * @param userObject
     * @param frame
     */
    public EncryptedMindMapNode(Object userObject, FreeMindMain frame) {
        super(userObject, frame);
        if (encryptedIcon == null) {
            encryptedIcon = new MindIcon("encrypted");
        }
        if (decryptedIcon == null) {
            decryptedIcon = new MindIcon("decrypted");
        }

    }

    public void decrypt(StringBuffer givenPassword) {
        if (!checkPassword(givenPassword)) {
            return;
        }
        if (!isDecrypted) {
            String childXml = decryptXml(encryptedContent, password);
            String[] childs = childXml.split(PasteAction.NODESEPARATOR);
            // and now? paste it:
            for (int i = 0; i < childs.length; i++) {
                String string = childs[i];
                ((ControllerAdapter) getFrame().getController()
                        .getModeController()).paste.pasteXMLWithoutRedisplay(
                        string, this, false);

            }
            isDecrypted = true;
        }
        isVisible = true;
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
        if (!decryptedNode.startsWith("<node ")) {
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
        isVisible = false;
    }

    public int getChildCount() {
        if (isVisible) {
            return super.getChildCount();
        }
        return 0;
    }

    public ListIterator childrenFolded() {
        if (isVisible) {
            return super.childrenFolded();
        }
        return new Vector().listIterator();
    }

    public ListIterator childrenUnfolded() {
        if (isVisible) {
            return super.childrenUnfolded();
        }
        return new Vector().listIterator();
    }

    public boolean hasChildren() {
        if (isVisible) {
            return super.hasChildren();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see freemind.modes.MindMapNode#getIcons()
     */
    public Vector getIcons() {
        Vector ret = new Vector();
        ret.addAll(super.getIcons());
        // the icon should not be saved.
        if (!isNodeCurrentlySaved) {
            ret.add(0, (isVisible) ? decryptedIcon : encryptedIcon);
        }
        return ret;
    }

    /**
     * @return Returns the isDecrpyted.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     *  
     */

    public boolean isFolded() {
        if (isVisible) {
            return super.isFolded();
        }
        return false;
    }

    /**
     *  
     */

    public void setFolded(boolean folded) {
        if (isVisible) {
            super.setFolded(folded);
        }
    }

    /**
     *  
     */

    public void setAdditionalInfo(String info) {
        encryptedContent = info;
        isVisible = false;
        isDecrypted = false;
    }

    public String getAdditionalInfo() {
        return encryptedContent;
    }

    public boolean isNodeClassToBeSaved() {
        return true;
    }

    /**
     *  
     */

    public XMLElement save(Writer writer, MindMapLinkRegistry registry)
            throws IOException {
        if (isDecrypted) {
            StringWriter sWriter = new StringWriter();
            for (Iterator i = childrenUnfolded(); i.hasNext();) {
                MindMapNode child = (MindMapNode) i.next();
                child.save(sWriter, registry);
                if (i.hasNext()) {
                    sWriter.write(PasteAction.NODESEPARATOR);
                }
            }
            StringBuffer childXml = sWriter.getBuffer();
            encryptedContent = encryptXml(childXml);
        }
        isNodeCurrentlySaved = true;
        boolean oldIsVisible = isVisible;
        isVisible = false;
        XMLElement ret = null;
        try {
            ret = super.save(writer, registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isNodeCurrentlySaved = false;
        isVisible = oldIsVisible;
        return ret;
    }

    /**
     * @param childXml
     * @return
     */
    private String encryptXml(StringBuffer childXml) {
        // Here is an example that uses the class
        try {
            // Create encrypter/decrypter class
            //FIXME: Use char[] instead of toString.
            DesEncrypter encrypter = new DesEncrypter(password.toString());

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
     * @return
     */
    private String decryptXml(String encryptedString, StringBuffer pwd) {
        // Create encrypter/decrypter class
        DesEncrypter encrypter = new DesEncrypter(pwd.toString());

        //        // Decrypt
        String decrypted = encrypter.decrypt(encryptedString);

        return decrypted;
    }

    // from: http://javaalmanac.com/egs/javax.crypto/PassKey.html
    public class DesEncrypter {
        Cipher ecipher;

        Cipher dcipher;

        // 8-byte Salt
        byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

        // Iteration count
        int iterationCount = 19;

        DesEncrypter(String passPhrase) {
            try {
                // Create the key
                KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(),
                        salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance(
                        "PBEWithMD5AndTripleDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());

                // Prepare the parameter to the ciphers
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,
                        iterationCount);

                // Create the ciphers
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (java.security.InvalidAlgorithmParameterException e) {
            } catch (java.security.spec.InvalidKeySpecException e) {
            } catch (javax.crypto.NoSuchPaddingException e) {
            } catch (java.security.NoSuchAlgorithmException e) {
            } catch (java.security.InvalidKeyException e) {
            }
        }

        public String encrypt(String str) {
            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");

                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);

                // Encode bytes to base64 to get a string
                return new sun.misc.BASE64Encoder().encode(enc);
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }

        public String decrypt(String str) {
            try {
                // Decode base64 to get bytes
                byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

                // Decrypt
                byte[] utf8 = dcipher.doFinal(dec);

                // Decode using utf-8
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(StringBuffer password) {
        this.password = password;
    }
}