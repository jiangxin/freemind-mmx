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
/*$Id: TimeManagement.java,v 1.1.2.3 2005-02-14 21:10:04 christianfoltin Exp $*/
package plugins.time;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.toedter.calendar.JCalendar;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.MindMapNode;

//FIXME: REminder: more than once. (later) 
//FIXME: Button shortcuts (difficult?)
//FIXME: make dummy plugin that stores data if plugin not present.

/**
 * @author foltin
 *  
 */
public class TimeManagement extends ModeControllerHookAdapter implements
        PropertyChangeListener, ActionListener {

    public final String REMINDER_HOOK_NAME = "plugins/TimeManagementReminder.xml";

    private static Date lastDate = null;

    private JCalendar calendar;

    private JDialog dialog;

    private JPanel timePanel;

    private JTextField hourField;

    private JTextField minuteField;

    public void startupMapHook() {
        super.startupMapHook();
        dialog = new JDialog(getController().getFrame().getJFrame(), true /* modal */);
        dialog.setTitle(getResourceString("plugins/TimeManagement.xml_WindowTitle"));
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

        calendar = new JCalendar();
        if (lastDate != null) {
            calendar.setDate(lastDate);
        }
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
                    disposeDialog();
                    for (Iterator i = getController().getSelecteds().iterator(); i
                            .hasNext();) {
                        MindMapNode element = (MindMapNode) i.next();
                        DateFormat df = DateFormat
                                .getDateInstance(DateFormat.SHORT);
                        String dateAsString = df.format(getCalendarDate());
                        getController().setNodeText(element,
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
            JButton cancelButton = new JButton(
                    getResourceString("plugins/TimeManagement.xml_cancelButton"));
            cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    disposeDialog();
                }
            });
            contentPane.add(cancelButton, gb2);
        }
        dialog.pack();
        dialog.setVisible(true);
        calendar.getDayChooser().setFocus();
    }

    /**
     * @return
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
                timePanel.add(new JLabel(getResourceString("plugins/TimeManagement.xml_hour")), gb2);
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
                timePanel.add(new JLabel(getResourceString("plugins/TimeManagement.xml_minute")), gb2);
            }
            {
                GridBagConstraints gb2 = new GridBagConstraints();
                gb2.gridx = 3;
                gb2.gridy = 0;
                gb2.fill = GridBagConstraints.HORIZONTAL;
                minuteField = new JTextField(2);
                minuteField.setText(new Integer(calendar.getCalendar().get(
                        Calendar.MINUTE)).toString());
                timePanel.add(minuteField, gb2);
            }

        }
        return timePanel;
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("day")) {
        }
    }

    private final class RemoveReminders implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (Iterator i = getController().getSelecteds().iterator(); i
                    .hasNext();) {
                MindMapNode node = (MindMapNode) i.next();

                ReminderHook alreadyPresentHook = getHook(node);
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
        for (Iterator i = getController().getSelecteds().iterator(); i
                .hasNext();) {
            MindMapNode node = (MindMapNode) i.next();

            ReminderHook alreadyPresentHook = getHook(node);
            if (alreadyPresentHook != null) {
                // already present:
                Object[] messageArguments = {
                        new Date(alreadyPresentHook.getRemindUserAt()), date };
                MessageFormat formatter = new MessageFormat(
                        getResourceString("plugins/TimeManagement.xml_reminderNode_onlyOneDate"));
                String message = formatter.format(messageArguments);
                logger.info(messageArguments.length + ", " + message);
                int result = JOptionPane.showConfirmDialog(getController()
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
            ReminderHook rh = getHook(node);
            if (rh == null) {
                throw new IllegalArgumentException(
                        "hook not found although it is present!!");
            }
            rh.setRemindUserAt(date.getTime());
            node.invokeHook(rh);
            getController().nodeChanged(node);
        }
        disposeDialog();
    }

    /**
     * @param node
     */
    private void addHook(MindMapNode node) {
        // add the hook:
        List selected = Arrays.asList(new MindMapNode[] { node });
        getController().addHook(node, selected, REMINDER_HOOK_NAME);
    }

    /**
     * @param node
     * @return
     */
    private ReminderHook getHook(MindMapNode node) {
        for (Iterator j = node.getActivatedHooks().iterator(); j.hasNext();) {
            PermanentNodeHook element = (PermanentNodeHook) j.next();
            if (element instanceof ReminderHook) {
                return (ReminderHook) element;
            }
        }
        return null;
    }

    /**
     *  
     */
    private void disposeDialog() {
        dialog.setVisible(false);
        dialog.dispose();
        lastDate = getCalendarDate();
    }

    /**
     * @return
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
}