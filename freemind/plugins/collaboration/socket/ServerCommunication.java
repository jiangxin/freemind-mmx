/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2009 Christian Foltin and others.
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
 * Created on 03.02.2009
 */

package plugins.collaboration.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Iterator;

import plugins.collaboration.socket.SocketBasics.UnableToGetLockException;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGetOffers;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationMapOffer;
import freemind.controller.actions.generated.instance.CollaborationOffers;
import freemind.controller.actions.generated.instance.CollaborationPublishNewMap;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationRequireLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.controller.actions.generated.instance.CollaborationWrongMap;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;

/**
 * This class handles the communication from the master to a single client. It
 * is thus instantiated for each master (e.g. by the MindMapMaster).
 * 
 * @author foltin
 * @date 13.09.2012
 */
public class ServerCommunication extends CommunicationBase {
	public static final String SERVER_VERSION = "2.1";
	protected SocketMaster mMindMapMaster = null;
	private boolean mIsSingleMapServer;

	public ServerCommunication(SocketMaster pSocketStarter, Socket pClient,
			ExtendedMapFeedback pMindMapController, boolean isSingleMapServer) throws Exception {
		super("Server Communication", pClient, pMindMapController,
				new DataOutputStream(pClient.getOutputStream()),
				new DataInputStream(pClient.getInputStream()));
		mMindMapMaster = pSocketStarter;
		mIsSingleMapServer = isSingleMapServer;
		CollaborationWhoAreYou commandWho = new CollaborationWhoAreYou();
		commandWho.setServerVersion(SERVER_VERSION);
		send(commandWho);
		setCurrentState(STATE_WAIT_FOR_GET_OFFERS);
	}

