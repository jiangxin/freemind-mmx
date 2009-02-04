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
/* $Id: DatabaseBasics.java,v 1.1.2.4 2009-02-04 19:31:21 christianfoltin Exp $ */
package plugins.collaboration.database;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
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
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

public class DatabaseBasics extends MindMapNodeHookAdapter  {

	public final static String SLAVE_HOOK_NAME = "plugins/collaboration/database/database_slave_plugin";
	public final static String SLAVE_STARTER_NAME = "plugins/collaboration/database/database_slave_starter_plugin";
	protected static final String ROW_PK = "PK";
	protected static final String ROW_ACTION = "do_action";
	protected static final String TABLE_XML_ACTIONS = "XmlActions";
	protected static final String ROW_UNDOACTION = "undo_action";
	protected static final String ROW_MAP = "map";
	private static final String PORT_PROPERTY = "plugins.collaboration.database.port";
	protected static java.util.logging.Logger logger = null;
	protected UpdateThread mUpdateThread = null;

	public interface ResultHandler {
		void processResults(ResultSet rs);
	}

	public DatabaseBasics() {
		super();
	}

	public void startupMapHook() {
		super.startupMapHook();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}

	protected static void togglePermanentHook(MindMapController controller) {
		MindMapNode rootNode = controller.getRootNode();
		List selecteds = Arrays.asList(new MindMapNode[] { rootNode });
		controller.addHook(rootNode, selecteds, SLAVE_HOOK_NAME);
	}
	

	protected void setPortProperty(final NumberProperty portProperty) {
		getMindMapController().getFrame().setProperty(PORT_PROPERTY,
				portProperty.getValue());
	}

	protected NumberProperty getPortProperty() {
		final NumberProperty portProperty = new NumberProperty(
				"The port to open", "Port", 1024, 32767, 1);
		// fill values:
		portProperty.setValue(""
				+ getMindMapController().getFrame().getIntProperty(PORT_PROPERTY, 9001));
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
			logger.info("Property change " + pEvt);
			mOkButton.setEnabled(mFormDialogValidator.isValid());
		}

	}

	public void setUpdateThread(UpdateThread pUpdateThread) {
		mUpdateThread = pUpdateThread;
	}

	public UpdateThread getUpdateThread() {
		return mUpdateThread;
	}

}