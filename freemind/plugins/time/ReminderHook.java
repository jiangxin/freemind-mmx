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
/*$Id: ReminderHook.java,v 1.1.2.3 2005-02-13 22:39:57 christianfoltin Exp $*/
package plugins.time;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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

    private static final String REMINDUSERAT = "REMINDUSERAT";

    private long remindUserAt = 0;

    private Timer timer;

    private static MindIcon clockIcon=null;

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
            setRemindUserAt(new Long(remindAt)
                    .longValue());
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
        getNode().removeStateIcon(getName());
        getController().nodeRefresh(getNode());
        if (timer != null) {
            timer.cancel();
        }
        super.shutdownMapHook();
    }

    public void invoke(MindMapNode node) {
        super.invoke(node);
        if (remindUserAt == 0) {
            return;
        }
        if (timer == null) {
            timer = new Timer();
            Date date = new Date(remindUserAt);
            timer.schedule(new CheckReminder(), date);
            Object[] messageArguments = { date };
            MessageFormat formatter = new MessageFormat(
                    getResourceString("plugins/TimeManagement.xml_reminderNode_tooltip"));
            String message = formatter.format(messageArguments);

            getController().setToolTip(node, getName(), message);
            // icon
            if (clockIcon == null) {
                clockIcon = new MindIcon("clock");
            }
            node.addStateIcon(getName(), clockIcon);
            getController().nodeRefresh(node);
        }
    }

    protected class CheckReminder extends TimerTask {
        CheckReminder() {

        }

        /** TimerTask method to enable the selection after a given time. */
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // yes, the time is over:
                    getController().displayNode(getNode());

                    int result = JOptionPane
                            .showConfirmDialog(
                                    getController().getFrame().getJFrame(),
                                    getResourceString("plugins/TimeManagement.xml_reminderNode_showNode"),
                                    "Freemind", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        setRemindUserAt(System.currentTimeMillis() + 10 * 60 * 1000);
                        timer.schedule(CheckReminder.this, new Date(
                                getRemindUserAt()));
                    }
                    nodeChanged(getNode());
                    // remove the hook (suicide)
                    getNode().removeHook(ReminderHook.this);
                }
            });
        }
    }

    public long getRemindUserAt() {
        return remindUserAt;
    }

    public void setRemindUserAt(long remindUserAt) {
        this.remindUserAt = remindUserAt;
    }
}