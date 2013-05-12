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
/*$Id: EditNodeTextField.java,v 1.1.4.3.10.25 2010/02/22 21:18:53 christianfoltin Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.inet.jortho.SpellChecker;

import freemind.main.FreeMindCommon;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author foltin
 * 
 */
public class EditNodeTextField extends EditNodeBase {
	final int EDIT = 1;
	final int CANCEL = 2;
	int cursorWidth = 1;
	int xOffset = 0;
	int yOffset = -1; // Optimized for Windows style; basically ad hoc
	int widthAddition = 2 * 0 + cursorWidth + 2;
	int heightAddition = 2;

	// minimal width for input field of leaf or folded node (PN)
	final int MINIMAL_LEAF_WIDTH = 150;
	final int MINIMAL_WIDTH = 50;
	final int MINIMAL_HEIGHT = 20;

	private KeyEvent firstEvent;
	protected JTextField textfield;
	protected JComponent mParent;
	private final JComponent mFocusListener;
	protected static java.util.logging.Logger logger = null;
	private Tools.IntHolder mEventSource;
	private UndoManager mUndoManager;

	public EditNodeTextField(final NodeView node, final String text,
			final KeyEvent firstEvent, ModeController controller,
			EditControl editControl) {
		this(node, text, firstEvent, controller, editControl, node.getMap(),
				node);
	}

	public EditNodeTextField(final NodeView node, final String text,
			final KeyEvent firstEvent, ModeController controller,
			EditControl editControl, JComponent pParent,
			JComponent pFocusListener) {
		super(node, text, controller, editControl);
		this.firstEvent = firstEvent;
		mParent = pParent;
		mFocusListener = pFocusListener;
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
	}

