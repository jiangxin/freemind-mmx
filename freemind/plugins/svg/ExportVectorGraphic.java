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

package plugins.svg;

//import java.awt.BasicStroke;
//import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class ExportVectorGraphic extends ExportHook {

	/**
	 */
	protected SVGGraphics2D fillSVGGraphics2D(MapView view) {
		// NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
		SVGGraphics2D g2d = createSvgGraphics2D();
		try {
			view.preparePrinting();
			Rectangle innerBounds = view.getInnerBounds();
			g2d.setSVGCanvasSize(new Dimension(innerBounds.width,
					innerBounds.height));
			g2d.translate(-innerBounds.x, -innerBounds.y);
			//
			// Generate SVG content
			//
			view.print(g2d);
		} finally {
			view.endPrinting();
		}
		// g2d.setColor(Color.BLACK);
		// g2d.setStroke(new BasicStroke(3));
		// g2d.drawRect(innerBounds.x, innerBounds.y, innerBounds.width - 2,
		// innerBounds.height - 2);
		return g2d;
	}

	protected SVGGraphics2D fillSVGGraphics2D(MapView view, MindMapNode pNode) {
		SVGGraphics2D g2d = createSvgGraphics2D();
		try {
			view.preparePrinting();
			Rectangle innerBounds = null;
			;
			for (Iterator it = pNode.getViewers().iterator(); it.hasNext();) {
				NodeView nodeView = (NodeView) it.next();
				if (innerBounds == null) {
					innerBounds = nodeView.getInnerBounds();
				} else {
					innerBounds.add(nodeView.getInnerBounds());
				}
			}
			g2d.setSVGCanvasSize(new Dimension(innerBounds.width,
					innerBounds.height));
			g2d.translate(-innerBounds.x, -innerBounds.y);
			//
			// Generate SVG content
			//
			for (Iterator it = pNode.getViewers().iterator(); it.hasNext();) {
				NodeView nodeView = (NodeView) it.next();
				nodeView.print(g2d);
			}
		} finally {
			view.endPrinting();
		}
		// g2d.setColor(Color.BLACK);
		// g2d.setStroke(new BasicStroke(3));
		// g2d.drawRect(innerBounds.x, innerBounds.y, innerBounds.width - 2,
		// innerBounds.height - 2);
		return g2d;
	}

	public SVGGraphics2D createSvgGraphics2D() {
		DOMImplementation impl = GenericDOMImplementation
				.getDOMImplementation();
		String namespaceURI = SVGConstants.SVG_NAMESPACE_URI;
		Document domFactory = impl.createDocument(namespaceURI, "svg", null);
		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
		ctx.setEmbeddedFontsOn(true);
		GraphicContextDefaults defaults = new GraphicContextDefaults();
		defaults.setFont(new Font("Arial", Font.PLAIN, 12));
		ctx.setGraphicContextDefaults(defaults);
		ctx.setPrecision(12);

		SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);
		// This prevents the
		// "null incompatible with text-specific antialiasing enable key" error
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		return g2d;
	}

	public void transForm(Source xmlSource, InputStream xsltStream,
			File resultFile, String areaCode) throws FileNotFoundException {
		// System.out.println("set xsl");
		Source xsltSource = new StreamSource(xsltStream);
		// System.out.println("set result");
		Result result = new StreamResult(new FileOutputStream(resultFile));

		// create an instance of TransformerFactory
		try {
			// System.out.println("make transform instance");
			TransformerFactory transFact = TransformerFactory.newInstance();

			Transformer trans = transFact.newTransformer(xsltSource);
			// set parameter:
			// relative directory <filename>_files
			trans.setParameter("destination_dir", resultFile.getName()
					+ "_files/");
			trans.setParameter("area_code", areaCode);
			trans.setParameter("folding_type", getController().getFrame()
					.getProperty("html_export_folding"));
			trans.transform(xmlSource, result);
		} catch (Exception e) {
			// System.err.println("error applying the xslt file "+e);
			freemind.main.Resources.getInstance().logException(e);
		}
		;
		return;
	}

}
