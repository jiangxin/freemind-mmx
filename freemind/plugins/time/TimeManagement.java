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
/*$Id: TimeManagement.java,v 1.1.2.1 2005-02-06 22:15:12 christianfoltin Exp $*/
package plugins.time;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.toedter.calendar.JCalendar;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

//FIXME: REminder: more than once. 
//FIXME: Dialog shortcuts.
//FIXME: more than one tooltip.
//FIXME: make dummy plugin that stores data if plugin not present.

/**
 * @author foltin
 *  
 */
public class TimeManagement extends ModeControllerHookAdapter implements
		PropertyChangeListener, ActionListener {

	private static Date lastDate = null;
	
	private JCalendar calendar;
	private JDialog dialog;

	public void startupMapHook() {
		super.startupMapHook();
		dialog = new JDialog(getController().getFrame().getJFrame(), true /* modal*/);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Action action = new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				disposeDialog();
			}};
		action.putValue(Action.NAME, "end_dialog");
		//		 Register keystroke
	    dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	        KeyStroke.getKeyStroke("ESCAPE"), action.getValue(Action.NAME));
	    
	    // Register action
	    dialog.getRootPane().getActionMap().put(action.getValue(Action.NAME), action);
	 
		calendar = new JCalendar();
		if(lastDate != null) {
			calendar.setDate(lastDate);
		}
		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx=0;
		gb1.gridwidth=3;
		gb1.fill = GridBagConstraints.BOTH;
		gb1.gridy=0;
		calendar.getDayChooser().addPropertyChangeListener(this);
		contentPane.add(calendar, gb1);
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 0;
			gb2.gridy = 1;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton appendButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_appendButton"));
			appendButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {
					disposeDialog();
					for (Iterator i = getController().getSelecteds().iterator(); i.hasNext();) {
						MindMapNode element = (MindMapNode) i.next();
						DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
						String dateAsString = df.format(calendar.getDate());
						getController().setNodeText(element, element.getText()+" "+dateAsString);
					}
					
				}});
			contentPane.add(appendButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 1;
			gb2.gridy = 1;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton reminderButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_reminderButton"));
			reminderButton.setToolTipText(getResourceString("plugins/TimeManagement.xml_reminderButton_tooltip"));
			reminderButton.addActionListener(this);
			contentPane.add(reminderButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 2;
			gb2.gridy = 1;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton cancelButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_cancelButton"));
			cancelButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {
					disposeDialog();
				}});
			contentPane.add(cancelButton, gb2);
		}
		dialog.pack();
		dialog.setVisible(true);
		calendar.getDayChooser().setFocus();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("day")) {
		} 
	}

	public void actionPerformed(ActionEvent arg0) {
		// add permanent node hook to the nodes and this hook checks permanently.
		for (Iterator i = getController().getSelecteds().iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			Date date = calendar.getDate();
			ReminderHook rh;
			if (getController() instanceof MindMapController) {
				MindMapController mc = (MindMapController) getController();
				rh = (ReminderHook) mc.createNodeHook("plugins/TimeManagementReminder.xml", node, getController().getMap());
				rh.setRemindUserAt(date.getTime());
				node.invokeHook(rh);
				getController().nodeChanged(node);
			}
		}
		disposeDialog();
	}

	/**
	 * 
	 */
	private void disposeDialog() {
		dialog.setVisible(false);
		dialog.dispose();
		lastDate = calendar.getDate();
	}
}