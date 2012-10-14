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
 * XmlExporter.java
 *
 * Created on 27 January 2004, 17:23
 */

package accessories.plugins.util.xslt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 
 * @author joerg
 */
public class XmlExporter {

	/** Creates a new instance of XmlExporter */
	public XmlExporter() {
	}

	public void transForm(File xmlFile, File xsltFile, File resultFile) throws FileNotFoundException {
		// System.out.println("set source");
		Source xmlSource = new StreamSource(xmlFile);
		// System.out.println("set xsl");
		Source xsltSource = new StreamSource(xsltFile);
		// System.out.println("set result");
		Result result = new StreamResult(new FileOutputStream(resultFile));

		// create an instance of TransformerFactory
		try {
			// System.out.println("make transform instance");
			TransformerFactory transFact = TransformerFactory.newInstance();

			Transformer trans = transFact.newTransformer(xsltSource);

			trans.transform(xmlSource, result);
		} catch (Exception e) {
			// System.err.println("error applying the xslt file "+e);
			freemind.main.Resources.getInstance().logException(e);
		}
		;
		return;
	}

}
