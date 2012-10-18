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
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;

import plugins.collaboration.socket.SocketBasics.UnableToGetLockException;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationRequireLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

/**
 * This class handles the communication from the master to a single client. It
 * is thus instanciated for each client (by the MindMapMaster).
 * 
 * @author foltin
 * @date 13.09.2012
 */
public class ServerCommunication extends CommunicationBase {
	protected MindMapMaster mMindMapMaster = null;

	public ServerCommunication(MindMapMaster pSocketStarter, Socket pClient,
			MindMapController pMindMapController) throws Exception {
		super("Client Communication", pClient, pMindMapController,
				new DataOutputStream(pClient.getOutputStream()),
				new DataInputStream(pClient.getInputStream()));
		mMindMapMaster = pSocketStarter;
		CollaborationWhoAreYou commandWho = new CollaborationWhoAreYou();
		send(commandWho);
		setCurrentState(STATE_WAIT_FOR_HELLO);
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
		if (pCommand instanceof CollaborationHello) {
			// here, we are strict in checking the state, as not the whole world
			// should get access to the map!
			if (getCurrentState() != STATE_WAIT_FOR_HELLO) {
				printWrongState(pCommand);
			} else {
				CollaborationHello commandHello = (CollaborationHello) pCommand;
				setName(commandHello.getUserId());
				// verify password:
				if (mMindMapMaster.getPassword().equals(
						commandHello.getPassword())) {
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
				} else {
					// Send error message
					CollaborationWrongCredentials wrongMessage = new CollaborationWrongCredentials();
					try {
						send(wrongMessage);
					} finally {
						terminateSocket();
					}
				}
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
							.safeEquals(mMindMapMaster.getLockId(), trans.getId())) {
						logger.severe("Wrong transaction id received. Expected: "
								+ mMindMapMaster.getLockId() + ", received: "
								+ trans.getId());
					} else {
						mMindMapMaster.broadcastCommand(trans.getDoAction(),
								trans.getUndoAction(), trans.getId());
						mMindMapMaster.executeTransaction(getActionPair(trans));
					}
				} finally {
					mMindMapMaster.unlock();
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
				String lockId = mMindMapMaster.lock(this.getName());
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