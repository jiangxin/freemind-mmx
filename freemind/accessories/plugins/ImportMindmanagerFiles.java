/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2005  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: ImportMindmanagerFiles.java,v 1.1.4.2 2005-07-12 15:41:13 dpolivaev Exp $*/

package accessories.plugins;

import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JFileChooser;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;
import freemind.extensions.ModeControllerHookAdapter;

/**
 * Applies an XSLT to the Document.xml file of MindManager(c) files.
 */
public class ImportMindmanagerFiles extends ModeControllerHookAdapter {

    public ImportMindmanagerFiles() {
        super();

    }

    public void startupMapHook() {
        super.startupMapHook();
        String type = "mmap";
        Container component = getController().getFrame().getContentPane();
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new ExportHook.ImageFilter(type, null /*
                                                                                 * No
                                                                                 * description
                                                                                 * so
                                                                                 * far
                                                                                 */));
        File mmFile = getController().getMap().getFile();
        if (mmFile != null && mmFile.getParentFile() != null) {
            chooser.setSelectedFile(mmFile.getParentFile());
        }
        int returnVal = chooser.showOpenDialog(component);
        if (returnVal != JFileChooser.APPROVE_OPTION) { // not ok pressed
            return;
        }

        // |= Pressed O.K.
        File chosenFile = chooser.getSelectedFile();
        importMindmanagerFile(chosenFile);

    }

    private void importMindmanagerFile(File file) {
        // from e455. Retrieving a Compressed File from a ZIP File
        // http://javaalmanac.com/egs/java.util.zip/GetZip.html
        try {
            // Open the ZIP file
            ZipInputStream in = new ZipInputStream(new FileInputStream(file));

            while (in.available() != 0) {
                ZipEntry entry = in.getNextEntry();
                if (!entry.getName().equals("Document.xml")) {
                    continue;
                }

                // now apply the transformation:
                // search for xslt file:
                String xsltFileName = "accessories/mindmanager2mm.xsl";
                URL xsltUrl = getResource(xsltFileName);
                if (xsltUrl == null) {
                    logger.severe("Can't find " + xsltFileName
                            + " as resource.");
                    throw new IllegalArgumentException("Can't find "
                            + xsltFileName + " as resource.");
                }
                InputStream xsltFile = xsltUrl.openStream();
                String xml = transForm(new StreamSource(in), xsltFile);
                if (xml != null) {
                    // now start a new map with this string:
                    File tempFile = File.createTempFile(file.getName(), ".mm", file.getParentFile());
                    FileWriter fw = new FileWriter(tempFile);
                    fw.write(xml);
                    fw.close();
                    getController().load(tempFile);
                }
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String transForm(Source xmlSource, InputStream xsltStream) {
        Source xsltSource = new StreamSource(xsltStream);
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        // create an instance of TransformerFactory
        try {
            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(xsltSource);
            trans.transform(xmlSource, result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return writer.toString();
    }

}
