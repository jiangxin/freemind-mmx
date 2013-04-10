/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.common;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import tests.freemind.FreeMindMainMock;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.main.Resources;
import freemind.main.Tools;

/**
 * @author foltin
 * @date 01.04.2013
 */
public class FreeMindProgressMonitor extends JDialog {

	/**
	 * 
	 */
	private static final String PROGRESS_MONITOR_WINDOW_CONFIGURATION_STORAGE = "progress_monitor_window_configuration_storage";
	private JLabel mLabel;
	private JProgressBar mProgressBar;
	private JButton mCancelButton;
	protected boolean mCanceled = false;

	/**
	 * 
	 */
	public FreeMindProgressMonitor(String pTitle) {
		setTitle(getString(pTitle));
		mLabel = new JLabel("!");
		mProgressBar = new JProgressBar();
		mCancelButton = new JButton();
		Tools.setLabelAndMnemonic(mCancelButton, getString(("cancel")));
		mCancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				mCanceled = true;
			}
		});
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints(0, 0,
				GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 5), 0, 0);
		add(mLabel, constraints);
		constraints.gridy = 1;
		add(mProgressBar, constraints);
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.EAST;
		add(mCancelButton, constraints);
		// Tools.addEscapeActionToDialog(this);
		pack();
		setSize(new Dimension(600, 200));
		String marshaled = Resources.getInstance().getProperty(
				PROGRESS_MONITOR_WINDOW_CONFIGURATION_STORAGE);
		if (marshaled != null) {
			XmlBindingTools.getInstance().decorateDialog(marshaled, this);
		}
	}

	protected String getString(String resource) {
		return Resources.getInstance().getResourceString(resource);
	}

	/**
	 * @param pCurrent
	 * @param pMax
	 * @param pName
	 *            resource string to be displayed as progress string (maybe with
	 *            parameters pParameters)
	 * @param pParameters
	 *            objects to be put in the resource string for pName
	 * @return
	 */
	public boolean showProgress(int pCurrent, final int pMax, String pName,
			Object[] pParameters) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mProgressBar.setMaximum(pMax);
			}
		});
		return showProgress(pCurrent, pName, pParameters);
	}

	public boolean showProgress(int pCurrent, String pName, Object[] pParameters) {
		final String format = Resources.getInstance().format(pName, pParameters);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mLabel.setText(format);
			}
		});
		return setProgress(pCurrent);
	}

	public boolean setProgress(final int pCurrent) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				mProgressBar.setValue(pCurrent);
			}
		});
		return mCanceled;
	}

	public void dismiss() {
		WindowConfigurationStorage storage = new WindowConfigurationStorage();
		String marshalled = XmlBindingTools.getInstance().storeDialogPositions(
				storage, this);
		Resources
				.getInstance()
				.getProperties()
				.setProperty(PROGRESS_MONITOR_WINDOW_CONFIGURATION_STORAGE,
						marshalled);
		this.setVisible(false);
	}

	/**
	 * Test method for this dialog.
	 */
	public static void main(String[] args) throws InterruptedException {
		FreeMindMainMock mock = new FreeMindMainMock();
		Resources.createInstance(mock);
		FreeMindProgressMonitor progress = new FreeMindProgressMonitor("title");
		progress.setVisible(true);
		for (int i = 0; i < 10; i++) {
			boolean canceled = progress.showProgress(i, 10, "inhalt {0}",
					new Object[] { Integer.valueOf(i) });
			if (canceled) {
				progress.dismiss();
				System.exit(1);
			}
			Thread.sleep(1000l);
		}
		progress.dismiss();
		System.exit(0);
	}
}
