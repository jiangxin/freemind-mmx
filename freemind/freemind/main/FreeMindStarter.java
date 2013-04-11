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
/*$Id: FreeMindStarter.java,v 1.1.2.11 2009/03/29 19:37:23 christianfoltin Exp $*/
package freemind.main;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * This class should check the java version and start freemind. In order to be
 * able to check, it must be startable with java versions < 1.4. We have
 * therefore a section in the build.xml that explicitly compiles this class for
 * java 1.1 compatibility. Currently, it is unclear, if this works as expected.
 * But in any case, almost no dependencies to other FreeMind sources should be
 * used here.
 * 
 * @author foltin
 * 
 */
public class FreeMindStarter {
	/** Doubled variable on purpose. See header of this class. */
	static final String JAVA_VERSION = System.getProperty("java.version");

	public static void main(String[] args) {
		FreeMindStarter starter = new FreeMindStarter();
		// First check version of Java
		starter.checkJavaVersion();
		Properties defaultPreferences = starter.readDefaultPreferences();
		starter.createUserDirectory(defaultPreferences);
		Properties userPreferences =
				starter.readUsersPreferences(defaultPreferences);
		starter.setDefaultLocale(userPreferences);

		// Christopher Robin Elmersson: set
		Toolkit xToolkit = Toolkit.getDefaultToolkit();

		// workaround for java bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7075600
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
		try {
			java.lang.reflect.Field awtAppClassNameField =
					xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			try {
				awtAppClassNameField.set(xToolkit, "FreeMind");
			} catch (java.lang.IllegalAccessException ex) {
				System.err.println("Could not set window name");
			}
		} catch (NoSuchFieldException ex) {
			// System.err.println("Could not get awtAppClassName");
		}

		// use reflection to call :
		// FreeMind.main(args, defaultPreferences, userPreferences,
		// starter.getUserPreferencesFile(defaultPreferences));
		try {
			Class mainClass = Class.forName("freemind.main.FreeMind");
			Method mainMethod = mainClass.getMethod("main", new Class[] {
					String[].class, Properties.class, Properties.class,
					File.class });
			mainMethod.invoke(null,
							new Object[] {
									args,
									defaultPreferences,
									userPreferences,
									starter.getUserPreferencesFile(defaultPreferences) });

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"freemind.main.FreeMind can't be started",
					"Startup problem", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private void checkJavaVersion() {
		System.out.println("Checking Java Version...");
		if (JAVA_VERSION.compareTo("1.4.0") < 0) {
			String message = "Warning: FreeMind requires version Java 1.4.0 or higher (your version: "
					+ JAVA_VERSION
					+ ", installed in "
					+ System.getProperty("java.home") + ").";
			System.err.println(message);
			JOptionPane.showMessageDialog(null, message, "FreeMind",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	private void createUserDirectory(Properties pDefaultProperties) {
		File userPropertiesFolder = new File(
				getFreeMindDirectory(pDefaultProperties));
		try {
			// create user directory:
			if (!userPropertiesFolder.exists()) {
				userPropertiesFolder.mkdir();
			}
		} catch (Exception e) {
			// exception is logged to console as we don't have a logger
			e.printStackTrace();
			System.err.println("Cannot create folder for user properties and logging: '"
							+ userPropertiesFolder.getAbsolutePath() + "'");

		}
	}

	/**
	 * @param pProperties
	 */
	private void setDefaultLocale(Properties pProperties) {
		String lang = pProperties.getProperty(FreeMindCommon.RESOURCE_LANGUAGE);
		if (lang == null) {
			return;
		}
		Locale localeDef = null;
		switch (lang.length()) {
		case 2:
			localeDef = new Locale(lang);
			break;
		case 5:
			localeDef = new Locale(lang.substring(0, 1), lang.substring(3, 4));
			break;
		default:
			return;
		}
		Locale.setDefault(localeDef);
	}

	private Properties readUsersPreferences(Properties defaultPreferences) {
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
			System.err.println("Panic! Error while loading default properties.");
		}
		return auto;
	}

	private File getUserPreferencesFile(Properties defaultPreferences) {
		if (defaultPreferences == null) {
			System.err.println("Panic! Error while loading default properties.");
			System.exit(1);
		}
		String freemindDirectory = getFreeMindDirectory(defaultPreferences);
		File userPropertiesFolder = new File(freemindDirectory);
		File autoPropertiesFile = new File(userPropertiesFolder,
				defaultPreferences.getProperty("autoproperties"));
		return autoPropertiesFile;
	}

	private String getFreeMindDirectory(Properties defaultPreferences) {
		return System.getProperty("user.home") + File.separator
				+ defaultPreferences.getProperty("properties_folder");
	}

	public Properties readDefaultPreferences() {
		String propsLoc = "freemind.properties";
		URL defaultPropsURL =
				this.getClass().getClassLoader().getResource(propsLoc);
		Properties props = new Properties();
		try {
			InputStream in = defaultPropsURL.openStream();
			props.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Panic! Error while loading default properties.");
		}
		return props;
	}
	
	public static class ProxyAuthenticator extends Authenticator {

	    private String user, password;

	    public ProxyAuthenticator(String user, String password) {
	        this.user = user;
	        this.password = password;
	    }

	    protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(user, password.toCharArray());
	    }
	}
}