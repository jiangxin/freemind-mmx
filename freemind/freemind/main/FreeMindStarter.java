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
 * Created on 06.07.2006
 */
/*$Id: FreeMindStarter.java,v 1.1.2.3 2008-05-28 19:39:15 christianfoltin Exp $*/
package freemind.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * @author foltin
 * 
 */
public class FreeMindStarter {

	public static final String JAVA_VERSION = System
			.getProperty("java.version");

	public static void main(String[] args) {
		// First check version of Java
		FreeMindStarter.checkJavaVersion();
		Properties defaultPreferences = readDefaultPreferences();
		createUserDirectory(defaultPreferences);
		Properties userPreferences = readUsersPreferences(defaultPreferences);
		setDefaultLocale(userPreferences);
		FreeMind.main(args, defaultPreferences, userPreferences, getUserPreferencesFile(defaultPreferences));
	}
	
	private static void checkJavaVersion() {
		System.out.println("Checking Java Version...");
		if (JAVA_VERSION.compareTo("1.4.0") < 0) {
			String message = "Warning: FreeMind requires version Java 1.4.0 or higher (your version: "
					+ System.getProperty("java.version")
					+ ", installed in "
					+ System.getProperty("java.home") + ").";
			System.err.println(message);
			JOptionPane.showMessageDialog(null, message, "FreeMind",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	
	
	
	private static void createUserDirectory(Properties pDefaultProperties) {
		File userPropertiesFolder = new File(getFreeMindDirectory(pDefaultProperties));
		try {
			// create user directory:
			if (!userPropertiesFolder.exists()) {
				userPropertiesFolder.mkdir();
			}
		} catch (Exception e) {
			// exception is logged to console as we don't have a logger
			e.printStackTrace();
			System.err
					.println("Cannot create folder for user properties and logging: '"
							+ userPropertiesFolder.getAbsolutePath() + "'");

		}
	}


	
	/**
	 * @param pProperties 
	 */
	private static void setDefaultLocale(Properties pProperties) {
		String lang = pProperties.getProperty(FreeMindCommon.RESOURCE_LANGUAGE);
		if(lang == null){
			return;
		}
		Locale localeDef = null;
		switch(lang.length()){
		case 2:
			localeDef = new Locale(lang);
			break;
		case 5:
			localeDef =new Locale(lang.substring(0, 1), lang.substring(3, 4));
			break;
		default:
			return;	
		}
		Locale.setDefault(localeDef);
	}

	private static Properties readUsersPreferences(Properties defaultPreferences) {
		Properties auto = null;
		auto = new Properties(defaultPreferences);
		try {
			InputStream in = null;
			File autoPropertiesFile = getUserPreferencesFile(defaultPreferences);
			in = new FileInputStream(autoPropertiesFile);
			auto.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err
			.println("Panic! Error while loading default properties.");
		}
		return auto;
	}

	private static File getUserPreferencesFile(Properties defaultPreferences) {
		String freemindDirectory = getFreeMindDirectory(defaultPreferences);
		File userPropertiesFolder = new File(freemindDirectory);
		File autoPropertiesFile = new File(userPropertiesFolder, defaultPreferences.getProperty("autoproperties"));
		return autoPropertiesFile;
	}



	private static String getFreeMindDirectory(Properties defaultPreferences) {
		return System.getProperty("user.home") + File.separator +  defaultPreferences.getProperty("properties_folder");
	}

	private static Properties readDefaultPreferences() {
		String propsLoc = "freemind.properties";
		URL defaultPropsURL = ClassLoader.getSystemResource(propsLoc);
		Properties props = new Properties();
		try {
			InputStream in = null;
			in = defaultPropsURL.openStream();
			props.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err
			.println("Panic! Error while loading default properties.");
		}
		return props;
	}
}
