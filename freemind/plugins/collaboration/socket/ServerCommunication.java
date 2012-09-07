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
import java.net.Socket;

import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.modes.mindmapmode.MindMapController;

public class ServerCommunication extends CommunicationBase {
	protected MindMapMaster mConnection = null;

	public ServerCommunication(MindMapMaster pSocketStarter, Socket pClient,
			MindMapController pMindMapController) throws Exception {
		super("Client Communication", pClient, pMindMapController,
				new DataOutputStream(pClient.getOutputStream()),
				new DataInputStream(pClient.getInputStream()));
		mConnection = pSocketStarter;
		CollaborationWhoAreYou commandWho = new CollaborationWhoAreYou();
		send(commandWho);
		mCurrentState = STATE_WAIT_FOR_HELLO;
	}

	public void processResults() {
		try {
			// while (rs.next()) {
			// long nextPk = rs.getLong(SocketBasics.ROW_PK);
			// mPrimaryKey = nextPk + 1;
			// String doAction = rs.getString(SocketBasics.ROW_ACTION);
			// String undoAction = rs.getString(SocketBasics.ROW_UNDOACTION);
			// String map = rs.getString(SocketBasics.ROW_MAP);
			// logger.info("Got the following from database: " + nextPk + ", "
			// + doAction + ", " + undoAction + ", " + map);
			// if (doAction != null && undoAction != null) {
			// XmlAction xmlDoAction = mController.unMarshall(doAction);
			// XmlAction xmlUndoAction = mController
			// .unMarshall(undoAction);
			// ActionPair pair = new ActionPair(xmlDoAction, xmlUndoAction);
			// executeTransaction(pair);
			// } else if (map != null) {
			// createNewMap(map);
			// } else {
			// logger.info("Shutting down was signalled.");
			// rs.close();
			// // session has ended.
			// SocketBasics.togglePermanentHook(mController);
			// // and end.
			// return;
			// }
			// }
			// rs.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public void processCommand(CollaborationActionBase command) {
		switch (mCurrentState) {
		case STATE_WAIT_FOR_HELLO:
			if (command instanceof CollaborationHello) {
				CollaborationHello commandHello = (CollaborationHello) command;
				// verify password:
				if (mConnection.getPassword()
						.equals(commandHello.getPassword())) {
					// send map:
					CollaborationWelcome welcomeCommand = new CollaborationWelcome();
					welcomeCommand.setMap("</map>");
					welcomeCommand.setFilename("///");
					send(welcomeCommand);
					mCurrentState = STATE_IDLE;
				}
			}
			break;
		case STATE_WAIT_FOR_COMMAND:
			break;
		case STATE_IDLE:
			break;
		}
	}

}