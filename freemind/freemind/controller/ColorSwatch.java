/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
package freemind.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

class ColorSwatch implements Icon {
	Color color = Color.white;

	public ColorSwatch() {
	}

	public ColorSwatch(Color color) {
		this.color = color;
	}

	public int getIconWidth() {
		return 11;
	}

	public int getIconHeight() {
		return 11;
	}

	Color getColor() {
		return color;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(Color.black);
		g.fillRect(x, y, getIconWidth(), getIconHeight());
		g.setColor(getColor());
		g.fillRect(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
	}
}
