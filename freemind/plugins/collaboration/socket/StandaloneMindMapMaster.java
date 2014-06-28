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
import java.util.Iterator;

import tests.freemind.FreeMindMainMock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Background server for map serving without gui. Single map instance.
 * 
 * @author foltin
 * @date 03.05.2014
 */
public class StandaloneMindMapMaster extends SocketMaster {

	private ServerSocket mServer;
	private MasterThread mMasterThread;
	private FreeMindMainMock mFreeMindMain;
	private File mBaseFilePath;

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
			// save maps on exit!
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			    public void run() {
			    	System.out.println("Automatic save on exit starts...");
			    	for (Iterator it = mFileMap.keySet().iterator(); it.hasNext();) {
			    		String mapName = (String) it.next();
			    		ExtendedMapFeedback extendedMapFeedback = mFileMap.get(mapName);
			    		MindMap map = extendedMapFeedback.getMap();
			    		File file = map.getFile();
			    		System.out.println("Looking for map " + file + " to be saved...");
			    		if(!map.isSaved()) {
			    			try {
			    				// save map:
								map.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
			    		}
			    	}
			    }
			}));

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
						StandaloneMindMapMaster.this, client, null, false);
				communication.start(); 
			} catch (SocketTimeoutException e) {
			}
			final long now = System.currentTimeMillis();
			synchronized (mSessions) {
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
		mBaseFilePath = pFilePath;
		String[] fileList = pFilePath.list(new FilenameFilter() {

			@Override
			public boolean accept(File pDir, String pName) {
				return pName.endsWith(".mm");
			}});
		for (int i = 0; i < fileList.length; i++) {
			String fileName = fileList[i];
			File file = new File(pFilePath, fileName);
			logger.info("Loading " + fileName);
			Tools.FileReaderCreator readerCreator = new Tools.FileReaderCreator(file);
			createMapOnServer(fileName, readerCreator, file);
			logger.info("Loading " + fileName + ". Done.");
		}
		mPassword = pPassword;
		logger.info("Start server...");
		try {
			mServer = new ServerSocket(pPort);
			mServer.setSoTimeout(SOCKET_TIMEOUT_IN_MILLIES);
			mMasterThread = new MasterThread();
			mMasterThread.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			System.exit(1);
			return;
		}
		logger.info("Starting server. Done.");
	}

	@Override
	protected File getBaseFile() {
		return mBaseFilePath;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws XMLParseException
	 */
	public static void main(String[] args) throws XMLParseException,
			IOException {
		String path;
		if(args.length > 0) {
			path = args[0];
		} else {
			path = "/tmp/";
		}
		System.out.println("Using path '" + path + "'");
		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				new FreeMindMainMock(), new File(path), "aa", 9001);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketMaster#setTitle()
	 */
	@Override
	protected void setTitle() {
	}
	
	public void terminate() {
		mMasterThread.commitSuicide();
		try {
			mServer.close();
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}
	
}
