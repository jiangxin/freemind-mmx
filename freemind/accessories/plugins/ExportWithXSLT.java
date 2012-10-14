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
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import accessories.plugins.util.html.ClickableImageCreator;
import accessories.plugins.util.xslt.ExportDialog;
import freemind.extensions.ExportHook;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author foltin
 * 
 *         Exports the map using an XSLT script. The parameterization is
 *         described in the corresponding Export... .xml-file.
 */
public class ExportWithXSLT extends ExportHook {
	private static final String NAME_EXTENSION_PROPERTY = "name_extension";
	/**
	 * For test purposes. True=no error
	 */
	private boolean mTransformResultWithoutError = false;

	protected File chooseFile() {

		String nameExtension = null;
		if (getProperties().containsKey(NAME_EXTENSION_PROPERTY)) {
			nameExtension = getResourceString(NAME_EXTENSION_PROPERTY);
		}
		return chooseFile(getResourceString("file_type"),
				getTranslatableResourceString("file_description"),
				nameExtension);
	}

	/**
	 * 
	 */
	public ExportWithXSLT() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.MindMapHook#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		ModeController mc = getController();
		MindMap model = getController().getMap();
		if (Tools.safeEquals(getResourceString("file_type"), "user")) {
			if (model == null)
				return; // there may be no map open
			if ((model.getFile() == null) || model.isReadOnly()) {
				if (mc.save()) {
					export(model.getFile());
					return;
				} else
					return;
			} else
				export(model.getFile());
		} else {
			File saveFile = chooseFile();
			if (saveFile == null) {
				// no file.
				return;
			}
			try {
				mTransformResultWithoutError = true;
				String transformErrors = transform(saveFile);
				if (transformErrors != null) {
					JOptionPane.showMessageDialog(null,
							getResourceString(transformErrors), "Freemind",
							JOptionPane.ERROR_MESSAGE);
					mTransformResultWithoutError = false;
				} else {
					if (Tools
							.safeEquals(getResourceString("load_file"), "true")) {
						getController().getFrame().openDocument(
								Tools.fileToUrl(saveFile));
					}
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				mTransformResultWithoutError = false;
			}
		}
	}

	/**
	 * @param saveFile
	 * @return If ok: null, else: the resource identifier of the error string.
	 * @throws Exception
	 * 
	 */
	public String transform(File saveFile) throws Exception {
		// get AREA:
		// create HTML image?
		boolean create_image = Tools.safeEquals(
				getResourceString("create_html_linked_image"), "true");
		String areaCode = getAreaCode(create_image);
		// XSLT Transformation
		String xsltFileName = getResourceString("xslt_file");
		boolean success = transformMapWithXslt(xsltFileName, saveFile, areaCode);
		if (!success) {
			// JOptionPane.showMessageDialog(null,
			// getResourceString("error_applying_template"),
			// "Freemind", JOptionPane.ERROR_MESSAGE);
			return "error_applying_template";
		}
		// create directory?
		if (success
				&& Tools.safeEquals(getResourceString("create_dir"), "true")) {
			String directoryName = saveFile.getAbsolutePath() + "_files";
			success = createDirectory(directoryName);

			// copy files from the resources to the file system:
			if (success) {
				String files = getResourceString("files_to_copy");
				String filePrefix = getResourceString("file_prefix");
				copyFilesFromResourcesToDirectory(directoryName, files,
						filePrefix);
			}
			// copy icons?
			if (success
					&& Tools.safeEquals(getResourceString("copy_icons"), "true")) {
				success = copyIcons(directoryName);
			}
			if (success
					&& Tools.safeEquals(getResourceString("copy_map"), "true")) {
				success = copyMap(directoryName);
			}
			if (success && create_image) {
				createImageFromMap(directoryName);
			}
		}
		if (!success) {
			// JOptionPane.showMessageDialog(null,
			// getResourceString("error_creating_directory"), "Freemind",
			// JOptionPane.ERROR_MESSAGE);
			return "error_creating_directory";
		}
		return null;
	}

