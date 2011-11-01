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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import freemind.main.XMLParseException;
import freemind.view.mindmapview.IndependantMapViewCreator;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * @date 12.08.2011
 */
public class ExportTests extends FreeMindTestBase {
	private static final String TESTMAP_MM = "tests/freemind/testmap.mm";

	public void testExportPng() throws Exception {
		System.setProperty("java.awt.headless", "true");
		IndependantMapViewCreator creator = new IndependantMapViewCreator();
		creator.exportFileToPng(TESTMAP_MM, "/tmp/test.png", mFreeMindMain);

		System.out.println("Done.");
	}

	public static void main(String[] args) throws FileNotFoundException,
			XMLParseException, IOException, URISyntaxException {
		FreeMindMainMock mFreeMindMain = new FreeMindMainMock();
		JDialog fm = new JDialog();
		fm.setTitle("Title");
		fm.setModal(true);
		final Rectangle dim = new Rectangle();
		JPanel parent = new JPanel() {
			protected void paintChildren(Graphics pG) {
				pG.translate(0, -dim.y);
				super.paintChildren(pG);
				pG.translate(0, dim.y);
			}
		};
		fm.add(parent, BorderLayout.CENTER);
		IndependantMapViewCreator creator = new IndependantMapViewCreator();
		MapView mapView = creator.createMapViewForFile(TESTMAP_MM, parent,
				mFreeMindMain);
		parent.add(mapView, BorderLayout.CENTER);
		mapView.doLayout();
		Rectangle innerBounds = mapView.getInnerBounds();
		Rectangle bounds = mapView.getBounds();
		dim.y = bounds.y + innerBounds.y;
		// System.out.println(bounds);
		// System.out.println(innerBounds);
		fm.setBounds(mapView.getInnerBounds());

		fm.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		fm.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		fm.setVisible(true);

	}

}
