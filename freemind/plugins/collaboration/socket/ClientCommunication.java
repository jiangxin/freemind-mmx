/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import plugins.collaboration.socket.SocketBasics.UnableToGetLockException;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationRequireLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 * @date 06.09.2012
 */
public class ClientCommunication extends CommunicationBase {

	private static final int MAX_LOCK_RETRIES = 5;
	private static final long LOCK_RETRY_SLEEP_TIME = 1000;
	private String mLockId;
	private HashSet mLockIds = new HashSet();
	private String mPassword;
	private SocketConnectionHook mSocketConnectionHook = null;
	private boolean mReceivedGoodbye = false;
	private CollaborationUserInformation mUserInfo;

	/**
	 * @param pName
	 * @param pClient
	 * @param pController
	 * @param pPassword
	 * @param pOut
	 * @param pIn
	 * @throws IOException
	 */
	public ClientCommunication(String pName, Socket pClient,
			MindMapController pController, String pPassword) throws IOException {
		super(pName, pClient, pController, new DataOutputStream(
				pClient.getOutputStream()), new DataInputStream(
				pClient.getInputStream()));
		mPassword = pPassword;
		setCurrentState(STATE_WAIT_FOR_WHO_ARE_YOU);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.CommunicationBase#processCommand(freemind
	 * .controller.actions.generated.instance.CollaborationActionBase)
	 */
	public void processCommand(CollaborationActionBase pCommand)
			throws IOException {
		if (pCommand instanceof CollaborationGoodbye) {
			CollaborationGoodbye goodbye = (CollaborationGoodbye) pCommand;
			logger.info("Goodbye received from " + goodbye.getUserId());
			terminateSocket();
			return;
		}
		boolean commandHandled = false;
		if (pCommand instanceof CollaborationUserInformation) {
			CollaborationUserInformation userInfo = (CollaborationUserInformation) pCommand;
			mUserInfo = userInfo;
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWhoAreYou) {
			if (getCurrentState() != STATE_WAIT_FOR_WHO_ARE_YOU) {
				printWrongState(pCommand);
			}
			// send hello:
			CollaborationHello helloCommand = new CollaborationHello();
			helloCommand.setUserId(Tools.getUserName());
			helloCommand.setPassword(mPassword);
			send(helloCommand);
			setCurrentState(STATE_WAIT_FOR_WELCOME);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWelcome) {
			if (getCurrentState() != STATE_WAIT_FOR_WELCOME) {
				printWrongState(pCommand);
			}
			CollaborationWelcome collWelcome = (CollaborationWelcome) pCommand;
			createNewMap(collWelcome.getMap());
			setCurrentState(STATE_IDLE);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWrongCredentials) {
			if (getCurrentState() != STATE_WAIT_FOR_WELCOME) {
				printWrongState(pCommand);
			}
			// Over and out.
			terminateSocket();
			// Display error message!
			getMindMapController().getController().errorMessage(
					getMindMapController().getText("socket_wrong_password"));
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationTransaction) {
			CollaborationTransaction trans = (CollaborationTransaction) pCommand;
			// check if it is from me!
			boolean removeResult;
			synchronized (mLockIds) {
				removeResult = mLockIds.remove(trans.getId());
			}
			if (!removeResult) {
				// it is not from me, so handle it:
				if (getCurrentState() != STATE_IDLE) {
					printWrongState(pCommand);
				}
				if (mSocketConnectionHook != null) {
					mSocketConnectionHook
							.executeTransaction(getActionPair(trans));
				}
			}
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationReceiveLock) {
			if (getCurrentState() != STATE_WAIT_FOR_LOCK) {
				printWrongState(pCommand);
			}
			CollaborationReceiveLock lockReceived = (CollaborationReceiveLock) pCommand;
			this.mLockId = lockReceived.getId();
			synchronized (mLockIds) {
				mLockIds.add(mLockId);
			}
			setCurrentState(STATE_LOCK_RECEIVED);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationUnableToLock) {
			// no lock possible.
			setCurrentState(STATE_IDLE);
			commandHandled = true;
		}
		if (!commandHandled) {
			logger.warning("Received unknown message of type "
					+ pCommand.getClass());
		}
	}

