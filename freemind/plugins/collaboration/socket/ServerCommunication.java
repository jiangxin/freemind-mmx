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
/*$Id: UpdateThread.java,v 1.1.2.1 2009/02/04 19:31:21 christianfoltin Exp $*/

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
import freemind.modes.mindmapmode.MindMapController;

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
			throws IOException {
		if (pCommand instanceof CollaborationGoodbye) {
			CollaborationGoodbye goodbye = (CollaborationGoodbye) pCommand;
			logger.info("Goodbye received from " + goodbye.getUserId());
			mMindMapMaster.closeConnection(this);
			return;
		}
		switch (getCurrentState()) {
		case STATE_WAIT_FOR_HELLO:
			if (pCommand instanceof CollaborationHello) {
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
				}
			}
			break;
		case STATE_WAIT_FOR_COMMAND:
			if (pCommand instanceof CollaborationTransaction) {
				CollaborationTransaction trans = (CollaborationTransaction) pCommand;
				try {
					mMindMapMaster.broadcastCommand(trans.getDoAction(),
							trans.getUndoAction(), trans.getId());
					mMindMapMaster.executeTransaction(getActionPair(trans));
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				} finally {
					mMindMapMaster.unlock();
					setCurrentState(STATE_IDLE);
				}
			}
			break;
		case STATE_IDLE:
			if (pCommand instanceof CollaborationRequireLock) {
				try {
					String lockId = mMindMapMaster.lock();
					logger.info("Got lock for " + getName());
					CollaborationReceiveLock lockCommand = new CollaborationReceiveLock();
					lockCommand.setId(lockId);
					send(lockCommand);
					setCurrentState(STATE_WAIT_FOR_COMMAND);
				} catch (UnableToGetLockException e) {
					freemind.main.Resources.getInstance().logException(e);
					CollaborationUnableToLock unableToLock = new CollaborationUnableToLock();
					send(unableToLock);
				} catch (InterruptedException e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
			break;
		}
	}

}