/*
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;
import freemind.modes.NodeAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class ExportToOoWriter extends ExportHook {

	/**
	 * 
	 */
	public ExportToOoWriter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		File chosenFile = chooseFile("sxw", null);
		if (chosenFile == null) {
			return;
		}
		getController().getFrame().setWaitingCursor(true);
		try {
			exportToOoWriter(chosenFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getController().getFrame().setWaitingCursor(false);
	}

	private void exportToOoWriter(File file) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(file));
		ZipEntry entry = new ZipEntry("content.xml");
		zipout.putNextEntry(entry);
		// get output:
		StringWriter writer = new StringWriter();
		// get XML
		getController().getMap().getXml(writer);
		StringReader reader = new StringReader(writer.getBuffer().toString());
		// search for xslt file:
		String xsltFileName = "accessories/mm2oowriter.xsl";
		URL xsltUrl = getResource(xsltFileName);
		if (xsltUrl == null) {
			logger.severe("Can't find " + xsltFileName + " as resource.");
			throw new IllegalArgumentException("Can't find " + xsltFileName
					+ " as resource.");
		}
		InputStream xsltStream = xsltUrl.openStream();
		// System.out.println("set xsl");
		Source xsltSource = new StreamSource(xsltStream);
		// System.out.println("set result");
		Result result = new StreamResult(zipout);

		// create an instance of TransformerFactory
		try {
			// System.out.println("make transform instance");
			TransformerFactory transFact = TransformerFactory.newInstance();

			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(new StreamSource(reader), result);
		} catch (Exception e) {
			// System.err.println("error applying the xslt file "+e);
			e.printStackTrace();
		}
		zipout.closeEntry();
		zipout.close();

	}

}
