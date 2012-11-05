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
/*$Id: ArrowLinkAdapter.java,v 1.4.18.5.12.1 2007/05/06 21:12:19 christianfoltin Exp $*/

package freemind.modes;

import java.awt.Point;

import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLElement;

public abstract class ArrowLinkAdapter extends LinkAdapter implements
		MindMapArrowLink {

	/** the zero is the start point of the line; */
	protected Point startInclination;
	/** the zero is the end point of the line; */
	protected Point endInclination;
	protected String startArrow;
	protected String endArrow;
	protected boolean showControlPointsFlag;

	public ArrowLinkAdapter(MindMapNode source, MindMapNode target,
			FreeMindMain frame) {
		super(source, target, frame);
		startArrow = "None";
		endArrow = "Default";
	}

	public Point getStartInclination() {
		if (startInclination == null)
			return null;
		return new Point(startInclination);
	}

	public Point getEndInclination() {
		if (endInclination == null)
			return null;
		return new Point(endInclination);
	}

	public String getStartArrow() {
		return startArrow;
	}

	public String getEndArrow() {
		return endArrow;
	}

	public void setStartInclination(Point startInclination) {
		this.startInclination = startInclination;
	}

	public void setEndInclination(Point endInclination) {
		this.endInclination = endInclination;
	}

	public void setStartArrow(String startArrow) {
		if (startArrow == null || startArrow.toUpperCase().equals("NONE")) {
			this.startArrow = "None";
			return;
		} else if (startArrow.toUpperCase().equals("DEFAULT")) {
			this.startArrow = "Default";
			return;
		}
		// dont change:
		System.err.println("Cannot set the start arrow type to " + startArrow);
	}

	public void setEndArrow(String endArrow) {
		if (endArrow == null || endArrow.toUpperCase().equals("NONE")) {
			this.endArrow = "None";
			return;
		} else if (endArrow.toUpperCase().equals("DEFAULT")) {
			this.endArrow = "Default";
			return;
		}
		// dont change:
		System.err.println("Cannot set the end arrow type to " + endArrow);
	}

	public Object clone() {
		ArrowLinkAdapter arrowLink = (ArrowLinkAdapter) super.clone();
		// now replace the points:
		arrowLink.startInclination = (startInclination == null) ? null
				: new Point(startInclination.x, startInclination.y);
		arrowLink.endInclination = (endInclination == null) ? null : new Point(
				endInclination.x, endInclination.y);
		arrowLink.startArrow = (startArrow == null) ? null : new String(
				startArrow);
		arrowLink.endArrow = (endArrow == null) ? null : new String(endArrow);
		return arrowLink;
	}

	public void showControlPoints(boolean bShowControlPointsFlag) {
		showControlPointsFlag = bShowControlPointsFlag;
	}

	public boolean getShowControlPointsFlag() {
		return showControlPointsFlag;
	}

	public XMLElement save() {
		XMLElement arrowLink = new XMLElement();
		arrowLink.setName("arrowlink");

		if (style != null) {
			arrowLink.setAttribute("STYLE", style);
		}
		if (getUniqueId() != null) {
			arrowLink.setAttribute("ID", getUniqueId());
		}
		if (color != null) {
			arrowLink.setAttribute("COLOR", Tools.colorToXml(color));
		}
		if (getDestinationLabel() != null) {
			arrowLink.setAttribute("DESTINATION", getDestinationLabel());
		}
		if (getReferenceText() != null) {
			arrowLink.setAttribute("REFERENCETEXT", getReferenceText());
		}
		if (getStartInclination() != null) {
			arrowLink.setAttribute("STARTINCLINATION",
					Tools.PointToXml(getStartInclination()));
		}
		if (getEndInclination() != null) {
			arrowLink.setAttribute("ENDINCLINATION",
					Tools.PointToXml(getEndInclination()));
		}
		if (getStartArrow() != null)
			arrowLink.setAttribute("STARTARROW", (getStartArrow()));
		if (getEndArrow() != null)
			arrowLink.setAttribute("ENDARROW", (getEndArrow()));
		return arrowLink;
	}
	
	public ArrowLinkTarget createArrowLinkTarget(MindMapLinkRegistry pRegistry) {
		ArrowLinkTarget linkTarget = new ArrowLinkTarget(source, target, frame);
		linkTarget.setSourceLabel(pRegistry.getLabel(source));
		copy(linkTarget);
		return linkTarget;
	}

	protected void copy(ArrowLinkAdapter linkTarget) {
		linkTarget.setUniqueId(getUniqueId());
		linkTarget.setColor(getColor());
		linkTarget.setDestinationLabel(getDestinationLabel());
		linkTarget.setEndArrow(getEndArrow());
		linkTarget.setEndInclination(getEndInclination());
		linkTarget.setReferenceText(getReferenceText());
		linkTarget.setStartArrow(getStartArrow());
		linkTarget.setStartInclination(getStartInclination());
		linkTarget.setStyle(getStyle());
		linkTarget.setTarget(getTarget());
		linkTarget.setWidth(getWidth());
		linkTarget.setSource(getSource());
	}

}
