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

package tests.freemind;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.HashMap;

import plugins.collaboration.socket.CommunicationBase;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationOffers;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.controller.actions.generated.instance.CollaborationWrongMap;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 * @date 07.05.2014
 */
public abstract class CollaborationTestClient extends CommunicationBase {


	String mLockId;

	/**
	 * @param pName
	 * @param pClient
	 * @param pMindMapController
	 * @param pOut
	 * @param pIn
	 */
	public CollaborationTestClient(String pName, Socket pClient,
			ExtendedMapFeedback pMindMapController, DataOutputStream pOut,
			DataInputStream pIn) {
		super(pName, pClient, pMindMapController, pOut, pIn);
		setCurrentState(STATE_WAIT_FOR_WHO_ARE_YOU);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.CommunicationBase#terminateSocket()
	 */
	@Override
	public void terminateSocket() throws IOException {
		commitSuicide();
		CollaborationGoodbye goodbye = new CollaborationGoodbye();
		goodbye.setUserId(Tools.getUserName());
		send(goodbye);
		close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.CommunicationBase#processCommand(freemind
	 * .controller.actions.generated.instance.CollaborationActionBase)
	 */
	@Override
	public void processCommand(CollaborationActionBase pCommand)
			throws Exception {
		if (pCommand instanceof CollaborationGoodbye) {
			CollaborationGoodbye goodbye = (CollaborationGoodbye) pCommand;
			logger.info("Goodbye received from " + goodbye.getUserId());
			terminateSocket();
			return;
		}
		boolean commandHandled = false;
		if (pCommand instanceof CollaborationUserInformation) {
			CollaborationUserInformation userInfo = (CollaborationUserInformation) pCommand;
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWhoAreYou) {
			if (getCurrentState() != STATE_WAIT_FOR_WHO_ARE_YOU) {
				printWrongState(pCommand);
			}
			// check server version:
			CollaborationWhoAreYou whoAre = (CollaborationWhoAreYou) pCommand;
			reactOnWhoAreYou(whoAre);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationOffers) {
			if (getCurrentState() != STATE_WAIT_FOR_OFFER) {
				printWrongState(pCommand);
			}
			final CollaborationOffers collOffers = (CollaborationOffers) pCommand;
			reactOnOffers(collOffers);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWelcome) {
			if (getCurrentState() != STATE_WAIT_FOR_WELCOME) {
				printWrongState(pCommand);
			}
			CollaborationWelcome collWelcome = (CollaborationWelcome) pCommand;
			reactOnWelcome(collWelcome);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWrongCredentials) {
			if (getCurrentState() != STATE_WAIT_FOR_OFFER) {
				printWrongState(pCommand);
			}
			// Over and out.
			terminateSocket();
			// Display error message!
			logger.severe("Wrong credentials");
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationWrongMap) {
			if (getCurrentState() != STATE_WAIT_FOR_WELCOME) {
				printWrongState(pCommand);
			}
			reactOnWrongMap();
			// Over and out.
			terminateSocket();
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationTransaction) {
			CollaborationTransaction trans = (CollaborationTransaction) pCommand;
			reactOnTransaction(trans);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationReceiveLock) {
			if (getCurrentState() != STATE_WAIT_FOR_LOCK) {
				printWrongState(pCommand);
			}
			CollaborationReceiveLock lockReceived = (CollaborationReceiveLock) pCommand;
			reactOnReceiveLock(lockReceived);
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

	public void reactOnWrongMap() {
		// Display error message!
		logger.severe("Wrong map");
	}


	void createNewMap(String map) throws IOException {
		MapAdapter newModel = new MindMapMapModel(mController);
		((ExtendedMapFeedbackImpl) mController).setMap(newModel);
		HashMap IDToTarget = new HashMap();
		StringReader reader = new StringReader(map);
		MindMapNodeModel rootNode = (MindMapNodeModel) newModel
				.createNodeTreeFromXml(reader, IDToTarget);
		reader.close();
		newModel.setRoot(rootNode);
		rootNode.setMap(newModel);
		mController.invokeHooksRecursively((NodeAdapter) rootNode, newModel);
	}
	
	public abstract void reactOnReceiveLock(
			CollaborationReceiveLock lockReceived);

	public abstract void reactOnTransaction(CollaborationTransaction trans);

	public abstract void reactOnWelcome(CollaborationWelcome collWelcome)
			throws IOException;

	public abstract void reactOnOffers(final CollaborationOffers collOffers);

	public abstract void reactOnWhoAreYou(CollaborationWhoAreYou whoAre);

}