	public void show() {
		// Make fields for short texts editable
		textfield = (getText().length() < 8) ? new JTextField(getText(), 8)
				: new JTextField(getText());

		// Set textFields's properties

		final NodeView nodeView = getNode();
		final MindMapNode model = nodeView.getModel();
		int xSize = nodeView.getMainView().getTextWidth() + widthAddition;
		xOffset += nodeView.getMainView().getTextX();
		int xExtraWidth = 0;
		if (MINIMAL_LEAF_WIDTH > xSize
				&& (model.isFolded() || !model.hasChildren())) {
			// leaf or folded node with small size
			xExtraWidth = MINIMAL_LEAF_WIDTH - xSize;
			xSize = MINIMAL_LEAF_WIDTH; // increase minimum size
			if (nodeView.isLeft()) { // left leaf
				xExtraWidth = -xExtraWidth;
				textfield.setHorizontalAlignment(JTextField.RIGHT);
			}
		} else if (MINIMAL_WIDTH > xSize) {
			// opened node with small size
			xExtraWidth = MINIMAL_WIDTH - xSize;
			xSize = MINIMAL_WIDTH; // increase minimum size
			if (nodeView.isLeft()) { // left node
				xExtraWidth = -xExtraWidth;
				textfield.setHorizontalAlignment(JTextField.RIGHT);
			}
		}

		int ySize = nodeView.getMainView().getHeight() + heightAddition;
		if (ySize < MINIMAL_HEIGHT) {
			ySize = MINIMAL_HEIGHT;
		}
		textfield.setSize(xSize, ySize);
		Font font = nodeView.getTextFont();
		final MapView mapView = nodeView.getMap();
		final float zoom = mapView.getZoom();
		if (zoom != 1F) {
			font = font.deriveFont(font.getSize() * zoom
					* MainView.ZOOM_CORRECTION_FACTOR);
		}
		textfield.setFont(font);

		final Color nodeTextColor = nodeView.getTextColor();
		textfield.setForeground(nodeTextColor);
		final Color nodeTextBackground = nodeView.getTextBackground();
		textfield.setBackground(nodeTextBackground);
		textfield.setCaretColor(nodeTextColor);

		// textField.selectAll(); // no selection on edit (PN)

		mEventSource = new Tools.IntHolder();
		mEventSource.setValue(EDIT);

		// create the listener
		final TextFieldListener textFieldListener = new TextFieldListener();

		// Add listeners
		this.textFieldListener = textFieldListener;
		textfield.addKeyListener(textFieldListener);
		textfield.addMouseListener(textFieldListener);
		mUndoManager = new UndoManager();
		textfield.getDocument().addUndoableEditListener(mUndoManager);
		// Create an undo action and add it to the text component
		textfield.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (mUndoManager.canUndo()) {
						mUndoManager.undo();
					}
				} catch (CannotUndoException e) {
				}
			}
		});

		// Bind the undo action to ctl-Z (or command-Z on mac)
		textfield.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit
						.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");

		// Create a redo action and add it to the text component
		textfield.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (mUndoManager.canRedo()) {
						mUndoManager.redo();
					}
				} catch (CannotRedoException e) {
				}
			}
		});

		// Bind the redo action to ctl-Y (or command-Y on mac)
		textfield.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit
						.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

		// screen positionining ---------------------------------------------

		// SCROLL if necessary
		getView().scrollNodeToVisible(nodeView, xExtraWidth);
		Point mPoint = null;
		if (mPoint == null) {
			// NOTE: this must be calculated after scroll because the pane
			// location
			// changes
			mPoint = new Point();

			Tools.convertPointToAncestor(nodeView.getMainView(), mPoint,
					mapView);
			if (xExtraWidth < 0) {
				mPoint.x += xExtraWidth;
			}
			mPoint.x += xOffset;
			mPoint.y += yOffset;
		}
		setTextfieldLoaction(mPoint);

		addTextfield();
		textfield.repaint();
		redispatchKeyEvents(textfield, firstEvent);

		if (checkSpelling) {
			SpellChecker.register(textfield, false, true, true, true);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				textfield.requestFocus();
				// Add listener now, as there are focus changes before.
				textfield.addFocusListener(textFieldListener);
				mFocusListener.addComponentListener(textFieldListener);
			}
		});
	}

	// listener class
	class TextFieldListener implements KeyListener, FocusListener,
			MouseListener, ComponentListener {
		private boolean checkSpelling = Resources.getInstance()
				.getBoolProperty(FreeMindCommon.CHECK_SPELLING);

		public void focusGained(FocusEvent e) {
		} // focus gained

		public void focusLost(FocusEvent e) {
			// %%% open problems:
			// - adding of a child to the rightmost node
			// - scrolling while in editing mode (it can behave just like
			// other viewers)
			// - block selected events while in editing mode
			if (!textfield.isVisible() || mEventSource.getValue() == CANCEL) {
				if (checkSpelling) {
					mEventSource.setValue(EDIT); // allow focus lost again
				}
				return;
			}
			if (e == null) { // can be when called explicitly
				hideMe();
				getEditControl().ok(textfield.getText());
				mEventSource.setValue(CANCEL); // disallow real focus lost
			} else {
				// always confirm the text if not yet
				hideMe();
				getEditControl().ok(textfield.getText());
			}
		}

		public void keyPressed(KeyEvent e) {
			// add to check meta keydown by koh 2004.04.16
			// logger.info("Key " + e);
			if (e.isAltDown() || e.isControlDown() || e.isMetaDown()
					|| mEventSource.getValue() == CANCEL) {
				return;
			}

			boolean commit = true;

			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				commit = false;
			case KeyEvent.VK_ENTER:
				e.consume();

				mEventSource.setValue(CANCEL);
				hideMe();
				// do not process loose of focus
				if (commit) {
					getEditControl().ok(textfield.getText());
				} else {
					getEditControl().cancel();
				}
				e.consume();
				break;
			case KeyEvent.VK_SPACE:
				e.consume();
			}
		}

		public void keyTyped(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			conditionallyShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			conditionallyShowPopup(e);
		}

		private void conditionallyShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				JPopupMenu popupMenu = new EditPopupMenu(textfield);
				if (checkSpelling) {
					popupMenu.add(SpellChecker.createCheckerMenu());
					popupMenu.add(SpellChecker.createLanguagesMenu());
					mEventSource.setValue(CANCEL); // disallow real focus lost
				}
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
				e.consume();
			}
		}

		public void componentHidden(ComponentEvent e) {
			focusLost(null);
		}

		public void componentMoved(ComponentEvent e) {
			focusLost(null);
		}

		public void componentResized(ComponentEvent e) {
			focusLost(null);
		}

		public void componentShown(ComponentEvent e) {
			focusLost(null);
		}

	}

	protected void addTextfield() {
		mParent.add(textfield, 0);
	}

	protected void setTextfieldLoaction(Point mPoint) {
		textfield.setLocation(mPoint);
	}

	private void hideMe() {
		final JComponent parent = (JComponent) textfield.getParent();
		final Rectangle bounds = textfield.getBounds();
		textfield.removeFocusListener(textFieldListener);
		textfield.removeKeyListener((KeyListener) textFieldListener);
		textfield.removeMouseListener((MouseListener) textFieldListener);
		mFocusListener
				.removeComponentListener((ComponentListener) textFieldListener);
		// workaround for java bug
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7075600, see
		// FreeMindStarter
		parent.remove(textfield);
		parent.revalidate();
		parent.repaint(bounds);
		textFieldListener = null;
	}
}