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
import java.io.StringWriter;
import java.net.Socket;
import java.util.Iterator;

import plugins.collaboration.socket.MindMapMaster;
import plugins.collaboration.socket.StandaloneMindMapMaster;
import freemind.controller.actions.generated.instance.CollaborationGetOffers;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationMapOffer;
import freemind.controller.actions.generated.instance.CollaborationOffers;
import freemind.controller.actions.generated.instance.CollaborationPublishNewMap;
import freemind.controller.actions.generated.instance.CollaborationReceiveLock;
import freemind.controller.actions.generated.instance.CollaborationRequireLock;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.controller.actions.generated.instance.EditNoteToNodeAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MindMap;

/**
 * @author foltin
 * @date 08.05.2014
 */
public class CollaborationTests extends FreeMindTestBase {
	public static final String PUBLISHED_MAP_NAME = "published_map_name.mm";

	/**
	 * 
	 */
	public CollaborationTests() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pArg0
	 */
	public CollaborationTests(String pArg0) {
		super(pArg0);
	}

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

	private static final String INITIAL_MAP = "<map>" + "<node ID='1' TEXT='ROOT'>"
			+ "<node ID='2' TEXT='FormatMe'>" + "<node ID='3' TEXT='Child1'/>"
			+ "<node ID='4' TEXT='Child2'/>" + "<node ID='5' TEXT='Child3'/>" + "</node>"
			+ "</node>" + "</map>";

	/**
	 * 
	 */
	private static final String PASSWORD = "aa";

	public class NormalTestClient extends CollaborationTestClient  {
		public NormalTestClient(String pName, Socket pClient,
				ExtendedMapFeedback pMindMapController, DataOutputStream pOut,
				DataInputStream pIn) {
			super(pName, pClient, pMindMapController, pOut, pIn);
		}

		public void reactOnWhoAreYou(CollaborationWhoAreYou whoAre) {
			// send hello:
			CollaborationGetOffers getOffersCommand = new CollaborationGetOffers();
			getOffersCommand.setUserId(Tools.getUserName());
			getOffersCommand.setPassword(PASSWORD);
			send(getOffersCommand);
			setCurrentState(STATE_WAIT_FOR_OFFER);
		}

		public void reactOnOffers(final CollaborationOffers collOffers) {
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
		}
		
		public void reactOnWelcome(CollaborationWelcome collWelcome)
				throws IOException {
			String map = collWelcome.getMap();
			logger.info("Received map: " + map);
			createNewMap(map);
			setCurrentState(STATE_IDLE);
		}

		public void reactOnReceiveLock(CollaborationReceiveLock lockReceived) {
			mLockId = lockReceived.getId();
			logger.info("Lock received: " + mLockId);
			setCurrentState(STATE_LOCK_RECEIVED);
		}
		
		public void reactOnTransaction(CollaborationTransaction trans) {
			logger.info("Transaction received: " + trans.getDoAction());
			setCurrentState(STATE_IDLE);
		}
		

	}
	
	public void testNormalStartup() throws Exception {
		PrintWriter writer = new PrintWriter(PATHNAME + FILE, "UTF-8");
		writer.println(INITIAL_MAP);
		writer.close();

		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				getFrame(), new File(PATHNAME), PASSWORD, PORT);

