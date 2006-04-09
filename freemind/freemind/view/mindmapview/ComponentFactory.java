/*
 * Created on 08.04.2006
 * Copyright (C) 2006 Dimitri Polivaev
 */
package freemind.view.mindmapview;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import de.xeinfach.kafenio.interfaces.KafenioPanelConfigurationInterface;
import de.xeinfach.kafenio.interfaces.KafenioPanelInterface;
import freemind.main.Resources;

public class ComponentFactory {
    static HashMap countryMap;
    static Class kafenioPanelClass;
    static Class kafenioPanelConfigurationClass;
    static Constructor kafenioPanelConstructor;
    static KafenioPanelConfigurationInterface kafenioPanelConfiguration;
    static public KafenioPanelInterface createKafenioPanel() throws Exception {
        KafenioPanelInterface htmlEditorPanel;
        if(kafenioPanelConstructor == null){
            String language = Resources.getInstance().getProperty("language");
            String[] countryMapArray = new String[]{ 
                    "de", "DE", "en", "UK", "en", "US", "es", "ES", "es", "MX", "fi", "FI", "fr", "FR", "hu", "HU", "it", "CH",
                    "it", "IT", "nl", "NL", "no", "NO", "pt", "PT", "ru", "RU", "sl", "SI", "uk", "UA", "zh", "CN" };
            
            countryMap = new HashMap();
            for (int i = 0; i < countryMapArray.length; i = i + 2) {
                countryMap.put(countryMapArray[i],countryMapArray[i+1]); } 
            
            //System.err.println(countryMap);
            //frame.setProperty("language","bf");
            
            kafenioPanelConfigurationClass = Class.forName("de.xeinfach.kafenio.KafenioPanelConfiguration");
            kafenioPanelClass = Class.forName("de.xeinfach.kafenio.KafenioPanel");
            kafenioPanelConstructor = kafenioPanelClass.getConstructor( new Class[]{ KafenioPanelConfigurationInterface.class } );
            
            kafenioPanelConfiguration = (KafenioPanelConfigurationInterface)kafenioPanelConfigurationClass.newInstance();
            kafenioPanelConfiguration.setImageDir("file://");
            kafenioPanelConfiguration.setDebugMode(true); 
            //kafenioPanelConfiguration.setLanguage("sk");
            //kafenioPanelConfiguration.setCountry("SK");
            kafenioPanelConfiguration.setLanguage(language);
            kafenioPanelConfiguration.setCountry((String)countryMap.get(language));
            kafenioPanelConfiguration.setCustomMenuItems("edit view font format insert table forms search tools help");
            // In the following excluded: new, open, styleselect
            kafenioPanelConfiguration.setCustomToolBar1("cut copy paste ld bold italic underline strike color left center right justify viewsource confirmcontent");
            // All available tool bar items:
            // new open save cut copy paste bold italic underline left center right justify styleselect ulist olist deindent indent anchor
            // image clearformats viewsource strike superscript subscript insertcharacter find color table
            
            kafenioPanelConfiguration.setShowToolbar2(false);
            kafenioPanelConfiguration.setProperty("escapeCloses","true");
            kafenioPanelConfiguration.setProperty("confirmRatherThanPost","true");
            //kafenioPanelConfiguration.setProperty("alternativeLanguage","en");
            //kafenioPanelConfiguration.setProperty("alternativeCountry","US");
        }
        htmlEditorPanel  = (KafenioPanelInterface) kafenioPanelConstructor.newInstance(new Object[]{ kafenioPanelConfiguration });
        htmlEditorPanel.getJToolBar1().setRollover(true);
        //htmlEditorPanel.getJToolBar2().setRollover(true);
        return htmlEditorPanel;           
       }

}
