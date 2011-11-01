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
/*$Id: MindMapLine.java,v 1.2.34.2 2007/08/21 19:54:08 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Color;

public interface MindMapLine extends Cloneable {

	public Color getColor();

	public String getStyle();

	public int getWidth();

	public int getRealWidth();

	public String toString();

	/** The node to which this line is associated. */
	public void setTarget(MindMapNode node);

	public Object clone();
}
