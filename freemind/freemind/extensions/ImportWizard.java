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
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Converts an unqualified class name to import statements by scanning
 * through the classpath.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version 1.0 - 6 May 1999
 */
public class ImportWizard {

	public final String lookFor = ".xml";
	/** Stores the list of all classes in the classpath */
	public Vector CLASS_LIST = new Vector(500);

	/** Build the list of classes */
	//  static {
	//
	//    // System.err.println("Making class list");
	//    buildClassList();
	//
	//    //    System.err.println("Done (" + CLASS_LIST.size() + " classes)");
	//
	//  }

	public void buildClassList() {
		String classPath = System.getProperty("java.class.path");
		String classPathSeparator = File.pathSeparator;
        // add the current dir to find more plugins
        classPath=HookFactory.getFreemindBaseDir()+classPathSeparator+classPath;

		StringTokenizer st = new StringTokenizer(classPath, classPathSeparator);
		while (st.hasMoreTokens()) {
			String classPathEntry = st.nextToken();
			File classPathFile = new File(classPathEntry);
			if (classPathFile.exists()) {
				if (classPathEntry.toLowerCase().endsWith(".jar")) {
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (classPathEntry.toLowerCase().endsWith(".zip")) {
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (classPathFile.isDirectory()) {
					addClassesFromDir(CLASS_LIST, classPathFile, classPathFile);
				}
			}
		}

	}

	/**
	 * Adds the classes from the supplied Zip file to the class list.
	 *
	 * @param classList the Vector to add the classes to
	 * @param classPathFile the File to scan as a zip file
	 */
	public void addClassesFromZip(
		Vector classList,
		File classPathFile) {
		// System.out.println("Processing jar/zip file: " + classPathFile);

		try {
			ZipFile zipFile = new ZipFile(classPathFile);
			Enumeration enum = zipFile.entries();
			while (enum.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enum.nextElement();
				String current = zipEntry.getName();
				if (current.toLowerCase().endsWith(lookFor)) {
					current =
						current.substring(
							0,
							current.length() - lookFor.length());
					current = current.replace('/', '.');
					current = current.replace('\\', '.');
					classList.addElement(current);
				}
			}
		} catch (Exception ex) {
			System.err.println(
				"Problem opening " + classPathFile + " with zip.");
		}
	}

	/**
	 * Adds the classes from the supplied directory to the class list.
	 *
	 * @param classList the Vector to add the classes to
	 * @param classPathFile the File to recursively scan as a directory
	 */
	public void addClassesFromDir(
		Vector classList,
		File rootDir,
		File currentDir) {

		String[] files = currentDir.list();
		for (int i = 0; i < files.length; i++) {
			String current = files[i];
			if (current.toLowerCase().endsWith(lookFor)) {
				String rootPath = rootDir.getPath();
				String currentPath = currentDir.getPath();
				if (! currentPath.startsWith(rootPath)) {
					System.err.println(
						"currentPath doesn't start with rootPath!\n"
							+ "rootPath: "
							+ rootPath
							+ "\n"
							+ "currentPath: "
							+ currentPath
							+ "\n");
				} else {
					current =
						current.substring(
							0,
							current.length() - lookFor.length());
					String packageName =
						currentPath.substring(rootPath.length());
					if (packageName.length() > 0) {
						// Not the current directory
						packageName = packageName.replace('\\', '.');
						packageName = packageName.replace('/', '.');
						classList.addElement(
							packageName.substring(1) + '.' + current);
					} else {
						// The current directory
						classList.addElement(current);
					}
				}
			} else {
				// Check if it's a directory to recurse into
				File currentFile = new File(currentDir, current);
				if (currentFile.isDirectory()) {
					addClassesFromDir(classList, rootDir, currentFile);
				}
			}
		}
	}

	/**
	 * Looks up an unqualified class name in the class path to find possible
	 * fully qualified matches.
	 *
	 * @param className a value of type 'String'
	 */
	public void makeImportStatement(String className) {

		String importList = "(list";

		for (int i = 0; i < CLASS_LIST.size(); i++) {
			String testName = (String) CLASS_LIST.elementAt(i);

			if ((testName.length() > className.length()
				&& testName.endsWith(className)
				&& testName.charAt(testName.length() - className.length() - 1)
					== '.')
				|| (testName.length() == className.length())
				&& testName.equals(className)) {

				// Avoid duplicates!
				testName = " \"" + testName + "\"";
				if (importList.indexOf(testName) == -1)
					importList += testName;
			}
		}

		importList += ")";

		System.out.println(importList);
		System.out.flush();

	}

	/**
	 * Tests the ImportWizard from the command line
	 *
	 * @param args an array of strings containing class names to look up
	 */
	public void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Give class names as arguments to look up");
		} else {
			for (int i = 0; i < args.length; i++) {
				System.out.println("=== " + args[i] + " ===");
				makeImportStatement(args[i]);
			}
		}
	}
} // ImportWizard

/*
 * $Log: ImportWizard.java,v $
 * Revision 1.1.4.3  2005-01-09 00:05:05  christianfoltin
 * * Bug fix: Show revisions in yellow. Thanks to michaelschwarz.
 * * Bug fix: packaging under linux.
 * * Bug fix: Quit and Cancel under Macintosh.
 * * Bug fix: Toggle encrypted nodes for unencrypted nodes give an error message.
 *
 * Revision 1.1.4.2  2004/11/16 16:42:35  christianfoltin
 * * merged from fm_040228_jython again.
 *
 * Revision 1.1.2.5  2004/11/13 08:28:35  christianfoltin
 * Startuptime reduced
 *
 * Revision 1.1.2.4  2004/09/05 19:56:39  christianfoltin
 * added jarbundler for mac os x. Application image added. TODO: Plugins must be packed.
 *
 * Revision 1.1.2.3  2004/08/29 15:18:21  christianfoltin
 * * Changed several occurences of setFolded to the undoable method.
 * * Changed the plugin class loader behaviour completely.
 *
 * Revision 1.1.2.2  2004/07/15 19:41:55  christianfoltin
 * plugins are referred by xml now
 * menus support check box entries
 *
 * Revision 1.1.2.1  2004/03/04 20:26:19  christianfoltin
 * Plugin mechanisms added
 *
 * Revision 1.6  2003/01/18 05:48:41  andyp
 * sync to jde 2.3.2
 *
 * Revision 1.4  1999/06/17 17:49:27  paulk
 * Added change log to end of file.
 *
 */

// End of ImportWizard.java
