/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/* MyTest.java */

package accessories.plugins.util.xslt;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import accessories.plugins.util.window.WindowClosingAdapter;
import freemind.main.ExampleFileFilter;
import freemind.modes.FreeMindFileDialog;
import freemind.modes.ModeController;

public class ExportDialog extends JFrame {
	private static final String ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_TARGET = "accessories.plugins.util.xslt.ExportDialog.store.target"; //$NON-NLS-1$
	private static final String ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_XSLT = "accessories.plugins.util.xslt.ExportDialog.store.xslt"; //$NON-NLS-1$

	class ExportListener implements ActionListener {
		private ExportDialog parent = null;
		boolean exitSystem = true;
		private boolean cancel = false;
		XmlExporter xe = null;

		public ExportListener(ExportDialog m) {
			parent = m;
		}

		public ExportListener(ExportDialog m, boolean pCancel) {
			parent = m;
			cancel = pCancel;
		}

		public void actionPerformed(ActionEvent e) {
			if (!cancel) {
				// System.out.println("voila, export methode");
				xe = new XmlExporter();
				try {
					xe.transForm(parent.xmlFile,
							new File(parent.fieldXsltFileName.getText()), new File(
									parent.fieldTargetFileName.getText()));
				} catch (FileNotFoundException e1) {
					freemind.main.Resources.getInstance().logException(e1);
				}
			}
			// store values in preferences:
			mController.getFrame().setProperty(
					ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_XSLT,
					fieldXsltFileName.getText());
			mController.getFrame().setProperty(
					ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_TARGET,
					fieldTargetFileName.getText());
			parent.setVisible(false);
			parent.dispose();
			/*
			 * if (exitSystem) { System.exit(0); }
			 */
		}

	}

	protected JTextField fieldXsltFileName = null;
	protected JTextField fieldTargetFileName = null;
	protected File xmlFile = null;
	private final ModeController mController;

