/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: FreeMindMain.java,v 1.12.14.5.2.10 2008-03-14 21:15:20 christianfoltin Exp $*/

package freemind.main;

import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.view.mindmapview.MapView;

public interface FreeMindMain {
	public JFrame getJFrame();
	
    public boolean isApplet();

    public MapView getView();

    public void setView(MapView view);

    public Controller getController();

    public void setWaitingCursor(boolean waiting);

    public File getPatternsFile();

    public MenuBar getFreeMindMenuBar();

    /**Returns the ResourceBundle with the current language*/
    public ResourceBundle getResources();

    public String getResourceString(String key) ;

    public String getResourceString(String key, String defaultResource);
    
    public Container getContentPane();
    
    public void out (String msg);

    public void err (String msg);

    /**
     * Open url in WWW browser. This method hides some differences between operating systems.
     */
    public void openDocument(URL location) throws Exception;

    /**remove this!*/
    public void repaint();

    public URL getResource(String name);

	public int getIntProperty(String key, int defaultValue);
	/** @return returns the list of all properties. */
	public Properties getProperties();

    /**
     * Properties are stored in freemind.properties (internally) and ~/.freemind/auto.properties
     * for user changed values. 
     * This method returns the user value (if changed) or the original.
     * 
     * @param key The property key as specified in freemind.properties
     * @return the value of the property or null, if not found.
     */
    public String getProperty(String key);

    public void setProperty(String key, String value);

    public void saveProperties();

    /** Returns the path to the directory the freemind auto properties are in, or null, if not present.*/
    public String getFreemindDirectory();

    public JLayeredPane getLayeredPane();

    public Container getViewport();

    public void setTitle(String title);

     // to keep last win size (PN)
    public int getWinHeight();
    public int getWinWidth();
    public int getWinState();
	public int getWinX();
	public int getWinY();

    /** version info:*/
    public String getFreemindVersion();

    /** To obtain a logging element, ask here. */
    public java.util.logging.Logger getLogger(String forClass);


	/**
	 * Inserts a (south) component into the split pane. If the
	 * screen isn't split yet, a split pane should be created on the fly.
	 * 
	 * @param pMindMapComponent south panel to be inserted
	 * @return the split pane in order to move the dividers.
	 */
	public JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent);
	/**
	 *  Indicates that the south panel should be made invisible.
	 */
	public void 	  removeSplitPane();
	
	/**
	 * @return a ClassLoader derived from the standard, with 
	 * freeminds base dir included.
	 */
	public ClassLoader getFreeMindClassLoader();
	
	/**
	 * @return default ".", but on different os this differs.
	 */
	String getFreemindBaseDir();

    public String getAdjustableProperty(String label);

    public void setDefaultProperty(String key, String value);
}
