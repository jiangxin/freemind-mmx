package plugins.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.lucene.queryparser.classic.ParseException;

import plugins.search.Search.SearchResult;

public class SearchViewPanel extends JDialog implements ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4038199355190689628L;
	private JTextField searchTermsField = new JTextField();
	private JRadioButton rdbtnOpen;
	private JRadioButton rdbtnDirectorySearch;
	private final ButtonGroup directoryButtonGroup = new ButtonGroup();
	private JButton btnChooseDirectoryButton;
	private JTextField selectedDirectoryField = new JTextField();
	private ISearchController searchControllerHook;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		class TestHook implements ISearchController {
			private File[] files;

			public TestHook(File[] files) {
				this.files = files;
			}

			@Override
			public Logger getLogger(Class className) {
				return Logger.getLogger(className.getName());
			}

			@Override
			public File[] getFilesOfOpenTabs() {
				return this.files;
			}

			@Override
			public void setWaitingCursor(boolean waiting) {
				// TODO Auto-generated method stub
			}

			@Override
			public JFrame getJFrame() {
				return new JFrame();
			}

			@Override
			public void openMap(String path) {
				// TODO Auto-generated method stub

			}
		}

		File[] files = new File[] { new File("data/freemind.mm") };
		SearchViewPanel searchPanel = new SearchViewPanel(new TestHook(
				files));
		searchPanel.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	SearchViewPanel(ISearchController searchController) {
		super(searchController.getJFrame(), "Search Multiple Maps", false);
		this._logger = searchController.getLogger(SearchViewPanel.class);

		this.searchControllerHook = searchController;
		initialize();

	}

	private static Logger _logger = null;

	private File selectedDirectory;
	private JButton btnGoButton = new JButton("Search");

	private JSplitPane splitPane;
	private JTextArea scorePanel;
	private JScrollPane resultsListPane;

	/**
	 * Initialize the contents of the frame.
	 */
	/**
	 * 
	 */
	private void initialize() {
		final JPanel content = new JPanel();
		setContentPane(content);
		JPanel criteriaPanel = new JPanel();
		updateSelectedFolderField();

		// / Bottom pane
		scorePanel = new JTextArea("");
		Dimension minimumSize = new Dimension(100, 0);
		String[] listing = new String[] { "No results" };
		resultsList = new JList(listing);
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsList.setSelectedIndex(0);
		resultsList.addListSelectionListener(this);
		resultsListPane = new JScrollPane(resultsList);

		scorePanel.setMinimumSize(minimumSize);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsListPane,
				scorePanel);
		splitPane.setDividerLocation(0.5);
		setMainPanelText("Choose search terms and select go");
		content.setLayout(new BorderLayout(0, 0));

		content.add(criteriaPanel, BorderLayout.NORTH);
		GridBagLayout gbl_criteriaPanel = new GridBagLayout();
		gbl_criteriaPanel.columnWidths = new int[] { 224, 224, 0 };
		gbl_criteriaPanel.rowHeights = new int[] { 25, 25, 25, 0 };
		gbl_criteriaPanel.columnWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_criteriaPanel.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		criteriaPanel.setLayout(gbl_criteriaPanel);

		searchTermsField = new JTextField();
		searchTermsField.setColumns(30);
		searchTermsField.setMinimumSize(searchTermsField.getPreferredSize());

		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		searchTermsField.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "search");
		searchTermsField.getActionMap().put("search", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					runSearch();
				} catch (Exception e1) {
					_logger.warning("Failed:" + e1.getLocalizedMessage());
				}
			}
		});

		GridBagConstraints gbc_searchTermsField = new GridBagConstraints();
		gbc_searchTermsField.fill = GridBagConstraints.BOTH;
		gbc_searchTermsField.insets = new Insets(0, 0, 5, 5);
		gbc_searchTermsField.gridx = 0;
		gbc_searchTermsField.gridy = 0;
		criteriaPanel.add(searchTermsField, gbc_searchTermsField);
		btnGoButton.addActionListener(new AbstractAction() {
			{
				putValue(NAME, "Go");
				putValue(SHORT_DESCRIPTION, "Search for the chosen terms");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					runSearch();
				} catch (Exception e1) {
					_logger.warning("Failed:" + e1.getLocalizedMessage());
				}
			}

		});
		GridBagConstraints gbc_btnGoButton = new GridBagConstraints();
		gbc_btnGoButton.fill = GridBagConstraints.BOTH;
		gbc_btnGoButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnGoButton.gridx = 1;
		gbc_btnGoButton.gridy = 0;
		criteriaPanel.add(btnGoButton, gbc_btnGoButton);

		rdbtnDirectorySearch = new JRadioButton("Directory Search");
		rdbtnDirectorySearch.setSelected(true);
		rdbtnDirectorySearch.setAction(new AbstractAction() {

			{
				putValue(NAME, "Directory Search");
				putValue(SHORT_DESCRIPTION,
						"Choose a folder with Freemind maps");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(true);
				updateSelectedFolderField();
			}
		});

		directoryButtonGroup.add(rdbtnDirectorySearch);
		GridBagConstraints gbc_rdbtnDirectorySearch = new GridBagConstraints();
		gbc_rdbtnDirectorySearch.fill = GridBagConstraints.BOTH;
		gbc_rdbtnDirectorySearch.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnDirectorySearch.gridx = 0;
		gbc_rdbtnDirectorySearch.gridy = 1;
		criteriaPanel.add(rdbtnDirectorySearch, gbc_rdbtnDirectorySearch);
		rdbtnOpen = new JRadioButton("Open Maps");
		rdbtnOpen.setSelected(false);
		rdbtnOpen.setAction(new AbstractAction() {

			{
				putValue(NAME, "Open Maps");
				putValue(SHORT_DESCRIPTION,
						"Search the maps currently open in the application");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(false);
				btnGoButton.setEnabled(true);
			}
		});

		directoryButtonGroup.add(rdbtnOpen);
		GridBagConstraints gbc_rdbtnOpen = new GridBagConstraints();
		gbc_rdbtnOpen.fill = GridBagConstraints.BOTH;
		gbc_rdbtnOpen.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnOpen.gridx = 1;
		gbc_rdbtnOpen.gridy = 1;
		criteriaPanel.add(rdbtnOpen, gbc_rdbtnOpen);

		btnChooseDirectoryButton = new JButton("Choose Directory");
		btnChooseDirectoryButton.setEnabled(true);
		btnChooseDirectoryButton.setAction(new AbstractAction() {
			{
				putValue(NAME, "Choose Directory");
				putValue(SHORT_DESCRIPTION, "Choose Directory");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				_logger.fine("Opened file chooser");
				JFileChooser fc = new JFileChooser() {

					@Override
					protected JDialog createDialog(Component parent)
							throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						// config here as needed - just to see a difference
						dialog.setLocationByPlatform(true);
						// might help - can't know because I can't reproduce the
						// problem
						dialog.setAlwaysOnTop(true);
						return dialog;
					}

				};
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retValue = fc.showOpenDialog(null);
				if (retValue == JFileChooser.APPROVE_OPTION) {
					selectedDirectory = fc.getSelectedFile();
					_logger.info("Selected : " + selectedDirectory);
				} else {
					_logger.fine("Cancelled");
				}
				updateSelectedFolderField();
			}
		});
		GridBagConstraints gbc_btnChooseDirectoryButton = new GridBagConstraints();
		gbc_btnChooseDirectoryButton.fill = GridBagConstraints.BOTH;
		gbc_btnChooseDirectoryButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnChooseDirectoryButton.gridx = 0;
		gbc_btnChooseDirectoryButton.gridy = 2;
		criteriaPanel.add(btnChooseDirectoryButton,
				gbc_btnChooseDirectoryButton);
		selectedDirectoryField.setEditable(true);
		selectedDirectoryField.setColumns(10);
		GridBagConstraints gbc_selectedDirectoryField = new GridBagConstraints();
		gbc_selectedDirectoryField.fill = GridBagConstraints.BOTH;
		gbc_selectedDirectoryField.gridx = 1;
		gbc_selectedDirectoryField.gridy = 2;
		criteriaPanel.add(selectedDirectoryField, gbc_selectedDirectoryField);
		content.add(splitPane, BorderLayout.CENTER);

		int width = 600;
		int height = 400;
		btnGoButton.setSize(10, 10);
		splitPane.setSize(width / 2, height / 2);
		criteriaPanel.setSize(width / 2, height / 2);
		content.setSize(width, height);
		setSize(width, height);
		
		setModal(true);

	}

	public void updateSelectedFolderField() {
		if (null == this.selectedDirectory) {
			this.selectedDirectory = new File(System.getProperty("user.dir"));
		}

		this.btnGoButton
				.setToolTipText("Choose a valid folder or open maps before doing search");

		selectedDirectoryField.setText(this.selectedDirectory.getPath());
		this.btnGoButton.setEnabled(true);
	}

	private void runSearch() throws IOException, ParseException {
		SwingUtilities.getRoot(this).setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setMainPanelText("Starting search...");

		try {
			File[] mapsFiles;
			boolean isDirectoryMode = isDirectoryMode();
			if (isDirectoryMode) {
				File file = new File(selectedDirectoryField.getText());
				mapsFiles = new File[] { file };
				if (!file.exists()) {
					String message = "Directory does not exist:"
							+ selectedDirectoryField.getText();
					setMainPanelText(message);
					throw new ParseException(message);
				}
			} else {
				mapsFiles = this.searchControllerHook.getFilesOfOpenTabs();
			}

			String searchString = this.searchTermsField.getText();
			setMainPanelText("Searching [" + Arrays.asList(mapsFiles)
					+ "] for [" + searchString + "]");

			Search search = new Search(_logger);

			Object[] listData = search.runSearch(searchString, mapsFiles);
			resultsList.setListData(listData);
			updateScorePanel();
		} finally {
			SwingUtilities.getRoot(this).setCursor(Cursor.getDefaultCursor());
		}

	}

	public boolean isDirectoryMode() {
		boolean directoryMode = false;
		Enumeration<AbstractButton> mode = this.directoryButtonGroup
				.getElements();
		while (mode.hasMoreElements()) {
			AbstractButton button = mode.nextElement();
			if (button.getText().equals("Directory Search")
					&& button.isSelected()) {
				directoryMode = true;
			}
		}
		return directoryMode;
	}

	private JList resultsList;

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		updateScorePanel();
		SearchResult selectedItem = getSelectedItem();
		if (null != selectedItem) {
			this.searchControllerHook.openMap(selectedItem.getPath());
		}
	}

	public void updateScorePanel() {
		SearchResult selectedItem = getSelectedItem();
		if (null != selectedItem) {
			setMainPanelText(selectedItem.getPath());
		}
	}

	public SearchResult getSelectedItem() {
		SearchResult selectedItem = null;
		int selectedIndex = this.resultsList.getSelectedIndex();
		if (this.resultsList.getModel().getSize() > -1) {
			if (selectedIndex < 0) {
				selectedIndex = 0;
			}
			selectedItem = (SearchResult) this.resultsList.getSelectedValue();
		}
		return selectedItem;
	}

	public void setMainPanelText(String text) {
		_logger.info("Set panel text to : " + text);
		scorePanel.setText(text);
	}
}
