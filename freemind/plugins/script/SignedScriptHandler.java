/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008 Christian Foltin and others.
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
 * Created on 16.04.2008
 */
/*$Id: SignedScriptHandler.java,v 1.1.2.2 2008/04/18 21:18:27 christianfoltin Exp $*/

package plugins.script;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemind.common.TextTranslator;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.common.dialogs.EnterPasswordDialog;

/**
 * @author foltin
 * 
 */
public class SignedScriptHandler {

	public static final String FREEMIND_SCRIPT_KEY_NAME = "FreeMindScriptKey";
	private static final String SIGN_PREFIX = "//SIGN:";
	/** This is for / /SIGN(keyname):signature */
	private static final String SIGN_PREFIX_REGEXP = "//SIGN\\((.*?)\\):(.*)";

	public static class ScriptContents {
		public String mScript;
		public String mSignature;
		public String mKeyName;
		private static Pattern sSignWithKeyPattern = null;

		public ScriptContents() {
			if (sSignWithKeyPattern == null)
				sSignWithKeyPattern = Pattern.compile(SIGN_PREFIX_REGEXP);
		}

		public ScriptContents(String pScript) {
			this();
			int indexOfSignaturePrefix = pScript.lastIndexOf(SIGN_PREFIX);
			int indexOfSignature = indexOfSignaturePrefix
					+ SIGN_PREFIX.length();
			if (indexOfSignaturePrefix > 0
					&& pScript.length() > indexOfSignature) {
				mSignature = pScript.substring(indexOfSignature);
				mScript = pScript.substring(0, indexOfSignaturePrefix);
				mKeyName = null;
			} else {
				Matcher matcher = sSignWithKeyPattern.matcher(pScript);
				if (matcher.find()) {
					mScript = pScript.substring(0, matcher.start());
					mKeyName = matcher.group(1);
					mSignature = matcher.group(2);
				} else {
					mSignature = null;
					mScript = pScript;
					mKeyName = null;
				}
			}
		}

		public String toString() {
			String prefix;
			if (mKeyName != null)
				prefix = "//SIGN(" + mKeyName + "):";
			else
				prefix = SIGN_PREFIX;
			return mScript + prefix + mSignature + "\n";
		}
	}

	private static KeyStore mKeyStore = null;

	public SignedScriptHandler() {
	}

