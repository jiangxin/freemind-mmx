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
/* $Id: EncryptedMindMapNode.java,v 1.1.2.9.6.4 2005-12-06 19:47:30 dpolivaev Exp $ */

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
import javax.swing.ImageIcon;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.actions.PasteAction;

public class EncryptedMindMapNode extends MindMapNodeModel {

    private boolean isAccessable = true;

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
    public EncryptedMindMapNode(Object userObject, FreeMindMain frame, MindMap map) {
        super(userObject, frame, map);
        if (encryptedIcon == null) {
            encryptedIcon = MindIcon.factory("encrypted").getIcon();
        }
        if (decryptedIcon == null) {
            decryptedIcon = MindIcon.factory("decrypted").getIcon();
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
            String[] childs = childXml.split(PasteAction.NODESEPARATOR);
            // and now? paste it:
            for (int i = childs.length-1; i >=0; i--) {
                String string = childs[i];
                ((ControllerAdapter) getFrame().getController()
                        .getModeController()).paste.pasteXMLWithoutRedisplay(
                        string, this, false);

            }
            isDecrypted = true;
        }
        setAccessable(true);
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
        if (decryptedNode == null || !decryptedNode.startsWith("<node ")) {
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
        setAccessable(false);
    }

    public int getChildCount() {
        if (isAccessable()) {
            return super.getChildCount();
        }
        return 0;
    }

    public ListIterator childrenFolded() {
        if (isAccessable()) {
            return super.childrenFolded();
        }
        return new Vector().listIterator();
    }

    public ListIterator childrenUnfolded() {
        if (isAccessable() || isShuttingDown) {
            return super.childrenUnfolded();
        }
        return new Vector().listIterator();
    }

    public boolean hasChildren() {
        if (isAccessable()) {
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
        setStateIcon("encryptedNode", (isAccessable()) ? decryptedIcon : encryptedIcon);
    }

	public void setPassword(StringBuffer password) {
		this.password = password;
	}

    /**
     *  
     */

    public boolean isFolded() {
        if (isAccessable()) {
            return super.isFolded();
        }
        return true;
    }

    /**
     *  
     */

    public void setFolded(boolean folded) {
        if (isAccessable()) {
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
        setAccessable(false);
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
        boolean oldIsVisible = isAccessable();
        setAccessable(false);
        XMLElement ret = null;
        try {
            ret = super.save(writer, registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAccessable(oldIsVisible);
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
                sWriter.write(PasteAction.NODESEPARATOR);
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
            DesEncrypter encrypter = new DesEncrypter(password);

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
        DesEncrypter encrypter = new DesEncrypter(pwd);

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
    private void setAccessable(boolean isAccessable) {
        this.isAccessable = isAccessable;
        updateIcon();
    }

    /**
     * @return Returns the isVisible.
     */
    public boolean isAccessable() {
        return isAccessable;
    }

    // from: http://javaalmanac.com/egs/javax.crypto/PassKey.html
    public class DesEncrypter {
        private static final String SALT_PRESENT_INDICATOR = " ";
        private static final int SALT_LENGTH=8;
        
        Cipher ecipher;

        Cipher dcipher;

        // 8-byte default Salt
        byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
                (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

        // Iteration count
        int iterationCount = 19;

		private final char[] passPhrase;

        DesEncrypter(StringBuffer pPassPhrase) {
        		passPhrase = new char[pPassPhrase.length()];
        		pPassPhrase.getChars(0, passPhrase.length, passPhrase, 0);
        }

        /**
		 * @param mSalt
		 */
		private void init(byte[] mSalt) {
            if(mSalt!=null) {
            		this.salt = mSalt;
            }
			if (ecipher==null) {
				try {
					// Create the key
					KeySpec keySpec = new PBEKeySpec(passPhrase,
							salt, iterationCount);
					SecretKey key = SecretKeyFactory.getInstance(
							"PBEWithMD5AndTripleDES").generateSecret(keySpec);
					ecipher = Cipher.getInstance(key.getAlgorithm());
					dcipher = Cipher.getInstance(key.getAlgorithm());

					// Prepare the parameter to the ciphers
					AlgorithmParameterSpec paramSpec = new PBEParameterSpec(
							salt, iterationCount);

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
		}

		public String encrypt(String str) {
            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");
                // determine salt by random:
                byte[] newSalt = new byte[SALT_LENGTH];
                for (int i = 0; i < newSalt.length; i++) {
                    newSalt[i] = (byte)(Math.random()*256l-128l);
                }

				init(newSalt);
                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);

                // Encode bytes to base64 to get a string
                return Tools.toBase64(newSalt)
                        + SALT_PRESENT_INDICATOR
                        + Tools.toBase64(enc);
            } catch (javax.crypto.BadPaddingException e) {
            } catch (IllegalBlockSizeException e) {
            } catch (UnsupportedEncodingException e) {
            } catch (java.io.IOException e) {
            }
            return null;
        }


        public String decrypt(String str) {
            if(str == null) {
                return null;
            }
            try {
                byte[] salt = null;
                // test if salt exists:
                int indexOfSaltIndicator = str.indexOf(SALT_PRESENT_INDICATOR);
                if(indexOfSaltIndicator>=0) {
                    String saltString = str.substring(0, indexOfSaltIndicator);
                    str = str.substring(indexOfSaltIndicator+1);
                    salt = Tools.fromBase64(saltString);
                }
                // Decode base64 to get bytes
                byte[] dec = Tools.fromBase64(str);
				init(salt);
				               
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
}