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
/*$Id: NodeKeyListener.java,v 1.13 2003-11-03 10:39:51 sviles Exp $*/

package freemind.controller;

import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.*;

/**
 * The KeyListener which belongs to the node and cares for
 * Events like C-D (Delete Node). It forwards the requests to
 * NodeController.
 */
public class NodeKeyListener implements KeyListener {

    private Controller c;
    private String up, down, left, right;

    public NodeKeyListener(Controller controller ) {
	c = controller;
	up = c.getFrame().getProperty("keystroke_move_up");
	down = c.getFrame().getProperty("keystroke_move_down");
	left = c.getFrame().getProperty("keystroke_move_left");
	right = c.getFrame().getProperty("keystroke_move_right");
    }

    //
    // Interface KeyListener
    //
    
    public void keyTyped( KeyEvent e ) {
    }

    public void keyPressed( KeyEvent e ) {

	if (e.isAltDown() || e.isControlDown()) {
	    return;
	}

	switch ( e.getKeyCode() ) {
	case KeyEvent.VK_UP:
	    c.moveUp();
	    e.consume();
	    return;

	case KeyEvent.VK_DOWN:
	    c.moveDown();
	    e.consume();
	    return;

	case KeyEvent.VK_LEFT:
	    c.moveLeft();
	    e.consume();
	    return;

	case KeyEvent.VK_RIGHT:
	    c.moveRight();
	    e.consume();
	    return;

	    //Easy access keybindings

	    //I tried a simple "n" to add a new node, but the keyEvent was fired
	    //twice and the n was posted to the editor as the first char.
	    //That was to annoying

	}

	if ( KeyStroke.getKeyStroke(up) != null &&
             e.getKeyCode() == KeyStroke.getKeyStroke(up).getKeyCode()) {
	    c.moveUp();
	    e.consume();
	    return;
	} else if ( KeyStroke.getKeyStroke(down) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(down).getKeyCode()) {
	    c.moveDown();
	    e.consume();
	    return;
	} else if ( KeyStroke.getKeyStroke(left) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(left).getKeyCode()) {
	    c.moveLeft();
	    e.consume();
	    return;
	} else if ( KeyStroke.getKeyStroke(right) != null &&
                    e.getKeyCode() == KeyStroke.getKeyStroke(right).getKeyCode()) {
	    c.moveRight();
	    e.consume();
	    return;
	}
    }

    public void keyReleased( KeyEvent e ) {
    }
}
    
