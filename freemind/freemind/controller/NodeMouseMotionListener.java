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
/*$Id: NodeMouseMotionListener.java,v 1.9 2003-12-20 16:12:51 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.TimerTask;
import java.util.Timer;
import freemind.view.mindmapview.NodeView;
import freemind.main.Tools;

/**
 * The MouseMotionListener which belongs to every
 * NodeView
 */
public class NodeMouseMotionListener implements MouseMotionListener, MouseListener {

    private final Controller c;
    /** time in ms, overwritten by property time_for_delayed_selection*/
    private static Tools.IntHolder timeForDelayedSelection; 
    /** overwritten by property delayed_selection_enabled*/
    private static Tools.BooleanHolder delayedSelectionEnabled; 
    /** And a static method to reread this holder. This is used when the selection method is changed via the option menu. */
    static public void updateSelectionMethod(Controller c) {
        delayedSelectionEnabled = new Tools.BooleanHolder();
        delayedSelectionEnabled.setValue(c.getFrame().getProperty("selection_method").equals("selection_method_delayed")?true:false);
    }

    Timer timerForDelayedSelection;
    /** The mouse has to stay in this region to enable the selection after a given time.*/
    Rectangle controlRegionForDelayedSelection;

    private boolean ignoreNextDoubleClick = false; 
    private boolean ignoreNextPlainClick = false;
    /** fc, 30.11.2003: This method is currently not used.*/
    public void ignoreNextDoubleClick() {
      ignoreNextDoubleClick = true; }
    public void ignoreNextPlainClick() {
       ignoreNextPlainClick = true; }

    public NodeMouseMotionListener(Controller controller) {
       c = controller; 
       if(timeForDelayedSelection == null)
           {
               timeForDelayedSelection = new Tools.IntHolder();
               timeForDelayedSelection.setValue(Integer.parseInt(c.getFrame().getProperty("time_for_delayed_selection")));
           }     
       if(delayedSelectionEnabled == null)
           updateSelectionMethod(c);
    }

    public void mouseDragged(MouseEvent e) {}
   // Invoked when a mouse button is pressed on a component and then dragged. 

    public void mouseMoved(MouseEvent e) {
   //  Invoked when the mouse button has been moved on a component (with no buttons down). 
       ((NodeView)e.getComponent()).updateCursor(e.getX());
       // test if still in selection region:
       if(controlRegionForDelayedSelection != null && delayedSelectionEnabled.getValue()) {
           if(!controlRegionForDelayedSelection.contains(e.getPoint())) {
               // point is not in the region. start timer again and adjust region to the current point:
               createTimer(e);
           }
       }
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
        createTimer(e);
        //c.getMode().getModeController().select(e);
    }

    public void createTimer( MouseEvent e ) {
        // stop old timer if present.*/
        stopTimerForDelayedSelection();
        /* Region to check for in the sequel. */
        controlRegionForDelayedSelection = getControlRegion(e.getPoint());
        timerForDelayedSelection = new Timer();
        timerForDelayedSelection.schedule(new timeDelayedSelection(c, e), 
                                          /*if the new selection method is not enabled we put 0 to get direct selection.*/
                                          (delayedSelectionEnabled.getValue())?timeForDelayedSelection.getValue():0);
        //c.getMode().getModeController().select(e);
    }


    protected void stopTimerForDelayedSelection() {
        // stop timer.
        if(timerForDelayedSelection != null)
            timerForDelayedSelection.cancel();
        timerForDelayedSelection = null;
        controlRegionForDelayedSelection = null;
    }

    public void mouseExited( MouseEvent e ) {
        stopTimerForDelayedSelection();
    }

    public void mousePressed( MouseEvent e ) {
        // first stop the timer and select the node:
      stopTimerForDelayedSelection();
      c.getView().extendSelection((NodeView)e.getSource(), e);
      // Right mouse <i>press</i> is <i>not</i> a popup trigger for Windows.
      // Only Right mouse release is a popup trigger!
      // OK, but Right mouse <i>press</i> <i>is</i> a popup trigger on Linux.
      c.getMode().getModeController().showPopupMenu(e);
      if (!e.isConsumed()) {
          // unified selection (PN) %%% (unify with mouse enntered above!!!
          c.getView().extendSelection((NodeView)e.getSource(), e);
          e.consume();
      }
    }

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
             ignoreNextDoubleClick = false; 
          }
          if (e.getClickCount() % 2 == 0) {
             if (ignoreNextDoubleClick) {
                ignoreNextDoubleClick = false; 
             } else {
                c.getMode().getModeController().doubleClick(e); 
             }
          } else {
             if (ignoreNextPlainClick) {
                ignoreNextPlainClick = false; 
             }
             else {
                c.getMode().getModeController().plainClick(e); 
             }
          }
       } else {
          ignoreNextDoubleClick = false; 
          ignoreNextPlainClick = false; 
       }

       e.consume();
       stopTimerForDelayedSelection();
    }

    protected Rectangle getControlRegion(Point2D p) {
        // Create a small square around the given point.
        int side = 8;
        return new Rectangle((int)(p.getX() - side / 2), (int)(p.getY() - side / 2),
                             side, side);
    }

    protected class timeDelayedSelection extends TimerTask {
        private final Controller c;
        private final MouseEvent e;
        timeDelayedSelection(Controller c, MouseEvent e) {
            this.c = c;
            this.e = e;
        }
        /** TimerTask method to enable the selection after a given time.*/
        public void run() {
            c.getMode().getModeController().select(e);
            //c.getView().extendSelection((NodeView)e.getSource(), e);
        }
    }

}
