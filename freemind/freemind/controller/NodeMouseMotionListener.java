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
/*$Id: NodeMouseMotionListener.java,v 1.6 2003-11-09 22:09:25 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import freemind.view.mindmapview.NodeView;

/**
 * The MouseMotionListener which belongs to every
 * NodeView
 */
public class NodeMouseMotionListener implements MouseMotionListener, MouseListener {

    private final Controller c;
   //    Timer timer;

    private boolean ignoreNextDoubleClick = false; 
    private boolean ignoreNextPlainClick = false;
    public void ignoreNextDoubleClick() {
      ignoreNextDoubleClick = true; }
    public void ignoreNextPlainClick() {
       ignoreNextPlainClick = true; }

    public NodeMouseMotionListener(Controller controller) {
       c = controller; }

    public void mouseDragged(MouseEvent e) {}
   // Invoked when a mouse button is pressed on a component and then dragged. 

    public void mouseMoved(MouseEvent e) {
   //  Invoked when the mouse button has been moved on a component (with no buttons down). 
       ((NodeView)e.getComponent()).updateCursor(e.getX());
    }


    //
    // Interface MouseListener
    //

    public void mouseClicked(MouseEvent e) {
      if (!e.isAltDown() 
          && !e.isControlDown() 
          && !e.isShiftDown() 
          && !e.isPopupTrigger()
          && e.getButton() == MouseEvent.BUTTON1
          && ((NodeView)(e.getComponent())).getModel().getLink() == null) {
        c.getMode().getModeController().edit(null, false, false);
      }
    }

    public void mouseEntered( MouseEvent e ) {
       c.getMode().getModeController().select(e);
    }

    public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
      // Right mouse <i>press</i> is <i>not</i> a popup trigger for Windows.
      // Only Right mouse release is a popup trigger!
      // OK, but Right mouse <i>press</i> <i>is</i> a popup trigger on Linux.
      c.getMode().getModeController().showPopupMenu(e);
      if (!e.isConsumed()) {        // unified selection (PN) %%% (unify with mose enntered above!!!
        c.getView().extendSelection((NodeView)e.getSource(), e);
        e.consume();
      }    }

    public void mouseReleased( MouseEvent e ) {
       // handling click in mouseReleased rather than in mouseClicked
       // provides better interaction. If mouse was slightly moved
       // between pressed and released events, the event clicked
       // is not triggered.
       // The behavior is not tested on Linux.

       c.getMode().getModeController().showPopupMenu(e);
       if (e.isConsumed()) {
         return;
       }
       
       if (e.getModifiers() == MouseEvent.BUTTON1_MASK ) {
          if (e.getClickCount() == 1) {
             ignoreNextDoubleClick = false; }
          if (e.getClickCount() % 2 == 0) {
             if (ignoreNextDoubleClick) {
                ignoreNextDoubleClick = false; }
             else {
                c.getMode().getModeController().doubleClick(e); }}
          else {
             if (ignoreNextPlainClick) {
                ignoreNextPlainClick = false; }
             else {
                c.getMode().getModeController().plainClick(e); }}}
       else {
          ignoreNextDoubleClick = false; 
          ignoreNextPlainClick = false; }

       e.consume();
    }
}
