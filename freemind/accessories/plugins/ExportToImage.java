/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;

/**
 * @author foltin
 * @author kakeda
 * @author rreppel
 * 
 */
public class ExportToImage extends ExportHook {

	/**
	 * 
	 */
	public ExportToImage() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		BufferedImage image = createBufferedImage();
		if (image != null) {
			String imageType = getResourceString("image_type");

			exportToImage(image, imageType,
					getResourceString("image_description"));
		}

	}

	/**
	 * Export image.
	 */
	public boolean exportToImage(BufferedImage image, String type,
			String description) {
		File chosenFile = chooseFile(type, description, null);
		if (chosenFile == null) {
			return false;
		}
		try {
			getController().getFrame().setWaitingCursor(true);
			FileOutputStream out = new FileOutputStream(chosenFile);
			ImageIO.write(image, type, out);
			// OutputStream out = new FileOutputStream(f);
			// JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			// encoder.encode(image);
			out.close();
		} catch (IOException e1) {
			freemind.main.Resources.getInstance().logException(e1);
		}
		getController().getFrame().setWaitingCursor(false);
		return true;
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
		return;
	}

}
