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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import plugins.collaboration.socket.CommunicationBase;
import plugins.collaboration.socket.MindMapMaster;
import plugins.collaboration.socket.SocketConnectionHook;
import plugins.collaboration.socket.StandaloneMindMapMaster;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationGetOffers;
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationMapOffer;
import freemind.controller.actions.generated.instance.CollaborationOffers;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationUnableToLock;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.CollaborationWrongCredentials;
import freemind.controller.actions.generated.instance.CollaborationWrongMap;
import freemind.extensions.NodeHookAdapter;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 * @date 07.05.2014
 */
public class CollaborationTestClient extends CommunicationBase {

	/**
	 * 
	 */
	private static final String FILE = "bla.mm";

	/**
	 * 
	 */
	private static final String PATHNAME = "/tmp/";

	/**
	 * 
	 */
	private static final int PORT = 9001;

	private static final String INITIAL_MAP = "<map>" + "<node TEXT='ROOT'>"
			+ "<node TEXT='FormatMe'>" + "<node TEXT='Child1'/>"
			+ "<node TEXT='Child2'/>" + "<node TEXT='Child3'/>" + "</node>"
			+ "</node>" + "</map>";

	/**
	 * 
	 */
	private static final String PASSWORD = "aa";

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {
		sFreeMindMain = new FreeMindMainMock();
		PrintWriter writer = new PrintWriter(PATHNAME + FILE, "UTF-8");
		writer.println(INITIAL_MAP);
		writer.close();

		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				sFreeMindMain, new File(PATHNAME), PASSWORD, PORT);

		Socket socket = new Socket("localhost", PORT);
		socket.setSoTimeout(MindMapMaster.SOCKET_TIMEOUT_IN_MILLIES);
		CollaborationTestClient testClient = new CollaborationTestClient(
				"TestClient", socket, new ExtendedMapFeedbackImpl(),
				new DataOutputStream(socket.getOutputStream()),
				new DataInputStream(socket.getInputStream()));
		testClient.start();
		while(testClient.getCurrentState() != STATE_IDLE) {
			Thread.sleep(100);
		}
		testClient.terminateSocket();
		master.terminate();
		
		System.exit(0);
	}

	private static FreeMindMainMock sFreeMindMain;

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

			// send hello:
			CollaborationGetOffers getOffersCommand = new CollaborationGetOffers();
			getOffersCommand.setUserId(Tools.getUserName());
			getOffersCommand.setPassword(PASSWORD);
			send(getOffersCommand);
			setCurrentState(STATE_WAIT_FOR_OFFER);
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationOffers) {
			if (getCurrentState() != STATE_WAIT_FOR_OFFER) {
				printWrongState(pCommand);
			}
			final CollaborationOffers collOffers = (CollaborationOffers) pCommand;
			// now, we have a bundle of different maps to offer to the user
			for (Iterator it = collOffers.getListCollaborationMapOfferList()
					.iterator(); it.hasNext();) {
				CollaborationMapOffer offer = (CollaborationMapOffer) it.next();
				System.out.println("Map: " + offer.getMap());
			}
			// send hello:
			CollaborationHello helloCommand = new CollaborationHello();
			helloCommand.setMap(FILE);
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
			// Over and out.
			terminateSocket();
			// Display error message!
			logger.severe("Wrong map");
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationTransaction) {
			CollaborationTransaction trans = (CollaborationTransaction) pCommand;
			commandHandled = true;
		}
		if (pCommand instanceof CollaborationReceiveLock) {
			if (getCurrentState() != STATE_WAIT_FOR_LOCK) {
				printWrongState(pCommand);
			}
			CollaborationReceiveLock lockReceived = (CollaborationReceiveLock) pCommand;
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

}
