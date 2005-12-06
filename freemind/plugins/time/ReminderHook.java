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
 * Created on 06.02.2005
 */
/*$Id: ReminderHook.java,v 1.1.2.7.6.2 2005-12-06 19:47:30 dpolivaev Exp $*/
package plugins.time;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.XMLElement;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 *  
 */
public class ReminderHook extends PermanentNodeHookAdapter {

	private static final int CLOCK_INVISIBLE = 0;

	private static final int CLOCK_VISIBLE = 1;

	private static final int REMOVE_CLOCK = -1;

	private static final String REMINDUSERAT = "REMINDUSERAT";

	private long remindUserAt = 0;

	private Timer timer;

	private static ImageIcon clockIcon = null;

	private static ImageIcon bellIcon;

	private static ImageIcon flagIcon;

	//private Vector dateVector = new Vector();

	/**
	 *  
	 */
	public ReminderHook() {
		super();
	}

	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		HashMap hash = loadNameValuePairs(child);
		if (hash.containsKey(REMINDUSERAT)) {
			String remindAt = (String) hash.get(REMINDUSERAT);
			setRemindUserAt(new Long(remindAt).longValue());
		}

	}

	public void save(XMLElement xml) {
		super.save(xml);
		HashMap nameValuePairs = new HashMap();
		nameValuePairs.put(REMINDUSERAT, new Long(remindUserAt));
		saveNameValuePairs(nameValuePairs, xml);
	}

	public void shutdownMapHook() {
		getController().setToolTip(getNode(), getName(), null);
		if (timer != null) {
			timer.cancel();
		}
		displayState(REMOVE_CLOCK, getNode(), true);
		super.shutdownMapHook();
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if (remindUserAt == 0) {
			return;
		}
		if (timer == null) {
			scheduleTimer(node);
		}
	}

	/**
	 * @param node
	 */
	private void scheduleTimer(MindMapNode node) {
		scheduleTimer(node, new TimerBlinkTask(false));
		//		scheduleTimer(node, new CheckReminder(false));
	}

	private void scheduleTimer(MindMapNode node, TimerTask task) {
		timer = new Timer();
		Date date = new Date(remindUserAt);
		timer.schedule(task, date);
		Object[] messageArguments = { date };
		MessageFormat formatter = new MessageFormat(
				getResourceString("plugins/TimeManagement.xml_reminderNode_tooltip"));
		String message = formatter.format(messageArguments);

		getController().setToolTip(node, getName(), message);
		displayState(CLOCK_VISIBLE, getNode(), false);
	}

	private ImageIcon getClockIcon() {
		// icon
		if (clockIcon == null) {
			clockIcon = MindIcon.factory("clock").getIcon();
		}
		return clockIcon;
	}

	private ImageIcon getBellIcon() {
		// icon
		if (bellIcon == null) {
			bellIcon = MindIcon.factory("bell").getIcon();
		}
		return bellIcon;
	}

	private ImageIcon getFlagIcon() {
		// icon
		if (flagIcon == null) {
			flagIcon = MindIcon.factory("flag").getIcon();
		}
		return flagIcon;
	}

	protected class CheckReminder extends TimerTask {
		CheckReminder() {

		}

		/** TimerTask method to enable the selection after a given time. */
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// yes, the time is over:
					// select the node.
					getController().centerNode(getNode());
					// format message:
					Object[] messageArguments = { getNode().getShortText(
							getController()) };
					MessageFormat formatter = new MessageFormat(
							getResourceString("plugins/TimeManagement.xml_reminderNode_showNode"));
					String message = formatter.format(messageArguments);

					int result = JOptionPane.showConfirmDialog(getController()
							.getFrame().getJFrame(), message, "Freemind",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						setRemindUserAt(System.currentTimeMillis() + 10 * 60 * 1000);
						scheduleTimer(getNode());
						return;
					}
					nodeChanged(getNode());
					// remove the hook (suicide)
					getNode().removeHook(ReminderHook.this);
				}
			});
		}
	}

	public class TimerBlinkTask extends TimerTask {

		/**
		 * @param stateAdded
		 */
		public TimerBlinkTask(boolean stateAdded) {
			super();
			this.stateAdded = stateAdded;
		}

		private boolean stateAdded = false;

		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// time is over, we add the new icon until the user removes
					// the user removes the reminder.
					// 
					stateAdded = !stateAdded;
					setRemindUserAt(System.currentTimeMillis() + 3000); // 3
					// secs
					scheduleTimer(getNode(), new TimerBlinkTask(stateAdded));
					displayState(
							(stateAdded) ? CLOCK_VISIBLE : CLOCK_INVISIBLE,
							getNode(), true);

				}
			});
		}

	}

	private final String STATE_TOOLTIP = TimerBlinkTask.class.getName()
			+ "_STATE_";

	public String getStateKey() {
		return STATE_TOOLTIP + getNode().getObjectId(getController());
	}

	public void displayState(int stateAdded, MindMapNode node, boolean recurse) {
		ImageIcon icon = null;
		if (stateAdded == CLOCK_VISIBLE) {
			icon = getClockIcon();
		} else if (stateAdded == CLOCK_INVISIBLE) {
			if (node == getNode()) {
				icon = getBellIcon();
			} else {
				icon = getFlagIcon();
			}
		}
		node.setStateIcon(getStateKey(), icon);
		getController().nodeRefresh(node);
		if (recurse && !node.isRoot()) {
			displayState(stateAdded, node.getParentNode(), recurse);
		}
	}

	public long getRemindUserAt() {
		return remindUserAt;
	}

	public void setRemindUserAt(long remindUserAt) {
		this.remindUserAt = remindUserAt;
	}
}