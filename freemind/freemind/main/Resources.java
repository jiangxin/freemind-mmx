/*
 * Created on 12.07.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.main;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import freemind.controller.Controller;
import freemind.modes.MindIcon;

/**
 * @author Dimitri Polivaev
 * 12.07.2005
 */
public class Resources {
    private class MindIconRenderer implements ListCellRenderer{
        private Map name2components = null;
        private ListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        public MindIconRenderer(){
        }
        Component getComponent(String name){
            if(name2components == null) name2components = new HashMap();
            JLabel component = (JLabel)name2components.get(name);
            if (component == null){
                Icon icon = MindIcon.factory(name).getIcon(Resources.getInstance().getFrame());
                component = new JLabel(icon);
                name2components.put(name, component);            
            }        
            return component;
        }
        
        Component getComponent(MindIcon mi){
            return getComponent(mi.getName());
        }
        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            if(value == null) return new JLabel("--");
            if (value instanceof MindIcon){
                MindIcon mi = (MindIcon) value;
                Component component = getComponent(mi);
                if (isSelected  || cellHasFocus){
                    component.setBackground(Color.BLUE);            
                }
                return component;
            }
            return defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus );
        }
        
    }
    
    private FreeMindMain frame;
    private MindIconRenderer mindIconRenderer = new MindIconRenderer();
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
    public ListCellRenderer getMindIconRenderer() {
        return mindIconRenderer;
    }
    
    
    public Component getComponent(String name) {
        return mindIconRenderer.getComponent(name);
    }
}
