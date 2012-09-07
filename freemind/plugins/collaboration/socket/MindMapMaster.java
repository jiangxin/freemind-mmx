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
/* $Id: DatabaseStarter.java,v 1.1.2.4 2009/02/05 22:12:37 christianfoltin Exp $ */

package plugins.collaboration.socket;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import freemind.common.NumberProperty;
import freemind.common.StringProperty;
import freemind.controller.actions.generated.instance.CollaborationActionBase;
import freemind.extensions.DontSaveMarker;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionFilter.FinalActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class MindMapMaster extends SocketBasics implements PermanentNodeHook,
		DontSaveMarker, FinalActionFilter {

	MasterThread mListener;
	ServerSocket mServer;
	Vector connections = new Vector();
	protected boolean mFilterEnabled = true;
	protected boolean mLockEnabled = false;
	private ServerCommunication mLockedBy = null;
	private long mLockedAt = 0L;
	private int mPort;

	private class MasterThread extends TerminateableThread {

		/**
		 * @param pName
		 */
		public MasterThread() {
			super("Master");
		}

		/* (non-Javadoc)
		 * @see plugins.collaboration.socket.TerminateableThread#processAction()
		 */
		public boolean processAction() throws Exception {
			try {
				Socket client = mServer.accept();
				ServerCommunication c = new ServerCommunication(
						MindMapMaster.this, client, getMindMapController());
				c.start();
				connections.addElement(c);
			} catch (SocketTimeoutException e) {
			}
			return true;
		}
		
	}
	

	public synchronized void broadcast(CollaborationActionBase message) {
		for (int i = 0; i < connections.size(); i++) {
			((ServerCommunication) connections.elementAt(i)).send(message);
		}
	}

	public synchronized void closeConnection(ServerCommunication client) {
		connections.remove(client);
	}

	public void startupMapHook() {
		super.startupMapHook();
		MindMapController controller = getMindMapController();
		final StringProperty passwordProperty = new StringProperty(
				PASSWORD_DESCRIPTION, PASSWORD);
		final StringProperty passwordProperty2 = new StringProperty(
				PASSWORD_VERIFICATION_DESCRIPTION, PASSWORD_VERIFICATION);
		// StringProperty bindProperty = new StringProperty(
		// "IP address of the local machine, or 0.0.0.0 if ", "Host");
		final NumberProperty portProperty = getPortProperty();
		Vector controls = new Vector();
		controls.add(passwordProperty);
		controls.add(passwordProperty2);
		// controls.add(bindProperty);
		controls.add(portProperty);
		FormDialog dialog = new FormDialog(controller);
		dialog.setUp(controls, new FormDialogValidator() {
			public boolean isValid() {
				logger.finest("Output valid?");
				return Tools.safeEquals(passwordProperty.getValue(),
						passwordProperty2.getValue());
			}
		});
		if (!dialog.isSuccess())
			return;
		/* Store port value in preferences. */ 
		setPortProperty(portProperty);
		mPassword = passwordProperty.getValue();
		// start server:
		logger.info("Start server...");
		try {
			mPort = getPortProperty().getIntValue();
			mServer = new ServerSocket(mPort);
			mServer.setSoTimeout(10000);
			mListener = new MasterThread();
			mListener.start();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			// TODO: Need a better message here.
			controller.getController().errorMessage(e.getLocalizedMessage());
			if (mListener != null) {
				mListener.commitSuicide();
			}
			return;
		}
	}

	public void loadFrom(XMLElement pChild) {
		// this plugin should not be saved.
	}

	public void save(XMLElement pXml) {
		// this plugin should not be saved.
		// nothing to do.
	}

	public void shutdownMapHook() {
		deregisterFilter();
		if (mListener != null) {
			signalEndOfSession();
			mListener.commitSuicide();
		}
		super.shutdownMapHook();
	}

	/**
	 * 
	 */
	private void signalEndOfSession() {
		// TODO Send information to all		
	}

	public void onAddChild(MindMapNode pAddedChildNode) {
	}

	public void onAddChildren(MindMapNode pAddedChild) {
	}

	public void onLostFocusNode(NodeView pNodeView) {
	}

	public void onNewChild(MindMapNode pNewChildNode) {
	}

	public void onRemoveChild(MindMapNode pOldChildNode) {
	}

	public void onRemoveChildren(MindMapNode pOldChildNode, MindMapNode pOldDad) {
	}

	public void onFocusNode(NodeView pNodeView) {
	}

	public void onUpdateChildrenHook(MindMapNode pUpdatedNode) {
	}

	public void onUpdateNodeHook() {
	}

	public void onViewCreatedHook(NodeView pNodeView) {
	}

	public void onViewRemovedHook(NodeView pNodeView) {
	}

	public Integer getRole() {
		return ROLE_MASTER;
	}

	/** 
	 * These are local changes here. Only possible, if no lock is set.
	 */
	public ActionPair filterAction(ActionPair pPair) {
		if (pPair == null || !mFilterEnabled)
			return pPair;
		String doAction = getMindMapController().marshall(pPair.getDoAction());
		String undoAction = getMindMapController().marshall(pPair.getUndoAction());
		// try {
		// } catch (SQLException e) {
		// freemind.main.Resources.getInstance().logException(e);
		// }
		return pPair;
	}

	protected void deregisterFilter() {
		logger.info("Deregistering filter");
		getMindMapController().getActionFactory().deregisterFilter(this);
	}

	protected void registerFilter() {
		logger.info("Registering filter");
		getMindMapController().getActionFactory().registerFilter(this);
	}
	
	private void executeTransaction(final ActionPair pair)
			throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mFilterEnabled = false;
				try {
					getMindMapController().getActionFactory().startTransaction("update");
					getMindMapController().getActionFactory().executeAction(pair);
					getMindMapController().getActionFactory().endTransaction("update");
				} finally {
					mFilterEnabled = true;
				}
			}
		});
	}

	private void createNewMap(String map) throws IOException {
		{
			// deregister from old controller:
			deregisterFilter();
			logger.info("Restoring the map...");
			MindMapController newModeController = (MindMapController) getMindMapController()
					.getMode().createModeController();
			MapAdapter newModel = new MindMapMapModel(getMindMapController().getFrame(),
					newModeController);
			HashMap IDToTarget = new HashMap();
			StringReader reader = new StringReader(map);
			MindMapNodeModel rootNode = (MindMapNodeModel) newModeController
					.createNodeTreeFromXml(reader, IDToTarget);
			reader.close();
			newModel.setRoot(rootNode);
			rootNode.setMap(newModel);
			getMindMapController().newMap(newModel);
			newModeController.invokeHooksRecursively((NodeAdapter) rootNode,
					newModel);
			setController(newModeController);
			// add new hook
			SocketBasics.togglePermanentHook(getMindMapController());
			// tell him about this thread.
			Collection activatedHooks = getMindMapController().getRootNode()
					.getActivatedHooks();
			for (Iterator it = activatedHooks.iterator(); it.hasNext();) {
				PermanentNodeHook hook = (PermanentNodeHook) it.next();
				if (hook instanceof SocketConnectionHook) {
					SocketConnectionHook connHook = (SocketConnectionHook) hook;
					// TODO: Tell anybody??
//					connHook.setUpdateThread(this);
					break;
				}
			}
			/* register as listener, as I am a slave. */
			registerFilter();
		}
	}

	/* (non-Javadoc)
	 * @see plugins.collaboration.socket.SocketBasics#getPort()
	 */
	public int getPort() {
		return mPort;
	}





	
}
