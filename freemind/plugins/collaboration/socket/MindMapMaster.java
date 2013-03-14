/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 28.12.2008
 */


package plugins.collaboration.socket;

import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import freemind.common.NumberProperty;
import freemind.common.StringProperty;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.extensions.DontSaveMarker;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class MindMapMaster extends SocketBasics implements PermanentNodeHook,
		DontSaveMarker {

	/**
	 * 
	 */
	public static final int SOCKET_TIMEOUT_IN_MILLIES = 500;
	MasterThread mListener = null;
	ServerSocket mServer;
	Vector mConnections = new Vector();
	protected boolean mLockEnabled = false;
	private String mLockMutex = "";
	private int mPort;
	private String mLockId;
	private long mLockedAt;
	private String mLockUserName;
	private boolean mMasterStarted;

	private class MasterThread extends TerminateableThread {

		private static final long TIME_BETWEEN_USER_INFORMATION_IN_MILLIES = 5000;
		private static final long TIME_FOR_ORPHANED_LOCK = 5000;
		private long mLastTimeUserInformationSent = 0;

		/**
		 * @param pName
		 */
		public MasterThread() {
			super("Master");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see plugins.collaboration.socket.TerminateableThread#processAction()
		 */
		public boolean processAction() throws Exception {
			try {
				logger.finest("Waiting for message");
				Socket client = mServer.accept();
				logger.info("Received new client.");
				client.setSoTimeout(SOCKET_TIMEOUT_IN_MILLIES);
				ServerCommunication c = new ServerCommunication(
						MindMapMaster.this, client, getMindMapController());
				c.start();
				synchronized (mConnections) {
					mConnections.addElement(c);
				}
			} catch (SocketTimeoutException e) {
			}
			final long now = System.currentTimeMillis();
			if (now - mLastTimeUserInformationSent > TIME_BETWEEN_USER_INFORMATION_IN_MILLIES) {
				mLastTimeUserInformationSent = now;
				CollaborationUserInformation userInfo = getMasterInformation();
				synchronized (mConnections) {
					for (int i = 0; i < mConnections.size(); i++) {
						try {
							final ServerCommunication connection = (ServerCommunication) mConnections
									.elementAt(i);
							/* to each server, the IP address is chosen that belongs to this connection.
							 * E.g. if the connection is routed over one of several network interfaces,
							 * the address of this interface is reported.
							 */
							userInfo.setMasterIp(connection.getIpToSocket());
							connection.send(userInfo);
						} catch (Exception e) {
							freemind.main.Resources.getInstance().logException(
									e);
						}
					}
				}
			}
			// timeout such that lock can't be held forever
			synchronized (mLockMutex) {
				if (mLockEnabled && now - mLockedAt > TIME_FOR_ORPHANED_LOCK) {
					logger.warning("Release lock " + mLockId + " held by "
							+ mLockUserName);
					clearLock();
				}
			}
			return true;
		}

	}

	public synchronized void removeConnection(ServerCommunication client) {
		synchronized (mConnections) {
			mConnections.remove(client);
		}
		// correct the map title, as we probably don't have clients anymore
		setTitle();
	}

	protected void setTitle() {
		getMindMapController().getController().setTitle();
	}

	public void startupMapHook() {
		super.startupMapHook();
		// Restart check, as the startup command is given, even if the mindmap
		// is changed via
		// the tab bar. So, this method must be idempotent...
		if (mMasterStarted) {
			// we were already here, so
			return;
		}
		MindMapController controller = getMindMapController();
		final StringProperty passwordProperty = new StringProperty(
				PASSWORD_DESCRIPTION, PASSWORD);
		final StringProperty passwordProperty2 = new StringProperty(
				PASSWORD_VERIFICATION_DESCRIPTION, PASSWORD_VERIFICATION);
		// StringProperty bindProperty = new StringProperty(
		// "IP address of the local machine, or 0.0.0.0 if ", "Host");
		final NumberProperty portProperty = getPortProperty();
		Vector controls = new Vector();
		controls.add(passwordProperty);
		controls.add(passwordProperty2);
		// controls.add(bindProperty);
		controls.add(portProperty);
		FormDialog dialog = new FormDialog(controller);
		dialog.setUp(controls, new FormDialogValidator() {
			public boolean isValid() {
				logger.finest("Output valid?");
				return Tools.safeEquals(passwordProperty.getValue(),
						passwordProperty2.getValue());
			}
		});
		if (!dialog.isSuccess()) {
			switchMeOff();
			return;
		}
		/* Store port value in preferences. */
		setPortProperty(portProperty);
		mPassword = passwordProperty.getValue();
		// start server:
		logger.info("Start server...");
		mMasterStarted = true;
		try {
			mPort = getPortProperty().getIntValue();
			mServer = new ServerSocket(mPort);
			mServer.setSoTimeout(SOCKET_TIMEOUT_IN_MILLIES);
			mListener = new MasterThread();
			mListener.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			controller.getController().errorMessage(
					Resources.getInstance().format(
							SOCKET_CREATION_EXCEPTION_MESSAGE,
							new Object[] { portProperty.getValue(),
									e.getMessage() }));
			switchMeOff();
			return;
		}
		registerFilter();
		logger.info("Starting server. Done.");
		setTitle();
	}

	public void switchMeOff() {
		final MindMapController mindMapController = getMindMapController();
		// this is not nice, but in the starting phase of the hook, it can't be switched off.
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				togglePermanentHook(mindMapController, MASTER_HOOK_LABEL);
				
			}});
	}

	public void loadFrom(XMLElement pChild) {
		// this plugin should not be saved.
	}

	public void save(XMLElement pXml) {
		// this plugin should not be saved.
		// nothing to do.
	}

	public void shutdownMapHook() {
		deregisterFilter();
		if (mListener != null) {
			signalEndOfSession();
			mListener.commitSuicide();
		}
		try {
			if (mServer != null) {
				mServer.close();
			}
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		mMasterStarted = false;
		super.shutdownMapHook();
	}

	/**
	 * 
	 */
	private void signalEndOfSession() {
		CollaborationGoodbye goodbye = new CollaborationGoodbye();
		goodbye.setUserId(Tools.getUserName());
		synchronized (mConnections) {
			for (int i = 0; i < mConnections.size(); i++) {
				final ServerCommunication serverCommunication = (ServerCommunication) mConnections
						.elementAt(i);
				try {
					serverCommunication.send(goodbye);
					serverCommunication.commitSuicide();
					serverCommunication.close();
				} catch (IOException e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
			mConnections.clear();
		}
	}

	public void onAddChild(MindMapNode pAddedChildNode) {
	}

	public void onAddChildren(MindMapNode pAddedChild) {
	}

	public void onLostFocusNode(NodeView pNodeView) {
	}

	public void onNewChild(MindMapNode pNewChildNode) {
	}

	public void onRemoveChild(MindMapNode pOldChildNode) {
	}

	public void onRemoveChildren(MindMapNode pOldChildNode, MindMapNode pOldDad) {
	}

	public void onFocusNode(NodeView pNodeView) {
	}

	public void onUpdateChildrenHook(MindMapNode pUpdatedNode) {
	}

	public void onUpdateNodeHook() {
	}

	public void onViewCreatedHook(NodeView pNodeView) {
	}

	public void onViewRemovedHook(NodeView pNodeView) {
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub

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
	public void processUnfinishedLinks() {
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.PermanentNodeHook#saveHtml(java.io.Writer)
	 */
	public void saveHtml(Writer pFileout) {
	}

}