	private void initializeKeystore(char[] pPassword) {
		if (mKeyStore != null)
			return;
		java.io.FileInputStream fis = null;
		try {
			mKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());

			fis = new java.io.FileInputStream(System.getProperty("user.home")
					+ File.separator + ".keystore");
			mKeyStore.load(fis, pPassword);
		} catch (Exception e) {
			Resources.getInstance().logException(e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Resources.getInstance().logException(e);
				}
			}
		}
	}

	public String signScript(String pScript, TextTranslator pTranslator,
			FreeMindMain pFrame) {
		ScriptContents content = new ScriptContents(pScript);
		// it is assumed, that keystore and key password are identical.
		EnterPasswordDialog pwdDialog = new EnterPasswordDialog(
				pFrame.getJFrame(), pTranslator, false);
		pwdDialog.setModal(true);
		pwdDialog.setVisible(true);
		if (pwdDialog.getResult() == EnterPasswordDialog.CANCEL) {
			return content.mScript;
		}
		char[] password = pwdDialog.getPassword().toString().toCharArray();
		initializeKeystore(password);
		try {
			Signature instance = Signature.getInstance("SHA1withDSA");
			String keyName = FREEMIND_SCRIPT_KEY_NAME;
			String propertyKeyName = Resources.getInstance().getProperty(
					FreeMind.RESOURCES_SCRIPT_USER_KEY_NAME_FOR_SIGNING);
			if (content.mKeyName != null) {
				keyName = content.mKeyName;
			} else if (propertyKeyName != null && propertyKeyName.length() > 0) {
				content.mKeyName = propertyKeyName;
				keyName = content.mKeyName;
			}
			instance.initSign((PrivateKey) mKeyStore.getKey(keyName, password));
			instance.update(content.mScript.getBytes());
			byte[] signature = instance.sign();
			content.mSignature = Tools.toBase64(signature);
			// System.out.println("Signed: " +content);
			return content.toString();
		} catch (Exception e) {
			Resources.getInstance().logException(e);
			pFrame.getController().errorMessage(e.getLocalizedMessage());
		}
		return content.mScript;
	}

	public boolean isScriptSigned(String pScript, OutputStream pOutStream) {
		ScriptContents content = new ScriptContents(pScript);
		if (content.mSignature != null) {
			try {
				Signature instanceVerify = Signature.getInstance("SHA1withDSA");
				if (content.mKeyName == null) {
					/**
					 * This is the FreeMind public key. keytool -v -rfc
					 * -exportcert -alias freemindscriptkey
					 */
					String cer = "-----BEGIN CERTIFICATE-----\n"
							+ "MIIDKDCCAuWgAwIBAgIESAY2ADALBgcqhkjOOAQDBQAwdzELMAkGA1UEBhMCREUxCzAJBgNVBAgT"
							+ "AkRFMRMwEQYDVQQHEwpPcGVuU291cmNlMRgwFgYDVQQKEw9zb3VyY2Vmb3JnZS5uZXQxETAPBgNV"
							+ "BAsTCEZyZWVNaW5kMRkwFwYDVQQDExBDaHJpc3RpYW4gRm9sdGluMB4XDTA4MDQxNjE3MjMxMloX"
							+ "DTA4MDcxNTE3MjMxMlowdzELMAkGA1UEBhMCREUxCzAJBgNVBAgTAkRFMRMwEQYDVQQHEwpPcGVu"
							+ "U291cmNlMRgwFgYDVQQKEw9zb3VyY2Vmb3JnZS5uZXQxETAPBgNVBAsTCEZyZWVNaW5kMRkwFwYD"
							+ "VQQDExBDaHJpc3RpYW4gRm9sdGluMIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9K"
							+ "nC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVCl"
							+ "pJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3R"
							+ "SAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdM"
							+ "Cz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/"
							+ "C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAZm5z5EZX"
							+ "Vhtye5jY3X9w24DJ3yNJbNl2tfkOBIc0KfgyxONTSJKtUpmLI3btUxy3pQf/T8BShlY3PAC0fp3M"
							+ "eDG8WRq1wM3luLd1V9SS8EG6tPJBZ3mciCUymTT7n9CZNzATIpqNIXHSD/wljRABedUi8PMg4KbV"
							+ "Pnhu6Y6b1uAwCwYHKoZIzjgEAwUAAzAAMC0CFQCFHGwe+HHOvY0MmKYHbiq7fRxMGwIUC0voAGYU"
							+ "u6vgVFqdLI5F96JLTqk="
							+ "\n-----END CERTIFICATE-----\n";
					CertificateFactory cf = CertificateFactory
							.getInstance("X.509");
					Collection c = cf
							.generateCertificates(new ByteArrayInputStream(cer
									.getBytes()));
					Iterator i = c.iterator();
					if (i.hasNext()) {
						Certificate cert = (Certificate) i.next();
						instanceVerify.initVerify(cert);
					} else {
						throw new IllegalArgumentException(
								"Internal certificate wrong.");
					}
				} else {
					initializeKeystore(null);
					instanceVerify.initVerify(mKeyStore
							.getCertificate(content.mKeyName));
				}
				instanceVerify.update(content.mScript.getBytes());
				boolean verify = instanceVerify.verify(Tools
						.fromBase64(content.mSignature));
				// System.out.println("Signature result: " + verify);
				return verify;
			} catch (Exception e) {
				Resources.getInstance().logException(e);
				try {
					pOutStream.write(e.toString().getBytes());
					pOutStream.write("\n".getBytes());
				} catch (Exception e1) {
					Resources.getInstance().logException(e1);
				}
			}
		}
		return false;
	}

}
