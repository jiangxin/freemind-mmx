/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 04.02.2005
 */
/*$Id: TimeList.java,v 1.1.2.6.6.2 2005-12-06 19:47:30 dpolivaev Exp $*/
package plugins.time;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.generated.instance.TimeWindowColumnSetting;
import freemind.controller.actions.generated.instance.TimeWindowColumnSettingType;
import freemind.controller.actions.generated.instance.TimeWindowConfigurationStorage;
import freemind.controller.actions.generated.instance.TimeWindowConfigurationStorageType;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.view.mindmapview.MultipleImage;

/**
 * @author foltin
 *  
 */
public class TimeList extends ModeControllerHookAdapter {

	private static  String COLUMN_MODIFIED = "Modified";

	private static  String COLUMN_CREATED = "Created";

	private static  String COLUMN_ICONS = "Icons";

	private static  String COLUMN_TEXT = "Text";

	private static  String COLUMN_DATE = "Date";

	private static final int DATE_COLUMN = 0;

	private static final int NODE_TEXT_COLUMN = 1;

	protected static final int NODE_ICON_COLUMN = 2;

	protected static final int NODE_CREATED_COLUMN = 3;

	protected static final int NODE_MODIFIED_COLUMN = 4;

	private JDialog dialog;

	private JPanel timePanel;

	private JTable timeTable;

	private DefaultTableModel timeTableModel;

	private plugins.time.TableSorter sorter;

	private DateRenderer dateRenderer;

	private NodeRenderer nodeRenderer;

	private IconsRenderer iconsRenderer;

	private boolean showAllNodes = false;

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = TimeList.class
			.getName()
			+ "_properties";

