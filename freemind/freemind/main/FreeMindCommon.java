/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 10.01.2006
 */
/*$Id: FreeMindCommon.java,v 1.1.2.2 2006-03-14 21:56:27 christianfoltin Exp $*/
package freemind.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author foltin
 * 
 */
public class FreeMindCommon {

	public static final String POSTFIX_TRANSLATE_ME = "[translate me]";

    private static PropertyResourceBundle languageResources;

	private PropertyResourceBundle defaultResources;

	public static final String RESOURCE_LANGUAGE = "language";

	public static final String DEFAULT_LANGUAGE = "en";

	private final FreeMindMain mFreeMindMain;

	private static Logger logger = null;

	/**
	 * 
	 */
	public FreeMindCommon(FreeMindMain main) {
		super();
		// TODO Auto-generated constructor stub
		this.mFreeMindMain = main;
		if (logger == null)
			logger = main.getLogger(this.getClass().getName());

	}

	public String getProperty(String key) {
		return mFreeMindMain.getProperty(key);
	}

	/** Returns the ResourceBundle with the current language */
	public ResourceBundle getResources() {
		if (languageResources == null) {
			try {
				String lang = getProperty(RESOURCE_LANGUAGE);
				if (lang == null || lang.equals("automatic")) {
					lang = Locale.getDefault().getLanguage() + "_"
							+ Locale.getDefault().getCountry();
					if (getLanguageResources(lang) == null) {
						lang = Locale.getDefault().getLanguage();
						if (getLanguageResources(lang) == null) {
							// default is english.
							lang = DEFAULT_LANGUAGE;
						}
					}
				}
				languageResources = getLanguageResources(lang);
				defaultResources = getLanguageResources(DEFAULT_LANGUAGE);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.severe("Error loading Resources");
				return null;
			}
		}
		return languageResources;
	}

	/**
	 * @param lang
	 * @return
	 * @throws IOException
	 */
	private PropertyResourceBundle getLanguageResources(String lang)
			throws IOException {
		URL systemResource = mFreeMindMain.getResource("Resources_" + lang
				+ ".properties");
		if (systemResource == null) {
			return null;
		}
		InputStream in = systemResource.openStream();
		if (in == null) {
			return null;
		}
		PropertyResourceBundle bundle = new PropertyResourceBundle(in);
		in.close();
		return bundle;
	}

	public String getResourceString(String resource) {
		try {
			return getResources().getString(resource);
		} catch (Exception ex) {
			logger.severe("Warning - resource string not found:" + resource);
			try {
				return defaultResources.getString(resource) + POSTFIX_TRANSLATE_ME;
			} catch (Exception e) {
				logger
						.severe("Warning - resource string not found (even in english):"
								+ resource);
				return resource;
			}
		}
	}

	public void clearLanguageResources() {
		languageResources = null;
	}
}
