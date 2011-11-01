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
/*$Id: NodeMotionListener.java,v 1.1.4.3.2.1 2006/04/05 21:26:24 dpolivaev Exp $*/

package freemind.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The MouseMotionListener which belongs to every NodeView
 */
public class NodeMotionListener extends MouseAdapter implements
		MouseMotionListener, MouseListener {

	public static abstract class NodeMotionAdapter extends MouseAdapter
			implements MouseMotionListener, MouseListener {

	}

	private final Controller c;
	private NodeMotionAdapter mListener;

	public NodeMotionListener(Controller controller) {
		c = controller;
	}

	public void register(NodeMotionAdapter listener) {
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

}
