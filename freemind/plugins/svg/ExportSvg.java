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
 * Created on 01.11.2004
 */
/*$Id: ExportSvg.java,v 1.1.2.1 2004-11-06 22:06:26 christianfoltin Exp $*/

package plugins.svg;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import freemind.extensions.ExportHook;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 *  
 */
public class ExportSvg extends ExportHook {

    public void startupMapHook() {
        super.startupMapHook();
        File chosenFile = chooseFile("svg",
                getResourceString("export_svg_text"));
        if (chosenFile == null) {
            return;
        }
        try {
            getController().getFrame().setWaitingCursor(true);
            FileWriter out = new FileWriter(chosenFile);
            //		  Get a DOMImplementation
            DOMImplementation domImpl = GenericDOMImplementation
                    .getDOMImplementation();

            // Create an instance of org.w3c.dom.Document
            Document document = domImpl.createDocument(null, "svg", null);

            // Create an instance of the SVG Generator
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

            MapView view = getController().getView();
            if (view == null)
                return;

            //Determine which part of the view contains the nodes of the map:
            //(Needed to eliminate areas of whitespace around the actual
            // rendering of the map)

            NodeAdapter root = (NodeAdapter) getController().getMap().getRoot();
            Rectangle innerBounds = view.getInnerBounds(root.getViewer());

            svgGenerator.clipRect(innerBounds.x, innerBounds.y,
                    innerBounds.width, innerBounds.height);
            view.print(svgGenerator);

            boolean useCSS = true; // we want to use CSS style attribute
            svgGenerator.stream(out, useCSS);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.getLocalizedMessage(), null, JOptionPane.ERROR_MESSAGE);
        }
        getController().getFrame().setWaitingCursor(false);

    }
}