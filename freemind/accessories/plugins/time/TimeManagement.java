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
/* $Id: TimeManagement.java,v 1.1.2.7 2008/03/26 21:25:35 christianfoltin Exp $ */
package accessories.plugins.time;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.common.plugins.ReminderHookBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

//FIXME: REminder: more than once. (later)
//FIXME: Button shortcuts (difficult?)
//FIXME: Only one open dialog possible.

/**
 * @author foltin
 *
 */
public class TimeManagement extends MindMapHookAdapter implements
		PropertyChangeListener, ActionListener, MapModuleChangeObserver {

	public final static String REMINDER_HOOK_NAME = "plugins/TimeManagementReminder.xml";

	private static Date lastDate = null;

	private JTripleCalendar calendar;

	private JDialog dialog;

	private JPanel timePanel;

	private JTextField hourField;

	private JTextField minuteField;

	private MindMapController mController;
	
	private static TimeManagement sCurrentlyOpenTimeManagement = null;

	public void startupMapHook() {
		super.startupMapHook();
		if(sCurrentlyOpenTimeManagement != null) {
			sCurrentlyOpenTimeManagement.dialog.getContentPane().setVisible(true);
			return;
		}
		sCurrentlyOpenTimeManagement = this;
		this.mController = getMindMapController();
		mController.getController().getMapModuleManager().addListener(this);
		dialog = new JDialog(mController.getFrame().getJFrame(), false /* not modal */);
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
		Tools.addEscapeActionToDialog(dialog, action);

		calendar = new JTripleCalendar();
		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 0;
		gb1.gridwidth = 4;
		gb1.fill = GridBagConstraints.BOTH;
		gb1.gridy = 0;
		calendar.getDayChooser().addPropertyChangeListener(this);
		contentPane.add(calendar, gb1);
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 0;
			gb2.gridy = 1;
			gb2.gridwidth = 4;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			contentPane.add(getTimePanel(), gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 0;
			gb2.gridy = 2;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton appendButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_appendButton"));
			appendButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
//					disposeDialog();
					for (Iterator i = mController.getSelecteds().iterator(); i
							.hasNext();) {
						MindMapNode element = (MindMapNode) i.next();
						DateFormat df = DateFormat
								.getDateInstance(DateFormat.SHORT);
						String dateAsString = df.format(getCalendarDate());
                        mController.setNodeText(element,
								element.getText() + " " + dateAsString);
					}

				}
			});
			contentPane.add(appendButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 1;
			gb2.gridy = 2;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton reminderButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_reminderButton"));
			reminderButton
					.setToolTipText(getResourceString("plugins/TimeManagement.xml_reminderButton_tooltip"));

			reminderButton.addActionListener(this);
			contentPane.add(reminderButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 2;
			gb2.gridy = 2;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton reminderButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_removeReminderButton"));
			reminderButton
					.setToolTipText(getResourceString("plugins/TimeManagement.xml_removeReminderButton_tooltip"));
			reminderButton.addActionListener(new RemoveReminders());
			contentPane.add(reminderButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 3;
			gb2.gridy = 2;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton todayButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_todayButton"));
			todayButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					calendar.setCalendar(Calendar.getInstance());
				}
			});
			contentPane.add(todayButton, gb2);
		}
		{
			GridBagConstraints gb2 = new GridBagConstraints();
			gb2.gridx = 4;
			gb2.gridy = 2;
			gb2.fill = GridBagConstraints.HORIZONTAL;
			JButton cancelButton = new JButton(
					getResourceString("plugins/TimeManagement.xml_closeButton"));
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					disposeDialog();
				}
			});
			contentPane.add(cancelButton, gb2);
		}
		if (lastDate != null) {
			logger.info("Setting date to " + lastDate);
			calendar.setDate(lastDate);
		}
		dialog.pack();
		calendar.getDayChooser().setFocus();
		dialog.setVisible(true);
	}

	/**
	 */
	private JPanel getTimePanel() {
		if (timePanel == null) {
			timePanel = new JPanel();
			timePanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 0;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				timePanel.add(new JLabel(
						getResourceString("plugins/TimeManagement.xml_hour")),
						gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 1;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				hourField = new JTextField(2);
				hourField.setText(new Integer(calendar.getCalendar().get(
						Calendar.HOUR_OF_DAY)).toString());
				timePanel.add(hourField, gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 2;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				timePanel
						.add(
								new JLabel(
										getResourceString("plugins/TimeManagement.xml_minute")),
								gb2);
			}
			{
				GridBagConstraints gb2 = new GridBagConstraints();
				gb2.gridx = 3;
				gb2.gridy = 0;
				gb2.fill = GridBagConstraints.HORIZONTAL;
				minuteField = new JTextField(2);
				String minuteString = new Integer(calendar.getCalendar().get(
						Calendar.MINUTE)).toString();
				// padding with "0"
				if (minuteString.length() < 2) {
					minuteString = "0" + minuteString;
				}
				minuteField.setText(minuteString);
				timePanel.add(minuteField, gb2);
			}

		}
		return timePanel;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(JDayChooser.DAY_PROPERTY)) {
		}
	}

	private final class RemoveReminders implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (Iterator i = mController.getSelecteds().iterator(); i
					.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();

				ReminderHookBase alreadyPresentHook = TimeManagementOrganizer.getHook(node);
				if (alreadyPresentHook != null) {
					addHook(node); // means remove hook, as it is already
					// present.
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		Date date = getCalendarDate();
		// add permanent node hook to the nodes and this hook checks
		// permanently.
		for (Iterator i = mController.getSelecteds().iterator(); i
				.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();

			ReminderHookBase alreadyPresentHook = TimeManagementOrganizer.getHook(node);
			if (alreadyPresentHook != null) {
				// already present:
				Object[] messageArguments = {
						new Date(alreadyPresentHook.getRemindUserAt()), date };
				MessageFormat formatter = new MessageFormat(
						getResourceString("plugins/TimeManagement.xml_reminderNode_onlyOneDate"));
				String message = formatter.format(messageArguments);
				logger.info(messageArguments.length + ", " + message);
				int result = JOptionPane.showConfirmDialog(mController
						.getFrame().getJFrame(), message, "FreeMind",
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION)
					return;
				//here, the old has to be removed and the new one installed.
				addHook(node); // means remove hook, as it is already present.

			}
			List selected;
			addHook(node);
			PermanentNodeHook element;
			ReminderHookBase rh = TimeManagementOrganizer.getHook(node);
			if (rh == null) {
				throw new IllegalArgumentException(
						"hook not found although it is present!!");
			}
			rh.setRemindUserAt(date.getTime());
			node.invokeHook(rh);
			mController.nodeChanged(node);
		}
//		disposeDialog();
	}

	/**
	 */
	private void addHook(MindMapNode node) {
		// add the hook:
		List selected = Arrays.asList(new MindMapNode[] { node });
        mController.addHook(node, selected, REMINDER_HOOK_NAME);
	}

	/**
	 *
	 */
	private void disposeDialog() {
		dialog.setVisible(false);
		dialog.dispose();
		lastDate = getCalendarDate();
		sCurrentlyOpenTimeManagement = null;
	}

	/**
	 */
	private Date getCalendarDate() {
		Calendar cal = calendar.getCalendar();
		try {
			int value = 0;
			value = Integer.parseInt(hourField.getText());
			cal.set(Calendar.HOUR_OF_DAY, value);
			value = Integer.parseInt(minuteField.getText());
			cal.set(Calendar.MINUTE, value);
			cal.set(Calendar.SECOND, 0);
		} catch (Exception e) {
		}
		return cal.getTime();
	}

	public void afterMapClose(MapModule oldMapModule, Mode oldMode) {
	}

	public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
	}

	public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		mController.getController().getMapModuleManager().removeListener(this);
		disposeDialog();
	}

	public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
			Mode oldMode, MapModule newMapModule, Mode newMode) {
		return true;
	}

	public void numberOfOpenMapInformation(int number) {
	}
}
