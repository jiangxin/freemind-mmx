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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import freemind.common.NumberProperty;
import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapTitleContributor;
import freemind.controller.actions.generated.instance.CollaborationUserInformation;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionFilter.FirstActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.MapModule;

public abstract class SocketBasics extends MindMapNodeHookAdapter implements
		MapTitleContributor, FirstActionFilter {

	/**
	 * 
	 */
	private static final String PLUGINS_COLLABORATION_SOCKET = "plugins/collaboration/socket/";
	public final static String MASTER_HOOK_LABEL = PLUGINS_COLLABORATION_SOCKET
			+ "socket_master_plugin";
	public final static String SLAVE_HOOK_LABEL = PLUGINS_COLLABORATION_SOCKET
			+ "socket_slave_plugin";
	public final static String SLAVE_STARTER_LABEL = PLUGINS_COLLABORATION_SOCKET
			+ "socket_slave_starter_plugin";
	protected static final Integer ROLE_MASTER = Integer.valueOf(0);
	protected static final Integer ROLE_SLAVE = Integer.valueOf(1);
	private static final String PORT_PROPERTY = "plugins.collaboration.socket.port";
	private static final String SOCKET_BASICS_CLASS = "plugins.collaboration.socket.SocketBasics";

	protected static final String PASSWORD = SOCKET_BASICS_CLASS + ".password";
	protected static final String PASSWORD_DESCRIPTION = SOCKET_BASICS_CLASS
			+ ".password.description";

	protected static final String PASSWORD_VERIFICATION = SOCKET_BASICS_CLASS
			+ ".password_verification";
	protected static final String PASSWORD_VERIFICATION_DESCRIPTION = SOCKET_BASICS_CLASS
			+ ".password_verification_description";

	protected static final String HOST = SOCKET_BASICS_CLASS + ".host";
	protected static final String HOST_DESCRIPTION = SOCKET_BASICS_CLASS
			+ ".host.description";

	protected static final String PORT = SOCKET_BASICS_CLASS + ".port";
	protected static final String PORT_DESCRIPTION = PORT + ".description";

	protected static final String TITLE = SOCKET_BASICS_CLASS + ".title";
	protected static final String UNKNWON_HOST_EXCEPTION_MESSAGE = SOCKET_BASICS_CLASS
			+ ".unknown_host_exception";
	protected static final String CONNECT_EXCEPTION_MESSAGE = SOCKET_BASICS_CLASS
			+ ".connection_exception";
	protected static final String SOCKET_CREATION_EXCEPTION_MESSAGE = SOCKET_BASICS_CLASS
			+ ".socket_creation_exception";;

	protected static java.util.logging.Logger logger = null;

	protected String mPassword;
	protected boolean mFilterEnabled = true;
	private String mUserName;

	public SocketBasics() {
		super();
		mUserName = Tools.getUserName();
	}

	/**
	 * @return ROLE_MASTER OR ROLE_SLAVE
	 */
	public abstract Integer getRole();

	public void startupMapHook() {
		super.startupMapHook();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		getMindMapController().getController()
				.registerMapTitleContributor(this);
	}

	public void shutdownMapHook() {
		Controller controller = getMindMapController().getController();
		controller.deregisterMapTitleContributor(this);
		controller.setTitle();
		super.shutdownMapHook();
	}

	public static void togglePermanentHook(MindMapController controller,
			final String hookName) {
		MindMapNode rootNode = controller.getRootNode();
		List selecteds = Arrays.asList(new MindMapNode[] { rootNode });
		controller.addHook(rootNode, selecteds, hookName, null);
	}

	protected void setPortProperty(final NumberProperty portProperty) {
		getMindMapController().getFrame().setProperty(PORT_PROPERTY,
				portProperty.getValue());
	}

	protected NumberProperty getPortProperty() {
		final NumberProperty portProperty = new NumberProperty(
				PORT_DESCRIPTION, PORT, 1024, 32767, 1);
		// fill values:
		portProperty.setValue(""
				+ getMindMapController().getFrame().getIntProperty(
						PORT_PROPERTY, 9001));
		return portProperty;
	}

	public static abstract class FormDialogValidator {
		/**
		 * @return true, if ok should be enabled.
		 */
		public abstract boolean isValid();
	}

	public static class FormDialog extends JDialog implements
			PropertyChangeListener {
		private final MindMapController mController2;
		private boolean mSuccess = false;
		private JButton mOkButton;
		private FormDialogValidator mFormDialogValidator;

		public boolean isSuccess() {
			return mSuccess;
		}

		public FormDialog(MindMapController pController) {
			super(pController.getFrame().getJFrame());
			mController2 = pController;
		}

		public void setUp(Vector controls) {
			setUp(controls, new FormDialogValidator() {

				public boolean isValid() {
					return true;
				}
			});
		}

		public void setUp(Vector controls, FormDialogValidator pValidator) {
			mFormDialogValidator = pValidator;
			setModal(true);
			getContentPane().setLayout(new BorderLayout());
			FormLayout formLayout = new FormLayout(
					"right:max(40dlu;p), 4dlu, 80dlu, 7dlu", "");
			DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
			builder.setDefaultDialogBorder();
			for (Iterator it = controls.iterator(); it.hasNext();) {
				PropertyControl prop = (PropertyControl) it.next();
				prop.layout(builder, mController2);
				PropertyBean bean = (PropertyBean) prop;
				bean.addPropertyChangeListener(this);
			}
			getContentPane().add(builder.getPanel(), BorderLayout.CENTER);
			JButton cancelButton = new JButton(getText("Cancel"));
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}

			});
			mOkButton = new JButton(getText("OK"));
			mOkButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					mSuccess = true;
					closeWindow();
				}

			});
			getRootPane().setDefaultButton(mOkButton);
			getContentPane().add(
					ButtonBarFactory.buildOKCancelBar(cancelButton, mOkButton),
					BorderLayout.SOUTH);
			setTitle("Enter Password Dialog");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					closeWindow();
				}
			});
			Action action = new AbstractAction() {

				public void actionPerformed(ActionEvent arg0) {
					closeWindow();
				}
			};
			Action actionSuccess = new AbstractAction() {

				public void actionPerformed(ActionEvent arg0) {
					mSuccess = true;
					closeWindow();
				}
			};
			Tools.addEscapeActionToDialog(this, action);
			Tools.addKeyActionToDialog(this, actionSuccess, "ENTER",
					"ok_dialog");

			pack();
			setVisible(true);

		}

		private void closeWindow() {
			setVisible(false);
		}

		String getText(String text) {
			return text;
		}

		public void propertyChange(PropertyChangeEvent pEvt) {
			logger.finest("Property change " + pEvt);
			mOkButton.setEnabled(mFormDialogValidator.isValid());
		}

	}

	public abstract int getPort();

	public String getMapTitle(String pOldTitle, MapModule pMapModule,
			MindMap pModel) {
		if (pModel.getModeController() != getMindMapController()) {
			return pOldTitle;
		}
		CollaborationUserInformation userInfo = getMasterInformation();
		if (userInfo == null) {
			return pOldTitle;
		}
		return pOldTitle
				+ Resources.getInstance().format(
						TITLE,
						new Object[] { this.getRole(),
								userInfo.getMasterHostname(),
								userInfo.getMasterIp(),
								new Integer(userInfo.getMasterPort()),
								userInfo.getUserIds() });
	}

	public abstract CollaborationUserInformation getMasterInformation();

	public String getPassword() {
		return mPassword;
	}

	/**
	 * Deep search inside the {@link XmlAction} to find a hook (i.e. myself).
	 * They should not be send over the wire.
	 * 
	 * @param pAction
	 * @param pSearchString
	 * @return
	 */
	private boolean visit(XmlAction pAction, String pSearchString) {
		if (pAction instanceof CompoundAction) {
			CompoundAction compound = (CompoundAction) pAction;
			boolean result = false;
			for (Iterator it = compound.getListChoiceList().iterator(); it
					.hasNext();) {
				XmlAction action = (XmlAction) it.next();
				result |= visit(action, pSearchString);
			}
			return result;
		}
		if (pAction instanceof HookNodeAction) {
			HookNodeAction hookNodeAction = (HookNodeAction) pAction;
			if (Tools.safeEquals(hookNodeAction.getHookName(), pSearchString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Try to lock, send update package to master (perhaps, myself), execute
	 * action and unlock
	 */
	public ActionPair filterAction(ActionPair pPair) {
		if (pPair == null || !mFilterEnabled)
			return pPair;
		// Don't send any hook instantiations to others.
		if (visit(pPair.getDoAction(), SocketConnectionHook.SLAVE_HOOK_LABEL)) {
			return pPair;
		}
		if (visit(pPair.getDoAction(), MindMapMaster.MASTER_HOOK_LABEL)) {
			return pPair;
		}
		String doAction = getMindMapController().marshall(pPair.getDoAction());
		String undoAction = getMindMapController().marshall(
				pPair.getUndoAction());
		logger.info("Require lock for command: " + doAction);
		try {
			String lockId = lock(getUserName());
			/*
			 * Blocking broadcast call: Client: send to master (who broadcasts
			 * the command afterwards), Master: send to all clients.
			 */
			broadcastCommand(doAction, undoAction, lockId);
		} catch (UnableToGetLockException e) {
			freemind.main.Resources.getInstance().logException(e);
			return getEmptyActionPair();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return getEmptyActionPair();
		} finally {
			unlock();
		}
		return pPair;
	}

	public ActionPair getEmptyActionPair() {
		return new ActionPair(new CompoundAction(), new CompoundAction());
	}

	protected static class UnableToGetLockException extends Exception {

	}

	/**
	 * @param pUserName
	 *            the user the lock belongs to.
	 * @return The id associated with this lock.
	 * @throws UnableToGetLockException
	 * @throws InterruptedException
	 */
	protected abstract String lock(String pUserName)
			throws UnableToGetLockException, InterruptedException;

	/**
	 * @return the user's name (to acquire a named lock)
	 */
	protected String getUserName() {
		return mUserName;
	}

	/**
	 * Should send the command to the master, or, if the master itself, sends it
	 * to the clients.
	 * 
	 * @throws Exception
	 */
	protected abstract void broadcastCommand(String pDoAction,
			String pUndoAction, String pLockId) throws Exception;

	/**
	 * Unlocks the previous lock
	 */
	protected abstract void unlock();

	protected void registerFilter() {
		logger.info("Registering filter");
		getMindMapController().getActionFactory().registerFilter(this);
	}

	protected void deregisterFilter() {
		logger.info("Deregistering filter");
		getMindMapController().getActionFactory().deregisterFilter(this);
	}

	protected void executeTransaction(final ActionPair pair) {
		mFilterEnabled = false;
		try {
			getMindMapController().doTransaction("update", pair);
		} finally {
			mFilterEnabled = true;
		}
	}

	/**
	 * Closes the connection.
	 */
	public abstract void shutdown();

}
