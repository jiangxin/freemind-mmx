/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
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
 * Created on 06.09.2006
 */
/*$Id: TransformTest.java,v 1.1.2.3 2009/03/01 20:16:29 christianfoltin Exp $*/
package tests.freemind;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.jibx.runtime.IUnmarshallingContext;

import accessories.plugins.ExportToOoWriter;
import accessories.plugins.ExportWithXSLT;
import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.Plugin;
import freemind.controller.actions.generated.instance.PluginAction;
import freemind.controller.actions.generated.instance.PluginProperty;
import freemind.main.Tools;

public class TransformTest extends FreeMindTestBase {

	private static final String TESTMAP_MM = "tests/freemind/testmap.mm";
	private static final String EXPORT_WITH_XSLT_XML = "accessories/plugins/ExportWithXSLT.xml";
	private static final String EXPORT_TO_OOO = "accessories/plugins/ExportToOoWriter.xml";

	public TransformTest() throws IOException {
	}

	public void testExportHtml() throws Exception {
		String mapFileToBeExported = TESTMAP_MM;
		String destinationFileName = "/tmp/test1.html";
		Properties properties = getProperties(EXPORT_WITH_XSLT_XML,
				"accessories/plugins/ExportWithXSLT_HTML.properties");
		doExportWithExportPlugin(mapFileToBeExported, destinationFileName,
				properties);
	}

	public void testExportHtmlWithImage() throws Exception {
		String mapFileToBeExported = TESTMAP_MM;
		String destinationFileName = "/tmp/test2.html";
		Properties properties = getProperties(EXPORT_WITH_XSLT_XML,
				"accessories/plugins/ExportWithXSLT_HTML3.properties");
		doExportWithExportPlugin(mapFileToBeExported, destinationFileName,
				properties);
	}

	public void testExportHtmlApplet() throws Exception {
		String mapFileToBeExported = TESTMAP_MM;
		String destinationFileName = "/tmp/test_applet.html";
		Properties properties = getProperties(EXPORT_WITH_XSLT_XML,
				"accessories/plugins/ExportWithXSLT_Applet.properties");
		doExportWithExportPlugin(mapFileToBeExported, destinationFileName,
				properties);
	}

	public void testExportHtmlFlash() throws Exception {
		String mapFileToBeExported = TESTMAP_MM;
		String destinationFileName = "/tmp/test_flash.html";
		Properties properties = getProperties(EXPORT_WITH_XSLT_XML,
				"accessories/plugins/ExportWithXSLT_Flash.properties");
		doExportWithExportPlugin(mapFileToBeExported, destinationFileName,
				properties);
	}

	public void testExportOoo() throws Exception {
		String mapFileToBeExported = TESTMAP_MM;
		String destinationFileName = "/tmp/test_ooo.odt";
		Properties properties = getProperties(EXPORT_TO_OOO,
				"accessories/plugins/ExportToOoWriter.properties");

		doExportWithOooPlugin(mapFileToBeExported, destinationFileName,
				properties);
	}

	private Properties getProperties(String xmlPluginFile, String pluginLabel)
			throws Exception {
		Properties properties = new Properties();
		IUnmarshallingContext unmarshaller = XmlBindingTools.getInstance()
				.createUnmarshaller();

		URL pluginURL = ClassLoader.getSystemResource(xmlPluginFile);
		assertNotNull("file " + xmlPluginFile + " found", pluginURL);
		// unmarshal xml:
		Plugin plugin = null;
		InputStream in = pluginURL.openStream();
		plugin = (Plugin) unmarshaller.unmarshalDocument(in, null);
		for (Iterator iter = plugin.getListChoiceList().iterator(); iter
				.hasNext();) {

			Object p = iter.next();
			if (p instanceof PluginAction) {
				PluginAction pl = (PluginAction) p;
				if (!pluginLabel.equals(pl.getLabel()))
					continue;
				for (Iterator iterator = pl.getListChoiceList().iterator(); iterator
						.hasNext();) {
					Object plObject = (Object) iterator.next();
					if (plObject instanceof PluginProperty) {
						PluginProperty property = (PluginProperty) plObject;
						properties.put(property.getName(), property.getValue());
					}
				}
				break;
			}
		}
		return properties;
	}

	private void doExportWithExportPlugin(String mapFileToBeExported,
			String destinationFileName, Properties properties) throws Exception {
		InputStream xmlSource = ClassLoader.getSystemResource(
				mapFileToBeExported).openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tools.copyStream(xmlSource, out, true);
		ExportWithXSLT exportHook = new ExportWithXSLT();
		MindMapControllerMock controller = new MindMapControllerMock(
				mFreeMindMain, out.toString());
		exportHook.setController(controller);

		exportHook.setProperties(properties);
		File destinationFile = new File(destinationFileName);
		exportHook.transform(destinationFile);
		assertTrue("File " + destinationFile + " exists?",
				destinationFile.exists());
	}

	private void doExportWithOooPlugin(String mapFileToBeExported,
			String destinationFileName, Properties properties)
			throws IOException {
		InputStream xmlSource = ClassLoader.getSystemResource(
				mapFileToBeExported).openStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tools.copyStream(xmlSource, out, true);
		ExportToOoWriter exportHook = new ExportToOoWriter();
		exportHook.setController(new MindMapControllerMock(mFreeMindMain, out
				.toString()));

		exportHook.setProperties(properties);
		File destinationFile = new File(destinationFileName);
		boolean result = exportHook.exportToOoWriter(destinationFile);
		assertTrue("File " + destinationFile + " exists?",
				destinationFile.exists());
		assertTrue("No error during export", result);
	}

}
