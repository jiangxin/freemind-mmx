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


package accessories.plugins.dialogs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import freemind.common.TextTranslator;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame;
import freemind.modes.mindmapmode.dialogs.StylePatternFrame.StylePatternFrameType;

/** */
public class ChooseFormatPopupDialog extends JDialog implements TextTranslator,
		KeyListener {

	public static final int CANCEL = -1;

	public static final int OK = 1;

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = "accessories.plugins.dialogs.ChooseFormatPopupDialog.window_storage";

	private int result = CANCEL;

	private javax.swing.JPanel jContentPane = null;

	private MindMapController mController;

	private JButton jCancelButton;

	private JButton jOKButton;

	private StylePatternFrame mStylePatternFrame;

	/**
	 * This constructor is used, if you need the user to enter a pattern
	 * generally.
	 * 
	 */
	public ChooseFormatPopupDialog(JFrame caller, MindMapController controller,
			String dialogTitle, Pattern pattern) {
		super(caller);
		this.mController = controller;
		initialize(dialogTitle);
		mStylePatternFrame.setPattern(pattern);
		mStylePatternFrame.addListeners();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(String dialogTitle) {

		this.setTitle(mController.getText(dialogTitle));
		JPanel contentPane = getJContentPane();
		this.setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelPressed();
			}
		});
		addKeyListener(this);
		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				cancelPressed();
			}
		};
		Tools.addEscapeActionToDialog(this, action);
		pack();
		mController.decorateDialog(this, WINDOW_PREFERENCE_STORAGE_PROPERTY);

	}

	private void close() {
		WindowConfigurationStorage storage = new WindowConfigurationStorage();
		mController.storeDialogPositions(this, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);
		setVisible(false);
		this.dispose();
	}

	private void okPressed() {
		result = OK;
		close();
	}

	private void cancelPressed() {
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
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			/*
			 * public GridBagConstraints(int gridx, int gridy, int gridwidth,
			 * int gridheight, double weightx, double weighty, int anchor, int
			 * fill, Insets insets, int ipadx, int ipady)
			 */
			jContentPane.add(new JScrollPane(getStylePatternFrame()),
					new GridBagConstraints(0, 0, 2, 1, 2.0, 8.0,
							GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(getJOKButton(), new GridBagConstraints(0, 1, 1, 1,
					1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			jContentPane.add(getJCancelButton(), new GridBagConstraints(1, 1,
					1, 1, 1.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			getRootPane().setDefaultButton(getJOKButton());
		}
		return jContentPane;
	}

	private Component getStylePatternFrame() {
		if (mStylePatternFrame == null) {
			mStylePatternFrame = new StylePatternFrame(this, mController,
					StylePatternFrameType.WITHOUT_NAME_AND_CHILDS);
			mStylePatternFrame.init();

		}
		return mStylePatternFrame;
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

			Tools.setLabelAndMnemonic(jOKButton, mController.getText("ok"));
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
			Tools.setLabelAndMnemonic(jCancelButton,
					mController.getText(("cancel")));
		}
		return jCancelButton;
	}

	/**
	 * @return Returns the result.
	 */
	public int getResult() {
		return result;
	}

	public String getText(String pKey) {
		return mController.getText(pKey);
	}

	public Pattern getPattern() {
		return mStylePatternFrame.getResultPattern();
	}

	public Pattern getPattern(Pattern copyIntoPattern) {
		return mStylePatternFrame.getResultPattern(copyIntoPattern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent keyEvent) {
		System.out.println("key pressed: " + keyEvent);
		switch (keyEvent.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			cancelPressed();
			keyEvent.consume();
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent keyEvent) {
		System.out.println("keyReleased: " + keyEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent keyEvent) {
		System.out.println("keyTyped: " + keyEvent);
	}

}
