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
/*$Id: MapMouseWheelListener.java,v 1.7 2003-11-18 23:19:45 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * The MouseListener which belongs to MapView
 */
public class MapMouseWheelListener implements MouseWheelListener {

    private final Controller c;

    public MapMouseWheelListener(Controller controller) {
       c = controller; }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.isShiftDown()) {
            // fc, 18.11.2003: when shift pressed, then the zoom is changed.
            c.setZoom(c.getView().getZoom() * ( 10f + (float) e.getWheelRotation() ) / 10f);
            // end zoomchange
        } else {
            c.getMode().getModeController().mouseWheelMoved(e);
        }
    }
}