	public void terminateSocket() {
		mReceivedGoodbye = true;
		if (mSocketConnectionHook != null) {
			// first deregister, as otherwise, the toggle hook command is tried
			// to
			// be sent over the wire.
			mSocketConnectionHook.deregisterFilter();
			// Terminates socket by shutdownHook.
			toggleHook();
		} else {
			// Terminate socket.
			shutdown();
		}
	}

	public void toggleHook() {
		SocketBasics.togglePermanentHook(getMindMapController(),
				SocketBasics.SLAVE_HOOK_LABEL);
	}

	/**
	 * Sends the lock requests, blocks until timeout or answer and returns the
	 * associated id. Exception otherwise.
	 * 
	 * @throws InterruptedException
	 * @throws UnableToGetLockException
	 */
	public synchronized String sendLockRequest() throws InterruptedException,
			UnableToGetLockException {
		// TODO: Global lock needed?
		mLockId = null;
		int lockTries = 0;
		int timeout = 0;
		while(lockTries < MAX_LOCK_RETRIES) {
			CollaborationRequireLock lockRequest = new CollaborationRequireLock();
			setCurrentState(STATE_WAIT_FOR_LOCK);
			if (!send(lockRequest)) {
				setCurrentState(STATE_IDLE);
				throw new SocketBasics.UnableToGetLockException();
			}
			final int sleepTime = ROUNDTRIP_TIMEOUT / ROUNDTRIP_ROUNDS;
			timeout = ROUNDTRIP_ROUNDS;
			while (getCurrentState() == STATE_WAIT_FOR_LOCK && timeout >= 0) {
				sleep(sleepTime);
				timeout--;
			}
			if(getCurrentState() == STATE_LOCK_RECEIVED) {
				break;
			}
			// error case, repeat lock question.
			lockTries ++;
			logger.info("Didn't receive a lock, current try: " + lockTries);
			sleep(LOCK_RETRY_SLEEP_TIME);
		}
		setCurrentState(STATE_IDLE);
		if(lockTries == MAX_LOCK_RETRIES || timeout < 0) {
			throw new SocketBasics.UnableToGetLockException();
		}
		return mLockId;
	}

	void createNewMap(String map) throws IOException {
		{
			// // deregister from old controller:
			// deregisterFilter();
			logger.info("Restoring the map...");
			MindMapController newModeController = (MindMapController) getMindMapController()
					.getMode().createModeController();
			MapAdapter newModel = new MindMapMapModel(getMindMapController()
					.getFrame(), newModeController);
			HashMap IDToTarget = new HashMap();
			StringReader reader = new StringReader(map);
			MindMapNodeModel rootNode = (MindMapNodeModel) newModeController
					.createNodeTreeFromXml(reader, IDToTarget);
			reader.close();
			newModel.setRoot(rootNode);
			rootNode.setMap(newModel);
			getMindMapController().newMap(newModel);
			newModeController.invokeHooksRecursively((NodeAdapter) rootNode,
					newModel);
			setController(newModeController);
			// add new hook
			toggleHook();
			// tell him about this thread.
			Collection activatedHooks = getMindMapController().getRootNode()
					.getActivatedHooks();
			for (Iterator it = activatedHooks.iterator(); it.hasNext();) {
				PermanentNodeHook hook = (PermanentNodeHook) it.next();
				if (hook instanceof SocketConnectionHook) {
					SocketConnectionHook connHook = null;
					connHook = (SocketConnectionHook) hook;
					// Tell the hook about me
					connHook.setClientCommunication(this);
					/* register as listener, as I am a slave. */
					connHook.registerFilter();
					this.mSocketConnectionHook = connHook;
					break;
				}
			}
		}
	}

	/**
	 * @param pNewModeController
	 */
	private void setController(MindMapController pNewModeController) {
		mController = pNewModeController;
	}

	/**
	 * @return
	 */
	private MindMapController getMindMapController() {
		return mController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#shutdown()
	 */
	public void shutdown() {
		// we don't want to read anymore, as we send the goodbye anyway!
		commitSuicide();
		try {
			if (!mReceivedGoodbye) {
				// Send only, if own goodbye.
				CollaborationGoodbye goodbye = new CollaborationGoodbye();
				goodbye.setUserId(Tools.getUserName());
				send(goodbye);
				// in between, the socket has been closed.
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		try {
			close();
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * @return
	 */
	public int getPort() {
		return mSocket.getLocalPort();
	}

	public CollaborationUserInformation getUserInfo() {
		return mUserInfo;
	}

}
