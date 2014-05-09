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

import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.controller.actions.generated.instance.CollaborationTransaction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 06.09.2012
 */
public abstract class CommunicationBase extends TerminateableThread {

	/**
	 * 
	 */
	private static final int TIMEOUT_FOR_READ_ERROR_IN_MS = 30000;
	/**
	 * 
	 */
	private static final String STRING_CONTINUATION_SUFFIX = "<cont>";
	protected Socket mSocket;

	/**
	 * @param pName
	 * @param pClient
	 * @param pMindMapController
	 * @param pOut
	 * @param pIn
	 */
	public CommunicationBase(String pName, Socket pClient,
			ExtendedMapFeedback pMindMapController, DataOutputStream pOut,
			DataInputStream pIn) {
		super(pName);
		mSocket = pClient;
		mController = pMindMapController;
		out = pOut;
		in = pIn;
	}

	protected ExtendedMapFeedback mController;
	protected DataOutputStream out;
	protected DataInputStream in;
	protected static final int ROUNDTRIP_ROUNDS = 200;
	protected static final int ROUNDTRIP_TIMEOUT = 20000;
	public static final int STATE_IDLE = 0;
	public static final int STATE_WAIT_FOR_HELLO = 1;
	public static final int STATE_WAIT_FOR_COMMAND = 2;
	public static final int STATE_WAIT_FOR_WHO_ARE_YOU = 3;
	public static final int STATE_WAIT_FOR_GET_OFFERS = 4;
	public static final int STATE_WAIT_FOR_OFFER = 5;
	public static final int STATE_WAIT_FOR_WELCOME = 6;
	public static final int STATE_WAIT_FOR_LOCK = 7;
	public static final int STATE_LOCK_RECEIVED = 8;
	private static final int MAX_STRING_LENGTH_TO_SEND = 65500;

	private int mCurrentState = STATE_IDLE;
	private String mCurrentStateMutex = "lockme";
	private StringBuffer mCurrentCommand = new StringBuffer();

