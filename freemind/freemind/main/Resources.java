/*
 * Created on 12.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.main;

import java.net.URL;

import javax.swing.JFrame;

/**
 * @author Dimitri Polivaev
 * 12.07.2005
 */
public class Resources {
    private FreeMindMain frame;
    static Resources resourcesInstance = null;
    private Resources(FreeMindMain frame) {
        this.frame = frame;  
    }
    
    static public void createInstance(FreeMindMain frame){
        if (resourcesInstance == null) resourcesInstance = new Resources(frame);
    }
    
    public URL getResource(String resource) {
        return getFrame().getResource(resource);
    }
    
    public String getResourceString(String resource) {
        return frame.getResourceString(resource);
    }
    
    static public Resources getInstance(){
        return resourcesInstance;
    }
    public JFrame getJFrame() {
        FreeMindMain f = getFrame();
        if (f instanceof JFrame) return (JFrame) f;
        return null;
    }
    
    public FreeMindMain getFrame() {
        return frame;
    }
}
