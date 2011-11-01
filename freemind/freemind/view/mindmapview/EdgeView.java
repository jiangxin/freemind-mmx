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
/*$Id: EdgeView.java,v 1.13.14.2.4.9 2008/06/09 21:01:15 dpolivaev Exp $*/

package freemind.view.mindmapview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMapEdge;

/**
 * This class represents a single Edge of a MindMap.
 */
public abstract class EdgeView {
	private NodeView target;
	protected NodeView source;
	protected Point start, end;
	private static int i;
	protected static final BasicStroke DEF_STROKE = new BasicStroke();

	static Stroke ECLIPSED_STROKE = null;

	/**
	 * This should be a task of MindMapLayout start,end must be initialized...
	 * 
	 * @param target
	 *            TODO
	 */
	public void paint(NodeView target, Graphics2D g) {
		this.source = target.getVisibleParentView();
		this.target = target;
		createEnd();
		createStart();
		paint(g);
		this.source = null;
		this.target = null;
	}

	protected void createEnd() {
		end = getTarget().getMainViewInPoint();
		Tools.convertPointToAncestor(this.target.getMainView(), end, source);
	}

	protected void createStart() {
		start = source.getMainViewOutPoint(getTarget(), end);
		Tools.convertPointToAncestor(source.getMainView(), start, source);
	}

	abstract protected void paint(Graphics2D g);

	protected void reset() {
		this.source = null;
		this.target = null;
	}

	public abstract Color getColor();

	public Stroke getStroke() {
		int width = getWidth();
		if (width == EdgeAdapter.WIDTH_THIN) {
			return DEF_STROKE;
		}
		return new BasicStroke(width * getMap().getZoom(),
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}

	public int getWidth() {
		return getModel().getWidth();
	}

	protected MindMapEdge getModel() {
		return getTarget().getModel().getEdge();
	}

	protected MapView getMap() {
		return getTarget().getMap();
	}

	protected static Stroke getEclipsedStroke() {
		if (ECLIPSED_STROKE == null) {
			float dash[] = { 3.0f, 9.0f };
			ECLIPSED_STROKE = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 12.0f, dash, 0.0f);
		}
		return ECLIPSED_STROKE;
	}

	protected boolean isTargetEclipsed() {
		return getTarget().isParentHidden();
	}

	/**
	 * @return Returns the source.
	 */
	protected NodeView getSource() {
		return source;
	}

	/**
	 * @return Returns the target.
	 */
	protected NodeView getTarget() {
		return target;
	}
}