	private boolean copyMap(String pDirectoryName) throws IOException {
		boolean success = true;
		// Generating output Stream
		BufferedWriter fileout = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(pDirectoryName
						+ File.separator + "map.mm")));
		getController().getMap().getFilteredXml(fileout);
		return success;
	}

	/**
     */
	private boolean copyIcons(String directoryName) {
		boolean success;
		String iconDirectoryName = directoryName + File.separatorChar + "icons";

		success = createDirectory(iconDirectoryName);
		if (success) {
			copyIconsToDirectory(iconDirectoryName);
		}
		return success;
	}

	/**
     */
	private void createImageFromMap(String directoryName) {
		// in the test case, we don't have a viewer and skip the image.
		if (getController().getView() == null)
			return;
		// create image:
		BufferedImage image = createBufferedImage();
		try {
			FileOutputStream out = new FileOutputStream(directoryName
					+ File.separator + "image.png");
			ImageIO.write(image, "png", out);
			out.close();
		} catch (IOException e1) {
			freemind.main.Resources.getInstance().logException(e1);
		}
	}

	/**
     */
	private void copyIconsToDirectory(String directoryName2) {
		Vector iconNames = MindIcon.getAllIconNames();
		for (int i = 0; i < iconNames.size(); ++i) {
			String iconName = ((String) iconNames.get(i));
			MindIcon myIcon = MindIcon.factory(iconName);
			copyFromResource(MindIcon.getIconsPath(),
					myIcon.getIconBaseFileName(), directoryName2);
		}
		File iconDir = new File(Resources.getInstance().getFreemindDirectory(),
				"icons");
		if (iconDir.exists()) {
			String[] userIconArray = iconDir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(".*\\.png");
				}
			});
			for (int i = 0; i < userIconArray.length; ++i) {
				String iconName = userIconArray[i];
				if (iconName.length() == 4) {
					continue;
				}
				copyFromFile(iconDir.getAbsolutePath(), iconName,
						directoryName2);
			}
		}

	}

	/**
     */
	private void copyFilesFromResourcesToDirectory(String directoryName,
			String files, String filePrefix) {
		StringTokenizer tokenizer = new StringTokenizer(files, ",");
		while (tokenizer.hasMoreTokens()) {
			String next = tokenizer.nextToken();
			copyFromResource(filePrefix, next, directoryName);
		}
	}

	/**
     */
	private boolean createDirectory(String directoryName) {
		File dir = new File(directoryName);
		// create directory, if not exists:
		if (!dir.exists()) {
			return dir.mkdir();
		}
		return true;
	}

	/**
	 * @throws IOException
	 */
	private boolean transformMapWithXslt(String xsltFileName, File saveFile,
			String areaCode) throws IOException {
		StringWriter writer = getMapXml();
		StringReader reader = new StringReader(writer.getBuffer().toString());
		// search for xslt file:
		URL xsltUrl = getResource(xsltFileName);
		if (xsltUrl == null) {
			logger.severe("Can't find " + xsltFileName + " as resource.");
			throw new IllegalArgumentException("Can't find " + xsltFileName
					+ " as resource.");
		}
		InputStream xsltFile = xsltUrl.openStream();
		return transform(new StreamSource(reader), xsltFile, saveFile, areaCode);
	}

	/**
	 * @throws IOException
	 */
	private StringWriter getMapXml() throws IOException {
		// get output:
		StringWriter writer = new StringWriter();
		// get XML
		getController().getMap().getFilteredXml(writer);
		return writer;
	}

	/**
	 * @param create_image
	 */
	private String getAreaCode(boolean create_image) {
		String areaCode = "";
		if (create_image) {
			MindMapNode root = getController().getMap().getRootNode();
			ClickableImageCreator creator = new ClickableImageCreator(root,
					getController(),
					getResourceString("link_replacement_regexp"));
			areaCode = creator.generateHtml();
		}
		return areaCode;
	}

	private void export(File file) {
		ExportDialog exp = new ExportDialog(file, getController());
		exp.setVisible(true);
	}

	public boolean transform(Source xmlSource, InputStream xsltStream,
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
			trans.setParameter("destination_dir", Tools.fileToRelativeUrlString(new File(resultFile.getAbsolutePath()
					+ "_files/"), resultFile) + "/");
			trans.setParameter("area_code", areaCode);
			trans.setParameter("folding_type", getController().getFrame()
					.getProperty("html_export_folding"));
			trans.transform(xmlSource, result);
		} catch (Exception e) {
			// System.err.println("error applying the xslt file "+e);
			freemind.main.Resources.getInstance().logException(e);
			return false;
		}
		;
		return true;
	}

	public boolean isTransformResultWithoutError() {
		return mTransformResultWithoutError;
	}

}