	public ExportDialog(File nxmlFile, ModeController pController) {

		super(pController.getFrame().getResourceString("ExportDialog.2")); //$NON-NLS-1$
		xmlFile = nxmlFile;
		mController = pController;

		setBackground(Color.lightGray);
		this.addWindowListener(new WindowClosingAdapter(false));
		// set layout and add components
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc;
		getContentPane().setLayout(gbl);

		// add list

		/*
		 * List list = new List(); for (int i = 0; i < 20; ++i) {
		 * list.add("This is item " + i); } gbc = makegbc(0, 0, 1, 3);
		 * gbc.weightx = 100; gbc.weighty = 100; gbc.fill =
		 * GridBagConstraints.BOTH; gbl.setConstraints(list, gbc);
		 * getContentPane().add(list);
		 */

		// get last value from preferences:
		String lastXsltFileName = mController.getFrame().getProperty(
				ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_XSLT);
		String lastTargetFileName = mController.getFrame().getProperty(
				ACCESSORIES_PLUGINS_UTIL_XSLT_EXPORT_DIALOG_STORE_TARGET);
		// Zwei Labels und zwei Textfelder
		gbc = makegbc(0, 0, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel label = new JLabel(getResourceString("ExportDialog.3")); //$NON-NLS-1$
		gbl.setConstraints(label, gbc);
		getContentPane().add(label);
		// Textfeld
		gbc = makegbc(1, 0, 1, 1);
		gbc.weightx = 300;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldXsltFileName = new JTextField(lastXsltFileName);
		fieldXsltFileName.setColumns(20);
		gbl.setConstraints(fieldXsltFileName, gbc);
		getContentPane().add(fieldXsltFileName);

		gbc = makegbc(0, 1, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		JLabel labeli = new JLabel(getResourceString("ExportDialog.4")); //$NON-NLS-1$
		gbl.setConstraints(labeli, gbc);
		getContentPane().add(labeli);
		// Textfeld
		gbc = makegbc(1, 1, 1, 1);
		gbc.weightx = 100;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldTargetFileName = new JTextField(lastTargetFileName);
		fieldTargetFileName.setColumns(20);
		gbl.setConstraints(fieldTargetFileName, gbc);
		getContentPane().add(fieldTargetFileName);

		// XSL-Button
		JButton xslbutton = new JButton(getResourceString("ExportDialog.5")); //$NON-NLS-1$
		gbc = makegbc(2, 0, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		// gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbl.setConstraints(xslbutton, gbc);
		xslbutton.addActionListener(new FileChooseListener(0,
				fieldXsltFileName, xslbutton, xmlFile));
		getContentPane().add(xslbutton);
		// export-Button
		JButton exportbutton = new JButton(getResourceString("ExportDialog.6")); //$NON-NLS-1$
		gbc = makegbc(2, 1, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		// gbc.anchor = GridBagConstraints.SOUTHEAST;
		exportbutton.addActionListener(new FileChooseListener(1,
				fieldTargetFileName, exportbutton, xmlFile));
		gbl.setConstraints(exportbutton, gbc);
		getContentPane().add(exportbutton);

		// ok-Button
		JButton button = new JButton(getResourceString("ExportDialog.7")); //$NON-NLS-1$
		gbc = makegbc(2, 2, 1, 1);
		gbc.fill = GridBagConstraints.NONE;
		// gbc.anchor = GridBagConstraints.SOUTHEAST;
		button.addActionListener(new ExportListener(this));
		gbl.setConstraints(button, gbc);
		getContentPane().add(button);

		// cancel-Button
		JButton cbutton = new JButton(getResourceString("ExportDialog.8")); //$NON-NLS-1$
		gbc = makegbc(1, 2, 1, 1);
		gbc.anchor = gbc.EAST;
		gbc.fill = GridBagConstraints.NONE;
		// gbc.anchor = GridBagConstraints.SOUTHEAST;
		gbl.setConstraints(cbutton, gbc);
		cbutton.addActionListener(new ExportListener(this, true));
		getContentPane().add(cbutton);

		// Dialogelemente layouten
		pack();
	}

	private GridBagConstraints makegbc(int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.anchor = gbc.WEST;
		return gbc;
	}

	private String getResourceString(String pString) {
		return mController.getFrame().getResourceString(pString);
	}

	class FileChooseListener implements ActionListener {

		private Component parent = null;
		private JTextField jtf = null;
		private int kind = 0;
		private final String xslch = getResourceString("ExportDialog.9"); //$NON-NLS-1$
		private final String expch = getResourceString("ExportDialog.10"); //$NON-NLS-1$
		private String WindowTitle = null;
		private File xf = null;

		private FileChooseListener() {
		};

		public FileChooseListener(int wit, JTextField jt, Component c,
				File mmFile) {
			parent = c;
			jtf = jt;
			kind = wit;
			xf = mmFile;
			if (kind == 0) {
				WindowTitle = xslch;
			} else {
				WindowTitle = expch;
			}
		}

		public void actionPerformed(ActionEvent e) {

			ExampleFileFilter filter = null;

			if (kind == 0) {
				filter = new ExampleFileFilter(
						new String("xsl"), getResourceString("ExportDialog.13")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			FreeMindFileDialog chooser = mController.getFileChooser(filter);
			chooser.setDialogTitle(WindowTitle);
			int returnVal;
			if (kind == 0) {
				returnVal = chooser.showOpenDialog(parent);
			} else {
				returnVal = chooser.showSaveDialog(parent);
			}
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					if (kind == 0) {
						if (!new File(chooser.getSelectedFile()
								.getAbsolutePath()).exists()) {
							Object Message = getResourceString("ExportDialog.14"); //$NON-NLS-1$
							JOptionPane
									.showMessageDialog(
											null,
											Message,
											getResourceString("ExportDialog.15"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
						} else {
							// System.out.println("File chosen:"+chooser.getSelectedFile().getAbsolutePath());
							jtf.setText(chooser.getSelectedFile()
									.getAbsolutePath());
						}
					}
					if (kind == 1) {
						if (!new File(chooser.getSelectedFile()
								.getAbsolutePath()).exists()) {
							jtf.setText(chooser.getSelectedFile()
									.getAbsolutePath());
						} else {
							int i = JOptionPane
									.showConfirmDialog(
											null,
											getResourceString("ExportDialog.16"), getResourceString("ExportDialog.17"), 2); //$NON-NLS-1$ //$NON-NLS-2$
							if (i == JOptionPane.YES_OPTION) {
								jtf.setText(chooser.getSelectedFile()
										.getAbsolutePath());
							}

							// alert = new
							// AlertBox("The Exportfile chosen exists. Do you want to overwrite it. \nPlease Choose another.");
							// System.out.println("File chosen:"+chooser.getSelectedFile().getAbsolutePath());
						}
					}

				} catch (Exception ex) {
					System.out.println("exeption:" + ex);
				}
			}
		}

	}

}