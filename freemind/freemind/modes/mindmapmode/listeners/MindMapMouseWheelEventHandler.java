/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 *
 * Created on 09.11.2005
 */
/* $Id: MindMapMouseWheelEventHandler.java,v 1.1.2.1.2.1 2006-04-05 21:26:31 dpolivaev Exp $ */
package freemind.modes.mindmapmode.listeners;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.Set;

import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.MindMapActions.MouseWheelEventHandler;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 *
 */
public class MindMapMouseWheelEventHandler implements MouseWheelListener {

    private static final int SCROLL_SKIPS = 8;
    private static final int SCROLL_SKIP = 10;
    private static final int HORIZONTAL_SCROLL_MASK
       = InputEvent.SHIFT_MASK | InputEvent.BUTTON1_MASK
         | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;
    private static final int ZOOM_MASK
       = InputEvent.CTRL_MASK;
      // |=   oldX >=0 iff we are in the drag

	private final MindMapController mController;

	/**
	 * @param controller
	 *
	 */
	public MindMapMouseWheelEventHandler(MindMapController controller) {
		super();
		this.mController = controller;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see freemind.modes.ModeController.MouseWheelEventHandler#handleMouseWheelEvent(java.awt.event.MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {

		if (mController.isBlocked()) {
			return; // block the scroll during edit (PN)
		}
		Set registeredMouseWheelEventHandler = mController.getRegisteredMouseWheelEventHandler();
		for (Iterator i = registeredMouseWheelEventHandler.iterator(); i
				.hasNext();) {
			MouseWheelEventHandler handler = (MouseWheelEventHandler) i.next();
			boolean result = handler.handleMouseWheelEvent(e);
			if (result) {
				// event was consumed:
				return;
			}
		}

		if ((e.getModifiers() & ZOOM_MASK) != 0) {
			// fc, 18.11.2003: when control pressed, then the zoom is changed.
			float newZoomFactor = 1f + Math.abs((float) e.getWheelRotation()) / 10f;
			if (e.getWheelRotation() < 0)
				newZoomFactor = 1 / newZoomFactor;
			float newZoom = ((MapView) e.getComponent()).getZoom()
					* newZoomFactor;
			// round the value due to possible rounding problems.
			newZoom = (float) Math.rint(newZoom * 1000f) / 1000f;
			mController.getController().setZoom(newZoom);
			// end zoomchange
		} else if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
			for (int i = 0; i < SCROLL_SKIPS; i++) {
				((MapView) e.getComponent()).scrollBy(SCROLL_SKIP
						* e.getWheelRotation(), 0, false);
			}
		} else {
			for (int i = 0; i < SCROLL_SKIPS; i++) {
				((MapView) e.getComponent()).scrollBy(0, SCROLL_SKIP
						* e.getWheelRotation(), false);
			}
		}
	}


}
