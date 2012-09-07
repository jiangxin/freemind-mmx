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
import freemind.controller.actions.generated.instance.CollaborationGoodbye;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * @date 06.09.2012
 */
public abstract class CommunicationBase extends TerminateableThread {

	Socket mSocket;
	/**
	 * @param pName
	 * @param pClient
	 * @param pController
	 * @param pOut
	 * @param pIn
	 */
	public CommunicationBase(String pName, Socket pClient,
			MindMapController pController, DataOutputStream pOut,
			DataInputStream pIn) {
		super(pName);
		mSocket = pClient;
		mController = pController;
		out = pOut;
		in = pIn;
	}
	protected MindMapController mController;
	protected DataOutputStream out;
	protected DataInputStream in;
	protected static final int STATE_IDLE = 0;
	protected static final int STATE_WAIT_FOR_HELLO = 1;
	protected static final int STATE_WAIT_FOR_COMMAND = 2;
	protected static final int STATE_WAIT_FOR_WHO_ARE_YOU = 3;
	protected static final int STATE_WAIT_FOR_WELCOME = 4;
	protected int mCurrentState = STATE_IDLE;

	public void shutdown(boolean pWithShutdown) {
		try {
			if (pWithShutdown) {
				// send shutdown
				CollaborationGoodbye goodbyeAction = new CollaborationGoodbye();
				goodbyeAction.setUserId("my");
				send(goodbyeAction);
			}
			// TODO: Fixme
//			mClient.close();
//			mConnection = null;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * @param pMessage
	 */
	public synchronized void send(CollaborationActionBase pCommand) {
		try {
			String text = Tools.compress(Tools.marshall(pCommand));
			out.writeUTF(text);
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public CollaborationActionBase receive(String pText) {
		return (CollaborationActionBase) Tools.unMarshall(Tools.decompress(pText));
	}

	public boolean processAction() throws Exception {
		boolean didSomething = false;
		String text;
		while ((text = in.readUTF()) != null) {
			CollaborationActionBase command = receive(text);
			if(command == null) {
				continue;
			}
			processCommand(command);
			didSomething = true;
		}
	
		mCounter--;
		if (mCounter <= 0) {
			mCounter = 10;
			mController.getController().setTitle();
		}
		return didSomething;
	}
	int  mCounter = 1;

	public abstract void processCommand(CollaborationActionBase command);

}