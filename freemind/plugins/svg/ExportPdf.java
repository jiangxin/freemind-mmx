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

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints.Key;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import freemind.controller.Controller;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 * 
 */
public class ExportPdf extends ExportVectorGraphic {
	ExportPdfPapers papers = new ExportPdfPapers();

	public void startupMapHook() {
		super.startupMapHook();
		boolean nodeExport = Tools.safeEquals("node",
				getResourceString("export_type"));
		HashMap transcodingHints = null;
		List selecteds = getController().getSelecteds();
		Vector documentsToOpen = new Vector();
		while (!selecteds.isEmpty()) {
			MindMapNode selectedNode = (MindMapNode) selecteds.remove(0);
			String nameExtension = null;
			if (nodeExport) {
				nameExtension = " "
						+ selectedNode.getShortText(getController());
			}
			File chosenFile = chooseFile("pdf",
					getResourceString("export_pdf_text"), nameExtension);
			if (chosenFile == null) {
				return;
			}
			if (transcodingHints == null) {
				transcodingHints = choosePaper();
			}
			if (transcodingHints == null) {
				return;
			}
			getController().getFrame().setWaitingCursor(true);
			try {
				exportAsPdf(nodeExport, selectedNode, chosenFile,
						transcodingHints);
				documentsToOpen.add(chosenFile);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				JOptionPane.showMessageDialog(getController().getFrame()
						.getContentPane(), e.getLocalizedMessage(), null,
						JOptionPane.ERROR_MESSAGE);
			}
			getController().getFrame().setWaitingCursor(false);
			if (!nodeExport) {
				selecteds.clear();
			}
		}
		try {
			for (Iterator it = documentsToOpen.iterator(); it.hasNext();) {
				File fileToOpen = (File) it.next();
				getController().getFrame().openDocument(
						Tools.fileToUrl(fileToOpen));
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	/**
	 * @return a map PDFTranscoder->value.
	 */
	public HashMap choosePaper() {
		HashMap retValue = new HashMap();
		// user dialog
		String[] paperNames = papers.getPaperNames();
		Controller controller = this.getController().getController();
		ExportPdfDialog dialog = new ExportPdfDialog(getController().getFrame()
				.getJFrame(), paperNames, controller);
		dialog.setVisible(true);

		// canceled?
		if (!dialog.getResult()) {
			return null;
		}

		// get user input for format
		int orientation = dialog.getOrientation();
		String format = dialog.getFormat();
		logger.info("Paper format=" + format);

		// set page format
		PageFormat pageFormat = new PageFormat();
		pageFormat.setOrientation(orientation);
		Paper paper = papers.determinePaper(format);
		if (paper != null) {
			pageFormat.setPaper(paper);

			if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
				logger.info("Orientation: Portrait");
				// portrait
				retValue.put(PDFTranscoder.KEY_HEIGHT, new Float(pageFormat
						.getPaper().getHeight()));
				retValue.put(PDFTranscoder.KEY_WIDTH, new Float(pageFormat
						.getPaper().getWidth()));
			} else {
				logger.info("Orientation: Landscape");
				// landscape
				retValue.put(PDFTranscoder.KEY_HEIGHT, new Float(pageFormat
						.getPaper().getWidth()));
				retValue.put(PDFTranscoder.KEY_WIDTH, new Float(pageFormat
						.getPaper().getHeight()));
			}
		} else {
			logger.severe("Paper == null");
		}
		return retValue;
	}

	/** For compatibility with groovy export scripts. */
	public boolean exportAsPdf(boolean nodeExport, MindMapNode selectedNode,
			File chosenFile) throws Exception {
		return exportAsPdf(nodeExport, selectedNode, chosenFile, null);
	}

	public boolean exportAsPdf(boolean nodeExport, MindMapNode selectedNode,
			File chosenFile, HashMap pTranscoderHints) throws Exception {
		MapView view = getController().getView();
		if (view == null)
			return false;

		SVGGraphics2D g2d;
		if (nodeExport) {
			g2d = fillSVGGraphics2D(view, selectedNode);
		} else {
			g2d = fillSVGGraphics2D(view);
		}

		PDFTranscoder pdfTranscoder = new PDFTranscoder();
		/*
		 * according to
		 * https://sourceforge.net/tracker/?func=detail&atid=107118&
		 * aid=1921334&group_id=7118
		 * 
		 * Submitted By: Frank Spangenberg (f_spangenberg) Summary: Large mind
		 * maps produce invalid PDF
		 */
		pdfTranscoder.addTranscodingHint(PDFTranscoder.KEY_MAX_HEIGHT,
				new Float(19200));
		pdfTranscoder.addTranscodingHint(PDFTranscoder.KEY_MAX_WIDTH,
				new Float(19200));
		if (pTranscoderHints != null) {
			for (Iterator it = pTranscoderHints.keySet().iterator(); it
					.hasNext();) {
				Key key = (Key) it.next();
				pdfTranscoder
						.addTranscodingHint(key, pTranscoderHints.get(key));
			}
		}
		/* end patch */
		Document doc = g2d.getDOMFactory();
		Element rootE = doc.getDocumentElement();
		g2d.getRoot(rootE);
		TranscoderInput input = new TranscoderInput(doc);
		final FileOutputStream ostream = new FileOutputStream(chosenFile);
		final BufferedOutputStream bufStream = new BufferedOutputStream(ostream);
		TranscoderOutput output = new TranscoderOutput(bufStream);
		// save the image
		pdfTranscoder.transcode(input, output);
		// flush and close the stream then exit
		ostream.flush();
		ostream.close();
		return true;
	}

}