	/**
	 * @param pMessage
	 * @return true, if successful.
	 */
	public synchronized boolean send(CollaborationActionBase pCommand) {
		try {
			printCommand("Send", pCommand);
			final String marshalledText = Tools.marshall(pCommand);
			logger.fine(getName() + " :Sending " + marshalledText);
			String text = Tools.compress(marshalledText);
			// split into pieces, as the writeUTF method is only able to send
			// 65535 bytes...
			int index = 0;
			while (index + MAX_STRING_LENGTH_TO_SEND < text.length()) {
				out.writeUTF(text.substring(index, index
						+ MAX_STRING_LENGTH_TO_SEND)
						+ STRING_CONTINUATION_SUFFIX);
				index += MAX_STRING_LENGTH_TO_SEND;
			}
			out.writeUTF(text.substring(index));
			return true;
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		return false;
	}

	boolean mConnectionErrorOccuredEarlier = false;
	long mFirstErrorOnConnection = 0l;
	boolean mTerminateSent = false;
	
	public boolean processAction() throws Exception {
		boolean didSomething = false;
		try {
			// Non blocking!!
			String text="";
			try {
				text = in.readUTF();
				mConnectionErrorOccuredEarlier = false;
			} catch (java.io.EOFException e) {
				freemind.main.Resources.getInstance().logException(e);
				if(mConnectionErrorOccuredEarlier) {
					// already lost earlier. check time
					if(System.currentTimeMillis() - mFirstErrorOnConnection > TIMEOUT_FOR_READ_ERROR_IN_MS) {
						if (!mTerminateSent) {
							new Thread(new Runnable() {
								public void run() {
									// 30sec. nothing. break
									try {
										terminateSocket();
									} catch (IOException e) {
										freemind.main.Resources.getInstance().logException(e);
									}
								}
							}).start();
							mTerminateSent = true;
							return false;
						}
					}
				} else {
					mConnectionErrorOccuredEarlier = true;
					mFirstErrorOnConnection = System.currentTimeMillis();
				}
			}
			if (text.endsWith(STRING_CONTINUATION_SUFFIX)) {
				mCurrentCommand.append(text.substring(0, text.length()
						- STRING_CONTINUATION_SUFFIX.length()));
				didSomething = true;
			} else {
				mCurrentCommand.append(text);
				final String textValue = mCurrentCommand.toString();
				mCurrentCommand.setLength(0);
				final String decompressedText = Tools.decompress(textValue);
				logger.fine(getName() + " :Received " + decompressedText);
				final CollaborationActionBase command = (CollaborationActionBase) Tools
						.unMarshall(decompressedText);
				if (command != null) {
					printCommand("Receive", command);
					Runnable runnable = new Runnable() {
						public void run() {
							try {
								processCommand(command);
							} catch (Exception e) {
								freemind.main.Resources.getInstance().logException(e);
							}
						}
					};
					if (command instanceof CollaborationTransaction) {
						// inserted in event queue here, to avoid
						// concurrency issues.
						EventQueue.invokeLater(runnable);						
					} else  {
						// other commands than transactions are processed directly.
						runnable.run();
					}
					didSomething = true;
				}
			}
		} catch (SocketTimeoutException e) {
		}

		mCounter--;
		if (mCounter <= 0) {
			mCounter = 10;
			if (mController instanceof MindMapController) {
				MindMapController mindMapController = (MindMapController) mController;
				mindMapController.getController().setTitle();
			}
		}
		return didSomething;
	}

	/**
	 * @param pDirection
	 * @param pCommand
	 */
	private void printCommand(String pDirection,
			CollaborationActionBase pCommand) {
		if (pCommand instanceof CollaborationTransaction) {
			CollaborationTransaction trans = (CollaborationTransaction) pCommand;
			XmlAction doAction = Tools.unMarshall(trans.getDoAction());
			String out = pDirection + ": " + Tools.printXmlAction(doAction)
					+ " (Id: " + trans.getId() + ")";
			logger.info(out);
		} else {
			String out = pDirection + ": " + Tools.printXmlAction(pCommand);
			logger.info(out);
			
		}
	}

	int mCounter = 1;

	public abstract void terminateSocket() throws IOException;
	
	public abstract void processCommand(CollaborationActionBase command)
			throws Exception;

	public int getCurrentState() {
		synchronized (mCurrentStateMutex) {
			return mCurrentState;
		}
	}

	public void setCurrentState(int pCurrentState) {
		synchronized (mCurrentStateMutex) {
			mCurrentState = pCurrentState;
		}
	}

	/**
	 * @param pDoAction
	 * @param pUndoAction
	 * @param pLockId
	 */
	public void sendCommand(String pDoAction, String pUndoAction, String pLockId) {
		CollaborationTransaction trans = new CollaborationTransaction();
		trans.setDoAction(pDoAction);
		trans.setUndoAction(pUndoAction);
		trans.setId(pLockId);
		send(trans);
	}

	public void close() throws IOException {
		mSocket.close();
	}

	public ActionPair getActionPair(CollaborationTransaction trans) {
		return new ActionPair(Tools.unMarshall(trans.getDoAction()),
				Tools.unMarshall(trans.getUndoAction()));
	}

	public String getIpToSocket() {
		return mSocket.getLocalAddress().getHostAddress();
	}

	protected void printWrongState(CollaborationActionBase pCommand) {
		logger.warning("Wrong state for " + pCommand.getClass() + ": "
				+ printState(getCurrentState()));
	}

	/**
	 * @param pCurrentState
	 * @return
	 */
	protected String printState(int pCurrentState) {
		switch (pCurrentState) {
		case STATE_IDLE:
			return "STATE_IDLE";
		case STATE_WAIT_FOR_HELLO:
			return "STATE_WAIT_FOR_HELLO";
		case STATE_WAIT_FOR_COMMAND:
			return "STATE_WAIT_FOR_COMMAND";
		case STATE_WAIT_FOR_WHO_ARE_YOU:
			return "STATE_WAIT_FOR_WHO_ARE_YOU";
		case STATE_WAIT_FOR_WELCOME:
			return "STATE_WAIT_FOR_WELCOME";
		case STATE_WAIT_FOR_LOCK:
			return "STATE_WAIT_FOR_LOCK";
		case STATE_LOCK_RECEIVED:
			return "STATE_LOCK_RECEIVED";
		case STATE_WAIT_FOR_OFFER:
			return "STATE_WAIT_FOR_OFFER";
		case STATE_WAIT_FOR_GET_OFFERS:
			return "STATE_WAIT_FOR_GET_OFFERS";
		}
		return "UNKNOWN: " + pCurrentState;
	}
	
	/**
	 * @param pNewModeController
	 */
	protected void setController(ExtendedMapFeedback pNewModeController) {
		mController = pNewModeController;
	}
	
	/**
	 * @return the controller
	 */
	public ExtendedMapFeedback getController() {
		return mController;
	}



}