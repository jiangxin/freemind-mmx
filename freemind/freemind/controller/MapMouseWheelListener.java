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
/*$Id: MapMouseWheelListener.java,v 1.3 2003-11-03 10:39:51 sviles Exp $*/

package freemind.controller;

import freemind.view.mindmapview.MapView;
import java.awt.event.*;

/**
 * The MouseListener which belongs to MapView
 */
public class MapMouseWheelListener implements MouseWheelListener {

    private final Controller c;
    private final int SCROLL_SKIPS = 8;
    private final int SCROLL_SKIP = 10;
    private final int HORIZONTAL_SCROLL_MASK = InputEvent.SHIFT_MASK | InputEvent.BUTTON1_MASK |
       InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;

    // |=   oldX >=0 iff we are in the drag

    public MapMouseWheelListener(Controller controller) {
       c = controller; }

    public void mouseWheelMoved(MouseWheelEvent e) {
       if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(SCROLL_SKIP * e.getWheelRotation(), 0); }}
       else {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(0, SCROLL_SKIP * e.getWheelRotation()); }}
    }
}
