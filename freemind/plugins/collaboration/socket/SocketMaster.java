/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

package plugins.collaboration.socket;

import java.util.Vector;

import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.main.Tools;

/**
 * @author foltin
 * @date 03.05.2014
 */
public abstract class SocketMaster extends SocketBasics {

	Vector mConnections = new Vector();
	protected boolean mLockEnabled = false;
	protected String mLockMutex = "";
	protected int mPort;
	protected String mLockId;
	protected long mLockedAt;
	protected String mLockUserName;

	
	public synchronized void removeConnection(ServerCommunication client) {
		synchronized (mConnections) {
			mConnections.remove(client);
		}
		// correct the map title, as we probably don't have clients anymore
		setTitle();
	}
	
	/**
	 * Updates the title of the dialog or display, is called, when changes occur.
	 */
	protected abstract void setTitle();

	public Integer getRole() {
		return ROLE_MASTER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getPort()
	 */
	public int getPort() {
		return mPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#lock()
	 */
	protected String lock(String pUserName) throws UnableToGetLockException,
			InterruptedException {
		synchronized (mLockMutex) {
			if (mLockEnabled) {
				throw new UnableToGetLockException();
			}
			mLockEnabled = true;
			String lockId = "Lock_" + Math.random();
			mLockId = lockId;
			mLockedAt = System.currentTimeMillis();
			mLockUserName = pUserName;
			logger.info("New lock " + lockId + " by " + mLockUserName);
			return lockId;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.SocketBasics#sendCommand(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	protected void broadcastCommand(String pDoAction, String pUndoAction,
			String pLockId) throws Exception {
		synchronized (mConnections) {
			for (int i = 0; i < mConnections.size(); i++) {
				((ServerCommunication) mConnections.elementAt(i)).sendCommand(
						pDoAction, pUndoAction, pLockId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#unlock()
	 */
	protected void unlock() {
		synchronized (mLockMutex) {
			if (!mLockEnabled) {
				throw new IllegalStateException();
			}
			logger.fine("Release lock " + mLockId + " held by " + mLockUserName);
			clearLock();
		}
	}

	public void clearLock() {
		mLockEnabled = false;
		mLockId = "none";
		mLockUserName = null;
	}

	public String getLockId() {
		synchronized (mLockMutex) {
			if (!mLockEnabled) {
				throw new IllegalStateException();
			}
			return mLockId;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getUsers()
	 */
	public String getUsers() {
		StringBuffer users = new StringBuffer(Tools.getUserName());
		synchronized (mConnections) {
			for (int i = 0; i < mConnections.size(); i++) {
				users.append(',');
				users.append(' ');
				users.append(((ServerCommunication) mConnections.elementAt(i))
						.getName());
			}
		}
		return users.toString();
	}

	public CollaborationUserInformation getMasterInformation() {
		CollaborationUserInformation userInfo = new CollaborationUserInformation();
		userInfo.setUserIds(getUsers());
		userInfo.setMasterHostname(Tools.getHostName());
		userInfo.setMasterPort(getPort());
		userInfo.setMasterIp(Tools.getHostIpAsString());
		return userInfo;
	}
	
}