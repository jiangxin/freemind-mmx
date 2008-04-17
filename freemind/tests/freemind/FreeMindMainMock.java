/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
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
/*$Id: FreeMindMainMock.java,v 1.1.2.13 2008-04-17 19:32:30 christianfoltin Exp $*/

package tests.freemind;

import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import freemind.controller.Controller;
import freemind.controller.MenuBar;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.FreeMindMain.VersionInformation;
import freemind.view.mindmapview.MapView;

/** */
public class FreeMindMainMock implements FreeMindMain {

    /**
     * 
     */
    public FreeMindMainMock() {
        super();
        Resources.createInstance(this);

    }

    public JFrame getJFrame() {
        return null;
    }

    public boolean isApplet() {
        return false;
    }

    public MapView getView() {
        return null;
    }

    public void setView(MapView view) {
    }

    public Controller getController() {
        return null;
    }

    public void setWaitingCursor(boolean waiting) {
    }

    public File getPatternsFile() {
        return null;
    }

    public MenuBar getFreeMindMenuBar() {
        return null;
    }

    public ResourceBundle getResources() {
        return null;
    }

    public String getResourceString(String key) {
        return key;
    }

    public String getResourceString(String key, String resource) {
        return null;
    }
    
    public Container getContentPane() {
        return null;
    }

    public void out(String msg) {
    }

    public void err(String msg) {
    }

    public void openDocument(URL location) throws Exception {
    }

    public void repaint() {
    }

    public URL getResource(String name) {
        return ClassLoader.getSystemResource(name);
    }

    public int getIntProperty(String key, int defaultValue) {
        return 0;
    }

    public Properties getProperties() {
        return null;
    }

    public String getProperty(String key) {
        HashMap predefinedProperties = new HashMap();
        predefinedProperties.put("html_export_folding", "html_export_fold_currently_folded");
        return (String) predefinedProperties.get(key);
    }

    public void setProperty(String key, String value) {
    }

    public void saveProperties() {
    }

    public String getFreemindDirectory() {
        return null;
    }

    public JLayeredPane getLayeredPane() {
        return null;
    }

    public Container getViewport() {
        return null;
    }

    public void setTitle(String title) {
    }

    public int getWinHeight() {
        return 0;
    }

    public int getWinWidth() {
        return 0;
    }

    public int getWinState() {
        return 0;
    }

	public int getWinX() {
		return 0;
	}

	public int getWinY() {
		return 0;
	}

	public VersionInformation getFreemindVersion() {
        return new VersionInformation(1,0,0,FreeMindMain.VERSION_TYPE_ALPHA,42);
    }

    public Logger getLogger(String forClass) {
        return java.util.logging.Logger.getLogger(forClass);
    }


	public ClassLoader getFreeMindClassLoader() {
		return this.getClass().getClassLoader();
	}

	public String getFreemindBaseDir() {
		return ".";
	}

	public String getAdjustableProperty(String pLabel) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultProperty(String pKey, String pValue) {
		// TODO Auto-generated method stub
		
	}

	public JSplitPane insertComponentIntoSplitPane(JComponent pMindMapComponent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSplitPane() {
		// TODO Auto-generated method stub
		
	}

}

