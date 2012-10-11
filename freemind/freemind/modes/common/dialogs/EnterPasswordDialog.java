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


package freemind.modes.common.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import freemind.common.TextTranslator;
import freemind.main.Tools;

/** */
public class EnterPasswordDialog extends JDialog {

	public static final int CANCEL = -1;

	public static final int OK = 1;

	private int result = CANCEL;

	private javax.swing.JPanel jContentPane = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JPasswordField jPasswordField = null;

	private JPasswordField jPasswordField1 = null;

	private JLabel jLabel2 = null;

	private JButton jOKButton = null;

	private JButton jCancelButton = null;

	private StringBuffer password = null;

	private boolean enterTwoPasswords = true;

	private TextTranslator mTranslator;

	/**
	 * @deprecated do not use. This is for visual editor only.
	 */
	public EnterPasswordDialog() {

	}

	/**
	 * This is the default constructor
	 */
	public EnterPasswordDialog(JFrame caller, TextTranslator pTranslator,
			boolean enterTwoPasswords) {
		super(caller, "", true /* =modal */);
		this.mTranslator = pTranslator;
		this.enterTwoPasswords = enterTwoPasswords;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle(mTranslator
				.getText("accessories/plugins/EncryptNode.properties_0")); //$NON-NLS-1$
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}

		});
		Tools.addEscapeActionToDialog(this, new AbstractAction() {
			public void actionPerformed(ActionEvent pE) {
				cancelPressed();
			}
		});

	}

	private void close() {
		this.dispose();

	}

	private void okPressed() {
		// check if equal:
		if (!checkEqualAndMinimumSize()) {
			JOptionPane.showMessageDialog(this, mTranslator
					.getText("accessories/plugins/EncryptNode.properties_1")); //$NON-NLS-1$
			return;
		}
		password = new StringBuffer();
		password.append(jPasswordField.getPassword());
		result = OK;
		close();
	}

	/**
     */
	private boolean checkEqualAndMinimumSize() {

		char[] a1 = jPasswordField.getPassword();

		if (a1.length < 2) {
			return false;
		}

		if (enterTwoPasswords) {
			char[] a2 = jPasswordField1.getPassword();
			if (a1.length != a2.length) {
				return false;
			}
			for (int i = 0; i < a1.length; i++) {
				if (a1[i] != a2[i]) {
					return false;
				}

			}
		}
		return true;
	}

	private void cancelPressed() {
		password = null;
		result = CANCEL;
		close();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
			jLabel.setText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_2")); //$NON-NLS-1$
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.insets = new java.awt.Insets(0, 5, 0, 0);
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			jLabel1.setText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_3")); //$NON-NLS-1$
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints5.insets = new java.awt.Insets(0, 5, 20, 0);
			jLabel2.setText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_4")); //$NON-NLS-1$
			jLabel2.setToolTipText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_5")); //$NON-NLS-1$
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.insets = new java.awt.Insets(20, 0, 0, 0);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.insets = new java.awt.Insets(20, 0, 0, 0);
			jContentPane.add(jLabel, gridBagConstraints1);
			jContentPane.add(getJPasswordField(), gridBagConstraints3);
			jContentPane.add(jLabel2, gridBagConstraints5);
			if (enterTwoPasswords) {
				jContentPane.add(getJPasswordField1(), gridBagConstraints4);
				jContentPane.add(jLabel1, gridBagConstraints2);
			}
			jContentPane.add(getJOKButton(), gridBagConstraints6);
			jContentPane.add(getJCancelButton(), gridBagConstraints7);
			getRootPane().setDefaultButton(getJOKButton());
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPasswordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jPasswordField1
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getJPasswordField1() {
		if (jPasswordField1 == null) {
			jPasswordField1 = new JPasswordField();
		}
		return jPasswordField1;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();

			jOKButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					okPressed();
				}

			});

			jOKButton.setText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_6")); //$NON-NLS-1$
		}
		return jOKButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setAction(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
			jCancelButton.setText(mTranslator
					.getText("accessories/plugins/EncryptNode.properties_7")); //$NON-NLS-1$
		}
		return jCancelButton;
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

	/**
	 * @return Returns the password.
	 */
	public StringBuffer getPassword() {
		return password;
	}
}
