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

    private void maybeShowPopup( MouseEvent e ) {
	if (e.isPopupTrigger()) {
	    c.showPopupMenu(e.getComponent(),e.getX(),e.getY());
	}
    }

    //
    // Interface MouseListener
    //
    
    public void mouseClicked(MouseEvent e) {
	if (e.getClickCount() == 3) {
	    //what to do?
	    e.consume();
	} else if(e.getClickCount() == 2) {
	    c.toggleFolded();
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
	c.select( (NodeView)e.getSource() );
	maybeShowPopup(e);
	e.consume();
    }
    
    public void mouseReleased( MouseEvent e ) {
	c.select( (NodeView)e.getSource() );
	maybeShowPopup(e);
    }

}
    
