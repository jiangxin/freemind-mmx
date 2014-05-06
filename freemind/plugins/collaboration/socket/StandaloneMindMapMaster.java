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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;

import plugins.collaboration.socket.SocketMaster.SessionData;
import tests.freemind.FreeMindMainMock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;

/**
 * Background server for map serving without gui. Single map instance.
 * 
 * @author foltin
 * @date 03.05.2014
 */
public class StandaloneMindMapMaster extends SocketMaster {

	private ServerSocket mServer;
	private MasterThread mListener;
	private FreeMindMainMock mFreeMindMain;

	private class MasterThread extends TerminateableThread {

		private static final long TIME_BETWEEN_USER_INFORMATION_IN_MILLIES = 5000;
		private static final long TIME_BETWEEN_SAVE_ACTIONS_IN_MILLIES = 60000;
		private static final long TIME_FOR_ORPHANED_LOCK = 5000;
		private long mLastTimeUserInformationSent = 0;
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
				ServerCommunication communication = new ServerCommunication(
						StandaloneMindMapMaster.this, client, null);
				communication.start(); 
			} catch (SocketTimeoutException e) {
			}
			final long now = System.currentTimeMillis();
			for (Iterator it = mFileMap.keySet().iterator(); it.hasNext();) {
				String mapName = (String) it.next();
				ExtendedMapFeedback extendedMapFeedback = mFileMap.get(mapName);
				SessionData sessionData = getSessionData(extendedMapFeedback);
				if (now - mLastTimeUserInformationSent > TIME_BETWEEN_USER_INFORMATION_IN_MILLIES) {
					mLastTimeUserInformationSent = now;
					CollaborationUserInformation userInfo = getMasterInformation(extendedMapFeedback);
					synchronized (sessionData.mConnections) {
						for (int i = 0; i < sessionData.mConnections.size(); i++) {
							try {
								final ServerCommunication connection = sessionData.mConnections
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
				synchronized (sessionData.mLockMutex) {
					if (sessionData.mLockEnabled && now - sessionData.mLockedAt > TIME_FOR_ORPHANED_LOCK) {
						logger.warning("Release lock " + sessionData.mLockId + " held by "
								+ sessionData.mLockUserName);
						clearLock(extendedMapFeedback);
					}
					// regular save action:
					if (!sessionData.mLockEnabled && now - mLastSaveAction > TIME_BETWEEN_SAVE_ACTIONS_IN_MILLIES) {
						MindMap map = extendedMapFeedback.getMap();
						File file = map.getFile();
						logger.fine("Checking map " + file + " for save action needed.");
						mLastSaveAction = now;
						if(!map.isSaved()) {
							// save map:
							logger.info("Saving map " + file + " now.");
							map.save(file);
						} else {
							logger.fine("No save necessary.");
						}
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
		throw new IllegalArgumentException("No controller here.");
	}

	/**
	 * @param pFreeMindMain
	 * @param pPassword
	 * @param pPort
	 * @throws IOException
	 * @throws XMLParseException
	 * 
	 */
	public StandaloneMindMapMaster(FreeMindMainMock pFreeMindMain, File pFilePath,
			String pPassword, int pPort) throws XMLParseException, IOException {
		mFreeMindMain = pFreeMindMain;
		String[] fileList = pFilePath.list(new FilenameFilter() {

			@Override
			public boolean accept(File pDir, String pName) {
				return pName.endsWith(".mm");
			}});
		mFileMap = new HashMap<String, ExtendedMapFeedback>();
		for (int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i];
			ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
			mFileMap.put(fileName, mapFeedback);
			mConnections.put(mapFeedback, new SessionData());

			MindMapMapModel map = new MindMapMapModel(mapFeedback);
			mapFeedback.setMap(map); 
			File file = new File(pFilePath, fileName);
			map.setFile(file);
			logger.info("Loading " + fileName);
			MindMapNode root = map.loadTree(new Tools.FileReaderCreator(file),
					MapAdapter.sDontAskInstance);
			map.setRoot(root);
			mapFeedback.invokeHooksRecursively(root, map);
			mapFeedback.getActionRegistry().registerFilter(this);
			logger.info("Loading " + fileName + ". Done.");
		}
		mPassword = pPassword;
		logger.info("Start server...");
		try {
			mServer = new ServerSocket(pPort);
			mServer.setSoTimeout(SOCKET_TIMEOUT_IN_MILLIES);
			mListener = new MasterThread();
			mListener.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			System.exit(1);
			return;
		}
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
				new FreeMindMainMock(), new File("/tmp/"), "aa", 9001);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketMaster#setTitle()
	 */
	@Override
	protected void setTitle() {
	}
	
}
