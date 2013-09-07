/*
 *    ImportWizard.java
 *    Copyright (C) 1999 Len Trigg (trigg@cs.waikato.ac.nz)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// adapted to freemind by christianfoltin, 29.2.2004.
// taken from /usr/share/xemacs/xemacs-packages/etc/jde/java/src/jde/wizards/ImportWizard.java
// changed: package name, commented out the static method.
//			if (current.toLowerCase().endsWith(".properties")) {
//											   // formerly ".class"
// and related changes.
// commented out: // For Java 2! ...

package freemind.extensions;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import freemind.main.FreeMindMain;

/**
 * Converts an unqualified class name to import statements by scanning through
 * the classpath.
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version 1.0 - 6 May 1999
 */
public class ImportWizard {

	public final String lookFor = ".xml";
	/** Stores the list of all classes in the classpath */
	public Vector CLASS_LIST = new Vector(500);
	private final FreeMindMain mFrame;
	private static Logger logger = null;

	public ImportWizard(FreeMindMain frame) {
		this.mFrame = frame;
		if (logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}
	}

	/** Build the list of classes */
	// static {
	//
	// // System.err.println("Making class list");
	// buildClassList();
	//
	// // System.err.println("Done (" + CLASS_LIST.size() + " classes)");
	//
	// }

	public void buildClassList() {
		String classPath = System.getProperty("java.class.path");
		String classPathSeparator = File.pathSeparator;
		// add the current dir to find more plugins
		classPath = mFrame.getFreemindBaseDir() + classPathSeparator
				+ classPath;
		logger.info("Classpath for plugins:" + classPath);
		// to remove duplicates
		HashSet foundPlugins = new HashSet();
		StringTokenizer st = new StringTokenizer(classPath, classPathSeparator);
		while (st.hasMoreTokens()) {
			String classPathEntry = st.nextToken();
			File classPathFile = new File(classPathEntry);
			try {
				String key = classPathFile.getCanonicalPath();
				if (foundPlugins.contains(key))
					continue;
				logger.fine("looking for plugins in " + key);
				foundPlugins.add(key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				freemind.main.Resources.getInstance().logException(e);
			}
			if (classPathFile.exists()) {
				String lowerCaseFileName = classPathEntry.toLowerCase();
				if (lowerCaseFileName.endsWith(".jar")) {
					logger.fine("searching for plugins in: " + classPathEntry);
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (lowerCaseFileName.endsWith(".zip")) {
					logger.fine("searching for plugins in: " + classPathEntry);
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (classPathFile.isDirectory()) {
					logger.fine("searching for plugins in: " + classPathEntry);
					addClassesFromDir(CLASS_LIST, classPathFile, classPathFile,
							0);
				}
			}
		}

	}

	/**
	 * Adds the classes from the supplied Zip file to the class list.
	 * 
	 * @param classList
	 *            the Vector to add the classes to
	 * @param classPathFile
	 *            the File to scan as a zip file
	 */
	public void addClassesFromZip(Vector classList, File classPathFile) {
		// System.out.println("Processing jar/zip file: " + classPathFile);

		try {
			ZipFile zipFile = new ZipFile(classPathFile);
			Enumeration enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String current = zipEntry.getName();
				if (isInteresting(current)) {
					current = current.substring(0,
							current.length() - lookFor.length());
					classList.addElement(current);
				}
			}
		} catch (Exception ex) {
			freemind.main.Resources.getInstance().logException(ex,
					"Problem opening " + classPathFile + " with zip.");
		}
	}

	/**
     */
	private boolean isInteresting(String current) {
		int length = current.length();
		if (length < lookFor.length()) {
			return false;
		}
		String currentPostfix = current.substring(length - lookFor.length());
		return lookFor.equalsIgnoreCase(currentPostfix);
	}

	/**
	 * Adds the classes from the supplied directory to the class list.
	 * 
	 * @param classList
	 *            the Vector to add the classes to
	 * @param currentDir
	 *            the File to recursively scan as a directory
	 * @param recursionLevel
	 *            To ensure that after a certain depth the recursive directory
	 *            search stops
	 */
	public void addClassesFromDir(Vector classList, File rootDir,
			File currentDir, int recursionLevel) {
		if (recursionLevel >= 6) {
			// search only the first levels
			return;
		}
		String[] files = currentDir.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String current = files[i];
				logger.fine("looking at: " + current);
				if (isInteresting(current)) {
					String rootPath = rootDir.getPath();
					String currentPath = currentDir.getPath();
					if (!currentPath.startsWith(rootPath)) {
						logger.severe("currentPath doesn't start with rootPath!\n"
								+ "rootPath: "
								+ rootPath
								+ "\n"
								+ "currentPath: " + currentPath + "\n");
					} else {
						current = current.substring(0, current.length()
								- lookFor.length());
						String packageName = currentPath.substring(rootPath
								.length());
						String fileName;
						if (packageName.length() > 0) {
							// Not the current directory
							fileName = packageName.substring(1)
									+ File.separator + current;
						} else {
							// The current directory
							fileName = current;
						}
						classList.addElement(fileName);
						logger.info("Found: " + fileName);
					}
				} else {
					// Check if it's a directory to recurse into
					File currentFile = new File(currentDir, current);
					if (currentFile.isDirectory()) {
						addClassesFromDir(classList, rootDir, currentFile,
								recursionLevel + 1);
					}
				}
			}
		}
	}

} // ImportWizard

/*
 * $Log: ImportWizard.java,v $ Revision 1.1.4.6.2.16 2008/07/28 03:06:01
 * christianfoltin * Bug fix: B19 startup fails with
 * "Mode not available: Mindmap" *
 * https://sourceforge.net/tracker/?func=detail&atid
 * =107118&aid=2025279&group_id=7118 * FreeMind Starter: no more statics.
 * 
 * Revision 1.1.4.6.2.15 2008/07/18 16:14:25 christianfoltin * Renamed zh into
 * zh_TW (patch from willyann * Reverted changes to Tools.
 * 
 * Revision 1.1.4.6.2.14 2006/12/14 16:45:00 christianfoltin * Search & Replace
 * Dialog with menu and nicer. Bug fixes...
 * 
 * Revision 1.1.4.6.2.13 2006/11/28 08:25:01 dpolivaev no message
 * 
 * Revision 1.1.4.6.2.12 2006/11/26 10:20:40 dpolivaev LocalProperties,
 * TextResources for SHTML and Bug Fixes
 * 
 * Revision 1.1.4.6.2.11 2006/11/12 21:07:06 christianfoltin * Mac bug fixes
 * (class path, error messages, directories)
 * 
 * Revision 1.1.4.6.2.10 2006/09/05 21:17:58 dpolivaev SimplyHTML
 */

// End of ImportWizard.java
