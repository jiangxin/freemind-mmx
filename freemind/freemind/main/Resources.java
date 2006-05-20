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
 * Created on 12.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.main;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JFrame;

/**
 * @author Dimitri Polivaev
 * 12.07.2005
 */
public class Resources {
    private FreeMindMain frame;
    static Resources resourcesInstance = null;
    private HashMap countryMap;
    private Resources(FreeMindMain frame) {
        this.frame = frame;  
    }
    
    static public void createInstance(FreeMindMain frame){
        if (resourcesInstance == null) resourcesInstance = new Resources(frame);
    }
    
    public URL getResource(String resource) {
        return frame.getResource(resource);
    }
    
    public String getResourceString(String resource) {
        return frame.getResourceString(resource);
    }
         
    static public Resources getInstance(){
        return resourcesInstance;
    }

    public String getFreemindDirectory() {
        return frame.getFreemindDirectory();
    }

    public String getFreemindVersion() {
        return frame.getFreemindVersion();
    }

    public int getIntProperty(String key, int defaultValue) {
        return frame.getIntProperty(key, defaultValue);
    }

    public Properties getProperties() {
        return frame.getProperties();
    }

    public String getProperty(String key) {
        return frame.getProperty(key);
    }

    public ResourceBundle getResources() {
        return frame.getResources();
    }

    public HashMap getCountryMap() {
        if(countryMap == null){
            String[] countryMapArray = new String[]{ 
                    "de", "DE", "en", "UK", "en", "US", "es", "ES", "es", "MX", "fi", "FI", "fr", "FR", "hu", "HU", "it", "CH",
                    "it", "IT", "nl", "NL", "no", "NO", "pt", "PT", "ru", "RU", "sl", "SI", "uk", "UA", "zh", "CN" };
            
            countryMap = new HashMap();
            for (int i = 0; i < countryMapArray.length; i = i + 2) {
                countryMap.put(countryMapArray[i],countryMapArray[i+1]); }
        }
        return countryMap;
    }
}
