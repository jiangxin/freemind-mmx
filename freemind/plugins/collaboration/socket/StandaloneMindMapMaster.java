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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import tests.freemind.FreeMindMainMock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;

/**
 * @author foltin
 * @date 03.05.2014
 */
public class StandaloneMindMapMaster extends SocketMaster {

	private ServerSocket mServer;
	private MasterThread mListener;
	private String mLockMutex = "";
	private ExtendedMapFeedbackImpl mMapFeedback;
	private FreeMindMainMock mFreeMindMain;
	private MindMapMapModel mMap;
	private File mFile;

	private class MasterThread extends TerminateableThread {

		private static final long TIME_BETWEEN_USER_INFORMATION_IN_MILLIES = 5000;
		private static final long TIME_BETWEEN_SAVE_ACTIONS_IN_MILLIES = 60000;
		private static final long TIME_FOR_ORPHANED_LOCK = 5000;
		private long mLastTimeUserInformationSent = 0;
		private ServerCommunication mCommunication;
		private long mLastSaveAction = 0;

		/**
		 * @param pName
		 */
		public MasterThread() {
			super("StandaloneMaster");
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
				mCommunication = new ServerCommunication(
						StandaloneMindMapMaster.this, client, mMapFeedback);
				mCommunication.start();
				synchronized (mConnections) {
					mConnections.addElement(mCommunication);
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
							/*
							 * to each server, the IP address is chosen that
							 * belongs to this connection. E.g. if the
							 * connection is routed over one of several network
							 * interfaces, the address of this interface is
							 * reported.
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
				// regular save action:
				if (!mLockEnabled && now - mLastSaveAction > TIME_BETWEEN_SAVE_ACTIONS_IN_MILLIES) {
					logger.fine("Checking map " + mFile + " for save action needed.");
					mLastSaveAction = now;
					if(!mMap.isSaved()) {
						// save map:
						logger.info("Saving map " + mFile + " now.");
						mMap.save(mFile);
					} else {
						logger.fine("No save necessary.");
					}
				}
			}
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter#getMindMapController
	 * ()
	 */
	@Override
	public MindMapController getMindMapController() {
		throw new IllegalArgumentException("No controller here.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getMapFeedback()
	 */
	@Override
	protected ExtendedMapFeedback getMapFeedback() {
		return mMapFeedback;
	}

	/**
	 * @param pFreeMindMain
	 * @param pPassword
	 * @param pPort
	 * @throws IOException
	 * @throws XMLParseException
	 * 
	 */
	public StandaloneMindMapMaster(FreeMindMainMock pFreeMindMain, File pFile,
			String pPassword, int pPort) throws XMLParseException, IOException {
		mFreeMindMain = pFreeMindMain;
		mFile = pFile;
		mPassword = pPassword;
		mMapFeedback = new ExtendedMapFeedbackImpl();
		mMap = new MindMapMapModel(mMapFeedback);
		mMapFeedback.setMap(mMap);
		mMap.setFile(pFile);
		MindMapNode root = mMap.loadTree(new Tools.FileReaderCreator(pFile),
				MapAdapter.sDontAskInstance);
		mMap.setRoot(root);
		logger.info("Start server...");
		try {
			mServer = new ServerSocket(pPort);
			mServer.setSoTimeout(SOCKET_TIMEOUT_IN_MILLIES);
			mListener = new MasterThread();
			mListener.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			// FIXME: Restart or what should be done here?
			System.exit(1);
			return;
		}
		registerFilter();
		logger.info("Starting server. Done.");
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws XMLParseException
	 */
	public static void main(String[] args) throws XMLParseException,
			IOException {
		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				new FreeMindMainMock(), new File("/tmp/bla.mm"), "aa", 9001);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketMaster#setTitle()
	 */
	@Override
	protected void setTitle() {
		logger.info("Set title to " + getUsers());
	}

}
