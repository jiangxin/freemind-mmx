/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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

package freemind.controller;

import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * The KeyListener which belongs to the node and cares for
 * Events like C-D (Delete Node). It forwards the requests to
 * NodeController.
 */
public class NodeKeyListener implements KeyListener {

    private Controller c;

    public NodeKeyListener(Controller controller ) {
	c = controller;
    }

    //
    // Interface KeyListener
    //
    
    public void keyTyped( KeyEvent e ) {
    }

    public void keyPressed( KeyEvent e ) {

	//Control is for navigation (emacs-style)
	if ( e.isControlDown() ) {
	    switch ( e.getKeyCode() ) {
	    case KeyEvent.VK_P:
		c.moveUp();
		e.consume();
		return;
	    case KeyEvent.VK_N:
		c.moveDown();
		e.consume();
		return;
	    case KeyEvent.VK_F:
		c.moveRight();
		e.consume();
		return;
	    case KeyEvent.VK_B:
		c.moveLeft();
		e.consume();
		return;
	    }
	}

	//Alt is for editing
	if ( e.isAltDown() ) {
	    switch ( e.getKeyCode() ) {
	    case KeyEvent.VK_D:
		c.delete( (NodeView)e.getSource() );
		e.consume();
		return;
	    case KeyEvent.VK_N:
		c.addNew( (NodeView)e.getSource() );
		e.consume();
		return;
	    case KeyEvent.VK_C:
		c.centerNode();
		e.consume();
		return;
	    }
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

	case KeyEvent.VK_ESCAPE:
	    c.moveToRoot();
	    e.consume();
	    return;

	case KeyEvent.VK_DELETE:
	    c.delete( (NodeView)e.getSource() );
	    e.consume();
	    return;

	case KeyEvent.VK_INSERT:
	    c.addNew( (NodeView)e.getSource() );
	    e.consume();
	    return;
	
	case KeyEvent.VK_ENTER:
	    c.edit( (NodeView)e.getSource() );
	    e.consume();
	    return;
	}
	//Workaround because KeyCodes for these characters are 0
	if (e.getKeyChar() == '>') {
	    c.nextMap();
	    e.consume();
	    return;
	} else if (e.getKeyChar() == '<') {
	    c.previousMap();
	    e.consume();
	    return;
	}
    }

    public void keyReleased( KeyEvent e ) {
    }
}
    
