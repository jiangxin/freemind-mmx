/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 10.11.2004
 */
/*$Id: ExportVectorGraphic.java,v 1.1.4.1 2004-11-16 16:42:38 christianfoltin Exp $*/
package plugins.svg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import freemind.extensions.ExportHook;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 *
 */
public class ExportVectorGraphic extends ExportHook{

	/**
	 * @param view
	 * @return
	 */
	protected SVGGraphics2D fillSVGGraphics2D(MapView view) {
		NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		Rectangle innerBounds = view.getInnerBounds(root.getViewer());
		DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
		String namespaceURI = SVGConstants.SVG_NAMESPACE_URI;
		Document domFactory = impl.createDocument(namespaceURI, "svg", null);
		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
		GraphicContextDefaults defaults 
		    = new GraphicContextDefaults();
		defaults.setFont(new Font("Arial", Font.PLAIN, 12));
		ctx.setGraphicContextDefaults(defaults);
		ctx.setPrecision(12);
	
		SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2d.setSVGCanvasSize(new Dimension(innerBounds.width, innerBounds.height));
		g2d.translate(-innerBounds.x, -innerBounds.y);
		//
		// Generate SVG content
		//
		view.print(g2d);
		return g2d;
	}

}
