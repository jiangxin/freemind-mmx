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


package freemind.modes.mindmapmode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;

import freemind.main.FreeMindMain;
import freemind.main.HtmlTools;
import freemind.main.Tools.SingleDesEncrypter;
import freemind.main.XMLElement;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

public class EncryptedMindMapNode extends MindMapNodeModel {

	/**
	 * Is used to hide all children (when false)
	 */
	private boolean isAccessible = true;

	/**
	 * Is used to store the child nodes (when true).
	 */
	private boolean isStoringEncryptedContent = false;

	/**
	 * is only set to false by the load mechanism. If the node is generated or
	 * it is decrypted once, this is always true.
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

	private boolean isShuttingDown = false;

	/**
     */
	public EncryptedMindMapNode(Object userObject, FreeMindMain frame,
			MindMap map) {
		super(userObject, frame, map);
		if (encryptedIcon == null) {
			encryptedIcon = MindIcon.factory("encrypted").getIcon();
		}
		if (decryptedIcon == null) {
			decryptedIcon = MindIcon.factory("decrypted").getIcon();
		}
		if (map != null) {
			updateIcon();
		}
	}

	public void setMap(MindMap map) {
		super.setMap(map);
		updateIcon();
	}

	/**
	 * @return true, if the password was correct.
	 */
	public boolean decrypt(StringBuffer givenPassword) {
		if (!checkPassword(givenPassword)) {
			return false;
		}
		setAccessible(true);
		if (!isDecrypted) {
			try {
				MindMapNode node = null;
				String childXml = decryptXml(encryptedContent, password);
				// is it a map at all?
				if (childXml.startsWith(MindMapMapModel.MAP_INITIAL_START)) {
					node = getNodeFromXml(childXml);
				} else {
					// old handling up to version 0.9.0_rc8:
					String[] childs = childXml
							.split(ModeController.NODESEPARATOR);
					// and now? paste it:
					// make a 0.8.0 map out of it:
					String mapContent = MindMapMapModel.MAP_INITIAL_START
							+ "0.8.0\"><node TEXT=\"DUMMY\">";
					for (int j = 0; j < childs.length; j++) {
						String nodeContent = childs[j];
						mapContent += nodeContent;
					}
					mapContent += "</node></map>";
					node = getNodeFromXml(mapContent);
				}
				int index = 0;
				for (ListIterator i = node.childrenUnfolded(); i.hasNext();) {
					MindMapNodeModel importNode = (MindMapNodeModel) i.next();
					((MindMapController) getModeController()).insertNodeInto(
							importNode, this, index++);
				}
				isDecrypted = true;
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				setAccessible(false);
			}
		}
		setFolded(false);
		getMap().getRegistry().registrySubtree(this, false);
		return true;
	}

	private void paste(MindMapNodeModel importNode) {
		((MindMapController) getModeController()).paste(importNode, this);
	}

	private MindMapNodeModel getNodeFromXml(String childXml) throws IOException {
		// the loadTree method performs an automatical version update.
		MindMapNodeModel node = getMindMapMapModel().loadTree(
				new MindMapMapModel.StringReaderCreator(childXml), false);
		return node;
	}

	private MindMapMapModel getMindMapMapModel() {
		return ((MindMapMapModel) getMap());
	}

	/**
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
		if (decryptedNode == null) {
			logger.warning("Wrong password supplied (deciphered text is null).");
			return false;
		}
		if (!decryptedNode.startsWith("<node ")) {
			// not an encrpyted node in the old format
			// (node,separator,node,...), we test for xml:
			if (!HtmlTools.getInstance().isWellformedXml(decryptedNode)) {
				logger.warning("Wrong password supplied (malformed deciphered text).");
				return false;
			}
		}
		this.password = givenPassword;
		return true;
	}

	/**
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
		setAccessible(false);
	}

	public int getChildCount() {
		if (isAccessible()) {
			return super.getChildCount();
		}
		return 0;
	}

	public ListIterator childrenFolded() {
		if (isAccessible()) {
			return super.childrenFolded();
		}
		return new Vector().listIterator();
	}

	public ListIterator childrenUnfolded() {
		if (isAccessible() || isShuttingDown) {
			return super.childrenUnfolded();
		}
		return new Vector().listIterator();
	}

	public boolean hasChildren() {
		if (isAccessible()) {
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
		if (isAccessible()) {
			setStateIcon("encrypted", null);
			setStateIcon("decrypted", decryptedIcon);
		} else {
			setStateIcon("decrypted", null);
			setStateIcon("encrypted", encryptedIcon);
		}
	}

	public void setPassword(StringBuffer password) {
		this.password = password;
	}

	/**
     *  
     */

