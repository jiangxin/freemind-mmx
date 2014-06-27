/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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
*/

package plugins.collaboration.socket;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

public class FormDialog extends JDialog implements
		PropertyChangeListener {

	public static interface FormDialogValidator {
		/**
		 * @return true, if ok should be enabled.
		 */
		boolean isValid();
	}


	private final MindMapController mController2;
	private boolean mSuccess = false;
	private JButton mOkButton;
	private FormDialogValidator mFormDialogValidator;
	protected static java.util.logging.Logger logger = null;

	public boolean isSuccess() {
		return mSuccess;
	}

	public FormDialog(MindMapController pController) {
		super(pController.getFrame().getJFrame());
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		mController2 = pController;
	}

	public void setUp(Vector controls) {
		setUp(controls, new FormDialogValidator() {

			public boolean isValid() {
				return true;
			}
		});
	}

	public void setUp(Vector controls, FormDialogValidator pValidator) {
		mFormDialogValidator = pValidator;
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		FormLayout formLayout = new FormLayout(
				"right:max(40dlu;p), 4dlu, 80dlu, 7dlu", "");
		DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
		builder.setDefaultDialogBorder();
		for (Iterator it = controls.iterator(); it.hasNext();) {
			PropertyControl prop = (PropertyControl) it.next();
			prop.layout(builder, mController2);
			PropertyBean bean = (PropertyBean) prop;
			bean.addPropertyChangeListener(this);
		}
		getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
		JButton cancelButton = new JButton();
		Tools.setLabelAndMnemonic(cancelButton, getText("cancel"));
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				closeWindow();
			}

		});
		mOkButton = new JButton();
		Tools.setLabelAndMnemonic(mOkButton, getText("ok"));
		mOkButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				mSuccess = true;
				closeWindow();
			}

		});
		getRootPane().setDefaultButton(mOkButton);
		getContentPane().add(
				new ButtonBarBuilder().addGlue().addButton(cancelButton).addButton(mOkButton).build(),
				BorderLayout.SOUTH);
		setTitle(getText("enter_password_dialog"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				closeWindow();
			}
		});
		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				closeWindow();
			}
		};
		Action actionSuccess = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				mSuccess = true;
				closeWindow();
			}
		};
		Tools.addEscapeActionToDialog(this, action);
		Tools.addKeyActionToDialog(this, actionSuccess, "ENTER",
				"ok_dialog");

		pack();
		setVisible(true);

	}

	private void closeWindow() {
		setVisible(false);
	}

	String getText(String text) {
		return mController2.getText(text);
	}

	public void propertyChange(PropertyChangeEvent pEvt) {
		logger.finest("Property change " + pEvt);
		mOkButton.setEnabled(mFormDialogValidator.isValid());
	}

}