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

import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;

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
	private final FreeMindMain mFrame;
	private static Logger logger = null;


	public ImportWizard(FreeMindMain frame) {
		this.mFrame = frame;
		if(logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}
	}

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
        classPath=FreeMindCommon.getFreemindBaseDir()+classPathSeparator+classPath;
        // to remove duplicates
        HashSet foundPlugins = new HashSet();
		StringTokenizer st = new StringTokenizer(classPath, classPathSeparator);
		while (st.hasMoreTokens()) {
			String classPathEntry = st.nextToken();
			File classPathFile = new File(classPathEntry);
			try {
				String key = classPathFile.getCanonicalPath();
				if(foundPlugins.contains(key))
					continue;
				logger.finest("looking for plugins in " + key);
				foundPlugins.add(key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			    freemind.main.Resources.getInstance().logExecption(				e);
			}
			if (classPathFile.exists()) {
				String lowerCaseFileName = classPathEntry.toLowerCase();
				if (lowerCaseFileName.endsWith(".jar")) {
					logger.finest("searching for plugins in: "+ classPathEntry);
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (lowerCaseFileName.endsWith(".zip")) {
					logger.finest("searching for plugins in: "+ classPathEntry);
					addClassesFromZip(CLASS_LIST, classPathFile);
				} else if (classPathFile.isDirectory()) {
					logger.finest("searching for plugins in: "+ classPathEntry);
					addClassesFromDir(CLASS_LIST, classPathFile, classPathFile, 0);
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
			Enumeration enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String current = zipEntry.getName();
				if (isInteresting(current)) {
					current =
						current.substring(
							0,
							current.length() - lookFor.length());
					classList.addElement(current);
				}
			}
		} catch (Exception ex) {
			freemind.main.Resources.getInstance().logExecption(ex,
				"Problem opening " + classPathFile + " with zip.");
		}
	}

    /**
     */
    private boolean isInteresting(String current) {
        int length = current.length();
        if(length<lookFor.length()) {
            return false;
        }
        String currentPostfix = current.substring(length-lookFor.length());
        return lookFor.equalsIgnoreCase(currentPostfix);
    }

	/**
	 * Adds the classes from the supplied directory to the class list.
	 *
	 * @param classList the Vector to add the classes to
	 * @param currentDir the File to recursively scan as a directory
	 * @param recursionLevel TODO
	 */
	public void addClassesFromDir(
		Vector classList,
		File rootDir,
		File currentDir, int recursionLevel) {
	    if(recursionLevel >= 6){
            // search only the first two levels
	        return;
        }
		String[] files = currentDir.list();
		for (int i = 0; i < files.length; i++) {
			String current = files[i];
			if (isInteresting(current)) {
				String rootPath = rootDir.getPath();
				String currentPath = currentDir.getPath();
				if (! currentPath.startsWith(rootPath)) {
					logger.severe(
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
					String fileName;
					if (packageName.length() > 0) {
						// Not the current directory
						fileName = packageName.substring(1) + File.separator + current;
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
					addClassesFromDir(classList, rootDir, currentFile, recursionLevel+1);
				}
			}
		}
	}

} // ImportWizard

/*
 * $Log: ImportWizard.java,v $
 * Revision 1.1.4.6.2.8  2006-08-20 19:34:25  christianfoltin
 * * Load of plugins generalized for Mac, more flexible ClassLoaders now.
 * * Started to generalize URL and hyperlink handlings.
 * * Bug fixes for rich / plain text actions
 *
 * Revision 1.1.4.6.2.7  2006/07/30 07:25:11  christianfoltin
 * * Startup shows progress messages.
 * * Limited the plugin search to two directory levels.
 *
 * Revision 1.1.4.6.2.6  2006/07/25 20:28:20  christianfoltin
 * * Integrated patch [ 1374771 ] Javadoc call with less warnings (generated + classpath). Thanks to Eric
 *
 * Revision 1.1.4.6.2.5  2006/07/23 21:01:21  christianfoltin
 * * some startup profiling resulted in these little changes
 *
 * Revision 1.1.4.6.2.4  2006/07/23 20:34:08  christianfoltin
 * * exceptions are logged to log file, too.
 *
 * Revision 1.1.4.6.2.3  2006/07/21 05:28:12  christianfoltin
 * BasePlugins corrected
 * Notes are always present - start
 * new danish translation -> thanks
 * improved kafenio from Dimitri
 * startup time reduced
 *
 * Revision 1.1.4.6.2.2  2006/07/07 04:26:26  christianfoltin
 * * Mac changes: whole application is in one package
 * * Slider for Notes introduced.
 * * FreeMindStarter introduced to detect wrong java versions.
 * * user.properties are obsolete and were removed. auto.properties are replacing them
 * * Logging to file enabled.
 *
 * Revision 1.1.4.6.2.1  2006/04/05 21:26:26  dpolivaev
 * no message
 *
 * Revision 1.1.4.5.6.1  2006/03/11 16:42:36  dpolivaev
 * Merged with branch fm_041017_base_integration from 01 Mar 2006
 *
 * Revision 1.1.4.6  2006/01/12 23:10:12  christianfoltin
 * * Refactoring: MindMap specific actions moved to mindmapmode.
 * * Refactoring: Each Model comes with its specific ModeController.
 * * Refactoring: Each mode has a default ModeController for the case that all maps are closed.
 * * Refactoring: Each model has its own HookFactory.
 * * Startup: ProgressBar added.
 * * Browse Mode: Note viewer written.
 * * Browse Mode: Encrypted node viewer written
 * * Browse Mode: Reminder viewer written
 * * Encryption: Bug fix for empty encrypted nodes.
 * * New and revised translations nn and se (thanks to the authors)
 * * New translation of the main documentation into german (thanks to the authors)
 * * Clean up: Removed clipboard image export that has never worked.
 * * Bug fix: Removing reminders call nodeChanged.
 * * Documentation is opened in german, if german is the current language.
 *
 * Revision 1.1.4.5  2005/04/12 21:12:14  christianfoltin
 * * New feature: Time Scheduler list added.
 *
 * * Bug fix: revision plugin shutdown implemented.
 * * Storage of creation/modification times moved to <node> tag. This times are always updated, even if its display is turned off.
 * * Bug fix: removal of node background colors introduced.
 *
 * * Moved personal freemind folder to '.freemind' to hide it under Linux and MacOSX. The old folder is moved the first time.
 *
 * Revision 1.1.4.4  2005/03/10 20:50:13  christianfoltin
 * * New feature: Collaboration mode (alpha version)
 * * Bug fix: java 1.5 compiler changes (thanks to Dimitri and to brcha)
 * * Bug fix: Typing into the node gulps the first letter. Due to Dimitri.
 *
 * Revision 1.1.4.3  2005/01/09 00:05:05  christianfoltin
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
