/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2008  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 28.12.2008
 */


package plugins.collaboration.socket;

import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import freemind.common.NumberProperty;
import freemind.common.StringProperty;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Resources;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * 
 */
public class MindMapClient extends SocketBasics {

	private static final String HOST_PROPERTY = "plugins.collaboration.database.host";

	public void startupMapHook() {
		super.startupMapHook();
		MindMapController controller = getMindMapController();
		StringProperty passwordProperty = new StringProperty(
				PASSWORD_DESCRIPTION, PASSWORD);
		StringProperty hostProperty = new StringProperty(HOST_DESCRIPTION, HOST);
		NumberProperty portProperty = getPortProperty();
		try {
			SocketConnectionHook connectionHook = isConnected();
			if (connectionHook != null) {
				// I'm already present, so remove me.
				logger.info("Deregister filter.");
				connectionHook.deregisterFilter();
				logger.info("Shutting down the permanent hook.");
				togglePermanentHook(controller, SLAVE_HOOK_LABEL);
				return;
			}
			// get last value
			hostProperty.setValue(controller.getFrame().getProperty(
					HOST_PROPERTY));
			Vector controls = new Vector();
			controls.add(passwordProperty);
			controls.add(hostProperty);
			controls.add(portProperty);
			FormDialog dialog = new FormDialog(controller);
			dialog.setUp(controls);
			if (!dialog.isSuccess())
				return;
			logger.info("Connect...");
			setPortProperty(portProperty);
			// store value for next time.
			controller.getFrame().setProperty(HOST_PROPERTY,
					hostProperty.getValue());
			mPassword = passwordProperty.getValue();
			logger.info("Starting client thread...");
			int port = portProperty.getIntValue();
			Socket serverConnection = new Socket(hostProperty.getValue(), port);
			serverConnection
					.setSoTimeout(MindMapMaster.SOCKET_TIMEOUT_IN_MILLIES);
			ClientCommunication clientCommunication = new ClientCommunication(
					"Client Communication", serverConnection,
					getMindMapController(), mPassword);
			clientCommunication.start();
		} catch (UnknownHostException e) {
			freemind.main.Resources.getInstance().logException(e);
			controller.getController().errorMessage(
					Resources.getInstance().format(
							UNKNWON_HOST_EXCEPTION_MESSAGE,
							new Object[] { e.getMessage() }));
			return;
		} catch (ConnectException e) {
			freemind.main.Resources.getInstance().logException(e);
			controller.getController().errorMessage(
					Resources.getInstance().format(
							CONNECT_EXCEPTION_MESSAGE,
							new Object[] { portProperty.getValue(),
									hostProperty.getValue(), e.getMessage() }));
			return;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			// Need a better message here.
			controller.getController().errorMessage(
					e.getClass().getName() + ": " + e.getLocalizedMessage());
			return;
		}
	}

	private SocketConnectionHook isConnected() {
		Collection activatedHooks = getMindMapController().getRootNode()
				.getActivatedHooks();
		for (Iterator it = activatedHooks.iterator(); it.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) it.next();
			if (hook instanceof SocketConnectionHook) {
				return (SocketConnectionHook) hook;
			}
		}
		return null;
	}

	public Integer getRole() {
		return ROLE_SLAVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getPort()
	 */
	public int getPort() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#lock()
	 */
	protected String lock(String pUserName) throws UnableToGetLockException,
			InterruptedException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.collaboration.socket.SocketBasics#sendCommand(java.lang.String,
	 * java.lang.String)
	 */
	protected void broadcastCommand(String pDoAction, String pUndoAction,
			String pLockId) throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#unlock()
	 */
	protected void unlock() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#shutdown()
	 */
	public void shutdown() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugins.collaboration.socket.SocketBasics#getMasterInformation()
	 */
	public CollaborationUserInformation getMasterInformation() {
		return null;
	}

}
