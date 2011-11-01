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
/*$Id: NodeMouseMotionListener.java,v 1.15.14.3 2006/01/12 23:10:12 christianfoltin Exp $*/

package freemind.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMouseMotionListener implements MouseMotionListener,
		MouseListener {

	public static interface NodeMouseMotionObserver extends
			MouseMotionListener, MouseListener {

		void updateSelectionMethod();

	}

	private final Controller c;

	private NodeMouseMotionObserver mListener;

	public NodeMouseMotionListener(Controller controller) {
		c = controller;
	}

	public void register(NodeMouseMotionObserver listener) {
		this.mListener = listener;

	}

	public void deregister() {
		mListener = null;
	}

	public void mouseClicked(MouseEvent e) {
		if (mListener != null)
			mListener.mouseClicked(e);
	}

	public void mouseDragged(MouseEvent e) {
		if (mListener != null)
			mListener.mouseDragged(e);
	}

	public void mouseEntered(MouseEvent e) {
		if (mListener != null)
			mListener.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		if (mListener != null)
			mListener.mouseExited(e);
	}

	public void mouseMoved(MouseEvent e) {
		if (mListener != null)
			mListener.mouseMoved(e);
	}

	public void mousePressed(MouseEvent e) {
		if (mListener != null)
			mListener.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if (mListener != null)
			mListener.mouseReleased(e);
	}

	public void updateSelectionMethod() {
		if (mListener != null)
			mListener.updateSelectionMethod();
	}

}
