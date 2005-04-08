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
/*$Id: TimeList.java,v 1.1.2.1 2005-04-08 21:37:30 christianfoltin Exp $*/
package plugins.time;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.Iterator;

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

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;


/**
 * @author foltin
 *  
 */
public class TimeList extends ModeControllerHookAdapter {

	private JDialog dialog;

	private JPanel timePanel;

	private JTable timeTable;

	private DefaultTableModel timeTableModel;

	private plugins.time.TableSorter sorter;

	private DateRenderer dateRenderer;

	private NodeRenderer nodeRenderer;

	public void startupMapHook() {
		super.startupMapHook();
		dialog = new JDialog(getController().getFrame().getJFrame(), true /* modal */);
		dialog
				.setTitle(getResourceString("plugins/TimeManagement.xml_WindowTitle"));
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
		gbl.columnWeights = new double[]{1.0f};
	    gbl.rowWeights = new double[]{1.0f};
		contentPane.setLayout(gbl);
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridy = 0;
		gb1.fill = GridBagConstraints.BOTH;
		dateRenderer = new DateRenderer();
		nodeRenderer = new NodeRenderer();
		timeTable = new JTable(){
		    public TableCellRenderer getCellRenderer(int row, int column) {
		        if ((column == 0)) {
		            return dateRenderer;
		        } else if (column == 1) {
		        	   return nodeRenderer;
		        }
		        // else...
		        return super.getCellRenderer(row, column);
		    }
		    public boolean isCellEditable(int rowIndex, int vColIndex) {
	            return false;
	        }
		    protected void processKeyEvent(KeyEvent e) {
		        if (e.getKeyCode()==KeyEvent.VK_ENTER) {
		          EventListener[] el = super.getListeners(KeyListener.class);
		          if(e.getID()!=KeyEvent.KEY_RELEASED)
		          	return;
		          for (int i=0; i<el.length; i++) {
		            KeyListener kl = (KeyListener)el[ i];
		            kl.keyReleased(e);
		          }
		          return;
		        }
		        super.processKeyEvent(e);
		      }

		};
		timeTable.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent arg0) {
			}

			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					disposeDialog();
				}
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					
					int selectedRow = timeTable.getSelectedRow();
					if(selectedRow >= 0) {
						MindMapNode selectedNode = (MindMapNode) timeTable.getModel().getValueAt(selectedRow, 1);
						getController().displayNode(selectedNode);
					}
					disposeDialog();
				}
			}});
		DefaultTableModel model = new DefaultTableModel(){
			/* (non-Javadoc)
			 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
			 */
			public Class getColumnClass(int arg0) {
				switch(arg0) {
					case 0: 
						return Date.class;
					case 1: 
						return MindMapNode.class;
					default:
						return Object.class;
				}
			}
		};

		timeTableModel = model;
		sorter = new TableSorter(timeTableModel);
		timeTable.setModel(sorter);
		
		sorter.setTableHeader(timeTable.getTableHeader());
		sorter.setColumnComparator(Date.class, TableSorter.COMPARABLE_COMAPRATOR);
		sorter.setColumnComparator(MindMapNode.class, TableSorter.LEXICAL_COMPARATOR);
		updateModel();
		//FIXME: Export of this list
		//FIXME: Sort by default by date.
		//FIXME: Internationalization.
		
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

	    
	    dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * @param timeListModel2
	 * @param node
	 * @return
	 */
	private DefaultTableModel updateModel() {
		MindMapNode node = (MindMapNode) getController().getMap().getRoot();
		DefaultTableModel model = timeTableModel;
		model.addColumn("Date");//FIXME: i18n
		model.addColumn("Text");
		model.setColumnIdentifiers(new Object[]{"Date", "Text"});
		updateModel(model, node);
		return model;
	}

	private void updateModel(DefaultTableModel model, MindMapNode node){
		ReminderHook hook = TimeManagement.getHook(node);
		if(hook != null){
			Date date = new Date(hook.getRemindUserAt());
			model.addRow(new Object[]{date, node});
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
		dialog.setVisible(false);
		dialog.dispose();
	}
	static class DateRenderer extends DefaultTableCellRenderer {
	    DateFormat formatter;
	    public DateRenderer() { super(); }

	    public void setValue(Object value) {
	        if (formatter==null) {
	            formatter = DateFormat.getDateInstance();
	        }
	        setText((value == null) ? "" : formatter.format(value));
	    }
	}
	static class NodeRenderer extends DefaultTableCellRenderer {
	    public NodeRenderer() { super(); }

	    public void setValue(Object value) {
	        setText((value == null) ? "" : ((MindMapNode)value).getText());
	    }
	}
}