	public boolean isFolded() {
		if (isAccessible()) {
			return super.isFolded();
		}
		return true;
	}

	/**
     *  
     */

	public void setFolded(boolean folded) {
		if (isAccessible()) {
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
		setAccessible(false);
		isDecrypted = false;
	}

	public String getAdditionalInfo() {
		if (isStoringEncryptedContent()) {
			return null;
		}
		return encryptedContent;
	}

	/**
     *  
     */

	public XMLElement save(Writer writer, MindMapLinkRegistry registry,
			boolean saveHidden, boolean saveChildren) throws IOException {
		if (isStoringEncryptedContent()) {
			return super.save(writer, registry, saveHidden, saveChildren);
		}
		if (isDecrypted) {
			if (!isAccessible()) {
				throw new IOException(
						"Should store contents of encrypted node "
								+ this.getText()
								+ ", but it is not accessible.");
			}
			setStoringEncryptedContent(true);
			try {
				generateEncryptedContent(registry);
			} finally {
				setStoringEncryptedContent(false);
			}
		}
		boolean oldIsVisible = isAccessible();
		setAccessible(false);
		XMLElement ret = null;
		try {
			ret = super.save(writer, registry, saveHidden, saveChildren);
		} finally {
			setAccessible(oldIsVisible);
		}
		return ret;
	}

	/**
	 * @throws IOException
	 */
	private void generateEncryptedContent(MindMapLinkRegistry registry)
			throws IOException {
		StringWriter sWriter = new StringWriter();
		getMindMapMapModel().getXml(sWriter, true, this);
		StringBuffer childXml = sWriter.getBuffer();
		encryptedContent = encryptXml(childXml);
	}

	/**
     */
	private String encryptXml(StringBuffer childXml) {
		try {
			// Create encrypter/decrypter class
			// FIXME: Use char[] instead of toString.
			SingleDesEncrypter encrypter = new SingleDesEncrypter(password);

			// Encrypt
			String encrypted = encrypter.encrypt(childXml.toString());
			return encrypted;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		throw new IllegalArgumentException("Can't encrypt the node.");
	}

	/**
	 * @return null if the password is wrong.
	 */
	private String decryptXml(String encryptedString, StringBuffer pwd) {
		SingleDesEncrypter encrypter = new SingleDesEncrypter(pwd);

		// // Decrypt
		String decrypted = encrypter.decrypt(encryptedString);

		// fc, only in case, it is needed, we can activate this code.
		// if (decrypted == null || decrypted.isEmpty()) {
		// logger.warning("Perhaps wrong algorithm used (due to a Java bug, in FreeMind 0.8.0 and Java4-5 DES whereas with Java6 Triple DES was used. Trying Triple DES...");
		// decrypted = new
		// Tools.TripleDesEncrypter(pwd).decrypt(encryptedString);
		// }

		return decrypted;
	}

	/**
	 * isShuttingDown is used to fold an encrypted node properly. If it is
	 * encrypted, it has no children. Thus, the formely existing children can't
	 * be removed. Thus, this flag postpones the childlessness of a node until it
	 * tree structure is updated.
	 * 
	 * @param isShuttingDown
	 *            The isShuttingDown to set.
	 */
	public void setShuttingDown(boolean isShuttingDown) {
		this.isShuttingDown = isShuttingDown;
	}

	/**
	 * @param isAccessible
	 *            The isAccessible to set.
	 */
	private void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
		updateIcon();
	}

	/**
	 * @return Returns the isAccessible (ie. if the node is decrypted
	 *         (isAccessible==true) or not).
	 */
	public boolean isAccessible() {
		return isAccessible;
	}

	public void insert(MutableTreeNode pChild, int pIndex) {
		if (isAccessible()) {
			super.insert(pChild, pIndex);
		} else {
			throw new IllegalArgumentException(
					"Trying to insert nodes into a ciphered node.");
		}

	}

	public boolean isWriteable() {
		return isAccessible() && super.isWriteable();
	}

	public boolean isStoringEncryptedContent() {
		return isStoringEncryptedContent;
	}

	public void setStoringEncryptedContent(boolean pIsStoringEncryptedContent) {
		isStoringEncryptedContent = pIsStoringEncryptedContent;
	}

}
