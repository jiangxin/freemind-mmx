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
/*$Id: NodeMouseListener.java,v 1.6 2001-04-06 20:50:11 ponder Exp $*/

package freemind.controller;

import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.MapView;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

/**
 * The MouseListener which belongs to every
 * NodeView
 */
public class NodeMouseListener implements MouseListener {

    private final Controller c;
    Timer timer;

    public NodeMouseListener(Controller controller) {
	c = controller;
    }

    private boolean maybeShowPopup( MouseEvent e ) {
	if (!e.isPopupTrigger()) return false;
	boolean extend = e.isControlDown(); 
	boolean branch = e.isShiftDown(); 
	if (!branch) {
		if (c.isSelected( (NodeView)e.getSource() )) {
			// nothing to do. The node is already selected
			// if not extended, we keep the selection and apply to
			// if extended, we will not unselecte this node
		}
		else {
			c.select( (NodeView)e.getSource(), extend );
		}
	}
	else {
		c.selectBranch( (NodeView)e.getSource(), extend );
	}
	c.showPopupMenu(e.getComponent(),e.getX(),e.getY());
	return true;
    }

    //
    // Interface MouseListener
    //
    
    public void mouseClicked(MouseEvent e) {
	if (e.getClickCount() == 3) {
	    //what to do?
	    e.consume();
	} else if(e.getClickCount() == 2) {
	    c.getMode().getModeController().doubleClick();
	    //The timer is necessary if ClickCount == 1 must not be called
	    //before ClickCount == 2 is called

	    //	    timer = new Timer(300, new ActionListener() {
// 		    public void actionPerformed(ActionEvent evt) {
// 			edit();
// 		    }
// 		});
	    //	    timer.start();
	} else if (e.getClickCount() == 1) {
	    e.consume();
	}
    }

//     public void edit() {
// 	c.toggleFolded();
// 	timer.stop();
    //  }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
	if (maybeShowPopup(e)) {
		e.consume();
		return;
	}
	boolean extend = e.isControlDown(); 
	boolean branch = e.isShiftDown(); 
	if (extend) {
		// nothing here, will be done in button up
	}
	else if (c.isSelected( (NodeView)e.getSource() )) {
		// nothing here because peharps a popup on sel
	}
	else {
		if (!branch) 
			c.select( (NodeView)e.getSource(), extend );
		else
			c.selectBranch( (NodeView)e.getSource(), extend );
	}
	e.consume();
    }
    
    public void mouseReleased( MouseEvent e ) {
	if (maybeShowPopup(e)) return;
	boolean extend = e.isControlDown(); 
	boolean branch = e.isShiftDown(); 
	if (!branch)
		c.select( (NodeView)e.getSource(), extend );
	else
		c.selectBranch( (NodeView)e.getSource(), extend );
	}
}
    
