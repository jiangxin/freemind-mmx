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
import java.net.Socket;

import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationHello;
import freemind.controller.actions.generated.instance.CollaborationWelcome;
import freemind.controller.actions.generated.instance.CollaborationWhoAreYou;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * @date 06.09.2012
 */
public class ClientCommunication extends CommunicationBase {

	private MindMapClient mSocketConnector;

	/**
	 * @param pSocketConnector 
	 * @param pName
	 * @param pClient
	 * @param pController
	 * @param pOut
	 * @param pIn
	 * @throws IOException
	 */
	public ClientCommunication(MindMapClient pSocketConnector, String pName, Socket pClient,
			MindMapController pController) throws IOException {
		super(pName, pClient, pController, new DataOutputStream(
				pClient.getOutputStream()), new DataInputStream(
				pClient.getInputStream()));
		mSocketConnector = pSocketConnector;
		mCurrentState = STATE_WAIT_FOR_WHO_ARE_YOU;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.CommunicationBase#processCommand(freemind
	 * .controller.actions.generated.instance.CollaborationActionBase)
	 */
	public void processCommand(CollaborationActionBase pCommand) {
		switch (mCurrentState) {
		case STATE_WAIT_FOR_WHO_ARE_YOU:
			if (pCommand instanceof CollaborationWhoAreYou) {
//				CollaborationWhoAreYou whoCommand = (CollaborationWhoAreYou) pCommand;
				// send hello:
				CollaborationHello helloCommand = new CollaborationHello();
				helloCommand.setUserId(Tools.getUserName());
				helloCommand.setPassword(mSocketConnector.getPassword());
				send(helloCommand);
				mCurrentState = STATE_WAIT_FOR_WELCOME;
			}
			break;
		case STATE_WAIT_FOR_WELCOME:
			break;
		case STATE_IDLE:
			break;
		default:
			logger.warning("Received unknown message of type "
					+ pCommand.getClass());

		}
	}

}
