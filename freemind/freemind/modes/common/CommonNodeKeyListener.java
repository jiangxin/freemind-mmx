/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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


package freemind.modes.common;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import freemind.main.Resources;
import freemind.modes.ModeController;

/**
 * The KeyListener which belongs to the node and cares for Events like C-D
 * (Delete Node). It forwards the requests to NodeController.
 */
public class CommonNodeKeyListener implements KeyListener {

	public interface EditHandler {
		void edit(KeyEvent e, boolean addNew, boolean editLong);
	}

	private ModeController c;

	private String up, down, left, right;

	private boolean disabledKeyType = true;

	private boolean keyTypeAddsNew = false;

	private KeyStroke keyStrokeUp;

	private KeyStroke keyStrokeDown;

	private KeyStroke keyStrokeLeft;

	private KeyStroke keyStrokeRight;

	private final EditHandler editHandler;

	private static Logger logger;

	public CommonNodeKeyListener(ModeController controller,
			EditHandler editHandler) {
		c = controller;
		this.editHandler = editHandler;
		if (logger == null) {
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}
		up = c.getFrame().getAdjustableProperty("keystroke_move_up");
		down = c.getFrame().getAdjustableProperty("keystroke_move_down");
		left = c.getFrame().getAdjustableProperty("keystroke_move_left");
		right = c.getFrame().getAdjustableProperty("keystroke_move_right");

		// like in excel - write a letter means edit (PN)
		// on the other hand it doesn't allow key navigation (sdfe)
		disabledKeyType = Resources.getInstance().getBoolProperty(
				"disable_key_type");
		keyTypeAddsNew = Resources.getInstance().getBoolProperty(
				"key_type_adds_new");
		keyStrokeUp = KeyStroke.getKeyStroke(up);
		keyStrokeDown = KeyStroke.getKeyStroke(down);
		keyStrokeLeft = KeyStroke.getKeyStroke(left);
		keyStrokeRight = KeyStroke.getKeyStroke(right);
	}

	//
	// Interface KeyListener
	//

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		logger.finest("Key pressend " + e.getKeyChar() + " alias " + e.getKeyCode());
		// add to check meta keydown by koh 2004.04.16
		if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
			return;
		}

		switch (e.getKeyCode()) {

		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_SHIFT:
		case KeyEvent.VK_DELETE:
		case KeyEvent.VK_SPACE:
		case KeyEvent.VK_INSERT:
			// fc, 20.6.2004: to enable tab for insert.
		case KeyEvent.VK_TAB:
			// end change.
			return; // processed by Adapters ActionListener
			// explicitly what is not caught in e.isActionKey()

		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_PAGE_DOWN:
			c.getView().move(e);
			return;

		case KeyEvent.VK_HOME:
		case KeyEvent.VK_END:
		case KeyEvent.VK_BACK_SPACE:
			editHandler.edit(e, false, false);
			return;

			// case KeyEvent.VK_SPACE:
			// c.getMode().getModeController().toggleFolded();
			// e.consume();
			// return;
		}

		// printable key creates new node in edit mode (PN)
		if (!disabledKeyType) {
			if (!e.isActionKey() && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
				logger.finest("Starting edit mode with: " + e.getKeyChar());
				editHandler.edit(e, keyTypeAddsNew, false);
				return; // do not process the (sdfe) navigation
			}
		}

		// printable key used for navigation
		boolean doMove = false; // unified call of the move method (PN)
		if (keyStrokeUp != null && e.getKeyCode() == keyStrokeUp.getKeyCode()) {
			e.setKeyCode(KeyEvent.VK_UP);
			doMove = true;
		} else if (keyStrokeDown != null
				&& e.getKeyCode() == keyStrokeDown.getKeyCode()) {
			e.setKeyCode(KeyEvent.VK_DOWN);
			doMove = true;
		} else if (keyStrokeLeft != null
				&& e.getKeyCode() == keyStrokeLeft.getKeyCode()) {
			e.setKeyCode(KeyEvent.VK_LEFT);
			doMove = true;
		} else if (keyStrokeRight != null
				&& e.getKeyCode() == keyStrokeRight.getKeyCode()) {
			e.setKeyCode(KeyEvent.VK_RIGHT);
			doMove = true;
		}
		if (doMove) {
			c.getView().move(e);
			e.consume();
			return;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			c.getView().resetShiftSelectionOrigin();
		}
	}
}