	public void startupMapHook() {
		super.startupMapHook();

		// get strings from resources:
		COLUMN_MODIFIED = getResourceString("plugins/TimeList.xml_Modified");
		COLUMN_CREATED = getResourceString("plugins/TimeList.xml_Created");
		COLUMN_ICONS = getResourceString("plugins/TimeList.xml_Icons");
		COLUMN_TEXT = getResourceString("plugins/TimeList.xml_Text");
		COLUMN_DATE = getResourceString("plugins/TimeList.xml_Date");



		showAllNodes = Tools.xmlToBoolean(getResourceString("show_all_nodes"));
		dialog = new JDialog(getController().getFrame().getJFrame(), true /* modal */);
		dialog
				.setTitle(getResourceString("plugins/TimeManagement.xml_WindowTitle"));
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent event) {
		        disposeDialog();
		    }
		});
		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				disposeDialog();
			}
		};
		action.putValue(Action.NAME, "end_dialog");
		//		 Register keystroke
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke("ESCAPE"),
						action.getValue(Action.NAME));

		// Register action
		dialog.getRootPane().getActionMap().put(action.getValue(Action.NAME),
				action);

		Container contentPane = dialog.getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[] { 1.0f };
		gbl.rowWeights = new double[] { 1.0f };
		contentPane.setLayout(gbl);
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridy = 0;
		gb1.fill = GridBagConstraints.BOTH;
		dateRenderer = new DateRenderer();
		nodeRenderer = new NodeRenderer();
		iconsRenderer = new IconsRenderer(getController());
		timeTable = new JTable() {
			public TableCellRenderer getCellRenderer(int row, int column) {
				Object object = getModel().getValueAt(row, column);
				if (object instanceof Date)
					return dateRenderer;
				if (object instanceof NodeHolder)
					return nodeRenderer;
				if (object instanceof IconsHolder)
					return iconsRenderer;
				return super.getCellRenderer(row, column);
			}

			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}

			protected void processKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					EventListener[] el = super.getListeners(KeyListener.class);
					if (e.getID() != KeyEvent.KEY_RELEASED)
						return;
					for (int i = 0; i < el.length; i++) {
						KeyListener kl = (KeyListener) el[i];
						kl.keyReleased(e);
					}
					return;
				}
				super.processKeyEvent(e);
			}

		};
		timeTable.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent arg0) {
			}

			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					disposeDialog();
				}
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {

					gotoNodesAndClose(timeTable.getSelectedRow(), timeTable.getSelectedRows());
				}
			}

		});
		//double click = goto.
		timeTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Point p = e.getPoint();
					int row = timeTable.rowAtPoint(p);
					gotoNodesAndClose(row, new int[]{row});
				}
			}
		});
		//disable moving:
		timeTable.getTableHeader().setReorderingAllowed(false);
		timeTableModel = updateModel();
		sorter = new TableSorter(timeTableModel);
		timeTable.setModel(sorter);

		sorter.setTableHeader(timeTable.getTableHeader());
		sorter.setColumnComparator(Date.class,
				TableSorter.COMPARABLE_COMAPRATOR);
		sorter.setColumnComparator(MindMapNode.class,
				TableSorter.LEXICAL_COMPARATOR);
		sorter.setColumnComparator(IconsHolder.class,
				TableSorter.COMPARABLE_COMAPRATOR);
		// Sort by default by date.
		sorter.setSortingStatus(DATE_COLUMN, TableSorter.ASCENDING);
		//FIXME: Export of this list
		JScrollPane pane = new JScrollPane(timeTable);
		contentPane.add(pane, gb1);
		//		{
		//			GridBagConstraints gb2 = new GridBagConstraints();
		//			gb2.gridx = 0;
		//			gb2.gridy = 1;
		//			gb2.gridwidth = 4;
		//			gb2.fill = GridBagConstraints.HORIZONTAL;
		//			contentPane.add(getTimePanel(), gb2);
		//		}

		// restore prefrences:

		//Retrieve window size and column positions.		
		WindowConfigurationStorage storage = getController().getController().decorateDialog(dialog, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			//			 Disable auto resizing
			timeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			int column = 0;
			for (Iterator i = ((TimeWindowConfigurationStorageType) storage).getTimeWindowColumnSetting().iterator(); i.hasNext();) {
                TimeWindowColumnSettingType setting = (TimeWindowColumnSettingType) i.next();
                timeTable.getColumnModel().getColumn(column).setPreferredWidth(setting.getColumnWidth());
                sorter.setSortingStatus(column, setting.getColumnSorting());
                column++;
            }
		}
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * @param focussedRow
	 * @param selectedRows TODO
	 */
	private void gotoNodesAndClose(int focussedRow, int[] selectedRows) {
		if (focussedRow >= 0) {
			MindMapNode focussedNode = getMindMapNode(focussedRow);
//			getController().centerNode(focussedNode);
			Vector selectedNodes = new Vector();
            for (int i = 0; i < selectedRows.length; i++) {
				int row = selectedRows[i];
				selectedNodes.add(getMindMapNode(row));
			}
            getController().selectMultipleNodes(focussedNode, selectedNodes);
			disposeDialog();
		}
	}

	/**
     * @param focussedRow
     * @return
     */
    private MindMapNode getMindMapNode(int focussedRow) {
        MindMapNode selectedNode = ((NodeHolder) timeTable.getModel()
        		.getValueAt(focussedRow, NODE_TEXT_COLUMN)).node;
        return selectedNode;
    }

    /**
	 * Creates a table model for the new table and returns it.
	 */
	private DefaultTableModel updateModel() {
		MindMapNode node = (MindMapNode) getController().getMap().getRoot();
		DefaultTableModel model = new DefaultTableModel() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
			 */
			public Class getColumnClass(int arg0) {
				switch (arg0) {
				case DATE_COLUMN:
				case NODE_CREATED_COLUMN:
				case NODE_MODIFIED_COLUMN:
					return Date.class;
				case NODE_TEXT_COLUMN:
					return NodeHolder.class;
				case NODE_ICON_COLUMN:
					return IconsHolder.class;
				default:
					return Object.class;
				}
			}
		};

		model.addColumn(COLUMN_DATE);//FIXME: i18n
		model.addColumn(COLUMN_TEXT);
		model.addColumn(COLUMN_ICONS);
		model.addColumn(COLUMN_CREATED);
		model.addColumn(COLUMN_MODIFIED);
		model.setColumnIdentifiers(new Object[] { COLUMN_DATE, COLUMN_TEXT,
				COLUMN_ICONS, COLUMN_CREATED, COLUMN_MODIFIED });
		updateModel(model, node);
		return model;
	}

	private void updateModel(DefaultTableModel model, MindMapNode node) {
		ReminderHook hook = TimeManagement.getHook(node);
		Date date = null;
		if (hook != null) {
			date = new Date(hook.getRemindUserAt());
		}
		// show all nodes or only those with reminder:
		if (showAllNodes || hook != null) {
			model.addRow(new Object[] { date, new NodeHolder(node),
					new IconsHolder(node),
					node.getHistoryInformation().getCreatedAt(),
					node.getHistoryInformation().getLastModifiedAt() });
		}
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			updateModel(model, child);
		}
	}

	/**
	 * @return
	 */
	private JPanel getTimePanel() {
		if (timePanel == null) {
			timePanel = new JPanel();
			timePanel.setLayout(new GridBagLayout());
			//			{
			//				GridBagConstraints gb2 = new GridBagConstraints();
			//				gb2.gridx = 0;
			//				gb2.gridy = 0;
			//				gb2.fill = GridBagConstraints.HORIZONTAL;
			//				timePanel.add(new JLabel(
			//						getResourceString("plugins/TimeManagement.xml_hour")),
			//						gb2);
			//			}
		}
		return timePanel;
	}

	/**
	 *  
	 */
	private void disposeDialog() {
		// store window positions:

		try {
			TimeWindowConfigurationStorage storage = getController()
					.getActionXmlFactory()
					.createTimeWindowConfigurationStorage();
			for(int i = 0; i< timeTable.getColumnCount(); i++) {
				TimeWindowColumnSetting setting = getController()
						.getActionXmlFactory()
						.createTimeWindowColumnSetting();
				setting.setColumnWidth(timeTable.getColumnModel().getColumn(i).getWidth());
				setting.setColumnSorting(sorter.getSortingStatus(i));
				storage.getTimeWindowColumnSetting().add(setting);
			}
			getController().getController().storeDialogPositions(dialog, storage, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		dialog.setVisible(false);
		dialog.dispose();
	}

	static class DateRenderer extends DefaultTableCellRenderer {
		DateFormat formatter;

		public DateRenderer() {
			super();
		}

		public void setValue(Object value) {
			if (formatter == null) {
				formatter = DateFormat.getDateTimeInstance();
			}
			setText((value == null) ? "" : formatter.format(value));
		}
	}

	static class NodeRenderer extends DefaultTableCellRenderer {
		public NodeRenderer() {
			super();
		}

		public void setValue(Object value) {
			setText((value == null) ? "" : ((NodeHolder) value).node.getText());
		}
	}

	//* removes html in nodes before comparison.
	static class NodeHolder implements Comparable {
		private final MindMapNode node;

		/**
		 *  
		 */
		public NodeHolder(MindMapNode node) {
			this.node = node;
		}

		public int compareTo(Object compareToObject) {
			return toString().compareTo(compareToObject.toString());
		}

		public String toString() {
			String text = node.getText();
			if (text.toLowerCase().matches("^\\s*<html>")) {
				text = text.replaceAll("<[^>]*>", ""); // remove all html tags.
			}
			return text;
		}
	}

	static class IconsHolder implements Comparable {
		Vector icons = new Vector();

		private Vector iconNames;

		public IconsHolder(MindMapNode node) {
			icons.addAll(node.getIcons());
			// sorting the output.
			iconNames = new Vector();
			for (Iterator i = icons.iterator(); i.hasNext();) {
				MindIcon icon = (MindIcon) i.next();
				iconNames.add(icon.getName());
			}
			Collections.sort(iconNames);
		}

		public int compareTo(Object compareToObject) {
			return toString().compareTo(compareToObject.toString());
		}

		public Vector getIcons() {
			return icons;
		}

		/** Returns a sorted list of icon names. */
		public String toString() {
			String result = "";
			for (Iterator i = iconNames.iterator(); i.hasNext();) {
				String name = (String) i.next();
				result += name + " ";
			}
			return result;
		}
	}

	static class IconsRenderer extends DefaultTableCellRenderer {
		private final ModeController modeController;

		public IconsRenderer(ModeController controller) {
			super();
			modeController = controller;
		}

		public void setValue(Object value) {
			if (value instanceof IconsHolder) {
				IconsHolder iconsHolder = (IconsHolder) value;
				MultipleImage iconImages = new MultipleImage(1.0f);
				for (Iterator i = iconsHolder.getIcons().iterator(); i
						.hasNext();) {
					MindIcon icon = (MindIcon) i.next();
					iconImages
							.addImage(icon.getIcon());
				}
				if (iconImages.getImageCount() > 0) {
					setIcon(iconImages);
				} else {
					setIcon(null);
				}
			}
		}
	}
}