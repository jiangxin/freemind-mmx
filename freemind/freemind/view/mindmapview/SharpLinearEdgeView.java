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
/*$Id: SharpLinearEdgeView.java,v 1.3.34.3 2007/08/22 13:23:54 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * This class represents a sharp Edge of a MindMap.
 */
public class SharpLinearEdgeView extends EdgeView {

	public SharpLinearEdgeView() {
		super();
	}

	protected void paint(Graphics2D g) {
		g.setColor(getColor());
		g.setPaint(getColor());
		g.setStroke(DEF_STROKE);
		int w = getMap().getZoomed(getWidth() / 2 + 1);
		int xs[] = { start.x, end.x, start.x };
		int ys[] = { start.y + w, end.y, start.y - w };
		// g.drawPolygon(xs,ys,3);
		g.fillPolygon(xs, ys, 3);
	}

	public Color getColor() {
		return getModel().getColor();
	}
}
