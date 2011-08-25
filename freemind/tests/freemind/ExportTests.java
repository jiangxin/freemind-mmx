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

package tests.freemind;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import freemind.controller.Controller;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapMode;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * @date 12.08.2011
 */
public class ExportTests extends FreeMindTestBase{
	private static final String TESTMAP_MM = "tests/freemind/testmap.mm";

	public void testExportPng() throws Exception {
        System.setProperty("java.awt.headless", "true");
		InputStream xmlSource = ClassLoader.getSystemResource(
				TESTMAP_MM).openStream();
		JPanel parent = new JPanel();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tools.copyStream(xmlSource, out, true);
		Controller controller = new Controller(mFreeMindMain);
		MindMapMode mode = new MindMapMode();
		mode.init(controller);
		MindMapController mc = (MindMapController) mode.createModeController();
		MindMapMapModel model = new MindMapMapModel(mFreeMindMain, mc);
		mc.setModel(model);
		MapView mapView = new MapView(model, controller){
		    DragGestureListener getNodeDragListener() {
		        return null;
		    }
		    DropTargetListener getNodeDropListener() {
		        return null;
		    }

		};
		parent.add(mapView, BorderLayout.CENTER);
		mc.setView(mapView);
		mc.load(out.toString());
		mapView.addNotify();
		parent.setOpaque(true);
		parent.setDoubleBuffered(false); // for better performance
		Rectangle dim = new Rectangle(300, 400);
		BufferedImage backBuffer = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = backBuffer.createGraphics();
		g.setColor(java.awt.Color.white); // if want a white bg
		g.fillRect(0, 0, dim.width, dim.height);
		g.setClip(0, 0, dim.width, dim.height);

		parent.validate(); // this might not be necessary
		parent.printAll(g); // this might not be necessary

		try {
			FileOutputStream out1 = new FileOutputStream("/tmp/test.png");
			ImageIO.write(backBuffer, "png", out1);
			out1.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}

		System.out.println("Juhu");
	}
	
}
