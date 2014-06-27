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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 03.05.2014
 */
public abstract class SocketMaster extends SocketBasics {

	static final class SessionData {
		boolean mLockEnabled = false;
		String mLockId;
		long mLockedAt;
		String mLockUserName;
		Vector<ServerCommunication> mConnections = new Vector<ServerCommunication>();
		public String mLockMutex = new String();
	}
	
	protected HashMap<ExtendedMapFeedback, SessionData> mSessions = new HashMap<ExtendedMapFeedback, SocketMaster.SessionData>();
	protected int mPort;
	protected HashMap<String, ExtendedMapFeedback> mFileMap = new HashMap<String, ExtendedMapFeedback>();

	
	public synchronized void removeConnection(ServerCommunication client) {
		synchronized (mSessions) {
			ExtendedMapFeedback controller = client.getController();
			if(mSessions.containsKey(controller)) {
				mSessions.get(controller).mConnections.remove(client);
			}
		}
		// correct the map title, as we probably don't have clients anymore
		setTitle();
	}
	
	/**
	 * Updates the title of the dialog or display, is called, when changes occur.
	 */
	protected abstract void setTitle();
	
	/**
	 * @return the path to where all maps are stored.
	 */
	protected abstract File getBaseFile();

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
	protected String lock(String pUserName, ExtendedMapFeedback pController) throws UnableToGetLockException,
			InterruptedException {
		SessionData sessionData = getSessionData(pController);
		synchronized (sessionData.mLockMutex) {
			if (sessionData.mLockEnabled) {
				throw new UnableToGetLockException();
			}
			sessionData.mLockEnabled = true;
			String lockId = "Lock_" + Math.random();
			sessionData.mLockId = lockId;
			sessionData.mLockedAt = System.currentTimeMillis();
			sessionData.mLockUserName = pUserName;
			logger.info("New lock " + lockId + " by " + sessionData.mLockUserName);
			return lockId;
		}
	}

	/**
	 * @param pController
	 */
	protected SessionData getSessionData(ExtendedMapFeedback pController) {
		if(mSessions.containsKey(pController)) {
			return mSessions.get(pController);
		}
		throw new IllegalArgumentException("Session for " + pController + " not present.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.SocketBasics#sendCommand(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	protected void broadcastCommand(String pDoAction, String pUndoAction,
			String pLockId, ExtendedMapFeedback pController) throws Exception {
		SessionData sessionData = getSessionData(pController);
		synchronized (sessionData.mConnections) {
			for (int i = 0; i < sessionData.mConnections.size(); i++) {
				((ServerCommunication) sessionData.mConnections.elementAt(i)).sendCommand(
						pDoAction, pUndoAction, pLockId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#unlock()
	 */
	protected void unlock(ExtendedMapFeedback pController) {
		SessionData sessionData = getSessionData(pController);
		synchronized (sessionData.mLockMutex) {
			if (!sessionData.mLockEnabled) {
				throw new IllegalStateException();
			}
			logger.fine("Release lock " + sessionData.mLockId + " held by " + sessionData.mLockUserName);
			clearLock(pController);
		}
	}

	public void clearLock(ExtendedMapFeedback pController) {
		SessionData sessionData = getSessionData(pController);
		sessionData.mLockEnabled = false;
		sessionData.mLockId = "none";
		sessionData.mLockUserName = null;
	}

	public String getLockId(ExtendedMapFeedback pController) {
		SessionData sessionData = getSessionData(pController);
		synchronized (sessionData.mLockMutex) {
			if (!sessionData.mLockEnabled) {
				throw new IllegalStateException();
			}
			return sessionData.mLockId;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getUsers()
	 */
	public String getUsers(ExtendedMapFeedback pController) {
		SessionData sessionData = getSessionData(pController);
		StringBuffer users = new StringBuffer(Tools.getUserName());
		synchronized (sessionData.mConnections) {
			for (int i = 0; i < sessionData.mConnections.size(); i++) {
				users.append(',');
				users.append(' ');
				users.append(sessionData.mConnections.elementAt(i).getName());
			}
		}
		return users.toString();
	}
	
	protected void executeTransaction(final ActionPair pair, ExtendedMapFeedback pController) {
		SessionData sessionData = getSessionData(pController);
		mFilterEnabled = false;
		try {
			pController.doTransaction("update", pair);
		} finally {
			mFilterEnabled = true;
		}
	}
	
	public CollaborationUserInformation getMasterInformation(ExtendedMapFeedback pController) {
		CollaborationUserInformation userInfo = new CollaborationUserInformation();
		userInfo.setUserIds(getUsers(pController));
		userInfo.setMasterHostname(Tools.getHostName());
		userInfo.setMasterPort(getPort());
		userInfo.setMasterIp(Tools.getHostIpAsString());
		return userInfo;
	}

	/**
	 * @return the fileMap
	 */
	public HashMap<String, ExtendedMapFeedback> getFileMap() {
		return mFileMap;
	}

	/**
	 * @param pServerCommunication
	 * @param pController
	 */
	public  void addConnection(ServerCommunication pServerCommunication,
			ExtendedMapFeedback pController) {
		synchronized (mSessions) {
			mSessions.get(pController).mConnections.addElement(pServerCommunication);
		}		
	}

	public ExtendedMapFeedback createMapOnServer(String fileName,
			Tools.ReaderCreator readerCreator, File pFile) throws IOException {
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();

		MindMapMapModel map = new MindMapMapModel(mapFeedback);
		mapFeedback.setMap(map); 
		MindMapNode root = map.loadTree(readerCreator,
				MapAdapter.sDontAskInstance);
		map.setRoot(root);
		mapFeedback.invokeHooksRecursively(root, map);
		mapFeedback.getActionRegistry().registerFilter(this);
		map.setFile(pFile);
		
		addSession(fileName, mapFeedback);
		return mapFeedback;
	}

	protected void addSession(String fileName,
			ExtendedMapFeedback mapFeedback) {
		synchronized (mSessions) {
			mFileMap.put(fileName, mapFeedback);
			mSessions.put(mapFeedback, new SessionData());
		}
	}



	
}