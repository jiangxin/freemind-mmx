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
/*$Id: NodeKeyListener.java,v 1.16.12.1 2004-04-24 18:44:22 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Externalizable;

import javax.swing.KeyStroke;

import freemind.main.Tools;

/**
 * The KeyListener which belongs to the node and cares for
 * Events like C-D (Delete Node). It forwards the requests to
 * NodeController.
 */
public class NodeKeyListener implements KeyListener {

    private Controller c;
    private String up, down, left, right;
    private boolean disabledKeyType = true;
    private boolean keyTypeAddsNew  = false;

    public NodeKeyListener(Controller controller ) {
	c = controller;
	up = c.getFrame().getProperty("keystroke_move_up");
	down = c.getFrame().getProperty("keystroke_move_down");
	left = c.getFrame().getProperty("keystroke_move_left");
	right = c.getFrame().getProperty("keystroke_move_right");

      // like in excel - write a letter means edit (PN)
      // on the other hand it doesn't allow key navigation (sdfe)
      disabledKeyType = Tools.safeEquals(
          c.getFrame().getProperty("disable_key_type"),"true");
      keyTypeAddsNew = Tools.safeEquals(
          c.getFrame().getProperty("key_type_adds_new"),"true");
    }

    //
    // Interface KeyListener
    //
    
    public void keyTyped( KeyEvent e ) {
    }

	public void keyPressed(KeyEvent e) {

	//		add to check meta keydown by koh 2004.04.16
	if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
		return;
	}

	switch ( e.getKeyCode() ) {
    
        case KeyEvent.VK_ENTER:
        case KeyEvent.VK_ESCAPE:
        case KeyEvent.VK_SHIFT:
        case KeyEvent.VK_DELETE:
        case KeyEvent.VK_SPACE:
        case KeyEvent.VK_INSERT:
            return; // processed by Adapters ActionListener
                     // explicitly what is not catched in e.isActionKey()
                     
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
            c.getMode().getModeController().edit(e, false, false);
            return;

//        case KeyEvent.VK_SPACE:
//	    c.getMode().getModeController().toggleFolded();
//            e.consume();
//	    return;
	}
  
        // printable key creates new node in edit mode (PN)
        if (!disabledKeyType) {
          if (!e.isActionKey() 
               && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            c.getMode().getModeController().edit(e, keyTypeAddsNew, false);
            return; // do not process the (sdfe) navigation
          }
        }
        
        // printable key used for navigation
        boolean doMove = false; // unified call of the move method (PN)
	if ( KeyStroke.getKeyStroke(up) != null &&
             e.getKeyCode() == KeyStroke.getKeyStroke(up).getKeyCode()) {
            e.setKeyCode(KeyEvent.VK_UP);
            doMove = true;
	} else if ( KeyStroke.getKeyStroke(down) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(down).getKeyCode()) {
            e.setKeyCode(KeyEvent.VK_DOWN);
            doMove = true;
	} else if ( KeyStroke.getKeyStroke(left) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(left).getKeyCode()) {
            e.setKeyCode(KeyEvent.VK_LEFT);
            doMove = true;
	} else if ( KeyStroke.getKeyStroke(right) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(right).getKeyCode()) {
            e.setKeyCode(KeyEvent.VK_RIGHT);
            doMove = true;
	}
        if (doMove) {
          c.getView().move(e);
          e.consume();
          return;
        }
    }

    public void keyReleased( KeyEvent e ) {
    }
}
    
