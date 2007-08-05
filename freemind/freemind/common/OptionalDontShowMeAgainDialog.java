/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Christian Foltin, Dimitry Polivaev and others.
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
 * Created on 31.07.2007
 */
/*$Id: OptionalDontShowMeAgainDialog.java,v 1.1.2.1 2007-08-05 20:33:13 christianfoltin Exp $*/

package freemind.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Tools;

/**
 * Dialog with a decision that can be disabled.
 * 
 * @author foltin
 * 
 */
public class OptionalDontShowMeAgainDialog {
	public final static int ONLY_OK_SELECTION_IS_STORED = 0;
	public final static int BOTH_OK_AND_CANCEL_OPTIONS_ARE_STORED = 1;
	private static final String PREFERENCE_STORAGE_PROPERTY = "OptionalDialogPosition";
	private final String mTitleId;
	private final String mMessageId;
	private final TextTranslator mTextTranslator;
	private final JFrame mParent;
	private int mResult = JOptionPane.CANCEL_OPTION;
	private JDialog mDialog;
	private JCheckBox mDontShowAgainBox;
	private final DontShowPropertyHandler mDontShowPropertyHandler;
	private final int mMessageType;
	private final Controller mController;

	public interface DontShowPropertyHandler {
		/**
		 * @return accepted are the following values as return values:
		 * * "" (means: show this dialog)
		 * * "true" (means: the answer was ok and I want to remember that).
		 * * "false" (means: the answer was cancel and I want to remember that).
		 */
		String getProperty();
		void setProperty(String pValue);
	}
	
	public OptionalDontShowMeAgainDialog(Controller pController, String pMessageId,
			String pTitleId, TextTranslator pTextTranslator, DontShowPropertyHandler pDontShowPropertyHandler, int pMessageType) {
		mController = pController;
		mParent = pController.getJFrame();
		mMessageId = pMessageId;
		mTitleId = pTitleId;
		mTextTranslator = pTextTranslator;
		mDontShowPropertyHandler = pDontShowPropertyHandler;
		mMessageType = pMessageType;
	}

	/**
	 * @return an int from JOptionPane (eg. JOptionPane.OK_OPTION).
	 */
	public int getResult() {
		return mResult;
	}


	public OptionalDontShowMeAgainDialog show() {
		String property = mDontShowPropertyHandler.getProperty();
		if(Tools.safeEquals(property, "true")) {
			mResult = JOptionPane.OK_OPTION;
			return this;
		}
		if(Tools.safeEquals(property, "false")) {
			mResult = JOptionPane.CANCEL_OPTION;
			return this;
		}
		mDialog = null;
		mDialog = new JDialog(mParent, mTextTranslator.getText(mTitleId));
//		}
//		if (mParent instanceof Window) {
//			Window window = (Window) mParent;
//			mDialog = new JDialog(window, mTextTranslator.getText(mTitleId));
//		}
		if (mDialog == null) {
			throw new IllegalArgumentException("Not right base class given");
		}
		mDialog.setModal(true);
		mDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		AbstractAction cancelAction = new AbstractAction() {

			public void actionPerformed(ActionEvent pE) {
				close(JOptionPane.CANCEL_OPTION);
			}
		};
		AbstractAction okAction = new AbstractAction() {

			public void actionPerformed(ActionEvent pE) {
				close(JOptionPane.OK_OPTION);
			}
		};
		Tools.addEscapeActionToDialog(mDialog, cancelAction);
		mDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent pE) {
				close(JOptionPane.CANCEL_OPTION);
			}
		});
		mDialog.setLayout(new GridBagLayout());
		mDialog.add(new JLabel(mTextTranslator.getText(mMessageId)),
				new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(5, 5, 0, 0), 0, 0));
		mDialog.add(new JLabel("?"), new GridBagConstraints(0, 0, 1, 2, 1.0,
				2.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
						5, 5, 0, 0), 0, 0));
		String boxString;
		if(mMessageType == ONLY_OK_SELECTION_IS_STORED) {
			boxString = "OptionalDontShowMeAgainDialog.dontShowAgain";			
		} else {
			boxString = "OptionalDontShowMeAgainDialog.rememberMyDescision";
		}
		mDontShowAgainBox = new JCheckBox(mTextTranslator
				.getText(boxString));
		mDialog.add(mDontShowAgainBox, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5,
						0, 0), 0, 0));
		JButton okButton = new JButton(mTextTranslator
				.getText("OptionalDontShowMeAgainDialog.ok"));
		okButton.addActionListener(okAction);
		mDialog.add(okButton, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5,
						0, 0), 0, 0));
		JButton cancelButton = new JButton(mTextTranslator
				.getText("OptionalDontShowMeAgainDialog.cancel"));
		cancelButton.addActionListener(cancelAction);
		mDialog.add(cancelButton, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5,
						0, 0), 0, 0));
		mDialog.getRootPane().setDefaultButton(okButton);
		mDialog.pack();
		//Retrieve window size and column positions.
		XmlBindingTools.getInstance().decorateDialog(mController,
				mDialog, PREFERENCE_STORAGE_PROPERTY);
		mDialog.setVisible(true);
		return this;
	}

	private void close(int pResult) {
		mResult = pResult;
		if(mDontShowAgainBox.isSelected()){
			if(mMessageType == ONLY_OK_SELECTION_IS_STORED) {
				if (mResult == JOptionPane.OK_OPTION) {
					mDontShowPropertyHandler.setProperty("true");
				}
			} else {
				mDontShowPropertyHandler.setProperty((mResult==JOptionPane.OK_OPTION)?"true":"false");
			}
		} else {
			mDontShowPropertyHandler.setProperty("");
		}
        XmlBindingTools.getInstance().storeDialogPositions(mController, mDialog, new WindowConfigurationStorage(),
        		PREFERENCE_STORAGE_PROPERTY);

		mDialog.setVisible(false);
		mDialog.dispose();
	}
}
