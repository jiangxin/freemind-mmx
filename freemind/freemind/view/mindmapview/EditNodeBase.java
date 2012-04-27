/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 02.05.2004
 */
/*$Id: EditNodeBase.java,v 1.1.4.2.12.14 2008/12/16 21:57:01 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import freemind.controller.Controller;
import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.ModeController;

/**
 * @author foltin
 * 
 */
public class EditNodeBase {
	protected static boolean checkSpelling = Resources.getInstance().
    		getBoolProperty(FreeMindCommon.CHECK_SPELLING);;
	protected static final int BUTTON_OK = 0;
	protected static final int BUTTON_CANCEL = 1;
	protected static final int BUTTON_SPLIT = 2;
	protected NodeView node;
	private EditControl editControl;
	private ModeController controller;
	protected String text;
	// this enables from outside close the edit mode
	protected FocusListener textFieldListener = null;

	EditNodeBase(final NodeView node, final String text,
			ModeController controller, EditControl editControl)
	{
		this.controller = controller;
		this.editControl = editControl;
		this.node = node;
		this.text = text;
	}

	abstract static class EditDialog extends JDialog {
		private static final long serialVersionUID = 6064679828160694117L;
		private EditNodeBase base;

		class DialogWindowListener extends WindowAdapter {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			public void windowClosing(WindowEvent e) {
				if (isVisible()) {
					confirmedSubmit();
				}
			}
		}

		class SubmitAction extends AbstractAction {
			private static final long serialVersionUID = -859458051986869388L;

			public void actionPerformed(ActionEvent e) {
				submit();
			}
		}

		class SplitAction extends AbstractAction {
			private static final long serialVersionUID = 6876147686811246433L;

			public void actionPerformed(ActionEvent e) {
				split();
			}
		}

		class CancelAction extends AbstractAction {
			private static final long serialVersionUID = -6277471363654329607L;

			public void actionPerformed(ActionEvent e) {
				confirmedCancel();
			}
		}

		EditDialog(EditNodeBase base) {
			super((JFrame) base.getFrame(), base.getText("edit_long_node"),
					/*modal = */ true);
			getContentPane().setLayout(new BorderLayout());
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			DialogWindowListener dfl = new DialogWindowListener();
			addWindowListener(dfl);
			this.base = base;
		}

		protected void confirmedSubmit() {
			if (isChanged()) {
				final int action = JOptionPane.showConfirmDialog(this,
						base.getText("long_node_changed_submit"), "",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (action == JOptionPane.CANCEL_OPTION)
					return;
				if (action == JOptionPane.YES_OPTION) {
					submit();
					return;
				}
			}
			cancel();
		}

		protected void confirmedCancel() {
			if (isChanged()) {
				final int action = JOptionPane.showConfirmDialog(this,
						base.getText("long_node_changed_cancel"), "",
						JOptionPane.OK_CANCEL_OPTION);
				if (action == JOptionPane.CANCEL_OPTION)
					return;
			}
			cancel();
		}

		protected void submit() {
			setVisible(false);
		}

		protected void cancel() {
			setVisible(false);
		}

		protected void split() {
			setVisible(false);
		}

		abstract protected boolean isChanged();

		/**
		 * @param base
		 *            The base to set.
		 */
		void setBase(EditNodeBase base) {
			this.base = base;
		}

		/**
		 * @return Returns the base.
		 */
		EditNodeBase getBase() {
			return base;
		}
	}

	public interface EditControl {
		void cancel();
		void ok(String newText);
		void split(String newText, int position);
	}

	protected MapView getView() {
		return controller.getView();
	}

	protected ModeController getModeController() {
		return controller;
	}

	protected Controller getController() {
		return controller.getController();
	}

	protected String getText(String string) {
		return controller.getText(string);
	}

	protected FreeMindMain getFrame() {
		return controller.getFrame();
	}

	protected boolean binOptionIsTrue(String option) {
		return Resources.getInstance().getBoolProperty(option);
	}

	protected class EditCopyAction extends AbstractAction {
		private static final long serialVersionUID = 5104219263806454592L;
		private JTextComponent textComponent;

		public EditCopyAction(JTextComponent textComponent) {
			super(getText("copy"));
			this.textComponent = textComponent;
		}

		public void actionPerformed(ActionEvent e) {
			String selection = textComponent.getSelectedText();
			if (selection != null) {
				getClipboard().setContents(new StringSelection(selection), null);
			}
		}
	}

	protected class EditPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = -6667980271052571216L;

		public EditPopupMenu(JTextComponent textComponent) {
			EditCopyAction editCopyAction = new EditCopyAction(textComponent);
			String selectedText = textComponent.getSelectedText();
			if (selectedText == null || selectedText.equals("")) {
				editCopyAction.setEnabled(false);
			}
			this.add(editCopyAction);
		}
	}

	public void closeEdit() {
		if (textFieldListener != null) {
			textFieldListener.focusLost(null); // hack to close the edit
		}
	}

	protected String getText() {
		return text;
	}

	public Clipboard getClipboard() {
		return Tools.getClipboard();
	}

	public EditControl getEditControl() {
		return editControl;
	}

	public NodeView getNode() {
		return node;
	}

	public FocusListener getTextFieldListener() {
		return textFieldListener;
	}

	public void setText(String string) {
		text = string;
	}

	public void setTextFieldListener(FocusListener listener) {
		textFieldListener = listener;
	}

	protected void redispatchKeyEvents(final JTextComponent textComponent,
			KeyEvent firstKeyEvent)
	{
		if (textComponent.hasFocus()) {
			return;
		}
		final KeyboardFocusManager currentKeyboardFocusManager =
				KeyboardFocusManager.getCurrentKeyboardFocusManager();
		class KeyEventQueue implements KeyEventDispatcher, FocusListener {
			LinkedList events = new LinkedList();

			public boolean dispatchKeyEvent(KeyEvent e) {
				events.add(e);
				return true;
			}

			public void focusGained(FocusEvent e) {
				e.getComponent().removeFocusListener(this);
				currentKeyboardFocusManager.removeKeyEventDispatcher(this);
				final Iterator iterator = events.iterator();
				while (iterator.hasNext()) {
					final KeyEvent ke = (KeyEvent) iterator.next();
					ke.setSource(textComponent);
					textComponent.dispatchEvent(ke);
				}
			}

			public void focusLost(FocusEvent e) {
			}
		};
		final KeyEventQueue keyEventDispatcher = new KeyEventQueue();
		currentKeyboardFocusManager.addKeyEventDispatcher(keyEventDispatcher);
		textComponent.addFocusListener(keyEventDispatcher);
		if (firstKeyEvent == null) {
			return;
		}
		if (firstKeyEvent.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
			switch (firstKeyEvent.getKeyCode()) {
			case KeyEvent.VK_HOME:
				textComponent.setCaretPosition(0);
				break;
			case KeyEvent.VK_END:
				textComponent.setCaretPosition(textComponent.getDocument()
						.getLength());
				break;
			}
		} else {
			textComponent.selectAll(); // to enable overwrite
			// redispath all key events
			textComponent.dispatchEvent(firstKeyEvent);
		}
	}
}