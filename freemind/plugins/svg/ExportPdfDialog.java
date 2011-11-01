/**
 * 
 */
package plugins.svg;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import freemind.controller.Controller;
import freemind.main.Tools;

/**
 * @author Andy
 * 
 *         Class is responsible for a dialog to input all pdf export parameters
 *         like page orientation or format
 */
public class ExportPdfDialog extends JDialog {

	private static final String PORTRAIT = "portrait";

	private static final String LANDSCAPE = "landscape";

	private static final String PROP_PDF_PAGE_FORMAT = "pdf_page_format";

	private static final String PROP_PDF_PAGE_ORIENTATION = "pdf_page_orientation";

	/**
	 * Chosen format
	 */
	private String format;

	/**
	 * list of available formats
	 */
	private String[] formatsCollection;

	/**
	 * reference to the freemind controller
	 */
	private Controller controller;

	/**
	 * chosen orientation
	 */
	private int orientation;

	private JComboBox listBox;

	private JRadioButton birdButtonLandscape;

	private JRadioButton birdButtonPortrait;

	private ButtonGroup group;

	private boolean mResult = true;

	/**
	 * 
	 * @return the format of the page format as text "A3","A4" defined in
	 *         ExportPdfPapers
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 
	 * @return the pageformat PageFormat.LANDSCAPE or PageFormat.PORTRAIT
	 */
	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * Constructor
	 * 
	 * @param owner
	 * @param formats
	 * @param controller
	 */
	public ExportPdfDialog(Frame owner, String[] formats, Controller controller) {
		super(owner);
		this.formatsCollection = formats;
		this.controller = controller;
		initialize();
	}

	/**
	 * Initialize the dialog elements
	 */
	private void initialize() {
		this.setModal(true);
		this.setTitle("PDF Export Settings");

		this.setPreferredSize(new Dimension(400, 150));
		// Page size
		JLabel labelPaper = new JLabel("Size:");
		java.util.Arrays.sort(formatsCollection);
		listBox = new JComboBox(formatsCollection);
		listBox.setEditable(false);
		listBox.setPreferredSize(new Dimension(100, 25));

		// Page orientation
		birdButtonLandscape = new JRadioButton("Landscape");
		birdButtonLandscape.setMnemonic(KeyEvent.VK_L);
		birdButtonLandscape.setSelected(true);

		birdButtonPortrait = new JRadioButton("Portrait");
		birdButtonPortrait.setMnemonic(KeyEvent.VK_P);

		group = new ButtonGroup();
		group.add(birdButtonLandscape);
		group.add(birdButtonPortrait);

		// ok button
		JButton jOKButton = new JButton();
		jOKButton.setPreferredSize(new Dimension(100, 20));
		jOKButton.setAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}

		});
		jOKButton.setText("OK");

		// panels and layout
		JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("Paper"));

		JPanel panel2 = new JPanel();
		panel2.setBorder(BorderFactory.createTitledBorder("Orientation"));

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		panel1.add(labelPaper);
		panel1.add(listBox);
		panel2.add(birdButtonLandscape);
		panel2.add(birdButtonPortrait);

		JPanel panel = new JPanel();
		panel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(panel1, c);
		c.gridx = 1;
		c.gridy = 0;
		panel.add(panel2, c);
		c.fill = GridBagConstraints.CENTER;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(jOKButton, c);

		// set default user values
		initDefaults();

		// add escape action
		freemind.main.Tools.addEscapeActionToDialog(this);

		// finish dialog
		this.setLocationRelativeTo(this.getParent());
		this.setContentPane(panel);
		this.getRootPane().setDefaultButton(jOKButton);

		this.pack();

	}

	/**
	 * Initialize the dialog elements with default values from the user settings
	 */
	private void initDefaults() {
		// page orientation
		String storedOrientation = controller
				.getProperty(PROP_PDF_PAGE_ORIENTATION);
		if (storedOrientation != null) // property found
		{
			if (Tools.safeEquals(storedOrientation, LANDSCAPE)) {
				birdButtonLandscape.setSelected(true);
			} else {
				birdButtonPortrait.setSelected(true);
			}
		}

		// page format
		String storedFormat = controller.getProperty(PROP_PDF_PAGE_FORMAT);
		if (storedFormat != null) // property found
		{
			listBox.setSelectedItem(storedFormat);
		}

	}

	/**
	 * stores the inputs as defaults in user settings
	 */
	private void storeDefaults() {
		// page orientation
		if (orientation == PageFormat.LANDSCAPE)
			controller.setProperty(PROP_PDF_PAGE_ORIENTATION, LANDSCAPE);
		else if (orientation == PageFormat.PORTRAIT)
			controller.setProperty(PROP_PDF_PAGE_ORIENTATION, PORTRAIT);

		// page format
		controller.setProperty(PROP_PDF_PAGE_FORMAT, format);
	}

	/**
	 * button ok pressed, will close the dialog and store the values
	 */
	private void okPressed() {
		format = (String) listBox.getSelectedItem();

		if (birdButtonLandscape.isSelected()) {
			orientation = PageFormat.LANDSCAPE;
		} else {
			orientation = PageFormat.PORTRAIT;
		}

		storeDefaults();
		this.setVisible(false);
	}

	public void dispose() {
		mResult = false;
		super.dispose();
	}

	public boolean getResult() {
		return mResult;
	}

}
