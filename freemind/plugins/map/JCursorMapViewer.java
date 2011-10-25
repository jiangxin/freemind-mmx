/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */

package plugins.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;

/**
 * @author foltin
 * @date 24.10.2011
 */
final class JCursorMapViewer extends JMapViewer {

	boolean mShowCursor;
	boolean mUseCursor;
	Coordinate mCursorPosition;
	Stroke mStroke = new BasicStroke(2);
	private FreeMindMapController mFreeMindMapController;

	/**
	 * 
	 */
	public JCursorMapViewer() {
        super(new FileTileCache(), 4);
        mFreeMindMapController = new FreeMindMapController(this);
		Action updateCursorAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				mShowCursor = !mShowCursor;
				repaint();
			}
		};
		new Timer(1000, updateCursorAction).start();

	}

	public boolean isUseCursor() {
		return mUseCursor;
	}

	public void setUseCursor(boolean pUseCursor) {
		mUseCursor = pUseCursor;
		repaint();

	}

	public Coordinate getCursorPosition() {
		return mCursorPosition;
	}

	public void setCursorPosition(Coordinate pCursorPosition) {
		mCursorPosition = pCursorPosition;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openstreetmap.gui.jmapviewer.JMapViewer#paintComponent(java.awt.Graphics
	 * )
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// do cursor
		if(mUseCursor && mShowCursor) {
	        Point position = getMapPosition(mCursorPosition.getLat(), mCursorPosition.getLon());
	        if (position != null) {
	            int size_h = 15;
	            if (g instanceof Graphics2D) {
					Graphics2D g2d = (Graphics2D) g;
					Stroke oldStroke = g2d.getStroke();
					Color oldColor = g2d.getColor();
					g2d.setStroke(mStroke);
					g2d.setColor(Color.RED);
					g2d.drawLine(position.x - size_h, position.y, position.x + size_h, position.y);
					g2d.drawLine(position.x, position.y - size_h, position.x, position.y + size_h);
					g2d.setColor(oldColor);
					g2d.setStroke(oldStroke);
				}
	        }

		}
	}

}