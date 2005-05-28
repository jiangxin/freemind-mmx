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
/*$Id: ExportPdf.java,v 1.1.4.1 2004-11-16 16:42:38 christianfoltin Exp $*/

package plugins.svg;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import freemind.view.mindmapview.MapView;

/**
 * @author foltin
 *  
 */
public class ExportPdf extends ExportVectorGraphic {

    public void startupMapHook() {
        super.startupMapHook();
        File chosenFile = chooseFile("pdf",
                getResourceString("export_pdf_text"));
        if (chosenFile == null) {
            return;
        }
        try {
            MapView view = getController().getView();
            if (view == null)
                return;

            getController().getFrame().setWaitingCursor(true);
            
            SVGGraphics2D g2d = fillSVGGraphics2D(view);

            PDFTranscoder pdfTranscoder = new PDFTranscoder();
            Document doc = g2d.getDOMFactory();
            Element rootE = doc.getDocumentElement();
            g2d.getRoot(rootE);
            TranscoderInput input = new TranscoderInput(doc);
            FileOutputStream ostream = new FileOutputStream(chosenFile);
			TranscoderOutput output = new TranscoderOutput(ostream);
            // save the image
            pdfTranscoder.transcode(input, output);
            // flush and close the stream then exit
            ostream.flush();
            ostream.close();

            
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getController().getFrame().getContentPane(), e.getLocalizedMessage(), null, JOptionPane.ERROR_MESSAGE);
        }
        getController().getFrame().setWaitingCursor(false);
    }


}