		Socket socket = new Socket("localhost", PORT);
		socket.setSoTimeout(MindMapMaster.SOCKET_TIMEOUT_IN_MILLIES);
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		CollaborationTestClient testClient = new NormalTestClient(
				"TestClient", socket, mapFeedback, new DataOutputStream(
						socket.getOutputStream()), new DataInputStream(
						socket.getInputStream()));
		testClient.start();
		while (testClient.getCurrentState() != CollaborationTestClient.STATE_IDLE) {
			Thread.sleep(100);
		}
		CollaborationRequireLock rl = new CollaborationRequireLock();
		testClient.setCurrentState(CollaborationTestClient.STATE_WAIT_FOR_LOCK);
		testClient.send(rl);
		while (testClient.getCurrentState() != CollaborationTestClient.STATE_LOCK_RECEIVED) {
			Thread.sleep(100);
		}
		EditNoteToNodeAction action = mapFeedback
				.getActorFactory()
				.getChangeNoteTextActor()
				.createEditNoteToNodeAction(mapFeedback.getMap().getRootNode(),
						"blubber");
		CollaborationTransaction t = new CollaborationTransaction();
		String marshall = Tools.marshall(action);
		t.setDoAction(marshall);
		t.setId(testClient.mLockId);
		t.setUndoAction(marshall);
		testClient.send(t);
		while (testClient.getCurrentState() != CollaborationTestClient.STATE_IDLE) {
			Thread.sleep(100);
		}
		// TODO: Wait on save.
		testClient.terminateSocket();
		master.terminate();

	}

	public void testPublishExistingMapStartup() throws Exception {
		File fileToBeCreated = new File(PATHNAME, PUBLISHED_MAP_NAME);
		fileToBeCreated.delete();
		assertFalse(fileToBeCreated.exists());
		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				getFrame(), new File(PATHNAME), PASSWORD, PORT);
		
		Socket socket = new Socket("localhost", PORT);
		socket.setSoTimeout(MindMapMaster.SOCKET_TIMEOUT_IN_MILLIES);
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		CollaborationTestClient testClient = new CreateNewMapClient(
				"TestClient", socket, mapFeedback, new DataOutputStream(
						socket.getOutputStream()), new DataInputStream(
								socket.getInputStream()), PUBLISHED_MAP_NAME);
		// create map in memory and read it:
		testClient.createNewMap(INITIAL_MAP);
		// run
		testClient.start();
		while (testClient.getCurrentState() != CollaborationTestClient.STATE_IDLE) {
			Thread.sleep(100);
		}
		testClient.terminateSocket();
		// wait for save of the server
		while(!fileToBeCreated.exists()) {
			Thread.sleep(100);
		}
		master.terminate();
		
	}
	
	public void testPublishExistingMapWrongName() throws Exception {
		StandaloneMindMapMaster master = new StandaloneMindMapMaster(
				getFrame(), new File(PATHNAME), PASSWORD, PORT);
		
		Socket socket = new Socket("localhost", PORT);
		socket.setSoTimeout(MindMapMaster.SOCKET_TIMEOUT_IN_MILLIES);
		ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
		CreateNewMapClient testClient = new CreateNewMapClient(
				"TestClient", socket, mapFeedback, new DataOutputStream(
						socket.getOutputStream()), new DataInputStream(
								socket.getInputStream()), "../file_at_wrong_position.mm") {

			public void reactOnWrongMap() {
				super.reactOnWrongMap();
				mWrongMap  = true;
			};
		};
		// create map in memory and read it:
		testClient.createNewMap(INITIAL_MAP);
		// run
		testClient.start();
		// error should occur.
		int timeout = 50;
		while (!testClient.mWrongMap && --timeout>0) {
			Thread.sleep(100);
		}
		assertTrue("wrong map sent", testClient.mWrongMap);
		testClient.terminateSocket();
		master.terminate();
		
	}
	
	public class CreateNewMapClient extends CollaborationTestClient {

		public boolean mWrongMap = false;
		private String mPublishMapName;

		public CreateNewMapClient(String pName, Socket pClient,
				ExtendedMapFeedback pMindMapController, DataOutputStream pOut,
				DataInputStream pIn, String pPublishMapName) {
			super(pName, pClient, pMindMapController, pOut, pIn);
			mPublishMapName = pPublishMapName;
		}

		public void reactOnWhoAreYou(CollaborationWhoAreYou whoAre) {
			// send hello:
			CollaborationPublishNewMap publishCommand = new CollaborationPublishNewMap();
			publishCommand.setUserId(Tools.getUserName());
			publishCommand.setPassword(PASSWORD);
			MindMap map = mController.getMap();
			publishCommand.setMapName(mPublishMapName);
			StringWriter writer = new StringWriter();
			try {
				map.getXml(writer);
			} catch (IOException e) {
				fail("Cant get map");
			}
			publishCommand.setMap(writer.toString());
			send(publishCommand);
			setCurrentState(STATE_IDLE);
		}

		public void reactOnOffers(final CollaborationOffers collOffers) {
			fail("no offers please");
		}
		
		public void reactOnWelcome(CollaborationWelcome collWelcome)
				throws IOException {
			fail("no welcome please");
		}

		public void reactOnReceiveLock(CollaborationReceiveLock lockReceived) {
			mLockId = lockReceived.getId();
			logger.info("Lock received: " + mLockId);
			setCurrentState(STATE_LOCK_RECEIVED);
		}
		
		public void reactOnTransaction(CollaborationTransaction trans) {
			logger.info("Transaction received: " + trans.getDoAction());
			setCurrentState(STATE_IDLE);
		}
		

	}


	
}
