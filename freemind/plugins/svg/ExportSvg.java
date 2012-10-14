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


package plugins.svg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;

import org.apache.batik.svggen.SVGGraphics2D;

import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class ExportSvg extends ExportVectorGraphic {

	public void startupMapHook() {
		super.startupMapHook();
		File chosenFile = chooseFile("svg",
				getResourceString("export_svg_text"), null);
		if (chosenFile == null) {
			return;
		}
		try {
			MapView view = getController().getView();
			if (view == null)
				return;

			getController().getFrame().setWaitingCursor(true);

			SVGGraphics2D g2d = fillSVGGraphics2D(view);
			FileOutputStream bos = new FileOutputStream(chosenFile);
			final BufferedOutputStream bufStream = new BufferedOutputStream(bos);
			OutputStreamWriter osw = new OutputStreamWriter(bufStream, "UTF-8");
			g2d.stream(osw);
			osw.flush();
			bos.flush();
			bos.close();
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			JOptionPane.showMessageDialog(getController().getFrame()
					.getContentPane(), e.getLocalizedMessage(), null,
					JOptionPane.ERROR_MESSAGE);
		}
		getController().getFrame().setWaitingCursor(false);
	}

}