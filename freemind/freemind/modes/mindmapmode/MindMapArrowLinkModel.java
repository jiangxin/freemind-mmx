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
/*$Id: MindMapArrowLinkModel.java,v 1.5.18.4.12.1 2007/04/21 15:11:21 dpolivaev Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Point;

import freemind.main.FreeMindMain;
import freemind.modes.ArrowLinkAdapter;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

public class MindMapArrowLinkModel extends ArrowLinkAdapter {

	public MindMapArrowLinkModel(MindMapNode source, MindMapNode target,
			FreeMindMain frame) {
		super(source, target, frame);
	}

	/* maybe this method is wrong here, but ... */
	public Object clone() {
		return super.clone();
	}

	public String toString() {
		return "Source=" + getSource() + ", target=" + getTarget() + ", "
				+ save().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MindMapArrowLink#changeInclination(int, int, int,
	 * int)
	 */
	public void changeInclination(MapView map, int originX, int originY,
			int deltaX, int deltaY) {
		double distSqToTarget = 0;
		double distSqToSource = 0;
		NodeView targetView = map.getNodeView(getTarget());
		NodeView sourceView = map.getNodeView(getSource());
		if (targetView != null && sourceView != null) {
			Point targetLinkPoint = targetView
					.getLinkPoint(getEndInclination());
			Point sourceLinkPoint = sourceView
					.getLinkPoint(getStartInclination());
			distSqToTarget = targetLinkPoint.distanceSq(originX, originY);
			distSqToSource = sourceLinkPoint.distanceSq(originX, originY);
		}
		if ((targetView == null || sourceView != null)
				&& distSqToSource < distSqToTarget * 2.25) {
			Point changedInclination = getStartInclination();
			changeInclination(deltaX, deltaY, sourceView, changedInclination);
			setStartInclination(changedInclination);
		}

		if ((sourceView == null || targetView != null)
				&& distSqToTarget < distSqToSource * 2.25) {
			Point changedInclination = getEndInclination();
			changeInclination(deltaX, deltaY, targetView, changedInclination);
			setEndInclination(changedInclination);
		}

	}

	private void changeInclination(int deltaX, int deltaY,
			NodeView linkedNodeView, Point changedInclination) {
		if (linkedNodeView.isLeft()) {
			deltaX = -deltaX;
		}
		changedInclination.translate(deltaX, deltaY);
		if (changedInclination.x != 0
				&& Math.abs((double) changedInclination.y
						/ changedInclination.x) < 0.015) {
			changedInclination.y = 0;
		}
		double k = changedInclination.distance(0, 0);
		if (k < 10) {
			if (k > 0) {
				changedInclination.x = (int) (changedInclination.x * 10 / k);
				changedInclination.y = (int) (changedInclination.y * 10 / k);
			} else {
				changedInclination.x = 10;
			}
		}
	}

}