	public void processCommand(CollaborationActionBase pCommand)
			throws Exception {
		if (pCommand instanceof CollaborationGoodbye) {
			CollaborationGoodbye goodbye = (CollaborationGoodbye) pCommand;
			logger.info("Goodbye received from " + goodbye.getUserId());
			terminateSocket();
			return;
		}
		boolean commandHandled = false;
		if (pCommand instanceof CollaborationGetOffers) {
			if (getCurrentState() != STATE_WAIT_FOR_GET_OFFERS) {
				printWrongState(pCommand);
			} else {
				CollaborationGetOffers commandGetOffers = (CollaborationGetOffers) pCommand;
				commandHandled = true;
				setName(commandGetOffers.getUserId());
				// verify password:
				if (mMindMapMaster.getPassword().equals(
						commandGetOffers.getPassword())) {
					CollaborationOffers commandOffers = new CollaborationOffers();
					commandOffers.setIsSingleOffer(mIsSingleMapServer);
					for (Iterator it = mMindMapMaster.getFileMap().keySet().iterator(); it
							.hasNext();) {
						String fileName = (String) it.next();
						CollaborationMapOffer offer = new CollaborationMapOffer();
						offer.setMap(fileName);
						commandOffers.addCollaborationMapOffer(offer);
					}
					send(commandOffers);
					setCurrentState(STATE_WAIT_FOR_HELLO);
				} else {
					// Send error message
					CollaborationWrongCredentials wrongMessage = new CollaborationWrongCredentials();
					try {
						send(wrongMessage);
					} finally {
						terminateSocket();
					}
				}
			}
		}
		if(pCommand instanceof CollaborationPublishNewMap) {
			CollaborationPublishNewMap commandPublish = (CollaborationPublishNewMap) pCommand;
			if (getCurrentState() != STATE_WAIT_FOR_GET_OFFERS || mIsSingleMapServer) {
				printWrongState(pCommand);
			} else {
				// we got a new map to be published on the server
				commandHandled = true;
				setName(commandPublish.getUserId());
				// verify password:
				if (mMindMapMaster.getPassword().equals(
						commandPublish.getPassword())) {
					String map = commandPublish.getMapName();
					File baseFile = mMindMapMaster.getBaseFile();
					File file = new File(baseFile, map);
					// check for correct name
					boolean notCorrectName = false;
					if(map.contains("/") || map.contains("\\")) {
						notCorrectName = true;
					}
					// check for unique name
					if(mMindMapMaster.getFileMap().containsKey(map) || notCorrectName) {
						// Send error message
						CollaborationWrongMap wrongMessage = new CollaborationWrongMap();
						try {
							send(wrongMessage);
						} finally {
							terminateSocket();
						}
					}
					logger.info("New map " + map + " published.");
					// create new controller and load map
					
					mController = mMindMapMaster.createMapOnServer(
							map,
							new Tools.StringReaderCreator(commandPublish
									.getMap()), baseFile);
					// save:
					mController.getMap().save(file);
					// publish map to others? No other could be present, so don't send a map.
					setCurrentState(STATE_IDLE);
					mMindMapMaster.addConnection(this, mController);
				} else {
					// Send error message
					CollaborationWrongCredentials wrongMessage = new CollaborationWrongCredentials();
					try {
						send(wrongMessage);
					} finally {
						terminateSocket();
					}
				}
			}			
		}
		// FIXME: Security check here!
		if (pCommand instanceof CollaborationHello) {
			// here, we are strict in checking the state, as not the whole world
			// should get access to the map!
			if (getCurrentState() != STATE_WAIT_FOR_HELLO) {
				printWrongState(pCommand);
			} else {
				CollaborationHello commandHello = (CollaborationHello) pCommand;
				// get map:
				String map = commandHello.getMap();
				if(!mMindMapMaster.getFileMap().containsKey(map)) {
					// Send error message
					CollaborationWrongMap wrongMessage = new CollaborationWrongMap();
					try {
						send(wrongMessage);
					} finally {
						terminateSocket();
					}
				}
				logger.info("Map " + map + " requested.");
				mController = mMindMapMaster.getFileMap().get(map);
				mMindMapMaster.addConnection(this, mController);
				// send map:
				CollaborationWelcome welcomeCommand = new CollaborationWelcome();
				logger.info("Store map in welcome command...");
				StringWriter writer = new StringWriter();
				mController.getMap().getXml(writer);
				welcomeCommand.setMap(writer.toString());
				welcomeCommand.setFilename(mController.getMap().getFile()
						.getName());
				send(welcomeCommand);
				setCurrentState(STATE_IDLE);
				commandHandled = true;
			}
		}

		if (pCommand instanceof CollaborationTransaction) {
			if (getCurrentState() != STATE_WAIT_FOR_COMMAND) {
				printWrongState(pCommand);
			} else {
				CollaborationTransaction trans = (CollaborationTransaction) pCommand;
				try {
					if (!Tools
							.safeEquals(mMindMapMaster.getLockId(getController()), trans.getId())) {
						logger.severe("Wrong transaction id received. Expected: "
								+ mMindMapMaster.getLockId(getController()) + ", received: "
								+ trans.getId());
					} else {
						mMindMapMaster.broadcastCommand(trans.getDoAction(),
								trans.getUndoAction(), trans.getId(), getController());
						mMindMapMaster.executeTransaction(getActionPair(trans), getController());
					}
				} finally {
					mMindMapMaster.unlock(getController());
					setCurrentState(STATE_IDLE);
					commandHandled = true;
				}
			}
		}
		if (pCommand instanceof CollaborationRequireLock) {
			if (getCurrentState() != STATE_IDLE) {
				printWrongState(pCommand);
			}
			try {
				String lockId = mMindMapMaster.lock(this.getName(), getController());
				logger.info("Got lock for " + getName());
				CollaborationReceiveLock lockCommand = new CollaborationReceiveLock();
				lockCommand.setId(lockId);
				send(lockCommand);
				setCurrentState(STATE_WAIT_FOR_COMMAND);
				commandHandled = true;
			} catch (UnableToGetLockException e) {
				freemind.main.Resources.getInstance().logException(e);
				CollaborationUnableToLock unableToLock = new CollaborationUnableToLock();
				send(unableToLock);
			} catch (InterruptedException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		if (!commandHandled) {
			logger.warning("Received unknown message of type "
					+ pCommand.getClass());
		}
	}

	public void terminateSocket() throws IOException {
		mMindMapMaster.removeConnection(this);
		commitSuicide();
		close();
	}

}